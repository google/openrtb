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

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBidOrBuilder;
import com.google.openrtb.OpenRtb.ContentCategory;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

/**
 * Utilities to manipulate {@link BidRequest} and
 * {@link com.google.openrtb.OpenRtb.BidResponse.Builder}.
 */
public final class OpenRtbUtils {
  /**
   * Special value for the {@code seat} parameter of some methods. Notice that you can't
   * pass any string with the same value, you need to pass a reference to this unique object.
   */
  public static final String SEAT_ANY = "*";
  /**
   * Special value for for {@code impFilter} parameter of some methods, will be more efficient
   * than an equivalent {@code imp -> false} predicate.
   */
  public static final Predicate<Imp> IMP_NONE = imp -> false;
  /**
   * Special value for for {@code impFilter} parameter of some methods, will be more efficient
   * than an equivalent {@code imp -> true} predicate.
   */
  public static final Predicate<Imp> IMP_ALL = imp -> true;

  private static final ImmutableMap<Object, String> CAT_TO_JSON;
  private static final ImmutableMap<String, ContentCategory> NAME_TO_CAT;

  static {
    ImmutableMap.Builder<Object, String> catToJson = ImmutableMap.builder();
    ImmutableMap.Builder<String, ContentCategory> nameToCat = ImmutableMap.builder();
    for (ContentCategory cat : ContentCategory.values()) {
      String json = cat.name().replace('_', '-');
      catToJson.put(cat.name(), json);
      catToJson.put(cat, json);
      nameToCat.put(cat.name(), cat);
      if (!json.equals(cat.name())) {
        catToJson.put(json, json);
        nameToCat.put(json, cat);
      }
    }
    CAT_TO_JSON = catToJson.build();
    NAME_TO_CAT = nameToCat.build();
  }

  /**
   * Get a {@link ContentCategory} from its name (either Java or JSON name).
   */
  @Nullable public static ContentCategory categoryFromName(@Nullable String catName) {
    return NAME_TO_CAT.get(catName);
  }

  /**
   * Get a {@link ContentCategory}'s JSON name, from its Java name.
   */
  @Nullable public static String categoryToJsonName(@Nullable String catName) {
    return CAT_TO_JSON.get(catName);
  }

  /**
   * Get a {@link ContentCategory}'s JSON name.
   */
  @Nullable public static String categoryToJsonName(@Nullable ContentCategory cat) {
    return CAT_TO_JSON.get(cat);
  }

  /**
   * @return The OpenRTB SeatBid with the specified ID; will be created if not existent.
   *     The ID should be present in the request's wseat.
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
   *     Will be created if not existent.
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
   *     May have bids from multiple seats, grouped by seat
   */
  public static Iterable<Bid.Builder> bids(BidResponse.Builder response) {
    return new ResponseBidsIterator(response, SEAT_ANY, null);
  }

  /**
   * Iterates all bids from a specific seat.
   *
   * @param seatFilter Filter for seatFilter. You can use {@code null} to select the anonymous seat,
   *     or {@link #SEAT_ANY} to not filter by seat
   * @return View for the seat's internal sequence of bids; or an empty, read-only
   *     view if that seat doesn't exist.
   */
  public static List<Bid.Builder> bids(BidResponse.Builder response, @Nullable String seatFilter) {
    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (filterSeat(seatbid, seatFilter)) {
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
  @Nullable public static Bid.Builder bidWithId(BidResponse.Builder response, String id) {
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
   * @param seatFilter Filter for seatFilter. You can use {@code null} to select the anonymous seat,
   *     or {@link #SEAT_ANY} to not filter by seat
   * @param id Bid ID, assumed to be unique within the filtered seats
   * @return Matching bid's builder, or {@code null} if not found
   */
  @Nullable public static Bid.Builder bidWithId(
      BidResponse.Builder response, @Nullable String seatFilter, String id) {
    checkNotNull(id);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (filterSeat(seatbid, seatFilter)) {
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
   * @param bidFilter Filter for bids
   * @param seatFilter Filter for seat. You can use {@code null} to select the anonymous seat,
   * or {@link #SEAT_ANY} to not filter by seat
   * @return Read-only sequence of bids that satisfy the filter.
   *     May have bids from multiple seats, grouped by seat
   */
  public static Stream<Bid.Builder> bidStreamWith(
      BidResponse.Builder response, @Nullable String seatFilter,
      @Nullable Predicate<Bid.Builder> bidFilter) {
    return StreamSupport.stream(
        new ResponseBidsIterator(response, seatFilter, bidFilter).spliterator(), false);
  }

  /**
   * Finds bids by a custom criteria.
   *
   * @param bidFilter Filter for bids
   * @param seatFilter Filter for seat. You can use {@code null} to select the anonymous seat,
   * or {@link #SEAT_ANY} to not filter by seat
   * @return Sequence of all bids that satisfy the filter.
   *     May have bids from multiple seats, grouped by seat
   */
  public static Iterable<Bid.Builder> bidsWith(
      BidResponse.Builder response, @Nullable String seatFilter,
      @Nullable Predicate<Bid.Builder> bidFilter) {
    return new ResponseBidsIterator(response, seatFilter, bidFilter);
  }

  /**
   * Updates bids, from all seats.
   *
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   *     object, and it's expected to return {@code true} for objects that were updated
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
   * @param seatFilter Seat ID, or {@code null} to select the anonymous seat
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   *     object, and it's expected to return {@code true} for objects that were updated
   * @return {@code true} if at least one bid was updated
   * @see ProtoUtils#update(Iterable, Function) for more general updating support
   */
  public static boolean updateBids(
      BidResponse.Builder response, @Nullable String seatFilter,
      Function<Bid.Builder, Boolean> updater) {
    checkNotNull(updater);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (filterSeat(seatbid, seatFilter)) {
        return ProtoUtils.update(seatbid.getBidBuilderList(), updater);
      }
    }
    return false;
  }

  /**
   * Remove bids by bid.
   *
   * @param filter Returns {@code true} to keep bid, {@code false} to remove
   * @return {@code true} if any bid was removed
   * @see ProtoUtils#filter(List, Predicate) for more general filtering support
   */
  public static boolean removeBids(BidResponse.Builder response, Predicate<Bid.Builder> filter) {
    checkNotNull(filter);
    boolean updated = false;

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      updated |= removeBids(seatbid, filter);
    }

    return updated;
  }

  private static boolean removeBids(SeatBid.Builder seatbid, Predicate<Bid.Builder> filter) {
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
   * Remove bids by seat and bid.
   *
   * @param seatFilter Seat ID, or {@code null} to select the anonymous seat
   * @param bidFilter Returns {@code true} to keep bid, {@code false} to remove
   * @return {@code true} if any bid was removed
   * @see ProtoUtils#filter(List, Predicate) for more general filtering support
   */
  public static boolean removeBids(
      BidResponse.Builder response, @Nullable String seatFilter, Predicate<Bid.Builder> bidFilter) {
    checkNotNull(bidFilter);
    boolean updated = false;

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (filterSeat(seatbid, seatFilter)) {
        updated |= removeBids(seatbid, bidFilter);
      }
    }

    return updated;
  }

  /**
   * Finds an {@link Imp} by ID.
   *
   * @return The {@link Imp}s that has the given id, or {@code null} if not found.
   */
  @Nullable public static Imp impWithId(BidRequest request, String id) {
    checkNotNull(id);

    for (Imp imp : request.getImpList()) {
      if (imp.getId().equals(id)) {
        return imp;
      }
    }

    return null;
  }

  /**
   * Find an {@link Imp} by its ID and its {@link Banner}'s ID.
   *
   * @param impId Imp ID; optional if the Banner IDs are unique within the request
   * @param bannerId Banner ID
   * @return The {@link Imp} for a given impression ID x banner ID, or {@code null} if not found
   */
  @Nullable public static Imp bannerImpWithId(
      BidRequest request, @Nullable String impId, String bannerId) {
    checkNotNull(bannerId);

    for (Imp imp : request.getImpList()) {
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
   * @param impFilter Filters impressions; will be executed exactly once,
   *     and only for impressions that pass the banner/video type filters.
   *     The constants {@link #IMP_NONE} and  {@link #IMP_ALL} allow
   *     more efficient execution when you want to filter none/all impressions.
   * @return Immutable or unmodifiable view for the filtered impressions
   */
  public static Iterable<Imp> impsWith(BidRequest request, Predicate<Imp> impFilter) {
    checkNotNull(impFilter);

    List<Imp> imps = request.getImpList();
    if (imps.isEmpty() || impFilter == IMP_ALL) {
      return imps;
    } else if (impFilter == IMP_NONE) {
      return ImmutableList.of();
    }

    boolean included = impFilter.test(imps.get(0));
    int size = imps.size(), i;

    for (i = 1; i < size; ++i) {
      if (impFilter.test(imps.get(i)) != included) {
        break;
      }
    }

    if (i == size) {
      return included
          ? imps // Unmodifiable, comes from protobuf
          : ImmutableList.<Imp>of();
    }

    int headingSize = i;
    return new FluentIterable<Imp>() {
      @Override public Iterator<Imp> iterator() {
        Iterator<Imp> unfiltered = imps.iterator();
        return new AbstractIterator<Imp>() {
          private int heading = 0;
          @Override protected Imp computeNext() {
            while (unfiltered.hasNext()) {
              Imp imp = unfiltered.next();
              if ((heading++ < headingSize)
                  ? included
                  : impFilter.test(imp)) {
                return imp;
              }
            }
            return endOfData();
        }};
      }};
  }

  public static Stream<Imp> impStreamWith(BidRequest request, Predicate<Imp> impFilter) {
    return StreamSupport.stream(impsWith(request, impFilter).spliterator(), false);
  }

  /**
   * Adds "impression type" subfilters to a base filter, to further restricts impressions
   * that contain a banner, video and/or native object.
   *
   * @param baseFilter base filter for impressions
   * @param banner {@code true} to include impressions with
   * {@link com.google.openrtb.OpenRtb.BidRequest.Imp.Banner}s
   * @param video {@code true} to include impressions with
   * {@link com.google.openrtb.OpenRtb.BidRequest.Imp.Video}s
   * @param nativ {@code true} to include impressions with
   * {@link com.google.openrtb.OpenRtb.BidRequest.Imp.Native}s
   * @return A filter in the form: {@code baseFilter AND (banner OR video OR native)}
   */
  public static Predicate<Imp> addFilters(
      Predicate<Imp> baseFilter, boolean banner, boolean video, boolean nativ) {
    int orCount = (banner ? 1 : 0) + (video ? 1 : 0) + (nativ ? 1 : 0);
    if (baseFilter == IMP_NONE || orCount == 0) {
      return baseFilter;
    }

    Predicate<Imp> typeFilter = null;
    if (banner) {
      typeFilter = Imp::hasBanner;
    }
    if (video) {
      typeFilter = typeFilter == null ? Imp::hasVideo : typeFilter.or(Imp::hasVideo);
    }
    if (nativ) {
      typeFilter = typeFilter == null ? Imp::hasNative : typeFilter.or(Imp::hasNative);
    }

    return baseFilter == IMP_ALL ? typeFilter : baseFilter.and(typeFilter);
  }

  /**
   * Performs a filter by seat.
   *
   * @param seatbid Seat to filter
   * @param seatFilter Filter for seat. You can use {@code null} to select the anonymous seat,
   * or {@link #SEAT_ANY} to not filter by seat
   * @return {@code true} if the seat passes the filter
   */
  public static boolean filterSeat(SeatBidOrBuilder seatbid, @Nullable String seatFilter) {
    return seatFilter == null
        ? !seatbid.hasSeat()
        : seatFilter == SEAT_ANY || seatFilter.equals(seatbid.getSeat());
  }
}
