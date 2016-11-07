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

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.List;

/**
 * A serialization extension, can add children of "ext" fields.
 *
 * <p>Implementations of this interface have to be threadsafe.
 *
 * @param <T> Type of extension field
 */
public abstract class OpenRtbJsonExtWriter<T> {
  private final String rootName;
  private final boolean isJsonObject;

  /**
   * Use this constructor for writers of regular extensions.
   */
  protected OpenRtbJsonExtWriter() {
    this.rootName = null;
    this.isJsonObject = false;
  }

  /**
   * Use this constructor for writers of repeated extensions.
   *
   * @param rootName Name for the "root" entity: the field that holds an array or the object
   * @param isJsonObject {@code true} if the extension value is serialized to a JSON object
   */
  protected OpenRtbJsonExtWriter(String rootName, boolean isJsonObject) {
    this.rootName = checkNotNull(rootName);
    this.isJsonObject = isJsonObject;
  }

  protected final void writeRepeated(List<T> extList, JsonGenerator gen) throws IOException {
    gen.writeArrayFieldStart(rootName);
    for (T ext : extList) {
      if (isJsonObject) {
        gen.writeStartObject();
      }
      write(ext, gen);
      if (isJsonObject) {
        gen.writeEndObject();
      }
    }
    gen.writeEndArray();
  }

  protected final void writeSingle(T value, JsonGenerator gen) throws IOException {
    if (isJsonObject) {
      gen.writeObjectFieldStart(rootName);
    }
    write(value, gen);
    if (isJsonObject) {
      gen.writeEndObject();
    }
  }

  /**
   * Write the fields of an extension value.
   *
   * @param value The extension value (scalar field)
   * @param gen The JSON generator
   * @throws IOException any serialization error
   */
  protected abstract void write(T value, JsonGenerator gen) throws IOException;

  @Override public String toString() {
    return getClass().getName()
        + (isJsonObject ? " JSON=object" : " JSON=scalar")
        + (rootName == null ? " regular" : " repeated:" + rootName);
  }

  protected final boolean checkRequired(boolean hasProperty) {
    return hasProperty;
  }

  protected final boolean checkRequired(int propertyCount) {
    return propertyCount != 0;
  }
}
