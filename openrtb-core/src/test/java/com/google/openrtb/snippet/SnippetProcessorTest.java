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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import com.google.common.collect.ImmutableList;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.TestUtil;

import org.junit.Test;

import java.util.List;

/**
 * Tests for {@link SnippetProcessor}.
 */
public class SnippetProcessorTest {
  private static BidRequest req = BidRequest.newBuilder()
      .setId("1")
      .build();
  private static Bid bid = Bid.newBuilder()
      .setId("1")
      .setImpid("1")
      .setPrice(10000)
      .build();
  private static BidResponse resp = BidResponse.newBuilder()
      .setId("1")
      .addSeatbid(SeatBid.newBuilder()
          .addBid(bid))
          .build();
  private final SnippetProcessor processor = new SnippetProcessor() {
    @Override protected List<SnippetMacroType> registerMacros() {
      return ImmutableList.<SnippetMacroType>builder()
          .addAll(super.registerMacros())
          .addAll(asList(TestMacros.values()))
          .build();
    }
    @Override protected void processMacroAt(
        SnippetProcessorContext ctx, StringBuilder sb, SnippetMacroType macroDef) {
      sb.append("#");
    }
  };

  @Test
  public void testContext() {
    SnippetProcessorContext ctx = new SnippetProcessorContext(req, resp, bid);
    TestUtil.testCommonMethods(ctx);
    assertSame(req, ctx.request());
    assertSame(resp, ctx.response());
    assertSame(bid, ctx.bid());
  }

  @Test
  public void testNullProcessor() {
    SnippetProcessorContext ctx = new SnippetProcessorContext(req, resp, bid);
    String snippet = OpenRtbMacros.AUCTION_ID.key();
    assertSame(snippet, SnippetProcessor.NULL.process(ctx, snippet));
  }

  @Test
  public void testUndefinedMacro1() {
    UndefinedMacroException e = new UndefinedMacroException(TestMacros.TEST);
    assertSame(TestMacros.TEST, e.key());
  }

  @Test
  public void testUndefinedMacro2() {
    UndefinedMacroException e = new UndefinedMacroException(TestMacros.TEST, "msg");
    assertSame(TestMacros.TEST, e.key());
  }

  @Test
  public void testUrlEncoding() {
    assertEquals("", process(""));
    assertEquals("{!+/}", process("{!+/}"));
    assertEquals("%!+/%", process("%!+/%"));
    assertEquals(esc("aaa"), process("%{aaa}%"));
    assertEquals(esc("!+/"), process("%{!+/}%"));
    assertEquals(esc("!+/") + esc("aaa"), process("%{!+/}%%{aaa}%"));
    assertEquals(esc2("!+/") + esc("aaa"), process("%{%{!+/}%aaa}%"));
    assertEquals(
        esc2(esc2(esc2(esc2(esc2("!"))))),
        process("%{%{%{%{%{%{%{%{%{%{!}%}%}%}%}%}%}%}%}%}%"));
  }

  @Test
  public void testUrlEncodingBad() {
    assertEquals("bad!}%", process("bad!}%"));
    assertEquals("bad!}%" + esc("+"), process("bad!}%%{+}%"));
    assertEquals("bad!", process("bad!%{"));
    assertEquals("bad!", process("%{bad!"));
    assertEquals(esc("good!") + "{bad!}%", process("%{good!}%{bad!}%"));
  }

  @Test
  public void testMacro() {
    assertNotNull(processor.toString());

    assertEquals("${UNKNOWN_MACRO}", process("${UNKNOWN_MACRO}"));
    assertEquals("#", process(TestMacros.TEST.key()));
    assertEquals(esc("#"), process("%{" + TestMacros.TEST.key() + "}%"));
  }

  private String process(String snippet) {
    return process(snippet, true);
  }

  private String process(String snippet, boolean full) {
    BidRequest request = BidRequest.newBuilder()
        .setId("1")
        .addImp(Impression.newBuilder()
            .setId("1")).build();
    BidResponse response = createBidResponse(snippet, full);
    return processor.process(
        new SnippetProcessorContext(request, response, response.getSeatbid(0).getBid(0)), snippet);
  }

  private static BidResponse createBidResponse(String snippet, boolean full) {
    Bid.Builder bid = Bid.newBuilder()
        .setId("bid1")
        .setImpid("1")
        .setPrice(1000);
    if (full) {
      bid
          .setAdid("ad1")
          .setAdm(snippet)
          .setIurl("https://mycontent.com/creative.png");
    }
    return BidResponse.newBuilder()
        .setId("1")
        .addSeatbid(SeatBid.newBuilder()
            .setSeat("seat1")
            .addBid(bid))
        .build();
  }

  private static String esc(String s) {
    return SnippetProcessor.getEscaper().escape(s);
  }

  private static String esc2(String s) {
    return esc(esc(s));
  }

  static enum TestMacros implements SnippetMacroType {
    TEST;

    @Override public String key() {
      return "${TEST}";
    }
  }
}
