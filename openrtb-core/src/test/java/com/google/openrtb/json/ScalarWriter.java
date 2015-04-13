/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

/**
 * Sample JSON writer for a regular or repeated extension of scalar type.
 * <p>
 * Nothing different is necessary for repeated extensions; the {@link #write()} method
 * will simply be invoked multiple times, for each item in the sequence.
 */
class ScalarWriter extends OpenRtbJsonExtWriter<Integer> {

  @Override protected void write(Integer ext, JsonGenerator gen) throws IOException {
    gen.writeNumberField("test3", ext);
  }
}
