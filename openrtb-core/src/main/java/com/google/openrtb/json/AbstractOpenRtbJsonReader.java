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

import static com.google.openrtb.json.OpenRtbJsonUtils.endObject;
import static com.google.openrtb.json.OpenRtbJsonUtils.startObject;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.openrtb.util.OpenRtbUtils;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desserializes OpenRTB messages from JSON.
 */
public abstract class AbstractOpenRtbJsonReader {
  static final Logger logger = LoggerFactory.getLogger(AbstractOpenRtbJsonReader.class);
  private final OpenRtbJsonFactory factory;

  protected AbstractOpenRtbJsonReader(OpenRtbJsonFactory factory) {
    this.factory = factory;
  }

  public final OpenRtbJsonFactory factory() {
    return factory;
  }

  protected final <EB extends ExtendableBuilder<?, EB>>
  void readOther(EB msg, JsonParser par, String fieldName) throws IOException {
    if ("ext".equals(fieldName)) {
      readExtensions(msg, par);
    } else {
      par.skipChildren();
    }
  }

  /**
   * Read any extensions that may exist in a message.
   *
   * @param msg Builder of a message that may contain extensions
   * @param par The JSON parser, positioned at the "ext" field
   * @param <EB> Type of message builder being constructed
   * @throws IOException any parsing error
   */
  protected final <EB extends ExtendableBuilder<?, EB>>
  void readExtensions(EB msg, JsonParser par) throws IOException {
    @SuppressWarnings("unchecked")
    Set<OpenRtbJsonExtReader<EB>> extReaders = factory.getReaders((Class<EB>) msg.getClass());
    if (extReaders.isEmpty()) {
      par.skipChildren();
      return;
    }

    startObject(par);
    JsonToken tokLast = par.getCurrentToken();
    JsonLocation locLast = par.getCurrentLocation();

    while (true) {
      boolean extRead = false;
      for (OpenRtbJsonExtReader<EB> extReader : extReaders) {
        if (extReader.filter(par)) {
          extReader.read(msg, par);
          JsonToken tokNew = par.getCurrentToken();
          JsonLocation locNew = par.getCurrentLocation();
          boolean advanced = tokNew != tokLast || !locNew.equals(locLast);
          extRead |= advanced;

          if (!endObject(par)) {
            return;
          } else if (advanced && par.getCurrentToken() != JsonToken.FIELD_NAME) {
            tokLast = par.nextToken();
            locLast = par.getCurrentLocation();
          } else {
            tokLast = tokNew;
            locLast = locNew;
          }
        }
      }

      if (!endObject(par)) {
        // Can't rely on this exit condition inside the for loop because no readers may filter.
        return;
      }

      if (!extRead) {
        // No field was consumed by any reader, so we need to skip the field to make progress.
        if (logger.isDebugEnabled()) {
          logger.debug("Extension field not consumed by any reader, skipping: {} @{}:{}",
              par.getCurrentName(), locLast.getLineNr(), locLast.getCharOffset());
        }
        par.nextToken();
        par.skipChildren();
        tokLast = par.nextToken();
        locLast = par.getCurrentLocation();
      }
      // Else loop, try all readers again
    }
  }

  protected final boolean checkEnum(Enum<?> e) {
    if (e == null) {
      if (factory.isStrict()) {
        throw new IllegalArgumentException("Invalid enumerated value");
      } else {
        return false;
      }
    }
    return true;
  }

  protected final boolean checkContentCategory(String cat) {
    if (OpenRtbUtils.categoryFromName(cat) == null) {
      if (factory.isStrict()) {
        throw new IllegalArgumentException("Invalid ContentCategory value");
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Special case for empty-string input. Returning null in non-@Nullable method,
   * but this is non-strict mode anyway.
   */
  protected final boolean emptyToNull(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null) {
      token = par.nextToken();
    }
    return !factory().isStrict() && token == null;
  }
}
