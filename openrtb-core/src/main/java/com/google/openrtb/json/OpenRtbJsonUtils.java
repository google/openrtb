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

import com.google.openrtb.util.OpenRtbUtils;
import com.google.protobuf.ProtocolMessageEnum;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.List;

/**
 * Utilities for writing JSON serialization code.
 */
public class OpenRtbJsonUtils {
  public static String getCurrentName(JsonParser par) throws IOException {
    String name = par.getCurrentName();
    return name == null ? "" : name;
  }

  public static void startObject(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null || token == JsonToken.FIELD_NAME) {
      token = par.nextToken();
    }
    if (token == JsonToken.START_OBJECT) {
      par.nextToken();
    } else {
      throw new JsonParseException("Expected start of object", par.getCurrentLocation());
    }
  }

  public static boolean endObject(JsonParser par) {
    JsonToken token = par.getCurrentToken();
    return token != null && token != JsonToken.END_OBJECT;
  }

  public static void startArray(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null || token == JsonToken.FIELD_NAME) {
      token = par.nextToken();
    }
    if (token == JsonToken.START_ARRAY) {
      par.nextToken();
    } else {
      throw new JsonParseException("Expected start of array", par.getCurrentLocation());
    }
  }

  public static boolean endArray(JsonParser par) {
    JsonToken token = par.getCurrentToken();
    return token != null && token != JsonToken.END_ARRAY;
  }

  public static JsonToken peekStruct(JsonParser par) throws IOException {
    JsonToken token = par.getCurrentToken();
    if (token == null || token == JsonToken.FIELD_NAME) {
      token = par.nextToken();
    }
    if (token.isStructStart()) {
      return token;
    } else {
      throw new JsonParseException("Expected start of array or object", par.getCurrentLocation());
    }
  }

  public static double getDoubleValue(JsonParser par) throws IOException {
    return Double.parseDouble(par.getText());
  }

  public static boolean getIntBoolValue(JsonParser par) throws IOException {
    return par.getIntValue() != 0;
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
      throw new JsonParseException("Expected string or array", par.getCurrentLocation());
    }
  }

  public static void writeIntBoolField(String fieldName, boolean data, JsonGenerator gen)
      throws IOException {
    gen.writeNumberField(fieldName, data ? 1 : 0);
  }

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

  public static void writeLongs(String fieldName, List<Long> data, JsonGenerator gen)
      throws IOException {
    if (!data.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (Long d : data) {
        gen.writeNumber(d);
      }
      gen.writeEndArray();
    }
  }

  public static void writeEnums(
      String fieldName, List<? extends ProtocolMessageEnum> enums, JsonGenerator gen)
      throws IOException {
    if (!enums.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (ProtocolMessageEnum e : enums) {
        gen.writeNumber(e.getNumber());
      }
      gen.writeEndArray();
    }
  }

  public static void writeContentCategories(
      String fieldName, List<String> cats, JsonGenerator gen)
      throws IOException {
    if (!cats.isEmpty()) {
      gen.writeArrayFieldStart(fieldName);
      for (String cat : cats) {
        if (OpenRtbUtils.categoryFromName(cat) != null) {
          gen.writeString(cat);
        }
      }
      gen.writeEndArray();
    }
  }
}
