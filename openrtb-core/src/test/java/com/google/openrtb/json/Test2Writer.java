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

import com.google.openrtb.Test.Test2;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * Sample JSON writer for a repeated extension of message type.
 * <p>
 * To support repeated extensions, the super-call needs the field name and object/scalar option.
 * The field name, the array-open and array-close tokens, and (for objects) the object-open
 * and object-close tokens, will all be emitted by the framework, so the {@link #write()} method
 * provided here is the same you need for a regular extension: just write all fields for one item.
 * Suppose {@code x: ( ext: ( a: 10 ))} and {@code y: ( ext: ( b: [ ( a: 1 ), ( a: 2 ) ] ))}.
 * where {@code x.ext.a} is a regular extension and {@code y.ext.b} is a repeated extension
 * which value is a sequence of the same {@code x.ext.a} objects, you can share the writer.
 */
class Test2Writer extends OpenRtbJsonExtWriter<Test2> {

  public Test2Writer() {
    super("test2obj", true);
  }

  @Override protected void write(Test2 ext, JsonGenerator gen) throws IOException {
    gen.writeStringField("test2", ext.getTest2());
  }
}
