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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Utilities to manipulate {@link BidRequest} and
 * {@link com.google.openrtb.OpenRtb.BidResponse.Builder}.
 */
public final class OpenRtbUtils {
  public static final Predicate<Impression> IMP_HAS_BANNER = new Predicate<Impression>() {
    @Override public boolean apply(Impression imp) {
      assert imp != null;
      return imp.hasBanner();
    }};
  public static final Predicate<Impression> IMP_HAS_VIDEO = new Predicate<Impression>() {
    @Override public boolean apply(Impression imp) {
      assert imp != null;
      return imp.hasVideo();
    }};

  /**
   * @return The OpenRTB SeatBid with the specified ID; will be created if not existent.
   * The ID should be present in the request's wseat.
   *
   * @see #seatBid(com.google.openrtb.OpenRtb.BidResponse.Builder)
   * Use for the anonymous seat
   */
  public static SeatBid.Builder seatBid(BidResponse.Builder response, String seat) {
    checkNotNull(seat);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (seatbid.hasSeat() && seat.equals(seatbid.getSeat())) {
        return seatbid;
      }
    }
    return response.addSeatbidBuilder().setSeat(seat);
  }

  /**
   * @return The anonymous OpenRTB SeatBid, used by non-seat-specific bids (the seat ID is not set).
   * Will be created if not existent.
   */
  public static SeatBid.Builder seatBid(BidResponse.Builder response) {
    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (!seatbid.hasSeat()) {
        return seatbid;
      }
    }
    return response.addSeatbidBuilder();
  }

  /**
   * Iterates all bids.
   *
   * @return Read-only sequence of all bis in the response.
   * May have bids from multiple seats, grouped by seat
   */
  public static Iterable<Bid.Builder> bids(BidResponse.Builder response) {
    return new ResponseBidsIterator(response);
  }

  /**
   * Iterates all bids from a specific seat.
   *
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @return View for the seat's internal sequence of bids; or an empty, read-only
   * view if that seat doesn't exist.
   */
  public static List<Bid.Builder> bids(BidResponse.Builder response, @Nullable String seat) {
    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (seatbid.hasSeat() ? seatbid.getSeat().equals(seat) : seat == null) {
        return seatbid.getBidBuilderList();
      }
    }
    return ImmutableList.of();
  }

  /**
   * Finds a bid by ID.
   *
   * @param id Bid ID, assumed to be unique within the response
   * @return Matching bid's builder, or {@code null} if not found
   */
  public static @Nullable Bid.Builder bidWithId(BidResponse.Builder response, String id) {
    checkNotNull(id);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      for (Bid.Builder bid : seatbid.getBidBuilderList()) {
        if (id.equals(bid.getId())) {
          return bid;
        }
      }
    }
    return null;
  }

  /**
   * Finds a bid by seat and ID.
   *
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @param id Bid ID, assumed to be unique within the seat
   * @return Matching bid's builder, or {@code null} if not found
   */
  public static @Nullable Bid.Builder bidWithId(
      BidResponse.Builder response, @Nullable String seat, String id) {
    checkNotNull(id);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (seatbid.hasSeat() ? seatbid.getSeat().equals(seat) : seat == null) {
        for (Bid.Builder bid : seatbid.getBidBuilderList()) {
          if (id.equals(bid.getId())) {
            return bid;
          }
        }
        return null;
      }
    }
    return null;
  }

  /**
   * Finds bids by a custom criteria.
   *
   * @param filter Selection criteria
   * @return Read-only sequence of bids that satisfy the filter.
   * May have bids from multiple seats, grouped by seat
   */
  public static Iterable<Bid.Builder> bidsWith(
      BidResponse.Builder response, Predicate<Bid.Builder> filter) {
    return Iterables.filter(new ResponseBidsIterator(response), filter);
  }

  /**
   * Finds bids by a custom criteria.
   *
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @param filter Selection criteria
   * @return Sequence of all bids that satisfy the filter.
   * May have bids from multiple seats, grouped by seat
   */
  public static Iterable<Bid.Builder> bidsWith(
      BidResponse.Builder response, @Nullable String seat, Predicate<Bid.Builder> filter) {
    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (seat == null ? !seatbid.hasSeat() : seatbid.hasSeat() && seat.equals(seatbid.getSeat())) {
        return Iterables.filter(seatbid.getBidBuilderList(), filter);
      }
    }
    return emptyList();
  }

  /**
   * Updates bids, from all seats.
   *
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   * object, and it's expected to return {@code true} for objects that were updated
   * @return {@code true} if at least one bid was updated
   * @see ProtoUtils#update(Iterable, Function) for more general updating support
   */
  public static boolean updateBids(
      BidResponse.Builder response, Function<Bid.Builder, Boolean> updater) {
    checkNotNull(updater);

    boolean updated = false;
    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      updated |= ProtoUtils.update(seatbid.getBidBuilderList(), updater);
    }
    return updated;
  }

  /**
   * Updates bids from a given seat.
   *
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   * object, and it's expected to return {@code true} for objects that were updated
   * @return {@code true} if at least one bid was updated
   * @see ProtoUtils#update(Iterable, Function) for more general updating support
   */
  public static boolean updateBids(
      BidResponse.Builder response, @Nullable String seat, Function<Bid.Builder, Boolean> updater) {
    checkNotNull(updater);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (seat == null ? !seatbid.hasSeat() : seatbid.hasSeat() && seat.equals(seatbid.getSeat())) {
        return ProtoUtils.update(seatbid.getBidBuilderList(), updater);
      }
    }
    return false;
  }

  /**
   * Filter bids from all seats.
   *
   * @param filter Returns {@code true} to keep bid, {@code false} to remove
   * @return {@code true} if any bid was removed
   * @see ProtoUtils#filter(Iterable, Predicate) for more general filtering support
   */
  public static boolean filterBids(BidResponse.Builder response, Predicate<Bid.Builder> filter) {
    checkNotNull(filter);
    boolean updated = false;

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      updated |= filterSeat(seatbid, filter);
    }

    return updated;
  }

  private static boolean filterSeat(SeatBid.Builder seatbid, Predicate<Bid.Builder> filter) {
    List<Bid.Builder> oldBids = seatbid.getBidBuilderList();
    Iterable<Bid.Builder> newBids = ProtoUtils.filter(oldBids, filter);
    if (newBids == oldBids) {
      return false;
    } else {
      seatbid.clearBid();
      for (Bid.Builder bid : newBids) {
        seatbid.addBid(bid);
      }
      return true;
    }
  }

  /**
   * Filter bids from a given seat.
   *
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @param filter Returns {@code true} to keep bid, {@code false} to remove
   * @return {@code true} if any bid was removed
   * @see ProtoUtils#filter(Iterable, Predicate) for more general filtering support
   */
  public static boolean filterBids(
      BidResponse.Builder response, @Nullable String seat, Predicate<Bid.Builder> filter) {
    checkNotNull(filter);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (seat == null ? !seatbid.hasSeat() : seatbid.hasSeat() && seat.equals(seatbid.getSeat())) {
        return OpenRtbUtils.filterSeat(seatbid, filter);
      }
    }

    return false;
  }

  /**
   * Finds an {@link Impression} by ID.
   *
   * @return The {@link Impression}s that has the given id, or {@code null} if not found.
   */
  public static @Nullable Impression impWithId(BidRequest request, final String id) {
    checkNotNull(id);

    for (Impression imp : request.getImpList()) {
      if (imp.getId().equals(id)) {
        return imp;
      }
    }

    return null;
  }

  /**
   * Find an {@link Impression} by its ID and its {@link Banner}'s ID.
   *
   * @param impId Impression ID; optional if the Banner IDs are unique within the request
   * @param bannerId Banner ID
   * @return The {@link Impression} for a given impression ID x banner ID,
   * or {@code null} if not found.
   */
  public static @Nullable Impression bannerImpWithId(
      BidRequest request, @Nullable String impId, String bannerId) {
    checkNotNull(bannerId);

    for (Impression imp : request.getImpList()) {
      if ((impId == null || imp.getId().equals(impId))
          && imp.hasBanner() && imp.getBanner().getId().equals(bannerId)) {
        return imp;
      }
    }

    return null;
  }

  /**
   * Optimized code for most filtered lookups. This is worth the effort
   * because bidder code may invoke these lookup methods intensely;
   * common cases like everything-filtered or nothing-filtered are very dominant;
   * and simpler code previously used needed lots of temporary collections.
   *
   * @param request Container of impressions
   * @param predicate Filters impressions; will be executed exactly once,
   * and only for impressions that pass the banner/video type filters
   * @return Immutable or unmodifiable view for the filtered impressions
   */
  public static Iterable<Impression> impsWith(
      BidRequest request, final Predicate<Impression> predicate, boolean banner, boolean video) {
    checkNotNull(predicate);

    final List<Impression> imps = request.getImpList();
    if (imps.isEmpty()) {
      return ImmutableList.of();
    }

    final Predicate<Impression> impTypeFilter = banner && video
        ? Predicates.<Impression>alwaysTrue()
        : banner ? IMP_HAS_BANNER : IMP_HAS_VIDEO;
    final boolean included = filter(imps.get(0), impTypeFilter, predicate);
    int size = imps.size(), i;

    for (i = 1; i < size; ++i) {
      if (filter(imps.get(i), impTypeFilter, predicate) != included) {
        break;
      }
    }

    if (i == size) {
      return included
          ? imps // Unmodifiable, comes from protobuf
          : ImmutableList.<Impression>of();
    }

    final int headingSize = i;
    return new FluentIterable<Impression>() {
      @Override public Iterator<Impression> iterator() {
        final Iterator<Impression> unfiltered = imps.iterator();
        return new AbstractIterator<Impression>() {
          private int heading = 0;
          @Override protected Impression computeNext() {
            while (unfiltered.hasNext()) {
              Impression imp = unfiltered.next();
              if ((heading++ < headingSize)
                  ? included
                  : OpenRtbUtils.filter(imp, impTypeFilter, predicate)) {
                return imp;
              }
            }
            return endOfData();
        }};
      }};
  }

  private static boolean filter(
      Impression imp, Predicate<Impression> typePredicate, Predicate<Impression> predicate) {
    return typePredicate.apply(imp) && predicate.apply(imp);
  }
}
