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

import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.ImmutableSet;
import com.google.openrtb.util.OpenRtbUtils;
import com.google.protobuf.GeneratedMessageV3.ExtendableBuilder;
import java.io.IOException;

/**
 * A desserialization extension, can consume children of "ext" fields.
 * This base class is good for extensions stored directly in the parent Message;
 * for wrapper extensions use the subclass {@link OpenRtbJsonExtComplexReader}.
 * Both allow very flexible mapping from JSON fields into the model extension messages.
 * Consider an example with "imp": { ..., "ext": { p1: 1, p2: 2, p3: 3 } }, and three
 * extension readers where ER1 reads {p4}, ER2 reads {p2}, ER3 reads {p1,p3}.
 * The main {@link OpenRtbJsonReader} will start at p1, invoking all compatible
 * {@link OpenRtbJsonExtReader}s until some of them consumes that property.
 * We also need to consider some complications:
 *
 * <p><ol>
 * <li>ER3 will read p1, but then comes p2 which ER3 doesn't recognize. We need to store
 *    what we have been able to read, then return false, so the main reader knows that
 *    it needs to reset the loop and try all ExtReaders again (ER2 will read p2).</li>
 * <li>ER2 won't recognize p3, so the same thing happens: return false, main reader
 *    tries all ExtReader's, ER3 will handle p3.  This will be the second invocation
 *    to ER3 for the same "ext" object, that's why we need the ternary conditional
 *    below to reuse the {@code MyExt.Imp.Builder} if that was already set previously.</li>
 * <li>ER1 will be invoked several times, but never find any property it recognizes.
 *    It shouldn'set set an extension object that will be always empty.</li>
 * </ol>
 *
 * <p>Implementations of this interface have to be threadsafe.
 *
 * @param <EB> Type of message builder being constructed
 */
public abstract class OpenRtbJsonExtReader<EB extends ExtendableBuilder<?, EB>> {
  private final ImmutableSet<String> rootNameFilters;

  /**
   * Use this constructor for readers of scalar type.
   *
   * @param rootNameFilters Filter for the root names (direct fields of "ext").
   *     If empty, this reader will be invoked for any field.
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

  protected final boolean checkEnum(Enum<?> e) {
    return e != null;
  }

  protected final boolean checkContentCategory(String cat) {
    return cat != null;
  }
}
