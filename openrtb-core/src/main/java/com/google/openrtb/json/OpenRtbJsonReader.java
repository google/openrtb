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

import static com.google.openrtb.json.OpenRtbJsonUtils.endArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.endObject;
import static com.google.openrtb.json.OpenRtbJsonUtils.getCurrentName;
import static com.google.openrtb.json.OpenRtbJsonUtils.nextDoubleValue;
import static com.google.openrtb.json.OpenRtbJsonUtils.startArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.startObject;

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
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP;
import com.google.openrtb.OpenRtb.BidRequest.Impression.PMP.DirectDeal;
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
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.openrtb.OpenRtb.CreativeAttribute;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

/**
 * Desserializes OpenRTB messages from JSON.
 * <p>
 * This class is threadsafe.
 */
public class OpenRtbJsonReader {
  private final OpenRtbJsonFactory factory;

  protected OpenRtbJsonReader(OpenRtbJsonFactory factory) {
    this.factory = factory;
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
    return readBidRequest(factory.getJsonFactory().createParser(reader)).build();
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, streamed from an {@link InputStream}.
   */
  public BidRequest readBidRequest(InputStream is) throws IOException {
    try {
      return readBidRequest(factory.getJsonFactory().createParser(is)).build();
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link BidRequest} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public BidRequest.Builder readBidRequest(JsonParser par) throws IOException {
    BidRequest.Builder req = BidRequest.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          req.setId(par.nextTextValue());
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
          req.setTest(par.nextIntValue(0) == 1);
          break;
        case "at":
          req.setAt(par.nextIntValue(2));
          break;
        case "tmax":
          req.setTmax(par.nextIntValue(0));
          break;
        case "wseat":
          for (startArray(par); endArray(par); par.nextToken()) {
            req.addWseat(par.getText());
          }
          break;
        case "allimps":
          req.setAllimps(par.nextIntValue(0) == 1);
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
    return req;
  }

  protected Regulations.Builder readRegulations(JsonParser par) throws IOException {
    Regulations.Builder reg = Regulations.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "coppa":
          reg.setCoppa(par.nextIntValue(0) == 1);
          break;
        case "ext":
          readExtensions(reg, par, "BidRequest.regs");
          break;
      }
    }
    return reg;
  }

  protected Impression.Builder readImp(JsonParser par) throws IOException {
    Impression.Builder imp = Impression.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          imp.setId(par.nextTextValue());
          break;
        case "banner":
          imp.setBanner(readBanner(par));
          break;
        case "video":
          imp.setVideo(readVideo(par));
          break;
        case "displaymanager":
          imp.setDisplaymanager(par.nextTextValue());
          break;
        case "displaymanagerver":
          imp.setDisplaymanagerver(par.nextTextValue());
          break;
        case "instl":
          imp.setInstl(par.nextIntValue(0) == 1);
          break;
        case "tagid":
          imp.setTagid(par.nextTextValue());
          break;
        case "bidfloor":
          imp.setBidfloor(nextDoubleValue(par));
          break;
        case "bidfloorcur":
          imp.setBidfloorcur(par.nextTextValue());
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
    return imp;
  }

  protected PMP.Builder readPMP(JsonParser par) throws IOException {
    PMP.Builder pmp = PMP.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "private_auction":
          pmp.setPrivateAuction(par.nextIntValue(0) == 1);
          break;
        case "deals":
          for (startArray(par); endArray(par); par.nextToken()) {
            pmp.addDeals(readDirectDeal(par));
          }
          break;
        case "ext":
          readExtensions(pmp, par, "BidRequest.imp.pmp");
          break;
      }
    }
    return pmp;
  }

  protected DirectDeal.Builder readDirectDeal(JsonParser par) throws IOException {
    DirectDeal.Builder deal = DirectDeal.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          deal.setId(par.nextTextValue());
          break;
        case "bidfloor":
          deal.setBidfloor(nextDoubleValue(par));
          break;
        case "bidfloorcur":
          deal.setBidfloorcur(par.nextTextValue());
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
          deal.setAt(par.nextIntValue(0));
          break;
        case "ext":
          readExtensions(deal, par, "BidRequest.imp.pmp.deals");
          break;
      }
    }
    return deal;
  }

  protected Video.Builder readVideo(JsonParser par) throws IOException {
    Video.Builder video = Video.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "mimes":
          for (startArray(par); endArray(par); par.nextToken()) {
            video.addMimes(par.getText());
          }
          break;
        case "linearity":
          video.setLinearity(Linearity.valueOf(par.nextIntValue(0)));
          break;
        case "minduration":
          video.setMinduration(par.nextIntValue(0));
          break;
        case "maxduration":
          video.setMaxduration(par.nextIntValue(0));
          break;
        case "protocol":
          video.setProtocol(Protocol.valueOf(par.nextIntValue(0)));
          break;
        case "w":
          video.setW(par.nextIntValue(0));
          break;
        case "h":
          video.setH(par.nextIntValue(0));
          break;
        case "startdelay":
          video.setStartdelay(par.nextIntValue(0));
          break;
        case "sequence":
          video.setSequence(par.nextIntValue(0));
          break;
        case "battr":
          for (startArray(par); endArray(par); par.nextToken()) {
            video.addBattr(CreativeAttribute.valueOf(par.getIntValue()));
          }
          break;
        case "maxextended":
          video.setMaxextended(par.nextIntValue(0));
          break;
        case "minbitrate":
          video.setMinbitrate(par.nextIntValue(0));
          break;
        case "maxbitrate":
          video.setMaxbitrate(par.nextIntValue(0));
          break;
        case "boxingallowed":
          video.setBoxingallowed(par.nextIntValue(0) == 1);
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
          video.setPos(AdPosition.valueOf(par.nextIntValue(0)));
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
    return video;
  }

  protected Banner.Builder readBanner(JsonParser par) throws IOException {
    Banner.Builder banner = Banner.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "w":
          banner.setW(par.nextIntValue(0));
          break;
        case "h":
          banner.setH(par.nextIntValue(0));
          break;
        case "wmax":
          banner.setWmax(par.nextIntValue(0));
          break;
        case "hmax":
          banner.setHmax(par.nextIntValue(0));
          break;
        case "wmin":
          banner.setWmin(par.nextIntValue(0));
          break;
        case "hmin":
          banner.setHmin(par.nextIntValue(0));
          break;
        case "id":
          banner.setId(par.nextTextValue());
          break;
        case "pos":
          banner.setPos(AdPosition.valueOf(par.nextIntValue(AdPosition.POSITION_UNKNOWN_VALUE)));
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
        case "mimes":
          for (startArray(par); endArray(par); par.nextToken()) {
            banner.addMimes(par.getText());
          }
          break;
        case "topframe":
          banner.setTopframe(par.nextIntValue(0) == 1);
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
    return banner;
  }

  protected Site.Builder readSite(JsonParser par) throws IOException {
    Site.Builder site = Site.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          site.setId(par.nextTextValue());
          break;
        case "name":
          site.setName(par.nextTextValue());
          break;
        case "domain":
          site.setDomain(par.nextTextValue());
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
          site.setPage(par.nextTextValue());
          break;
        case "privacypolicy":
          site.setPrivacypolicy(par.nextIntValue(0) == 1);
          break;
        case "ref":
          site.setRef(par.nextTextValue());
          break;
        case "search":
          site.setSearch(par.nextTextValue());
          break;
        case "mobile":
          site.setMobile(par.nextIntValue(0) == 1);
          break;
        case "publisher":
          site.setPublisher(readPublisher(par));
          break;
        case "content":
          site.setContent(readContent(par));
          break;
        case "keywords":
          site.setKeywords(par.nextTextValue());
          break;
        case "ext":
          readExtensions(site, par, "BidRequest.site");
          break;
      }
    }
    return site;
  }

  protected App.Builder readApp(JsonParser par) throws IOException {
    App.Builder app = App.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          app.setId(par.nextTextValue());
          break;
        case "name":
          app.setName(par.nextTextValue());
          break;
        case "domain":
          app.setDomain(par.nextTextValue());
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
          app.setVer(par.nextTextValue());
          break;
        case "bundle":
          app.setBundle(par.nextTextValue());
          break;
        case "privacypolicy":
          app.setPrivacypolicy(par.nextIntValue(0) == 1);
          break;
        case "paid":
          app.setPaid(par.nextIntValue(0) == 1);
          break;
        case "publisher":
          app.setPublisher(readPublisher(par));
          break;
        case "content":
          app.setContent(readContent(par));
          break;
        case "keywords":
          app.setKeywords(par.nextTextValue());
          break;
        case "storeurl":
          app.setStoreurl(par.nextTextValue());
          break;
        case "ext":
          readExtensions(app, par, "BidRequest.app");
          break;
      }
    }
    return app;
  }

  protected Content.Builder readContent(JsonParser par) throws IOException {
    Content.Builder content = Content.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          content.setId(par.nextTextValue());
          break;
        case "episode":
          content.setEpisode(par.nextIntValue(0));
          break;
        case "title":
          content.setTitle(par.nextTextValue());
          break;
        case "series":
          content.setSeries(par.nextTextValue());
          break;
        case "season":
          content.setSeason(par.nextTextValue());
          break;
        case "url":
          content.setUrl(par.nextTextValue());
          break;
        case "cat":
          for (startArray(par); endArray(par); par.nextToken()) {
            content.addCat(par.getText());
          }
          break;
        case "videoquality":
          content.setVideoquality(VideoQuality.valueOf(par.nextIntValue(0)));
          break;
        case "keywords":
          content.setKeywords(par.nextTextValue());
          break;
        case "contentrating":
          content.setContentrating(par.nextTextValue());
          break;
        case "userrating":
          content.setUserrating(par.nextTextValue());
          break;
        case "context":
          content.setContext(Context.valueOf(par.nextIntValue(0)));
          break;
        case "livestream":
          content.setLivestream(par.nextIntValue(0) == 1);
          break;
        case "sourcerelationship":
          content.setSourcerelationship(SourceRelationship.valueOf(par.nextIntValue(0)));
          break;
        case "producer":
          content.setProducer(readProducer(par));
          break;
        case "len":
          content.setLen(par.nextIntValue(0));
          break;
        case "qagmediarating":
          content.setQagmediarating(QAGMediaRating.valueOf(par.nextIntValue(0)));
          break;
        case "embeddable":
          content.setEmbeddable(par.nextIntValue(0) == 1);
          break;
        case "language":
          content.setLanguage(par.nextTextValue());
          break;
        case "ext":
          readExtensions(content, par, "BidRequest.app.content");
          break;
      }
    }
    return content;
  }

  protected Producer.Builder readProducer(JsonParser par) throws IOException {
    Producer.Builder producer = Producer.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          producer.setId(par.nextTextValue());
          break;
        case "name":
          producer.setName(par.nextTextValue());
          break;
        case "cat":
          for (startArray(par); endArray(par); par.nextToken()) {
            producer.addCat(par.getText());
          }
          break;
        case "domain":
          producer.setDomain(par.nextTextValue());
          break;
        case "ext":
          readExtensions(producer, par, "BidRequest.app.content.producer");
          break;
      }
    }
    return producer;
  }

  protected Publisher.Builder readPublisher(JsonParser par) throws IOException {
    Publisher.Builder publisher = Publisher.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          publisher.setId(par.nextTextValue());
          break;
        case "name":
          publisher.setName(par.nextTextValue());
          break;
        case "cat":
          for (startArray(par); endArray(par); par.nextToken()) {
            publisher.addCat(par.getText());
          }
          break;
        case "domain":
          publisher.setDomain(par.nextTextValue());
          break;
        case "ext":
          readExtensions(publisher, par, "BidRequest.app.publisher");
          break;
      }
    }
    return publisher;
  }

  protected Device.Builder readDevice(JsonParser par) throws IOException {
    Device.Builder device = Device.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "ua":
          device.setUa(par.nextTextValue());
          break;
        case "geo":
          device.setGeo(readGeo(par, "BidRequest.device.geo"));
          break;
        case "dnt":
          device.setDnt(par.nextIntValue(0) == 1);
          break;
        case "lmt":
          device.setLmt(par.nextIntValue(0) == 1);
          break;
        case "ip":
          device.setIp(par.nextTextValue());
          break;
        case "ipv6":
          device.setIpv6(par.nextTextValue());
          break;
        case "devicetype":
          device.setDevicetype(DeviceType.valueOf(par.nextIntValue(0)));
          break;
        case "make":
          device.setMake(par.nextTextValue());
          break;
        case "model":
          device.setModel(par.nextTextValue());
          break;
        case "os":
          device.setOs(par.nextTextValue());
          break;
        case "osv":
          device.setOsv(par.nextTextValue());
          break;
        case "hwv":
          device.setHwv(par.nextTextValue());
          break;
        case "w":
          device.setW(par.nextIntValue(0));
          break;
        case "h":
          device.setH(par.nextIntValue(0));
          break;
        case "ppi":
          device.setPpi(par.nextIntValue(0));
          break;
        case "pxratio":
          device.setPxratio(nextDoubleValue(par));
          break;
        case "js":
          device.setJs(par.nextIntValue(0) == 1);
          break;
        case "flashver":
          device.setFlashver(par.nextTextValue());
          break;
        case "language":
          device.setLanguage(par.nextTextValue());
          break;
        case "carrier":
          device.setCarrier(par.nextTextValue());
          break;
        case "connectiontype":
          device.setConnectiontype(ConnectionType.valueOf(par.nextIntValue(0)));
          break;
        case "ifa":
          device.setIfa(par.nextTextValue());
          break;
        case "didsha1":
          device.setDidsha1(par.nextTextValue());
          break;
        case "didmd5":
          device.setDidmd5(par.nextTextValue());
          break;
        case "dpidsha1":
          device.setDpidsha1(par.nextTextValue());
          break;
        case "dpidmd5":
          device.setDpidmd5(par.nextTextValue());
          break;
        case "macsha1":
          device.setMacsha1(par.nextTextValue());
          break;
        case "macmd5":
          device.setMacmd5(par.nextTextValue());
          break;
        case "ext":
          readExtensions(device, par, "BidRequest.device");
          break;
      }
    }
    return device;
  }

  protected Geo.Builder readGeo(JsonParser par, String path) throws IOException {
    Geo.Builder geo = Geo.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "lat":
          geo.setLat(nextDoubleValue(par));
          break;
        case "lon":
          geo.setLon(nextDoubleValue(par));
          break;
        case "type":
          geo.setType(LocationType.valueOf(par.nextIntValue(0)));
          break;
        case "country":
          geo.setCountry(par.nextTextValue());
          break;
        case "region":
          geo.setRegion(par.nextTextValue());
          break;
        case "regionfips104":
          geo.setRegionfips104(par.nextTextValue());
          break;
        case "metro":
          geo.setMetro(par.nextTextValue());
          break;
        case "city":
          geo.setCity(par.nextTextValue());
          break;
        case "zip":
          geo.setZip(par.nextTextValue());
          break;
        case "utcoffset":
          geo.setUtcoffset(par.nextIntValue(0));
          break;
        case "ext":
          readExtensions(geo, par, path);
          break;
      }
    }
    return geo;
  }

  protected User.Builder readUser(JsonParser par) throws IOException {
    User.Builder user = User.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          user.setId(par.nextTextValue());
          break;
        case "buyeruid":
          user.setBuyeruid(par.nextTextValue());
          break;
        case "yob":
          user.setYob(par.nextIntValue(0));
          break;
        case "gender":
          user.setGender(par.nextTextValue());
          break;
        case "keywords":
          user.setKeywords(par.nextTextValue());
          break;
        case "customdata":
          user.setCustomdata(par.nextTextValue());
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
    return user;
  }

  protected Data.Builder readData(JsonParser par) throws IOException {
    Data.Builder data = Data.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          data.setId(par.nextTextValue());
          break;
        case "name":
          data.setName(par.nextTextValue());
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
    return data;
  }

  protected Segment.Builder readSegment(JsonParser par) throws IOException {
    Segment.Builder segment = Segment.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          segment.setId(par.nextTextValue());
          break;
        case "name":
          segment.setName(par.nextTextValue());
          break;
        case "value":
          segment.setValue(par.nextTextValue());
          break;
        case "ext":
          readExtensions(segment, par, "BidRequest.user.data.segment");
          break;
      }
    }
    return segment;
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
    return readBidResponse(factory.getJsonFactory().createParser(reader)).build();
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, streamed from an {@link InputStream}.
   */
  public BidResponse readBidResponse(InputStream is) throws IOException {
    try {
      return readBidResponse(factory.getJsonFactory().createParser(is)).build();
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link BidResponse} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public BidResponse.Builder readBidResponse(JsonParser par) throws IOException {
    BidResponse.Builder resp = BidResponse.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          resp.setId(par.nextTextValue());
          break;
        case "seatbid":
          for (startArray(par); endArray(par); par.nextToken()) {
            resp.addSeatbid(readSeatBid(par));
          }
          break;
        case "bidid":
          resp.setBidid(par.nextTextValue());
          break;
        case "cur":
          resp.setCur(par.nextTextValue());
          break;
        case "customdata":
          resp.setCustomdata(par.nextTextValue());
          break;
        case "ext":
          readExtensions(resp, par, "BidResponse");
          break;
      }
    }
    return resp;
  }

  protected SeatBid.Builder readSeatBid(JsonParser par) throws IOException {
    SeatBid.Builder seatbid = SeatBid.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "bid":
          for (startArray(par); endArray(par); par.nextToken()) {
            seatbid.addBid(readBid(par));
          }
          break;
        case "seat":
          seatbid.setSeat(par.nextTextValue());
          break;
        case "group":
          seatbid.setGroup(par.nextIntValue(0) == 1);
          break;
        case "ext":
          readExtensions(seatbid, par, "BidResponse.seatbid");
          break;
      }
    }
    return seatbid;
  }

  protected Bid.Builder readBid(JsonParser par) throws IOException {
    Bid.Builder bid = Bid.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      switch (getCurrentName(par)) {
        case "id":
          bid.setId(par.nextTextValue());
          break;
        case "impid":
          bid.setImpid(par.nextTextValue());
          break;
        case "price":
          bid.setPrice(nextDoubleValue(par));
          break;
        case "adid":
          bid.setAdid(par.nextTextValue());
          break;
        case "nurl":
          bid.setNurl(par.nextTextValue());
          break;
        case "adm":
          bid.setAdm(par.nextTextValue());
          break;
        case "adomain":
          for (startArray(par); endArray(par); par.nextToken()) {
            bid.addAdomain(par.getText());
          }
          break;
        case "bundle":
          bid.setBundle(par.nextTextValue());
          break;
        case "iurl":
          bid.setIurl(par.nextTextValue());
          break;
        case "cid":
          bid.setCid(par.nextTextValue());
          break;
        case "crid":
          bid.setCrid(par.nextTextValue());
          break;
        case "cat":
          bid.setCat(par.nextTextValue());
          break;
        case "attr":
          for (startArray(par); endArray(par); par.nextToken()) {
            bid.addAttr(CreativeAttribute.valueOf(par.getIntValue()));
          }
          break;
        case "dealid":
          bid.setDealid(par.nextTextValue());
          break;
        case "w":
          bid.setW(par.nextIntValue(0));
          break;
        case "h":
          bid.setH(par.nextIntValue(0));
          break;
        case "ext":
          readExtensions(bid, par, "BidResponse.seatbid.bid");
          break;
      }
    }
    return bid;
  }

  protected <EB extends ExtendableBuilder<?, EB>>
  void readExtensions(EB ext, JsonParser par, String path) throws IOException {
    startObject(par);
    @SuppressWarnings("unchecked")
    Collection<OpenRtbJsonExtReader<EB>> extReaders = factory.getReaders(path);

    while (true) {
      boolean someFieldRead = false;
      for (OpenRtbJsonExtReader<EB> extReader : extReaders) {
        someFieldRead |= extReader.read(ext, par);

        if (!endObject(par)) {
          return;
        }
      }

      if (!someFieldRead) {
        throw new IOException("Unhandled extension");
      }
      // Else loop, try all readers again
    }
  }
}
