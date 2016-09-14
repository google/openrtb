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
import static com.google.openrtb.json.OpenRtbJsonUtils.writeStrings;

import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeResponse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Serializes OpenRTB {@link NativeRequest}/{@link NativeResponse} messages to JSON.
 *
 * <p>Note: Among methods that write to a {@link JsonGenerator} parameter, only the {@code public}
 * methods will call {@code flush()} on the generator before returning.
 *
 * <p>This class is threadsafe.
 */
public class OpenRtbNativeJsonWriter extends AbstractOpenRtbJsonWriter {
  private OpenRtbJsonWriter coreWriter;

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
    if (factory().isRootNativeField()) {
      gen.writeObjectFieldStart("native");
    }
    writeNativeRequestFields(req, gen);
    writeExtensions(req, gen);
    if (factory().isRootNativeField()) {
      gen.writeEndObject();
    }
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
    if (req.hasContext()) {
      gen.writeNumberField("context", req.getContext().getNumber());
    }
    if (req.hasContextsubtype()) {
      gen.writeNumberField("contextsubtype", req.getContextsubtype().getNumber());
    }
    if (req.hasPlcmttype()) {
      gen.writeNumberField("plcmttype", req.getPlcmttype().getNumber());
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
    switch (asset.getAssetOneofCase()) {
      case TITLE:
        gen.writeFieldName("title");
        writeReqTitle(asset.getTitle(), gen);
        break;
      case IMG:
        gen.writeFieldName("img");
        writeReqImage(asset.getImg(), gen);
        break;
      case VIDEO:
        gen.writeFieldName("video");
        coreWriter().writeVideo(asset.getVideo(), gen);
        break;
      case DATA:
        gen.writeFieldName("data");
        writeReqData(asset.getData(), gen);
        break;
      case ASSETONEOF_NOT_SET:
        checkRequired(false);
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
    if (factory().isRootNativeField()) {
      gen.writeObjectFieldStart("native");
    }
    writeNativeResponseFields(resp, gen);
    writeExtensions(resp, gen);
    if (factory().isRootNativeField()) {
      gen.writeEndObject();
    }
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
      writeIntBoolField("required", asset.getRequired(), gen);
    }
    if (asset.hasLink()) {
      gen.writeFieldName("link");
      writeRespLink(asset.getLink(), gen);
    }
    switch (asset.getAssetOneofCase()) {
      case TITLE:
        gen.writeFieldName("title");
        writeRespTitle(asset.getTitle(), gen);
        break;
      case IMG:
        gen.writeFieldName("img");
        writeRespImage(asset.getImg(), gen);
        break;
      case VIDEO:
        gen.writeFieldName("video");
        writeRespVideo(asset.getVideo(), gen);
        break;
      case DATA:
        gen.writeFieldName("data");
        writeRespData(asset.getData(), gen);
        break;
      case ASSETONEOF_NOT_SET:
        checkRequired(false);
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
    gen.writeStringField("url", image.getUrl());
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
    gen.writeStringField("vasttag", video.getVasttag());
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

  protected final OpenRtbJsonWriter coreWriter() {
    if (coreWriter == null) {
      coreWriter = factory().newWriter();
    }
    return coreWriter;
  }
}
