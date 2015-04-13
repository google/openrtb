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

import static com.google.openrtb.json.OpenRtbJsonUtils.getCurrentName;

import com.google.openrtb.Test.Test2;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Sample JSON reader for a repeated extension of message type.
 * (Exactly the same code as for regular extension of message type / {@link Test1Reader}.)
 */
class Test2Reader<EB extends ExtendableBuilder<?, EB>>
extends OpenRtbJsonExtReader<EB, Test2.Builder> {

  public Test2Reader(GeneratedExtension<?, ?> key) {
    super(key);
  }

  @Override protected void read(EB msg, Test2.Builder ext, JsonParser par)
      throws IOException {
    switch (getCurrentName(par)) {
      case "test2":
        ext.setTest2(par.nextTextValue());
        break;
    }
  }
}
