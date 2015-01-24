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

import com.google.common.io.Closeables;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.App;
import com.google.openrtb.OpenRtb.BidRequest.Content;
import com.google.openrtb.OpenRtb.BidRequest.Content.Context;
import com.google.openrtb.OpenRtb.BidRequest.Content.QAGMediaRating;
import com.google.openrtb.OpenRtb.BidRequest.Content.SourceRelationship;
import com.google.openrtb.OpenRtb.BidRequest.Content.VideoQuality;
import com.google.openrtb.OpenRtb.BidRequest.Data;
import com.google.openrtb.OpenRtb.BidRequest.Data.Segment;
import com.google.openrtb.OpenRtb.BidRequest.Device;
import com.google.openrtb.OpenRtb.BidRequest.Device.ConnectionType;
import com.google.openrtb.OpenRtb.BidRequest.Device.DeviceType;
import com.google.openrtb.OpenRtb.BidRequest.Geo;
import com.google.openrtb.OpenRtb.BidRequest.Geo.LocationType;
import com.google.openrtb.OpenRtb.BidRequest.Impression;
import com.google.openrtb.OpenRtb.BidRequest.Impression.AdPosition;
import com.google.openrtb.OpenRtb.BidRequest.Impression.ApiFramework;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner.AdType;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Banner.ExpandableDirection;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Native;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP.Deal;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.CompanionType;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.ContentDelivery;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Linearity;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.PlaybackMethod;
import com.google.openrtb.OpenRtb.BidRequest.Impression.Video.Protocol;
import com.google.openrtb.OpenRtb.BidRequest.Producer;
import com.google.openrtb.OpenRtb.BidRequest.Publisher;
import com.google.openrtb.OpenRtb.BidRequest.Regulations;
import com.google.openrtb.OpenRtb.BidRequest.Site;
import com.google.openrtb.OpenRtb.BidRequest.User;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.NoBidReasonCode;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.CreativeAttribute;
import com.google.protobuf.ByteString;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import static com.google.openrtb.json.OpenRtbJsonUtils.*;

/**
 * Desserializes OpenRTB BidRequest/BidResponse messages from JSON.
 * <p>
 * This class is threadsafe.
 */
public class OpenRtbJsonReader extends AbstractOpenRtbJsonReader {

  protected OpenRtbJsonReader(OpenRtbJsonFactory factory) {
    super(factory);
  }

  /**
   * Desserializes a {@link BidRequest} from a JSON string, provided as a {@link ByteString}.
   */
  public BidRequest readBidRequest(ByteString bs) throws IOException {
    return readBidRequest(bs.newInput());
  }

  /**
   * Desserializes a {@link BidRequest} from a JSON string, provided as a {@link CharSequence}.
   */
  public BidRequest readBidRequest(CharSequence chars) throws IOException {
    return readBidRequest(new CharSequenceReader(chars));
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, streamed from a {@link Reader}.
   */
  public BidRequest readBidRequest(Reader reader) throws IOException {
    return readBidRequest(factory().getJsonFactory().createParser(reader)).build();
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, streamed from an {@link InputStream}.
   */
  public BidRequest readBidRequest(InputStream is) throws IOException {
    try {
      return readBidRequest(factory().getJsonFactory().createParser(is)).build();
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public final BidRequest.Builder readBidRequest(JsonParser par) throws IOException {
    BidRequest.Builder req = BidRequest.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBidRequestField(par, req, fieldName);
      }
    }
    return req;
  }

  protected void readBidRequestField(JsonParser par, BidRequest.Builder req, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        req.setId(par.getText());
        break;
      case "imp":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addImp(readImp(par));
        }
        break;
      case "site":
        req.setSite(readSite(par));
        break;
      case "app":
        req.setApp(readApp(par));
        break;
      case "device":
        req.setDevice(readDevice(par));
        break;
      case "user":
        req.setUser(readUser(par));
        break;
      case "test":
        req.setTest(getIntBoolValue(par));
        break;
      case "at":
        req.setAt(par.getIntValue());
        break;
      case "tmax":
        req.setTmax(par.getIntValue());
        break;
      case "wseat":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addWseat(par.getText());
        }
        break;
      case "allimps":
        req.setAllimps(getIntBoolValue(par));
        break;
      case "cur":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addCur(par.getText());
        }
        break;
      case "bcat":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addBcat(par.getText());
        }
        break;
      case "badv":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addBadv(par.getText());
        }
        break;
      case "regs":
        req.setRegs(readRegulations(par));
        break;
      case "ext":
        readExtensions(req, par, "BidRequest");
        break;
    }
  }

  protected final Regulations.Builder readRegulations(JsonParser par) throws IOException {
    Regulations.Builder reg = Regulations.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRegulationsField(par, reg, fieldName);
      }
    }
    return reg;
  }

  protected void readRegulationsField(JsonParser par, Regulations.Builder reg, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "coppa":
        reg.setCoppa(getIntBoolValue(par));
        break;
      case "ext":
        readExtensions(reg, par, "BidRequest.regs");
        break;
    }
  }

  protected final Impression.Builder readImp(JsonParser par) throws IOException {
    Impression.Builder imp = Impression.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readImpField(par, imp, fieldName);
      }
    }
    return imp;
  }

  protected void readImpField(JsonParser par, Impression.Builder imp, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        imp.setId(par.getText());
        break;
      case "banner":
        imp.setBanner(readBanner(par));
        break;
      case "video":
        imp.setVideo(readVideo(par));
        break;
      case "native":
        imp.setNative(readNative(par));
        break;
      case "displaymanager":
        imp.setDisplaymanager(par.getText());
        break;
      case "displaymanagerver":
        imp.setDisplaymanagerver(par.getText());
        break;
      case "instl":
        imp.setInstl(getIntBoolValue(par));
        break;
      case "tagid":
        imp.setTagid(par.getText());
        break;
      case "bidfloor":
        imp.setBidfloor(getMillisFromDecimalValue(par));
        break;
      case "bidfloorcur":
        imp.setBidfloorcur(par.getText());
        break;
      case "secure":
        imp.setSecure(getIntBoolValue(par));
        break;
      case "iframebuster":
        for (startArray(par); endArray(par); par.nextToken()) {
          imp.addIframebuster(par.getText());
        }
        break;
      case "pmp":
        imp.setPmp(readPMP(par));
        break;
      case "ext":
        readExtensions(imp, par, "BidRequest.imp");
        break;
    }
  }

  protected final Native.Builder readNative(JsonParser par) throws IOException {
    Native.Builder nativ = Native.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readNativeField(par, nativ, fieldName);
      }
    }
    return nativ;
  }

  protected void readNativeField(JsonParser par, Native.Builder nativ, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "request": {
          OpenRtbNativeJsonReader nativeReader = factory().newNativeReader();
          nativ.setRequest(nativeReader.readNativeRequest(new CharArrayReader(
              par.getTextCharacters(), par.getTextOffset(), par.getTextLength())));
        }
        break;
      case "ver":
        nativ.setVer(par.getText());
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          nativ.addApi(ApiFramework.valueOf(par.getIntValue()));
        }
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
          nativ.addBattr(CreativeAttribute.valueOf(par.getIntValue()));
        }
        break;
      case "ext":
        readExtensions(nativ, par, "BidRequest.imp.native");
        break;
    }
  }

  protected final PMP.Builder readPMP(JsonParser par) throws IOException {
    PMP.Builder pmp = PMP.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readPMPField(par, pmp, fieldName);
      }
    }
    return pmp;
  }

  protected void readPMPField(JsonParser par, PMP.Builder pmp, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "private_auction":
        pmp.setPrivateAuction(getIntBoolValue(par));
        break;
      case "deals":
        for (startArray(par); endArray(par); par.nextToken()) {
          pmp.addDeals(readDeal(par));
        }
        break;
      case "ext":
        readExtensions(pmp, par, "BidRequest.imp.pmp");
        break;
    }
  }

  protected final Deal.Builder readDeal(JsonParser par) throws IOException {
    Deal.Builder deal = Deal.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDealField(par, deal, fieldName);
      }
    }
    return deal;
  }

  protected void readDealField(JsonParser par, Deal.Builder deal, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        deal.setId(par.getText());
        break;
      case "bidfloor":
        deal.setBidfloor(getMillisFromDecimalValue(par));
        break;
      case "bidfloorcur":
        deal.setBidfloorcur(par.getText());
        break;
      case "wseat":
        for (startArray(par); endArray(par); par.nextToken()) {
          deal.addWseat(par.getText());
        }
        break;
      case "wadomain":
        for (startArray(par); endArray(par); par.nextToken()) {
          deal.addWadomain(par.getText());
        }
        break;
      case "at":
        deal.setAt(par.getIntValue());
        break;
      case "ext":
        readExtensions(deal, par, "BidRequest.imp.pmp.deals");
        break;
    }
  }

  protected final Video.Builder readVideo(JsonParser par) throws IOException {
    Video.Builder video = Video.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readVideoField(par, video, fieldName);
      }
    }
    return video;
  }

  protected void readVideoField(JsonParser par, Video.Builder video, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "mimes":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addMimes(par.getText());
        }
        break;
      case "minduration":
        video.setMinduration(par.getIntValue());
        break;
      case "maxduration":
        video.setMaxduration(par.getIntValue());
        break;
      case "protocol":
        video.setDeprecatedProtocol(Protocol.valueOf(par.getIntValue()));
        break;
      case "protocols":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addProtocols(Protocol.valueOf(par.getIntValue()));
        }
        break;
      case "w":
        video.setW(par.getIntValue());
        break;
      case "h":
        video.setH(par.getIntValue());
        break;
      case "startdelay":
        video.setStartdelay(par.getIntValue());
        break;
      case "linearity":
        video.setLinearity(Linearity.valueOf(par.getIntValue()));
        break;
      case "sequence":
        video.setSequence(par.getIntValue());
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addBattr(CreativeAttribute.valueOf(par.getIntValue()));
        }
        break;
      case "maxextended":
        video.setMaxextended(par.getIntValue());
        break;
      case "minbitrate":
        video.setMinbitrate(par.getIntValue());
        break;
      case "maxbitrate":
        video.setMaxbitrate(par.getIntValue());
        break;
      case "boxingallowed":
        video.setBoxingallowed(getIntBoolValue(par));
        break;
      case "playbackmethod":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addPlaybackmethod(PlaybackMethod.valueOf(par.getIntValue()));
        }
        break;
      case "delivery":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addDelivery(ContentDelivery.valueOf(par.getIntValue()));
        }
        break;
      case "pos":
        video.setPos(AdPosition.valueOf(par.getIntValue()));
        break;
      case "companionad":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addCompanionad(readBanner(par));
        }
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addApi(ApiFramework.valueOf(par.getIntValue()));
        }
        break;
      case "companiontype":
        for (startArray(par); endArray(par); par.nextToken()) {
          video.addCompaniontype(CompanionType.valueOf(par.getIntValue()));
        }
        break;
      case "ext":
        readExtensions(video, par, "BidRequest.imp.video");
        break;
    }
  }

  protected final Banner.Builder readBanner(JsonParser par) throws IOException {
    Banner.Builder banner = Banner.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBannerField(par, banner, fieldName);
      }
    }
    return banner;
  }

  protected void readBannerField(JsonParser par, Banner.Builder banner, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "w":
        banner.setW(par.getIntValue());
        break;
      case "h":
        banner.setH(par.getIntValue());
        break;
      case "wmax":
        banner.setWmax(par.getIntValue());
        break;
      case "hmax":
        banner.setHmax(par.getIntValue());
        break;
      case "wmin":
        banner.setWmin(par.getIntValue());
        break;
      case "hmin":
        banner.setHmin(par.getIntValue());
        break;
      case "id":
        banner.setId(par.getText());
        break;
      case "btype":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addBtype(AdType.valueOf(par.getIntValue()));
        }
        break;
      case "battr":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addBattr(CreativeAttribute.valueOf(par.getIntValue()));
        }
        break;
      case "pos":
        banner.setPos(AdPosition.valueOf(par.getIntValue()));
        break;
      case "mimes":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addMimes(par.getText());
        }
        break;
      case "topframe":
        banner.setTopframe(getIntBoolValue(par));
        break;
      case "expdir":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addExpdir(ExpandableDirection.valueOf(par.getIntValue()));
        }
        break;
      case "api":
        for (startArray(par); endArray(par); par.nextToken()) {
          banner.addApi(ApiFramework.valueOf(par.getIntValue()));
        }
        break;
      case "ext":
        readExtensions(banner, par, "BidRequest.imp.banner");
        break;
    }
  }

  protected final Site.Builder readSite(JsonParser par) throws IOException {
    Site.Builder site = Site.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSiteField(par, site, fieldName);
      }
    }
    return site;
  }

  protected void readSiteField(JsonParser par, Site.Builder site, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        site.setId(par.getText());
        break;
      case "name":
        site.setName(par.getText());
        break;
      case "domain":
        site.setDomain(par.getText());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          site.addCat(par.getText());
        }
        break;
      case "sectioncat":
        for (startArray(par); endArray(par); par.nextToken()) {
          site.addSectioncat(par.getText());
        }
        break;
      case "pagecat":
        for (startArray(par); endArray(par); par.nextToken()) {
          site.addPagecat(par.getText());
        }
        break;
      case "page":
        site.setPage(par.getText());
        break;
      case "ref":
        site.setRef(par.getText());
        break;
      case "search":
        site.setSearch(par.getText());
        break;
      case "mobile":
        site.setMobile(getIntBoolValue(par));
        break;
      case "privacypolicy":
        site.setPrivacypolicy(getIntBoolValue(par));
        break;
      case "publisher":
        site.setPublisher(readPublisher(par));
        break;
      case "content":
        site.setContent(readContent(par));
        break;
      case "keywords":
        site.setKeywords(par.getText());
        break;
      case "ext":
        readExtensions(site, par, "BidRequest.site");
        break;
    }
  }

  protected final App.Builder readApp(JsonParser par) throws IOException {
    App.Builder app = App.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String name = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readAppField(par, app, name);
      }
    }
    return app;
  }

  protected void readAppField(JsonParser par, App.Builder app, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        app.setId(par.getText());
        break;
      case "name":
        app.setName(par.getText());
        break;
      case "bundle":
        app.setBundle(par.getText());
        break;
      case "domain":
        app.setDomain(par.getText());
        break;
      case "storeurl":
        app.setStoreurl(par.getText());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          app.addCat(par.getText());
        }
        break;
      case "sectioncat":
        for (startArray(par); endArray(par); par.nextToken()) {
          app.addSectioncat(par.getText());
        }
        break;
      case "pagecat":
        for (startArray(par); endArray(par); par.nextToken()) {
          app.addPagecat(par.getText());
        }
        break;
      case "ver":
        app.setVer(par.getText());
        break;
      case "privacypolicy":
        app.setPrivacypolicy(getIntBoolValue(par));
        break;
      case "paid":
        app.setPaid(getIntBoolValue(par));
        break;
      case "publisher":
        app.setPublisher(readPublisher(par));
        break;
      case "content":
        app.setContent(readContent(par));
        break;
      case "keywords":
        app.setKeywords(par.getText());
        break;
      case "ext":
        readExtensions(app, par, "BidRequest.app");
        break;
    }
  }

  protected final Content.Builder readContent(JsonParser par) throws IOException {
    Content.Builder content = Content.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String name = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readContentField(par, content, name);
      }
    }
    return content;
  }

  protected void readContentField(JsonParser par, Content.Builder content, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        content.setId(par.getText());
        break;
      case "episode":
        content.setEpisode(par.getIntValue());
        break;
      case "title":
        content.setTitle(par.getText());
        break;
      case "series":
        content.setSeries(par.getText());
        break;
      case "season":
        content.setSeason(par.getText());
        break;
      case "producer":
        content.setProducer(readProducer(par));
        break;
      case "url":
        content.setUrl(par.getText());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          content.addCat(par.getText());
        }
        break;
      case "videoquality":
        content.setVideoquality(VideoQuality.valueOf(par.getIntValue()));
        break;
      case "context":
        content.setContext(Context.valueOf(par.getIntValue()));
        break;
      case "contentrating":
        content.setContentrating(par.getText());
        break;
      case "userrating":
        content.setUserrating(par.getText());
        break;
      case "qagmediarating":
        content.setQagmediarating(QAGMediaRating.valueOf(par.getIntValue()));
        break;
      case "keywords":
        content.setKeywords(par.getText());
        break;
      case "livestream":
        content.setLivestream(getIntBoolValue(par));
        break;
      case "sourcerelationship":
        content.setSourcerelationship(SourceRelationship.valueOf(par.getIntValue()));
        break;
      case "len":
        content.setLen(par.getIntValue());
        break;
      case "language":
        content.setLanguage(par.getText());
        break;
      case "embeddable":
        content.setEmbeddable(getIntBoolValue(par));
        break;
      case "ext":
        readExtensions(content, par, "BidRequest.app.content");
        break;
    }
  }

  protected final Producer.Builder readProducer(JsonParser par) throws IOException {
    Producer.Builder producer = Producer.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readProducerField(par, producer, fieldName);
      }
    }
    return producer;
  }

  protected void readProducerField(JsonParser par, Producer.Builder producer, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        producer.setId(par.getText());
        break;
      case "name":
        producer.setName(par.getText());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          producer.addCat(par.getText());
        }
        break;
      case "domain":
        producer.setDomain(par.getText());
        break;
      case "ext":
        readExtensions(producer, par, "BidRequest.app.content.producer");
        break;
    }
  }

  protected final Publisher.Builder readPublisher(JsonParser par) throws IOException {
    Publisher.Builder publisher = Publisher.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readPublisherField(par, publisher, fieldName);
      }
    }
    return publisher;
  }

  protected void readPublisherField(JsonParser par, Publisher.Builder publisher, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        publisher.setId(par.getText());
        break;
      case "name":
        publisher.setName(par.getText());
        break;
      case "cat":
        for (startArray(par); endArray(par); par.nextToken()) {
          publisher.addCat(par.getText());
        }
        break;
      case "domain":
        publisher.setDomain(par.getText());
        break;
      case "ext":
        readExtensions(publisher, par, "BidRequest.app.publisher");
        break;
    }
  }

  protected final Device.Builder readDevice(JsonParser par) throws IOException {
    Device.Builder device = Device.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDeviceField(par, device, fieldName);
      }
    }
    return device;
  }

  protected void readDeviceField(JsonParser par, Device.Builder device, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "ua":
        device.setUa(par.getText());
        break;
      case "geo":
        device.setGeo(readGeo(par, "BidRequest.device.geo"));
        break;
      case "dnt":
        device.setDnt(getIntBoolValue(par));
        break;
      case "lmt":
        device.setLmt(getIntBoolValue(par));
        break;
      case "ip":
        device.setIp(par.getText());
        break;
      case "ipv6":
        device.setIpv6(par.getText());
        break;
      case "devicetype":
        device.setDevicetype(DeviceType.valueOf(par.getIntValue()));
        break;
      case "make":
        device.setMake(par.getText());
        break;
      case "model":
        device.setModel(par.getText());
        break;
      case "os":
        device.setOs(par.getText());
        break;
      case "osv":
        device.setOsv(par.getText());
        break;
      case "hwv":
        device.setHwv(par.getText());
        break;
      case "w":
        device.setW(par.getIntValue());
        break;
      case "h":
        device.setH(par.getIntValue());
        break;
      case "ppi":
        device.setPpi(par.getIntValue());
        break;
      case "pxratio":
        device.setPxratio(getDoubleValue(par));
        break;
      case "js":
        device.setJs(getIntBoolValue(par));
        break;
      case "flashver":
        device.setFlashver(par.getText());
        break;
      case "language":
        device.setLanguage(par.getText());
        break;
      case "carrier":
        device.setCarrier(par.getText());
        break;
      case "connectiontype":
        device.setConnectiontype(ConnectionType.valueOf(par.getIntValue()));
        break;
      case "ifa":
        device.setIfa(par.getText());
        break;
      case "didsha1":
        device.setDidsha1(par.getText());
        break;
      case "didmd5":
        device.setDidmd5(par.getText());
        break;
      case "dpidsha1":
        device.setDpidsha1(par.getText());
        break;
      case "dpidmd5":
        device.setDpidmd5(par.getText());
        break;
      case "macsha1":
        device.setMacsha1(par.getText());
        break;
      case "macmd5":
        device.setMacmd5(par.getText());
        break;
      case "ext":
        readExtensions(device, par, "BidRequest.device");
        break;
    }
  }

  protected final Geo.Builder readGeo(JsonParser par, String path) throws IOException {
    Geo.Builder geo = Geo.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readGeoField(par, path, geo, fieldName);
      }
    }
    return geo;
  }

  protected void readGeoField(JsonParser par, String path, Geo.Builder geo, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "lat":
        geo.setLat(getDoubleValue(par));
        break;
      case "lon":
        geo.setLon(getDoubleValue(par));
        break;
      case "type":
        geo.setType(LocationType.valueOf(par.getIntValue()));
        break;
      case "country":
        geo.setCountry(par.getText());
        break;
      case "region":
        geo.setRegion(par.getText());
        break;
      case "regionfips104":
        geo.setRegionfips104(par.getText());
        break;
      case "metro":
        geo.setMetro(par.getText());
        break;
      case "city":
        geo.setCity(par.getText());
        break;
      case "zip":
        geo.setZip(par.getText());
        break;
      case "utcoffset":
        geo.setUtcoffset(par.getIntValue());
        break;
      case "ext":
        readExtensions(geo, par, path);
        break;
    }
  }

  protected final User.Builder readUser(JsonParser par) throws IOException {
    User.Builder user = User.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readUserField(par, user, fieldName);
      }
    }
    return user;
  }

  protected void readUserField(JsonParser par, User.Builder user, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        user.setId(par.getText());
        break;
      case "buyeruid":
        user.setBuyeruid(par.getText());
        break;
      case "yob":
        user.setYob(par.getIntValue());
        break;
      case "gender":
        user.setGender(par.getText());
        break;
      case "keywords":
        user.setKeywords(par.getText());
        break;
      case "customdata":
        user.setCustomdata(par.getText());
        break;
      case "geo":
        user.setGeo(readGeo(par, "BidRequest.user.geo"));
        break;
      case "data":
        for (startArray(par); endArray(par); par.nextToken()) {
          user.addData(readData(par));
        }
        break;
      case "ext":
        readExtensions(user, par, "BidRequest.user");
        break;
    }
  }

  protected final Data.Builder readData(JsonParser par) throws IOException {
    Data.Builder data = Data.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readDataField(par, data, fieldName);
      }
    }
    return data;
  }

  protected void readDataField(JsonParser par, Data.Builder data, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        data.setId(par.getText());
        break;
      case "name":
        data.setName(par.getText());
        break;
      case "segment":
        for (startArray(par); endArray(par); par.nextToken()) {
          data.addSegment(readSegment(par));
        }
        break;
      case "ext":
        readExtensions(data, par, "BidRequest.user.data");
        break;
    }
  }

  protected final Segment.Builder readSegment(JsonParser par) throws IOException {
    Segment.Builder segment = Segment.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSegmentField(par, segment, fieldName);
      }
    }
    return segment;
  }

  protected void readSegmentField(JsonParser par, Segment.Builder segment, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        segment.setId(par.getText());
        break;
      case "name":
        segment.setName(par.getText());
        break;
      case "value":
        segment.setValue(par.getText());
        break;
      case "ext":
        readExtensions(segment, par, "BidRequest.user.data.segment");
        break;
    }
  }

  /**
   * Desserializes a {@link BidResponse} from a JSON string, provided as a {@link ByteString}.
   */
  public BidResponse readBidResponse(ByteString bs) throws IOException {
    return readBidResponse(bs.newInput());
  }

  /**
   * Desserializes a {@link BidResponse} from a JSON string, provided as a {@link CharSequence}.
   */
  public BidResponse readBidResponse(CharSequence chars) throws IOException {
    return readBidResponse(new CharSequenceReader(chars));
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, streamed from a {@link Reader}.
   */
  public BidResponse readBidResponse(Reader reader) throws IOException {
    return readBidResponse(factory().getJsonFactory().createParser(reader)).build();
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, streamed from an {@link InputStream}.
   */
  public BidResponse readBidResponse(InputStream is) throws IOException {
    try {
      return readBidResponse(factory().getJsonFactory().createParser(is)).build();
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public final BidResponse.Builder readBidResponse(JsonParser par) throws IOException {
    BidResponse.Builder resp = BidResponse.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBidResponseField(par, resp, fieldName);
      }
    }
    return resp;
  }

  protected void readBidResponseField(JsonParser par, BidResponse.Builder resp, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        resp.setId(par.getText());
        break;
      case "seatbid":
        for (startArray(par); endArray(par); par.nextToken()) {
          resp.addSeatbid(readSeatBid(par));
        }
        break;
      case "bidid":
        resp.setBidid(par.getText());
        break;
      case "cur":
        resp.setCur(par.getText());
        break;
      case "customdata":
        resp.setCustomdata(par.getText());
        break;
      case "nbr":
        resp.setNbr(NoBidReasonCode.valueOf(par.getIntValue()));
        break;
      case "ext":
        readExtensions(resp, par, "BidResponse");
        break;
    }
  }

  protected final SeatBid.Builder readSeatBid(JsonParser par) throws IOException {
    SeatBid.Builder seatbid = SeatBid.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readSeatBidField(par, seatbid, fieldName);
      }
    }
    return seatbid;
  }

  protected void readSeatBidField(JsonParser par, SeatBid.Builder seatbid, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "bid":
        for (startArray(par); endArray(par); par.nextToken()) {
          seatbid.addBid(readBid(par));
        }
        break;
      case "seat":
        seatbid.setSeat(par.getText());
        break;
      case "group":
        seatbid.setGroup(getIntBoolValue(par));
        break;
      case "ext":
        readExtensions(seatbid, par, "BidResponse.seatbid");
        break;
    }
  }

  protected final Bid.Builder readBid(JsonParser par) throws IOException {
    Bid.Builder bid = Bid.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readBidField(par, bid, fieldName);
      }
    }
    return bid;
  }

  protected void readBidField(JsonParser par, Bid.Builder bid, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "id":
        bid.setId(par.getText());
        break;
      case "impid":
        bid.setImpid(par.getText());
        break;
      case "price":
        bid.setPrice(getMillisFromDecimalValue(par));
        break;
      case "adid":
        bid.setAdid(par.getText());
        break;
      case "nurl":
        bid.setNurl(par.getText());
        break;
      case "adm":
        bid.setAdm(par.getText());
        break;
      case "adomain":
        for (startArray(par); endArray(par); par.nextToken()) {
          bid.addAdomain(par.getText());
        }
        break;
      case "bundle":
        bid.setBundle(par.getText());
        break;
      case "iurl":
        bid.setIurl(par.getText());
        break;
      case "cid":
        bid.setCid(par.getText());
        break;
      case "crid":
        bid.setCrid(par.getText());
        break;
      case "cat":
        bid.setCat(par.getText());
        break;
      case "attr":
        for (startArray(par); endArray(par); par.nextToken()) {
          bid.addAttr(CreativeAttribute.valueOf(par.getIntValue()));
        }
        break;
      case "dealid":
        bid.setDealid(par.getText());
        break;
      case "w":
        bid.setW(par.getIntValue());
        break;
      case "h":
        bid.setH(par.getIntValue());
        break;
      case "ext":
        readExtensions(bid, par, "BidResponse.seatbid.bid");
        break;
    }
  }
}
