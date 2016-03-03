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

import com.google.common.collect.ImmutableList;
import com.google.openrtb.OpenRtb.BidRequest.ImpOrBuilder;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.BidOrBuilder;
import com.google.openrtb.OpenRtb.BidResponse.SeatBidOrBuilder;

import java.util.List;

import javax.inject.Singleton;

/**
 * Default {@link SnippetProcessor}.
 */
@Singleton
public class OpenRtbSnippetProcessor extends SnippetProcessor {
  public static final OpenRtbSnippetProcessor ORTB_NULL = new OpenRtbSnippetProcessor() {
    @Override public String process(SnippetProcessorContext ctx, String snippet) {
      return SnippetProcessor.NULL.process(ctx, snippet);
    }};

  /**
   * Creates a processor.
   */
  public OpenRtbSnippetProcessor() {
  }

  @Override protected List<SnippetMacroType> registerMacros() {
    return ImmutableList.<SnippetMacroType>copyOf(OpenRtbMacros.values());
  }

  @Override protected void processMacroAt(
      SnippetProcessorContext ctx, StringBuilder sb, SnippetMacroType macroDef) {
    if (macroDef instanceof OpenRtbMacros) {
      switch ((OpenRtbMacros) macroDef) {
        // Standard OpenRTB macros (OpenRTB 4.6) ----------------------------

        case AUCTION_AD_ID: {
          if (ctx.getBid().hasAdid()) {
            sb.append(ctx.getBid().getAdid());
          }
          break;
        }

        case AUCTION_BID_ID: {
          sb.append(ctx.response().getBidid());
          break;
        }

        case AUCTION_CURRENCY: {
          if (ctx.request().getCurCount() == 1) {
            sb.append(ctx.request().getCur(0));
          }
          break;
        }

        case AUCTION_ID: {
          sb.append(ctx.request().getId());
          break;
        }

        case AUCTION_IMP_ID: {
          sb.append(findImp(ctx, macroDef).getId());
          break;
        }

        case AUCTION_SEAT_ID: {
          SeatBidOrBuilder seatBid = findSeat(ctx, macroDef);
          if (seatBid.hasSeat()) {
            sb.append(seatBid.getSeat());
          }
          break;
        }

        // "Server-side" macros: keep the macro untouched, so it will be
        // expanded by the exchange or other system that receives the message.
        case AUCTION_PRICE: {
          sb.append(((OpenRtbMacros) macroDef).key());
        }
      }
    }
  }

  /**
   * Processes the context's response in-place, modifying properties that may contain macros.
   */
  public void process(SnippetProcessorContext bidCtx) {
    for (SeatBid.Builder seat : bidCtx.response().getSeatbidBuilderList()) {
      for (Bid.Builder bid : seat.getBidBuilderList()) {
        bidCtx.setBid(bid);
        processFields(bidCtx);
      }
    }
  }

  /**
   * Processes all fields of a bid that should support macro expansion.
   */
  protected void processFields(SnippetProcessorContext bidCtx) {
    if (bidCtx.getBid().hasAdm()) {
      bidCtx.getBid().setAdm(process(bidCtx, bidCtx.getBid().getAdm()));
    }
  }

  private SeatBidOrBuilder findSeat(SnippetProcessorContext ctx, SnippetMacroType macro) {
    for (SeatBidOrBuilder seatBid : ctx.response().getSeatbidOrBuilderList()) {
      for (BidOrBuilder lookupBid : seatBid.getBidOrBuilderList()) {
        if (lookupBid == ctx.getBid()) {
          return seatBid;
        }
      }
    }

    throw new UndefinedMacroException(
        macro, "Bid doesn't belong to this request");
  }

  protected ImpOrBuilder findImp(SnippetProcessorContext ctx, SnippetMacroType macro) {
    for (ImpOrBuilder imp : ctx.request().getImpOrBuilderList()) {
      if (imp.getId().equals(ctx.getBid().getImpid())) {
        return imp;
      }
    }

    throw new UndefinedMacroException(macro,
        "Bid's impression id: " + ctx.getBid().getImpid() + " doesn't match request");
  }
}
