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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.protobuf.ProtocolMessageEnum;
import java.io.IOException;
import java.util.List;

/**
 * Utilities for writing JSON serialization code.
 */
public class OpenRtbJsonUtils {
  private static final long MAX_JSON_INT = 1L << 53;

  /**
   * Returns the current field name, or empty string if none.
   */
  public static String getCurrentName(JsonParser par) throws IOException {
    String name = par.getCurrentName();
    return name == null ? "" : name;
  }

  /**
   * Starts an Object, skipping the '{' token, and if necessary a field name before it.
   */
  public static void startObject(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null || token == JsonToken.FIELD_NAME) {
      token = par.nextToken();
    }
    if (token == JsonToken.START_OBJECT) {
      par.nextToken();
    } else {
      throw new JsonParseException(par, "Expected start of object");
    }
  }

  /**
   * Returns {@code true} if the JSON Object is NOT finished.
   * Logic is inverted so this is used as a loop-end condition.
   */
  public static boolean endObject(JsonParser par) {
    JsonToken token = par.getCurrentToken();
    return token != null && token != JsonToken.END_OBJECT;
  }

  /**
   * Starts an Array, skipping the '[' token, and if necessary a field name before it.
   */
  public static void startArray(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null || token == JsonToken.FIELD_NAME) {
      token = par.nextToken();
    }
    if (token == JsonToken.START_ARRAY) {
      par.nextToken();
    } else {
      throw new JsonParseException(par, "Expected start of array");
    }
  }

  /**
   * Returns {@code true} if the JSON Object is NOT finished.
   * Logic is inverted so this is used as a loop-end condition.
   */
  public static boolean endArray(JsonParser par) {
    JsonToken token = par.getCurrentToken();
    return token != null && token != JsonToken.END_ARRAY;
  }

  /**
   * Skips a field name if necessary, returning the current token then.
   */
  public static JsonToken peekToken(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null || token == JsonToken.FIELD_NAME) {
      token = par.nextToken();
    }
    return token;
  }

  /**
   * Skips a field name if necessary, returning the current token then, which must be
   * the start of an Array or Object: '{' or '['.
   */
  public static JsonToken peekStructStart(JsonParser par) throws IOException {
    JsonToken token = peekToken(par);
    if (token.isStructStart()) {
      return token;
    } else {
      throw new JsonParseException(par, "Expected start of array or object");
    }
  }

  /**
   * Writes a boolean as int, where false = 0 and true = 1.
   */
  public static void writeIntBoolField(String fieldName, boolean data, JsonGenerator gen)
      throws IOException {
    gen.writeNumberField(fieldName, data ? 1 : 0);
  }

  /**
   * Writes a string array if not empty.
   */
  public static void writeStrings(String fieldName, List<String> data, JsonGenerator gen)
      throws IOException {
    if (!data.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (String d : data) {
        gen.writeString(d);
      }
      gen.writeEndArray();
    }
  }

  /**
   * Writes an int array if not empty.
   */
  public static void writeInts(String fieldName, List<Integer> data, JsonGenerator gen)
      throws IOException {
    if (!data.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (Integer d : data) {
        gen.writeNumber(d);
      }
      gen.writeEndArray();
    }
  }

  /**
   * Writes a long, using quotes only if it's too big (over 53-bit mantissa).
   */
  public static void writeLong(long data, JsonGenerator gen) throws IOException {
    if (data > MAX_JSON_INT || data < -MAX_JSON_INT) {
      gen.writeString(Long.toString(data));
    } else {
      gen.writeNumber(data);
    }
  }

  /**
   * Writes a long, using quotes only if it's too big (over 53-bit mantissa).
   */
  public static void writeLongField(String fieldName, long data, JsonGenerator gen)
      throws IOException {
    gen.writeFieldName(fieldName);
    writeLong(data, gen);
  }

  /**
   * Writes a long array if not empty, using quotes for values that are too big.
   *
   * @see #writeLong(long, JsonGenerator)
   */
  public static void writeLongs(String fieldName, List<Long> data, JsonGenerator gen)
      throws IOException {
    if (!data.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (long d : data) {
        writeLong(d, gen);
      }
      gen.writeEndArray();
    }
  }

  /**
   * Writes a enum value as an int, using its Protobuf number.
   */
  protected static void writeEnum(ProtocolMessageEnum e, JsonGenerator gen) throws IOException {
    gen.writeNumber(e.getNumber());
  }

  /**
   * Writes a enum array if not empty.
   *
   * @see #writeEnum(ProtocolMessageEnum, JsonGenerator)
   */
  public static void writeEnums(
      String fieldName, List<? extends ProtocolMessageEnum> enums, JsonGenerator gen)
      throws IOException {
    if (!enums.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (ProtocolMessageEnum e : enums) {
        writeEnum(e, gen);
      }
      gen.writeEndArray();
    }
  }

  /**
   * Reads from either a JSON Value String (containing CSV) or a JSON Array.
   * The dual input format is needed because some fields (e.g. keywords) were allowed
   * to be of either type in OpenRTB 2.2; now in 2.3 they are all CSV strings only.
   * TODO: Simplify this to only accept CSV strings after 2.2 compatibility is dropped.
   */
  public static String readCsvString(JsonParser par) throws IOException {
    JsonToken currentToken = par.getCurrentToken();
    if (currentToken == JsonToken.START_ARRAY) {
      StringBuilder keywords = new StringBuilder();
      for (startArray(par); endArray(par); par.nextToken()) {
        if (keywords.length() != 0) {
          keywords.append(',');
        }
        keywords.append(par.getText());
      }
      return keywords.toString();
    } else if (currentToken == JsonToken.VALUE_STRING) {
      return par.getText();
    } else {
      throw new JsonParseException(par, "Expected string or array");
    }
  }
}
