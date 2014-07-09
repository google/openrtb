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

package com.google.openrtb.snippet;

import com.google.openrtb.OpenRtb.BidRequestOrBuilder;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.BidOrBuilder;
import com.google.openrtb.OpenRtb.BidResponseOrBuilder;

/**
 * Context for {@link SnippetProcessor}.
 */
public class SnippetProcessorContext {
  private final BidRequestOrBuilder request;
  private final BidResponseOrBuilder response;
  private final BidOrBuilder bid;

  public SnippetProcessorContext(
      BidRequestOrBuilder request, BidResponseOrBuilder response, BidOrBuilder bid) {
    this.request = request;
    this.response = response;
    this.bid = bid;
  }

  public BidRequestOrBuilder request() {
    return request;
  }

  public BidResponseOrBuilder response() {
    return response;
  }

  public BidOrBuilder bid() {
    return bid;
  }
}
