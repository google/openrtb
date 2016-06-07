package com.google.openrtb.json;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.TestExt;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Test helper class, to be used for generating and comparing Json test data <p> Created by
 * bundeskanzler4711 on 12/05/16.
 */
class OpenRtbJsonResponseHelper {

  /**
   * Response Json string containing <p> - native part as adm string field
   */
  static final String RESPONSE_SHORT_NOROOT_STRING = OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_NOROOT_STRING.json");

  /**
   * Response Json string containing <p> - native part as adm_native object
   */
  static final String RESPONSE_SHORT_NOROOT_OBJECT = OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_NOROOT_OBJECT.json");

  /**
   * Response Json string containing <p> - native part as adm string field <p> - root native
   * enabled
   */
  static final String RESPONSE_SHORT_ROOT___STRING = OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_ROOT___STRING.json");

  /**
   * Response Json string containing <p> - native part as adm_native object <p> - root native
   * enabled
   */
  static final String RESPONSE_SHORT_ROOT___OBJECT = OpenRtbJsonFactoryHelper.readFile("RESPONSE_SHORT_ROOT___OBJECT.json");

  /**
   * Response Json string containing <p> - native part as adm string field <p> - nearly all possible
   * fields filled
   */
  static final String RESPONSE_FULL__NOROOT_STRING = OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__NOROOT_STRING.json");

  /**
   * Response Json string containing <p> - native part as adm_native object <p> - nearly all
   * possible fields filled
   */
  static final String RESPONSE_FULL__NOROOT_OBJECT = OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__NOROOT_OBJECT.json");

  /**
   * Response Json string <p> - containing native part as adm string field <p> - root native enabled
   * <p> - nearly all possible fields filled
   */
  static final String RESPONSE_FULL__ROOT___STRING = OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__ROOT___STRING.json");

  /**
   * Response Json string containing <p> - native part as adm_native object <p> - root native
   * enabled <p> - nearly all possible fields filled
   */
  static final String RESPONSE_FULL__ROOT___OBJECT = OpenRtbJsonFactoryHelper.readFile("RESPONSE_FULL__ROOT___OBJECT.json");

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OpenRtbJsonResponseHelper.class);

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
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_SHORT_NOROOT_STRING.json", generateJson(false, false, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_SHORT_NOROOT_OBJECT.json", generateJson(false, false, true));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_SHORT_ROOT___STRING.json", generateJson(false, true, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_SHORT_ROOT___OBJECT.json", generateJson(false, true, true));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_FULL__NOROOT_STRING.json", generateJson(true, false, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_FULL__NOROOT_OBJECT.json", generateJson(true, false, true));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_FULL__ROOT___STRING.json", generateJson(true, true, false));
    OpenRtbJsonFactoryHelper.writeFile("openrtb-core/src/test/resources/RESPONSE_FULL__ROOT___OBJECT.json", generateJson(true, true, true));
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
    Object json = mapper.readValue(generateResponse(isFull, isRootNative, isNativeObject), Object.class);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  private static String generateResponse(final boolean isFull, final boolean isRootNative,
                                         final boolean isNativeObject) throws IOException {
    return isFull ? generateFullResponse(isRootNative, isNativeObject) :
        generateShortResponse(isRootNative, isNativeObject);
  }

  private static String generateShortResponse(final boolean isRootNative, final boolean isNativeObject)
      throws IOException {
    final OpenRtb.BidResponse.SeatBid.Bid.Builder seatBidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
    final OpenRtb.BidResponse.SeatBid.Builder seatBuilder = OpenRtb.BidResponse.SeatBid.newBuilder();
    final OpenRtb.NativeResponse.Builder nativeResponseBuilder = OpenRtb.NativeResponse.newBuilder();

    final OpenRtb.BidResponse.Builder bidResponseBuilder = OpenRtb.BidResponse.newBuilder();

    final OpenRtbJsonFactory openRtbJsonFactory = OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    seatBidBuilder.setId("bid").setImpid("imp").setPrice(19.95).setAdid("adid").setNurl("http://iwon.com")
        .addAdomain("http://myads.com").setIurl("http://mycdn.com/ad.gif").setCid("cid").setCrid("crid")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY).setDealid("deal").setW(100).setH(80).setBundle("com.google.testapp")
        .addCat("IAB10-2").setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    nativeResponseBuilder.setVer("1.0").setLink(OpenRtb.NativeResponse.Link.newBuilder()).addImptrackers("http://my.imp.tracker");

    if (isNativeObject) {
      seatBidBuilder.setAdmNative(nativeResponseBuilder);
    } else {
      seatBidBuilder.setAdm(openRtbJsonFactory.newNativeWriter().writeNativeResponse(nativeResponseBuilder.build()));
    }

    seatBuilder.addBid(seatBidBuilder).setSeat("seat");

    bidResponseBuilder.setId("resp").addSeatbid(seatBuilder).setBidid("bid").setCur("USD").setCustomdata("mydata").setNbr(OpenRtb.BidResponse.NoBidReason.TECHNICAL_ERROR);

    return openRtbJsonFactory.newWriter().writeBidResponse(bidResponseBuilder.build());

  }

  private static String generateFullResponse(final boolean isRootNative, final boolean isNativeObject)
      throws IOException {
    final OpenRtb.BidResponse.SeatBid.Bid.Builder firstSeatBidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
    final OpenRtb.BidResponse.SeatBid.Builder firstSeatBuilder = OpenRtb.BidResponse.SeatBid.newBuilder();
    final OpenRtb.NativeResponse.Builder firstNativeResponseBuilder = OpenRtb.NativeResponse.newBuilder();

    final OpenRtb.BidResponse.SeatBid.Bid.Builder secondSeatBidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
    final OpenRtb.BidResponse.SeatBid.Builder secondSeatBuilder = OpenRtb.BidResponse.SeatBid.newBuilder();
    final OpenRtb.NativeResponse.Builder secondNativeResponseBuilder = OpenRtb.NativeResponse.newBuilder();

    final OpenRtb.BidResponse.SeatBid.Bid.Builder thirdSeatBidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
    final OpenRtb.NativeResponse.Builder thirdNativeResponseBuilder = OpenRtb.NativeResponse.newBuilder();

    final List<OpenRtb.BidResponse.SeatBid> seatArrayList = new ArrayList<>();
    final List<OpenRtb.BidResponse.SeatBid.Bid> seatBidArrayList = new ArrayList<>();
    final OpenRtb.BidResponse.Builder bidResponseBuilder = OpenRtb.BidResponse.newBuilder();

    final OpenRtbJsonFactory openRtbJsonFactory = OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative, isNativeObject);

    firstSeatBidBuilder.setId("bid1").setImpid("imp1").setPrice(19.95).setAdid("adid1").setNurl("http://iwon.com")
        .addAdomain("http://myads.com").setIurl("http://mycdn.com/ad.gif").setCid("cid1").setCrid("crid1")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY).setDealid("deal1").setW(100).setH(80).setBundle("com.google.testapp")
        .addCat("IAB10-2").setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    firstNativeResponseBuilder.setVer("1.0").setLink(OpenRtb.NativeResponse.Link.newBuilder()).addImptrackers("http://my.first.imp.tracker");

    secondSeatBidBuilder.setId("bid2").setImpid("imp2").setPrice(19.95).setAdid("adid2").setNurl("http://iwon.com")
        .addAdomain("http://myads.com").setIurl("http://mycdn.com/ad.gif").setCid("cid2").setCrid("crid2")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY).setDealid("deal2").setW(100).setH(80).setBundle("com.google.testapp")
        .addCat("IAB10-2").setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    secondNativeResponseBuilder.setVer("2.0").setLink(OpenRtb.NativeResponse.Link.newBuilder()).addImptrackers("http://my.first.imp.tracker");

    thirdSeatBidBuilder.setId("bid2").setImpid("imp3").setPrice(19.95).setAdid("adid3").setNurl("http://iwon.com")
        .addAdomain("http://myads.com").setIurl("http://mycdn.com/ad.gif").setCid("cid3").setCrid("crid3")
        .addAttr(OpenRtb.CreativeAttribute.TEXT_ONLY).setDealid("deal3").setW(100).setH(80).setBundle("com.google.testapp")
        .addCat("IAB10-2").setExtension(TestExt.testBid, OpenRtbJsonFactoryHelper.test1);

    thirdNativeResponseBuilder.setVer("3.0").setLink(OpenRtb.NativeResponse.Link.newBuilder()).addImptrackers("http://my.third.imp.tracker");

    if (isNativeObject) {
      firstSeatBidBuilder.setAdmNative(firstNativeResponseBuilder);
      secondSeatBidBuilder.setAdmNative(secondNativeResponseBuilder);
    } else {
      firstSeatBidBuilder.setAdm(openRtbJsonFactory.newNativeWriter().writeNativeResponse(firstNativeResponseBuilder.build()));
      secondSeatBidBuilder.setAdm(openRtbJsonFactory.newNativeWriter().writeNativeResponse(secondNativeResponseBuilder.build()));
    }

    firstSeatBuilder.addBid(firstSeatBidBuilder).setSeat("seat1").setGroup(false).setExtension(TestExt.testSeat, OpenRtbJsonFactoryHelper.test1);

    seatBidArrayList.add(secondSeatBidBuilder.build());
    seatBidArrayList.add(thirdSeatBidBuilder.build());

    secondSeatBuilder.addAllBid(seatBidArrayList).setSeat("seat2").setGroup(true).setExtension(TestExt.testSeat, OpenRtbJsonFactoryHelper.test1);

    seatArrayList.add(firstSeatBuilder.build());
    seatArrayList.add(secondSeatBuilder.build());

    bidResponseBuilder.setId("resp1").addAllSeatbid(seatArrayList).setBidid("bid1").setCur("USD").setCustomdata("mydata")
        .setNbr(OpenRtb.BidResponse.NoBidReason.TECHNICAL_ERROR).setExtension(TestExt.testResponse1, OpenRtbJsonFactoryHelper.test1)
        .addExtension(TestExt.testResponse2, OpenRtbJsonFactoryHelper.test2).addExtension(TestExt.testResponse2, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testResponse2A, OpenRtbJsonFactoryHelper.test2).setExtension(TestExt.testResponse2B, OpenRtbJsonFactoryHelper.test2)
        .setExtension(TestExt.testResponse3, 99).addExtension(TestExt.testResponse4, 10).addExtension(TestExt.testResponse4, 20);

    return openRtbJsonFactory.newWriter().writeBidResponse(bidResponseBuilder.build());
  }

}
