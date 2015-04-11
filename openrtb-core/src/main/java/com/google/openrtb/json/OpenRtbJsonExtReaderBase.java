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

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.List;

/**
 * Consider an example with "imp": { ..., "ext": { p1: 1, p2: 2, p3: 3 } }, and three
 * {@code ExtReader<Impression.Builder>} where ER1 reads {p4}, ER2 reads {p2}, ER3 reads {p1,p3}.
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
 *    below to reuse the {@code MyExt.Impression.Builder} if that was already set previously.
 * 3) ER1 will be invoked several times, but never find any property it recognizes.
 *    It shouldn'set set an extension object that will be always empty.
 *
 * @param <EB> Type of message builder being constructed
 * @param <XB> Type of message builder for the extension
 */
public abstract class OpenRtbJsonExtReaderBase <
    EB extends ExtendableBuilder<?, EB>, XB extends Message.Builder
>
implements OpenRtbJsonExtReader<EB> {

  @SuppressWarnings("rawtypes")
  private final GeneratedExtension key;

  protected OpenRtbJsonExtReaderBase(GeneratedExtension<?, ?> key) {
    this.key = checkNotNull(key);
  }

  @SuppressWarnings("unchecked")
  @Override public final boolean read(EB msg, JsonParser par) throws IOException {
    boolean extRead = false;
    Object extObj = msg.getExtension(key);
    if (extObj instanceof Message) {
      extRead |= readSingle(msg, par, (XB) ((Message) extObj).toBuilder());
    } else if (extObj instanceof List<?>) {
      extRead |= readRepeated(msg, par);
    }
    return extRead;
  }

  /**
   * Desserializes an extension property, if supported by this reader.
   *
   * @param msg Buider for the container message, where {@code ext} will be set
   * @param ext Buider for the extension object, where properties will be set
   * @param par JSON parser, positioned at the property to be desserialized
   * @return {@code true} if the property was recognized, and its value consumed from the parser;
   * {@code false} if the property was ignored, leaving the parser in the same position
   */
  protected abstract boolean read(EB msg, XB ext, JsonParser par) throws IOException;
  @SuppressWarnings("unchecked")
  private boolean readSingle(EB msg, JsonParser par, XB ext) throws IOException {
    boolean extRead = false;
    while (endObject(par)) {
      if (read(msg, ext, par)) {
        extRead = true;
        par.nextToken();
      } else {
        break;
      }
    }
    if (extRead) {
      msg.setExtension(key, ext.build());
    }
    return extRead;
  }

  @SuppressWarnings("unchecked")
  protected boolean readRepeated(EB msg, JsonParser par) throws IOException {
    boolean extRead = false;
    for (startArray(par); endArray(par); par.nextToken()) {
      boolean objRead = false;
      XB ext = (XB) key.getMessageDefaultInstance().toBuilder();
      for (startObject(par); endObject(par); par.nextToken()) {
        if (read(msg, ext, par)) {
          objRead = true;
        }
      }
      if (objRead) {
        extRead = true;
        msg.addExtension(key, ext.build());
      }
    }
    if (extRead) {
      par.nextToken();
    }
    return extRead;
  }
}
