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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeCsvString;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeEnums;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeIntBoolField;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeRequiredEnums;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeRequiredStrings;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeStrings;

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Data.Segment;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Native;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP.Deal;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regulations;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Serializes OpenRTB {@link BidRequest}/{@link BidResponse} messages to JSON.
 * <p>
 * Note: Among methods that write to a {@link JsonGenerator} parameter, only the {@code public}
 * methods will call {@code flush()} on the generator before returning.
 * <p>
 * This class is threadsafe.
 */
public class OpenRtbJsonWriter extends AbstractOpenRtbJsonWriter {
  private OpenRtbNativeJsonWriter nativeWriter;

  protected OpenRtbJsonWriter(OpenRtbJsonFactory factory) {
    super(factory);
  }

  /**
   * Serializes a {@link BidRequest} to JSON, returned as a {@code String}.
   */
  public String writeBidRequest(BidRequest req) throws IOException {
    try (StringWriter writer = new StringWriter()) {
      writeBidRequest(req, writer);
      return writer.toString();
    }
  }

  /**
   * Serializes a {@link BidRequest} to JSON, streamed into an {@link Writer}.
   *
   * @see JsonFactory#createGenerator(Writer)
   */
  public void writeBidRequest(BidRequest req, Writer writer) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(writer);
    writeBidRequest(req, gen);
  }

  /**
   * Serializes a {@link BidRequest} to JSON, streamed into an {@link OutputStream}.
   *
   * @see JsonFactory#createGenerator(OutputStream)
   */
  public void writeBidRequest(BidRequest req, OutputStream os) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(os);
    writeBidRequest(req, gen);
  }

  /**
   * Serializes a {@link BidRequest} to JSON, with a provided {@link JsonGenerator}
   * which allows several choices of output and encoding.
   */
  public void writeBidRequest(BidRequest req, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("id", req.getId());
    if (checkRequired(req.getImpCount())) {
      gen.writeArrayFieldStart("imp");
      for (Impression imp : req.getImpList()) {
        writeImpression(imp, gen);
      }
      gen.writeEndArray();
    }
    if (req.hasSite()) {
      gen.writeFieldName("site");
      writeSite(req.getSite(), gen);
    }
    if (req.hasApp()) {
      gen.writeFieldName("app");
      writeApp(req.getApp(), gen);
    }
    if (req.hasDevice()) {
      gen.writeFieldName("device");
      writeDevice(req.getDevice(), gen);
    }
    if (req.hasUser()) {
      gen.writeFieldName("user");
      writeUser(req.getUser(), gen);
    }
    if (req.hasTest()) {
      writeIntBoolField("test", req.getTest(), gen);
    }
    if (req.hasAt()) {
      gen.writeNumberField("at", req.getAt());
    }
    if (req.hasTmax()) {
      gen.writeNumberField("tmax", req.getTmax());
    }
    writeStrings("wseat", req.getWseatList(), gen);
    if (req.hasAllimps()) {
      writeIntBoolField("allimps", req.getAllimps(), gen);
    }
    writeStrings("cur", req.getCurList(), gen);
    writeStrings("bcat", req.getBcatList(), gen);
    writeStrings("badv", req.getBadvList(), gen);
    if (req.hasRegs()) {
      gen.writeFieldName("regs");
      writeRegulations(req.getRegs(), gen);
    }
    writeExtensions(req, gen);
    gen.writeEndObject();
    gen.flush();
  }

  public void writeImpression(Impression imp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("id", imp.getId());
    if (imp.hasBanner()) {
      gen.writeFieldName("banner");
      writeBanner(imp.getBanner(), gen);
    }
    if (imp.hasVideo()) {
      gen.writeFieldName("video");
      writeVideo(imp.getVideo(), gen);
    }
    if (imp.hasNative()) {
      gen.writeFieldName("native");
      writeNative(imp.getNative(), gen);
    }
    if (imp.hasDisplaymanager()) {
      gen.writeStringField("displaymanager", imp.getDisplaymanager());
    }
    if (imp.hasDisplaymanagerver()) {
      gen.writeStringField("displaymanagerver", imp.getDisplaymanagerver());
    }
    if (imp.hasInstl()) {
      writeIntBoolField("instl", imp.getInstl(), gen);
    }
    if (imp.hasTagid()) {
      gen.writeStringField("tagid", imp.getTagid());
    }
    if (imp.hasBidfloor()) {
      gen.writeNumberField("bidfloor", imp.getBidfloor());
    }
    if (imp.hasBidfloorcur()) {
      gen.writeStringField("bidfloorcur", imp.getBidfloorcur());
    }
    if (imp.hasSecure()) {
      writeIntBoolField("secure", imp.getSecure(), gen);
    }
    writeStrings("iframebuster", imp.getIframebusterList(), gen);
    if (imp.hasPmp()) {
      gen.writeFieldName("pmp");
      writePMP(imp.getPmp(), gen);
    }
    writeExtensions(imp, gen);
    gen.writeEndObject();
  }

  public void writeBanner(Banner banner, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (banner.hasW()) {
      gen.writeNumberField("w", banner.getW());
    }
    if (banner.hasH()) {
      gen.writeNumberField("h", banner.getH());
    }
    if (banner.hasWmax()) {
      gen.writeNumberField("wmax", banner.getWmax());
    }
    if (banner.hasHmax()) {
      gen.writeNumberField("hmax", banner.getHmax());
    }
    if (banner.hasWmin()) {
      gen.writeNumberField("wmin", banner.getWmin());
    }
    if (banner.hasHmin()) {
      gen.writeNumberField("hmin", banner.getHmin());
    }
    if (banner.hasId()) {
      gen.writeStringField("id", banner.getId());
    }
    writeEnums("btype", banner.getBtypeList(), gen);
    writeEnums("battr", banner.getBattrList(), gen);
    if (banner.hasPos()) {
      gen.writeNumberField("pos", banner.getPos().getNumber());
    }
    writeStrings("mimes", banner.getMimesList(), gen);
    if (banner.hasTopframe()) {
      writeIntBoolField("topframe", banner.getTopframe(), gen);
    }
    writeEnums("expdir", banner.getExpdirList(), gen);
    writeEnums("api", banner.getApiList(), gen);
    writeExtensions(banner, gen);
    gen.writeEndObject();
  }

  @SuppressWarnings("deprecation")
  public void writeVideo(Video video, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (checkRequired(video.getMimesCount())) {
      writeRequiredStrings("mimes", video.getMimesList(), gen);
    }
    if (video.hasMinduration()) {
      gen.writeNumberField("minduration", video.getMinduration());
    }
    if (video.hasMaxduration()) {
      gen.writeNumberField("maxduration", video.getMaxduration());
    }
    if (video.hasProtocol()) {
      gen.writeNumberField("protocol", video.getProtocol().getNumber());
    }
    if (checkRequired(video.getProtocolsCount())) {
      writeRequiredEnums("protocols", video.getProtocolsList(), gen);
    }
    if (video.hasW()) {
      gen.writeNumberField("w", video.getW());
    }
    if (video.hasH()) {
      gen.writeNumberField("h", video.getH());
    }
    if (video.hasStartdelay()) {
      gen.writeNumberField("startdelay", video.getStartdelay());
    }
    if (video.hasLinearity()) {
      gen.writeNumberField("linearity", video.getLinearity().getNumber());
    }
    if (video.hasSequence()) {
      gen.writeNumberField("sequence", video.getSequence());
    }
    writeEnums("battr", video.getBattrList(), gen);
    if (video.hasMaxextended()) {
      gen.writeNumberField("maxextended", video.getMaxextended());
    }
    if (video.hasMinbitrate()) {
      gen.writeNumberField("minbitrate", video.getMinbitrate());
    }
    if (video.hasMaxbitrate()) {
      gen.writeNumberField("maxbitrate", video.getMaxbitrate());
    }
    if (video.hasBoxingallowed()) {
      writeIntBoolField("boxingallowed", video.getBoxingallowed(), gen);
    }
    writeEnums("playbackmethod", video.getPlaybackmethodList(), gen);
    writeEnums("delivery", video.getDeliveryList(), gen);
    if (video.hasPos()) {
      gen.writeNumberField("pos", video.getPos().getNumber());
    }
    if (video.getCompanionadCount() != 0) {
      gen.writeArrayFieldStart("companionad");
      for (Banner companionad : video.getCompanionadList()) {
        writeBanner(companionad, gen);
      }
      gen.writeEndArray();
    }
    writeEnums("api", video.getApiList(), gen);
    writeEnums("companiontype", video.getCompaniontypeList(), gen);
    writeExtensions(video, gen);
    gen.writeEndObject();
  }

  protected final void writeNative(Native nativ, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("request", nativeWriter().writeNativeRequest(nativ.getRequest()));
    if (nativ.hasVer()) {
      gen.writeStringField("ver", nativ.getVer());
    }
    writeEnums("api", nativ.getApiList(), gen);
    writeEnums("battr", nativ.getBattrList(), gen);
    writeExtensions(nativ, gen);
    gen.writeEndObject();
  }

  public void writePMP(PMP pmp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (pmp.hasPrivateAuction()) {
      writeIntBoolField("private_auction", pmp.getPrivateAuction(), gen);
    }
    if (pmp.getDealsCount() != 0) {
      gen.writeArrayFieldStart("deals");
      for (Deal deals : pmp.getDealsList()) {
        writeDeal(deals, gen);
      }
      gen.writeEndArray();
    }
    writeExtensions(pmp, gen);
    gen.writeEndObject();
  }

  public void writeDeal(Deal deal, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("id", deal.getId());
    if (deal.hasBidfloor()) {
      gen.writeNumberField("bidfloor", deal.getBidfloor());
    }
    if (deal.hasBidfloorcur()) {
      gen.writeStringField("bidfloorcur", deal.getBidfloorcur());
    }
    writeStrings("wseat", deal.getWseatList(), gen);
    writeStrings("wadomain", deal.getWadomainList(), gen);
    if (deal.hasAt()) {
      gen.writeNumberField("at", deal.getAt());
    }
    writeExtensions(deal, gen);
    gen.writeEndObject();
  }

  public void writeSite(Site site, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (site.hasId()) {
      gen.writeStringField("id", site.getId());
    }
    if (site.hasName()) {
      gen.writeStringField("name", site.getName());
    }
    if (site.hasDomain()) {
      gen.writeStringField("domain", site.getDomain());
    }
    writeStrings("cat", site.getCatList(), gen);
    writeStrings("sectioncat", site.getSectioncatList(), gen);
    writeStrings("pagecat", site.getPagecatList(), gen);
    if (site.hasPage()) {
      gen.writeStringField("page", site.getPage());
    }
    if (site.hasRef()) {
      gen.writeStringField("ref", site.getRef());
    }
    if (site.hasSearch()) {
      gen.writeStringField("search", site.getSearch());
    }
    if (site.hasMobile()) {
      writeIntBoolField("mobile", site.getMobile(), gen);
    }
    if (site.hasPrivacypolicy()) {
      writeIntBoolField("privacypolicy", site.getPrivacypolicy(), gen);
    }
    if (site.hasPublisher()) {
      gen.writeFieldName("publisher");
      writePublisher(site.getPublisher(), gen);
    }
    if (site.hasContent()) {
      gen.writeFieldName("content");
      writeContent(site.getContent(), gen);
    }
    writeCsvString("keywords", site.getKeywordsList(), gen);
    writeExtensions(site, gen);
    gen.writeEndObject();
  }

  public void writeApp(App app, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (app.hasId()) {
      gen.writeStringField("id", app.getId());
    }
    if (app.hasName()) {
      gen.writeStringField("name", app.getName());
    }
    if (app.hasBundle()) {
      gen.writeStringField("bundle", app.getBundle());
    }
    if (app.hasDomain()) {
      gen.writeStringField("domain", app.getDomain());
    }
    if (app.hasStoreurl()) {
      gen.writeStringField("storeurl", app.getStoreurl());
    }
    writeStrings("cat", app.getCatList(), gen);
    writeStrings("sectioncat", app.getSectioncatList(), gen);
    writeStrings("pagecat", app.getPagecatList(), gen);
    if (app.hasVer()) {
      gen.writeStringField("ver", app.getVer());
    }
    if (app.hasPrivacypolicy()) {
      writeIntBoolField("privacypolicy", app.getPrivacypolicy(), gen);
    }
    if (app.hasPaid()) {
      writeIntBoolField("paid", app.getPaid(), gen);
    }
    if (app.hasPublisher()) {
      gen.writeFieldName("publisher");
      writePublisher(app.getPublisher(), gen);
    }
    if (app.hasContent()) {
      gen.writeFieldName("content");
      writeContent(app.getContent(), gen);
    }
    writeCsvString("keywords", app.getKeywordsList(), gen);
    writeExtensions(app, gen);
    gen.writeEndObject();
  }

  public void writeContent(Content content, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (content.hasId()) {
      gen.writeStringField("id", content.getId());
    }
    if (content.hasEpisode()) {
      gen.writeNumberField("episode", content.getEpisode());
    }
    if (content.hasTitle()) {
      gen.writeStringField("title", content.getTitle());
    }
    if (content.hasSeries()) {
      gen.writeStringField("series", content.getSeries());
    }
    if (content.hasSeason()) {
      gen.writeStringField("season", content.getSeason());
    }
    if (content.hasProducer()) {
      gen.writeFieldName("producer");
      writeProducer(content.getProducer(), gen);
    }
    if (content.hasUrl()) {
      gen.writeStringField("url", content.getUrl());
    }
    writeStrings("cat", content.getCatList(), gen);
    if (content.hasVideoquality()) {
      gen.writeNumberField("videoquality", content.getVideoquality().getNumber());
    }
    if (content.hasContext()) {
      gen.writeNumberField("context", content.getContext().getNumber());
    }
    if (content.hasContentrating()) {
      gen.writeStringField("contentrating", content.getContentrating());
    }
    if (content.hasUserrating()) {
      gen.writeStringField("userrating", content.getUserrating());
    }
    if (content.hasQagmediarating()) {
      gen.writeNumberField("qagmediarating", content.getQagmediarating().getNumber());
    }
    writeCsvString("keywords", content.getKeywordsList(), gen);
    if (content.hasLivestream()) {
      writeIntBoolField("livestream", content.getLivestream(), gen);
    }
    if (content.hasSourcerelationship()) {
      writeIntBoolField("sourcerelationship", content.getSourcerelationship(), gen);
    }
    if (content.hasLen()) {
      gen.writeNumberField("len", content.getLen());
    }
    if (content.hasLanguage()) {
      gen.writeStringField("language", content.getLanguage());
    }
    if (content.hasEmbeddable()) {
      writeIntBoolField("embeddable", content.getEmbeddable(), gen);
    }
    writeExtensions(content, gen);
    gen.writeEndObject();
  }

  public void writeProducer(Producer producer, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (producer.hasId()) {
      gen.writeStringField("id", producer.getId());
    }
    if (producer.hasName()) {
      gen.writeStringField("name", producer.getName());
    }
    writeStrings("cat", producer.getCatList(), gen);
    if (producer.hasDomain()) {
      gen.writeStringField("domain", producer.getDomain());
    }
    writeExtensions(producer, gen);
    gen.writeEndObject();
  }

  public void writePublisher(Publisher publisher, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (publisher.hasId()) {
      gen.writeStringField("id", publisher.getId());
    }
    if (publisher.hasName()) {
      gen.writeStringField("name", publisher.getName());
    }
    writeStrings("cat", publisher.getCatList(), gen);
    if (publisher.hasDomain()) {
      gen.writeStringField("domain", publisher.getDomain());
    }
    writeExtensions(publisher, gen);
    gen.writeEndObject();
  }

  public void writeDevice(Device device, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (device.hasUa()) {
      gen.writeStringField("ua", device.getUa());
    }
    if (device.hasGeo()) {
      gen.writeFieldName("geo");
      writeGeo(device.getGeo(), gen);
    }
    if (device.hasDnt()) {
      writeIntBoolField("dnt", device.getDnt(), gen);
    }
    if (device.hasLmt()) {
      writeIntBoolField("lmt", device.getLmt(), gen);
    }
    if (device.hasIp()) {
      gen.writeStringField("ip", device.getIp());
    }
    if (device.hasIpv6()) {
      gen.writeStringField("ipv6", device.getIpv6());
    }
    if (device.hasDevicetype()) {
      gen.writeNumberField("devicetype", device.getDevicetype().getNumber());
    }
    if (device.hasMake()) {
      gen.writeStringField("make", device.getMake());
    }
    if (device.hasModel()) {
      gen.writeStringField("model", device.getModel());
    }
    if (device.hasOs()) {
      gen.writeStringField("os", device.getOs());
    }
    if (device.hasOsv()) {
      gen.writeStringField("osv", device.getOsv());
    }
    if (device.hasHwv()) {
      gen.writeStringField("hwv", device.getHwv());
    }
    if (device.hasW()) {
      gen.writeNumberField("w", device.getW());
    }
    if (device.hasH()) {
      gen.writeNumberField("h", device.getH());
    }
    if (device.hasPpi()) {
      gen.writeNumberField("ppi", device.getPpi());
    }
    if (device.hasPxratio()) {
      gen.writeNumberField("pxratio", device.getPxratio());
    }
    if (device.hasJs()) {
      writeIntBoolField("js", device.getJs(), gen);
    }
    if (device.hasFlashver()) {
      gen.writeStringField("flashver", device.getFlashver());
    }
    if (device.hasLanguage()) {
      gen.writeStringField("language", device.getLanguage());
    }
    if (device.hasCarrier()) {
      gen.writeStringField("carrier", device.getCarrier());
    }
    if (device.hasConnectiontype()) {
      gen.writeNumberField("connectiontype", device.getConnectiontype().getNumber());
    }
    if (device.hasIfa()) {
      gen.writeStringField("ifa", device.getIfa());
    }
    if (device.hasDidsha1()) {
      gen.writeStringField("didsha1", device.getDidsha1());
    }
    if (device.hasDidmd5()) {
      gen.writeStringField("didmd5", device.getDidmd5());
    }
    if (device.hasDpidsha1()) {
      gen.writeStringField("dpidsha1", device.getDpidsha1());
    }
    if (device.hasDpidmd5()) {
      gen.writeStringField("dpidmd5", device.getDpidmd5());
    }
    if (device.hasMacsha1()) {
      gen.writeStringField("macsha1", device.getMacsha1());
    }
    if (device.hasMacmd5()) {
      gen.writeStringField("macmd5", device.getMacmd5());
    }
    writeExtensions(device, gen);
    gen.writeEndObject();
  }

  public void writeGeo(Geo geo, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (geo.hasLat()) {
      gen.writeNumberField("lat", geo.getLat());
    }
    if (geo.hasLon()) {
      gen.writeNumberField("lon", geo.getLon());
    }
    if (geo.hasType()) {
      gen.writeNumberField("type", geo.getType().getNumber());
    }
    if (geo.hasCountry()) {
      gen.writeStringField("country", geo.getCountry());
    }
    if (geo.hasRegion()) {
      gen.writeStringField("region", geo.getRegion());
    }
    if (geo.hasRegionfips104()) {
      gen.writeStringField("regionfips104", geo.getRegionfips104());
    }
    if (geo.hasMetro()) {
      gen.writeStringField("metro", geo.getMetro());
    }
    if (geo.hasCity()) {
      gen.writeStringField("city", geo.getCity());
    }
    if (geo.hasZip()) {
      gen.writeStringField("zip", geo.getZip());
    }
    if (geo.hasUtcoffset()) {
      gen.writeNumberField("utcoffset", geo.getUtcoffset());
    }
    writeExtensions(geo, gen);
    gen.writeEndObject();
  }

  public void writeUser(User user, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (user.hasId()) {
      gen.writeStringField("id", user.getId());
    }
    if (user.hasBuyeruid()) {
      gen.writeStringField("buyeruid", user.getBuyeruid());
    }
    if (user.hasYob()) {
      gen.writeNumberField("yob", user.getYob());
    }
    if (user.hasGender()) {
      gen.writeStringField("gender", user.getGender());
    }
    writeCsvString("keywords", user.getKeywordsList(), gen);
    if (user.hasCustomdata()) {
      gen.writeStringField("customdata", user.getCustomdata());
    }
    if (user.hasGeo()) {
      gen.writeFieldName("geo");
      writeGeo(user.getGeo(), gen);
    }
    if (user.getDataCount() != 0) {
      gen.writeArrayFieldStart("data");
      for (Data data : user.getDataList()) {
        writeData(data, gen);
      }
      gen.writeEndArray();
    }
    writeExtensions(user, gen);
    gen.writeEndObject();
  }

  public void writeData(Data data, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (data.hasId()) {
      gen.writeStringField("id", data.getId());
    }
    if (data.hasName()) {
      gen.writeStringField("name", data.getName());
    }
    if (data.getSegmentCount() != 0) {
      gen.writeArrayFieldStart("segment");
      for (Segment segment : data.getSegmentList()) {
        writeSegment(segment, gen);
      }
      gen.writeEndArray();
    }
    writeExtensions(data, gen);
    gen.writeEndObject();
  }

  public void writeSegment(Segment segment, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (segment.hasId()) {
      gen.writeStringField("id", segment.getId());
    }
    if (segment.hasName()) {
      gen.writeStringField("name", segment.getName());
    }
    if (segment.hasValue()) {
      gen.writeStringField("value", segment.getValue());
    }
    writeExtensions(segment, gen);
    gen.writeEndObject();
  }

  public void writeRegulations(Regulations regs, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (regs.hasCoppa()) {
      writeIntBoolField("coppa", regs.getCoppa(), gen);
    }
    writeExtensions(regs, gen);
    gen.writeEndObject();
  }

  /**
   * Serializes a {@link BidResponse} to JSON, returned as a {@link String}.
   */
  public String writeBidResponse(BidResponse resp) throws IOException {
    try (StringWriter writer = new StringWriter()) {
      writeBidResponse(resp, writer);
      return writer.toString();
    }
  }

  /**
   * Serializes a {@link BidResponse} to JSON, streamed to a {@link OutputStream}.
   *
   * @see JsonFactory#createGenerator(OutputStream)
   */
  public void writeBidResponse(BidResponse resp, OutputStream os) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(os);
    writeBidResponse(resp, gen);
  }

  /**
   * Serializes a {@link BidResponse} to JSON, streamed to a {@link Writer}.
   *
   * @see JsonFactory#createGenerator(Writer)
   */
  public void writeBidResponse(BidResponse resp, Writer writer) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(writer);
    writeBidResponse(resp, gen);
  }

  /**
   * Serializes a {@link BidResponse} to JSON, with a provided {@link JsonGenerator}
   * which allows several choices of output and encoding.
   */
  public void writeBidResponse(BidResponse resp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("id", resp.getId());
    if (resp.getSeatbidCount() != 0) {
      gen.writeArrayFieldStart("seatbid");
      for (SeatBid seatbid : resp.getSeatbidList()) {
        writeSeatBid(seatbid, gen);
      }
      gen.writeEndArray();
    }
    if (resp.hasBidid()) {
      gen.writeStringField("bidid", resp.getBidid());
    }
    if (resp.hasCur()) {
      gen.writeStringField("cur", resp.getCur());
    }
    if (resp.hasCustomdata()) {
      gen.writeStringField("customdata", resp.getCustomdata());
    }
    if (resp.hasNbr()) {
      gen.writeNumberField("nbr", resp.getNbr().getNumber());
    }
    writeExtensions(resp, gen);
    gen.writeEndObject();
    gen.flush();
  }

  public void writeSeatBid(SeatBid seatbid, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (seatbid.getBidCount() != 0) {
      gen.writeArrayFieldStart("bid");
      for (Bid bid : seatbid.getBidList()) {
        writeBid(bid, gen);
      }
      gen.writeEndArray();
    }
    if (seatbid.hasSeat()) {
      gen.writeStringField("seat", seatbid.getSeat());
    }
    if (seatbid.hasGroup()) {
      writeIntBoolField("group", seatbid.getGroup(), gen);
    }
    writeExtensions(seatbid, gen);
    gen.writeEndObject();
  }

  public void writeBid(Bid bid, JsonGenerator gen) throws IOException {
    checkArgument(!(bid.hasAdm() && bid.hasAdmNative()),
        "Bid.adm and Bid.admNative cannot both be populated");
    gen.writeStartObject();
    gen.writeStringField("id", bid.getId());
    gen.writeStringField("impid", bid.getImpid());
    gen.writeNumberField("price", bid.getPrice());
    if (bid.hasAdid()) {
      gen.writeStringField("adid", bid.getAdid());
    }
    if (bid.hasNurl()) {
      gen.writeStringField("nurl", bid.getNurl());
    }
    if (bid.hasAdm()) {
      gen.writeStringField("adm", bid.getAdm());
    } else if (bid.hasAdmNative()) {
      gen.writeStringField("adm", nativeWriter().writeNativeResponse(bid.getAdmNative()));
    }
    writeStrings("adomain", bid.getAdomainList(), gen);
    if (bid.hasBundle()) {
      gen.writeStringField("bundle", bid.getBundle());
    }
    if (bid.hasIurl()) {
      gen.writeStringField("iurl", bid.getIurl());
    }
    if (bid.hasCid()) {
      gen.writeStringField("cid", bid.getCid());
    }
    if (bid.hasCrid()) {
      gen.writeStringField("crid", bid.getCrid());
    }
    if (bid.hasCat()) {
      gen.writeStringField("cat", bid.getCat());
    }
    writeEnums("attr", bid.getAttrList(), gen);
    if (bid.hasDealid()) {
      gen.writeStringField("dealid", bid.getDealid());
    }
    if (bid.hasW()) {
      gen.writeNumberField("w", bid.getW());
    }
    if (bid.hasH()) {
      gen.writeNumberField("h", bid.getH());
    }
    writeExtensions(bid, gen);
    gen.writeEndObject();
  }

  protected final OpenRtbNativeJsonWriter nativeWriter() {
    if (nativeWriter == null) {
      nativeWriter = factory().newNativeWriter();
    }
    return nativeWriter;
  }
}
