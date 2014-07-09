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

import com.google.common.collect.Iterators;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;

import java.util.Iterator;

/**
 * Iterates all {@link com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid.Builder}s
 * in a {@link com.google.openrtb.OpenRtb.BidResponse.Builder},
 * dealing with the intermediate layer of seats transparently.
 */
class ResponseBidsIterator implements Iterator<Bid.Builder>, Iterable<Bid.Builder> {
  private final Iterator<SeatBid.Builder> seatbidIter;
  private Iterator<Bid.Builder> bidIter = Iterators.emptyIterator();

  public ResponseBidsIterator(BidResponse.Builder bidResponse) {
    this.seatbidIter = bidResponse.getSeatbidBuilderList().iterator();
  }

  private void skipSeats() {
    while (!bidIter.hasNext() && seatbidIter.hasNext()) {
      SeatBid.Builder seatBid = seatbidIter.next();
      bidIter = seatBid.getBidBuilderList().iterator();
    }
  }

  @Override
  public boolean hasNext() {
    skipSeats();
    return bidIter.hasNext();
  }

  @Override
  public Bid.Builder next() {
    skipSeats();
    return bidIter.next();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Bid.Builder> iterator() {
    return this;
  }
}
