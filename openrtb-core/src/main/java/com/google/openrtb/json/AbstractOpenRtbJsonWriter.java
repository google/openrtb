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

  /**
   * Write any extensions that may exist in a message.
   *
   * @param msg A message that may contain extensions
   * @param gen The JSON generator
   * @throws IOException any serialization error
   */
  @SuppressWarnings("unchecked")
  protected <EM extends ExtendableMessage<EM>>
  void writeExtensions(EM msg, JsonGenerator gen) throws IOException {
    boolean openExt = false;

    for (Map.Entry<FieldDescriptor, Object> field : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = field.getKey();
      if (fd.isExtension()) {
        if (fd.isRepeated()) {
          openExt = writeRepeated(msg, gen, fd.getName(), (List<Object>) field.getValue(), openExt);
        } else {
          openExt = writeSingle(msg, gen, fd.getName(), field.getValue(), openExt);
        }
      }
    }

    if (openExt) {
      gen.writeEndObject();
    }
  }

  protected <EM extends ExtendableMessage<EM>> boolean writeSingle(
      EM msg, JsonGenerator gen, String fieldName, Object extValue, boolean openExtP)
      throws IOException {
    boolean openExt = openExtP;
    OpenRtbJsonExtWriter<Object> extWriter =
        factory.getWriter(msg.getClass(), extValue.getClass(), fieldName);
    if (extWriter != null) {
      openExt = openExt(gen, openExt);
      extWriter.write(extValue, gen);
    }
    return openExt;
  }

  protected <EM extends ExtendableMessage<EM>> boolean writeRepeated(
      EM msg, JsonGenerator gen, String fieldName, List<Object> extList, boolean openExtP)
      throws IOException {
    boolean openExt = openExtP;
    if (!extList.isEmpty()) {
      OpenRtbJsonExtWriter<Object> extWriter =
          factory.getWriter(msg.getClass(), extList.get(0).getClass(), fieldName);
      if (extWriter != null) {
        openExt = openExt(gen, openExt);
        extWriter.writeRepeated(extList, gen);
      }
    }
    return openExt;
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
