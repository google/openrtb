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
import static com.google.openrtb.json.OpenRtbJsonUtils.writeIntBoolField;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeStrings;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.openrtb.Gender;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Data.Segment;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Imp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Audio;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Banner.Format;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Metric;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Pmp.Deal;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Video.CompanionAd;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regs;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.Source;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Serializes OpenRTB {@link BidRequest}/{@link BidResponse} messages to JSON.
 *
 * <p>Note: Among methods that write to a {@link JsonGenerator} parameter, only the {@code public}
 * methods will call {@code flush()} on the generator before returning.
 *
 * <p>This class is threadsafe.
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
  public final void writeBidRequest(BidRequest req, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeBidRequestFields(req, gen);
    writeExtensions(req, gen);
    gen.writeEndObject();
    gen.flush();
  }

  protected void writeBidRequestFields(BidRequest req, JsonGenerator gen) throws IOException {
    gen.writeStringField("id", req.getId());
    if (checkRequired(req.getImpCount())) {
      gen.writeArrayFieldStart("imp");
      for (Imp imp : req.getImpList()) {
        writeImp(imp, gen);
      }
      gen.writeEndArray();
    }
    switch (req.getDistributionchannelOneofCase()) {
      case SITE:
        gen.writeFieldName("site");
        writeSite(req.getSite(), gen);
        break;
      case APP:
        gen.writeFieldName("app");
        writeApp(req.getApp(), gen);
        break;
      case DISTRIBUTIONCHANNELONEOF_NOT_SET:
        checkRequired(false);
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
      gen.writeNumberField("at", req.getAt().getNumber());
    }
    if (req.hasTmax()) {
      gen.writeNumberField("tmax", req.getTmax());
    }
    writeStrings("wseat", req.getWseatList(), gen);
    if (req.hasAllimps()) {
      writeIntBoolField("allimps", req.getAllimps(), gen);
    }
    writeStrings("cur", req.getCurList(), gen);
    writeContentCategories("bcat", req.getBcatList(), gen);
    writeStrings("badv", req.getBadvList(), gen);
    if (req.hasRegs()) {
      gen.writeFieldName("regs");
      writeRegs(req.getRegs(), gen);
    }
    writeStrings("bapp", req.getBappList(), gen);
    writeStrings("bseat", req.getBseatList(), gen);
    writeStrings("wlang", req.getWlangList(), gen);
    if (req.hasSource()) {
      gen.writeFieldName("source");
      writeSource(req.getSource(), gen);
    }
  }

  public final void writeImp(Imp imp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeImpFields(imp, gen);
    writeExtensions(imp, gen);
    gen.writeEndObject();
  }

  protected void writeImpFields(Imp imp, JsonGenerator gen) throws IOException {
    gen.writeStringField("id", imp.getId());
    if (imp.hasBanner()) {
      gen.writeFieldName("banner");
      writeBanner(imp.getBanner(), gen);
    }
    if (imp.hasVideo()) {
      gen.writeFieldName("video");
      writeVideo(imp.getVideo(), gen);
    }
    if (imp.hasAudio()) {
      gen.writeFieldName("audio");
      writeAudio(imp.getAudio(), gen);
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
      writePmp(imp.getPmp(), gen);
    }
    if (imp.hasClickbrowser()) {
      gen.writeBooleanField("clickbrowser", imp.getClickbrowser());
    }
    if (imp.hasExp()) {
      gen.writeNumberField("exp", imp.getExp());
    }
    if (imp.getMetricCount() != 0) {
      gen.writeArrayFieldStart("metric");
      for (Metric metric : imp.getMetricList()) {
        writeMetric(metric, gen);
      }
      gen.writeEndArray();
    }
  }

  public final void writeMetric(Metric metric, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeMetricFields(metric, gen);
    writeExtensions(metric, gen);
    gen.writeEndObject();
  }

  protected void writeMetricFields(Metric metric, JsonGenerator gen) throws IOException {
    if (metric.hasType()) {
      gen.writeStringField("type", metric.getType());
    }
    if (metric.hasValue()) {
      gen.writeNumberField("value", metric.getValue());
    }
    if (metric.hasVendor()) {
      gen.writeStringField("vendor", metric.getVendor());
    }
  }

  public final void writeBanner(Banner banner, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeBannerFields(banner, gen);
    writeExtensions(banner, gen);
    gen.writeEndObject();
  }

  @SuppressWarnings("deprecation")
  protected void writeBannerFields(Banner banner, JsonGenerator gen) throws IOException {
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
    if (checkRequired(banner.getFormatCount())) {
      gen.writeArrayFieldStart("format");
      for (Format format : banner.getFormatList()) {
        writeFormat(format, gen);
      }
      gen.writeEndArray();
    }
    if (banner.hasVcm()) {
      writeIntBoolField("vcm", banner.getVcm(), gen);
    }
  }

  public final void writeFormat(Format format, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeFormatFields(format, gen);
    writeExtensions(format, gen);
    gen.writeEndObject();
  }

  protected void writeFormatFields(Format format, JsonGenerator gen) throws IOException {
    if (format.hasW()) {
      gen.writeNumberField("w", format.getW());
    }
    if (format.hasH()) {
      gen.writeNumberField("h", format.getH());
    }
    if (format.hasWratio()) {
      gen.writeNumberField("wratio", format.getWratio());
    }
    if (format.hasHratio()) {
      gen.writeNumberField("hratio", format.getHratio());
    }
    if (format.hasWmin()) {
      gen.writeNumberField("wmin", format.getWmin());
    }
  }

  public final void writeVideo(Video video, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeVideoFields(video, gen);
    writeExtensions(video, gen);
    gen.writeEndObject();
  }

  @SuppressWarnings("deprecation")
  protected void writeVideoFields(Video video, JsonGenerator gen) throws IOException {
    if (checkRequired(video.getMimesCount())) {
      writeStrings("mimes", video.getMimesList(), gen);
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
      writeEnums("protocols", video.getProtocolsList(), gen);
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
      // OpenRTB 2.2+
      gen.writeArrayFieldStart("companionad");
      for (Banner companionad : video.getCompanionadList()) {
        writeBanner(companionad, gen);
      }
      gen.writeEndArray();
    }
    if (video.hasCompanionad21()) {
      // OpenRTB 2.1-
      gen.writeFieldName("companionad");
      writeCompanionAd21(video.getCompanionad21(), gen);
    }
    writeEnums("api", video.getApiList(), gen);
    writeEnums("companiontype", video.getCompaniontypeList(), gen);
    if (video.hasSkip()) {
      writeIntBoolField("skip", video.getSkip(), gen);
    }
    if (video.hasSkipmin()) {
      gen.writeNumberField("skipmin", video.getSkipmin());
    }
    if (video.hasSkipafter()) {
      gen.writeNumberField("skipafter", video.getSkipafter());
    }
    if (video.hasPlacement()) {
      gen.writeNumberField("placement", video.getPlacement().getNumber());
    }
    if (video.hasPlaybackend()) {
      gen.writeNumberField("playbackend", video.getPlaybackend().getNumber());
    }
  }

  public final void writeCompanionAd21(CompanionAd companionad21, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeCompanionAd21Fields(companionad21, gen);
    writeExtensions(companionad21, gen);
    gen.writeEndObject();
  }

  protected void writeCompanionAd21Fields(CompanionAd companionad21, JsonGenerator gen)
      throws IOException {
    if (companionad21.getBannerCount() != 0) {
      gen.writeArrayFieldStart("banner");
      for (Banner banner : companionad21.getBannerList()) {
        writeBanner(banner, gen);
      }
      gen.writeEndArray();
    }
  }

  public final void writeAudio(Audio audio, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeAudioFields(audio, gen);
    writeExtensions(audio, gen);
    gen.writeEndObject();
  }

  protected void writeAudioFields(Audio audio, JsonGenerator gen) throws IOException {
    // Common to Video & Audio

    if (checkRequired(audio.getMimesCount())) {
      writeStrings("mimes", audio.getMimesList(), gen);
    }
    if (audio.hasMinduration()) {
      gen.writeNumberField("minduration", audio.getMinduration());
    }
    if (audio.hasMaxduration()) {
      gen.writeNumberField("maxduration", audio.getMaxduration());
    }
    if (checkRequired(audio.getProtocolsCount())) {
      writeEnums("protocols", audio.getProtocolsList(), gen);
    }
    if (audio.hasStartdelay()) {
      gen.writeNumberField("startdelay", audio.getStartdelay());
    }
    if (audio.hasSequence()) {
      gen.writeNumberField("sequence", audio.getSequence());
    }
    writeEnums("battr", audio.getBattrList(), gen);
    if (audio.hasMaxextended()) {
      gen.writeNumberField("maxextended", audio.getMaxextended());
    }
    if (audio.hasMinbitrate()) {
      gen.writeNumberField("minbitrate", audio.getMinbitrate());
    }
    if (audio.hasMaxbitrate()) {
      gen.writeNumberField("maxbitrate", audio.getMaxbitrate());
    }
    writeEnums("delivery", audio.getDeliveryList(), gen);
    if (audio.getCompanionadCount() != 0) {
      // OpenRTB 2.2+
      gen.writeArrayFieldStart("companionad");
      for (Banner companionad : audio.getCompanionadList()) {
        writeBanner(companionad, gen);
      }
      gen.writeEndArray();
    }
    writeEnums("api", audio.getApiList(), gen);
    writeEnums("companiontype", audio.getCompaniontypeList(), gen);

    // Audio only

    if (audio.hasMaxseq()) {
      gen.writeNumberField("maxseq", audio.getMaxseq());
    }
    if (audio.hasFeed()) {
      gen.writeNumberField("feed", audio.getFeed().getNumber());
    }
    if (audio.hasStitched()) {
      writeIntBoolField("stitched", audio.getStitched(), gen);
    }
    if (audio.hasNvol()) {
      gen.writeNumberField("nvol", audio.getNvol().getNumber());
    }
  }

  public final void writeNative(Native nativ, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeNativeFields(nativ, gen);
    writeExtensions(nativ, gen);
    gen.writeEndObject();
  }

  protected void writeNativeFields(Native nativ, JsonGenerator gen) throws IOException {
    switch (nativ.getRequestOneofCase()) {
      case REQUEST_NATIVE:
        gen.writeFieldName("request");
        if (factory().isForceNativeAsObject()) {
          nativeWriter().writeNativeRequest(nativ.getRequestNative(), gen);
        } else {
          gen.writeString(nativeWriter().writeNativeRequest(nativ.getRequestNative()));
        }
        break;
      case REQUEST:
        gen.writeStringField("request", nativ.getRequest());
        break;
      case REQUESTONEOF_NOT_SET:
        checkRequired(false);
    }
    if (nativ.hasVer()) {
      gen.writeStringField("ver", nativ.getVer());
    }
    writeEnums("api", nativ.getApiList(), gen);
    writeEnums("battr", nativ.getBattrList(), gen);
  }

  public final void writePmp(Pmp pmp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writePmpFields(pmp, gen);
    writeExtensions(pmp, gen);
    gen.writeEndObject();
  }

  protected void writePmpFields(Pmp pmp, JsonGenerator gen) throws IOException {
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
  }

  public final void writeDeal(Deal deal, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeDealFields(deal, gen);
    writeExtensions(deal, gen);
    gen.writeEndObject();
  }

  protected void writeDealFields(Deal deal, JsonGenerator gen) throws IOException {
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
      gen.writeNumberField("at", deal.getAt().getNumber());
    }
  }

  public final void writeSite(Site site, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeSiteFields(site, gen);
    writeExtensions(site, gen);
    gen.writeEndObject();
  }

  protected void writeSiteFields(Site site, JsonGenerator gen) throws IOException {
    if (site.hasId()) {
      gen.writeStringField("id", site.getId());
    }
    if (site.hasName()) {
      gen.writeStringField("name", site.getName());
    }
    if (site.hasDomain()) {
      gen.writeStringField("domain", site.getDomain());
    }
    writeContentCategories("cat", site.getCatList(), gen);
    writeContentCategories("sectioncat", site.getSectioncatList(), gen);
    writeContentCategories("pagecat", site.getPagecatList(), gen);
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
    if (site.hasKeywords()) {
      gen.writeStringField("keywords", site.getKeywords());
    }
  }

  public final void writeApp(App app, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeAppFields(app, gen);
    writeExtensions(app, gen);
    gen.writeEndObject();
  }

  protected void writeAppFields(App app, JsonGenerator gen) throws IOException {
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
    writeContentCategories("cat", app.getCatList(), gen);
    writeContentCategories("sectioncat", app.getSectioncatList(), gen);
    writeContentCategories("pagecat", app.getPagecatList(), gen);
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
    if (app.hasKeywords()) {
      gen.writeStringField("keywords", app.getKeywords());
    }
  }

  public final void writeContent(Content content, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeContentFields(content, gen);
    writeExtensions(content, gen);
    gen.writeEndObject();
  }

  @SuppressWarnings("deprecation")
  protected void writeContentFields(Content content, JsonGenerator gen) throws IOException {
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
    writeContentCategories("cat", content.getCatList(), gen);
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
    if (content.hasKeywords()) {
      gen.writeStringField("keywords", content.getKeywords());
    }
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
    if (content.hasArtist()) {
      gen.writeStringField("artist", content.getArtist());
    }
    if (content.hasGenre()) {
      gen.writeStringField("genre", content.getGenre());
    }
    if (content.hasAlbum()) {
      gen.writeStringField("album", content.getAlbum());
    }
    if (content.hasIsrc()) {
      gen.writeStringField("isrc", content.getIsrc());
    }
    if (content.hasProdq()) {
      gen.writeNumberField("prodq", content.getProdq().getNumber());
    }
  }

  public final void writeProducer(Producer producer, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeProducerFields(producer, gen);
    writeExtensions(producer, gen);
    gen.writeEndObject();
  }

  protected void writeProducerFields(Producer producer, JsonGenerator gen) throws IOException {
    if (producer.hasId()) {
      gen.writeStringField("id", producer.getId());
    }
    if (producer.hasName()) {
      gen.writeStringField("name", producer.getName());
    }
    writeContentCategories("cat", producer.getCatList(), gen);
    if (producer.hasDomain()) {
      gen.writeStringField("domain", producer.getDomain());
    }
  }

  public final void writePublisher(Publisher publisher, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writePublisherFields(publisher, gen);
    writeExtensions(publisher, gen);
    gen.writeEndObject();
  }

  protected void writePublisherFields(Publisher publisher, JsonGenerator gen) throws IOException {
    if (publisher.hasId()) {
      gen.writeStringField("id", publisher.getId());
    }
    if (publisher.hasName()) {
      gen.writeStringField("name", publisher.getName());
    }
    writeContentCategories("cat", publisher.getCatList(), gen);
    if (publisher.hasDomain()) {
      gen.writeStringField("domain", publisher.getDomain());
    }
  }

  public final void writeDevice(Device device, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeDeviceFields(device, gen);
    writeExtensions(device, gen);
    gen.writeEndObject();
  }

  protected void writeDeviceFields(Device device, JsonGenerator gen) throws IOException {
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
    if (device.hasGeofetch()) {
      writeIntBoolField("geofetch", device.getGeofetch(), gen);
    }
    if (device.hasMccmnc()) {
      gen.writeStringField("mccmnc", device.getMccmnc());
    }
  }

  public final void writeGeo(Geo geo, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeGeoFields(geo, gen);
    writeExtensions(geo, gen);
    gen.writeEndObject();
  }

  protected void writeGeoFields(Geo geo, JsonGenerator gen) throws IOException {
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
    if (geo.hasAccuracy()) {
      gen.writeNumberField("accuracy", geo.getAccuracy());
    }
    if (geo.hasLastfix()) {
      gen.writeNumberField("lastfix", geo.getLastfix());
    }
    if (geo.hasIpservice()) {
      gen.writeNumberField("ipservice", geo.getIpservice().getNumber());
    }
  }

  public final void writeUser(User user, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeUserFields(user, gen);
    writeExtensions(user, gen);
    gen.writeEndObject();
  }

  protected void writeUserFields(User user, JsonGenerator gen) throws IOException {
    if (user.hasId()) {
      gen.writeStringField("id", user.getId());
    }
    if (user.hasBuyeruid()) {
      gen.writeStringField("buyeruid", user.getBuyeruid());
    }
    if (user.hasYob()) {
      gen.writeNumberField("yob", user.getYob());
    }
    if (user.hasGender() && Gender.forCode(user.getGender()) != null) {
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
      writeGeo(user.getGeo(), gen);
    }
    if (user.getDataCount() != 0) {
      gen.writeArrayFieldStart("data");
      for (Data data : user.getDataList()) {
        writeData(data, gen);
      }
      gen.writeEndArray();
    }
  }

  public final void writeData(Data data, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeDataFields(data, gen);
    writeExtensions(data, gen);
    gen.writeEndObject();
  }

  protected void writeDataFields(Data data, JsonGenerator gen) throws IOException {
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
  }

  public final void writeSegment(Segment segment, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeSegmentFields(segment, gen);
    writeExtensions(segment, gen);
    gen.writeEndObject();
  }

  protected void writeSegmentFields(Segment segment, JsonGenerator gen) throws IOException {
    if (segment.hasId()) {
      gen.writeStringField("id", segment.getId());
    }
    if (segment.hasName()) {
      gen.writeStringField("name", segment.getName());
    }
    if (segment.hasValue()) {
      gen.writeStringField("value", segment.getValue());
    }
  }

  public final void writeRegs(Regs regs, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeRegsFields(regs, gen);
    writeExtensions(regs, gen);
    gen.writeEndObject();
  }

  protected void writeRegsFields(Regs regs, JsonGenerator gen) throws IOException {
    if (regs.hasCoppa()) {
      writeIntBoolField("coppa", regs.getCoppa(), gen);
    }
  }

  public final void writeSource(Source source, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeSourceFields(source, gen);
    writeExtensions(source, gen);
    gen.writeEndObject();
  }

  protected void writeSourceFields(Source source, JsonGenerator gen) throws IOException {
    if (source.hasFd()) {
      writeIntBoolField("fd", source.getFd(), gen);
    }
    if (source.hasTid()) {
      gen.writeStringField("tid", source.getTid());
    }
    if (source.hasPchain()) {
      gen.writeStringField("pchain", source.getPchain());
    }
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
  public final void writeBidResponse(BidResponse resp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeBidResponseFields(resp, gen);
    writeExtensions(resp, gen);
    gen.writeEndObject();
    gen.flush();
  }

  protected void writeBidResponseFields(BidResponse resp, JsonGenerator gen) throws IOException {
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
  }

  public final void writeSeatBid(SeatBid seatbid, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeSeatBidFields(seatbid, gen);
    writeExtensions(seatbid, gen);
    gen.writeEndObject();
  }

  protected void writeSeatBidFields(SeatBid seatbid, JsonGenerator gen) throws IOException {
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
  }

  public final void writeBid(Bid bid, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeBidFields(bid, gen);
    writeExtensions(bid, gen);
    gen.writeEndObject();
  }

  protected void writeBidFields(Bid bid, JsonGenerator gen) throws IOException {
    gen.writeStringField("id", bid.getId());
    gen.writeStringField("impid", bid.getImpid());
    gen.writeNumberField("price", bid.getPrice());
    if (bid.hasAdid()) {
      gen.writeStringField("adid", bid.getAdid());
    }
    if (bid.hasNurl()) {
      gen.writeStringField("nurl", bid.getNurl());
    }
    switch (bid.getAdmOneofCase()) {
      case ADM:
        gen.writeStringField("adm", bid.getAdm());
        break;
      case ADM_NATIVE:
        gen.writeFieldName("adm");
        if (factory().isForceNativeAsObject()) {
          nativeWriter().writeNativeResponse(bid.getAdmNative(), gen);
        } else {
          gen.writeString(nativeWriter().writeNativeResponse(bid.getAdmNative()));
        }
        break;
      case ADMONEOF_NOT_SET:
        checkRequired(false);
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
    writeContentCategories("cat", bid.getCatList(), gen);
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
    if (bid.hasApi()) {
      gen.writeNumberField("api", bid.getApi().getNumber());
    }
    if (bid.hasProtocol()) {
      gen.writeNumberField("protocol", bid.getProtocol().getNumber());
    }
    if (bid.hasQagmediarating()) {
      gen.writeNumberField("qagmediarating", bid.getQagmediarating().getNumber());
    }
    if (bid.hasExp()) {
      gen.writeNumberField("exp", bid.getExp());
    }
    if (bid.hasBurl()) {
      gen.writeStringField("burl", bid.getBurl());
    }
    if (bid.hasLurl()) {
      gen.writeStringField("lurl", bid.getLurl());
    }
    if (bid.hasTactic()) {
      gen.writeStringField("tactic", bid.getTactic());
    }
    if (bid.hasLanguage()) {
      gen.writeStringField("language", bid.getLanguage());
    }
    if (bid.hasWratio()) {
      gen.writeNumberField("wratio", bid.getWratio());
    }
    if (bid.hasHratio()) {
      gen.writeNumberField("hratio", bid.getHratio());
    }
  }

  protected final OpenRtbNativeJsonWriter nativeWriter() {
    if (nativeWriter == null) {
      nativeWriter = factory().newNativeWriter();
    }
    return nativeWriter;
  }
}
