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

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.VideoBidResponseProtocol;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeResponse;
import com.google.openrtb.Test.Test1;
import com.google.openrtb.Test.Test2;
import com.google.openrtb.TestExt;
import com.google.openrtb.TestNExt;

import com.fasterxml.jackson.core.JsonFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tests for {@link OpenRtbJsonFactory},
 * {@link OpenRtbNativeJsonReader}, {@link OpenRtbNativeJsonWriter}.
 */
public class OpenRtbNativeJsonTest {
  private static final Logger logger = LoggerFactory.getLogger(OpenRtbNativeJsonTest.class);
  private static final Test1 test1 = Test1.newBuilder().setTest1("test1").build();
  private static final Test2 test2 = Test2.newBuilder().setTest2("test2").build();

  @Test
  public void testRequest() throws IOException {
    testRequest(newJsonFactory(), newNativeRequest().build());
  }

  @Test
  public void testRequest_emptyMessage() throws IOException {
    testRequest(newJsonFactory(), NativeRequest.newBuilder()
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(1)
            .setTitle(NativeRequest.Asset.Title.newBuilder()
                .setLen(100)))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(2)
            .setImg(NativeRequest.Asset.Image.newBuilder()))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(3)
            .setVideo(BidRequest.Imp.Video.newBuilder()
                .setMinduration(100)
                .setMaxduration(200)))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(4)
            .setData(NativeRequest.Asset.Data.newBuilder()
                .setType(NativeRequest.Asset.Data.DataAssetType.SPONSORED)))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(5))
        .build());
    testRequest(newJsonFactory(), NativeRequest.newBuilder().build());
  }

  @Test
  public void testResponse() throws IOException {
    testResponse(newJsonFactory(), newNativeResponse().build());
  }

  @Test
  public void testResponse_emptyMessage() throws IOException {
    testResponse(newJsonFactory(), NativeResponse.newBuilder()
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(1)
            .setRequired(true)
            .setTitle(NativeResponse.Asset.Title.newBuilder()
                .setText("title")))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(2)
            .setImg(NativeResponse.Asset.Image.newBuilder()))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(3)
            .setVideo(NativeResponse.Asset.Video.newBuilder()))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(4)
            .setData(NativeResponse.Asset.Data.newBuilder()
                .setValue("v"))
            .setLink(NativeResponse.Link.newBuilder()))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(5))
        .setLink(NativeResponse.Link.newBuilder())
        .build());
    testResponse(newJsonFactory(), NativeResponse.newBuilder()
        .setLink(NativeResponse.Link.newBuilder())
        .build());
  }

  @Test
  public void testRequest_emptyToNull() throws IOException {
    OpenRtbNativeJsonReader reader = OpenRtbJsonFactory.create().setStrict(false).newNativeReader();
    assertThat(reader.readNativeRequest("")).isNull();
    assertThat(reader.readNativeResponse("")).isNull();
  }

  static void testRequest(OpenRtbJsonFactory jsonFactory, NativeRequest req) throws IOException {
    String jsonReq = jsonFactory.newNativeWriter().writeNativeRequest(req);
    logger.info(jsonReq);
    NativeRequest req2 = jsonFactory.newNativeReader().readNativeRequest(jsonReq);
    assertThat(req2).isEqualTo(req);
  }

  static void testResponse(OpenRtbJsonFactory jsonFactory, NativeResponse resp) throws IOException {
    String jsonResp = jsonFactory.newNativeWriter().writeNativeResponse(resp);
    logger.info(jsonResp);
    NativeResponse resp2 = jsonFactory.newNativeReader().readNativeResponse(jsonResp);
    assertThat(resp2).isEqualTo(resp);
  }

  static OpenRtbJsonFactory newJsonFactory() {
    return OpenRtbJsonFactory.create()
        .setJsonFactory(new JsonFactory())
        // NativeRequest Readers
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
        .register(new Test1Reader<BidRequest.Imp.Video.Builder>(TestExt.testVideo),
            BidRequest.Imp.Video.Builder.class)
        .register(new Test1Reader<NativeRequest.Asset.Data.Builder>(TestNExt.testNReqData),
            NativeRequest.Asset.Data.Builder.class)
        // NativeResponse Readers
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
        .register(new Test1Writer(), Test1.class, NativeRequest.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.Title.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.Image.class)
        .register(new Test1Writer(), Test1.class, BidRequest.Imp.Video.class)
        .register(new Test1Writer(), Test1.class, NativeRequest.Asset.Data.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Link.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Title.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Image.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Video.class)
        .register(new Test1Writer(), Test1.class, NativeResponse.Asset.Data.class)
        .register(new Test2Writer("test2ext"), Test2.class, NativeRequest.class)
        .register(new Test2Writer("test2ext"), Test2.class, NativeResponse.class);
  }

  static NativeRequest.Builder newNativeRequest() {
    return NativeRequest.newBuilder()
        .setVer("1")
        .setLayout(NativeRequest.LayoutId.APP_WALL)
        .setAdunit(NativeRequest.AdUnitId.PROMOTED_LISTING)
        .setPlcmtcnt(4)
        .setSeq(5)
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(1)
            .setRequired(true)
            .setTitle(NativeRequest.Asset.Title.newBuilder()
                .setLen(100)
                .setExtension(TestNExt.testNReqTitle, test1))
            .setExtension(TestNExt.testNReqAsset, test1))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(2)
            .setImg(NativeRequest.Asset.Image.newBuilder()
                .setType(NativeRequest.Asset.Image.ImageAssetType.ICON)
                .setW(2)
                .setWmin(2)
                .setH(3)
                .setHmin(4)
                .addAllMimes(asList("a", "b"))
                .setExtension(TestNExt.testNReqImage, test1)))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(3)
            .setVideo(BidRequest.Imp.Video.newBuilder()
                .addAllMimes(asList("a", "b"))
                .setMinduration(100)
                .setMaxduration(200)
                .addProtocols(VideoBidResponseProtocol.VAST_3_0)
                .setExtension(TestExt.testVideo, test1)))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(4)
            .setData(NativeRequest.Asset.Data.newBuilder()
                .setType(NativeRequest.Asset.Data.DataAssetType.SPONSORED)
                .setLen(10)
                .setExtension(TestNExt.testNReqData, test1)))
        .setExtension(TestNExt.testNRequest1, test1)
        .setExtension(TestNExt.testNRequest2, test2);
  }

  static NativeResponse.Builder newNativeResponse() {
    return NativeResponse.newBuilder()
        .setVer("1")
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(1)
            .setRequired(true)
            .setTitle(NativeResponse.Asset.Title.newBuilder()
                .setText("title")
                .setExtension(TestNExt.testNRespTitle, test1))
            .setLink(NativeResponse.Link.newBuilder()
                .setUrl("url")
                .addAllClicktrackers(asList("a", "b"))
                .setFallback("f")
                .setExtension(TestNExt.testNRespLink, test1))
            .setExtension(TestNExt.testNRespAsset, test1))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(2)
            .setImg(NativeResponse.Asset.Image.newBuilder()
                .setUrl("url")
                .setW(2)
                .setH(3)
                .setExtension(TestNExt.testNRespImage, test1)))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(2)
            .setVideo(NativeResponse.Asset.Video.newBuilder()
                .addAllVasttag(asList("a", "b"))
                .setExtension(TestNExt.testNRespVideo, test1)))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(2)
            .setData(NativeResponse.Asset.Data.newBuilder()
                .setLabel("l")
                .setValue("v")
                .setExtension(TestNExt.testNRespData, test1)))
        .setLink(NativeResponse.Link.newBuilder()
            .setUrl("url")
            .addAllClicktrackers(asList("a", "b"))
            .setFallback("f")
            .setExtension(TestNExt.testNRespLink, test1))
        .addAllImptrackers(asList("a"))
        .setJstracker("b")
    .setExtension(TestNExt.testNResponse1, test1)
    .setExtension(TestNExt.testNResponse2, test2);
  }
}
