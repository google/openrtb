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

import com.google.common.collect.ImmutableMap;

/**
 * OpenRTB 4.6: Standard OpenRTB macros.
 *
 * <p>All {@code Bid} properties of type String can use macros.
 * Important notes about macro expansion:
 *
 * <ul><li>All properties can safely use macros that refer to values from the request:
 *         {@code AUCTION_ID, AUCTION_CURRENCY, AUCTION_IMP_ID, AUCTION_SEAT_ID}.</li>
 *     <li>All properties can also safely use macros that refer to properties that have
 *         non-String type, so they cannot contain macros: {@code AUCTION_PRICE}.
 *     <li>Properties are processed in two stages. The first stage resolves properties that can
 *         contain macros AND feed other macros; {@code adid => AUCTION_AD_ID, id => AUCTION_BID_ID}
 *         are currently the only items in this group. In the second stage, we process the
 *         properties that support macros but don't provide values for any macro, which are:
 *         {@code adm}, {@code cid}, {@code crid}, {@code dealid}, {@code iurl}, {@code nurl}.</li>
 *     <li>Notice that {code impid} is expected to be set to {@code AUCTION_IMP_ID}; you can
 *         use the macro or set the value manually but in the latter case they should match.
 *         All other properties that use the macro {@code AUCTION_IMP_ID} will resolve that
 *         to the bid's {code Imp.id}, not to the bid's own {code impid} property.</li>
 * </ul>
 */
public enum OpenRtbMacros implements SnippetMacroType {
  /**
   * ID of the ad markup the bid wishes to serve; from "adid" attribute.
   */
  AUCTION_AD_ID("${AUCTION_AD_ID}"),
  /**
   * ID of the bid; from "bidid" attribute.
   */
  AUCTION_BID_ID("${AUCTION_BID_ID}"),
  /**
   * The currency used in the bid (explicit or implied); for confirmation only.
   * WARNING: May not be supported by all exchanges.
   */
  AUCTION_CURRENCY("${AUCTION_CURRENCY}"),
  /**
   * ID of the bid request; from "id" attribute.
   */
  AUCTION_ID("${AUCTION_ID}"),
  /**
   * ID of the impression just won; from "impid" attribute.
   * WARNING: May not be supported by all exchanges.
   */
  AUCTION_IMP_ID("${AUCTION_IMP_ID}"),
  /**
   * Settlement price using the same currency and units as the bid.
   * WARNING: May not be supported by all exchanges.
   */
  AUCTION_PRICE("${AUCTION_PRICE}"),
  /**
   * ID of the bidder's seat for whom the bid was made.
   */
  AUCTION_SEAT_ID("${AUCTION_SEAT_ID}"),
  ;

  private static final ImmutableMap<String, OpenRtbMacros> LOOKUP_KEY;

  static {
    ImmutableMap.Builder<String, OpenRtbMacros> builder = ImmutableMap.builder();
    for (OpenRtbMacros snippetMacro : values()) {
      builder.put(snippetMacro.key, snippetMacro);
    }
    LOOKUP_KEY = builder.build();
  }

  private final String key;

  private OpenRtbMacros(String key) {
    this.key = key;
  }

  /**
   * Returns the key for this macro (string that will be substituted when the macro is processed).
   */
  @Override public final String key() {
    return key;
  }

  /**
   * @return {@link OpenRtbMacros} instance by key name
   */
  public static OpenRtbMacros valueOfKey(String key) {
    return LOOKUP_KEY.get(key);
  }
}
