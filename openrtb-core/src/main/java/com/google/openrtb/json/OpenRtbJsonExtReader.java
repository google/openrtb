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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.openrtb.json.OpenRtbJsonUtils.endArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.endObject;
import static com.google.openrtb.json.OpenRtbJsonUtils.startArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.startObject;

import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.Message;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.List;

/**
 * A desserialization extension, can consume children of "ext" fields.
 * <p>
 * Implementations of this interface have to be threadsafe.
 *
 * @param <EB> Type of message builder being constructed
 * @param <XB> Type of message builder for the extension
 */
public abstract class OpenRtbJsonExtReader<
    EB extends ExtendableBuilder<?, EB>, XB extends Message.Builder> {

  @SuppressWarnings("rawtypes")
  private final GeneratedExtension key;

  /**
   * Use this constructor for readers of scalar type.
   */
  protected OpenRtbJsonExtReader() {
    this.key = null;
  }

  /**
   * Use this constructor for readers of message type.
   */
  protected OpenRtbJsonExtReader(GeneratedExtension<?, ?> key) {
    this.key = checkNotNull(key);
  }

  /**
   * Override this method for extensions of scalar type.
   *
   * @param msg Builder for the container message, where an extension message will be set
   * @param par JSON parser, positioned at the property to be desserialized
   * @throws IOException any parsing error
   */
  @SuppressWarnings("unchecked")
  protected void read(EB msg, JsonParser par) throws IOException {
    Object extObj = msg.getExtension(key);
    if (extObj instanceof Message) {
      readSingle(msg, par, (XB) ((Message) extObj).toBuilder());
    } else if (extObj instanceof List<?>) {
      readRepeated(msg, par);
    } // Else it's a scalar: reader class should override this method
  }

  /**
   * Override this method for extensions of message type.
   * <p>
   * Makes easy to implement an {@link OpenRtbJsonExtReader} that supports very flexible
   * mapping from JSON fields into the model extension messages.
   * <p>
   * Consider an example with "imp": { ..., "ext": { p1: 1, p2: 2, p3: 3 } }, and three
   * {@code ExtReader<Imp.Builder>} where ER1 reads {p4}, ER2 reads {p2}, ER3 reads {p1,p3}.
   * The main {@link OpenRtbJsonReader} will start at p1, invoking all compatible
   * {@link OpenRtbJsonExtReader}s until some of them consumes that property.
   * The {@code ExtReader}'s top-level {@code read()} may use a loop so it can read multiple
   * properties, if possible, in a single call.  We also need to consider some complications:
   * <p>
   * 1) ER3 will read p1, but then comes p2 which ER3 doesn't recognize. We need to store
   *    what we have been able to read, then return false, so the main reader knows that
   *    it needs to reset the loop and try all ExtReaders again (ER2 will read p2).
   * 2) ER2 won't recognize p3, so the same thing happens: return false, main reader
   *    tries all ExtReader's, ER3 will handle p3.  This will be the second invocation
   *    to ER3 for the same "ext" object, that's why we need the ternary conditional
   *    below to reuse the {@code MyExt.Imp.Builder} if that was already set previously.
   * 3) ER1 will be invoked several times, but never find any property it recognizes.
   *    It shouldn'set set an extension object that will be always empty.
   *
   * @param msg Builder for the container message, where {@code ext} will be set
   * @param ext Builder for the extension object, where properties will be set
   * @param par JSON parser, positioned at the property to be desserialized
   * {@code false} if the property was ignored, leaving the parser in the same position
   */
  protected void read(EB msg, XB ext, JsonParser par) throws IOException {
    throw new AbstractMethodError();
  }

  @SuppressWarnings("unchecked")
  private void readSingle(EB msg, JsonParser par, XB ext) throws IOException {
    boolean extRead = false;
    JsonToken tokLast = par.getCurrentToken();
    JsonLocation locLast = par.getCurrentLocation();
    while (endObject(par)) {
      read(msg, ext, par);
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
  }

  @SuppressWarnings("unchecked")
  protected void readRepeated(EB msg, JsonParser par) throws IOException {
    par.nextToken();
    JsonToken tokLast = par.getCurrentToken();
    JsonLocation locLast = par.getCurrentLocation();
    for (startArray(par); endArray(par); par.nextToken()) {
      boolean objRead = false;
      XB ext = (XB) key.getMessageDefaultInstance().toBuilder();
      for (startObject(par); endObject(par); par.nextToken()) {
        read(msg, ext, par);
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

  @Override
  public String toString() {
    return getClass().getName()
        + (key == null ? " (scalar " : " (" + key.getDescriptor().getFullName() + ")");
  }
}
