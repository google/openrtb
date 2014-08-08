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

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidResponse;

import javax.annotation.Nullable;

/**
 * Converts between OpenRTB and exchange-specific requests/response.
 *
 * @param <Req> Type for the exchange-specific bid request model
 * @param <Resp> Type for the exchange-specific bid response model
 */
public interface OpenRtbMapper<Req, Resp> {

  /**
   * Converts an OpenRTB response to native.
   *
   * @param request OpenRTB request, if necessary for context or validations
   * @param response OpenRTB response
   * @return Native response
   */
  Resp toNative(@Nullable BidRequest request, BidResponse response);

  /**
   * Converts a native request to OpenRTB.
   *
   * @param request Native request
   * @return OpenRTB request
   */
  OpenRtb.BidRequest toOpenRtb(Req request);

  /**
   * Converts an OpenRTB request to native.
   *
   * @param request OpenRTB request
   * @return Native request
   */
  Req toNative(@Nullable BidRequest request);

  /**
   * Converts a native response to OpenRTB.
   *
   * @param request native request, if necessary for context or validations
   * @param response The response
   * @return OpenRTB response
   */
  OpenRtb.BidResponse toOpenRtb(@Nullable Req request, Req response);
}
