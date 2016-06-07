package com.google.openrtb.json;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.Test;
import com.google.openrtb.TestExt;

import com.fasterxml.jackson.core.JsonFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Test helper class, to be used for generating and comparing Json test data <p> Created by
 * bundeskanzler4711 on 12/05/16.
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
        .register(new Test1Reader<>(TestExt.testRequest1), OpenRtb.BidRequest.Builder.class)
        .register(new Test2Reader<>(TestExt.testRequest2, "test2ext"),
            OpenRtb.BidRequest.Builder.class)
        .register(new Test1Reader<>(TestExt.testApp), OpenRtb.BidRequest.App.Builder.class)
        .register(new Test1Reader<>(TestExt.testContent),
            OpenRtb.BidRequest.Content.Builder.class)
        .register(new Test1Reader<>(TestExt.testProducer),
            OpenRtb.BidRequest.Producer.Builder.class)
        .register(new Test1Reader<>(TestExt.testPublisher),
            OpenRtb.BidRequest.Publisher.Builder.class)
        .register(new Test1Reader<>(TestExt.testDevice), OpenRtb.BidRequest.Device.Builder.class)
        .register(new Test1Reader<>(TestExt.testGeo), OpenRtb.BidRequest.Geo.Builder.class)
        .register(new Test1Reader<>(TestExt.testImp), OpenRtb.BidRequest.Imp.Builder.class)
        .register(new Test1Reader<>(TestExt.testBanner),
            OpenRtb.BidRequest.Imp.Banner.Builder.class)
        .register(new Test1Reader<>(TestExt.testNative),
            OpenRtb.BidRequest.Imp.Native.Builder.class)
        .register(new Test1Reader<>(TestExt.testPmp), OpenRtb.BidRequest.Imp.Pmp.Builder.class)
        .register(new Test1Reader<>(TestExt.testDeal),
            OpenRtb.BidRequest.Imp.Pmp.Deal.Builder.class)
        .register(new Test1Reader<>(TestExt.testVideo),
            OpenRtb.BidRequest.Imp.Video.Builder.class)
        .register(new Test1Reader<>(TestExt.testRegs), OpenRtb.BidRequest.Regs.Builder.class)
        .register(new Test1Reader<>(TestExt.testSite), OpenRtb.BidRequest.Site.Builder.class)
        .register(new Test1Reader<>(TestExt.testUser), OpenRtb.BidRequest.User.Builder.class)
        .register(new Test1Reader<>(TestExt.testData), OpenRtb.BidRequest.Data.Builder.class)
        .register(new Test1Reader<>(TestExt.testSegment),
            OpenRtb.BidRequest.Data.Segment.Builder.class)
        // BidResponse Readers
        .register(new Test1Reader<>(TestExt.testResponse1), OpenRtb.BidResponse.Builder.class)
        .register(new Test2Reader<>(TestExt.testResponse2, "test2arr"),
            OpenRtb.BidResponse.Builder.class)
        .register(new Test2Reader<>(TestExt.testResponse2A, "test2a"),
            OpenRtb.BidResponse.Builder.class)
        .register(new Test2Reader<>(TestExt.testResponse2B, "test2b"),
            OpenRtb.BidResponse.Builder.class)
        .register(new Test3Reader(), OpenRtb.BidResponse.Builder.class)
        .register(new Test4Reader(), OpenRtb.BidResponse.Builder.class)
        .register(new Test1Reader<>(TestExt.testSeat), OpenRtb.BidResponse.SeatBid.Builder.class)
        .register(new Test1Reader<>(TestExt.testBid),
            OpenRtb.BidResponse.SeatBid.Bid.Builder.class)
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
    InputStream inputStream = OpenRtbJsonFactoryHelper.class.getClassLoader().getResourceAsStream(fileName);
    Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
    return scanner.hasNext() ? scanner.next() : "";
  }

  static void writeFile(String fileName, String content) {
    final OutputStream outputStream;
    try {
      outputStream = new FileOutputStream(fileName);
      final PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());
      printStream.print(content);
      printStream.close();
    } catch (FileNotFoundException | UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
  }
}
