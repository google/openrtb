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

import com.google.protobuf.GeneratedMessage.ExtendableBuilder;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * A desserialization extension, can consume children of "ext" fields.
 * <p>
 * Implementations of this interface have to be threadsafe.
 *
 * @param <EB> Type of message builder being constructed by the {@link OpenRtbJsonReader}.
 */
public interface OpenRtbJsonExtReader<EB extends ExtendableBuilder<?, EB>> {

  /**
   * Desserializes extension properties supported by this reader, if any.
   *
   * @param msg Buider for the container message, where an extension message will be set
   * @param par JSON parser, positioned at the property to be desserialized
   * @return {@code true} if at least one extension property was consumed
   */
  boolean read(EB msg, JsonParser par) throws IOException;
}
