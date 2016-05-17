package com.google.openrtb.json;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.TestExt;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Test helper class, to be used for generating and comparing Json test data
 * <p>
 * Created by sschlegel on 12/05/16.
 */
class OpenRtbJsonRequestHelper
{
   /**
    * Request Json string containing
    * <p>
    * - native part as adm string field
    */
   static final String REQUEST__SHORT_NOROOT_STRING =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_short_false_false\",\"imp\":[{\"id\":\"imp1\",\"native\":{" +
      "\"request\":\"{\\\"ver\\\":\\\"1\\\",\\\"layout\\\":2,\\\"adunit\\\":4,\\\"plcmtcnt\\\":1,\\\"seq\\\":1}\"}," +
      "\"bidfloor\":100.0,\"bidfloorcur\":\"USD\"}],\"app\":{\"id\":\"app1\",\"name\":\"my-app-name\"," +
      "\"domain\":\"mysite.foo.com\",\"paid\":1,\"keywords\":\"my,app,key,words\"},\"device\":{" +
      "\"ua\":\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36\"," +
      "\"geo\":{\"city\":\"New York\"},\"dnt\":0,\"lmt\":1,\"ip\":\"192.168.1.0\",\"ipv6\":\"1:2:3:4:5:6:0:0\"}," +
      "\"user\":{\"id\":\"user1\",\"buyeruid\":\"buyer1\",\"gender\":\"O\",\"keywords\":\"user,builder,key,words\"," +
      "\"geo\":{\"city\":\"New York\"}}}";

   /**
    * Request Json string containing
    * <p>
    * - native part as adm_native object
    */
   static final String REQUEST__SHORT_NOROOT_OBJECT =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_short_false_true\",\"imp\":[{\"id\":\"imp1\",\"native\":{" +
      "\"request_native\":{\"ver\":\"1\",\"layout\":2,\"adunit\":4,\"plcmtcnt\":1,\"seq\":1}},\"bidfloor\":100.0," +
      "\"bidfloorcur\":\"USD\"}],\"app\":{\"id\":\"app1\",\"name\":\"my-app-name\",\"domain\":\"mysite.foo.com\"," +
      "\"paid\":1,\"keywords\":\"my,app,key,words\"},\"device\":{\"ua\":\"Mozilla/5.0 (Windows NT 6.1) " +
      "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36\",\"geo\":{\"city\":\"New York\"}," +
      "\"dnt\":0,\"lmt\":1,\"ip\":\"192.168.1.0\",\"ipv6\":\"1:2:3:4:5:6:0:0\"},\"user\":{\"id\":\"user1\"," +
      "\"buyeruid\":\"buyer1\",\"gender\":\"O\",\"keywords\":\"user,builder,key,words\",\"geo\":{" +
      "\"city\":\"New York\"}}}";

   /**
    * Request Json string containing
    * <p>
    * - native part as adm string field
    * <p>
    * - root native enabled
    */
   static final String REQUEST__SHORT_ROOT___STRING =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_short_true_false\",\"imp\":[{\"id\":\"imp1\",\"native\":{" +
      "\"request\":\"{\\\"native\\\":{\\\"ver\\\":\\\"1\\\",\\\"layout\\\":2,\\\"adunit\\\":4,\\\"plcmtcnt\\\":1," +
      "\\\"seq\\\":1}}\"},\"bidfloor\":100.0,\"bidfloorcur\":\"USD\"}],\"app\":{\"id\":\"app1\"," +
      "\"name\":\"my-app-name\",\"domain\":\"mysite.foo.com\",\"paid\":1,\"keywords\":\"my,app,key,words\"}," +
      "\"device\":{\"ua\":\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 " +
      "Safari/537.36\",\"geo\":{\"city\":\"New York\"},\"dnt\":0,\"lmt\":1,\"ip\":\"192.168.1.0\"," +
      "\"ipv6\":\"1:2:3:4:5:6:0:0\"},\"user\":{\"id\":\"user1\",\"buyeruid\":\"buyer1\",\"gender\":\"O\"," +
      "\"keywords\":\"user,builder,key,words\",\"geo\":{\"city\":\"New York\"}}}";

   /**
    * Request Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - root native enabled
    */
   static final String REQUEST__SHORT_ROOT___OBJECT =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_short_true_true\",\"imp\":[{\"id\":\"imp1\",\"native\":{" +
      "\"request_native\":{\"native\":{\"ver\":\"1\",\"layout\":2,\"adunit\":4,\"plcmtcnt\":1,\"seq\":1}}}," +
      "\"bidfloor\":100.0,\"bidfloorcur\":\"USD\"}],\"app\":{\"id\":\"app1\",\"name\":\"my-app-name\"," +
      "\"domain\":\"mysite.foo.com\",\"paid\":1,\"keywords\":\"my,app,key,words\"},\"device\":{" +
      "\"ua\":\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36\"," +
      "\"geo\":{\"city\":\"New York\"},\"dnt\":0,\"lmt\":1,\"ip\":\"192.168.1.0\",\"ipv6\":\"1:2:3:4:5:6:0:0\"}," +
      "\"user\":{\"id\":\"user1\",\"buyeruid\":\"buyer1\",\"gender\":\"O\",\"keywords\":\"user,builder,key,words\"," +
      "\"geo\":{\"city\":\"New York\"}}}";

   /**
    * Request Json string containing
    * <p>
    * - native part as adm string field
    * <p>
    * - nearly all possible fields filled
    */
   static final String REQUEST__FULL__NOROOT_STRING =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_full_false_false\",\"imp\":[{\"id\":\"imp1\",\"banner\":{" +
      "\"wmax\":300,\"hmax\":100,\"wmin\":200,\"hmin\":50,\"id\":\"banner1\",\"btype\":[3],\"battr\":[12],\"pos\":1," +
      "\"mimes\":[\"image/gif\"],\"topframe\":1,\"expdir\":[2],\"api\":[3],\"ext\":{\"test1\":\"data1\"}}," +
      "\"displaymanager\":\"dm1\",\"displaymanagerver\":\"1.0\",\"instl\":0,\"tagid\":\"tag1\",\"bidfloor\":100.0," +
      "\"bidfloorcur\":\"USD\",\"secure\":0,\"iframebuster\":[\"buster1\"],\"pmp\":{\"private_auction\":0,\"deals\":[{" +
      "\"id\":\"deal1\",\"bidfloor\":200.0,\"bidfloorcur\":\"USD\",\"wseat\":[\"seat2\"],\"wadomain\":[\"goodadv1\"]," +
      "\"at\":2,\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\"}},{" +
      "\"id\":\"imp2\",\"video\":{\"mimes\":[\"video/vp9\"],\"minduration\":15,\"maxduration\":60,\"protocol\":3," +
      "\"protocols\":[2],\"w\":200,\"h\":50,\"startdelay\":0,\"linearity\":1,\"sequence\":1,\"battr\":[12]," +
      "\"maxextended\":120,\"minbitrate\":1000,\"maxbitrate\":2000,\"boxingallowed\":0,\"playbackmethod\":[3]," +
      "\"delivery\":[1],\"pos\":1,\"companionad\":[{\"w\":100,\"h\":50,\"id\":\"compad1\"}],\"companionad\":{" +
      "\"banner\":[{\"w\":110,\"h\":60,\"id\":\"compad2\"}]},\"api\":[2],\"companiontype\":[2],\"ext\":{" +
      "\"test1\":\"data1\"}}},{\"id\":\"imp3\",\"native\":{\"request\":\"{\\\"ver\\\":\\\"1\\\",\\\"layout\\\":2," +
      "\\\"adunit\\\":4,\\\"plcmtcnt\\\":1,\\\"seq\\\":1}\",\"ver\":\"1.0\",\"api\":[3],\"battr\":[12],\"ext\":{" +
      "\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\"}}],\"device\":{\"ua\":\"Chrome\",\"geo\":{\"lat\":90.0," +
      "\"lon\":45.0,\"type\":1,\"country\":\"USA\",\"region\":\"New York\",\"regionfips104\":\"US36\"," +
      "\"metro\":\"New York\",\"city\":\"New York City\",\"zip\":\"10000\",\"utcoffset\":3600,\"ext\":{" +
      "\"test1\":\"data1\"}},\"dnt\":0,\"lmt\":0,\"ip\":\"192.168.1.0\",\"ipv6\":\"1:2:3:4:5:6:0:0\",\"devicetype\":1," +
      "\"make\":\"Motorola\",\"model\":\"MotoX\",\"os\":\"Android\",\"osv\":\"3.2.1\",\"hwv\":\"X\",\"w\":640," +
      "\"h\":1024,\"ppi\":300,\"pxratio\":1.0,\"js\":1,\"flashver\":\"11\",\"language\":\"en\",\"carrier\":\"77777\"," +
      "\"connectiontype\":6,\"ifa\":\"999\",\"didsha1\":\"1234\",\"didmd5\":\"4321\",\"dpidsha1\":\"5678\"," +
      "\"dpidmd5\":\"8765\",\"macsha1\":\"abc\",\"macmd5\":\"xyz\",\"ext\":{\"test1\":\"data1\"}},\"user\":{" +
      "\"id\":\"user1\",\"buyeruid\":\"Picard\",\"yob\":1973,\"gender\":\"M\",\"keywords\":\"boldly,going\"," +
      "\"customdata\":\"data1\",\"geo\":{\"zip\":\"12345\"},\"data\":[{\"id\":\"data1\",\"name\":\"dataname1\"," +
      "\"segment\":[{\"id\":\"seg1\",\"name\":\"segname1\",\"value\":\"segval1\",\"ext\":{\"test1\":\"data1\"}}]," +
      "\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}},\"test\":0,\"at\":2,\"tmax\":100,\"wseat\":[" +
      "\"seat1\"],\"allimps\":0,\"cur\":[\"USD\"],\"bcat\":[\"IAB11\",\"IAB11-4\"],\"badv\":[\"badguy\"],\"regs\":{" +
      "\"coppa\":1,\"ext\":{\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\",\"test2ext\":{\"test2\":\"data2\"}}}";

   /**
    * Request Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - nearly all possible fields filled
    */
   static final String REQUEST__FULL__NOROOT_OBJECT =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_full_false_true\",\"imp\":[{\"id\":\"imp1\",\"banner\":{\"wmax\":300," +
      "\"hmax\":100,\"wmin\":200,\"hmin\":50,\"id\":\"banner1\",\"btype\":[3],\"battr\":[12],\"pos\":1,\"mimes\":[" +
      "\"image/gif\"],\"topframe\":1,\"expdir\":[2],\"api\":[3],\"ext\":{\"test1\":\"data1\"}}," +
      "\"displaymanager\":\"dm1\",\"displaymanagerver\":\"1.0\",\"instl\":0,\"tagid\":\"tag1\",\"bidfloor\":100.0," +
      "\"bidfloorcur\":\"USD\",\"secure\":0,\"iframebuster\":[\"buster1\"],\"pmp\":{\"private_auction\":0," +
      "\"deals\":[{\"id\":\"deal1\",\"bidfloor\":200.0,\"bidfloorcur\":\"USD\",\"wseat\":[\"seat2\"],\"wadomain\":[" +
      "\"goodadv1\"],\"at\":2,\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}},\"ext\":{" +
      "\"test1\":\"data1\"}},{\"id\":\"imp2\",\"video\":{\"mimes\":[\"video/vp9\"],\"minduration\":15," +
      "\"maxduration\":60,\"protocol\":3,\"protocols\":[2],\"w\":200,\"h\":50,\"startdelay\":0,\"linearity\":1," +
      "\"sequence\":1,\"battr\":[12],\"maxextended\":120,\"minbitrate\":1000,\"maxbitrate\":2000,\"boxingallowed\":0," +
      "\"playbackmethod\":[3],\"delivery\":[1],\"pos\":1,\"companionad\":[{\"w\":100,\"h\":50,\"id\":\"compad1\"}]," +
      "\"companionad\":{\"banner\":[{\"w\":110,\"h\":60,\"id\":\"compad2\"}]},\"api\":[2],\"companiontype\":[2]," +
      "\"ext\":{\"test1\":\"data1\"}}},{\"id\":\"imp3\",\"native\":{\"request_native\":{\"ver\":\"1\",\"layout\":2," +
      "\"adunit\":4,\"plcmtcnt\":1,\"seq\":1},\"ver\":\"1.0\",\"api\":[3],\"battr\":[12],\"ext\":{\"test1\":\"data1\"}}," +
      "\"ext\":{\"test1\":\"data1\"}}],\"device\":{\"ua\":\"Chrome\",\"geo\":{\"lat\":90.0,\"lon\":45.0,\"type\":1," +
      "\"country\":\"USA\",\"region\":\"New York\",\"regionfips104\":\"US36\",\"metro\":\"New York\"," +
      "\"city\":\"New York City\",\"zip\":\"10000\",\"utcoffset\":3600,\"ext\":{\"test1\":\"data1\"}},\"dnt\":0," +
      "\"lmt\":0,\"ip\":\"192.168.1.0\",\"ipv6\":\"1:2:3:4:5:6:0:0\",\"devicetype\":1,\"make\":\"Motorola\"," +
      "\"model\":\"MotoX\",\"os\":\"Android\",\"osv\":\"3.2.1\",\"hwv\":\"X\",\"w\":640,\"h\":1024,\"ppi\":300," +
      "\"pxratio\":1.0,\"js\":1,\"flashver\":\"11\",\"language\":\"en\",\"carrier\":\"77777\",\"connectiontype\":6," +
      "\"ifa\":\"999\",\"didsha1\":\"1234\",\"didmd5\":\"4321\",\"dpidsha1\":\"5678\",\"dpidmd5\":\"8765\"," +
      "\"macsha1\":\"abc\",\"macmd5\":\"xyz\",\"ext\":{\"test1\":\"data1\"}},\"user\":{\"id\":\"user1\"," +
      "\"buyeruid\":\"Picard\",\"yob\":1973,\"gender\":\"M\",\"keywords\":\"boldly,going\",\"customdata\":\"data1\"," +
      "\"geo\":{\"zip\":\"12345\"},\"data\":[{\"id\":\"data1\",\"name\":\"dataname1\",\"segment\":[{\"id\":\"seg1\"," +
      "\"name\":\"segname1\",\"value\":\"segval1\",\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}}]," +
      "\"ext\":{\"test1\":\"data1\"}},\"test\":0,\"at\":2,\"tmax\":100,\"wseat\":[\"seat1\"],\"allimps\":0," +
      "\"cur\":[\"USD\"],\"bcat\":[\"IAB11\",\"IAB11-4\"],\"badv\":[\"badguy\"],\"regs\":{\"coppa\":1,\"ext\":{" +
      "\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\",\"test2ext\":{\"test2\":\"data2\"}}}";

   /**
    * Request Json string
    * <p>
    * - containing native part as adm string field
    * <p>
    * - root native enabled
    * <p>
    * - nearly all possible fields filled
    */
   static final String REQUEST__FULL__ROOT___STRING =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_full_true_false\",\"imp\":[{\"id\":\"imp1\",\"banner\":{" +
      "\"wmax\":300,\"hmax\":100,\"wmin\":200,\"hmin\":50,\"id\":\"banner1\",\"btype\":[3],\"battr\":[12],\"pos\":1," +
      "\"mimes\":[\"image/gif\"],\"topframe\":1,\"expdir\":[2],\"api\":[3],\"ext\":{\"test1\":\"data1\"}}," +
      "\"displaymanager\":\"dm1\",\"displaymanagerver\":\"1.0\",\"instl\":0,\"tagid\":\"tag1\",\"bidfloor\":100.0," +
      "\"bidfloorcur\":\"USD\",\"secure\":0,\"iframebuster\":[\"buster1\"],\"pmp\":{\"private_auction\":0,\"deals\":[{" +
      "\"id\":\"deal1\",\"bidfloor\":200.0,\"bidfloorcur\":\"USD\",\"wseat\":[\"seat2\"],\"wadomain\":[\"goodadv1\"]," +
      "\"at\":2,\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\"}},{" +
      "\"id\":\"imp2\",\"video\":{\"mimes\":[\"video/vp9\"],\"minduration\":15,\"maxduration\":60,\"protocol\":3," +
      "\"protocols\":[2],\"w\":200,\"h\":50,\"startdelay\":0,\"linearity\":1,\"sequence\":1,\"battr\":[12]," +
      "\"maxextended\":120,\"minbitrate\":1000,\"maxbitrate\":2000,\"boxingallowed\":0,\"playbackmethod\":[3]," +
      "\"delivery\":[1],\"pos\":1,\"companionad\":[{\"w\":100,\"h\":50,\"id\":\"compad1\"}],\"companionad\":{" +
      "\"banner\":[{\"w\":110,\"h\":60,\"id\":\"compad2\"}]},\"api\":[2],\"companiontype\":[2],\"ext\":{" +
      "\"test1\":\"data1\"}}},{\"id\":\"imp3\",\"native\":{\"request\":\"{\\\"native\\\":{\\\"ver\\\":\\\"1\\\"," +
      "\\\"layout\\\":2,\\\"adunit\\\":4,\\\"plcmtcnt\\\":1,\\\"seq\\\":1}}\",\"ver\":\"1.0\",\"api\":[3]," +
      "\"battr\":[12],\"ext\":{\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\"}}],\"device\":{\"ua\":\"Chrome\"," +
      "\"geo\":{\"lat\":90.0,\"lon\":45.0,\"type\":1,\"country\":\"USA\",\"region\":\"New York\"," +
      "\"regionfips104\":\"US36\",\"metro\":\"New York\",\"city\":\"New York City\",\"zip\":\"10000\"," +
      "\"utcoffset\":3600,\"ext\":{\"test1\":\"data1\"}},\"dnt\":0,\"lmt\":0,\"ip\":\"192.168.1.0\"," +
      "\"ipv6\":\"1:2:3:4:5:6:0:0\",\"devicetype\":1,\"make\":\"Motorola\",\"model\":\"MotoX\",\"os\":\"Android\"," +
      "\"osv\":\"3.2.1\",\"hwv\":\"X\",\"w\":640,\"h\":1024,\"ppi\":300,\"pxratio\":1.0,\"js\":1,\"flashver\":\"11\"," +
      "\"language\":\"en\",\"carrier\":\"77777\",\"connectiontype\":6,\"ifa\":\"999\",\"didsha1\":\"1234\"," +
      "\"didmd5\":\"4321\",\"dpidsha1\":\"5678\",\"dpidmd5\":\"8765\",\"macsha1\":\"abc\",\"macmd5\":\"xyz\"," +
      "\"ext\":{\"test1\":\"data1\"}},\"user\":{\"id\":\"user1\",\"buyeruid\":\"Picard\",\"yob\":1973,\"gender\":\"M\"," +
      "\"keywords\":\"boldly,going\",\"customdata\":\"data1\",\"geo\":{\"zip\":\"12345\"},\"data\":[{\"id\":\"data1\"," +
      "\"name\":\"dataname1\",\"segment\":[{\"id\":\"seg1\",\"name\":\"segname1\",\"value\":\"segval1\",\"ext\":{" +
      "\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}},\"test\":0,\"at\":2," +
      "\"tmax\":100,\"wseat\":[\"seat1\"],\"allimps\":0,\"cur\":[\"USD\"],\"bcat\":[\"IAB11\",\"IAB11-4\"],\"badv\":[" +
      "\"badguy\"],\"regs\":{\"coppa\":1,\"ext\":{\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\",\"test2ext\":{" +
      "\"test2\":\"data2\"}}}";

   /**
    * Request Json string containing
    * <p>
    * - native part as adm_native object
    * <p>
    * - root native enabled
    * <p>
    * - nearly all possible fields filled
    */
   static final String REQUEST__FULL__ROOT___OBJECT =
      "{\"id\":\"9zj61whbdl319sjgz098lpys5cngmtro_full_true_true\",\"imp\":[{\"id\":\"imp1\",\"banner\":{\"wmax\":300," +
      "\"hmax\":100,\"wmin\":200,\"hmin\":50,\"id\":\"banner1\",\"btype\":[3],\"battr\":[12],\"pos\":1,\"mimes\":[" +
      "\"image/gif\"],\"topframe\":1,\"expdir\":[2],\"api\":[3],\"ext\":{\"test1\":\"data1\"}}," +
      "\"displaymanager\":\"dm1\",\"displaymanagerver\":\"1.0\",\"instl\":0,\"tagid\":\"tag1\",\"bidfloor\":100.0," +
      "\"bidfloorcur\":\"USD\",\"secure\":0,\"iframebuster\":[\"buster1\"],\"pmp\":{\"private_auction\":0," +
      "\"deals\":[{\"id\":\"deal1\",\"bidfloor\":200.0,\"bidfloorcur\":\"USD\",\"wseat\":[\"seat2\"]," +
      "\"wadomain\":[\"goodadv1\"],\"at\":2,\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}}," +
      "\"ext\":{\"test1\":\"data1\"}},{\"id\":\"imp2\",\"video\":{\"mimes\":[\"video/vp9\"],\"minduration\":15," +
      "\"maxduration\":60,\"protocol\":3,\"protocols\":[2],\"w\":200,\"h\":50,\"startdelay\":0,\"linearity\":1," +
      "\"sequence\":1,\"battr\":[12],\"maxextended\":120,\"minbitrate\":1000,\"maxbitrate\":2000,\"boxingallowed\":0," +
      "\"playbackmethod\":[3],\"delivery\":[1],\"pos\":1,\"companionad\":[{\"w\":100,\"h\":50,\"id\":\"compad1\"}]," +
      "\"companionad\":{\"banner\":[{\"w\":110,\"h\":60,\"id\":\"compad2\"}]},\"api\":[2],\"companiontype\":[2]," +
      "\"ext\":{\"test1\":\"data1\"}}},{\"id\":\"imp3\",\"native\":{\"request_native\":{\"native\":{\"ver\":\"1\"," +
      "\"layout\":2,\"adunit\":4,\"plcmtcnt\":1,\"seq\":1}},\"ver\":\"1.0\",\"api\":[3],\"battr\":[12],\"ext\":{" +
      "\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\"}}],\"device\":{\"ua\":\"Chrome\",\"geo\":{\"lat\":90.0," +
      "\"lon\":45.0,\"type\":1,\"country\":\"USA\",\"region\":\"New York\",\"regionfips104\":\"US36\"," +
      "\"metro\":\"New York\",\"city\":\"New York City\",\"zip\":\"10000\",\"utcoffset\":3600,\"ext\":{" +
      "\"test1\":\"data1\"}},\"dnt\":0,\"lmt\":0,\"ip\":\"192.168.1.0\",\"ipv6\":\"1:2:3:4:5:6:0:0\",\"devicetype\":1," +
      "\"make\":\"Motorola\",\"model\":\"MotoX\",\"os\":\"Android\",\"osv\":\"3.2.1\",\"hwv\":\"X\",\"w\":640," +
      "\"h\":1024,\"ppi\":300,\"pxratio\":1.0,\"js\":1,\"flashver\":\"11\",\"language\":\"en\",\"carrier\":\"77777\"," +
      "\"connectiontype\":6,\"ifa\":\"999\",\"didsha1\":\"1234\",\"didmd5\":\"4321\",\"dpidsha1\":\"5678\"," +
      "\"dpidmd5\":\"8765\",\"macsha1\":\"abc\",\"macmd5\":\"xyz\",\"ext\":{\"test1\":\"data1\"}},\"user\":{" +
      "\"id\":\"user1\",\"buyeruid\":\"Picard\",\"yob\":1973,\"gender\":\"M\",\"keywords\":\"boldly,going\"," +
      "\"customdata\":\"data1\",\"geo\":{\"zip\":\"12345\"},\"data\":[{\"id\":\"data1\",\"name\":\"dataname1\"," +
      "\"segment\":[{\"id\":\"seg1\",\"name\":\"segname1\",\"value\":\"segval1\",\"ext\":{\"test1\":\"data1\"}}]," +
      "\"ext\":{\"test1\":\"data1\"}}],\"ext\":{\"test1\":\"data1\"}},\"test\":0,\"at\":2,\"tmax\":100,\"wseat\":[" +
      "\"seat1\"],\"allimps\":0,\"cur\":[\"USD\"],\"bcat\":[\"IAB11\",\"IAB11-4\"],\"badv\":[\"badguy\"],\"regs\":{" +
      "\"coppa\":1,\"ext\":{\"test1\":\"data1\"}},\"ext\":{\"test1\":\"data1\",\"test2ext\":{\"test2\":\"data2\"}}}";

   private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OpenRtbJsonRequestHelper.class);

   public static void main(String[] args) throws IOException
   {
      logger.info("REQUEST__SHORT_NOROOT_STRING = " + generateJson(false, false, false));
      logger.info("REQUEST__SHORT_NOROOT_OBJECT = " + generateJson(false, false, true));
      logger.info("REQUEST__SHORT_ROOT___STRING = " + generateJson(false, true, false));
      logger.info("REQUEST__SHORT_ROOT___OBJECT = " + generateJson(false, true, true));
      logger.info("REQUEST__FULL__NOROOT_STRING = " + generateJson(true, false, false));
      logger.info("REQUEST__FULL__NOROOT_OBJECT = " + generateJson(true, false, true));
      logger.info("REQUEST__FULL__ROOT___STRING = " + generateJson(true, true, false));
      logger.info("REQUEST__FULL__ROOT___OBJECT = " + generateJson(true, true, true));
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
      return generateRequest(isFull, isRootNative, isNativeObject);
   }

   private static String generateRequest(final boolean isFull, final boolean isRootNative, final boolean isNativeObject)
      throws IOException
   {
      return isFull ? generateFullRequest(isRootNative, isNativeObject) :
             generateShortRequest(isRootNative, isNativeObject);
   }

   private static String generateShortRequest(final boolean isRootNative, final boolean isNativeObject)
      throws IOException
   {
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

      if(isNativeObject)
      {
         nativeBuilder.setRequestNative(generateNativeRequest());
      }
      else
      {
         nativeBuilder.setRequest(OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                                          .newNativeWriter()
                                                          .writeNativeRequest(generateNativeRequest().build()));
      }
      impressionBuilder.setId("imp1").setBidfloor(100.0).setBidfloorcur("USD").setNative(nativeBuilder);

      siteBuilder.setId("site1")
                 .setDomain("mysite.foo.com")
                 .setPage("http://mysite.foo.com/my/link")
                 .setMobile(false)
                 .setKeywords("my,key,words");

      appBuilder.setId("app1")
                .setName("my-app-name")
                .setDomain("mysite.foo.com")
                .setPaid(true)
                .setKeywords("my,app,key,words");

      geoBuilder.setCity("New York");

      deviceBuilder.setUa(
         "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                   .setGeo(geoBuilder)
                   .setDnt(false)
                   .setLmt(true)
                   .setIp("192.168.1.0")
                   .setIpv6("1:2:3:4:5:6:0:0");

      userBuilder.setId("user1")
                 .setBuyeruid("buyer1")
                 .setGender("O")
                 .setKeywords("user,builder,key,words")
                 .setGeo(geoBuilder);

      bidRequestBuilder.setId("9zj61whbdl319sjgz098lpys5cngmtro_short_" + isRootNative + "_" + isNativeObject)
                       .addImp(impressionBuilder)
                       .setSite(siteBuilder)
                       .setApp(appBuilder)
                       .setDevice(deviceBuilder)
                       .setUser(userBuilder);

      return OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                     .newWriter()
                                     .writeBidRequest(bidRequestBuilder.build());
   }

   private static String generateFullRequest(final boolean isRootNative, final boolean isNativeObject)
      throws IOException
   {
      final OpenRtb.BidRequest.Imp.Builder firstImpressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
      final OpenRtb.BidRequest.Imp.Builder secondImpressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
      final OpenRtb.BidRequest.Imp.Builder thirdImpressionBuilder = OpenRtb.BidRequest.Imp.newBuilder();
      final OpenRtb.BidRequest.Imp.Native.Builder nativeBuilder = OpenRtb.BidRequest.Imp.Native.newBuilder();
      final OpenRtb.BidRequest.Device.Builder deviceBuilder = OpenRtb.BidRequest.Device.newBuilder();
      final OpenRtb.BidRequest.User.Builder userBuilder = OpenRtb.BidRequest.User.newBuilder();
      final OpenRtb.BidRequest.Regs.Builder regsBuilder = OpenRtb.BidRequest.Regs.newBuilder();
      final OpenRtb.BidRequest.Builder bidRequestBuilder = OpenRtb.BidRequest.newBuilder();

      firstImpressionBuilder.setId("imp1")
                            .setBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder()
                                                                    .setWmax(300)
                                                                    .setWmin(200)
                                                                    .setHmax(100)
                                                                    .setHmin(50)
                                                                    .setId("banner1")
                                                                    .setPos(
                                                                       OpenRtb.BidRequest.Imp.AdPosition.ABOVE_THE_FOLD)
                                                                    .addBtype(
                                                                       OpenRtb.BidRequest.Imp.Banner.BannerAdType.JAVASCRIPT_AD)
                                                                    .addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
                                                                    .addMimes("image/gif")
                                                                    .setTopframe(true)
                                                                    .addExpdir(
                                                                       OpenRtb.BidRequest.Imp.Banner.ExpandableDirection.RIGHT)
                                                                    .addApi(OpenRtb.BidRequest.Imp.APIFramework.MRAID_1)
                                                                    .setExtension(TestExt.testBanner,
                                                                                  OpenRtbJsonFactoryHelper.test1))
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
                                                                                                       .setBidfloor(
                                                                                                          200.0)
                                                                                                       .setBidfloorcur(
                                                                                                          "USD")
                                                                                                       .addWseat(
                                                                                                          "seat2")
                                                                                                       .addWadomain(
                                                                                                          "goodadv1")
                                                                                                       .setAt(
                                                                                                          OpenRtb.BidRequest.AuctionType.SECOND_PRICE)
                                                                                                       .setExtension(
                                                                                                          TestExt.testDeal,
                                                                                                          OpenRtbJsonFactoryHelper.test1))
                                                              .setExtension(TestExt.testPmp,
                                                                            OpenRtbJsonFactoryHelper.test1))
                            .setExtension(TestExt.testImp, OpenRtbJsonFactoryHelper.test1);

      secondImpressionBuilder.setId("imp2")
                             .setVideo(OpenRtb.BidRequest.Imp.Video.newBuilder()
                                                                   .addMimes("video/vp9")
                                                                   .setLinearity(
                                                                      OpenRtb.BidRequest.Imp.Video.VideoLinearity.LINEAR)
                                                                   .setMinduration(15)
                                                                   .setMaxduration(60)
                                                                   .setProtocol(
                                                                      OpenRtb.BidRequest.Imp.Video.VideoBidResponseProtocol.VAST_3_0)
                                                                   .addProtocols(
                                                                      OpenRtb.BidRequest.Imp.Video.VideoBidResponseProtocol.VAST_2_0)
                                                                   .setW(200)
                                                                   .setH(50)
                                                                   .setStartdelay(0)
                                                                   .setSequence(1)
                                                                   .addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
                                                                   .setMaxextended(120)
                                                                   .setMinbitrate(1000)
                                                                   .setMaxbitrate(2000)
                                                                   .setBoxingallowed(false)
                                                                   .addPlaybackmethod(
                                                                      OpenRtb.BidRequest.Imp.Video.VideoPlaybackMethod.CLICK_TO_PLAY)
                                                                   .addDelivery(
                                                                      OpenRtb.BidRequest.Imp.Video.ContentDeliveryMethod.STREAMING)
                                                                   .setPos(
                                                                      OpenRtb.BidRequest.Imp.AdPosition.ABOVE_THE_FOLD)
                                                                   .addCompanionad(
                                                                      OpenRtb.BidRequest.Imp.Banner.newBuilder()
                                                                                                   .setId("compad1")
                                                                                                   .setW(100)
                                                                                                   .setH(50))
                                                                   .setCompanionad21(
                                                                      OpenRtb.BidRequest.Imp.Video.CompanionAd.newBuilder()
                                                                                                              .addBanner(
                                                                                                                 OpenRtb.BidRequest.Imp.Banner
                                                                                                                    .newBuilder()
                                                                                                                    .setId(
                                                                                                                       "compad2")
                                                                                                                    .setW(
                                                                                                                       110)
                                                                                                                    .setH(
                                                                                                                       60)))
                                                                   .addApi(OpenRtb.BidRequest.Imp.APIFramework.VPAID_2)
                                                                   .addCompaniontype(
                                                                      OpenRtb.BidRequest.Imp.Video.VASTCompanionType.HTML)
                                                                   .setExtension(TestExt.testVideo,
                                                                                 OpenRtbJsonFactoryHelper.test1));

      if(isNativeObject)
      {
         nativeBuilder.setRequestNative(generateNativeRequest());
      }
      else
      {
         nativeBuilder.setRequest(OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                                          .newNativeWriter()
                                                          .writeNativeRequest(generateNativeRequest().build()));
      }

      nativeBuilder.setVer("1.0")
                   .addApi(OpenRtb.BidRequest.Imp.APIFramework.MRAID_1)
                   .addBattr(OpenRtb.CreativeAttribute.TEXT_ONLY)
                   .setExtension(TestExt.testNative, OpenRtbJsonFactoryHelper.test1);

      thirdImpressionBuilder.setId("imp3")
                            .setNative(nativeBuilder)
                            .setExtension(TestExt.testImp, OpenRtbJsonFactoryHelper.test1);

      deviceBuilder.setUa("Chrome")
                   .setGeo(OpenRtb.BidRequest.Geo.newBuilder()
                                                 .setLat(90.0)
                                                 .setLon(45.0)
                                                 .setType(OpenRtb.BidRequest.Geo.LocationType.GPS_LOCATION)
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
                   .setDevicetype(OpenRtb.BidRequest.Device.DeviceType.MOBILE)
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
                   .setConnectiontype(OpenRtb.BidRequest.Device.ConnectionType.CELL_4G)
                   .setIfa("999")
                   .setDidsha1("1234")
                   .setDidmd5("4321")
                   .setDpidsha1("5678")
                   .setDpidmd5("8765")
                   .setMacsha1("abc")
                   .setMacmd5("xyz")
                   .setExtension(TestExt.testDevice, OpenRtbJsonFactoryHelper.test1);

      userBuilder.setId("user1")
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
                                                                                            .setExtension(
                                                                                               TestExt.testSegment,
                                                                                               OpenRtbJsonFactoryHelper.test1))
                                                 .setExtension(TestExt.testData, OpenRtbJsonFactoryHelper.test1))
                 .setExtension(TestExt.testUser, OpenRtbJsonFactoryHelper.test1);

      regsBuilder.setCoppa(true).setExtension(TestExt.testRegs, OpenRtbJsonFactoryHelper.test1);

      bidRequestBuilder.setId("9zj61whbdl319sjgz098lpys5cngmtro_full_" + isRootNative + "_" + isNativeObject)
                       .addImp(firstImpressionBuilder)
                       .addImp(secondImpressionBuilder)
                       .addImp(thirdImpressionBuilder)
                       .setDevice(deviceBuilder)
                       .setUser(userBuilder)
                       .setAt(OpenRtb.BidRequest.AuctionType.SECOND_PRICE)
                       .setTmax(100)
                       .addWseat("seat1")
                       .setAllimps(false)
                       .addCur("USD")
                       .addAllBcat(asList("IAB11", "IAB11-4"))
                       .addBadv("badguy")
                       .setRegs(regsBuilder)
                       .setTest(false)
                       .setExtension(TestExt.testRequest2, OpenRtbJsonFactoryHelper.test2)
                       .setExtension(TestExt.testRequest1, OpenRtbJsonFactoryHelper.test1);

      return OpenRtbJsonFactoryHelper.newJsonFactory(isRootNative)
                                     .newWriter()
                                     .writeBidRequest(bidRequestBuilder.build());
   }

   private static OpenRtb.NativeRequest.Builder generateNativeRequest() throws IOException
   {
      final OpenRtb.NativeRequest.Builder nativeRequest = OpenRtb.NativeRequest.newBuilder();
      nativeRequest.setVer("1")
                   .setLayout(OpenRtb.NativeRequest.LayoutId.APP_WALL)
                   .setAdunit(OpenRtb.NativeRequest.AdUnitId.IAB_IN_AD_NATIVE)
                   .setPlcmtcnt(1)
                   .setSeq(1);
      return nativeRequest;
   }
}


