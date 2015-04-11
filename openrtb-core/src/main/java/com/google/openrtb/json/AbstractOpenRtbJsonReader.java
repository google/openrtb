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
import static com.google.openrtb.json.OpenRtbJsonUtils.getCurrentName;
import static com.google.openrtb.json.OpenRtbJsonUtils.startObject;

import com.google.protobuf.GeneratedMessage.ExtendableBuilder;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.Collection;

/**
 * Desserializes OpenRTB messages from JSON.
 */
public abstract class AbstractOpenRtbJsonReader {
  private final OpenRtbJsonFactory factory;

  protected AbstractOpenRtbJsonReader(OpenRtbJsonFactory factory) {
    this.factory = factory;
  }

  public final OpenRtbJsonFactory factory() {
    return factory;
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
        throw new IOException("Unhandled extension at " + path
            + ": " + getCurrentName(par));
      }
      // Else loop, try all readers again
    }
  }
}
