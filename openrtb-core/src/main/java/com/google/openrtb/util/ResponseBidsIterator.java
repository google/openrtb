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

import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import javax.annotation.Nullable;

/**
 * Iterates all {@link com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid.Builder}s
 * in a {@link com.google.openrtb.OpenRtb.BidResponse.Builder},
 * dealing with the intermediate layer of seats transparently.
 */
class ResponseBidsIterator implements Iterator<Bid.Builder>, Iterable<Bid.Builder> {
  private final @Nullable String seatFilter;
  private final Predicate<Bid.Builder> bidFilter;
  private final Iterator<SeatBid.Builder> seatbidIter;
  private Iterator<Bid.Builder> bidIter = Collections.emptyIterator();
  private Bid.Builder nextBid;

  public ResponseBidsIterator(
      BidResponse.Builder bidResponse,
      @Nullable String seatFilter,
      @Nullable Predicate<Bid.Builder> bidFilter) {
    this.seatbidIter = bidResponse.getSeatbidBuilderList().iterator();
    this.seatFilter = seatFilter;
    this.bidFilter = bidFilter;
  }

  @Override public boolean hasNext() {
    scanIters();
    return nextBid != null;
  }

  @Override public Bid.Builder next() {
    scanIters();
    if (nextBid == null) {
      throw new NoSuchElementException();
    }
    Bid.Builder ret = nextBid;
    nextBid = null;
    return ret;
  }

  @Override public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override public Iterator<Bid.Builder> iterator() {
    return this;
  }

  private void scanIters() {
    while (!scanBidIter() && seatbidIter.hasNext()) {
      SeatBid.Builder seatBid = seatbidIter.next();
      if (OpenRtbUtils.filterSeat(seatBid, seatFilter)) {
        bidIter = seatBid.getBidBuilderList().iterator();
      }
    }
  }

  private boolean scanBidIter() {
    while (true) {
      if (nextBid != null) {
        return true;
      }
      if (!bidIter.hasNext()) {
        return false;
      }
      nextBid = bidIter.next();
      if (bidFilter != null && !bidFilter.test(nextBid)) {
        nextBid = null;
      }
    }
  }
}
