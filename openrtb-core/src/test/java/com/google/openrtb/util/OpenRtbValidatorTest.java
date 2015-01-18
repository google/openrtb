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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Iterables;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Linearity;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Protocol;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.CreativeAttribute;

import com.codahale.metrics.MetricRegistry;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link OpenRtbValidator}.
 */
public class OpenRtbValidatorTest {
  private static BidRequest requestBanner = BidRequest.newBuilder()
      .setId("1")
      .addAllBadv(asList("adv1.com", "adv2.com"))
      .addAllBcat(asList("cat1", "cat2"))
      .addImp(Impression.newBuilder()
          .setId("1")
          .setBanner(Banner.newBuilder()
              .setId("1")
              .addAllBattr(asList(CreativeAttribute.ANNOYING, CreativeAttribute.POP_UP))))
      .build();
  private static BidRequest requestVideo = BidRequest.newBuilder()
      .setId("1")
      .addImp(Impression.newBuilder()
          .setId("1")
          .setVideo(Video.newBuilder()
              .setLinearity(Linearity.LINEAR).addProtocols(Protocol.VAST_3_0)
              .setMinduration(0).setMaxduration(0)
              .addAllBattr(asList(CreativeAttribute.ANNOYING, CreativeAttribute.POP_UP))
              .addCompanionad(Banner.newBuilder()
                  .setId("1")
                  .addAllBattr(asList(CreativeAttribute.TEXT_ONLY)))))
      .build();

  private MetricRegistry metricRegistry;
  private OpenRtbValidator validator;

  @Before
  public void setUp() {
    metricRegistry = new MetricRegistry();
    validator = new OpenRtbValidator(metricRegistry);
  }

  @Test
  public void testBannerGoodAttrs() {
    BidResponse.Builder response = testResponse(testBid()
        .addAllAdomain(asList("adv0.com", "adv3.com"))
        .addAllAttr(asList(CreativeAttribute.SURVEYS)));
    validator.validate(requestBanner, response);
    assertFalse(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testBannerNoAttrs() {
    BidResponse.Builder response = testResponse(testBid());
    validator.validate(requestBanner, response);
    assertFalse(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testBannerBlockedAdvertiser() {
    BidResponse.Builder response = testResponse(testBid()
        .addAllAdomain(asList("adv0.com", "adv2.com", "adv3.com"))
        .addAllAttr(asList(CreativeAttribute.SURVEYS)));
    validator.validate(requestBanner, response);
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testBannerBlockedCreativeAttribute() {
    BidResponse.Builder response = testResponse(testBid()
        .addAllAdomain(asList("adv0.com", "adv2.com", "adv3.com"))
        .addAllAttr(asList(CreativeAttribute.SURVEYS, CreativeAttribute.ANNOYING)));
    validator.validate(requestBanner, response);
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testVideoOk() {
    BidResponse.Builder response = testResponse(testBid()
        .addAllAdomain(asList("adv0.com", "adv3.com"))
        .addAllAttr(asList(CreativeAttribute.SURVEYS)));
    validator.validate(requestVideo, response);
    assertFalse(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testVideoBlockedCreativeAttribute() {
    BidResponse.Builder response = testResponse(testBid()
        .addAllAdomain(asList("adv0.com", "adv2.com", "adv3.com"))
        .addAllAttr(asList(CreativeAttribute.SURVEYS, CreativeAttribute.ANNOYING)));
    validator.validate(requestVideo, response);
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testVideoCompanionBlockedCreativeAttribute() {
    BidResponse.Builder response = testResponse(testBid()
        .addAllAdomain(asList("adv0.com", "adv2.com", "adv3.com"))
        .addAllAttr(asList(CreativeAttribute.SURVEYS, CreativeAttribute.TEXT_ONLY)));
    validator.validate(requestVideo, response);
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  @Test
  public void testNoImp() {
    BidResponse.Builder response = testResponse(testBid().setImpid("2"));
    validator.validate(requestBanner, response);
    assertTrue(Iterables.isEmpty(OpenRtbUtils.bids(response)));
  }

  private static BidResponse.Builder testResponse(Bid.Builder bid) {
    return BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder().addBid(bid));
  }

  private static Bid.Builder testBid() {
    return Bid.newBuilder()
        .setId("1")
        .setImpid("1")
        .setPrice(100000000);
  }
}
