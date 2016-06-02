package com.google.openrtb.json;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.TestExt;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;

/**
 * Test helper class, to be used for generating and comparing Json test data <p> Created by
 * bundeskanzler4711 on 12/05/16.
 */
class OpenRtbJsonRequestHelper {
  /**
   * Request Json string containing <p> - native part as adm string field
   */
  static final String REQUEST__SHORT_NOROOT_STRING = OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_NOROOT_STRING.json");

  /**
   * Request Json string containing <p> - native part as adm_native object
   */
  static final String REQUEST__SHORT_NOROOT_OBJECT = OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_NOROOT_OBJECT.json");

  /**
   * Request Json string containing <p> - native part as adm string field <p> - root native enabled
   */
  static final String REQUEST__SHORT_ROOT___STRING = OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_ROOT___STRING.json");

  /**
   * Request Json string containing <p> - native part as adm_native object <p> - root native
   * enabled
   */
  static final String REQUEST__SHORT_ROOT___OBJECT = OpenRtbJsonFactoryHelper.readFile("REQUEST__SHORT_ROOT___OBJECT.json");

  /**
   * Request Json string containing <p> - native part as adm string field <p> - nearly all possible
   * fields filled
   */
  static final String REQUEST__FULL__NOROOT_STRING = OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__NOROOT_STRING.json");

  /**
   * Request Json string containing <p> - native part as adm_native object <p> - nearly all possible
   * fields filled
   */
  static final String REQUEST__FULL__NOROOT_OBJECT = OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__NOROOT_OBJECT.json");

  /**
   * Request Json string <p> - containing native part as adm string field <p> - root native enabled
   * <p> - nearly all possible fields filled
   */
  static final String REQUEST__FULL__ROOT___STRING = OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__ROOT___STRING.json");

  /**
   * Request Json string containing <p> - native part as adm_native object <p> - root native enabled
   * <p> - nearly all possible fields filled
   */
  static final String REQUEST__FULL__ROOT___OBJECT = OpenRtbJsonFactoryHelper.readFile("REQUEST__FULL__ROOT___OBJECT.json");

  private static final Logger logger = LoggerFactory.getLogger(OpenRtbJsonRequestHelper.class);

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
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__SHORT_NOROOT_STRING.json", generateJson(false, false, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__SHORT_NOROOT_OBJECT.json", generateJson(false, false, true));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__SHORT_ROOT___STRING.json", generateJson(false, true, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__SHORT_ROOT___OBJECT.json", generateJson(false, true, true));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__FULL__NOROOT_STRING.json", generateJson(true, false, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__FULL__NOROOT_OBJECT.json", generateJson(true, false, true));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__FULL__ROOT___STRING.json", generateJson(true, true, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/REQUEST__FULL__ROOT___OBJECT.json", generateJson(true, true, true));
  }

  /**
   * Json generator method, using these Parameters:
   * @param isFull true, if nearly all fields should be filled; just some selected fields otherwise
   * @param isRootNative true, if the "native" field should be included as root element
   * @param isNativeObject true, if the native part should be generated as Json object; String
   * otherwise
   * @return not pretty printed String representation of Json
   */
  private static String generateJson(final boolean isFull, final boolean isRootNative, final boolean isNativeObject)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Object json = mapper.readValue(generateRequest(isFull, isRootNative, isNativeObject), Object.class);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  private static String generateRequest(final boolean isFull, final boolean isRootNative, final boolean isNativeObject)
      throws IOException {
    return isFull ? generateFullRequest(isRootNative, isNativeObject) :
        generateShortRequest(isRootNative, isNativeObject);
  }

  private static String generateShortRequest(final boolean isRootNative, final boolean isNativeObject)
      throws IOException {
    // Used for BidRequest
    final OpenRtb.BidRequest.Imp.Builder impressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
    final OpenRtb.BidRequest.Site.Builder siteBuilder = OpenRtb.BidRequest.Site.newBuilder();
    final OpenRtb.BidRequest.App.Builder appBuilder = OpenRtb.BidRequest.App.newBuilder();
    final OpenRtb.BidRequest.Geo.Builder geoBuilder = OpenRtb.BidRequest.Geo.newBuilder();
    final OpenRtb.BidRequest.Device.Builder deviceBuilder = OpenRtb.BidRequest.Device.newBuilder();
    final OpenRtb.BidRequest.User.Builder userBuilder = OpenRtb.BidRequest.User.newBuilder();

    // Used for Impression
    final OpenRtb.BidRequest.Imp.Native.Builder nativeBuilder = OpenRtb.BidRequest.Imp.Native.newBuilder();

    // The BidRequest builder
    final OpenRtb.BidRequest.Builder bidRequestBuilder = OpenRtb.BidRequest.newBuilder();

    final OpenRtbJsonFactory openRtbJsonFactory = OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    if (isNativeObject) {
      nativeBuilder.setRequestNative(generateNativeRequest());
    } else {
      nativeBuilder.setRequest(openRtbJsonFactory
          .newNativeWriter()
          .writeNativeRequest(generateNativeRequest().build()));
    }
    impressionBuilder.setId("imp1").setBidfloor(100.0).setBidfloorcur("USD").setNative(nativeBuilder);

    siteBuilder.setId("site1").setDomain("mysite.foo.com").setPage("http://mysite.foo.com/my/link").setMobile(false).setKeywords("my,key,words");

    appBuilder.setId("app1").setName("my-app-name").setDomain("mysite.foo.com").setPaid(true).setKeywords("my,app,key,words");

    geoBuilder.setCity("New York");

    deviceBuilder.setUa("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
        .setGeo(geoBuilder).setDnt(false).setLmt(true).setIp("192.168.1.0").setIpv6("1:2:3:4:5:6:0:0");

    userBuilder.setId("user1").setBuyeruid("buyer1").setGender("O").setKeywords("user,builder,key,words").setGeo(geoBuilder);

    bidRequestBuilder.setId("9zj61whbdl319sjgz098lpys5cngmtro_short_" + isRootNative + "_" + isNativeObject)
        .addImp(impressionBuilder).setSite(siteBuilder).setApp(appBuilder).setDevice(deviceBuilder).setUser(userBuilder);

    return openRtbJsonFactory.newWriter().writeBidRequest(bidRequestBuilder.build());
  }

  private static String generateFullRequest(final boolean isRootNative, final boolean isNativeObject)
      throws IOException {
    final OpenRtb.BidRequest.Imp.Builder firstImpressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
    final OpenRtb.BidRequest.Imp.Builder secondImpressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
    final OpenRtb.BidRequest.Imp.Builder thirdImpressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
    final OpenRtb.BidRequest.Imp.Native.Builder nativeBuilder = OpenRtb.BidRequest.Imp.Native.newBuilder();
    final OpenRtb.BidRequest.Device.Builder deviceBuilder = OpenRtb.BidRequest.Device.newBuilder();
    final OpenRtb.BidRequest.User.Builder userBuilder = OpenRtb.BidRequest.User.newBuilder();
    final OpenRtb.BidRequest.Regs.Builder regsBuilder = OpenRtb.BidRequest.Regs.newBuilder();
    final OpenRtb.BidRequest.Builder bidRequestBuilder = OpenRtb.BidRequest.newBuilder();

    final OpenRtbJsonFactory openRtbJsonFactory = OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    firstImpressionBuilder.setId("imp1").setBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder().setWmax(300)
        .setWmin(200).setHmax(100).setHmin(50).setId("banner1").setPos(OpenRtb.BidRequest.Imp.AdPosition.ABOVE_THE_FOLD)
        .addBtype(OpenRtb.BidRequest.Imp.Banner.BannerAdType.JAVASCRIPT_AD).addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .addMimes("image/gif").setTopframe(true).addExpdir(OpenRtb.BidRequest.Imp.Banner.ExpandableDirection.RIGHT)
        .addApi(OpenRtb.BidRequest.Imp.APIFramework.MRAID_1).setExtension(TestExt.testBanner, OpenRtbJsonFactoryHelper.test1))
        .setDisplaymanager("dm1").setDisplaymanagerver("1.0").setInstl(false).setTagid("tag1").setBidfloor(100.0)
        .setBidfloorcur("USD").setSecure(false).addIframebuster("buster1").setPmp(OpenRtb.BidRequest.Imp.Pmp.newBuilder()
        .setPrivateAuction(false).addDeals(OpenRtb.BidRequest.Imp.Pmp.Deal.newBuilder().setId("deal1").setBidfloor(200.0)
            .setBidfloorcur("USD").addWseat("seat2").addWadomain("goodadv1").setAt(OpenRtb.BidRequest.AuctionType.SECOND_PRICE)
            .setExtension(TestExt.testDeal, OpenRtbJsonFactoryHelper.test1)).setExtension(TestExt.testPmp, OpenRtbJsonFactoryHelper.test1))
        .setExtension(TestExt.testImp, OpenRtbJsonFactoryHelper.test1);

    secondImpressionBuilder.setId("imp2").setVideo(OpenRtb.BidRequest.Imp.Video.newBuilder().addMimes("video/vp9")
        .setLinearity(OpenRtb.BidRequest.Imp.Video.VideoLinearity.LINEAR).setMinduration(15).setMaxduration(60)
        .setProtocol(OpenRtb.BidRequest.Imp.Video.VideoBidResponseProtocol.VAST_3_0)
        .addProtocols(OpenRtb.BidRequest.Imp.Video.VideoBidResponseProtocol.VAST_2_0).setW(200).setH(50).setStartdelay(0)
        .setSequence(1).addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY).setMaxextended(120).setMinbitrate(1000)
        .setMaxbitrate(2000).setBoxingallowed(false).addPlaybackmethod(OpenRtb.BidRequest.Imp.Video.VideoPlaybackMethod.CLICK_TO_PLAY)
        .addDelivery(OpenRtb.BidRequest.Imp.Video.ContentDeliveryMethod.STREAMING)
        .setPos(OpenRtb.BidRequest.Imp.AdPosition.ABOVE_THE_FOLD).addCompanionad(OpenRtb.BidRequest.Imp.Banner.newBuilder()
            .setId("compad1").setW(100).setH(50)).setCompanionad21(OpenRtb.BidRequest.Imp.Video.CompanionAd.newBuilder()
            .addBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder().setId("compad2").setW(110).setH(60)))
        .addApi(OpenRtb.BidRequest.Imp.APIFramework.VPAID_2).addCompaniontype(OpenRtb.BidRequest.Imp.Video.VASTCompanionType.HTML)
        .setExtension(TestExt.testVideo, OpenRtbJsonFactoryHelper.test1));

    if (isNativeObject) {
      nativeBuilder.setRequestNative(generateNativeRequest());
    } else {
      nativeBuilder.setRequest(openRtbJsonFactory
          .newNativeWriter()
          .writeNativeRequest(generateNativeRequest().build()));
    }

    nativeBuilder.setVer("1.0").addApi(OpenRtb.BidRequest.Imp.APIFramework.MRAID_1).addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
        .setExtension(TestExt.testNative, OpenRtbJsonFactoryHelper.test1);

    thirdImpressionBuilder.setId("imp3").setNative(nativeBuilder).setExtension(TestExt.testImp, OpenRtbJsonFactoryHelper.test1);

    deviceBuilder.setUa("Chrome").setGeo(OpenRtb.BidRequest.Geo.newBuilder().setLat(90.0).setLon(45.0)
        .setType(OpenRtb.BidRequest.Geo.LocationType.GPS_LOCATION).setCountry("USA").setRegion("New York")
        .setRegionfips104("US36").setMetro("New York").setCity("New York City").setZip("10000").setUtcoffset(3600)
        .setExtension(TestExt.testGeo, OpenRtbJsonFactoryHelper.test1)).setDnt(false).setLmt(false).setIp("192.168.1.0")
        .setIpv6("1:2:3:4:5:6:0:0").setDevicetype(OpenRtb.BidRequest.Device.DeviceType.MOBILE).setMake("Motorola")
        .setModel("MotoX").setOs("Android").setOsv("3.2.1").setHwv("X").setW(640).setH(1024).setPpi(300).setPxratio(1.0)
        .setJs(true).setFlashver("11").setLanguage("en").setCarrier("77777").setConnectiontype(OpenRtb.BidRequest.Device
        .ConnectionType.CELL_4G).setIfa("999").setDidsha1("1234").setDidmd5("4321").setDpidsha1("5678").setDpidmd5("8765")
        .setMacsha1("abc").setMacmd5("xyz").setExtension(TestExt.testDevice, OpenRtbJsonFactoryHelper.test1);

    userBuilder.setId("user1").setBuyeruid("Picard").setYob(1973).setGender("M").setKeywords("boldly,going")
        .setCustomdata("data1").setGeo(OpenRtb.BidRequest.Geo.newBuilder().setZip("12345"))
        .addData(OpenRtb.BidRequest.Data.newBuilder().setId("data1").setName("dataname1")
            .addSegment(OpenRtb.BidRequest.Data.Segment.newBuilder().setId("seg1").setName("segname1")
                .setValue("segval1").setExtension(TestExt.testSegment, OpenRtbJsonFactoryHelper.test1))
            .setExtension(TestExt.testData, OpenRtbJsonFactoryHelper.test1))
        .setExtension(TestExt.testUser, OpenRtbJsonFactoryHelper.test1);

    regsBuilder.setCoppa(true).setExtension(TestExt.testRegs, OpenRtbJsonFactoryHelper.test1);

    bidRequestBuilder.setId("9zj61whbdl319sjgz098lpys5cngmtro_full_" + isRootNative + "_" + isNativeObject)
        .addImp(firstImpressionBuilder).addImp(secondImpressionBuilder).addImp(thirdImpressionBuilder).setDevice(deviceBuilder)
        .setUser(userBuilder).setAt(OpenRtb.BidRequest.AuctionType.SECOND_PRICE).setTmax(100).addWseat("seat1").setAllimps(false)
        .addCur("USD").addAllBcat(asList("IAB11", "IAB11-4")).addBadv("badguy").setRegs(regsBuilder).setTest(false)
        .setExtension(TestExt.testRequest2, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testRequest1, OpenRtbJsonFactoryHelper.test1);

    return openRtbJsonFactory.newWriter().writeBidRequest(bidRequestBuilder.build());
  }

  private static OpenRtb.NativeRequest.Builder generateNativeRequest() throws IOException {
    final OpenRtb.NativeRequest.Builder nativeRequest = OpenRtb.NativeRequest.newBuilder();
    nativeRequest.setVer("1").setLayout(OpenRtb.NativeRequest.LayoutId.APP_WALL).setAdunit(OpenRtb.NativeRequest.AdUnitId.IAB_IN_AD_NATIVE).setPlcmtcnt(1).setSeq(1);
    return nativeRequest;
  }


}

