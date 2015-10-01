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

package com.google.openrtb.mapper;

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidResponse;

import javax.annotation.Nullable;

/**
 * Converts between OpenRTB and exchange-specific requests/response.
 *
 * <p>Implementations of this interface have to be threadsafe.
 *
 * @param <ReqIn> Type for the exchange-specific bid request model (input)
 * @param <RespIn> Type for the exchange-specific bid response model (input)
 * @param <ReqOut> Type for the exchange-specific bid request model (output)
 * @param <RespOut> Type for the exchange-specific bid response model (output)
 */
public interface OpenRtbMapper<ReqIn, RespIn, ReqOut, RespOut> {

  /**
   * Converts an OpenRTB response to the exchange-specific format.
   *
   * @param request OpenRTB request, if necessary for context or validations
   * @param response OpenRTB response
   * @return Response in the exchange-specific format
   */
  RespOut toExchangeBidResponse(@Nullable BidRequest request, BidResponse response);

  /**
   * Converts an exchange-specific request to OpenRTB.
   *
   * @param request Request in the exchange-specific format
   * @return OpenRTB request
   */
  BidRequest.Builder toOpenRtbBidRequest(ReqIn request);

  /**
   * Converts an OpenRTB request to the exchange-specific format.
   *
   * @param request OpenRTB request
   * @return Request in the exchange-specific format
   */
  ReqOut toExchangeBidRequest(@Nullable BidRequest request);

  /**
   * Converts a n exchange-specific response to OpenRTB.
   *
   * @param request Request in the exchange-specific format, if necessary for context or validations
   * @param response The response
   * @return OpenRTB response
   */
  BidResponse.Builder toOpenRtbBidResponse(@Nullable ReqIn request, RespIn response);
}
