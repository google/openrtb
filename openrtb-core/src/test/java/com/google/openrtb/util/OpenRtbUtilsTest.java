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
import com.google.common.collect.Iterables;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.ContentCategory;

import org.junit.Test;

import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * Tests for {@link OpenRtbUtils}.
 */
public class OpenRtbUtilsTest {

  @Test
  public void testCatUtils() {
    assertThat(OpenRtbUtils.categoryFromName("IAB10_1")).isEqualTo(ContentCategory.IAB10_1);
    assertThat(OpenRtbUtils.categoryFromName("IAB10-1")).isEqualTo(ContentCategory.IAB10_1);
    assertThat(OpenRtbUtils.categoryToJsonName("IAB10-1")).isEqualTo("IAB10-1");
    assertThat(OpenRtbUtils.categoryToJsonName("IAB10_1")).isEqualTo("IAB10-1");
    assertThat(OpenRtbUtils.categoryToJsonName(ContentCategory.IAB10_1)).isEqualTo("IAB10-1");
  }

  @Test
  public void testRequest_imps() {
    BidRequest request = BidRequest.newBuilder().setId("1").build();
    assertThat(OpenRtbUtils.impsWith(request, Predicates.<Imp>alwaysTrue())).isEmpty();
    request = request.toBuilder().addImp(Imp.newBuilder().setId("1")).build();
    assertThat(OpenRtbUtils.impsWith(request, Predicates.<Imp>alwaysTrue())).hasSize(1);
    assertThat(OpenRtbUtils.impsWith(request, new Predicate<Imp>() {
      @Override public boolean apply(Imp imp) {
        return "notfound".equals(imp.getId());
      }
    })).isEmpty();
    assertThat(OpenRtbUtils.impsWith(request, new Predicate<Imp>() {
      @Override public boolean apply(Imp imp) {
        return "1".equals(imp.getId());
      }
    })).hasSize(1);
    assertThat(OpenRtbUtils.impWithId(request, "1")).isNotNull();
  }

  @Test
  public void testRequest_imps_oftype() {
    BidRequest request = BidRequest.newBuilder()
        .setId("1")
        .addImp(Imp.newBuilder().setId("1").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Imp.newBuilder().setId("2").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Imp.newBuilder().setId("3").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Imp.newBuilder().setId("4").setBanner(Banner.newBuilder().setId("0")))
        .addImp(Imp.newBuilder().setId("5").setVideo(Video.newBuilder()))
        .addImp(Imp.newBuilder().setId("6").setVideo(Video.newBuilder()))
        .addImp(Imp.newBuilder().setId("7").setNative(Native.newBuilder()))
        .build();

    // Banner

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), true, false, false))).hasSize(4);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysFalse(), true, false, false))).isEmpty();
    // Filter-all case
    assertThat(OpenRtbUtils.impsWith(
        request, OpenRtbUtils.addFilters(new Predicate<Imp>() {
          @Override public boolean apply(Imp imp) {
            return "0".equals(imp.getBanner().getId());
          }
        }, true, false, false))).hasSize(4);
    // Filter-none case
    assertThat(OpenRtbUtils.impsWith(
        request, OpenRtbUtils.addFilters(new Predicate<Imp>() {
          @Override public boolean apply(Imp imp) {
            return "1".equals(imp.getBanner().getId());
          }
        }, true, false, false))).isEmpty();
    // Filter-1 case
    assertThat(OpenRtbUtils.impsWith(
        request, OpenRtbUtils.addFilters(new Predicate<Imp>() {
          @Override public boolean apply(Imp imp) {
            return "1".equals(imp.getId());
          }
        }, true, false, false))).hasSize(1);
    // Filter-N case
    assertThat(OpenRtbUtils.impsWith(
        request, OpenRtbUtils.addFilters(new Predicate<Imp>() {
          @Override public boolean apply(Imp imp) {
            return imp.getId().compareTo("1") > 0;
          }
        }, true, false, false))).hasSize(3);
    assertThat(OpenRtbUtils.bannerImpWithId(request, "notfound", "2")).isNull();
    assertThat(OpenRtbUtils.bannerImpWithId(request, "1", "notfound")).isNull();
    assertThat(OpenRtbUtils.bannerImpWithId(request, "1", "0")).isNotNull();

    // Video

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), false, true, false))).hasSize(2);

    // Native

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), false, false, true))).hasSize(1);

    // Mixed

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), true, true, false))).hasSize(6);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), true, false, true))).hasSize(5);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), false, true, true))).hasSize(3);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        Predicates.<Imp>alwaysTrue(), true, true, true))).hasSize(7);
  }

  @Test
  public void testResponse_bids() {
    BidResponse.Builder response = BidResponse.newBuilder().setCur("USD");
    OpenRtbUtils.seatBid(response, "unused");
    OpenRtbUtils.seatBid(response); // no seat
    SeatBid.Builder seatbidAnon = OpenRtbUtils.seatBid(response);
    assertThat(OpenRtbUtils.seatBid(response)).isSameAs(seatbidAnon);
    SeatBid.Builder seatbidX = OpenRtbUtils.seatBid(response, "x");
    assertThat(OpenRtbUtils.seatBid(response, "x")).isSameAs(seatbidX);
    assertThat(OpenRtbUtils.seatBid(response)).isNotSameAs(seatbidX);
    assertThat(OpenRtbUtils.bids(response)).isEmpty();
    Bid bid1 = buildHtmlBid("1", 100).build();
    OpenRtbUtils.seatBid(response).addBid(bid1);
    Bid bid11 = buildHtmlBid("11", 100).build();
    OpenRtbUtils.seatBid(response).addBid(bid11);
    assertThat(OpenRtbUtils.bids(response)).hasSize(2);
    assertThat(OpenRtbUtils.bids(response, "none")).isEmpty();
    assertThat(OpenRtbUtils.bids(response, null)).hasSize(2);
    assertThat(OpenRtbUtils.bidWithId(response, "1").build()).isEqualTo(bid1);
    Bid bid2 = buildHtmlBid("2", 100).build();
    Bid bidUnused = buildHtmlBid("unused", 100).build();
    OpenRtbUtils.seatBid(response, "x").addBid(bidUnused);
    OpenRtbUtils.seatBid(response, "x").addBid(bid2);
    Bid bid22 = buildHtmlBid("22", 100).build();
    OpenRtbUtils.seatBid(response, "x").addBid(bid22);
    assertThat(OpenRtbUtils.bidWithId(response, "x", "2").build()).isEqualTo(bid2);
    assertThat(OpenRtbUtils.bidWithId(response, "x", "1")).isNull();
    assertThat(OpenRtbUtils.bidWithId(response, "none")).isNull();
    assertThat(OpenRtbUtils.bidWithId(response, "none", "1")).isNull();
    assertThat(OpenRtbUtils.bidWithId(response, null, "1")).isNotNull();
    assertThat(BuilderToBid.toBids(OpenRtbUtils.bids(response)))
        .containsExactly(bid1, bid11, bidUnused, bid2, bid22);
    assertThat(BuilderToBid.toBids(OpenRtbUtils.bids(response, null)))
        .containsExactly(bid1, bid11);
    assertThat(BuilderToBid.toBids(OpenRtbUtils.bids(response, "x")))
        .containsExactly(bidUnused, bid2, bid22);
    Predicate<Bid.Builder> filterGoodBids = new Predicate<Bid.Builder>(){
      @Override public boolean apply(Bid.Builder bid) {
        return !"unused".equals(bid.getId());
      }};
    assertThat(OpenRtbUtils.bidsWith(response, filterGoodBids)).hasSize(4);
    assertThat(OpenRtbUtils.bidsWith(response, "none", filterGoodBids)).isEmpty();
    assertThat(OpenRtbUtils.bidsWith(response, "x", filterGoodBids)).hasSize(2);
    assertThat(OpenRtbUtils.bidsWith(response, null, filterGoodBids)).hasSize(2);
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
    assertThat(OpenRtbUtils.bids(response)).hasSize(3);
    assertThat(OpenRtbUtils.filterBids(response, new Predicate<Bid.Builder>() {
      @Override public boolean apply(Bid.Builder bid) {
        return !"1".equals(bid.getId());
      }})).isTrue();
    assertThat(OpenRtbUtils.bids(response)).hasSize(2);
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("unused", 100));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("4", 100));
    assertThat(OpenRtbUtils.filterBids(response, "x", new Predicate<Bid.Builder>() {
      @Override public boolean apply(Bid.Builder bid) {
        return !"4".equals(bid.getId());
      }})).isTrue();
    assertThat(OpenRtbUtils.bids(response, "x")).hasSize(1);
    assertThat(OpenRtbUtils.filterBids(response, "none", Predicates.<Bid.Builder>alwaysFalse()))
        .isFalse();
    assertThat(OpenRtbUtils.filterBids(response, null, Predicates.<Bid.Builder>alwaysFalse()))
        .isTrue();
    assertThat(OpenRtbUtils.filterBids(response, "x", Predicates.<Bid.Builder>alwaysFalse()))
        .isTrue();
    assertThat(OpenRtbUtils.bids(response, "x")).isEmpty();
    assertThat(OpenRtbUtils.filterBids(response, Predicates.<Bid.Builder>alwaysFalse())).isFalse();
    assertThat(OpenRtbUtils.bids(response)).isEmpty();
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
    assertThat(OpenRtbUtils.updateBids(response, inflation)).isTrue();
    assertThat(OpenRtbUtils.updateBids(response, noUpdates)).isFalse();
    assertThat(OpenRtbUtils.updateBids(response, noUpdates)).isFalse();
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("1", 100));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("2", 200));
    assertThat(OpenRtbUtils.updateBids(response, "x", inflation)).isTrue();
    assertThat(OpenRtbUtils.updateBids(response, "x", noUpdates)).isFalse();
    assertThat(OpenRtbUtils.updateBids(response, "none", noUpdates)).isFalse();
    assertThat(OpenRtbUtils.updateBids(response, null, noUpdates)).isFalse();
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