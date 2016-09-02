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

import com.google.common.collect.ImmutableList;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.ContentCategory;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * Tests for {@link OpenRtbUtils}.
 */
public class OpenRtbUtilsTest {

  @Test
  public void testCatUtils() {
    assertThat(OpenRtbUtils.categoryFromName("IAB10_1")).isSameAs(ContentCategory.IAB10_1);
    assertThat(OpenRtbUtils.categoryFromName("IAB10-1")).isSameAs(ContentCategory.IAB10_1);
    assertThat(OpenRtbUtils.categoryToJsonName("IAB10-1")).isEqualTo("IAB10-1");
    assertThat(OpenRtbUtils.categoryToJsonName("IAB10_1")).isEqualTo("IAB10-1");
    assertThat(OpenRtbUtils.categoryToJsonName(ContentCategory.IAB10_1)).isEqualTo("IAB10-1");
  }

  @Test
  public void testRequest_imps() {
    BidRequest request = BidRequest.newBuilder().setId("1").build();
    assertThat(OpenRtbUtils.impsWith(request, imp -> true)).isEmpty();
    request = request.toBuilder().addImp(Imp.newBuilder().setId("1")).build();
    assertThat(OpenRtbUtils.impsWith(request, imp -> true)).hasSize(1);
    assertThat(OpenRtbUtils.impsWith(request, imp -> "notfound".equals(imp.getId()))).isEmpty();
    assertThat(OpenRtbUtils.impsWith(request, imp -> "1".equals(imp.getId()))).hasSize(1);
    assertThat(OpenRtbUtils.impWithId(request, "1")).isNotNull();
    assertThat(OpenRtbUtils.impStreamWith(request, imp -> "1".equals(imp.getId())).count())
        .isEqualTo(1);
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
        imp -> true, true, false, false))).hasSize(4);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        OpenRtbUtils.IMP_ALL, true, false, false))).hasSize(4);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> false, true, false, false))).isEmpty();
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        OpenRtbUtils.IMP_NONE, true, false, false))).isEmpty();
    // Filter-all case
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> "0".equals(imp.getBanner().getId()), true, false, false))).hasSize(4);
    // Filter-none case
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> "1".equals(imp.getBanner().getId()), true, false, false))).isEmpty();
    // Filter-1 case
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> "1".equals(imp.getId()), true, false, false))).hasSize(1);
    // Filter-N case
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> imp.getId().compareTo("1") > 0, true, false, false))).hasSize(3);
    assertThat(OpenRtbUtils.bannerImpWithId(request, "notfound", "2")).isNull();
    assertThat(OpenRtbUtils.bannerImpWithId(request, "1", "notfound")).isNull();
    assertThat(OpenRtbUtils.bannerImpWithId(request, "1", "0")).isNotNull();

    // Video

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> true, false, true, false))).hasSize(2);

    // Native

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> true, false, false, true))).hasSize(1);

    // Mixed

    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> true, true, true, false))).hasSize(6);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> true, true, false, true))).hasSize(5);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> true, false, true, true))).hasSize(3);
    assertThat(OpenRtbUtils.impsWith(request, OpenRtbUtils.addFilters(
        imp -> true, true, true, true))).hasSize(7);
  }

  @Test
  public void testAddFilter_specialCases() {
    Predicate<Imp> pred = imp -> true;
    assertThat(OpenRtbUtils.addFilters(pred, false, false, false)).isSameAs(pred);
    assertThat(OpenRtbUtils.addFilters(OpenRtbUtils.IMP_NONE, true, true, true))
        .isSameAs(OpenRtbUtils.IMP_NONE);
    assertThat(OpenRtbUtils.addFilters(OpenRtbUtils.IMP_ALL, true, true, true))
        .isNotSameAs(OpenRtbUtils.IMP_ALL);  // Mostly for coverage
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
    Function<Bid.Builder, Bid> build = bid -> bid.buildPartial();
    assertThat(ImmutableList.copyOf(OpenRtbUtils.bidsWith(response, OpenRtbUtils.SEAT_ANY, null))
        .stream().map(build).collect(Collectors.toList()))
        .containsExactly(bid1, bid11, bidUnused, bid2, bid22);
    assertThat(OpenRtbUtils.bids(response, null).stream().map(build).collect(Collectors.toList()))
        .containsExactly(bid1, bid11);
    assertThat(OpenRtbUtils.bids(response, "x").stream().map(build).collect(Collectors.toList()))
        .containsExactly(bidUnused, bid2, bid22);
    Predicate<Bid.Builder> filterGoodBids = bid -> !"unused".equals(bid.getId());
    assertThat(OpenRtbUtils.bidsWith(response, OpenRtbUtils.SEAT_ANY, filterGoodBids)).hasSize(4);
    assertThat(OpenRtbUtils.bidStreamWith(response, OpenRtbUtils.SEAT_ANY, filterGoodBids).count())
        .isEqualTo(4);
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
  public void testResponse_remove() {
    BidResponse.Builder response = BidResponse.newBuilder()
        .addSeatbid(SeatBid.newBuilder()
            .addBid(buildHtmlBid("1", 100))
            .addBid(buildHtmlBid("2", 100))
            .addBid(buildHtmlBid("3", 200)))
        .addSeatbid(SeatBid.newBuilder().setSeat("unused"));
    OpenRtbUtils.removeBids(response, bid -> true);
    assertThat(OpenRtbUtils.bids(response)).hasSize(3);
    assertThat(OpenRtbUtils.removeBids(response, bid -> !"1".equals(bid.getId()))).isTrue();
    assertThat(OpenRtbUtils.bids(response)).hasSize(2);
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("unused", 100));
    OpenRtbUtils.seatBid(response, "x").addBid(buildHtmlBid("4", 100));
    assertThat(OpenRtbUtils.removeBids(response, "x", bid -> !"4".equals(bid.getId()))).isTrue();
    assertThat(OpenRtbUtils.bids(response, "x")).hasSize(1);
    assertThat(OpenRtbUtils.removeBids(response, "none", bid -> false))
        .isFalse();
    assertThat(OpenRtbUtils.removeBids(response, null, bid -> false))
        .isTrue();
    assertThat(OpenRtbUtils.removeBids(response, "x", bid -> false))
        .isTrue();
    assertThat(OpenRtbUtils.bids(response, "x")).isEmpty();
    assertThat(OpenRtbUtils.removeBids(response, bid -> false)).isFalse();
    assertThat(OpenRtbUtils.bids(response)).isEmpty();
  }

  @Test
  public void testResponse_updater() {
    BidResponse.Builder response = BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder()
        .addBid(buildHtmlBid("1", 100))
        .addBid(buildHtmlBid("2", 200)));
    OpenRtbUtils.seatBid(response, "unused");
    Function<Bid.Builder, Boolean> inflation = bid -> {
      if (bid.getPrice() < 150) {
        bid.setPrice(bid.getPrice() * 2);
        return true;
      } else {
        return false;
      }
    };
    Function<Bid.Builder, Boolean> noUpdates = bid -> false;
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

  private static Bid.Builder buildHtmlBid(String id, long bidMicros) {
    return Bid.newBuilder()
        .setId(id)
        .setAdid("ad" + id)
        .setImpid("imp" + id)
        .setPrice(bidMicros);
  }
}