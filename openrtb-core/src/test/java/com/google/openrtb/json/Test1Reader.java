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

import com.fasterxml.jackson.core.JsonParser;
import com.google.openrtb.Test.Test1;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import java.io.IOException;

/**
 * Regular extension: {@code "test1": "data1"}, message type {@code Test1}.
 *
 * <p>(Exactly the same code as for repeated extension of message type / {@link Test2Reader}.)
 *
 * @param <EB> Type of message builder being constructed.  Keeping this generic parameter
 *     open here allows this extension to be used inside any message; if you don't need that,
 *     you can bind this to a specific message builder type, see {@link Test3Reader}.
 */
class Test1Reader<EB extends ExtendableBuilder<?, EB>>
    extends OpenRtbJsonExtComplexReader<EB, Test1.Builder> {

  public Test1Reader(GeneratedExtension<?, ?> key) {
    super(key, false, "test1");
  }

  @Override protected void read(Test1.Builder ext, JsonParser par) throws IOException {
    if ("test1".equals(getCurrentName(par))) {
      ext.setTest1(par.nextTextValue());
    }
  }
}
