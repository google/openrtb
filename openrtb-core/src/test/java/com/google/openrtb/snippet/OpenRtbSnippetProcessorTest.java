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

import static com.google.common.truth.Truth.assertThat;

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
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
    SnippetProcessorContext ctx = new SnippetProcessorContext(req, resp);
    ctx.setBid(bid);
    assertThat(OpenRtbSnippetProcessor.ORTB_NULL.process(ctx, macro)).isSameAs(macro);
  }

  @Test
  public void testOpenRtbMacros() {
    TestUtil.testCommonEnum(OpenRtbMacros.values());
    assertThat(OpenRtbMacros.valueOfKey("${AUCTION_ID}")).isSameAs(OpenRtbMacros.AUCTION_ID);
    assertThat(OpenRtbMacros.valueOfKey("${UNKNOWN_MACRO}")).isNull();
  }

  @Test
  public void testProcess() {
    BidRequest req = BidRequest.newBuilder()
        .setId("req1")
        .addCur("USD")
        .addImp(Imp.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()))
        .build();
    Bid.Builder bid = Bid.newBuilder()
        // See docs about allowed macro dependencies.
        .setAdid("ad-" + OpenRtbMacros.AUCTION_CURRENCY.key())
        .setAdm("adm-" + OpenRtbMacros.AUCTION_PRICE.key())
        .setCid("c-" + OpenRtbMacros.AUCTION_SEAT_ID.key())
        .setCrid("cr-" + OpenRtbMacros.AUCTION_ID.key())
        .setDealid("%{deal-" + OpenRtbMacros.AUCTION_PRICE.key() + "}%")
        .setId("bid-" + OpenRtbMacros.AUCTION_AD_ID.key())
        .setImpid("imp1")
        .setIurl("http://iurl?id=" + OpenRtbMacros.AUCTION_BID_ID.key())
        .setNurl("http://nurl?id=" + OpenRtbMacros.AUCTION_IMP_ID.key())
        .setPrice(10000);
    BidResponse.Builder resp1 = BidResponse.newBuilder()
        .setBidid("bid-1")
        .addSeatbid(SeatBid.newBuilder()
            .setSeat("seat1")
            .addBid(bid));
    BidResponse.Builder resp2 = resp1.clone();
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor(true);
    processor.process(new SnippetProcessorContext(req, resp1));
    bid = resp1.getSeatbidBuilder(0).getBidBuilder(0);
    assertThat(bid.getAdid()).isEqualTo("ad-USD");
    assertThat(bid.getAdm()).isEqualTo("adm-${AUCTION_PRICE}");
    assertThat(bid.getCid()).isEqualTo("c-seat1");
    assertThat(bid.getCrid()).isEqualTo("cr-req1");
    assertThat(bid.getDealid()).isEqualTo("deal-%24%7BAUCTION_PRICE%7D");
    assertThat(bid.getId()).isEqualTo("bid-ad-USD");
    assertThat(bid.getIurl()).isEqualTo("http://iurl?id=bid-1");
    assertThat(bid.getNurl()).isEqualTo("http://nurl?id=imp1");
    processor = new OpenRtbSnippetProcessor(false);
    processor.process(new SnippetProcessorContext(req, resp2));
    bid = resp2.getSeatbidBuilder(0).getBidBuilder(0);
    assertThat(bid.getAdid()).isEqualTo("ad-${AUCTION_CURRENCY}");
    assertThat(bid.getAdm()).isEqualTo("adm-${AUCTION_PRICE}");
    assertThat(bid.getCid()).isEqualTo("c-${AUCTION_SEAT_ID}");
    assertThat(bid.getCrid()).isEqualTo("cr-${AUCTION_ID}");
    assertThat(bid.getDealid()).isEqualTo("%{deal-${AUCTION_PRICE}}%");
    assertThat(bid.getId()).isEqualTo("bid-${AUCTION_AD_ID}");
    assertThat(bid.getIurl()).isEqualTo("http://iurl?id=${AUCTION_BID_ID}");
    assertThat(bid.getNurl()).isEqualTo("http://nurl?id=${AUCTION_IMP_ID}");
  }

  @Test
  public void testNoData() {
    BidRequest request = BidRequest.newBuilder()
        .setId("req1")
        .addImp(Imp.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()))
        .build();
    Bid.Builder bid = Bid.newBuilder()
        .setId("bid1")
        .setImpid("imp1")
        .setAdm(
              OpenRtbMacros.AUCTION_AD_ID.key()
            + OpenRtbMacros.AUCTION_BID_ID.key()
            + OpenRtbMacros.AUCTION_CURRENCY.key()
            + OpenRtbMacros.AUCTION_SEAT_ID.key())
        .setPrice(10000);
    BidResponse.Builder resp = BidResponse.newBuilder()
        .addSeatbid(SeatBid.newBuilder().addBid(bid));
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor(true);
    processor.process(new SnippetProcessorContext(request, resp));
    bid = resp.getSeatbidBuilder(0).getBidBuilder(0);
    assertThat(bid.getAdm()).isEmpty();
  }

  @Test(expected = UndefinedMacroException.class)
  public void testUndefinedImp() {
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor(true);
    SnippetProcessorContext ctx = new SnippetProcessorContext(
        BidRequest.newBuilder().setId("req1").build(),
        BidResponse.newBuilder());
    ctx.setBid(Bid.newBuilder().setId("bid1").setImpid("imp1").setPrice(10000));
    processor.process(ctx, OpenRtbMacros.AUCTION_IMP_ID.key());
  }

  @Test(expected = UndefinedMacroException.class)
  public void testUndefinedSeat() {
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor(true);
    SnippetProcessorContext ctx = new SnippetProcessorContext(
        BidRequest.newBuilder().setId("req1").build(),
        BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder().setSeat("seat1")));
    ctx.setBid(Bid.newBuilder().setId("bid1").setImpid("imp1").setPrice(10000));
    processor.process(ctx,
        OpenRtbMacros.AUCTION_SEAT_ID.key());
  }
}
