/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.openrtb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.AuctionType;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.Test.Test1;
import com.google.openrtb.Test.Test2;
import com.google.openrtb.TestExt;
import com.google.protobuf.Descriptors.FieldDescriptor;

import org.junit.Test;

/**
 * Tests for {@link ProtoUtils}.
 */
public class ProtoUtilsTest {

  @Test
  public void testFilter() {
    Test1 test1 = Test1.newBuilder().setTest1("test1").build();
    Test2 test2 = Test2.newBuilder().setTest2("test2").build();
    BidRequest reqPlainClear = BidRequest.newBuilder()
        .setId("0")
        .addImp(Imp.newBuilder().setId("1"))
        .build();
    BidRequest reqPlainNoClear = BidRequest.newBuilder()
        .setId("0")
        .addImp(Imp.newBuilder().setId("1")
            .setBanner(Banner.newBuilder()))
        .build();
    BidRequest reqExt = BidRequest.newBuilder()
        .setId("0")
        .addImp(Imp.newBuilder()
            .setId("1")
            .setBanner(Banner.newBuilder()
                .setExtension(TestExt.testBanner, test1))
            .setExtension(TestExt.testImp, test1))
        .setExtension(TestExt.testRequest1, test1)
        .setExtension(TestExt.testRequest2, test2)
        .build();
    BidRequest reqExtNoImp = reqExt.toBuilder().clearImp().build();
    BidRequest reqDiff = reqPlainClear.toBuilder().setId("1").build();
    assertSame(reqExt, ProtoUtils.filter(reqExt, true, Predicates.<FieldDescriptor>alwaysTrue()));
    assertNull(ProtoUtils.filter(reqExt, true, Predicates.<FieldDescriptor>alwaysFalse()));
    assertSame(BidRequest.getDefaultInstance(), ProtoUtils.filter(
        reqExt, false, Predicates.<FieldDescriptor>alwaysFalse()));
    assertEquals(reqPlainClear, ProtoUtils.filter(reqExt, true, ProtoUtils.NOT_EXTENSION));
    assertEquals(reqPlainNoClear, ProtoUtils.filter(reqExt, false, ProtoUtils.NOT_EXTENSION));
    assertEquals(reqExtNoImp, ProtoUtils.filter(reqExt, false, new Predicate<FieldDescriptor>() {
      @Override public boolean apply(FieldDescriptor fd) {
        return !"imp".equals(fd.getName());
      }}));

    ImmutableList<BidRequest> list = ImmutableList.of(reqPlainClear, reqDiff);
    assertEquals(
        ImmutableList.of(reqPlainClear),
        ProtoUtils.filter(list, new Predicate<BidRequest>() {
          @Override public boolean apply(BidRequest req) {
            return "0".equals(req.getId());
          }}));
    assertEquals(
        ImmutableList.of(reqPlainClear),
        ProtoUtils.filter(list, new Predicate<BidRequest>() {
          @Override public boolean apply(BidRequest req) {
            return "0".equals(req.getId());
          }}));
    assertSame(list, ProtoUtils.filter(list, Predicates.<BidRequest>alwaysTrue()));
    assertTrue(Iterables.isEmpty(ProtoUtils.filter(list, Predicates.<BidRequest>alwaysFalse())));
  }

  @Test
  public void testUpdate() {
    BidRequest.Builder req = BidRequest.newBuilder().setId("0");
    ProtoUtils.update(ImmutableList.of(req), new Function<BidRequest.Builder, Boolean>() {
      @Override public Boolean apply(BidRequest.Builder req) {
        req.setAt(AuctionType.FIRST_PRICE);
        return true;
      }});
    assertEquals(AuctionType.FIRST_PRICE, req.getAt());
  }

  @Test
  public void testBuilderConversions() {
    BidRequest.Builder reqBuilder = BidRequest.newBuilder().setId("0");
    BidRequest req = reqBuilder.build();
    assertEquals(req, ProtoUtils.built(reqBuilder));
    assertSame(req, ProtoUtils.built(req));
    assertNull(ProtoUtils.built(null));
    assertEquals(reqBuilder.build(), ProtoUtils.builder(req).build());
    assertSame(reqBuilder, ProtoUtils.builder(reqBuilder));
    assertNull(ProtoUtils.builder(null));
  }
}
