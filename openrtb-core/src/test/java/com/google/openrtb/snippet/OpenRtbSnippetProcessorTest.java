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
        .setId("1")
        .setImpid("1")
        .setPrice(10000)
        .setAdm("adm-" + OpenRtbMacros.AUCTION_ID.key());
    BidResponse.Builder resp = BidResponse.newBuilder()
        .setBidid("1")
        .addSeatbid(SeatBid.newBuilder()
            .setSeat("seat1")
            .addBid(bid));
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    processor.process(new SnippetProcessorContext(req, resp));
    bid = resp.getSeatbidBuilder(0).getBidBuilder(0);
    assertThat(bid.getAdm()).isEqualTo("adm-req1");
  }

  @Test
  public void testNoData() {
    BidRequest request = BidRequest.newBuilder()
        .setId("req1")
        .addImp(Imp.newBuilder()
            .setId("1")
            .setBanner(Banner.newBuilder()))
        .build();
    Bid.Builder bid = Bid.newBuilder()
        .setId("1")
        .setImpid("1")
        .setPrice(10000)
        .setAdm(OpenRtbMacros.AUCTION_CURRENCY.key());
    BidResponse.Builder resp = BidResponse.newBuilder()
        .addSeatbid(SeatBid.newBuilder().addBid(bid));
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    processor.process(new SnippetProcessorContext(request, resp));
    bid = resp.getSeatbidBuilder(0).getBidBuilder(0);
    assertThat(bid.getAdm()).isEqualTo("");
  }

  @Test(expected = UndefinedMacroException.class)
  public void testUndefinedImp() {
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    SnippetProcessorContext ctx = new SnippetProcessorContext(
        BidRequest.newBuilder().setId("req1").build(),
        BidResponse.newBuilder());
    ctx.setBid(Bid.newBuilder().setId("bid1").setImpid("imp1").setPrice(10000));
    processor.process(ctx, OpenRtbMacros.AUCTION_IMP_ID.key());
  }

  @Test(expected = UndefinedMacroException.class)
  public void testUndefinedSeat() {
    OpenRtbSnippetProcessor processor = new OpenRtbSnippetProcessor();
    SnippetProcessorContext ctx = new SnippetProcessorContext(
        BidRequest.newBuilder().setId("req1").build(),
        BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder().setSeat("seat1")));
    ctx.setBid(Bid.newBuilder().setId("bid1").setImpid("imp1").setPrice(10000));
    processor.process(ctx,
        OpenRtbMacros.AUCTION_SEAT_ID.key());
  }
}
