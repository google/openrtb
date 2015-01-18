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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.TestUtil;

import org.junit.Test;

/**
 * Tests for {@link OpenRtbSnippetProcessor}.
 */
public class OpenRtbSnippetProcessorTest {

  @Test
  public void testNullProcessor() {
    String macro = OpenRtbMacros.AUCTION_ID.key();
    BidRequest req = BidRequest.newBuilder().setId("req1").build();
    BidResponse.Builder resp = BidResponse.newBuilder();
    Bid.Builder bid = Bid.newBuilder()
        .setId("bid1")
        .setImpid(macro)
        .setPrice(10000);
    SnippetProcessorContext ctx = new SnippetProcessorContext(req, resp, bid);
    assertSame(macro, OpenRtbSnippetProcessor.ORTB_NULL.process(ctx, macro));
  }

  @Test
  public void testOpenRtbMacros() {
    TestUtil.testCommonEnum(OpenRtbMacros.values());
    assertSame(OpenRtbMacros.AUCTION_ID, OpenRtbMacros.valueOfKey("${AUCTION_ID}"));
    assertNull(OpenRtbMacros.valueOfKey("${UNKNOWN_MACRO}"));
  }

  @Test
  public void testProcess() {
    BidRequest req = BidRequest.newBuilder()
        .setId("req1")
        .addCur("USD")
        .addImp(Impression.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()))
        .build();
    Bid.Builder bid = Bid.newBuilder()
        // See docs about allowed macro dependencies.
        .setAdid("ad-" + OpenRtbMacros.AUCTION_CURRENCY.key())
        .setAdm("adm-" + OpenRtbMacros.AUCTION_PRICE.key())
        .setCid("c-" + OpenRtbMacros.AUCTION_SEAT_ID.key())
        .setCrid("cr-" + OpenRtbMacros.AUCTION_ID.key())
        .setDealid("deal-" + OpenRtbMacros.AUCTION_CURRENCY.key())
        .setId("bid-" + OpenRtbMacros.AUCTION_AD_ID.key())
        .setImpid("imp1")
        .setIurl("http://iurl?id=" + OpenRtbMacros.AUCTION_BID_ID.key())
        .setNurl("http://nurl?id=" + OpenRtbMacros.AUCTION_IMP_ID.key())
        .setPrice(10000);
    BidResponse.Builder resp = BidResponse.newBuilder()
        .addSeatbid(SeatBid.newBuilder()
            .setSeat("seat1")
            .addBid(bid));
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    processor.process(req, resp);
    bid = resp.getSeatbidBuilder(0).getBidBuilder(0);
    assertEquals("ad-USD", bid.getAdid());
    assertEquals("adm-10000", bid.getAdm());
    assertEquals("c-seat1", bid.getCid());
    assertEquals("cr-req1", bid.getCrid());
    assertEquals("deal-USD", bid.getDealid());
    assertEquals("bid-ad-USD", bid.getId());
    assertEquals("http://iurl?id=bid-ad-USD", bid.getIurl());
    assertEquals("http://nurl?id=imp1", bid.getNurl());
  }

  @Test
  public void testNoData() {
    BidRequest request = BidRequest.newBuilder()
        .setId("req1")
        .addImp(Impression.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()))
        .build();
    Bid.Builder bid = Bid.newBuilder()
        .setId("bid-" + OpenRtbMacros.AUCTION_CURRENCY.key())
        .setImpid("imp-" + OpenRtbMacros.AUCTION_SEAT_ID.key())
        .setPrice(10000);
    BidResponse.Builder resp = BidResponse.newBuilder()
        .addSeatbid(SeatBid.newBuilder().addBid(bid));
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    processor.process(request, resp);
    bid = resp.getSeatbidBuilder(0).getBidBuilder(0);
    assertEquals("bid-", bid.getId());
    assertEquals("imp-", bid.getImpid());
  }

  @Test(expected = UndefinedMacroException.class)
  public void testUndefinedImp() {
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    processor.process(new SnippetProcessorContext(
            BidRequest.newBuilder().setId("req1").build(),
            BidResponse.newBuilder(),
            Bid.newBuilder().setId("bid1").setImpid("imp1").setPrice(10000)),
        OpenRtbMacros.AUCTION_IMP_ID.key());
  }

  @Test(expected = UndefinedMacroException.class)
  public void testUndefinedSeat() {
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    processor.process(new SnippetProcessorContext(
            BidRequest.newBuilder().setId("req1").build(),
            BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder().setSeat("seat1")),
            Bid.newBuilder().setId("bid1").setImpid("imp1").setPrice(10000)),
        OpenRtbMacros.AUCTION_SEAT_ID.key());
  }
}
