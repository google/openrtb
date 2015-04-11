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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage.ExtendableMessage;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Serializes OpenRTB messages to JSON.
 */
public class AbstractOpenRtbJsonWriter {
  private final OpenRtbJsonFactory factory;
  private final boolean requiredAlways = false;

  protected AbstractOpenRtbJsonWriter(OpenRtbJsonFactory factory) {
    this.factory = factory;
  }

  public final OpenRtbJsonFactory factory() {
    return factory;
  }

  protected <EM extends ExtendableMessage<EM>>
  void writeExtensions(EM msg, JsonGenerator gen, String path) throws IOException {
    boolean openExt = false;
    StringBuilder fullPath = new StringBuilder(path).append(':');
    int pathMsg = fullPath.length();

    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = entry.getKey();
      if (fd.isExtension()) {
        fullPath.setLength(pathMsg);
        if (fd.isRepeated()) {
          @SuppressWarnings("unchecked")
          List<Object> extList = (List<Object>) entry.getValue();
          if (!extList.isEmpty()) {
            fullPath.append(extList.get(0).getClass().getName());
            OpenRtbJsonExtWriter<Object> extWriter = factory.getWriter(fullPath.toString());
            if (extWriter instanceof OpenRtbJsonExtListWriter<?>) {
              openExt = openExt(gen, openExt);
              ((OpenRtbJsonExtListWriter<Object>) extWriter).writeList(extList, gen);
            }
          }
        } else {
          Object extValue = entry.getValue();
          fullPath.append(extValue.getClass().getName());
          OpenRtbJsonExtWriter<Object> extWriter = factory.getWriter(fullPath.toString());
          if (extWriter != null) {
            openExt = openExt(gen, openExt);
            extWriter.write(extValue, gen);
          }
        }
      }
    }

    if (openExt) {
      gen.writeEndObject();
    }
  }

  private static boolean openExt(JsonGenerator gen, boolean openExt) throws IOException {
    if (!openExt) {
      gen.writeObjectFieldStart("ext");
    }
    return true;
  }

  protected boolean checkRequired(boolean hasProperty) {
    return requiredAlways || hasProperty;
  }
}
