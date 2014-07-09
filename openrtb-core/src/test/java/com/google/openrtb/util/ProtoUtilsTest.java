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
import static org.junit.Assert.assertTrue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.openrtb.OpenRtb.BidRequest;

import org.junit.Test;

/**
 * Tests for {@link ProtoUtils}.
 */
public class ProtoUtilsTest {

  @Test
  public void testProto() {
    BidRequest req1 = BidRequest.newBuilder().setId("0").buildPartial();
    BidRequest req2 = BidRequest.newBuilder().setId("1").buildPartial();
    assertEquals(req1, ProtoUtils.filter(req1, true, ProtoUtils.NOT_EXTENSION));
    assertEquals(
        ImmutableList.of(req1),
        ProtoUtils.filter(ImmutableList.of(req1, req2), new Predicate<BidRequest>() {
          @Override public boolean apply(BidRequest req) {
          return "0".equals(req.getId());
        }}));
    BidRequest.Builder reqb = req1.toBuilder();
    ProtoUtils.update(ImmutableList.of(reqb), new Function<BidRequest.Builder, Boolean>() {
      @Override public Boolean apply(BidRequest.Builder req) {
        req.setAt(1);
        return true;
      }});
    assertTrue(reqb.getAt() == 1);
  }
}
