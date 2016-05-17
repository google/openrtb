package com.google.openrtb.json;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.TestExt;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test helper class, to be used for generating and comparing Json test data
 * <p>
 * Created by sschlegel on 12/05/16.
 */
class OpenRtbJsonResponseHelper
{

   /**
    * Response Json string containing
    * <p>
    * - native part as adm string field
    */
   static final String RESPONSE_SHORT_NOROOT_STRING =
      "{\"id\":\"resp\",\"seatbid\":[{\"bid\":[{\"id\":\"bid\",\"impid\":\"imp\",\"price\":19.95,\"adid\":\"adid\"," +
      "\"nurl\":\"http://iwon.com\",\"adm\":\"{\\\"ver\\\":\\\"1.0\\\",\\\"link\\\":{},\\\"imptrackers\\\":[" +
      "\\\"http://my.imp.tracker\\\"]}\",\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid\",\"crid\":\"crid\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat\"}],\"bidid\":\"bid\"," +
      "\"cur\":\"USD\",\"customdata\":\"mydata\",\"nbr\":1}";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    */
   static final String RESPONSE_SHORT_NOROOT_OBJECT =
      "{\"id\":\"resp\",\"seatbid\":[{\"bid\":[{\"id\":\"bid\",\"impid\":\"imp\",\"price\":19.95,\"adid\":\"adid\"," +
      "\"nurl\":\"http://iwon.com\",\"adm_native\":{\"ver\":\"1.0\",\"link\":{},\"imptrackers\":[" +
      "\"http://my.imp.tracker\"]},\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid\",\"crid\":\"crid\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat\"}],\"bidid\":\"bid\"," +
      "\"cur\":\"USD\",\"customdata\":\"mydata\",\"nbr\":1}";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm string field
    * <p>
    * - root native enabled
    */
   static final String RESPONSE_SHORT_ROOT___STRING =
      "{\"id\":\"resp\",\"seatbid\":[{\"bid\":[{\"id\":\"bid\",\"impid\":\"imp\",\"price\":19.95,\"adid\":\"adid\"," +
      "\"nurl\":\"http://iwon.com\",\"adm\":\"{\\\"native\\\":{\\\"ver\\\":\\\"1.0\\\",\\\"link\\\":{}," +
      "\\\"imptrackers\\\":[\\\"http://my.imp.tracker\\\"]}}\",\"adomain\":[\"http://myads.com\"]," +
      "\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid\",\"crid\":\"crid\"," +
      "\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}]," +
      "\"seat\":\"seat\"}],\"bidid\":\"bid\",\"cur\":\"USD\",\"customdata\":\"mydata\",\"nbr\":1}";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - root native enabled
    */
   static final String RESPONSE_SHORT_ROOT___OBJECT =
      "{\"id\":\"resp\",\"seatbid\":[{\"bid\":[{\"id\":\"bid\",\"impid\":\"imp\",\"price\":19.95,\"adid\":\"adid\"," +
      "\"nurl\":\"http://iwon.com\",\"adm_native\":{\"native\":{\"ver\":\"1.0\",\"link\":{},\"imptrackers\":[" +
      "\"http://my.imp.tracker\"]}},\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid\",\"crid\":\"crid\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat\"}],\"bidid\":\"bid\"," +
      "\"cur\":\"USD\",\"customdata\":\"mydata\",\"nbr\":1}";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm string field
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__NOROOT_STRING =
      "{\"id\":\"resp1\",\"seatbid\":[{\"bid\":[{\"id\":\"bid1\",\"impid\":\"imp1\",\"price\":19.95,\"adid\":\"adid1\"," +
      "\"nurl\":\"http://iwon.com\",\"adm\":\"{\\\"ver\\\":\\\"1.0\\\",\\\"link\\\":{},\\\"imptrackers\\\":[" +
      "\\\"http://my.first.imp.tracker\\\"]}\",\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid1\",\"crid\":\"crid1\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal1\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat1\",\"group\":0,\"ext\":{" +
      "\"test1\":\"data1\"}},{\"bid\":[{\"id\":\"bid2\",\"impid\":\"imp2\",\"price\":19.95,\"adid\":\"adid2\"," +
      "\"nurl\":\"http://iwon.com\",\"adm\":\"{\\\"ver\\\":\\\"2.0\\\",\\\"link\\\":{},\\\"imptrackers\\\":[" +
      "\\\"http://my.first.imp.tracker\\\"]}\",\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid2\",\"crid\":\"crid2\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal2\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}},{\"id\":\"bid2\",\"impid\":\"imp3\"," +
      "\"price\":19.95,\"adid\":\"adid3\",\"nurl\":\"http://iwon.com\",\"adomain\":[\"http://myads.com\"]," +
      "\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid3\",\"crid\":\"crid3\"," +
      "\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal3\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}]," +
      "\"seat\":\"seat2\",\"group\":1,\"ext\":{\"test1\":\"data1\"}}],\"bidid\":\"bid1\",\"cur\":\"USD\"," +
      "\"customdata\":\"mydata\",\"nbr\":1,\"ext\":{\"test1\":\"data1\",\"test2arr\":[{\"test2\":\"data2\"},{" +
      "\"test2\":\"data2\"}],\"test2a\":{\"test2\":\"data2\"},\"test2b\":{\"test2\":\"data2\"},\"test3\":99," +
      "\"test4arr\":[10,20]}}";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__NOROOT_OBJECT =
      "{\"id\":\"resp1\",\"seatbid\":[{\"bid\":[{\"id\":\"bid1\",\"impid\":\"imp1\",\"price\":19.95,\"adid\":\"adid1\"," +
      "\"nurl\":\"http://iwon.com\",\"adm_native\":{\"ver\":\"1.0\",\"link\":{},\"imptrackers\":[" +
      "\"http://my.first.imp.tracker\"]},\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid1\",\"crid\":\"crid1\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal1\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat1\",\"group\":0," +
      "\"ext\":{\"test1\":\"data1\"}},{\"bid\":[{\"id\":\"bid2\",\"impid\":\"imp2\",\"price\":19.95,\"adid\":\"adid2\"," +
      "\"nurl\":\"http://iwon.com\",\"adm_native\":{\"ver\":\"2.0\",\"link\":{}," +
      "\"imptrackers\":[\"http://my.first.imp.tracker\"]},\"adomain\":[\"http://myads.com\"]," +
      "\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid2\",\"crid\":\"crid2\"," +
      "\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal2\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}},{" +
      "\"id\":\"bid2\",\"impid\":\"imp3\",\"price\":19.95,\"adid\":\"adid3\",\"nurl\":\"http://iwon.com\"," +
      "\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\"," +
      "\"cid\":\"cid3\",\"crid\":\"crid3\",\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal3\",\"w\":100,\"h\":80," +
      "\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat2\",\"group\":1,\"ext\":{\"test1\":\"data1\"}}]," +
      "\"bidid\":\"bid1\",\"cur\":\"USD\",\"customdata\":\"mydata\",\"nbr\":1,\"ext\":{\"test1\":\"data1\"," +
      "\"test2arr\":[{\"test2\":\"data2\"},{\"test2\":\"data2\"}],\"test2a\":{\"test2\":\"data2\"},\"test2b\":{" +
      "\"test2\":\"data2\"},\"test3\":99,\"test4arr\":[10,20]}}";

   /**
    * Response Json string
    * <p>
    * - containing native part as adm string field
    * <p>
    * - root native enabled
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__ROOT___STRING =
      "{\"id\":\"resp1\",\"seatbid\":[{\"bid\":[{\"id\":\"bid1\",\"impid\":\"imp1\",\"price\":19.95,\"adid\":\"adid1\"," +
      "\"nurl\":\"http://iwon.com\",\"adm\":\"{\\\"native\\\":{\\\"ver\\\":\\\"1.0\\\",\\\"link\\\":{}," +
      "\\\"imptrackers\\\":[\\\"http://my.first.imp.tracker\\\"]}}\",\"adomain\":[\"http://myads.com\"]," +
      "\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid1\",\"crid\":\"crid1\"," +
      "\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal1\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}]," +
      "\"seat\":\"seat1\",\"group\":0,\"ext\":{\"test1\":\"data1\"}},{\"bid\":[{\"id\":\"bid2\",\"impid\":\"imp2\"," +
      "\"price\":19.95,\"adid\":\"adid2\",\"nurl\":\"http://iwon.com\",\"adm\":\"{\\\"native\\\":{" +
      "\\\"ver\\\":\\\"2.0\\\",\\\"link\\\":{},\\\"imptrackers\\\":[\\\"http://my.first.imp.tracker\\\"]}}\"," +
      "\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\"," +
      "\"cid\":\"cid2\",\"crid\":\"crid2\",\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal2\",\"w\":100," +
      "\"h\":80,\"ext\":{\"test1\":\"data1\"}},{\"id\":\"bid2\",\"impid\":\"imp3\",\"price\":19.95,\"adid\":\"adid3\"," +
      "\"nurl\":\"http://iwon.com\",\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid3\",\"crid\":\"crid3\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal3\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat2\",\"group\":1," +
      "\"ext\":{\"test1\":\"data1\"}}],\"bidid\":\"bid1\",\"cur\":\"USD\",\"customdata\":\"mydata\",\"nbr\":1," +
      "\"ext\":{\"test1\":\"data1\",\"test2arr\":[{\"test2\":\"data2\"},{\"test2\":\"data2\"}],\"test2a\":{" +
      "\"test2\":\"data2\"},\"test2b\":{\"test2\":\"data2\"},\"test3\":99,\"test4arr\":[10,20]}}";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - root native enabled
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__ROOT___OBJECT =
      "{\"id\":\"resp1\",\"seatbid\":[{\"bid\":[{\"id\":\"bid1\",\"impid\":\"imp1\",\"price\":19.95,\"adid\":\"adid1\"," +
      "\"nurl\":\"http://iwon.com\",\"adm_native\":{\"native\":{\"ver\":\"1.0\",\"link\":{},\"imptrackers\":[" +
      "\"http://my.first.imp.tracker\"]}},\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid1\",\"crid\":\"crid1\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal1\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}],\"seat\":\"seat1\",\"group\":0," +
      "\"ext\":{\"test1\":\"data1\"}},{\"bid\":[{\"id\":\"bid2\",\"impid\":\"imp2\",\"price\":19.95,\"adid\":\"adid2\"," +
      "\"nurl\":\"http://iwon.com\",\"adm_native\":{\"native\":{\"ver\":\"2.0\",\"link\":{},\"imptrackers\":[" +
      "\"http://my.first.imp.tracker\"]}},\"adomain\":[\"http://myads.com\"],\"bundle\":\"com.google.testapp\"," +
      "\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid2\",\"crid\":\"crid2\",\"cat\":[\"IAB10-2\"],\"attr\":[12]," +
      "\"dealid\":\"deal2\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}},{\"id\":\"bid2\",\"impid\":\"imp3\"," +
      "\"price\":19.95,\"adid\":\"adid3\",\"nurl\":\"http://iwon.com\",\"adomain\":[\"http://myads.com\"]," +
      "\"bundle\":\"com.google.testapp\",\"iurl\":\"http://mycdn.com/ad.gif\",\"cid\":\"cid3\",\"crid\":\"crid3\"," +
      "\"cat\":[\"IAB10-2\"],\"attr\":[12],\"dealid\":\"deal3\",\"w\":100,\"h\":80,\"ext\":{\"test1\":\"data1\"}}]," +
      "\"seat\":\"seat2\",\"group\":1,\"ext\":{\"test1\":\"data1\"}}],\"bidid\":\"bid1\",\"cur\":\"USD\"," +
      "\"customdata\":\"mydata\",\"nbr\":1,\"ext\":{\"test1\":\"data1\",\"test2arr\":[{\"test2\":\"data2\"},{" +
      "\"test2\":\"data2\"}],\"test2a\":{\"test2\":\"data2\"},\"test2b\":{\"test2\":\"data2\"},\"test3\":99," +
      "\"test4arr\":[10,20]}}";

   private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OpenRtbJsonResponseHelper.class);

   public static void main(String[] args) throws IOException
   {
      logger.info("RESPONSE_SHORT_NOROOT_STRING = " + generateJson(false, false, false));
      logger.info("RESPONSE_SHORT_NOROOT_OBJECT = " + generateJson(false, false, true));
      logger.info("RESPONSE_SHORT_ROOT___STRING = " + generateJson(false, true, false));
      logger.info("RESPONSE_SHORT_ROOT___OBJECT = " + generateJson(false, true, true));
      logger.info("RESPONSE_FULL__NOROOT_STRING = " + generateJson(true, false, false));
      logger.info("RESPONSE_FULL__NOROOT_OBJECT = " + generateJson(true, false, true));
      logger.info("RESPONSE_FULL__ROOT___STRING = " + generateJson(true, true, false));
      logger.info("RESPONSE_FULL__ROOT___OBJECT = " + generateJson(true, true, true));
   }

   /**
    * Json generator method, using these Parameters:
    * @param isFull true, if nearly all fields should be filled; just some selected fields otherwise
    * @param isRootNative true, if the "native" field should be included as root element
    * @param isNativeObject true, if the native part should be generated as Json object; String otherwise
    * @return not pretty printed String representation of Json
    */
   private static String generateJson(final boolean isFull, final boolean isRootNative, final boolean isNativeObject)
      throws IOException
   {
      return generateResponse(isFull, isRootNative, isNativeObject);
   }

   private static String generateResponse(final boolean isFull, final boolean isRootNative,
                                          final boolean isNativeObject) throws IOException
   {
      return isFull ? generateFullResponse(isRootNative, isNativeObject) :
             generateShortResponse(isRootNative, isNativeObject);
   }

   private static String generateShortResponse(final boolean isRootNative, final boolean isNativeObject)
      throws IOException
   {
      final OpenRtb.BidResponse.SeatBid.Bid.Builder seatBidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
      final OpenRtb.BidResponse.SeatBid.Builder seatBuilder = OpenRtb.BidResponse.SeatBid.newBuilder();
      final OpenRtb.NativeResponse.Builder nativeResponseBuilder = OpenRtb.NativeResponse.newBuilder();

      final OpenRtb.BidResponse.Builder bidResponseBuilder = OpenRtb.BidResponse.newBuilder();

      seatBidBuilder.setId("bid")
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

      nativeResponseBuilder.setVer("1.0")
                           .setLink(OpenRtb.NativeResponse.Link.newBuilder())
                           .addImptrackers("http://my.imp.tracker");

      if(isNativeObject)
      {
         seatBidBuilder.setAdmNative(nativeResponseBuilder);
      }
      else
      {
         seatBidBuilder.setAdm(OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                                       .newNativeWriter()
                                                       .writeNativeResponse(nativeResponseBuilder.build()));
      }

      seatBuilder.addBid(seatBidBuilder).setSeat("seat");

      bidResponseBuilder.setId("resp")
                        .addSeatbid(seatBuilder)
                        .setBidid("bid")
                        .setCur("USD")
                        .setCustomdata("mydata")
                        .setNbr(OpenRtb.BidResponse.NoBidReason.TECHNICAL_ERROR);

      return OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                     .newWriter()
                                     .writeBidResponse(bidResponseBuilder.build());

   }

   private static String generateFullResponse(final boolean isRootNative, final boolean isNativeObject)
      throws IOException
   {
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

      firstSeatBidBuilder.setId("bid1")
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

      firstNativeResponseBuilder.setVer("1.0")
                                .setLink(OpenRtb.NativeResponse.Link.newBuilder())
                                .addImptrackers("http://my.first.imp.tracker");

      secondSeatBidBuilder.setId("bid2")
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

      secondNativeResponseBuilder.setVer("2.0")
                                 .setLink(OpenRtb.NativeResponse.Link.newBuilder())
                                 .addImptrackers("http://my.first.imp.tracker");

      thirdSeatBidBuilder.setId("bid2")
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

      thirdNativeResponseBuilder.setVer("3.0")
                                .setLink(OpenRtb.NativeResponse.Link.newBuilder())
                                .addImptrackers("http://my.third.imp.tracker");

      if(isNativeObject)
      {
         firstSeatBidBuilder.setAdmNative(firstNativeResponseBuilder);
         secondSeatBidBuilder.setAdmNative(secondNativeResponseBuilder);
      }
      else
      {
         firstSeatBidBuilder.setAdm(OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                                            .newNativeWriter()
                                                            .writeNativeResponse(firstNativeResponseBuilder.build()));
         secondSeatBidBuilder.setAdm(OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                                             .newNativeWriter()
                                                             .writeNativeResponse(secondNativeResponseBuilder.build()));
      }

      firstSeatBuilder.addBid(firstSeatBidBuilder)
                      .setSeat("seat1")
                      .setGroup(false)
                      .setExtension(TestExt.testSeat, OpenRtbJsonFactoryHelper.test1);

      seatBidArrayList.add(secondSeatBidBuilder.build());
      seatBidArrayList.add(thirdSeatBidBuilder.build());

      secondSeatBuilder.addAllBid(seatBidArrayList)
                       .setSeat("seat2")
                       .setGroup(true)
                       .setExtension(TestExt.testSeat, OpenRtbJsonFactoryHelper.test1);

      seatArrayList.add(firstSeatBuilder.build());
      seatArrayList.add(secondSeatBuilder.build());

      bidResponseBuilder.setId("resp1")
                        .addAllSeatbid(seatArrayList)
                        .setBidid("bid1")
                        .setCur("USD")
                        .setCustomdata("mydata")
                        .setNbr(OpenRtb.BidResponse.NoBidReason.TECHNICAL_ERROR)
                        .setExtension(TestExt.testResponse1, OpenRtbJsonFactoryHelper.test1)
                        .addExtension(TestExt.testResponse2, OpenRtbJsonFactoryHelper.test2)
                        .addExtension(TestExt.testResponse2, OpenRtbJsonFactoryHelper.test2)
                        .setExtension(TestExt.testResponse2A, OpenRtbJsonFactoryHelper.test2)
                        .setExtension(TestExt.testResponse2B, OpenRtbJsonFactoryHelper.test2)
                        .setExtension(TestExt.testResponse3, 99)
                        .addExtension(TestExt.testResponse4, 10)
                        .addExtension(TestExt.testResponse4, 20);

      return OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                     .newWriter()
                                     .writeBidResponse(bidResponseBuilder.build());
   }

}
