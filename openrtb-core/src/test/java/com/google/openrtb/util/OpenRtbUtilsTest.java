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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Linearity;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Protocol;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;

import org.junit.Test;

import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * Tests for {@link OpenRtbUtils}.
 */
public class OpenRtbUtilsTest {

  @Test
  public void testRequest_imps() {
    BidRequest request = BidRequest.newBuilder().setId("1").build();
    assertTrue(Iterables.isEmpty(OpenRtbUtils.impsWith(
        request, Predicates.<Impression>alwaysTrue(), true, true)));
    request = request.toBuilder().addImp(Impression.newBuilder().setId("1")).build();
    assertEquals(1, Iterables.size(OpenRtbUtils.impsWith(
        request, Predicates.<Impression>alwaysTrue(), true, true)));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return "notfound".equals(imp.getId());
      }
    }, true, true)));
    assertEquals(1, Iterables.size(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return "1".equals(imp.getId());
      }
    }, true, true)));
    assertNotNull(OpenRtbUtils.impWithId(request, "1"));
  }

  @Test
  public void testRequest_banners() {
    BidRequest request = BidRequest.newBuilder()
        .setId("1")
        .addImp(Impression.newBuilder().setId("1").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Impression.newBuilder().setId("2").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Impression.newBuilder().setId("3").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Impression.newBuilder().setId("4").setBanner(Banner.newBuilder().setId("0")))
        .build();
    assertEquals(4, Iterables.size(OpenRtbUtils.impsWith(
        request, Predicates.<Impression>alwaysTrue(), true, false)));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.impsWith(
        request, Predicates.<Impression>alwaysFalse(), true, false)));
    // Filter-all case
    assertEquals(4, Iterables.size(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return "0".equals(imp.getBanner().getId());
      }
    }, true, false)));
    // Filter-none case
    assertEquals(0, Iterables.size(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return "1".equals(imp.getBanner().getId());
      }
    }, true, false)));
    // Filter-1 case
    assertEquals(1, Iterables.size(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return "1".equals(imp.getId());
      }
    }, true, false)));
    // Filter-N case
    assertEquals(3, Iterables.size(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return imp.getId().compareTo("1") > 0;
      }
    }, true, false)));
    assertNull(OpenRtbUtils.bannerImpWithId(request, "notfound", "2"));
    assertNull(OpenRtbUtils.bannerImpWithId(request, "1", "notfound"));
    assertNotNull(OpenRtbUtils.bannerImpWithId(request, "1", "0"));
  }

  @Test
  public void testRequest_videos() {
    BidRequest request = BidRequest.newBuilder()
        .setId("1")
        .addImp(Impression.newBuilder().setId("1").setVideo(Video.newBuilder()
            .setLinearity(Linearity.LINEAR)
            .setMinduration(100)
            .setMaxduration(200)
            .addProtocols(Protocol.VAST_3_0)))
        .build();
    assertEquals(1, Iterables.size(OpenRtbUtils.impsWith(
        request, Predicates.<Impression>alwaysTrue(), false, true)));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.impsWith(
        request, Predicates.<Impression>alwaysFalse(), false, true)));
    assertEquals(1, Iterables.size(OpenRtbUtils.impsWith(request, new Predicate<Impression>() {
      @Override public boolean apply(Impression imp) {
        return imp.getVideo().getLinearity() == Linearity.LINEAR;
      }
    }, false, true)));
  }

  @Test
  public void testResponse_bids() {
    BidResponse.Builder response = BidResponse.newBuilder().setCur("USD");
    OpenRtbUtils.seatBid(response, "unused");
    OpenRtbUtils.seatBid(response); // no seat
    SeatBid.Builder seatbidAnon = OpenRtbUtils.seatBid(response);
    assertSame(seatbidAnon, OpenRtbUtils.seatBid(response));
    SeatBid.Builder seatbidX = OpenRtbUtils.seatBid(response, "x");
    assertSame(seatbidX, OpenRtbUtils.seatBid(response, "x"));
    assertNotSame(seatbidX, OpenRtbUtils.seatBid(response));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
    Bid bid1 = buildHtmlBid("1", 100).build();
    OpenRtbUtils.seatBid(response).addBid(bid1);
    Bid bid11 = buildHtmlBid("11", 100).build();
    OpenRtbUtils.seatBid(response).addBid(bid11);
    assertEquals(2, Iterables.size(OpenRtbUtils.bids(response)));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response, "none")));
    assertEquals(2, Iterables.size(OpenRtbUtils.bids(response, null)));
    assertEquals(bid1, OpenRtbUtils.bidWithId(response, "1").build());
    Bid bid2 = buildHtmlBid("2", 100).build();
    Bid bidUnused = buildHtmlBid("unused", 100).build();
    OpenRtbUtils.seatBid(response, "x").addBid(bidUnused);
    OpenRtbUtils.seatBid(response, "x").addBid(bid2);
    Bid bid22 = buildHtmlBid("22", 100).build();
    OpenRtbUtils.seatBid(response, "x").addBid(bid22);
    assertEquals(bid2, OpenRtbUtils.bidWithId(response, "x", "2").build());
    assertNull(OpenRtbUtils.bidWithId(response, "x", "1"));
    assertNull(OpenRtbUtils.bidWithId(response, "none"));
    assertNull(OpenRtbUtils.bidWithId(response, "none", "1"));
    assertNotNull(OpenRtbUtils.bidWithId(response, null, "1"));
    assertTrue(Iterables.elementsEqual(
        ImmutableList.of(bid1, bid11, bidUnused, bid2, bid22),
        BuilderToBid.toBids(OpenRtbUtils.bids(response))));
    assertTrue(Iterables.elementsEqual(
        ImmutableList.of(bid1, bid11),
        BuilderToBid.toBids(OpenRtbUtils.bids(response, null))));
    assertTrue(Iterables.elementsEqual(
        ImmutableList.of(bidUnused, bid2, bid22),
        BuilderToBid.toBids(OpenRtbUtils.bids(response, "x"))));
    Predicate<Bid.Builder> filterGoodBids = new Predicate<Bid.Builder>(){
      @Override public boolean apply(Bid.Builder bid) {
        return !"unused".equals(bid.getId());
      }};
    assertEquals(4, Iterables.size(OpenRtbUtils.bidsWith(response, filterGoodBids)));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bidsWith(response, "none", filterGoodBids)));
    assertEquals(2, Iterables.size(OpenRtbUtils.bidsWith(response, "x", filterGoodBids)));
    assertEquals(2, Iterables.size(OpenRtbUtils.bidsWith(response, null, filterGoodBids)));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testResponse_unsupportedRemove() {
    BidResponse.Builder response = BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder()
        .addBid(buildHtmlBid("1", 100)));
    Iterator<Bid.Builder> bids = OpenRtbUtils.bids(response).iterator();
    bids.next();
    bids.remove();
  }

  @Test
  public void testResponse_filter() {
    BidResponse.Builder response = BidResponse.newBuilder()
        .addSeatbid(SeatBid.newBuilder()
            .addBid(buildHtmlBid("1", 100))
            .addBid(buildHtmlBid("2", 100))
            .addBid(buildHtmlBid("3", 200)))
        .addSeatbid(SeatBid.newBuilder().setSeat("unused"));
    OpenRtbUtils.filterBids(response, Predicates.<Bid.Builder>alwaysTrue());
    assertEquals(3, Iterables.size(OpenRtbUtils.bids(response)));
    assertTrue(OpenRtbUtils.filterBids(response, new Predicate<Bid.Builder>() {
      @Override public boolean apply(Bid.Builder bid) {
        return !"1".equals(bid.getId());
      }}));
    assertEquals(2, Iterables.size(OpenRtbUtils.bids(response)));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("unused", 100));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("4", 100));
    assertTrue(OpenRtbUtils.filterBids(response, "x", new Predicate<Bid.Builder>() {
      @Override public boolean apply(Bid.Builder bid) {
        return !"4".equals(bid.getId());
      }}));
    assertEquals(1, Iterables.size(OpenRtbUtils.bids(response, "x")));
    assertFalse(OpenRtbUtils.filterBids(response, "none", Predicates.<Bid.Builder>alwaysFalse()));
    assertTrue(OpenRtbUtils.filterBids(response, null, Predicates.<Bid.Builder>alwaysFalse()));
    assertTrue(OpenRtbUtils.filterBids(response, "x", Predicates.<Bid.Builder>alwaysFalse()));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response, "x")));
    assertFalse(OpenRtbUtils.filterBids(response, Predicates.<Bid.Builder>alwaysFalse()));
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testResponse_updater() {
    BidResponse.Builder response = BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder()
        .addBid(buildHtmlBid("1", 100))
        .addBid(buildHtmlBid("2", 200)));
    OpenRtbUtils.seatBid(response, "unused");
    Function<Bid.Builder, Boolean> inflation = new Function<Bid.Builder, Boolean>() {
      @Override public Boolean apply(Bid.Builder bid) {
        if (bid.getPrice() < 150) {
          bid.setPrice(bid.getPrice() * 2);
          return true;
        } else {
          return false;
        }
      }};
    Function<Bid.Builder, Boolean> noUpdates = new Function<Bid.Builder, Boolean>() {
      @Override public Boolean apply(@Nullable Bid.Builder bid) {
        return false;
      }};
    assertTrue(OpenRtbUtils.updateBids(response, inflation));
    assertFalse(OpenRtbUtils.updateBids(response, noUpdates));
    assertFalse(OpenRtbUtils.updateBids(response, noUpdates));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("1", 100));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("2", 200));
    assertTrue(OpenRtbUtils.updateBids(response, "x", inflation));
    assertFalse(OpenRtbUtils.updateBids(response, "x", noUpdates));
    assertFalse(OpenRtbUtils.updateBids(response, "none", noUpdates));
    assertFalse(OpenRtbUtils.updateBids(response, null, noUpdates));
  }

  static class BuilderToBid implements Function<Bid.Builder, Bid> {
    static final BuilderToBid INSTANCE = new BuilderToBid();
    @Override public Bid apply(Bid.Builder builder) {
      return builder.buildPartial();
    }
    static Iterable<Bid> toBids(Iterable<Bid.Builder> builders) {
      return Iterables.transform(builders, INSTANCE);
    }
  }

  private static Bid.Builder buildHtmlBid(String id, long bidMicros) {
    return Bid.newBuilder()
        .setId(id)
        .setAdid("ad" + id)
        .setImpid("imp" + id)
        .setPrice(bidMicros);
  }
}