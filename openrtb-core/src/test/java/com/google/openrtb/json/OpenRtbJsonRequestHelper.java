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
import static java.util.Arrays.asList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.TestExt;
import java.io.IOException;

/**
 * Test helper class, to be used for generating and comparing JSON test data.
 */
class OpenRtbJsonRequestHelper {
  /**
   * Request JSON containing: native part as adm string field.
   */
  static final String REQUEST__SHORT_NOROOT_STRING =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_NOROOT_STRING.json");

  /**
   * Request JSON containing: native part as adm_native object.
   */
  static final String REQUEST__SHORT_NOROOT_OBJECT =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_NOROOT_OBJECT.json");

  /**
   * Request JSON containing: native part as adm string field; root native enabled.
   */
  static final String REQUEST__SHORT_ROOT___STRING =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_ROOT___STRING.json");

  /**
   * Request JSON containing: native part as adm_native object; root native enabled.
   */
  static final String REQUEST__SHORT_ROOT___OBJECT =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_ROOT___OBJECT.json");

  /**
   * Request JSON containing: native part as adm string field; nearly all possible fields filled.
   */
  static final String REQUEST__FULL__NOROOT_STRING =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__NOROOT_STRING.json");

  /**
   * Request JSON containing: native part as adm_native object; nearly all possible fields filled.
   */
  static final String REQUEST__FULL__NOROOT_OBJECT =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__NOROOT_OBJECT.json");

  /**
   * Request JSON containing: native part as adm string field; root native enabled;
   * nearly all possible fields filled.
   */
  static final String REQUEST__FULL__ROOT___STRING =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__ROOT___STRING.json");

  /**
   * Request JSON containing: native part as adm_native object; root native enabled;
   * nearly all possible fields filled.
   */
  static final String REQUEST__FULL__ROOT___OBJECT =
      OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__ROOT___OBJECT.json");

  public static void testJsonGeneratedFiles() throws IOException {
    assertThat(generateJson(false, false, false)).isEqualTo(REQUEST__SHORT_NOROOT_STRING);
    assertThat(generateJson(false, false, true)).isEqualTo(REQUEST__SHORT_NOROOT_OBJECT);
    assertThat(generateJson(false, true, false)).isEqualTo(REQUEST__SHORT_ROOT___STRING);
    assertThat(generateJson(false, true, true)).isEqualTo(REQUEST__SHORT_ROOT___OBJECT);
    assertThat(generateJson(true, false, false)).isEqualTo(REQUEST__FULL__NOROOT_STRING);
    assertThat(generateJson(true, false, true)).isEqualTo(REQUEST__FULL__NOROOT_OBJECT);
    assertThat(generateJson(true, true, false)).isEqualTo(REQUEST__FULL__ROOT___STRING);
    assertThat(generateJson(true, true, true)).isEqualTo(REQUEST__FULL__ROOT___OBJECT);
  }

  public static void main(String[] args) throws IOException {
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__SHORT_NOROOT_STRING.json",
        generateJson(false, false, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__SHORT_NOROOT_OBJECT.json",
        generateJson(false, false, true));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__SHORT_ROOT___STRING.json",
        generateJson(false, true, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__SHORT_ROOT___OBJECT.json",
        generateJson(false, true, true));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__FULL__NOROOT_STRING.json",
        generateJson(true, false, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__FULL__NOROOT_OBJECT.json",
        generateJson(true, false, true));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__FULL__ROOT___STRING.json",
        generateJson(true, true, false));
    OpenRtbJsonFactoryHelper.writeFile(
        "openrtb-core/src/test/resources/REQUEST__FULL__ROOT___OBJECT.json",
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
        generateRequest(isFull, isRootNative, isNativeObject), Object.class);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  private static String generateRequest(
      boolean isFull, boolean isRootNative, boolean isNativeObject) throws IOException {
    return isFull
        ? generateFullRequest(isRootNative, isNativeObject)
        : generateShortRequest(isRootNative, isNativeObject);
  }

  private static String generateShortRequest(boolean isRootNative, boolean isNativeObject)
      throws IOException {
    OpenRtbJsonFactory jsonFactory =
        OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    OpenRtb.BidRequest.Imp.Native.Builder nativ = OpenRtb.BidRequest.Imp.Native.newBuilder();
    if (isNativeObject) {
      nativ.setRequestNative(generateNativeRequest());
    } else {
      nativ.setRequest(jsonFactory.newNativeWriter().writeNativeRequest(
          generateNativeRequest().build()));
    }

    OpenRtb.BidRequest.Geo.Builder geo = OpenRtb.BidRequest.Geo.newBuilder()
        .setCity("New York");

    OpenRtb.BidRequest.Builder bidRequest = OpenRtb.BidRequest.newBuilder()
        .setId("9zj61whbdl319sjgz098lpys5cngmtro_short_" + isRootNative + "_" + isNativeObject)
        .addImp(OpenRtb.BidRequest.Imp.newBuilder()
            .setId("imp1")
            .setBidfloor(100.0)
            .setBidfloorcur("USD")
            .setNative(nativ))
        .setSite(OpenRtb.BidRequest.Site.newBuilder()
            .setId("site1")
            .setDomain("mysite.foo.com")
            .setPage("http://mysite.foo.com/my/link")
            .setMobile(false)
            .setKeywords("my,key,words"))
        .setApp(OpenRtb.BidRequest.App.newBuilder()
            .setId("app1")
            .setName("my-app-name")
            .setDomain("mysite.foo.com")
            .setPaid(true)
            .setKeywords("my,app,key,words"))
        .setDevice(OpenRtb.BidRequest.Device.newBuilder()
            .setUa("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) "
                + "Chrome/41.0.2228.0 Safari/537.36")
            .setGeo(geo)
            .setDnt(false)
            .setLmt(true)
            .setIp("192.168.1.0")
            .setIpv6("1:2:3:4:5:6:0:0"))
        .setUser(OpenRtb.BidRequest.User.newBuilder()
            .setId("user1")
            .setBuyeruid("buyer1")
            .setGender("O")
            .setKeywords("user,builder,key,words")
            .setGeo(geo));

    return jsonFactory.newWriter().writeBidRequest(bidRequest.build());
  }

  private static String generateFullRequest(boolean isRootNative, boolean isNativeObject)
      throws IOException {
    OpenRtbJsonFactory jsonFactory =
        OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    @SuppressWarnings("deprecation")
    OpenRtb.BidRequest.Imp.Builder imp1 = OpenRtb.BidRequest.Imp.newBuilder()
        .setId("imp1")
        .setBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder()
            .setWmax(300)
            .setWmin(200)
            .setHmax(100)
            .setHmin(50)
            .setId("banner1")
            .setPos(OpenRtb.AdPosition.ABOVE_THE_FOLD)
            .addBtype(OpenRtb.BannerAdType.JAVASCRIPT_AD)
            .addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
            .addMimes("image/gif")
            .setTopframe(true)
            .addExpdir(OpenRtb.ExpandableDirection.RIGHT)
            .addApi(OpenRtb.APIFramework.MRAID_1)
            .setExtension(TestExt.testBanner, OpenRtbJsonFactoryHelper.test1))
        .setDisplaymanager("dm1")
        .setDisplaymanagerver("1.0")
        .setInstl(false)
        .setTagid("tag1")
        .setBidfloor(100.0)
        .setBidfloorcur("USD")
        .setSecure(false)
        .addIframebuster("buster1")
        .setPmp(OpenRtb.BidRequest.Imp.Pmp.newBuilder()
            .setPrivateAuction(false)
            .addDeals(OpenRtb.BidRequest.Imp.Pmp.Deal.newBuilder()
                .setId("deal1")
                .setBidfloor(200.0)
                .setBidfloorcur("USD")
                .addWseat("seat2")
                .addWadomain("goodadv1")
                .setAt(OpenRtb.AuctionType.SECOND_PRICE)
                .setExtension(TestExt.testDeal, OpenRtbJsonFactoryHelper.test1))
            .setExtension(TestExt.testPmp, OpenRtbJsonFactoryHelper.test1))
        .setExtension(TestExt.testImp, OpenRtbJsonFactoryHelper.test1);

    @SuppressWarnings("deprecation")
    OpenRtb.BidRequest.Imp.Builder imp2 = OpenRtb.BidRequest.Imp.newBuilder()
        .setId("imp2")
        .setVideo(OpenRtb.BidRequest.Imp.Video.newBuilder()
            // Video/Audio common
            .addMimes("video/vp9")
            .setMinduration(15)
            .setMaxduration(60)
            .addProtocols(OpenRtb.Protocol.VAST_2_0)
            .setStartdelay(0)
            .setSequence(1)
            .addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
            .setMaxextended(120)
            .setMinbitrate(1000)
            .setMaxbitrate(2000)
            .addDelivery(OpenRtb.ContentDeliveryMethod.STREAMING)
            .addCompanionad(OpenRtb.BidRequest.Imp.Banner.newBuilder()
                .setId("compad1")
                .setW(100)
                .setH(50))
            .addApi(OpenRtb.APIFramework.VPAID_2)
            .addCompaniontype(OpenRtb.CompanionType.HTML)
            // Video specific
            .setLinearity(OpenRtb.VideoLinearity.LINEAR)
            .setProtocol(OpenRtb.Protocol.VAST_3_0)
            .setW(200)
            .setH(50)
            .setBoxingallowed(false)
            .addPlaybackmethod(OpenRtb.PlaybackMethod.CLICK_TO_PLAY)
            .setPos(OpenRtb.AdPosition.ABOVE_THE_FOLD)
            .setCompanionad21(OpenRtb.BidRequest.Imp.Video.CompanionAd.newBuilder()
                .addBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder()
                    .setId("compad2")
                    .setW(110)
                    .setH(60)))
            .setExtension(TestExt.testVideo, OpenRtbJsonFactoryHelper.test1));

    OpenRtb.BidRequest.Imp.Native.Builder nativ = OpenRtb.BidRequest.Imp.Native.newBuilder()
        .setVer("1.0")
        .addApi(OpenRtb.APIFramework.MRAID_1)
        .addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .setExtension(TestExt.testNative, OpenRtbJsonFactoryHelper.test1);
    if (isNativeObject) {
      nativ.setRequestNative(generateNativeRequest());
    } else {
      nativ.setRequest(jsonFactory
          .newNativeWriter()
          .writeNativeRequest(generateNativeRequest().build()));
    }

    OpenRtb.BidRequest.Imp.Builder imp3 = OpenRtb.BidRequest.Imp.newBuilder()
        .setId("imp3")
        .setNative(nativ)
        .setExtension(TestExt.testImp, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.BidRequest.Device.Builder device = OpenRtb.BidRequest.Device.newBuilder()
        .setUa("Chrome")
        .setGeo(OpenRtb.BidRequest.Geo.newBuilder()
            .setLat(90.0)
            .setLon(45.0)
            .setType(OpenRtb.LocationType.GPS_LOCATION)
            .setCountry("USA")
            .setRegion("New York")
            .setRegionfips104("US36")
            .setMetro("New York")
            .setCity("New York City")
            .setZip("10000")
            .setUtcoffset(3600)
            .setExtension(TestExt.testGeo, OpenRtbJsonFactoryHelper.test1))
        .setDnt(false)
        .setLmt(false)
        .setIp("192.168.1.0")
        .setIpv6("1:2:3:4:5:6:0:0")
        .setDevicetype(OpenRtb.DeviceType.MOBILE)
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
        .setConnectiontype(OpenRtb.ConnectionType.CELL_4G)
        .setIfa("999")
        .setDidsha1("1234")
        .setDidmd5("4321")
        .setDpidsha1("5678")
        .setDpidmd5("8765")
        .setMacsha1("abc")
        .setMacmd5("xyz")
        .setExtension(TestExt.testDevice, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.BidRequest.User.Builder user = OpenRtb.BidRequest.User.newBuilder()
        .setId("user1")
        .setBuyeruid("Picard")
        .setYob(1973)
        .setGender("M")
        .setKeywords("boldly,going")
        .setCustomdata("data1")
        .setGeo(OpenRtb.BidRequest.Geo.newBuilder().setZip("12345"))
        .addData(OpenRtb.BidRequest.Data.newBuilder()
            .setId("data1")
            .setName("dataname1")
            .addSegment(OpenRtb.BidRequest.Data.Segment.newBuilder()
                .setId("seg1")
                .setName("segname1")
                .setValue("segval1")
                .setExtension(TestExt.testSegment, OpenRtbJsonFactoryHelper.test1))
            .setExtension(TestExt.testData, OpenRtbJsonFactoryHelper.test1))
        .setExtension(TestExt.testUser, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.BidRequest.Regs.Builder regs = OpenRtb.BidRequest.Regs.newBuilder()
        .setCoppa(true)
        .setExtension(TestExt.testRegs, OpenRtbJsonFactoryHelper.test1);

    OpenRtb.BidRequest.Builder bidRequest = OpenRtb.BidRequest.newBuilder()
        .setId("9zj61whbdl319sjgz098lpys5cngmtro_full_" + isRootNative + "_" + isNativeObject)
        .addImp(imp1)
        .addImp(imp2)
        .addImp(imp3)
        .setDevice(device)
        .setUser(user)
        .setAt(OpenRtb.AuctionType.SECOND_PRICE)
        .setTmax(100)
        .addWseat("seat1")
        .setAllimps(false)
        .addCur("USD")
        .addAllBcat(asList("IAB11", "IAB11-4"))
        .addBadv("badguy")
        .setRegs(regs)
        .setTest(false)
        .setExtension(TestExt.testRequest2, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testRequest1, OpenRtbJsonFactoryHelper.test1);

    return jsonFactory.newWriter().writeBidRequest(bidRequest.build());
  }

  private static OpenRtb.NativeRequest.Builder generateNativeRequest() {
    return OpenRtb.NativeRequest.newBuilder()
        .setVer("1")
        .setLayout(OpenRtb.LayoutId.APP_WALL)
        .setAdunit(OpenRtb.AdUnitId.IAB_IN_AD_NATIVE)
        .setPlcmtcnt(1)
        .setSeq(1);
  }
}
