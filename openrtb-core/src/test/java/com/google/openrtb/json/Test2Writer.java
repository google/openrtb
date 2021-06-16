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
import com.google.openrtb.Test.Test2;
import java.io.IOException;

/**
 * Regular extension: <code>"test2ext": {"test2": "data2", "test3": ["data3"]}</code>,
 * message type {@code Test2}.
 *
 * <p>Repeated extension: <code>"test2ext": [{"test2": "data2", "test3": ["data3"]},
 *                                           {"test2": "data4", "test3": ["data5"]}}</code>,
 * message type {@code Test2}.
 */
class Test2Writer extends OpenRtbJsonExtWriter<Test2> {

  public Test2Writer(String name) {
    super(name, true);
  }

  @Override protected void write(Test2 ext, JsonGenerator gen) throws IOException {
    gen.writeStringField("test2", ext.getTest2());
    OpenRtbJsonUtils.writeStrings("test3", ext.getTest3List(), gen);
  }
}
