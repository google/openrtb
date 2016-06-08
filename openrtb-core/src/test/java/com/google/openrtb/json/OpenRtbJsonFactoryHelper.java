package com.google.openrtb.json;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Data.Segment;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp.Deal;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regs;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.Test;
import com.google.openrtb.TestExt;

import com.fasterxml.jackson.core.JsonFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Test helper class, to be used for generating and comparing JSON test data.
 */
class OpenRtbJsonFactoryHelper {
  static final Test.Test1 test1 = Test.Test1.newBuilder().setTest1("data1").build();
  static final Test.Test2 test2 = Test.Test2.newBuilder().setTest2("data2").build();

  static OpenRtbJsonFactory newJsonFactory(boolean isRootNative, boolean isNativeObject) {
    return OpenRtbJsonFactory.create()
        .setRootNativeField(isRootNative)
        .setForceNativeAsObject(isNativeObject)
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
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.class)
        .register(new Test2Writer("test2ext"), Test.Test2.class, OpenRtb.BidRequest.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.App.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Device.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Site.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.User.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Geo.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Data.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Data.Segment.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Publisher.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Content.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Producer.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Imp.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Imp.Banner.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Imp.Video.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Imp.Native.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Imp.Pmp.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Imp.Pmp.Deal.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidRequest.Regs.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidResponse.SeatBid.class)
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidResponse.SeatBid.Bid.class)
        // BidResponse Writers
        .register(new Test1Writer(), Test.Test1.class, OpenRtb.BidResponse.class,
            "testResponse1")
        .register(new Test2Writer("test2arr"), Test.Test2.class, OpenRtb.BidResponse.class,
            "testResponse2")
        .register(new Test2Writer("test2a"), Test.Test2.class, OpenRtb.BidResponse.class,
            "testResponse2a")
        .register(new Test2Writer("test2b"), Test.Test2.class, OpenRtb.BidResponse.class,
            "testResponse2b")
        .register(new Test3Writer(), Integer.class, OpenRtb.BidResponse.class, "testResponse3")
        .register(new Test4Writer(), Integer.class, OpenRtb.BidResponse.class, "testResponse4");
  }

  static String readFile(String fileName) {
    try (InputStream is = OpenRtbJsonFactoryHelper.class.getResourceAsStream("/" + fileName);
        Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  static void writeFile(String fileName, String content) {
    try (OutputStream os = new FileOutputStream(fileName);
      PrintStream ps = new PrintStream(os, true, StandardCharsets.UTF_8.name())) {
      ps.print(content);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
