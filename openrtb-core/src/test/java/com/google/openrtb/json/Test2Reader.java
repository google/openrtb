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

import static com.google.openrtb.json.OpenRtbJsonUtils.endArray;
import static com.google.openrtb.json.OpenRtbJsonUtils.getCurrentName;
import static com.google.openrtb.json.OpenRtbJsonUtils.startArray;

import com.fasterxml.jackson.core.JsonParser;
import com.google.openrtb.Test.Test2;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import java.io.IOException;

/**
 * Regular extension: <code>"test2ext": {"test2": "data2", "test3": ["data3"]}</code>,
 * message type {@code Test2}.
 *
 * <p>Repeated extension: <code>"test2ext": [{"test2": "data2", "test3": ["data3"]},
 *                                           {"test2": "data4", "test3": ["data5"]}}</code>,
 * message type {@code Test2}.
 */
class Test2Reader<EB extends ExtendableBuilder<?, EB>>
    extends OpenRtbJsonExtComplexReader<EB, Test2.Builder> {

  public Test2Reader(GeneratedExtension<?, ?> key, String name) {
    super(key, true, name);
  }

  @Override protected void read(Test2.Builder ext, JsonParser par) throws IOException {
    switch (getCurrentName(par)) {
      case "test2":
        ext.setTest2(par.nextTextValue());
        break;
      case "test3":
        for (startArray(par); endArray(par); par.nextToken()) {
          ext.addTest3(par.getText());
        }
        break;
    }
  }
}
