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

import java.io.IOException;
import java.util.List;

/**
 * A serialization extension, can add children of "ext" fields.
 *
 * @param <T> type of extension field
 * <p>
 * Implementations of this interface have to be threadsafe.
 */
public abstract class OpenRtbJsonExtWriter<T> {
  private final String fieldName;
  private final boolean object;

  /**
   * Use this constructor for writers of regular extensions.
   */
  protected OpenRtbJsonExtWriter() {
    this(null, true);
  }

  /**
   * Use this constructor for writers of repeated extensions.
   */
  protected OpenRtbJsonExtWriter(String fieldName, boolean object) {
    this.fieldName = fieldName;
    this.object = object;
  }

  protected final String getFieldName() {
    return fieldName;
  }

  void writeRepeated(List<T> list, JsonGenerator gen) throws IOException {
    gen.writeArrayFieldStart(fieldName);
    for (T item : list) {
      if (object) {
        gen.writeStartObject();
      }
      write(item, gen);
      if (object) {
        gen.writeEndObject();
      }
    }
    gen.writeEndArray();
  }

  /**
   * Serialize all properties set in an extension node.
   *
   * @param value The extension value (scalar field)
   * @param gen The JSON generator
   * @throws IOException any serialization error
   */
  protected abstract void write(T value, JsonGenerator gen) throws IOException;
}
