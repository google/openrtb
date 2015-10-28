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
import com.google.openrtb.OpenRtb.BidRequest.User.Gender;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.ContentCategory;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
  private static final ImmutableMap<String, Gender> NAME_TO_GENDER =
      ImmutableMap.<String, Gender>builder()
          .put("M", Gender.MALE)
          .put(Gender.MALE.name(), Gender.MALE)
          .put("F", Gender.FEMALE)
          .put(Gender.FEMALE.name(), Gender.FEMALE)
          .put("O", Gender.OTHER)
          .put(Gender.OTHER.name(), Gender.OTHER)
          .build();
  private static final ImmutableMap<Gender, String> GENDER_TO_JSON = ImmutableMap.of(
      Gender.MALE, "M",
      Gender.FEMALE, "F",
      Gender.OTHER, "O");

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
   * Get a {@link Gender} from its name (either Java or JSON name).
   */
  @Nullable public static Gender genderFromName(String genderName) {
    return NAME_TO_GENDER.get(genderName);
  }

  /**
   * Get a {@link Gender}'s JSON name, from its Java name.
   */
  @Nullable public static String genderToJsonName(Gender gender) {
    return GENDER_TO_JSON.get(gender);
  }

  /**
   * Get a {@link ContentCategory} from its name (either Java or JSON name).
   */
  @Nullable public static ContentCategory categoryFromName(String catName) {
    return NAME_TO_CAT.get(catName);
  }

  /**
   * Get a {@link ContentCategory}'s JSON name, from its Java name.
   */
  @Nullable public static String categoryToJsonName(String catName) {
    return CAT_TO_JSON.get(catName);
  }

  /**
   * Get a {@link ContentCategory}'s JSON name.
   */
  @Nullable public static String categoryToJsonName(ContentCategory cat) {
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
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @return View for the seat's internal sequence of bids; or an empty, read-only
   *     view if that seat doesn't exist.
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
   * @param seat Filter for seat. You can use {@code null} to select the anonymous seat,
   *     or {@link #SEAT_ANY} to not filter by seat
   * @param id Bid ID, assumed to be unique within the filtered seats
   * @return Matching bid's builder, or {@code null} if not found
   */
  @Nullable public static Bid.Builder bidWithId(
      BidResponse.Builder response, @Nullable String seat, String id) {
    checkNotNull(id);

    for (SeatBid.Builder seatbid : response.getSeatbidBuilderList()) {
      if (filterSeat(seatbid, seat)) {
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
   * @return Read-only sequence of bids that satisfy the filter.
   *     May have bids from multiple seats, grouped by seat
   * @deprecated Use {@link #bidsWith(com.google.openrtb.OpenRtb.BidResponse.Builder, String, Predicate)}
   *     with seat = {@link #SEAT_ANY}
   */
  @Deprecated
  public static Iterable<Bid.Builder> bidsWith(
      BidResponse.Builder response, Predicate<Bid.Builder> bidFilter) {
    return bidsWith(response, SEAT_ANY, bidFilter);
  }

  /**
   * Finds bids by a custom criteria.
   *
   * @param bidFilter Filter for bids
   * @param seat Filter for seat. You can use {@code null} to select the anonymous seat,
   * or {@link #SEAT_ANY} to not filter by seat
   * @return Read-only sequence of bids that satisfy the filter.
   *     May have bids from multiple seats, grouped by seat
   */
  public static Stream<Bid.Builder> bidStreamWith(
      BidResponse.Builder response, String seat, @Nullable Predicate<Bid.Builder> bidFilter) {
    return StreamSupport.stream(
        new ResponseBidsIterator(response, seat, bidFilter).spliterator(), false);
  }

  /**
   * Finds bids by a custom criteria.
   *
   * @param bidFilter Filter for bids
   * @param seat Filter for seat. You can use {@code null} to select the anonymous seat,
   * or {@link #SEAT_ANY} to not filter by seat
   * @return Sequence of all bids that satisfy the filter.
   *     May have bids from multiple seats, grouped by seat
   */
  public static Iterable<Bid.Builder> bidsWith(
      BidResponse.Builder response, @Nullable String seat,
      @Nullable Predicate<Bid.Builder> bidFilter) {
    return bidStreamWith(response, seat, bidFilter).collect(Collectors.toList());
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
   * @param seat Seat ID, or {@code null} to select the anonymous seat
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   *     object, and it's expected to return {@code true} for objects that were updated
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
   * Finds an {@link Imp} by ID.
   *
   * @return The {@link Imp}s that has the given id, or {@code null} if not found.
   */
  @Nullable public static Imp impWithId(BidRequest request, final String id) {
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
   * @deprecated Use {@link #impsWith(BidRequest, Predicate)}, with the help of
   * {@link #addFilters(Predicate, boolean, boolean, boolean)} if necessary.
   */
  @Deprecated
  public static Iterable<Imp> impsWith(
      BidRequest request, final Predicate<Imp> predicate, boolean banner, boolean video) {
    return impsWith(request, addFilters(predicate, banner, video, false));
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
  public static Iterable<Imp> impsWith(BidRequest request, final Predicate<Imp> impFilter) {
    checkNotNull(impFilter);

    final List<Imp> imps = request.getImpList();
    if (imps.isEmpty() || impFilter == IMP_ALL) {
      return imps;
    } else if (impFilter == IMP_NONE) {
      return ImmutableList.of();
    }

    final boolean included = impFilter.test(imps.get(0));
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

    final int headingSize = i;
    return new FluentIterable<Imp>() {
      @Override public Iterator<Imp> iterator() {
        final Iterator<Imp> unfiltered = imps.iterator();
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

  public static Stream<Imp> impStreamWith(BidRequest request, final Predicate<Imp> impFilter) {
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

  protected static boolean filterSeat(SeatBid.Builder seatbid, String seatFilter) {
    return seatFilter == null
        ? !seatbid.hasSeat()
        : seatFilter == SEAT_ANY || seatFilter.equals(seatbid.getSeat());
  }
}
