/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.openrtb.json.OpenRtbJsonUtils.endArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.endObject;
import static com.google.openrtb.json.OpenRtbJsonUtils.startArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.startObject;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.GeneratedMessageV3.ExtendableBuilder;
import com.google.protobuf.Message;
import java.io.IOException;
import java.util.List;

/**
 * Supports "complex" extensions, where the values are wrapped by a proto Message.
 *
 * @param <EB> Type of message builder being constructed
 * @param <XB> Type of message builder for the extension
 */
public abstract class OpenRtbJsonExtComplexReader<
    EB extends ExtendableBuilder<?, EB>,
    XB extends Message.Builder> extends OpenRtbJsonExtReader<EB> {
  @SuppressWarnings("rawtypes")
  private final GeneratedExtension key;
  private final boolean isJsonObject;

  /**
   * Use this constructor for readers of message type.
   *
   * @param key Extension key
   * @param isJsonObject {@code true} if the extension value is desserialized from a JSON object
   * @param rootNameFilters Filter for the root names (direct fields of "ext").
   *     If empty, this reader will be invoked for any field.
   */
  protected OpenRtbJsonExtComplexReader(
      GeneratedExtension<?, ?> key, boolean isJsonObject, String... rootNameFilters) {
    super(rootNameFilters);
    this.key = checkNotNull(key);
    this.isJsonObject = isJsonObject;
  }

  @SuppressWarnings("unchecked")
  @Override protected final void read(EB msg, JsonParser par) throws IOException {
    Object extObj = msg.getExtension(key);
    if (extObj instanceof Message) {
      readSingle(msg, par, (XB) ((Message) extObj).toBuilder());
    } else if (extObj instanceof List<?>) {
      readRepeated(msg, par);
    } else {
      throw new IllegalStateException("Extension must be Message or repeated Message");
    }
  }

  /**
   * Reads extensions of message type.
   *
   * @param ext Builder for the extension object, where properties will be set
   * @param par JSON parser, positioned at the property to be desserialized
   * {@code false} if the property was ignored, leaving the parser in the same position
   * @throws IOException any parsing error
   */
  protected abstract void read(XB ext, JsonParser par) throws IOException;

  @SuppressWarnings("unchecked")
  private void readSingle(EB msg, JsonParser par, XB ext) throws IOException {
    if (isJsonObject) {
      startObject(par);
    }
    boolean extRead = false;
    JsonToken tokLast = par.getCurrentToken();
    JsonLocation locLast = par.getCurrentLocation();
    while (endObject(par) && (isJsonObject || filter(par))) {
      read(ext, par);
      if (par.getCurrentToken() != tokLast || !par.getCurrentLocation().equals(locLast)) {
        extRead = true;
        par.nextToken();
        tokLast = par.getCurrentToken();
        locLast = par.getCurrentLocation();
      } else {
        break;
      }
    }
    if (extRead) {
      msg.setExtension(key, ext.build());
    }
    if (isJsonObject) {
      par.nextToken();
    }
  }

  @SuppressWarnings("unchecked")
  private void readRepeated(EB msg, JsonParser par) throws IOException {
    par.nextToken();
    JsonToken tokLast = par.getCurrentToken();
    JsonLocation locLast = par.getCurrentLocation();
    for (startArray(par); endArray(par); par.nextToken()) {
      boolean objRead = false;
      XB ext = (XB) key.getMessageDefaultInstance().toBuilder();
      for (startObject(par); endObject(par); par.nextToken()) {
        read(ext, par);
        JsonToken tokNew = par.getCurrentToken();
        JsonLocation locNew = par.getCurrentLocation();
        if (tokNew != tokLast || !locNew.equals(locLast)) {
          objRead = true;
        }
        tokLast = tokNew;
        locLast = locNew;
      }
      if (objRead) {
        msg.addExtension(key, ext.build());
      }
    }
  }

  @Override public String toString() {
    return super.toString()
        + (isJsonObject ? " JSON=object" : " JSON=scalar")
        + " message=" + key.getDescriptor().getFullName();
  }
}