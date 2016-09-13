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
import static com.google.openrtb.json.OpenRtbJsonFactoryHelper.newJsonFactory;
import static java.util.Arrays.asList;

import com.google.openrtb.OpenRtb.AdUnitId;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.ContextSubtype;
import com.google.openrtb.OpenRtb.ContextType;
import com.google.openrtb.OpenRtb.DataAssetType;
import com.google.openrtb.OpenRtb.ImageAssetType;
import com.google.openrtb.OpenRtb.LayoutId;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeResponse;
import com.google.openrtb.OpenRtb.PlacementType;
import com.google.openrtb.OpenRtb.Protocol;
import com.google.openrtb.Test.Test1;
import com.google.openrtb.Test.Test2;
import com.google.openrtb.TestExt;
import com.google.openrtb.TestNExt;
import java.io.IOException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            .setTitle(NativeRequest.Asset.Title.newBuilder().setLen(100)))
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
            .setData(NativeRequest.Asset.Data.newBuilder().setType(DataAssetType.SPONSORED)))
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
            .setTitle(NativeResponse.Asset.Title.newBuilder().setText("title")))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(2)
            .setImg(NativeResponse.Asset.Image.newBuilder().setUrl("http://image.gif")))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(3)
            .setVideo(NativeResponse.Asset.Video.newBuilder().setVasttag("http://vast.xml")))
        .addAssets(NativeResponse.Asset.newBuilder()
            .setId(4)
            .setData(NativeResponse.Asset.Data.newBuilder().setValue("v"))
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

  static NativeRequest.Builder newNativeRequest() {
    return NativeRequest.newBuilder()
        .setVer("1")
        .setLayout(LayoutId.APP_WALL)
        .setAdunit(AdUnitId.PROMOTED_LISTING)
        .setPlcmtcnt(4)
        .setSeq(5)
        .setContext(ContextType.PRODUCT)
        .setContextsubtype(ContextSubtype.CONTENT_AUDIO)
        .setPlcmttype(PlacementType.IN_FEED)
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
                .setType(ImageAssetType.ICON)
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
                .addProtocols(Protocol.VAST_3_0)
                .setExtension(TestExt.testVideo, test1)))
        .addAssets(NativeRequest.Asset.newBuilder()
            .setId(4)
            .setData(NativeRequest.Asset.Data.newBuilder()
                .setType(DataAssetType.SPONSORED)
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
                .setVasttag("vast")
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
