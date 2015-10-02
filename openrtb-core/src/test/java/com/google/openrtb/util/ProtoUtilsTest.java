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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
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
    assertThat(ProtoUtils.filter(reqExt, true, Predicates.<FieldDescriptor>alwaysTrue()))
        .isSameAs(reqExt);
    assertThat(ProtoUtils.filter(reqExt, true, Predicates.<FieldDescriptor>alwaysFalse())).isNull();
    assertThat(ProtoUtils.filter(reqExt, false, Predicates.<FieldDescriptor>alwaysFalse()))
        .isSameAs(BidRequest.getDefaultInstance());

    BidRequest reqPlainClear = BidRequest.newBuilder()
        .setId("0")
        .addImp(Imp.newBuilder().setId("1"))
        .build();
    assertThat(ProtoUtils.filter(reqExt, true, ProtoUtils.NOT_EXTENSION)).isEqualTo(reqPlainClear);

    BidRequest reqPlainNoClear = BidRequest.newBuilder()
        .setId("0")
        .addImp(Imp.newBuilder().setId("1")
            .setBanner(Banner.newBuilder()))
        .build();
    assertThat(ProtoUtils.filter(reqExt, false, ProtoUtils.NOT_EXTENSION))
        .isEqualTo(reqPlainNoClear);

    BidRequest reqExtNoImp = reqExt.toBuilder().clearImp().build();
    assertThat(ProtoUtils.filter(reqExt, false, new Predicate<FieldDescriptor>() {
      @Override public boolean apply(FieldDescriptor fd) {
        return !"imp".equals(fd.getName());
      }})).isEqualTo(reqExtNoImp);

    BidRequest reqDiff = reqPlainClear.toBuilder().setId("1").build();
    ImmutableList<BidRequest> list = ImmutableList.of(reqPlainClear, reqDiff);
    assertThat(ProtoUtils.filter(list, new Predicate<BidRequest>() {
        @Override public boolean apply(BidRequest req) {
          return "0".equals(req.getId());
        }})).containsExactly(reqPlainClear);
    assertThat(ProtoUtils.filter(list, new Predicate<BidRequest>() {
        @Override public boolean apply(BidRequest req) {
          return "0".equals(req.getId());
        }})).containsExactly(reqPlainClear);
    assertThat(ProtoUtils.filter(list, Predicates.<BidRequest>alwaysTrue())).isSameAs(list);
    assertThat(ProtoUtils.filter(list, Predicates.<BidRequest>alwaysFalse())).isEmpty();
  }

  @Test
  public void testUpdate() {
    BidRequest.Builder req = BidRequest.newBuilder().setId("0");
    ProtoUtils.update(ImmutableList.of(req), new Function<BidRequest.Builder, Boolean>() {
      @Override public Boolean apply(BidRequest.Builder req) {
        req.setAt(AuctionType.FIRST_PRICE);
        return true;
      }});
    assertThat(req.getAt()).isEqualTo(AuctionType.FIRST_PRICE);
  }

  @Test
  public void testBuilderConversions() {
    BidRequest.Builder reqBuilder = BidRequest.newBuilder().setId("0");
    BidRequest req = reqBuilder.build();
    assertThat(ProtoUtils.built(reqBuilder)).isEqualTo(req);
    assertThat(ProtoUtils.built(req)).isSameAs(req);
    assertThat(ProtoUtils.built(null)).isNull();
    assertThat(ProtoUtils.builder(req).build()).isEqualTo(reqBuilder.build());
    assertThat(ProtoUtils.builder(reqBuilder)).isSameAs(reqBuilder);
    assertThat(ProtoUtils.builder(null)).isNull();
  }
}
