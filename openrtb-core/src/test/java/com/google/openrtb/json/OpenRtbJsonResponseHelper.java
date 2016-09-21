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

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.TestExt;
import java.io.IOException;

/**
 * Test helper class, to be used for generating and comparing JSON test data.
 */
class OpenRtbJsonResponseHelper {
  /**
   * Response JSON containing: native part as adm string field.
   */
  static final String RESPONSE_SHORT_NOROOT_STRING =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_NOROOT_STRING.json");

  /**
   * Response JSON containing: native part as adm_native object.
   */
  static final String RESPONSE_SHORT_NOROOT_OBJECT =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_NOROOT_OBJECT.json");

  /**
   * Response JSON containing: native part as adm string field; root native. enabled
   */
  static final String RESPONSE_SHORT_ROOT___STRING =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_ROOT___STRING.json");

  /**
   * Response JSON containing: native part as adm_native object; root native. enabled
   */
  static final String RESPONSE_SHORT_ROOT___OBJECT =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_ROOT___OBJECT.json");

  /**
   * Response JSON containing: native part as adm string field; nearly all possible fields filled.
   */
  static final String RESPONSE_FULL__NOROOT_STRING =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__NOROOT_STRING.json");

  /**
   * Response JSON containing: native part as adm_native object; nearly all possible fields filled.
   */
  static final String RESPONSE_FULL__NOROOT_OBJECT =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__NOROOT_OBJECT.json");

  /**
   * Response JSON containing: native part as adm string field; root native enabled; nearly all
   * possible fields filled.
   */
  static final String RESPONSE_FULL__ROOT___STRING =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__ROOT___STRING.json");

  /**
   * Response JSON containing: native part as adm_native object; root native enabled; nearly all
   * possible fields filled.
   */
  static final String RESPONSE_FULL__ROOT___OBJECT =
      OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__ROOT___OBJECT.json");

  public static void testJsonGeneratedFiles() throws IOException {
    assertThat(generateJson(false, false, false)).isEqualTo(RESPONSE_SHORT_NOROOT_STRING);
    assertThat(generateJson(false, false, true)).isEqualTo(RESPONSE_SHORT_NOROOT_OBJECT);
    assertThat(generateJson(false, true, false)).isEqualTo(RESPONSE_SHORT_ROOT___STRING);
    assertThat(generateJson(false, true, true)).isEqualTo(RESPONSE_SHORT_ROOT___OBJECT);
    assertThat(generateJson(true, false, false)).isEqualTo(RESPONSE_FULL__NOROOT_STRING);
    assertThat(generateJson(true, false, true)).isEqualTo(RESPONSE_FULL__NOROOT_OBJECT);
    assertThat(generateJson(true, true, false)).isEqualTo(RESPONSE_FULL__ROOT___STRING);
    assertThat(generateJson(true, true, true)).isEqualTo(RESPONSE_FULL__ROOT___OBJECT);
  }

  public static void main(String[] args) throws IOException {
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_SHORT_NOROOT_STRING.json",
        generateJson(false, false, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_SHORT_NOROOT_OBJECT.json",
        generateJson(false, false, true));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_SHORT_ROOT___STRING.json",
        generateJson(false, true, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_SHORT_ROOT___OBJECT.json",
        generateJson(false, true, true));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_FULL__NOROOT_STRING.json",
        generateJson(true, false, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_FULL__NOROOT_OBJECT.json",
        generateJson(true, false, true));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_FULL__ROOT___STRING.json",
        generateJson(true, true, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/RESPONSE_FULL__ROOT___OBJECT.json",
        generateJson(true, true, true));
  }

  /**
   * Json generator method.
   *
   * @param isFull true, if nearly all fields should be filled; just some selected fields otherwise
   * @param isRootNative true, if the "native" field should be included as root element
   * @param isNativeObject true, if the native part should be generated as Json object;
   *     String otherwise
   * @return not pretty printed String representation of Json
   */
  private static String generateJson(boolean isFull, boolean isRootNative, boolean isNativeObject)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Object json = mapper.readValue(
        generateResponse(isFull, isRootNative, isNativeObject), Object.class);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  private static String generateResponse(
      boolean isFull, boolean isRootNative, boolean isNativeObject) throws IOException {
    return isFull
        ? generateFullResponse(isRootNative, isNativeObject)
        : generateShortResponse(isRootNative, isNativeObject);
  }

  private static String generateShortResponse(boolean isRootNative, boolean isNativeObject)
      throws IOException {
    OpenRtbJsonFactory jsonFactory =
        OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    OpenRtb.BidResponse.SeatBid.Bid.Builder seatBid = OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
        .setId("bid")
        .setImpid("imp")
        .setPrice(19.95)
        .setAdid("adid")
        .setNurl("http://iwon.com")
        .addAdomain("http://myads.com")
        .setIurl("http://mycdn.com/ad.gif")
        .setCid("cid")
        .setCrid("crid")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .setDealid("deal")
        .setW(100)
        .setH(80)
        .setBundle("com.google.testapp")
        .addCat("IAB10-2")
        .setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.NativeResponse.Builder nativeResponse = OpenRtb.NativeResponse.newBuilder()
        .setVer("1.0")
        .setLink(OpenRtb.NativeResponse.Link.newBuilder())
        .addImptrackers("http://my.imp.tracker");

    if (isNativeObject) {
      seatBid.setAdmNative(nativeResponse);
    } else {
      seatBid.setAdm(jsonFactory.newNativeWriter().writeNativeResponse(nativeResponse.build()));
    }

    OpenRtb.BidResponse.Builder bidResponse = OpenRtb.BidResponse.newBuilder()
        .setId("resp")
        .addSeatbid(OpenRtb.BidResponse.SeatBid.newBuilder()
            .addBid(seatBid)
            .setSeat("seat"))
        .setBidid("bid")
        .setCur("USD")
        .setCustomdata("mydata")
        .setNbr(OpenRtb.NoBidReason.TECHNICAL_ERROR);

    return jsonFactory.newWriter().writeBidResponse(bidResponse.build());
  }

  private static String generateFullResponse(boolean isRootNative, boolean isNativeObject)
      throws IOException {
    OpenRtbJsonFactory JsonFactory =
        OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    OpenRtb.BidResponse.SeatBid.Bid.Builder seatBid1 = OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
        .setId("bid1")
        .setImpid("imp1")
        .setPrice(19.95)
        .setAdid("adid1")
        .setNurl("http://iwon.com")
        .addAdomain("http://myads.com")
        .setIurl("http://mycdn.com/ad.gif")
        .setCid("cid1")
        .setCrid("crid1")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .setDealid("deal1")
        .setW(100)
        .setH(80)
        .setBundle("com.google.testapp")
        .addCat("IAB10-2")
        .setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.NativeResponse.Builder nativeResponse1 = OpenRtb.NativeResponse.newBuilder()
        .setVer("1.0")
        .setLink(OpenRtb.NativeResponse.Link.newBuilder())
        .addImptrackers("http://my.first.imp.tracker");

    OpenRtb.BidResponse.SeatBid.Bid.Builder seatBid2 = OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
        .setId("bid2")
        .setImpid("imp2")
        .setPrice(19.95)
        .setAdid("adid2")
        .setNurl("http://iwon.com")
        .addAdomain("http://myads.com")
        .setIurl("http://mycdn.com/ad.gif")
        .setCid("cid2")
        .setCrid("crid2")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .setDealid("deal2")
        .setW(100)
        .setH(80)
        .setBundle("com.google.testapp")
        .addCat("IAB10-2")
        .setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.NativeResponse.Builder nativeResponse2 = OpenRtb.NativeResponse.newBuilder()
        .setVer("2.0")
        .setLink(OpenRtb.NativeResponse.Link.newBuilder())
        .addImptrackers("http://my.second.imp.tracker");

    OpenRtb.BidResponse.SeatBid.Bid.Builder seatBid3 = OpenRtb.BidResponse.SeatBid.Bid.newBuilder()
        .setId("bid2")
        .setImpid("imp3")
        .setPrice(19.95)
        .setAdid("adid3")
        .setNurl("http://iwon.com")
        .addAdomain("http://myads.com")
        .setIurl("http://mycdn.com/ad.gif")
        .setCid("cid3")
        .setCrid("crid3")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .setDealid("deal3")
        .setW(100)
        .setH(80)
        .setBundle("com.google.testapp")
        .addCat("IAB10-2")
        .setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.NativeResponse.Builder nativeResponse3 = OpenRtb.NativeResponse.newBuilder()
        .setVer("3.0")
        .setLink(OpenRtb.NativeResponse.Link.newBuilder())
        .addImptrackers("http://my.third.imp.tracker");

    if (isNativeObject) {
      seatBid1.setAdmNative(nativeResponse1);
      seatBid2.setAdmNative(nativeResponse2);
      seatBid3.setAdmNative(nativeResponse3);
    } else {
      seatBid1.setAdm(JsonFactory.newNativeWriter().writeNativeResponse(nativeResponse1.build()));
      seatBid2.setAdm(JsonFactory.newNativeWriter().writeNativeResponse(nativeResponse2.build()));
      seatBid3.setAdm(JsonFactory.newNativeWriter().writeNativeResponse(nativeResponse3.build()));
    }

    OpenRtb.BidResponse.SeatBid.Builder seat1 = OpenRtb.BidResponse.SeatBid.newBuilder()
        .addBid(seatBid1)
        .setSeat("seat1")
        .setGroup(false)
        .setExtension(TestExt.testSeat, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.BidResponse.SeatBid.Builder seat2 = OpenRtb.BidResponse.SeatBid.newBuilder()
        .addBid(seatBid2)
        .addBid(seatBid3)
        .setSeat("seat2")
        .setGroup(true)
        .setExtension(TestExt.testSeat, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.BidResponse.Builder bidResponse = OpenRtb.BidResponse.newBuilder()
        .setId("resp1")
        .addSeatbid(seat1)
        .addSeatbid(seat2)
        .setBidid("bid1")
        .setCur("USD")
        .setCustomdata("mydata")
        .setNbr(OpenRtb.NoBidReason.TECHNICAL_ERROR)
        .setExtension(TestExt.testResponse1, OpenRtbJsonFactoryHelper.test1)
        .addExtension(TestExt.testResponse2, OpenRtbJsonFactoryHelper.test2)
        .addExtension(TestExt.testResponse2, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testResponse2A, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testResponse2B, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testResponse3, 99)
        .addExtension(TestExt.testResponse4, 10)
        .addExtension(TestExt.testResponse4, 20);

    return JsonFactory.newWriter().writeBidResponse(bidResponse.build());
  }
}
