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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.Message;

import com.fasterxml.jackson.core.JsonFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Factory that will create JSON serializer objects:
 * <ul>
 *   <li>Core model: {@link OpenRtbJsonWriter} and {@link OpenRtbJsonReader}</li>
 *   <li>Native model: {@link OpenRtbNativeJsonWriter} and {@link OpenRtbNativeJsonReader}</li>
 * </ul>
 * <p>
 * This class is not threadsafe. You should use only to configure and create the
 * reader/writer objects, which will be threadsafe.
 */
public class OpenRtbJsonFactory {
  private JsonFactory jsonFactory;
  private final Multimap<String, OpenRtbJsonExtReader<?>> extReaders;
  private final Map<String, OpenRtbJsonExtWriter<?>> extWriters;

  /**
   * Creates a new factory with default configuration.
   */
  public static OpenRtbJsonFactory create() {
    return new OpenRtbJsonFactory(null,
        LinkedListMultimap.<String, OpenRtbJsonExtReader<?>>create(),
        Maps.<String, OpenRtbJsonExtWriter<?>>newLinkedHashMap());
  }

  private OpenRtbJsonFactory(
      JsonFactory jsonFactory,
      Multimap<String, OpenRtbJsonExtReader<?>> extReaders,
      Map<String, OpenRtbJsonExtWriter<?>> extWriters) {
    this.jsonFactory = jsonFactory;
    this.extReaders = checkNotNull(extReaders);
    this.extWriters = checkNotNull(extWriters);
  }

  /**
   * Use a specific {@link JsonFactory}. A default factory will created if this is never called.
   */
  public OpenRtbJsonFactory setJsonFactory(JsonFactory jsonFactory) {
    this.jsonFactory = checkNotNull(jsonFactory);
    return this;
  }

  /**
   * Register a desserializer extension.
   * See {@link #register(OpenRtbJsonExtWriter, Class, String...)} about {@code paths}.
   *
   * @param extReader code to desserialize some extension properties
   * @param paths Paths in the OpenRTB model
   * @param <EB> Type of message builder being constructed
   */
  public <EB extends ExtendableBuilder<?, EB>> OpenRtbJsonFactory register(
      OpenRtbJsonExtReader<EB> extReader, String... paths) {
    for (String path : paths) {
      extReaders.put(path, extReader);
    }
    return this;
  }

  /**
   * Register a serializer extension. Each of these is registered for a specific
   * "path" inside the OpenRTB model; for example, "BidRequest.device.geo" registers
   * extensions for the {@code Geo} object inside the request's device object.
   * You need this path, not just the leaf message type like {@code Geo}, because
   * you might have the same message in a different place in the model (in this case,
   * there's also "BidRequest.user.geo") but you may not want the same extension
   * properties to be supported in both places.
   *
   * @param extWriter code to serialize some {@code extKlass}'s properties
   * @param extKlass class of container message, e.g. {@code MyImpression.class}
   * @param paths Paths in the OpenRTB model
   * @param <M> Type of message for the extension
   */
  public <M extends Message> OpenRtbJsonFactory register(
      OpenRtbJsonExtWriter<M> extWriter, Class<M> extKlass, String... paths) {
    for (String path : paths) {
      extWriters.put(path + ':' + extKlass.getName(), extWriter);
    }
    return this;
  }

  /**
   * Creates an {@link OpenRtbJsonWriter}, configured to the current state of this factory.
   */
  public OpenRtbJsonWriter newWriter() {
    return new OpenRtbJsonWriter(new OpenRtbJsonFactory(
        getJsonFactory(),
        ImmutableMultimap.copyOf(extReaders),
        ImmutableMap.copyOf(extWriters)));
  }

  /**
   * Creates an {@link OpenRtbJsonWriter}, configured to the current state of this factory.
   */
  public OpenRtbJsonReader newReader() {
    return new OpenRtbJsonReader(new OpenRtbJsonFactory(
        getJsonFactory(),
        ImmutableMultimap.copyOf(extReaders),
        ImmutableMap.copyOf(extWriters)));
  }

  /**
   * Creates an {@link OpenRtbNativeJsonWriter}, configured to the current state of this factory.
   */
  public OpenRtbNativeJsonWriter newNativeWriter() {
    return new OpenRtbNativeJsonWriter(new OpenRtbJsonFactory(
        getJsonFactory(),
        ImmutableMultimap.copyOf(extReaders),
        ImmutableMap.copyOf(extWriters)));
  }

  /**
   * Creates an {@link OpenRtbNativeJsonWriter}, configured to the current state of this factory.
   */
  public OpenRtbNativeJsonReader newNativeReader() {
    return new OpenRtbNativeJsonReader(new OpenRtbJsonFactory(
        getJsonFactory(),
        ImmutableMultimap.copyOf(extReaders),
        ImmutableMap.copyOf(extWriters)));
  }

  @SuppressWarnings("unchecked")
  <EB extends ExtendableBuilder<?, EB>>
  Collection<OpenRtbJsonExtReader<EB>> getReaders(String path) {
    return (Collection<OpenRtbJsonExtReader<EB>>) (Collection<?>) extReaders.get(path);
  }

  @SuppressWarnings("unchecked")
  <M extends Message> OpenRtbJsonExtWriter<M> getWriter(String path) {
    return (OpenRtbJsonExtWriter<M>) extWriters.get(path);
  }

  public JsonFactory getJsonFactory() {
    if (jsonFactory == null) {
      jsonFactory = new JsonFactory();
    }
    return jsonFactory;
  }
}
