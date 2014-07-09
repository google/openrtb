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

import static com.google.openrtb.json.OpenRtbJsonUtils.getCurrentName;
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
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP.DirectDeal;
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
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.CreativeAttribute;
import com.google.openrtb.OpenRtb.Flag;
import com.google.openrtb.TestExt;
import com.google.openrtb.TestExt.Test1;
import com.google.openrtb.TestExt.Test2;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tests for {@link OpenRtbJsonWriter}.
 */
public class OpenRtbJsonTest {
  private static final Logger logger = LoggerFactory.getLogger(OpenRtbJsonTest.class);
  private static final Test1 test1 = TestExt.Test1.newBuilder().setTest1("test1").build();
  private static final Test2 test2 = TestExt.Test2.newBuilder().setTest2("test2").build();

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
        .register(new OpenRtbJsonExtWriter<TestExt.Test1>() {
          @Override public void write(TestExt.Test1 ext, JsonGenerator gen) throws IOException {
            gen.writeStringField("unknownField", "junk");
          }
        }, TestExt.Test1.class, "BidRequest"),
        newBidRequest().build());
  }

  @Test
  public void testRequest_AlternateFields() throws IOException {
    testRequest(newJsonFactory()
        .register(new OpenRtbJsonExtWriter<TestExt.Test1>() {
          @Override public void write(TestExt.Test1 ext, JsonGenerator gen) throws IOException {
            gen.writeStringField("test1", "test1");
            gen.writeStringField("test2", "test2");
            gen.writeStringField("test1", "test1");
            gen.writeStringField("test2", "test2");
          }
        }, TestExt.Test1.class, ""),
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
                .setLinearity(Linearity.LINEAR).setProtocol(Protocol.VAST_3_0)
                .setMinduration(0).setMaxduration(1))
            .setPmp(PMP.newBuilder().addDeals(DirectDeal.newBuilder().setId("0"))))
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

  static void testRequest(OpenRtbJsonFactory jsonFactory, BidRequest req) throws IOException {
    ByteString jsonReq = jsonFactory.newWriter().writeBidRequest(req);
    logger.info(jsonReq.toStringUtf8());
    BidRequest req2 = jsonFactory.newReader().readBidRequest(jsonReq);
    assertEquals(req, req2);
  }

  static void testResponse(OpenRtbJsonFactory jsonFactory, BidResponse resp) throws IOException {
    ByteString jsonResp = jsonFactory.newWriter().writeBidResponse(resp);
    logger.info(jsonResp.toStringUtf8());
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
        .register(new Test1Reader<PMP.Builder>(TestExt.testPmp), "BidRequest.imp.pmp")
        .register(new Test1Reader<DirectDeal.Builder>(TestExt.testDeal), "BidRequest.imp.pmp.deals")
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
        .register(new Test1Writer(), TestExt.Test1.class,
            "BidRequest", "BidRequest.app", "BidRequest.app.content",
            "BidRequest.app.content.producer", "BidRequest.app.publisher",
            "BidRequest.device", "BidRequest.device.geo", "BidRequest.imp", "BidRequest.imp.banner",
            "BidRequest.imp.pmp", "BidRequest.imp.pmp.deals", "BidRequest.imp.video",
            "BidRequest.regs", "BidRequest.site", "BidRequest.user", "BidRequest.user.data",
            "BidRequest.user.data.segment", "BidRequest.user.geo",
            "BidResponse", "BidResponse.seatbid", "BidResponse.seatbid.bid")
        .register(new Test2Writer(), TestExt.Test2.class, "BidRequest", "BidResponse");
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
                .setTopframe(Flag.YES)
                .addExpdir(ExpandableDirection.RIGHT)
                .addApi(ApiFramework.MRAID)
                .setExtension(TestExt.testBanner, test1))
            .setDisplaymanager("dm1")
            .setDisplaymanagerver("1.0")
            .setInstl(Flag.NO)
            .setTagid("tag1")
            .setBidfloor(100.0f)
            .setBidfloorcur("USD")
            .addIframebuster("buster1")
            .setPmp(PMP.newBuilder()
                .setPrivateAuction(Flag.NO)
                .addDeals(DirectDeal.newBuilder()
                    .setId("deal1")
                    .setBidfloor(200.0f)
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
                .setProtocol(Protocol.VAST_3_0)
                .setW(200)
                .setH(50)
                .setStartdelay(0)
                .setSequence(1)
                .addBattr(CreativeAttribute.TEXT_ONLY)
                .setMaxextended(120)
                .setMinbitrate(1000)
                .setMaxbitrate(2000)
                .setBoxingallowed(Flag.NO)
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
        .setDevice(Device.newBuilder()
            .setDnt(Flag.NO)
            .setUa("Chrome")
            .setIp("192.168.1.0")
            .setGeo(Geo.newBuilder()
                .setLat(90.0f)
                .setLon(45.0f)
                .setCountry("USA")
                .setRegion("New York")
                .setRegionfips104("US36")
                .setMetro("New York")
                .setCity("New York City")
                .setZip("10000")
                .setType(LocationType.GPS_LOCATION)
                .setExtension(TestExt.testGeo, test1))
            .setDidsha1("1234")
            .setDidmd5("4321")
            .setDpidsha1("5678")
            .setDpidmd5("8765")
            .setIpv6("1:2:3:4:5:6:0:0")
            .setCarrier("77777")
            .setLanguage("en")
            .setMake("Motorola")
            .setModel("MotoX")
            .setOs("Android")
            .setOsv("3.2.1")
            .setJs(Flag.YES)
            .setConnectiontype(ConnectionType.CELL_4G)
            .setDevicetype(DeviceType.MOBILE)
            .setFlashver("11")
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
        .setAllimps(Flag.NO)
        .addCur("USD")
        .addAllBcat(asList("IAB11", "IAB11-4"))
        .addBadv("badguy")
        .setRegs(Regulations.newBuilder()
            .setCoppa(Flag.YES)
            .setExtension(TestExt.testRegs, test1))
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
        .setPrivacypolicy(Flag.YES)
        .setRef("http://referrer.com")
        .setSearch("http://google.com?q=elections")
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
            .setLivestream(Flag.NO)
            .setSourcerelationship(SourceRelationship.INDIRECT)
            .setProducer(Producer.newBuilder()
                .setId("prod1")
                .setName("Warner")
                .addCat("IAB10")
                .setDomain("http://bros.com")
                .setExtension(TestExt.testProducer, test1))
            .setLen(240)
            .setQagmediarating(QAGMediaRating.MATURE)
            .setEmbeddable(Flag.NO)
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
        .setPrivacypolicy(Flag.YES)
        .setPaid(Flag.NO)
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
                .setPrice(19.95f)
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
                .setExtension(TestExt.testBid, test1))
            .setSeat("seat1")
            .setGroup(Flag.NO)
            .setExtension(TestExt.testSeat, test1))
        .setBidid("bid1")
        .setCur("USD")
        .setCustomdata("mydata")
        .setExtension(TestExt.testResponse1, test1)
        .setExtension(TestExt.testResponse2, test2);
  }

  static class Test1Reader<EB extends ExtendableBuilder<?, EB>>
  extends OpenRtbJsonExtReaderBase<EB, TestExt.Test1.Builder> {
    public Test1Reader(GeneratedExtension<?, ?> key) {
      super(key, TestExt.Test1.newBuilder());
    }

    @Override public boolean read(EB msg, TestExt.Test1.Builder ext, JsonParser par)
        throws IOException {
      switch (getCurrentName(par)) {
        case "test1":
          ext.setTest1(par.nextTextValue());
          return true;
        default:
          return false;
      }
    }
  }

  static class Test1Writer implements OpenRtbJsonExtWriter<TestExt.Test1> {
    @Override public void write(TestExt.Test1 ext, JsonGenerator gen) throws IOException {
      gen.writeStringField("test1", ext.getTest1());
    }
  }

  static class Test2Reader<EB extends ExtendableBuilder<?, EB>>
  extends OpenRtbJsonExtReaderBase<EB, TestExt.Test2.Builder> {
    public Test2Reader(GeneratedExtension<?, ?> key) {
      super(key, TestExt.Test2.newBuilder());
    }

    @Override public boolean read(EB msg, TestExt.Test2.Builder ext, JsonParser par)
        throws IOException {
      switch (getCurrentName(par)) {
        case "test2":
          ext.setTest2(par.nextTextValue());
          return true;
        default:
          return false;
      }
    }
  }

  static class Test2Writer implements OpenRtbJsonExtWriter<TestExt.Test2> {
    @Override public void write(TestExt.Test2 ext, JsonGenerator gen) throws IOException {
      gen.writeStringField("test2", ext.getTest2());
    }
  }
}
