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

package com.google.openrtb.json;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.AuctionType;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Content.ContentContext;
import com.google.openrtb.OpenRtb.BidRequest.Content.QAGMediaRating;
import com.google.openrtb.OpenRtb.BidRequest.Content.VideoQuality;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Data.Segment;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Device.ConnectionType;
import com.google.openrtb.OpenRtb.BidRequest.Device.DeviceType;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Geo.LocationType;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.APIFramework;
import com.google.openrtb.OpenRtb.BidRequest.Imp.AdPosition;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner.BannerAdType;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner.ExpandableDirection;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp.Deal;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.CompanionAd;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.ContentDeliveryMethod;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.VASTCompanionType;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.VideoBidResponseProtocol;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.VideoLinearity;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.VideoPlaybackMethod;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regs;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.NoBidReason;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.CreativeAttribute;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeResponse;
import com.google.openrtb.Test.Test1;
import com.google.openrtb.Test.Test2;
import com.google.openrtb.TestExt;
import com.google.openrtb.TestUtil;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tests for {@link OpenRtbJsonFactory}, {@link OpenRtbJsonReader}, {@link OpenRtbJsonWriter}.
 */
public class OpenRtbJsonTest {
  private static final Logger logger = LoggerFactory.getLogger(OpenRtbJsonTest.class);
  private static final Test1 test1 = Test1.newBuilder().setTest1("data1").build();
  private static final Test2 test2 = Test2.newBuilder().setTest2("data2").build();

  @Test
  public void testJsonGeneratedFiles() throws IOException {
    OpenRtbJsonRequestHelper.testJsonGeneratedFiles();
    OpenRtbJsonResponseHelper.testJsonGeneratedFiles();
  }

  @Test
  public void testJsonFactory() {
    assertThat(OpenRtbJsonFactory.create().getJsonFactory()).isNotNull();
    JsonFactory jf = new JsonFactory();
    assertThat(OpenRtbJsonFactory.create().setJsonFactory(jf).getJsonFactory()).isSameAs(jf);
    TestUtil.testCommonMethods(new Test2Reader<BidRequest.Builder>(TestExt.testRequest2, "x"));
    TestUtil.testCommonMethods(new Test4Writer());
  }

  @Test
  public void testRequest_site() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setSite(newSite()).build());
  }

  @Test
  public void testRequest_app() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setApp(newApp()).build());
  }

  @Test
  public void testRequest_AlternateFields() throws IOException {
    testRequest(newJsonFactory()
        .register(new OpenRtbJsonExtWriter<Test1>() {
          @Override protected void write(Test1 ext, JsonGenerator gen) throws IOException {
            gen.writeStringField("test1", "data1");
            gen.writeStringField("test2", "data2");
            gen.writeStringField("test1", "data1");
            gen.writeStringField("test2", "data2");
          }
        }, Test1.class, BidRequest.class),
        newBidRequest().build());
  }

  @Test
  public void testRequest_emptyMessages() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0").build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0"))
        .setDevice(Device.newBuilder())
        .setApp(App.newBuilder())
        .setUser(User.newBuilder())
        .setRegs(Regs.newBuilder())
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0")
            .setBanner(Banner.newBuilder())
            .setPmp(Pmp.newBuilder()))
        .setDevice(Device.newBuilder().setGeo(Geo.newBuilder()))
        .setSite(Site.newBuilder())
        .setUser(User.newBuilder().addData(Data.newBuilder()))
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Imp.newBuilder().setId("0")
            .setVideo(Video.newBuilder())
            .setPmp(Pmp.newBuilder().addDeals(Deal.newBuilder().setId("0"))))
        .setSite(Site.newBuilder()
            .setContent(Content.newBuilder())
            .setPublisher(Publisher.newBuilder()))
            .setUser(User.newBuilder().addData(Data.newBuilder().addSegment(Segment.newBuilder())))
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .setSite(Site.newBuilder()
            .setContent(Content.newBuilder().setProducer(Producer.newBuilder())))
        .build());
  }

  @Test
  public void testRequest_emptyToNull() throws IOException {
    OpenRtbJsonReader reader = OpenRtbJsonFactory.create().setStrict(false).newReader();
    assertThat(reader.readBidRequest("")).isNull();
    assertThat(reader.readBidResponse("")).isNull();
  }

  @Test
  public void testRequest_extNoReadersRegistered() throws IOException {
    OpenRtbJsonReader reader = OpenRtbJsonFactory.create().newReader();
    BidRequest req = BidRequest.newBuilder().setId("0").build();
    // Based on Issue #34
    assertThat(reader.readBidRequest("{ \"ext\": { \"x\": 0 }, \"id\": \"0\" }")).isEqualTo(req);
  }

  @Test
  public void testRequest_extNoReadersConsume() throws IOException {
    OpenRtbJsonReader reader = newJsonFactory().newReader();
    BidRequest req = BidRequest.newBuilder().setId("0").build();
    // Based on Issue #34
    assertThat(reader.readBidRequest("{ \"ext\": { \"x\": { } }, \"id\": \"0\" }")).isEqualTo(req);
  }

  @Test
  public void testRequest_extBug() throws IOException {
    OpenRtbJsonReader reader = OpenRtbJsonFactory.create().newReader();
    BidRequest req = BidRequest.newBuilder().setId("0").build();
    // Based on Issue #34
    assertThat(reader.readBidRequest("{ \"ext\": { \"x\": 0, \"y\": {} }, \"id\": \"0\" }"))
        .isEqualTo(req);
  }

  @Test(expected = JsonParseException.class)
  public void testRequest_extNonObject1() throws IOException {
    newJsonFactory().newReader().readBidRequest("{ \"ext\": [ \"x\": { } ], \"id\": \"0\" }");
  }

  @Test(expected = JsonParseException.class)
  public void testRequest_extNonObject2() throws IOException {
    newJsonFactory().newReader().readBidRequest("{ \"ext\": \"x\", \"id\": \"0\" }");
  }

  @Test
  public void testResponse() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    BidResponse resp = newBidResponse(false).build();
    String jsonResp = testResponse(jsonFactory, resp);
    assertThat(jsonFactory.newWriter().writeBidResponse(newBidResponse(false).build()))
        .isEqualTo(jsonResp);
  }

  @Test
  public void testRequestWithNative() throws IOException {
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_STRING, false, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_NOROOT_OBJECT, false, true);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___STRING, true, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__SHORT_ROOT___OBJECT, true, true);

    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_STRING, false, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__NOROOT_OBJECT, false, true);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___STRING, true, false);
    testRequestWithNative(OpenRtbJsonRequestHelper.REQUEST__FULL__ROOT___OBJECT, true, true);
  }

  @Test
  public void testResponseWithNative() throws IOException {
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_STRING, false, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_NOROOT_OBJECT, false, true);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___STRING, true, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_SHORT_ROOT___OBJECT, true, true);

    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_STRING, false, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__NOROOT_OBJECT, false, true);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___STRING, true, false);
    testResponseWithNative(OpenRtbJsonResponseHelper.RESPONSE_FULL__ROOT___OBJECT, true, true);
  }

  @Test
  public void testExt1() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .setExtension(TestExt.testResponse1, test1)
        .build());
  }

  @Test
  public void testExt2Repeated() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .addExtension(TestExt.testResponse2, test2)
        .addExtension(TestExt.testResponse2, test2)
        .build());
  }

  @Test
  public void testExt3() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .setExtension(TestExt.testResponse3, 99)
        .build());
  }

  @Test
  public void testExt4() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .addExtension(TestExt.testResponse4, 10)
        .addExtension(TestExt.testResponse4, 20)
        .build());
  }

  @Test
  public void testExt2Scalar() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testRequest(jsonFactory, BidRequest.newBuilder()
        .setId("0")
        .setExtension(TestExt.testRequest2, test2)
        .build());
  }

  @Test
  public void testExt2Double() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder()
        .setId("0")
        .setExtension(TestExt.testResponse2A, Test2.newBuilder().setTest2("data2a").build())
        .setExtension(TestExt.testResponse2B, Test2.newBuilder().setTest2("data2b").build())
        .build());
  }

  @Test
  public void testResponse_emptyMessages() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder().setId("1").build());
    testResponse(jsonFactory,
        BidResponse.newBuilder().setId("1").addSeatbid(SeatBid.newBuilder()).build());
    testResponse(jsonFactory, BidResponse.newBuilder().setId("1").addSeatbid(SeatBid.newBuilder()
        .addBid(Bid.newBuilder().setId("0").setImpid("0").setPrice(0))).build());
  }

  @Test(expected = JsonParseException.class)
  public void testBadArrayField() throws IOException, JsonParseException {
    String test = // based on Issue #10; sample message from SpotXchange with non-array "cat"
          "{\n \"id\": \"0\",\n \"imp\": [\n {\n \"id\": \"1\",\n \"banner\": "
        + "{\n \"h\": 250,\n \"w\": 300,\n \"pos\": 1\n },\n \"bidfloor\": 0.05\n }\n ],\n "
        + "\"site\": {\n \"id\": \"15047\",\n \"domain\": \"dailymotion.com\",\n "
        + "\"cat\": \"IAB1\",\n "
        + "\"page\": \"http://www.dailymotion.com\",\n "
        + "\"publisher\": {\n \"id\": \"8796\",\n \"name\": \"dailymotion\",\n "
        + "\"cat\": \"IAB3-1\",\n "
        + "\"domain\": \"dailymotion.com\"\n }\n },\n \"user\": {\n \"id\": \"0\"\n },\n "
        + "\"device\": {\n \"ua\": \"Mozilla/4.0\",\n "
        + "\"ip\": \"1.2.3.4\"\n },\n \"at\": 1,\n \"cur\": [\n \"USD\"\n ]\n}";
    newJsonFactory().newReader().readBidRequest(test);
  }

  @Test
  public void testIgnoredFields() throws IOException {
    String test =
          "{ \"id\": \"0\", "
        + "\"x1\": 10, \"at\": 1, "
        + "\"x2\": \"x\", \"x3\": [4], \"test\": 1, "
        + "\"x4\": { \"x5\": [] }, \"tmax\": 100, "
        + "\"ext\": { \"x6\": [ { \"x7\": 100, \"x8\": 3.1415 } ], \"test1\": \"*\" }"
        + "}";
    assertThat(newJsonFactory().newReader().readBidRequest(test))
        .isEqualTo(BidRequest.newBuilder()
            .setId("0")
            .setAt(AuctionType.FIRST_PRICE)
            .setTest(true)
            .setTmax(100)
            .setExtension(TestExt.testRequest1, Test1.newBuilder().setTest1("*").build())
            .build());
  }

  @Test
  public void testNulls() throws IOException {
    String test = // based on issue #13
          "{ \"id\": \"0\",\n  \"app\": { \"content\": { "
        + "\"keywords\": null },\n \"id\": \"56600\",\n \"cat\": [\"IAB19\"],\n "
        + "\"keywords\": \"\",\n \"name\": \"Emoji Free!\",\n \"ver\": null\n } \n}";
    newJsonFactory().newReader().readBidRequest(test);
  }

  @Test
  public void testKeywordsAsArray() throws IOException {
    String test =
           "{ \"id\": \"0\",\n  \"site\": { \"content\": { "
        + "\"keywords\":  [\"foo\", \"bar\"]},\n \"id\": \"56600\",\n \"cat\": [\"IAB19\"],\n "
        + "\"keywords\": \"\",\n \"name\": \"Emoji Free!\",\n \"ver\": null\n } \n}";
    BidRequest bidRequest = newJsonFactory().newReader().readBidRequest(test);
    assertThat(bidRequest.getSite().getContent().getKeywords()).isEqualTo("foo,bar");
  }

  static void testRequest(OpenRtbJsonFactory jsonFactory, BidRequest req) throws IOException {
    String jsonReq = jsonFactory.newWriter().writeBidRequest(req);
    logger.info(jsonReq);
    jsonFactory.setStrict(false).newWriter().writeBidRequest(req);
    BidRequest req2 = jsonFactory.newReader().readBidRequest(jsonReq);
    assertThat(req2).isEqualTo(req);
    jsonFactory.setStrict(false).newReader().readBidRequest(jsonReq);
  }

  static void testRequestWithNative(String requestString, boolean rootNative, boolean nativeAsObject) throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory().setForceNativeAsObject(nativeAsObject);
    OpenRtb.BidRequest bidRequest = jsonFactory.newReader().readBidRequest(requestString);
    String jsonRequNativeStr = jsonFactory.setRootNativeField(rootNative).newWriter().writeBidRequest(bidRequest);
    ObjectMapper mapper = new ObjectMapper();
    Object json = mapper.readValue(jsonRequNativeStr, Object.class);
    jsonRequNativeStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    assertThat(jsonRequNativeStr).isEqualTo(requestString);
  }

  static String testResponse(OpenRtbJsonFactory jsonFactory, BidResponse resp) throws IOException {
    String jsonResp = jsonFactory.newWriter().writeBidResponse(resp);
    logger.info(jsonResp);
    jsonFactory.setStrict(false).newWriter().writeBidResponse(resp);
    OpenRtb.BidResponse resp2 = jsonFactory.newReader().readBidResponse(jsonResp);
    assertThat(resp2).isEqualTo(resp);
    jsonFactory.setStrict(false).newReader().readBidResponse(jsonResp);
    return jsonResp;
  }

  static void testResponseWithNative(String responseString, boolean rootNative, boolean nativeAsObject) throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory().setForceNativeAsObject(nativeAsObject);
    OpenRtb.BidResponse bidResponse1 = jsonFactory.newReader().readBidResponse(responseString);
    String jsonRespNativeStr = jsonFactory.setRootNativeField(rootNative).newWriter().writeBidResponse(bidResponse1);
    ObjectMapper mapper = new ObjectMapper();
    Object json = mapper.readValue(jsonRespNativeStr, Object.class);
    jsonRespNativeStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    assertThat(jsonRespNativeStr).isEqualTo(responseString);
  }

  static OpenRtbJsonFactory newJsonFactory() {
    return OpenRtbJsonFactory.create()
        .setJsonFactory(new JsonFactory())
        // BidRequest Readers
        .register(new Test1Reader<BidRequest.Builder>(TestExt.testRequest1),
            BidRequest.Builder.class)
        .register(new Test2Reader<BidRequest.Builder>(TestExt.testRequest2, "test2ext"),
            BidRequest.Builder.class)
        .register(new Test1Reader<App.Builder>(TestExt.testApp), App.Builder.class)
        .register(new Test1Reader<Content.Builder>(TestExt.testContent), Content.Builder.class)
        .register(new Test1Reader<Producer.Builder>(TestExt.testProducer), Producer.Builder.class)
        .register(new Test1Reader<Publisher.Builder>(TestExt.testPublisher),
            Publisher.Builder.class)
        .register(new Test1Reader<Device.Builder>(TestExt.testDevice), Device.Builder.class)
        .register(new Test1Reader<Geo.Builder>(TestExt.testGeo), Geo.Builder.class)
        .register(new Test1Reader<Imp.Builder>(TestExt.testImp), Imp.Builder.class)
        .register(new Test1Reader<Banner.Builder>(TestExt.testBanner), Banner.Builder.class)
        .register(new Test1Reader<Native.Builder>(TestExt.testNative), Native.Builder.class)
        .register(new Test1Reader<Pmp.Builder>(TestExt.testPmp), Pmp.Builder.class)
        .register(new Test1Reader<Deal.Builder>(TestExt.testDeal), Deal.Builder.class)
        .register(new Test1Reader<Video.Builder>(TestExt.testVideo), Video.Builder.class)
        .register(new Test1Reader<Regs.Builder>(TestExt.testRegs), Regs.Builder.class)
        .register(new Test1Reader<Site.Builder>(TestExt.testSite), Site.Builder.class)
        .register(new Test1Reader<User.Builder>(TestExt.testUser), User.Builder.class)
        .register(new Test1Reader<Data.Builder>(TestExt.testData), Data.Builder.class)
        .register(new Test1Reader<Segment.Builder>(TestExt.testSegment), Segment.Builder.class)
        // BidResponse Readers
        .register(new Test1Reader<BidResponse.Builder>(TestExt.testResponse1),
            BidResponse.Builder.class)
        .register(new Test2Reader<BidResponse.Builder>(TestExt.testResponse2, "test2arr"),
            BidResponse.Builder.class)
        .register(new Test2Reader<BidResponse.Builder>(TestExt.testResponse2A, "test2a"),
            BidResponse.Builder.class)
        .register(new Test2Reader<BidResponse.Builder>(TestExt.testResponse2B, "test2b"),
            BidResponse.Builder.class)
        .register(new Test3Reader(), BidResponse.Builder.class)
        .register(new Test4Reader(), BidResponse.Builder.class)
        .register(new Test1Reader<SeatBid.Builder>(TestExt.testSeat), SeatBid.Builder.class)
        .register(new Test1Reader<Bid.Builder>(TestExt.testBid), Bid.Builder.class)
        // BidRequest Writers
        .register(new Test1Writer(), Test1.class, BidRequest.class)
        .register(new Test2Writer("test2ext"), Test2.class, BidRequest.class)
        .register(new Test1Writer(), Test1.class, App.class)
        .register(new Test1Writer(), Test1.class, Device.class)
        .register(new Test1Writer(), Test1.class, Site.class)
        .register(new Test1Writer(), Test1.class, User.class)
        .register(new Test1Writer(), Test1.class, Geo.class)
        .register(new Test1Writer(), Test1.class, Data.class)
        .register(new Test1Writer(), Test1.class, Segment.class)
        .register(new Test1Writer(), Test1.class, Publisher.class)
        .register(new Test1Writer(), Test1.class, Content.class)
        .register(new Test1Writer(), Test1.class, Producer.class)
        .register(new Test1Writer(), Test1.class, Imp.class)
        .register(new Test1Writer(), Test1.class, Banner.class)
        .register(new Test1Writer(), Test1.class, Video.class)
        .register(new Test1Writer(), Test1.class, Native.class)
        .register(new Test1Writer(), Test1.class, Pmp.class)
        .register(new Test1Writer(), Test1.class, Deal.class)
        .register(new Test1Writer(), Test1.class, Regs.class)
        .register(new Test1Writer(), Test1.class, SeatBid.class)
        .register(new Test1Writer(), Test1.class, Bid.class)
        // BidResponse Writers
        .register(new Test1Writer(), Test1.class, BidResponse.class, "testResponse1")
        .register(new Test2Writer("test2arr"), Test2.class, BidResponse.class, "testResponse2")
        .register(new Test2Writer("test2a"), Test2.class, BidResponse.class, "testResponse2a")
        .register(new Test2Writer("test2b"), Test2.class, BidResponse.class, "testResponse2b")
        .register(new Test3Writer(), Integer.class, BidResponse.class, "testResponse3")
        .register(new Test4Writer(), Integer.class, BidResponse.class, "testResponse4");
  }

  @SuppressWarnings("deprecation")
  static BidRequest.Builder newBidRequest() {
    return BidRequest.newBuilder()
        .setId("3031323334353637")
        .addImp(Imp.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()
                .setWmax(300)
                .setWmin(200)
                .setHmax(100)
                .setHmin(50)
                .setId("banner1")
                .setPos(AdPosition.ABOVE_THE_FOLD)
                .addBtype(BannerAdType.JAVASCRIPT_AD)
                .addBattr(CreativeAttribute.TEXT_ONLY)
                .addMimes("image/gif")
                .setTopframe(true)
                .addExpdir(ExpandableDirection.RIGHT)
                .addApi(APIFramework.MRAID_1)
                .setExtension(TestExt.testBanner, test1))
            .setDisplaymanager("dm1")
            .setDisplaymanagerver("1.0")
            .setInstl(false)
            .setTagid("tag1")
            .setBidfloor(100.0)
            .setBidfloorcur("USD")
            .setSecure(false)
            .addIframebuster("buster1")
            .setPmp(Pmp.newBuilder()
                .setPrivateAuction(false)
                .addDeals(Deal.newBuilder()
                    .setId("deal1")
                    .setBidfloor(200.0)
                    .setBidfloorcur("USD")
                    .addWseat("seat2")
                    .addWadomain("goodadv1")
                    .setAt(AuctionType.SECOND_PRICE)
                    .setExtension(TestExt.testDeal, test1))
                .setExtension(TestExt.testPmp, test1))
            .setExtension(TestExt.testImp, test1))
        .addImp(Imp.newBuilder()
            .setId("imp2")
            .setVideo(Video.newBuilder()
                .addMimes("video/vp9")
                .setLinearity(VideoLinearity.LINEAR)
                .setMinduration(15)
                .setMaxduration(60)
                .setProtocol(VideoBidResponseProtocol.VAST_3_0)
                .addProtocols(VideoBidResponseProtocol.VAST_2_0)
                .setW(200)
                .setH(50)
                .setStartdelay(0)
                .setSequence(1)
                .addBattr(CreativeAttribute.TEXT_ONLY)
                .setMaxextended(120)
                .setMinbitrate(1000)
                .setMaxbitrate(2000)
                .setBoxingallowed(false)
                .addPlaybackmethod(VideoPlaybackMethod.CLICK_TO_PLAY)
                .addDelivery(ContentDeliveryMethod.STREAMING)
                .setPos(AdPosition.ABOVE_THE_FOLD)
                .addCompanionad(Banner.newBuilder()
                    .setId("compad1")
                    .setW(100)
                    .setH(50))
                .setCompanionad21(CompanionAd.newBuilder()
                    .addBanner(Banner.newBuilder()
                    .setId("compad2")
                    .setW(110)
                    .setH(60)))
                .addApi(APIFramework.VPAID_2)
                .addCompaniontype(VASTCompanionType.HTML)
                .setExtension(TestExt.testVideo, test1)))
        .addImp(Imp.newBuilder()
            .setId("imp3")
            .setNative(Native.newBuilder()
                .setRequestNative(NativeRequest.newBuilder().setVer("1"))
                .setVer("1.0")
                .addApi(APIFramework.MRAID_1)
                .addBattr(CreativeAttribute.TEXT_ONLY)
                .setExtension(TestExt.testNative, test1))
            .setExtension(TestExt.testImp, test1))
        .setDevice(Device.newBuilder()
            .setUa("Chrome")
            .setGeo(Geo.newBuilder()
                .setLat(90.0)
                .setLon(45.0)
                .setType(LocationType.GPS_LOCATION)
                .setCountry("USA")
                .setRegion("New York")
                .setRegionfips104("US36")
                .setMetro("New York")
                .setCity("New York City")
                .setZip("10000")
                .setUtcoffset(3600)
                .setExtension(TestExt.testGeo, test1))
            .setDnt(false)
            .setLmt(false)
            .setIp("192.168.1.0")
            .setIpv6("1:2:3:4:5:6:0:0")
            .setDevicetype(DeviceType.MOBILE)
            .setMake("Motorola")
            .setModel("MotoX")
            .setOs("Android")
            .setOsv("3.2.1")
            .setHwv("X")
            .setW(640)
            .setH(1024)
            .setPpi(300)
            .setPxratio(1.0)
            .setJs(true)
            .setFlashver("11")
            .setLanguage("en")
            .setCarrier("77777")
            .setConnectiontype(ConnectionType.CELL_4G)
            .setIfa("999")
            .setDidsha1("1234")
            .setDidmd5("4321")
            .setDpidsha1("5678")
            .setDpidmd5("8765")
            .setMacsha1("abc")
            .setMacmd5("xyz")
            .setExtension(TestExt.testDevice, test1))
        .setUser(User.newBuilder()
            .setId("user1")
            .setBuyeruid("Picard")
            .setYob(1973)
            .setGender("M")
            .setKeywords("boldly,going")
            .setCustomdata("data1")
            .setGeo(Geo.newBuilder().setZip("12345"))
            .addData(Data.newBuilder()
                .setId("data1")
                .setName("dataname1")
                .addSegment(Segment.newBuilder()
                    .setId("seg1")
                    .setName("segname1")
                    .setValue("segval1")
                    .setExtension(TestExt.testSegment, test1))
                .setExtension(TestExt.testData, test1))
            .setExtension(TestExt.testUser, test1))
        .setAt(AuctionType.SECOND_PRICE)
        .setTmax(100)
        .addWseat("seat1")
        .setAllimps(false)
        .addCur("USD")
        .addAllBcat(asList("IAB11", "IAB11-4"))
        .addBadv("badguy")
        .setRegs(Regs.newBuilder()
            .setCoppa(true)
            .setExtension(TestExt.testRegs, test1))
        .setTest(false)
        .setExtension(TestExt.testRequest2, test2)
        .setExtension(TestExt.testRequest1, test1);
  }

  static Site.Builder newSite() {
    return Site.newBuilder()
        .setId("88")
        .setName("CNN")
        .setDomain("cnn.com")
        .addCat("IAB1")
        .addSectioncat("IAB1-2")
        .addPagecat("IAB1-2")
        .setPage("http://cnn.com/news/elections.html")
        .setPrivacypolicy(true)
        .setRef("http://referrer.com")
        .setSearch("http://google.com?q=elections")
        .setMobile(false)
        .setPublisher(Publisher.newBuilder()
            .setId("pub1")
            .setName("Turner")
            .addCat("IAB1")
            .setDomain("tbs.com")
            .setExtension(TestExt.testPublisher, test1))
        .setContent(Content.newBuilder()
            .setId("cont1")
            .setEpisode(1)
            .setTitle("Godzilla")
            .setSeries("Dr. Who")
            .setSeason("S4")
            .setUrl("http://who.com")
            .addCat("IAB10-2")
            .setVideoquality(VideoQuality.PROFESSIONAL)
            .setKeywords("sci-fi,aliens")
            .setContentrating("R")
            .setUserrating("Awesome!")
            .setContext(ContentContext.OTHER)
            .setLivestream(false)
            .setSourcerelationship(false)
            .setProducer(Producer.newBuilder()
                .setId("prod1")
                .setName("Warner")
                .addCat("IAB10")
                .setDomain("http://bros.com")
                .setExtension(TestExt.testProducer, test1))
            .setLen(240)
            .setQagmediarating(QAGMediaRating.MATURE)
            .setEmbeddable(false)
            .setLanguage("en")
            .setExtension(TestExt.testContent, test1))
        .setKeywords("news,politics")
        .setExtension(TestExt.testSite, test1);
  }

  static App.Builder newApp() {
    return App.newBuilder()
        .setId("PewDiePie")
        .setName("CNN App")
        .setDomain("cnn.com")
        .addCat("IAB1")
        .addSectioncat("IAB1-1")
        .addPagecat("IAB1-2")
        .setVer("1.0")
        .setBundle("com.cnn.app")
        .setPrivacypolicy(true)
        .setPaid(false)
        .setPublisher(Publisher.newBuilder().setId("pub9"))
        .setContent(Content.newBuilder().setId("cont9"))
        .setKeywords("news,politics")
        .setStoreurl("http://appstore.com/cnn")
        .setExtension(TestExt.testApp, test1);
  }

  static BidResponse.Builder newBidResponse(boolean admNative) {
    Bid.Builder bid = Bid.newBuilder()
        .setId("bid1")
        .setImpid("imp1")
        .setPrice(19.95)
        .setAdid("adid1")
        .setNurl("http://iwon.com")
        .addAdomain("http://myads.com")
        .setIurl("http://mycdn.com/ad.gif")
        .setCid("cid1")
        .setCrid("crid1")
        .addAttr(CreativeAttribute.TEXT_ONLY)
        .setDealid("deal1")
        .setW(100)
        .setH(80)
        .setBundle("com.google.testapp")
        .addCat("IAB10-2")
        .setExtension(TestExt.testBid, test1);
    if (admNative) {
      bid.setAdmNative(NativeResponse.newBuilder()
          .setVer("1.0")
          .setLink(NativeResponse.Link.newBuilder()));
    } else {
      bid.setAdm("<test></test>");
    }
    return BidResponse.newBuilder()
        .setId("resp1")
        .addSeatbid(SeatBid.newBuilder()
            .addBid(bid)
            .setSeat("seat1")
            .setGroup(false)
            .setExtension(TestExt.testSeat, test1))
        .setBidid("bid1")
        .setCur("USD")
        .setCustomdata("mydata")
        .setNbr(NoBidReason.TECHNICAL_ERROR)
        .setExtension(TestExt.testResponse1, test1)
        .addExtension(TestExt.testResponse2, test2)
        .addExtension(TestExt.testResponse2, test2)
        .setExtension(TestExt.testResponse2A, test2)
        .setExtension(TestExt.testResponse2B, test2)
        .setExtension(TestExt.testResponse3, 99)
        .addExtension(TestExt.testResponse4, 10)
        .addExtension(TestExt.testResponse4, 20);
  }
}
