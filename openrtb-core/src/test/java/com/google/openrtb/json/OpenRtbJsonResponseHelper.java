package com.google.openrtb.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.Test;
import com.google.openrtb.TestExt;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
   static final String RESPONSE_SHORT_NOROOT_STRING = "";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    */
   static final String RESPONSE_SHORT_NOROOT_OBJECT = "";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm string field
    * <p>
    * - root native enabled
    */
   static final String RESPONSE_SHORT_ROOT___STRING = "";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - root native enabled
    */
   static final String RESPONSE_SHORT_ROOT___OBJECT = "";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm string field
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__NOROOT_STRING = "";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__NOROOT_OBJECT = "";

   /**
    * Response Json string
    * <p>
    * - containing native part as adm string field
    * <p>
    * - root native enabled
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__ROOT___STRING = "";

   /**
    * Response Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - root native enabled
    * <p>
    * - nearly all possible fields filled
    */
   static final String RESPONSE_FULL__ROOT___OBJECT = "";

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
                                          final boolean isNativeObject)
   {
      return isFull ? generateFullResponse(isRootNative, isNativeObject) :
             generateShortResponse(isRootNative, isNativeObject);
   }

   private static String generateShortResponse(final boolean isRootNative, final boolean isNativeObject)
   {

      return null;
   }

   private static String generateFullResponse(final boolean isRootNative, final boolean isNativeObject)
   {
      return null;
   }

   static OpenRtbJsonFactory newJsonFactory(final boolean isRootNative)
   {
      return OpenRtbJsonFactory.create()
                               .setRootNativeField(isRootNative)
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

}
