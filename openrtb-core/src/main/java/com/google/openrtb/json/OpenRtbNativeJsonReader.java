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
import static com.google.openrtb.json.OpenRtbJsonUtils.startArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.startObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.io.CharSource;
import com.google.common.io.Closeables;
import com.google.openrtb.OpenRtb.AdUnitId;
import com.google.openrtb.OpenRtb.ContextSubtype;
import com.google.openrtb.OpenRtb.ContextType;
import com.google.openrtb.OpenRtb.DataAssetType;
import com.google.openrtb.OpenRtb.ImageAssetType;
import com.google.openrtb.OpenRtb.LayoutId;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeResponse;
import com.google.openrtb.OpenRtb.PlacementType;
import com.google.openrtb.util.ProtoUtils;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Desserializes OpenRTB {@link NativeRequest}/{@link NativeResponse} messages from JSON.
 *
 * <p>This class is threadsafe.
 */
public class OpenRtbNativeJsonReader extends AbstractOpenRtbJsonReader {
  private OpenRtbJsonReader coreReader;

  protected OpenRtbNativeJsonReader(OpenRtbJsonFactory factory) {
    super(factory);
  }

  /**
   * Desserializes a {@link NativeRequest} from a JSON string, provided as a {@link ByteString}.
   */
  public NativeRequest readNativeRequest(ByteString bs) throws IOException {
    return readNativeRequest(bs.newInput());
  }

  /**
   * Desserializes a {@link NativeRequest} from a JSON string, provided as a {@link CharSequence}.
   */
  public NativeRequest readNativeRequest(CharSequence chars) throws IOException {
    return readNativeRequest(CharSource.wrap(chars).openStream());
  }

  /**
   * Desserializes a {@link NativeRequest} from JSON, streamed from a {@link Reader}.
   */
  public NativeRequest readNativeRequest(Reader reader) throws IOException {
    return ProtoUtils.built(readNativeRequest(factory().getJsonFactory().createParser(reader)));
  }

  /**
   * Desserializes a {@link NativeRequest} from JSON, streamed from an {@link InputStream}.
   */
  public NativeRequest readNativeRequest(InputStream is) throws IOException {
    try {
      return ProtoUtils.built(readNativeRequest(factory().getJsonFactory().createParser(is)));
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link NativeRequest} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public final NativeRequest.Builder readNativeRequest(JsonParser par) throws IOException {
    if (emptyToNull(par)) {
      return null;
    }
    NativeRequest.Builder req = NativeRequest.newBuilder();
    boolean rootNativeField = false;
    boolean firstField = true;
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        if (firstField) {
          firstField = false;
          if ((rootNativeField = "native".equals(fieldName)) == true) {
            startObject(par);
            fieldName = getCurrentName(par);
            par.nextToken();
          }
        }
        if (par.getCurrentToken() != JsonToken.VALUE_NULL) {
          readNativeRequestField(par, req, fieldName);
        }
      }
    }
    if (rootNativeField && !endObject(par)) {
      par.nextToken();
    }
    return req;
  }

  protected void readNativeRequestField(JsonParser par, NativeRequest.Builder req, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "ver":
        req.setVer(par.getText());
        break;
      case "layout": {
          LayoutId value = LayoutId.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            req.setLayout(value);
          }
        }
        break;
      case "adunit": {
          AdUnitId value = AdUnitId.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            req.setAdunit(value);
          }
        }
        break;
      case "plcmtcnt":
        req.setPlcmtcnt(par.getIntValue());
        break;
      case "seq":
        req.setSeq(par.getIntValue());
        break;
      case "assets":
        for (startArray(par); endArray(par); par.nextToken()) {
          req.addAssets(readReqAsset(par));
        }
        break;
      case "context": {
          ContextType value = ContextType.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            req.setContext(value);
          }
        }
        break;
      case "contextsubtype": {
          ContextSubtype value = ContextSubtype.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            req.setContextsubtype(value);
          }
        }
        break;
      case "plcmttype": {
          PlacementType value = PlacementType.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            req.setPlcmttype(value);
          }
        }
        break;
      default:
        readOther(req, par, fieldName);
    }
  }

  public final NativeRequest.Asset.Builder readReqAsset(JsonParser par) throws IOException {
    NativeRequest.Asset.Builder asset = NativeRequest.Asset.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readReqAssetField(par, asset, fieldName);
      }
    }
    return asset;
  }

  protected void readReqAssetField(
      JsonParser par, NativeRequest.Asset.Builder asset, String fieldName) throws IOException {
    switch (fieldName) {
      case "id":
        asset.setId(par.getIntValue());
        break;
      case "required":
        asset.setRequired(par.getValueAsBoolean());
        break;
      case "title":
        asset.setTitle(readReqTitle(par));
        break;
      case "img":
        asset.setImg(readReqImage(par));
        break;
      case "video":
        asset.setVideo(coreReader().readVideo(par));
        break;
      case "data":
        asset.setData(readReqData(par));
        break;
      default:
        readOther(asset, par, fieldName);
    }
  }

  public final NativeRequest.Asset.Title.Builder readReqTitle(JsonParser par)
      throws IOException {
    NativeRequest.Asset.Title.Builder title = NativeRequest.Asset.Title.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readReqTitleField(par, title, fieldName);
      }
    }
    return title;
  }

  protected void readReqTitleField(
      JsonParser par, NativeRequest.Asset.Title.Builder title, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "len":
        title.setLen(par.getIntValue());
        break;
      default:
        readOther(title, par, fieldName);
    }
  }

  public final NativeRequest.Asset.Image.Builder readReqImage(JsonParser par)
      throws IOException {
    NativeRequest.Asset.Image.Builder req = NativeRequest.Asset.Image.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readReqImageField(par, req, fieldName);
      }
    }
    return req;
  }

  protected void readReqImageField(
      JsonParser par, NativeRequest.Asset.Image.Builder image, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "type": {
          ImageAssetType value = ImageAssetType.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            image.setType(value);
          }
        }
        break;
      case "w":
        image.setW(par.getIntValue());
        break;
      case "h":
        image.setH(par.getIntValue());
        break;
      case "wmin":
        image.setWmin(par.getIntValue());
        break;
      case "hmin":
        image.setHmin(par.getIntValue());
        break;
      case "mimes":
        for (startArray(par); endArray(par); par.nextToken()) {
          image.addMimes(par.getText());
        }
        break;
      default:
        readOther(image, par, fieldName);
    }
  }

  public final NativeRequest.Asset.Data.Builder readReqData(JsonParser par) throws IOException {
    NativeRequest.Asset.Data.Builder data = NativeRequest.Asset.Data.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readReqDataField(par, data, fieldName);
      }
    }
    return data;
  }

  protected void readReqDataField(
      JsonParser par, NativeRequest.Asset.Data.Builder data, String fieldName) throws IOException {
    switch (fieldName) {
      case "type": {
          DataAssetType value = DataAssetType.forNumber(par.getIntValue());
          if (checkEnum(value)) {
            data.setType(value);
          }
        }
        break;
      case "len":
        data.setLen(par.getIntValue());
        break;
      default:
        readOther(data, par, fieldName);
    }
  }

  /**
   * Desserializes a {@link NativeResponse} from a JSON string, provided as a {@link ByteString}.
   */
  public NativeResponse readNativeResponse(ByteString bs) throws IOException {
    return readNativeResponse(bs.newInput());
  }

  /**
   * Desserializes a {@link NativeResponse} from a JSON string, provided as a {@link CharSequence}.
   */
  public NativeResponse readNativeResponse(CharSequence chars) throws IOException {
    return readNativeResponse(CharSource.wrap(chars).openStream());
  }

  /**
   * Desserializes a {@link NativeResponse} from JSON, streamed from a {@link Reader}.
   */
  public NativeResponse readNativeResponse(Reader reader) throws IOException {
    return ProtoUtils.built(readNativeResponse(factory().getJsonFactory().createParser(reader)));
  }

  /**
   * Desserializes a {@link NativeResponse} from JSON, streamed from an {@link InputStream}.
   */
  public NativeResponse readNativeResponse(InputStream is) throws IOException {
    try {
      return ProtoUtils.built(readNativeResponse(factory().getJsonFactory().createParser(is)));
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  /**
   * Desserializes a {@link NativeResponse} from JSON, with a provided {@link JsonParser}
   * which allows several choices of input and encoding.
   */
  public final NativeResponse.Builder readNativeResponse(JsonParser par) throws IOException {
    if (emptyToNull(par)) {
      return null;
    }
    NativeResponse.Builder resp = NativeResponse.newBuilder();
    boolean rootNativeField = false;
    boolean firstField = true;
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        if (firstField) {
          firstField = false;
          if ((rootNativeField = "native".equals(fieldName)) == true) {
            startObject(par);
            fieldName = getCurrentName(par);
            par.nextToken();
          }
        }
        if (par.getCurrentToken() != JsonToken.VALUE_NULL) {
          readNativeResponseField(par, resp, fieldName);
        }
      }
    }
    if (rootNativeField && !endObject(par)) {
      par.nextToken();
    }
    return resp;
  }

  protected void readNativeResponseField(
      JsonParser par, NativeResponse.Builder resp, String fieldName) throws IOException {
    switch (fieldName) {
      case "ver":
        resp.setVer(par.getText());
        break;
      case "assets":
        for (startArray(par); endArray(par); par.nextToken()) {
          resp.addAssets(readRespAsset(par));
        }
        break;
      case "link":
        resp.setLink(readRespLink(par));
        break;
      case "imptrackers":
        for (startArray(par); endArray(par); par.nextToken()) {
          resp.addImptrackers(par.getText());
        }
        break;
      case "jstracker":
        resp.setJstracker(par.getText());
        break;
      default:
        readOther(resp, par, fieldName);
    }
  }

  public final NativeResponse.Asset.Builder readRespAsset(JsonParser par) throws IOException {
    NativeResponse.Asset.Builder asset = NativeResponse.Asset.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRespAssetField(par, asset, fieldName);
      }
    }
    return asset;
  }

  protected void readRespAssetField(
      JsonParser par, NativeResponse.Asset.Builder asset, String fieldName) throws IOException {
    switch (fieldName) {
      case "id":
        asset.setId(par.getIntValue());
        break;
      case "required":
        asset.setRequired(par.getValueAsBoolean());
        break;
      case "title":
        asset.setTitle(readRespTitle(par));
        break;
      case "img":
        asset.setImg(readRespImage(par));
        break;
      case "video":
        asset.setVideo(readRespVideo(par));
        break;
      case "data":
        asset.setData(readRespData(par));
        break;
      case "link":
        asset.setLink(readRespLink(par));
        break;
      default:
        readOther(asset, par, fieldName);
    }
  }

  public final NativeResponse.Asset.Title.Builder readRespTitle(JsonParser par)
      throws IOException {
    NativeResponse.Asset.Title.Builder title = NativeResponse.Asset.Title.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRespTitleField(par, title, fieldName);
      }
    }
    return title;
  }

  protected void readRespTitleField(
      JsonParser par, NativeResponse.Asset.Title.Builder title, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "text":
        title.setText(par.getText());
        break;
      default:
        readOther(title, par, fieldName);
    }
  }

  public final NativeResponse.Asset.Image.Builder readRespImage(JsonParser par)
      throws IOException {
    NativeResponse.Asset.Image.Builder image = NativeResponse.Asset.Image.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRespImageField(par, image, fieldName);
      }
    }
    return image;
  }

  protected void readRespImageField(
      JsonParser par, NativeResponse.Asset.Image.Builder image, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "url":
        image.setUrl(par.getText());
        break;
      case "w":
        image.setW(par.getIntValue());
        break;
      case "h":
        image.setH(par.getIntValue());
        break;
      default:
        readOther(image, par, fieldName);
    }
  }

  public final NativeResponse.Asset.Video.Builder readRespVideo(JsonParser par)
      throws IOException {
    NativeResponse.Asset.Video.Builder video = NativeResponse.Asset.Video.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRespVideoField(par, video, fieldName);
      }
    }
    return video;
  }

  protected void readRespVideoField(
      JsonParser par, NativeResponse.Asset.Video.Builder video, String fieldName)
      throws IOException {
    switch (fieldName) {
      case "vasttag":
        video.setVasttag(par.getText());
        break;
      default:
        readOther(video, par, fieldName);
    }
  }

  public final NativeResponse.Asset.Data.Builder readRespData(JsonParser par) throws IOException {
    NativeResponse.Asset.Data.Builder data = NativeResponse.Asset.Data.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRespDataField(par, data, fieldName);
      }
    }
    return data;
  }

  protected void readRespDataField(
      JsonParser par, NativeResponse.Asset.Data.Builder data, String fieldName) throws IOException {
    switch (fieldName) {
      case "label":
        data.setLabel(par.getText());
        break;
      case "value":
        data.setValue(par.getText());
        break;
      default:
        readOther(data, par, fieldName);
    }
  }

  public final NativeResponse.Link.Builder readRespLink(JsonParser par) throws IOException {
    NativeResponse.Link.Builder link = NativeResponse.Link.newBuilder();
    for (startObject(par); endObject(par); par.nextToken()) {
      String fieldName = getCurrentName(par);
      if (par.nextToken() != JsonToken.VALUE_NULL) {
        readRespLinkField(par, link, fieldName);
      }
    }
    return link;
  }

  protected void readRespLinkField(
      JsonParser par, NativeResponse.Link.Builder link, String fieldName) throws IOException {
    switch (fieldName) {
      case "url":
        link.setUrl(par.getText());
        break;
      case "clicktrackers":
        for (startArray(par); endArray(par); par.nextToken()) {
          link.addClicktrackers(par.getText());
        }
        break;
      case "fallback":
        link.setFallback(par.getText());
        break;
      default:
        readOther(link, par, fieldName);
    }
  }

  protected final OpenRtbJsonReader coreReader() {
    if (coreReader == null) {
      coreReader = factory().newReader();
    }
    return coreReader;
  }
}
