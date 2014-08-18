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

import static com.google.openrtb.json.OpenRtbJsonUtils.writeEnums;
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
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP.DirectDeal;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regulations;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage.ExtendableMessage;
import com.google.protobuf.Message;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Serializes OpenRTB messages to JSON.
 */
public class OpenRtbJsonWriter {
  private final OpenRtbJsonFactory factory;
  private final boolean requiredAlways = false;

  protected OpenRtbJsonWriter(OpenRtbJsonFactory factory) {
    this.factory = factory;
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
    JsonGenerator gen = factory.getJsonFactory().createGenerator(writer);
    writeBidRequest(req, gen);
    gen.flush();
  }

  /**
   * Serializes a {@link BidRequest} to JSON, streamed into an {@link OutputStream}.
   *
   * @see JsonFactory#createGenerator(OutputStream)
   */
  public void writeBidRequest(BidRequest req, OutputStream os) throws IOException {
    JsonGenerator gen = factory.getJsonFactory().createGenerator(os);
    writeBidRequest(req, gen);
    gen.flush();
  }

  /**
   * Serializes a {@link BidRequest} to JSON, with a provided {@link JsonGenerator}
   * which allows several choices of output and encoding.
   */
  public void writeBidRequest(BidRequest req, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (checkRequired(req.hasId())) {
      gen.writeStringField("id", req.getId());
    }
    if (req.getImpCount() != 0) {
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
    if (req.hasAt()) {
      gen.writeNumberField("at", req.getAt());
    }
    if (req.hasTmax()) {
      gen.writeNumberField("tmax", req.getTmax());
    }
    writeStrings("wseat", req.getWseatList(), gen);
    if (req.hasAllimps()) {
      gen.writeNumberField("allimps", req.getAllimps().getNumber());
    }
    writeStrings("cur", req.getCurList(), gen);
    writeStrings("bcat", req.getBcatList(), gen);
    writeStrings("badv", req.getBadvList(), gen);
    if (req.hasRegs()) {
      gen.writeFieldName("regs");
      writeRegulations(req.getRegs(), gen);
    }
    writeExtensions(req, gen, "BidRequest");
    gen.writeEndObject();
  }

  protected void writeImpression(Impression imp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (checkRequired(imp.hasId())) {
      gen.writeStringField("id", imp.getId());
    }
    if (imp.hasBanner()) {
      gen.writeFieldName("banner");
      writeBanner(imp.getBanner(), gen);
    }
    if (imp.hasVideo()) {
      gen.writeFieldName("video");
      writeVideo(imp.getVideo(), gen);
    }
    if (imp.hasDisplaymanager()) {
      gen.writeStringField("displaymanager", imp.getDisplaymanager());
    }
    if (imp.hasDisplaymanagerver()) {
      gen.writeStringField("displaymanagerver", imp.getDisplaymanagerver());
    }
    if (imp.hasInstl()) {
      gen.writeNumberField("instl", imp.getInstl().getNumber());
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
    writeStrings("iframebuster", imp.getIframebusterList(), gen);
    if (imp.hasPmp()) {
      gen.writeFieldName("pmp");
      writePMP(imp.getPmp(), gen);
    }
    writeExtensions(imp, gen, "BidRequest.imp");
    gen.writeEndObject();
  }

  protected void writeBanner(Banner banner, JsonGenerator gen) throws IOException {
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
    if (banner.hasPos()) {
      gen.writeNumberField("pos", banner.getPos().getNumber());
    }
    writeEnums("btype", banner.getBtypeList(), gen);
    writeEnums("battr", banner.getBattrList(), gen);
    writeStrings("mimes", banner.getMimesList(), gen);
    if (banner.hasTopframe()) {
      gen.writeNumberField("topframe", banner.getTopframe().getNumber());
    }
    writeEnums("expdir", banner.getExpdirList(), gen);
    writeEnums("api", banner.getApiList(), gen);
    writeExtensions(banner, gen, "BidRequest.imp.banner");
    gen.writeEndObject();
  }

  protected void writeVideo(Video video, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeStrings("mimes", video.getMimesList(), gen);
    if (checkRequired(video.hasLinearity())) {
      gen.writeNumberField("linearity", video.getLinearity().getNumber());
    }
    if (checkRequired(video.hasMinduration())) {
      gen.writeNumberField("minduration", video.getMinduration());
    }
    if (checkRequired(video.hasMaxduration())) {
      gen.writeNumberField("maxduration", video.getMaxduration());
    }
    if (checkRequired(video.hasProtocol())) {
      gen.writeNumberField("protocol", video.getProtocol().getNumber());
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
      gen.writeNumberField("boxingallowed", video.getBoxingallowed().getNumber());
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
    writeExtensions(video, gen, "BidRequest.imp.video");
    gen.writeEndObject();
  }

  protected void writePMP(PMP pmp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (pmp.hasPrivateAuction()) {
      gen.writeNumberField("private_auction", pmp.getPrivateAuction().getNumber());
    }
    if (pmp.getDealsCount() != 0) {
      gen.writeArrayFieldStart("deals");
      for (DirectDeal deals : pmp.getDealsList()) {
        writeDirectDeal(deals, gen);
      }
      gen.writeEndArray();
    }
    writeExtensions(pmp, gen, "BidRequest.imp.pmp");
    gen.writeEndObject();
  }

  protected void writeDirectDeal(DirectDeal deal, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (checkRequired(deal.hasId())) {
      gen.writeStringField("id", deal.getId());
    }
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
    writeExtensions(deal, gen, "BidRequest.imp.pmp.deals");
    gen.writeEndObject();
  }

  protected void writeSite(Site site, JsonGenerator gen) throws IOException {
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
    if (site.hasPrivacypolicy()) {
      gen.writeNumberField("privacypolicy", site.getPrivacypolicy().getNumber());
    }
    if (site.hasRef()) {
      gen.writeStringField("ref", site.getRef());
    }
    if (site.hasSearch()) {
      gen.writeStringField("search", site.getSearch());
    }
    if (site.hasPublisher()) {
      gen.writeFieldName("publisher");
      writePublisher(site.getPublisher(), gen);
    }
    if (site.hasContent()) {
      gen.writeFieldName("content");
      writeContent(site.getContent(), gen);
    }
    if (site.hasKeywords()) {
      gen.writeStringField("keywords", site.getKeywords());
    }
    writeExtensions(site, gen, "BidRequest.site");
    gen.writeEndObject();
  }

  protected void writeApp(App app, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (app.hasId()) {
      gen.writeStringField("id", app.getId());
    }
    if (app.hasName()) {
      gen.writeStringField("name", app.getName());
    }
    if (app.hasDomain()) {
      gen.writeStringField("domain", app.getDomain());
    }
    writeStrings("cat", app.getCatList(), gen);
    writeStrings("sectioncat", app.getSectioncatList(), gen);
    writeStrings("pagecat", app.getPagecatList(), gen);
    if (app.hasVer()) {
      gen.writeStringField("ver", app.getVer());
    }
    if (app.hasBundle()) {
      gen.writeStringField("bundle", app.getBundle());
    }
    if (app.hasPrivacypolicy()) {
      gen.writeNumberField("privacypolicy", app.getPrivacypolicy().getNumber());
    }
    if (app.hasPaid()) {
      gen.writeNumberField("paid", app.getPaid().getNumber());
    }
    if (app.hasPublisher()) {
      gen.writeFieldName("publisher");
      writePublisher(app.getPublisher(), gen);
    }
    if (app.hasContent()) {
      gen.writeFieldName("content");
      writeContent(app.getContent(), gen);
    }
    if (app.hasKeywords()) {
      gen.writeStringField("keywords", app.getKeywords());
    }
    if (app.hasStoreurl()) {
      gen.writeStringField("storeurl", app.getStoreurl());
    }
    writeExtensions(app, gen, "BidRequest.app");
    gen.writeEndObject();
  }

  protected void writeContent(Content content, JsonGenerator gen) throws IOException {
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
    if (content.hasUrl()) {
      gen.writeStringField("url", content.getUrl());
    }
    writeStrings("cat", content.getCatList(), gen);
    if (content.hasVideoquality()) {
      gen.writeNumberField("videoquality", content.getVideoquality().getNumber());
    }
    if (content.hasKeywords()) {
      gen.writeStringField("keywords", content.getKeywords());
    }
    if (content.hasContentrating()) {
      gen.writeStringField("contentrating", content.getContentrating());
    }
    if (content.hasUserrating()) {
      gen.writeStringField("userrating", content.getUserrating());
    }
    if (content.hasContext()) {
      gen.writeNumberField("context", content.getContext().getNumber());
    }
    if (content.hasLivestream()) {
      gen.writeNumberField("livestream", content.getLivestream().getNumber());
    }
    if (content.hasSourcerelationship()) {
      gen.writeNumberField("sourcerelationship", content.getSourcerelationship().getNumber());
    }
    if (content.hasProducer()) {
      gen.writeFieldName("producer");
      writeProducer(content.getProducer(), gen);
    }
    if (content.hasLen()) {
      gen.writeNumberField("len", content.getLen());
    }
    if (content.hasQagmediarating()) {
      gen.writeNumberField("qagmediarating", content.getQagmediarating().getNumber());
    }
    if (content.hasEmbeddable()) {
      gen.writeNumberField("embeddable", content.getEmbeddable().getNumber());
    }
    if (content.hasLanguage()) {
      gen.writeStringField("language", content.getLanguage());
    }
    writeExtensions(content, gen, "BidRequest.app.content");
    gen.writeEndObject();
  }

  protected void writeProducer(Producer producer, JsonGenerator gen) throws IOException {
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
    writeExtensions(producer, gen, "BidRequest.app.content.producer");
    gen.writeEndObject();
  }

  protected void writePublisher(Publisher publisher, JsonGenerator gen) throws IOException {
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
    writeExtensions(publisher, gen, "BidRequest.app.publisher");
    gen.writeEndObject();
  }

  protected void writeDevice(Device device, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (device.hasDnt()) {
      gen.writeNumberField("dnt", device.getDnt().getNumber());
    }
    if (device.hasUa()) {
      gen.writeStringField("ua", device.getUa());
    }
    if (device.hasIp()) {
      gen.writeStringField("ip", device.getIp());
    }
    if (device.hasGeo()) {
      gen.writeFieldName("geo");
      writeGeo(device.getGeo(), gen, "BidRequest.device.geo");
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
    if (device.hasIpv6()) {
      gen.writeStringField("ipv6", device.getIpv6());
    }
    if (device.hasCarrier()) {
      gen.writeStringField("carrier", device.getCarrier());
    }
    if (device.hasLanguage()) {
      gen.writeStringField("language", device.getLanguage());
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
    if (device.hasJs()) {
      gen.writeNumberField("js", device.getJs().getNumber());
    }
    if (device.hasConnectiontype()) {
      gen.writeNumberField("connectiontype", device.getConnectiontype().getNumber());
    }
    if (device.hasDevicetype()) {
      gen.writeNumberField("devicetype", device.getDevicetype().getNumber());
    }
    if (device.hasFlashver()) {
      gen.writeStringField("flashver", device.getFlashver());
    }
    writeExtensions(device, gen, "BidRequest.device");
    gen.writeEndObject();
  }

  protected void writeGeo(Geo geo, JsonGenerator gen, String path) throws IOException {
    gen.writeStartObject();
    if (geo.hasLat()) {
      gen.writeNumberField("lat", geo.getLat());
    }
    if (geo.hasLon()) {
      gen.writeNumberField("lon", geo.getLon());
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
    if (geo.hasType()) {
      gen.writeNumberField("type", geo.getType().getNumber());
    }
    writeExtensions(geo, gen, path);
    gen.writeEndObject();
  }

  protected void writeUser(User user, JsonGenerator gen) throws IOException {
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
    if (user.hasKeywords()) {
      gen.writeStringField("keywords", user.getKeywords());
    }
    if (user.hasCustomdata()) {
      gen.writeStringField("customdata", user.getCustomdata());
    }
    if (user.hasGeo()) {
      gen.writeFieldName("geo");
      writeGeo(user.getGeo(), gen, "BidRequest.user.geo");
    }
    if (user.getDataCount() != 0) {
      gen.writeArrayFieldStart("data");
      for (Data data : user.getDataList()) {
        writeData(data, gen);
      }
      gen.writeEndArray();
    }
    writeExtensions(user, gen, "BidRequest.user");
    gen.writeEndObject();
  }

  protected void writeData(Data data, JsonGenerator gen) throws IOException {
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
    writeExtensions(data, gen, "BidRequest.user.data");
    gen.writeEndObject();
  }

  protected void writeSegment(Segment segment, JsonGenerator gen) throws IOException {
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
    writeExtensions(segment, gen, "BidRequest.user.data.segment");
    gen.writeEndObject();
  }

  protected void writeRegulations(Regulations regs, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (regs.hasCoppa()) {
      gen.writeNumberField("coppa", regs.getCoppa().getNumber());
    }
    writeExtensions(regs, gen, "BidRequest.regs");
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
    JsonGenerator gen = factory.getJsonFactory().createGenerator(os);
    writeBidResponse(resp, gen);
    gen.flush();
  }

  /**
   * Serializes a {@link BidResponse} to JSON, streamed to a {@link Writer}.
   *
   * @see JsonFactory#createGenerator(Writer)
   */
  public void writeBidResponse(BidResponse resp, Writer writer) throws IOException {
    JsonGenerator gen = factory.getJsonFactory().createGenerator(writer);
    writeBidResponse(resp, gen);
    gen.flush();
  }

  /**
   * Serializes a {@link BidResponse} to JSON, with a provided {@link JsonGenerator}
   * which allows several choices of output and encoding.
   */
  public void writeBidResponse(BidResponse resp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (resp.hasId()) {
      gen.writeStringField("id", resp.getId());
    }
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
    writeExtensions(resp, gen, "BidResponse");
    gen.writeEndObject();
  }

  protected void writeSeatBid(SeatBid seatbid, JsonGenerator gen) throws IOException {
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
      gen.writeNumberField("group", seatbid.getGroup().getNumber());
    }
    writeExtensions(seatbid, gen, "BidResponse.seatbid");
    gen.writeEndObject();
  }

  protected void writeBid(Bid bid, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    if (checkRequired(bid.hasId())) {
      gen.writeStringField("id", bid.getId());
    }
    if (checkRequired(bid.hasImpid())) {
      gen.writeStringField("impid", bid.getImpid());
    }
    if (checkRequired(bid.hasPrice())) {
      gen.writeNumberField("price", bid.getPrice());
    }
    if (bid.hasAdid()) {
      gen.writeStringField("adid", bid.getAdid());
    }
    if (bid.hasNurl()) {
      gen.writeStringField("nurl", bid.getNurl());
    }
    if (bid.hasAdm()) {
      gen.writeStringField("adm", bid.getAdm());
    }
    writeStrings("adomain", bid.getAdomainList(), gen);
    if (bid.hasIurl()) {
      gen.writeStringField("iurl", bid.getIurl());
    }
    if (bid.hasCid()) {
      gen.writeStringField("cid", bid.getCid());
    }
    if (bid.hasCrid()) {
      gen.writeStringField("crid", bid.getCrid());
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
    writeExtensions(bid, gen, "BidResponse.seatbid.bid");
    gen.writeEndObject();
  }

  protected <EM extends ExtendableMessage<EM>>
  void writeExtensions(EM msg, JsonGenerator gen, String path) throws IOException {
    boolean openExt = false;
    StringBuilder fullPath = new StringBuilder(path).append(':');
    int pathMsg = fullPath.length();

    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = entry.getKey();
      if (fd.isExtension() && entry.getValue() instanceof Message) {
        Message extMsg = (Message) entry.getValue();
        fullPath.setLength(pathMsg);
        fullPath.append(extMsg.getClass().getName());
        @SuppressWarnings("unchecked")
        OpenRtbJsonExtWriter<Message> extWriter = factory.getWriter(fullPath.toString());
        if (extWriter != null) {
          if (!openExt) {
            openExt = true;
            gen.writeFieldName("ext");
            gen.writeStartObject();
          }
          extWriter.write(extMsg, gen);
        }
      }
    }

    if (openExt) {
      gen.writeEndObject();
    }
  }

  protected boolean checkRequired(boolean hasProperty) {
    return requiredAlways || hasProperty;
  }
}
