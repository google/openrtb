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

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A serialization extension, can add children of "ext" fields.
 *
 * @param <T> type of extension field
 * <p>
 * Implementations of this interface have to be threadsafe.
 */
public abstract class OpenRtbJsonExtWriter<T> {
  private final String fieldName;
  private final boolean isMessage;

  /**
   * Use this constructor for writers of regular extensions.
   */
  protected OpenRtbJsonExtWriter() {
    this(null, false);
  }

  /**
   * This constructor supports extensions of any kind, including repeated and message type, so you
   * only need to write the message contents or array item in {@link #write(Object, JsonGenerator)}.
   *
   * @param fieldName name for the JSON field that contains an array or object; {@code null} for
   * regular extensions (single value of scalar type). 
   * @param isMessage {@code true} if the extension value is of message type, {@code false} scalar
   */
  protected OpenRtbJsonExtWriter(@Nullable String fieldName, boolean isMessage) {
    this.fieldName = fieldName;
    this.isMessage = isMessage;
    checkArgument(!(isMessage && fieldName == null), "isMessage=true requires fieldName");
  }

  protected final String getFieldName() {
    return fieldName;
  }

  protected final boolean isMessage() {
    return isMessage;
  }

  /**
   * Write a repeated extension. The default implementation will emit a JSON array; you
   * can override this if you need to provide a different encoding (e.g., a CSV string).
   *
   * @param list The list of extension values (repeated field)
   * @param gen The JSON generator
   * @throws IOException any serialization error
   */
  protected void writeRepeated(List<T> list, JsonGenerator gen) throws IOException {
    gen.writeArrayFieldStart(getFieldName());
    for (T item : list) {
      if (isMessage) {
        gen.writeStartObject();
      }
      write(item, gen);
      if (isMessage) {
        gen.writeEndObject();
      }
    }
    gen.writeEndArray();
  }

  @Override public String toString() {
    return getClass().getName()
        + (isMessage ? " (message" : " (scalar")
        + (fieldName == null ? ")" : " " + fieldName + ")");
  }

  /**
   * Write the fields of an extension value.
   * <p>
   * You need to override this if you want to rely on the default implementation of
   * {@link #writeRepeated(List, JsonGenerator)} for repeated extensions.
   *
   * @param value The extension value (scalar field)
   * @param gen The JSON generator
   * @throws IOException any serialization error
   */
  protected abstract void write(T value, JsonGenerator gen) throws IOException;
}
