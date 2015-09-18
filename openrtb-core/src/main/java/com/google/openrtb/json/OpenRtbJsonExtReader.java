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

import com.google.common.collect.ImmutableSet;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * A desserialization extension, can consume children of "ext" fields.
 * This base class is good for extensions stored directly in the parent Message;
 * for wrapper extensions use the subclass {@link OpenRtbJsonExtComplexReader}.
 *
 * <p>
 * Implementations of this interface have to be threadsafe.
 *
 * @param <EB> Type of message builder being constructed
 */
public abstract class OpenRtbJsonExtReader<EB extends ExtendableBuilder<?, EB>> {

  private final ImmutableSet<String> rootNameFilters;

  /**
   * Use this constructor for readers of scalar type.
   *
   * @param rootNameFilters Filter for the root names (direct fields of "ext").
   * If empty, this reader will be invoked for any field.
   */
  protected OpenRtbJsonExtReader(String... rootNameFilters) {
    this.rootNameFilters = ImmutableSet.copyOf(rootNameFilters);
  }

  protected final boolean filter(JsonParser par) throws IOException {
    return rootNameFilters.isEmpty() || rootNameFilters.contains(par.getCurrentName());
  }

  @Override public String toString() {
    return getClass().getName()
        + (rootNameFilters.isEmpty() ? "" : " filter=" + rootNameFilters.toString());
  }

  /**
   * Reads a field, which will be stored as direct extensions in the {@code msg}.
   *
   * @param msg Builder for the container message, where an extension message will be set
   * @param par JSON parser, positioned at the property to be desserialized
   * @throws IOException any parsing error
   */
  protected abstract void read(EB msg, JsonParser par) throws IOException;
}
