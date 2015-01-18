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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Content.Context;
import com.google.openrtb.OpenRtb.BidRequest.Content.QAGMediaRating;
import com.google.openrtb.OpenRtb.BidRequest.Content.SourceRelationship;
import com.google.openrtb.OpenRtb.BidRequest.Content.VideoQuality;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Data.Segment;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Device.ConnectionType;
import com.google.openrtb.OpenRtb.BidRequest.Device.DeviceType;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Geo.LocationType;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.AdPosition;
import com.google.openrtb.OpenRtb.BidRequest.Impression.ApiFramework;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner.AdType;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner.ExpandableDirection;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Native;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP.Deal;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.CompanionType;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.ContentDelivery;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Linearity;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.PlaybackMethod;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Protocol;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regulations;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.NoBidReasonCode;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.ContentCategory;
import com.google.openrtb.OpenRtb.CreativeAttribute;
import com.google.openrtb.OpenRtbNative.NativeRequest;
import com.google.openrtb.Test.Test1;
import com.google.openrtb.Test.Test2;
import com.google.openrtb.TestExt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tests for {@link OpenRtbJsonWriter}.
 */
public class OpenRtbJsonTest {
  private static final Logger logger = LoggerFactory.getLogger(OpenRtbJsonTest.class);
  private static final Test1 test1 = Test1.newBuilder().setTest1("test1").build();
  private static final Test2 test2 = Test2.newBuilder().setTest2("test2").build();

  @Test
  public void testRequest_site() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setSite(newSite()).build());
  }

  @Test
  public void testRequest_app() throws IOException {
    testRequest(newJsonFactory(), newBidRequest().setApp(newApp()).build());
  }

  @Test(expected = IOException.class)
  public void testRequest_unknownField() throws IOException {
    testRequest(OpenRtbJsonFactory.create()
        .setJsonFactory(new JsonFactory())
        .register(new Test1Reader<BidRequest.Builder>(TestExt.testRequest1), "")
        .register(new OpenRtbJsonExtWriter<Test1>() {
          @Override public void write(Test1 ext, JsonGenerator gen) throws IOException {
            gen.writeStringField("unknownField", "junk");
          }
        }, Test1.class, "BidRequest"),
        newBidRequest().build());
  }

  @Test
  public void testRequest_AlternateFields() throws IOException {
    testRequest(newJsonFactory()
        .register(new OpenRtbJsonExtWriter<Test1>() {
          @Override public void write(Test1 ext, JsonGenerator gen) throws IOException {
            gen.writeStringField("test1", "test1");
            gen.writeStringField("test2", "test2");
            gen.writeStringField("test1", "test1");
            gen.writeStringField("test2", "test2");
          }
        }, Test1.class, ""),
        newBidRequest().build());
  }

  @Test
  public void testRequest_emptyMessages() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0").build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Impression.newBuilder().setId("0"))
        .setDevice(Device.newBuilder())
        .setApp(App.newBuilder())
        .setUser(User.newBuilder())
        .setRegs(Regulations.newBuilder())
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Impression.newBuilder().setId("0")
            .setBanner(Banner.newBuilder())
            .setPmp(PMP.newBuilder()))
        .setDevice(Device.newBuilder().setGeo(Geo.newBuilder()))
        .setSite(Site.newBuilder())
        .setUser(User.newBuilder().addData(Data.newBuilder()))
        .build());
    testRequest(jsonFactory, BidRequest.newBuilder().setId("0")
        .addImp(Impression.newBuilder().setId("0")
            .setVideo(Video.newBuilder()
                .setLinearity(Linearity.LINEAR).addProtocols(Protocol.VAST_3_0)
                .setMinduration(0).setMaxduration(1))
            .setPmp(PMP.newBuilder().addDeals(Deal.newBuilder().setId("0"))))
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
  public void testResponse() throws IOException {
    testResponse(newJsonFactory(), newBidResponse().build());
  }

  @Test
  public void testResponse_emptyMessages() throws IOException {
    OpenRtbJsonFactory jsonFactory = newJsonFactory();
    testResponse(jsonFactory, BidResponse.newBuilder().build());
    testResponse(jsonFactory, BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder()).build());
    testResponse(jsonFactory, BidResponse.newBuilder().addSeatbid(SeatBid.newBuilder()
        .addBid(Bid.newBuilder().setId("0").setImpid("0").setPrice(0))).build());
  }

  @Test(expected = JsonParseException.class)
  public void testBadArrayField() throws IOException, JsonParseException {
    String test = // based on Issue #10; sample message from SpotXchange with non-array "cat"
      "{\n \"id\": \"0\",\n \"imp\": [\n {\n \"id\": \"1\",\n \"banner\": "
    + "{\n \"h\": 250,\n \"w\": 300,\n \"pos\": 1\n },\n \"bidfloor\": 0.05\n }\n ],\n "
    + "\"site\": {\n \"id\": \"15047\",\n \"domain\": \"dailymotion.com\",\n \"cat\": \"IAB1\",\n "
    + "\"page\": \"http://www.dailymotion.com\",\n "
    + "\"publisher\": {\n \"id\": \"8796\",\n \"name\": \"dailymotion\",\n \"cat\": \"IAB3-1\",\n "
    + "\"domain\": \"dailymotion.com\"\n }\n },\n \"user\": {\n \"id\": \"0\"\n },\n "
    + "\"device\": {\n \"ua\": \"Mozilla/4.0\",\n "
    + "\"ip\": \"1.2.3.4\"\n },\n \"at\": 1,\n \"cur\": [\n \"USD\"\n ]\n}";
    newJsonFactory().newReader().readBidRequest(test);
  }

  @Test
  public void testNulls() throws IOException {
    String test = // based on issue #13
          "{ \"id\": \"0\",\n  \"app\": { \"content\": { "
        + "\"keywords\": null },\n \"id\": \"56600\",\n \"cat\": [\"IAB19\"],\n "
        + "\"keywords\": \"\",\n \"name\": \"Emoji Free!\",\n \"ver\": null\n } \n}";
    newJsonFactory().newReader().readBidRequest(test);
  }

  static void testRequest(OpenRtbJsonFactory jsonFactory, BidRequest req) throws IOException {
    String jsonReq = jsonFactory.newWriter().writeBidRequest(req);
    logger.info(jsonReq);
    BidRequest req2 = jsonFactory.newReader().readBidRequest(jsonReq);
    assertEquals(req, req2);
  }

  static void testResponse(OpenRtbJsonFactory jsonFactory, BidResponse resp) throws IOException {
    String jsonResp = jsonFactory.newWriter().writeBidResponse(resp);
    logger.info(jsonResp);
    OpenRtb.BidResponse resp2 = jsonFactory.newReader().readBidResponse(jsonResp);
    assertEquals(resp, resp2);
  }

  static OpenRtbJsonFactory newJsonFactory() {
    return OpenRtbJsonFactory.create()
        .setJsonFactory(new JsonFactory())
        // BidRequest Readers
        .register(new Test1Reader<BidRequest.Builder>(TestExt.testRequest1), "BidRequest")
        .register(new Test2Reader<BidRequest.Builder>(TestExt.testRequest2), "BidRequest")
        .register(new Test1Reader<App.Builder>(TestExt.testApp), "BidRequest.app")
        .register(new Test1Reader<Content.Builder>(TestExt.testContent), "BidRequest.app.content")
        .register(new Test1Reader<Producer.Builder>(TestExt.testProducer),
            "BidRequest.app.content.producer")
        .register(new Test1Reader<Publisher.Builder>(TestExt.testPublisher),
            "BidRequest.app.publisher")
        .register(new Test1Reader<Device.Builder>(TestExt.testDevice), "BidRequest.device")
        .register(new Test1Reader<Geo.Builder>(TestExt.testGeo),
            "BidRequest.device.geo", "BidRequest.user.geo")
        .register(new Test1Reader<Impression.Builder>(TestExt.testImp), "BidRequest.imp")
        .register(new Test1Reader<Banner.Builder>(TestExt.testBanner), "BidRequest.imp.banner")
        .register(new Test1Reader<Native.Builder>(TestExt.testNative), "BidRequest.imp.native")
        .register(new Test1Reader<PMP.Builder>(TestExt.testPmp), "BidRequest.imp.pmp")
        .register(new Test1Reader<Deal.Builder>(TestExt.testDeal), "BidRequest.imp.pmp.deals")
        .register(new Test1Reader<Video.Builder>(TestExt.testVideo), "BidRequest.imp.video")
        .register(new Test1Reader<Regulations.Builder>(TestExt.testRegs), "BidRequest.regs")
        .register(new Test1Reader<Site.Builder>(TestExt.testSite), "BidRequest.site")
        .register(new Test1Reader<User.Builder>(TestExt.testUser), "BidRequest.user")
        .register(new Test1Reader<Data.Builder>(TestExt.testData), "BidRequest.user.data")
        .register(new Test1Reader<Segment.Builder>(TestExt.testSegment),
            "BidRequest.user.data.segment")
        // BidResponse Readers
        .register(new Test1Reader<BidResponse.Builder>(TestExt.testResponse1), "BidResponse")
        .register(new Test2Reader<BidResponse.Builder>(TestExt.testResponse2), "BidResponse")
        .register(new Test1Reader<SeatBid.Builder>(TestExt.testSeat), "BidResponse.seatbid")
        .register(new Test1Reader<Bid.Builder>(TestExt.testBid), "BidResponse.seatbid.bid")
        // Writers
        .register(new Test1Writer(), Test1.class,
            "BidRequest", "BidRequest.app", "BidRequest.app.publisher",
            "BidRequest.app.content", "BidRequest.app.content.producer",
            "BidRequest.device", "BidRequest.device.geo",
            "BidRequest.user", "BidRequest.user.data",
            "BidRequest.user.data.segment", "BidRequest.user.geo",
            "BidRequest.imp", "BidRequest.imp.banner", "BidRequest.imp.video",
            "BidRequest.imp.native", "BidRequest.imp.pmp", "BidRequest.imp.pmp.deals",
            "BidRequest.regs", "BidRequest.site",
            "BidResponse", "BidResponse.seatbid", "BidResponse.seatbid.bid")
        .register(new Test2Writer(), Test2.class, "BidRequest", "BidResponse");
  }

  static BidRequest.Builder newBidRequest() {
    return BidRequest.newBuilder()
        .setId("3031323334353637")
        .addImp(Impression.newBuilder()
            .setId("imp1")
            .setBanner(Banner.newBuilder()
                .setWmax(300)
                .setWmin(200)
                .setHmax(100)
                .setHmin(50)
                .setId("banner1")
                .setPos(AdPosition.ABOVE_THE_FOLD)
                .addBtype(AdType.JAVASCRIPT)
                .addBattr(CreativeAttribute.TEXT_ONLY)
                .addMimes("image/gif")
                .setTopframe(true)
                .addExpdir(ExpandableDirection.RIGHT)
                .addApi(ApiFramework.MRAID_1)
                .setExtension(TestExt.testBanner, test1))
            .setDisplaymanager("dm1")
            .setDisplaymanagerver("1.0")
            .setInstl(false)
            .setTagid("tag1")
            .setBidfloor(100000000)
            .setBidfloorcur("USD")
            .setSecure(false)
            .addIframebuster("buster1")
            .setPmp(PMP.newBuilder()
                .setPrivateAuction(false)
                .addDeals(Deal.newBuilder()
                    .setId("deal1")
                    .setBidfloor(200000000)
                    .setBidfloorcur("USD")
                    .addWseat("seat2")
                    .addWadomain("goodadv1")
                    .setAt(2)
                    .setExtension(TestExt.testDeal, test1))
                .setExtension(TestExt.testPmp, test1))
            .setExtension(TestExt.testImp, test1))
        .addImp(Impression.newBuilder()
            .setId("imp2")
            .setVideo(Video.newBuilder()
                .addMimes("video/vp9")
                .setLinearity(Linearity.LINEAR)
                .setMinduration(15)
                .setMaxduration(60)
                .setDeprecatedProtocol(Protocol.VAST_3_0)
                .addProtocols(Protocol.VAST_2_0)
                .setW(200)
                .setH(50)
                .setStartdelay(0)
                .setSequence(1)
                .addBattr(CreativeAttribute.TEXT_ONLY)
                .setMaxextended(120)
                .setMinbitrate(1000)
                .setMaxbitrate(2000)
                .setBoxingallowed(false)
                .addPlaybackmethod(PlaybackMethod.CLICK_TO_PLAY)
                .addDelivery(ContentDelivery.STREAMING)
                .setPos(AdPosition.ABOVE_THE_FOLD)
                .addCompanionad(Banner.newBuilder()
                    .setId("compad1")
                    .setW(100)
                    .setH(50))
                .addApi(ApiFramework.VPAID_2_0)
                .addCompaniontype(CompanionType.HTML)
                .setExtension(TestExt.testVideo, test1)))
        .addImp(Impression.newBuilder()
            .setId("imp3")
            .setNative(Native.newBuilder()
                .setRequest(NativeRequest.newBuilder().setVer("1"))
                .setVer("1.0")
                .addApi(ApiFramework.MRAID_1)
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
        .setAt(2)
        .setTmax(100)
        .addWseat("seat1")
        .setAllimps(false)
        .addCur("USD")
        .addAllBcat(asList("IAB11", "IAB11-4"))
        .addBadv("badguy")
        .setRegs(Regulations.newBuilder()
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
            .setContext(Context.OTHER)
            .setLivestream(false)
            .setSourcerelationship(SourceRelationship.INDIRECT)
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

  static BidResponse.Builder newBidResponse() {
    return BidResponse.newBuilder()
        .setId("resp1")
        .addSeatbid(SeatBid.newBuilder()
            .addBid(Bid.newBuilder()
                .setId("bid1")
                .setImpid("imp1")
                .setPrice(19950000)
                .setAdid("adid1")
                .setNurl("http://iwon.com")
                .setAdm("<snippet/>")
                .addAdomain("http://myads.com")
                .setIurl("http://mycdn.com/ad.gif")
                .setCid("cid1")
                .setCrid("crid1")
                .addAttr(CreativeAttribute.TEXT_ONLY)
                .setDealid("deal1")
                .setW(100)
                .setH(80)
                .setBundle("com.google.testapp")
                .setCat(ContentCategory.IAB10_2.name())
                .setExtension(TestExt.testBid, test1))
            .setSeat("seat1")
            .setGroup(false)
            .setExtension(TestExt.testSeat, test1))
        .setBidid("bid1")
        .setCur("USD")
        .setCustomdata("mydata")
        .setNbr(NoBidReasonCode.TECHNICAL_ERROR)
        .setExtension(TestExt.testResponse1, test1)
        .setExtension(TestExt.testResponse2, test2);
  }
}
