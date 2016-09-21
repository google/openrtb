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

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.openrtb.util.OpenRtbUtils;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage.ExtendableMessage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Serializes OpenRTB messages to JSON.
 */
public class AbstractOpenRtbJsonWriter {
  private final OpenRtbJsonFactory factory;

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
   * @param <EM> Type of message being serialized, containing extensions
   * @throws IOException any serialization error
   */
  @SuppressWarnings("unchecked")
  protected final <EM extends ExtendableMessage<EM>>
  void writeExtensions(EM msg, JsonGenerator gen) throws IOException {
    boolean openExt = false;

    for (Map.Entry<FieldDescriptor, Object> field : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = field.getKey();
      if (fd.isExtension()) {
        if (fd.isRepeated()) {
          List<Object> extValue = (List<Object>) field.getValue();
          if (!extValue.isEmpty()) {
            OpenRtbJsonExtWriter<Object> extWriter =
                factory.getWriter(msg.getClass(), extValue.get(0).getClass(), fd.getName());
            if (extWriter != null) {
              openExt = openExt(gen, openExt);
              extWriter.writeRepeated(extValue, gen);
            }
          }
        } else {
          Object extValue = field.getValue();
          OpenRtbJsonExtWriter<Object> extWriter =
              factory.getWriter(msg.getClass(), extValue.getClass(), fd.getName());
          if (extWriter != null) {
            openExt = openExt(gen, openExt);
            extWriter.writeSingle(extValue, gen);
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

  protected final boolean checkRequired(boolean hasProperty) {
    if (!hasProperty) {
      if (factory.isStrict()) {
        throw new IllegalArgumentException("Required property not set");
      } else {
        return false;
      }
    }
    return true;
  }

  protected final boolean checkRequired(int propertyCount) {
    if (propertyCount == 0) {
      if (factory.isStrict()) {
        throw new IllegalArgumentException("Required property is empty");
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Writes a string that represents a ContentCategory's JSON name, returning success status.
   * If the factory is in strict mode, the category name is validated.
   */
  protected final boolean writeContentCategory(String cat, JsonGenerator gen) throws IOException {
    if (!factory.isStrict() || OpenRtbUtils.categoryFromName(cat) != null) {
      gen.writeString(cat);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Writes an array of ContentCategory if not empty.
   *
   * @see #writeContentCategory(String, JsonGenerator)
   */
  protected final void writeContentCategories(
      String fieldName, List<String> cats, JsonGenerator gen)
      throws IOException {
    if (!cats.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (String cat : cats) {
        writeContentCategory(cat, gen);
      }
      gen.writeEndArray();
    }
  }
}
