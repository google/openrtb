/*
 * Copyright 2016 Google Inc. All Rights Reserved.
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

import com.fasterxml.jackson.core.JsonFactory;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Audio;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regs;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.Source;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeResponse;
import com.google.openrtb.Test;
import com.google.openrtb.Test.Test1;
import com.google.openrtb.Test.Test2;
import com.google.openrtb.TestExt;
import com.google.openrtb.TestNExt;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utilities for JSON tests.
 */
class OpenRtbJsonFactoryHelper {
  static final Test.Test1 test1 = Test.Test1.newBuilder().setTest1("data1").build();
  static final Test.Test2 test2 = Test.Test2.newBuilder().setTest2("data2").build();

  static OpenRtbJsonFactory newJsonFactory() {
    return newJsonFactory(false, false);
  }

  static OpenRtbJsonFactory newJsonFactory(boolean isRootNative, boolean isNativeObject) {
    OpenRtbJsonFactory factory = OpenRtbJsonFactory.create()
        .setRootNativeField(isRootNative)
        .setForceNativeAsObject(isNativeObject)
        .setJsonFactory(new JsonFactory());
    registerBidRequestExt(factory);
    registerBidResponseExt(factory);
    registerNativeRequestExt(factory);
    registerNativeResponseExt(factory);
    return factory;
  }

  static OpenRtbJsonFactory registerBidRequestExt(OpenRtbJsonFactory factory) {
    return factory
        // Readers
        .register(new Test1Reader<>(TestExt.testRequest1), BidRequest.Builder.class)
        .register(new Test2Reader<>(TestExt.testRequest2, "test2ext"), BidRequest.Builder.class)
        .register(new Test1Reader<>(TestExt.testApp), App.Builder.class)
        .register(new Test1Reader<>(TestExt.testContent), Content.Builder.class)
        .register(new Test1Reader<>(TestExt.testProducer), Producer.Builder.class)
        .register(new Test1Reader<>(TestExt.testPublisher), Publisher.Builder.class)
        .register(new Test1Reader<>(TestExt.testDevice), Device.Builder.class)
        .register(new Test1Reader<>(TestExt.testGeo), Geo.Builder.class)
        .register(new Test1Reader<>(TestExt.testImp), Imp.Builder.class)
        .register(new Test1Reader<>(TestExt.testMetric), Imp.Metric.Builder.class)
        .register(new Test1Reader<>(TestExt.testBanner), Banner.Builder.class)
        .register(new Test1Reader<>(TestExt.testFormat), Banner.Format.Builder.class)
        .register(new Test1Reader<>(TestExt.testNative), Native.Builder.class)
        .register(new Test1Reader<>(TestExt.testPmp), Pmp.Builder.class)
        .register(new Test1Reader<>(TestExt.testDeal), Pmp.Deal.Builder.class)
        .register(new Test1Reader<>(TestExt.testVideo), Video.Builder.class)
        .register(new Test1Reader<>(TestExt.testCompanionAd), Video.CompanionAd.Builder.class)
        .register(new Test1Reader<>(TestExt.testAudio), Audio.Builder.class)
        .register(new Test1Reader<>(TestExt.testRegs), Regs.Builder.class)
        .register(new Test1Reader<>(TestExt.testSite), Site.Builder.class)
        .register(new Test1Reader<>(TestExt.testSource), Source.Builder.class)
        .register(new Test1Reader<>(TestExt.testUser), User.Builder.class)
        .register(new Test1Reader<>(TestExt.testData), Data.Builder.class)
        .register(new Test1Reader<>(TestExt.testSegment), Data.Segment.Builder.class)
        // Writers
        .register(new Test1Writer(), Test.Test1.class, BidRequest.class)
        .register(new Test2Writer("test2ext"), Test.Test2.class, BidRequest.class)
        .register(new Test1Writer(), Test.Test1.class, App.class)
        .register(new Test1Writer(), Test.Test1.class, Device.class)
        .register(new Test1Writer(), Test.Test1.class, Site.class)
        .register(new Test1Writer(), Test.Test1.class, User.class)
        .register(new Test1Writer(), Test.Test1.class, Geo.class)
        .register(new Test1Writer(), Test.Test1.class, Data.class)
        .register(new Test1Writer(), Test.Test1.class, Data.Segment.class)
        .register(new Test1Writer(), Test.Test1.class, Publisher.class)
        .register(new Test1Writer(), Test.Test1.class, Content.class)
        .register(new Test1Writer(), Test.Test1.class, Producer.class)
        .register(new Test1Writer(), Test.Test1.class, Imp.class)
        .register(new Test1Writer(), Test.Test1.class, Imp.Metric.class)
        .register(new Test1Writer(), Test.Test1.class, Banner.class)
        .register(new Test1Writer(), Test.Test1.class, Banner.Format.class)
        .register(new Test1Writer(), Test.Test1.class, Video.class)
        .register(new Test1Writer(), Test.Test1.class, Video.CompanionAd.class)
        .register(new Test1Writer(), Test.Test1.class, Audio.class)
        .register(new Test1Writer(), Test.Test1.class, Native.class)
        .register(new Test1Writer(), Test.Test1.class, Pmp.class)
        .register(new Test1Writer(), Test.Test1.class, Pmp.Deal.class)
        .register(new Test1Writer(), Test.Test1.class, Regs.class)
        .register(new Test1Writer(), Test.Test1.class, Source.class);
  }

  static OpenRtbJsonFactory registerBidResponseExt(OpenRtbJsonFactory factory) {
    return factory
        // Readers
        .register(new Test1Reader<>(TestExt.testResponse1), BidResponse.Builder.class)
        .register(new Test2Reader<>(TestExt.testResponse2, "test2arr"), BidResponse.Builder.class)
        .register(new Test2Reader<>(TestExt.testResponse2A, "test2a"), BidResponse.Builder.class)
        .register(new Test2Reader<>(TestExt.testResponse2B, "test2b"), BidResponse.Builder.class)
        .register(new Test3Reader(), BidResponse.Builder.class)
        .register(new Test4Reader(), BidResponse.Builder.class)
        .register(new Test1Reader<>(TestExt.testSeat), SeatBid.Builder.class)
        .register(new Test1Reader<>(TestExt.testBid), SeatBid.Bid.Builder.class)
        // Writers
        .register(new Test1Writer(), Test.Test1.class, BidResponse.class, "testResponse1")
        .register(new Test2Writer("test2arr"), Test.Test2.class, BidResponse.class, "testResponse2")
        .register(new Test2Writer("test2a"), Test.Test2.class, BidResponse.class, "testResponse2a")
        .register(new Test2Writer("test2b"), Test.Test2.class, BidResponse.class, "testResponse2b")
        .register(new Test3Writer(), Integer.class, BidResponse.class, "testResponse3")
        .register(new Test4Writer(), Integer.class, BidResponse.class, "testResponse4")
        .register(new Test1Writer(), Test.Test1.class, SeatBid.class)
        .register(new Test1Writer(), Test.Test1.class, SeatBid.Bid.class);
  }

  static OpenRtbJsonFactory registerNativeRequestExt(OpenRtbJsonFactory factory) {
    return factory
        // Readers
        .register(new Test1Reader<NativeRequest.Builder>(TestNExt.testNRequest1),
            NativeRequest.Builder.class)
        .register(new Test2Reader<NativeRequest.Builder>(TestNExt.testNRequest2, "test2ext"),
            NativeRequest.Builder.class)
        .register(new Test1Reader<NativeRequest.Asset.Builder>(TestNExt.testNReqAsset),
            NativeRequest.Asset.Builder.class)
        .register(new Test1Reader<NativeRequest.Asset.Title.Builder>(TestNExt.testNReqTitle),
            NativeRequest.Asset.Title.Builder.class)
        .register(new Test1Reader<NativeRequest.Asset.Image.Builder>(TestNExt.testNReqImage),
            NativeRequest.Asset.Image.Builder.class)
        .register(new Test1Reader<Video.Builder>(TestExt.testVideo), Video.Builder.class)
        .register(new Test1Reader<NativeRequest.Asset.Data.Builder>(TestNExt.testNReqData),
            NativeRequest.Asset.Data.Builder.class)
        .register(new Test2Writer("test2ext"), Test2.class, NativeRequest.class)
        // Writers
        .register(new Test1Writer(), Test1.class, NativeRequest.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.Title.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.Image.class)
        .register(new Test1Writer(), Test1.class, Video.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.Data.class);
  }

  static OpenRtbJsonFactory registerNativeResponseExt(OpenRtbJsonFactory factory) {
    return factory
        // Readers
        .register(new Test1Reader<NativeResponse.Builder>(TestNExt.testNResponse1),
            NativeResponse.Builder.class)
        .register(new Test2Reader<NativeResponse.Builder>(TestNExt.testNResponse2, "test2ext"),
            NativeResponse.Builder.class)
        .register(new Test1Reader<NativeResponse.Link.Builder>(TestNExt.testNRespLink),
            NativeResponse.Link.Builder.class)
        .register(new Test1Reader<NativeResponse.Asset.Builder>(TestNExt.testNRespAsset),
            NativeResponse.Asset.Builder.class)
        .register(new Test1Reader<NativeResponse.Asset.Title.Builder>(TestNExt.testNRespTitle),
            NativeResponse.Asset.Title.Builder.class)
        .register(new Test1Reader<NativeResponse.Asset.Image.Builder>(TestNExt.testNRespImage),
            NativeResponse.Asset.Image.Builder.class)
        .register(new Test1Reader<NativeResponse.Asset.Video.Builder>(TestNExt.testNRespVideo),
            NativeResponse.Asset.Video.Builder.class)
        .register(new Test1Reader<NativeResponse.Asset.Data.Builder>(TestNExt.testNRespData),
            NativeResponse.Asset.Data.Builder.class)
        // Writers
        .register(new Test1Writer(), Test1.class, NativeResponse.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Link.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Title.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Image.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Video.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Data.class)
        .register(new Test2Writer("test2ext"), Test2.class, NativeResponse.class);

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
