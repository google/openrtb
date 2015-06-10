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

import static com.google.openrtb.json.OpenRtbJsonUtils.writeIntBoolField;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeInts;
import static com.google.openrtb.json.OpenRtbJsonUtils.writeStrings;

import com.google.openrtb.OpenRtbNative.NativeRequest;
import com.google.openrtb.OpenRtbNative.NativeResponse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Serializes OpenRTB {@link NativeRequest}/{@link NativeResponse} messages to JSON.
 * <p>
 * Note: Among methods that write to a {@link JsonGenerator} parameter, only the {@code public}
 * methods will call {@code flush()} on the generator before returning.
 * <p>
 * This class is threadsafe.
 */
public class OpenRtbNativeJsonWriter extends AbstractOpenRtbJsonWriter {

  protected OpenRtbNativeJsonWriter(OpenRtbJsonFactory factory) {
    super(factory);
  }

  /**
   * Serializes a {@link NativeRequest} to JSON, returned as a {@code String}.
   */
  public String writeNativeRequest(NativeRequest req) throws IOException {
    try (StringWriter writer = new StringWriter()) {
      writeNativeRequest(req, writer);
      return writer.toString();
    }
  }

  /**
   * Serializes a {@link NativeRequest} to JSON, streamed into an {@link Writer}.
   *
   * @see JsonFactory#createGenerator(Writer)
   */
  public void writeNativeRequest(NativeRequest req, Writer writer) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(writer);
    writeNativeRequest(req, gen);
  }

  /**
   * Serializes a {@link NativeRequest} to JSON, streamed into an {@link OutputStream}.
   *
   * @see JsonFactory#createGenerator(OutputStream)
   */
  public void writeNativeRequest(NativeRequest req, OutputStream os) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(os);
    writeNativeRequest(req, gen);
  }

  /**
   * Serializes a {@link NativeRequest} to JSON, with a provided {@link JsonGenerator}
   * which allows several choices of output and encoding.
   */
  public final void writeNativeRequest(NativeRequest req, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeNativeRequestFields(req, gen);
    writeExtensions(req, gen);
    gen.writeEndObject();
    gen.flush();
  }

  protected void writeNativeRequestFields(NativeRequest req, JsonGenerator gen) throws IOException {
    if (req.hasVer()) {
      gen.writeStringField("ver", req.getVer());
    }
    if (req.hasLayout()) {
      gen.writeNumberField("layout", req.getLayout().getNumber());
    }
    if (req.hasAdunit()) {
      gen.writeNumberField("adunit", req.getAdunit().getNumber());
    }
    if (req.hasPlcmtcnt()) {
      gen.writeNumberField("plcmtcnt", req.getPlcmtcnt());
    }
    if (req.hasSeq()) {
      gen.writeNumberField("seq", req.getSeq());
    }
    if (checkRequired(req.getAssetsCount())) {
      gen.writeArrayFieldStart("assets");
      for (NativeRequest.Asset asset : req.getAssetsList()) {
        writeReqAsset(asset, gen);
      }
      gen.writeEndArray();
    }
  }

  public final void writeReqAsset(NativeRequest.Asset asset, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeReqAssetFields(asset, gen);
    writeExtensions(asset, gen);
    gen.writeEndObject();
  }

  protected void writeReqAssetFields(NativeRequest.Asset asset, JsonGenerator gen)
      throws IOException {
    gen.writeNumberField("id", asset.getId());
    if (asset.hasRequired()) {
      writeIntBoolField("required", asset.getRequired(), gen);
    }
    if (asset.hasTitle()) {
      gen.writeFieldName("title");
      writeReqTitle(asset.getTitle(), gen);
    }
    if (asset.hasImg()) {
      gen.writeFieldName("img");
      writeReqImage(asset.getImg(), gen);
    }
    if (asset.hasVideo()) {
      gen.writeFieldName("video");
      writeReqVideo(asset.getVideo(), gen);
    }
    if (asset.hasData()) {
      gen.writeFieldName("data");
      writeReqData(asset.getData(), gen);
    }
  }

  public final void writeReqTitle(NativeRequest.Asset.Title title, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeReqTitleFields(title, gen);
    writeExtensions(title, gen);
    gen.writeEndObject();
  }

  protected void writeReqTitleFields(NativeRequest.Asset.Title title, JsonGenerator gen)
      throws IOException {
    gen.writeNumberField("len", title.getLen());
  }

  public final void writeReqImage(NativeRequest.Asset.Image image, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeReqImageFields(image, gen);
    writeExtensions(image, gen);
    gen.writeEndObject();
  }

  protected void writeReqImageFields(NativeRequest.Asset.Image image, JsonGenerator gen)
      throws IOException {
    if (image.hasType()) {
      gen.writeNumberField("type", image.getType().getNumber());
    }
    if (image.hasW()) {
      gen.writeNumberField("w", image.getW());
    }
    if (image.hasH()) {
      gen.writeNumberField("h", image.getH());
    }
    if (image.hasWmin()) {
      gen.writeNumberField("wmin", image.getWmin());
    }
    if (image.hasHmin()) {
      gen.writeNumberField("hmin", image.getHmin());
    }
    if (checkRequired(image.getMimesCount())) {
      writeStrings("mimes", image.getMimesList(), gen);
    }
  }

  public final void writeReqVideo(NativeRequest.Asset.Video video, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeReqVideoFields(video, gen);
    writeExtensions(video, gen);
    gen.writeEndObject();
  }

  protected void writeReqVideoFields(NativeRequest.Asset.Video video, JsonGenerator gen)
      throws IOException {
    if (checkRequired(video.getMimesCount())) {
      writeStrings("mimes", video.getMimesList(), gen);
    }
    gen.writeNumberField("minduration", video.getMinduration());
    gen.writeNumberField("maxduration", video.getMaxduration());
    if (checkRequired(video.getProtocolsCount())) {
      writeInts("protocols", video.getProtocolsList(), gen);
    }
  }

  public final void writeReqData(NativeRequest.Asset.Data data, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeReqDataFields(data, gen);
    writeExtensions(data, gen);
    gen.writeEndObject();
  }

  protected void writeReqDataFields(NativeRequest.Asset.Data data, JsonGenerator gen)
      throws IOException {
    gen.writeNumberField("type", data.getType().getNumber());
    if (data.hasLen()) {
      gen.writeNumberField("len", data.getLen());
    }
  }

  /**
   * Serializes a {@link NativeResponse} to JSON, returned as a {@link String}.
   */
  public String writeNativeResponse(NativeResponse resp) throws IOException {
    try (StringWriter writer = new StringWriter()) {
      writeNativeResponse(resp, writer);
      return writer.toString();
    }
  }

  /**
   * Serializes a {@link NativeResponse} to JSON, streamed to a {@link OutputStream}.
   *
   * @see JsonFactory#createGenerator(OutputStream)
   */
  public void writeNativeResponse(NativeResponse resp, OutputStream os) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(os);
    writeNativeResponse(resp, gen);
  }

  /**
   * Serializes a {@link NativeResponse} to JSON, streamed to a {@link Writer}.
   *
   * @see JsonFactory#createGenerator(Writer)
   */
  public void writeNativeResponse(NativeResponse resp, Writer writer) throws IOException {
    JsonGenerator gen = factory().getJsonFactory().createGenerator(writer);
    writeNativeResponse(resp, gen);
  }

  /**
   * Serializes a {@link NativeResponse} to JSON, with a provided {@link JsonGenerator}
   * which allows several choices of output and encoding.
   */
  public final void writeNativeResponse(NativeResponse resp, JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    writeNativeResponseFields(resp, gen);
    writeExtensions(resp, gen);
    gen.writeEndObject();
    gen.flush();
  }

  protected void writeNativeResponseFields(NativeResponse resp, JsonGenerator gen)
      throws IOException {
    if (resp.hasVer()) {
      gen.writeStringField("ver", resp.getVer());
    }
    if (resp.getAssetsCount() != 0) {
      gen.writeArrayFieldStart("assets");
      for (NativeResponse.Asset asset : resp.getAssetsList()) {
        writeRespAsset(asset, gen);
      }
      gen.writeEndArray();
    }
    gen.writeFieldName("link");
    writeRespLink(resp.getLink(), gen);
    writeStrings("imptrackers", resp.getImptrackersList(), gen);
    if (resp.hasJstracker()) {
      gen.writeStringField("jstracker", resp.getJstracker());
    }
  }

  public final void writeRespAsset(NativeResponse.Asset asset, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeRespAssetFields(asset, gen);
    writeExtensions(asset, gen);
    gen.writeEndObject();
  }

  protected void writeRespAssetFields(NativeResponse.Asset asset, JsonGenerator gen)
      throws IOException {
    gen.writeNumberField("id", asset.getId());
    if (asset.hasRequired()) {
      writeIntBoolField("req", asset.getRequired(), gen);
    }
    if (asset.hasTitle()) {
      gen.writeFieldName("title");
      writeRespTitle(asset.getTitle(), gen);
    }
    if (asset.hasImg()) {
      gen.writeFieldName("img");
      writeRespImage(asset.getImg(), gen);
    }
    if (asset.hasVideo()) {
      gen.writeFieldName("video");
      writeRespVideo(asset.getVideo(), gen);
    }
    if (asset.hasData()) {
      gen.writeFieldName("data");
      writeRespData(asset.getData(), gen);
    }
    if (asset.hasLink()) {
      gen.writeFieldName("link");
      writeRespLink(asset.getLink(), gen);
    }
  }

  public final void writeRespTitle(NativeResponse.Asset.Title title, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeRespTitleFields(title, gen);
    writeExtensions(title, gen);
    gen.writeEndObject();
  }

  protected void writeRespTitleFields(NativeResponse.Asset.Title title, JsonGenerator gen)
      throws IOException {
    gen.writeStringField("text", title.getText());
  }

  public final void writeRespImage(NativeResponse.Asset.Image image, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeRespImageFields(image, gen);
    writeExtensions(image, gen);
    gen.writeEndObject();
  }

  protected void writeRespImageFields(NativeResponse.Asset.Image image, JsonGenerator gen)
      throws IOException {
    if (image.hasUrl()) {
      gen.writeStringField("url", image.getUrl());
    }
    if (image.hasW()) {
      gen.writeNumberField("w", image.getW());
    }
    if (image.hasH()) {
      gen.writeNumberField("h", image.getH());
    }
  }

  public final void writeRespVideo(NativeResponse.Asset.Video video, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeRespVideoFields(video, gen);
    writeExtensions(video, gen);
    gen.writeEndObject();
  }

  protected void writeRespVideoFields(NativeResponse.Asset.Video video, JsonGenerator gen)
      throws IOException {
    writeStrings("vasttag", video.getVasttagList(), gen);
  }

  public final void writeRespData(NativeResponse.Asset.Data data, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeRespDataFields(data, gen);
    writeExtensions(data, gen);
    gen.writeEndObject();
  }

  protected void writeRespDataFields(NativeResponse.Asset.Data data, JsonGenerator gen)
      throws IOException {
    if (data.hasLabel()) {
      gen.writeStringField("label", data.getLabel());
    }
    gen.writeStringField("value", data.getValue());
  }

  public final void writeRespLink(NativeResponse.Link link, JsonGenerator gen)
      throws IOException {
    gen.writeStartObject();
    writeRespLinkFields(link, gen);
    writeExtensions(link, gen);
    gen.writeEndObject();
  }

  protected void writeRespLinkFields(NativeResponse.Link link, JsonGenerator gen)
      throws IOException {
    if (link.hasUrl()) {
      gen.writeStringField("url", link.getUrl());
    }
    writeStrings("clicktrackers", link.getClicktrackersList(), gen);
    if (link.hasFallback()) {
      gen.writeStringField("fallback", link.getFallback());
    }
  }
}
