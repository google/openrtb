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

import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.protobuf.GeneratedMessage.ExtendableBuilder;
import com.google.protobuf.Message;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Factory that will create JSON serializer objects:
 * <ul>
 *   <li>Core model: {@link OpenRtbJsonWriter} and {@link OpenRtbJsonReader}</li>
 *   <li>Native model: {@link OpenRtbNativeJsonWriter} and {@link OpenRtbNativeJsonReader}</li>
 * </ul>
 *
 * <p>This class is NOT threadsafe. You should use only to configure and create the
 * reader/writer objects, which will be threadsafe.
 */
public class OpenRtbJsonFactory {
  private static final String FIELDNAME_ALL = "*";

  private JsonFactory jsonFactory;
  private boolean strict;
  private boolean rootNativeField;
  private boolean forceNativeAsObject;
  private final SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders;
  private final Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters;

  protected OpenRtbJsonFactory(
      @Nullable JsonFactory jsonFactory,
      boolean strict,
      boolean rootNativeField,
      boolean forceNativeAsObject,
      @Nullable SetMultimap<String, OpenRtbJsonExtReader<?>> extReaders,
      @Nullable Map<String, Map<String, Map<String, OpenRtbJsonExtWriter<?>>>> extWriters) {
    this.jsonFactory = jsonFactory;
    this.strict = strict;
    this.rootNativeField = rootNativeField;
    this.forceNativeAsObject = forceNativeAsObject;
    this.extReaders = extReaders == null ? LinkedHashMultimap.create() : extReaders;
    this.extWriters = extWriters == null ? new LinkedHashMap<>() : extWriters;
  }

  /**
   * This "immutable-clone-constructor" returns an immutable copy of this factory.
   * Subclasses should have a protected constructor that super-calls this and makes
   * immutable copies of their own fields if necessary.  You can then override the
   * methods that create Reader/Writer objects so they use that constructor.
   */
  protected OpenRtbJsonFactory(OpenRtbJsonFactory config) {
    this.jsonFactory = config.getJsonFactory();
    this.strict = config.strict;
    this.rootNativeField = config.rootNativeField;
    this.forceNativeAsObject = config.forceNativeAsObject;
    this.extReaders = ImmutableSetMultimap.copyOf(config.extReaders);
    this.extWriters = ImmutableMap.copyOf(Maps.transformValues(config.extWriters,
        (Map<String, Map<String, OpenRtbJsonExtWriter<?>>> map) ->
            ImmutableMap.copyOf(Maps.transformValues(map, map2 -> ImmutableMap.copyOf(map2)))));
  }

  /**
   * Creates a new factory with default configuration.
   */
  public static OpenRtbJsonFactory create() {
    return new OpenRtbJsonFactory(null, false, false, false, null, null);
  }

  /**
   * Use a specific {@link JsonFactory}. A default factory will created if this is never called.
   */
  public final OpenRtbJsonFactory setJsonFactory(JsonFactory jsonFactory) {
    this.jsonFactory = checkNotNull(jsonFactory);
    return this;
  }

  /**
   * Sets strict mode.
   */
  public final OpenRtbJsonFactory setStrict(boolean strict) {
    this.strict = strict;
    return this;
  }

  /**
   * Sets root native field generation mode.
   */
  public final OpenRtbJsonFactory setRootNativeField(boolean rootNativeField) {
    this.rootNativeField = rootNativeField;
    return this;
  }

  /**
   * Sets object native field generation mode.
   */
  public final OpenRtbJsonFactory setForceNativeAsObject(boolean forceNativeAsObject) {
    this.forceNativeAsObject = forceNativeAsObject;
    return this;
  }

  /**
   * Returns {@code true} for strict mode, {@code false} lenient mode.
   */
  public final boolean isStrict() {
    return strict;
  }

  /**
   * Returns {@code true} for root native field mode, {@code false} if not.
   */
  public final boolean isRootNativeField() {
    return rootNativeField;
  }

  /**
   * Returns {@code true} for object native field mode, {@code false} if not.
   */
  public boolean isForceNativeAsObject() {
    return forceNativeAsObject;
  }

  /**
   * Register an extension reader.
   *
   * @param extReader code to desserialize some extension properties
   * @param msgKlass class of extension message's builder, e.g. {@code MyImp.Builder.class}
   */
  public final <EB extends ExtendableBuilder<?, EB>> OpenRtbJsonFactory register(
      OpenRtbJsonExtReader<EB> extReader, Class<EB> msgKlass) {
    extReaders.put(msgKlass.getName(), extReader);
    return this;
  }

  /**
   * Register an extension writer, bound to a specific field name. This writer will be
   * used in preference to a non-field-specific writer that may exist for the same class.
   *
   * @param extWriter code to serialize some {@code extKlass}'s properties
   * @param extKlass class of extension message, e.g. {@code MyImp.class}
   * @param msgKlass class of container message, e.g. {@code Imp.class}
   * @param fieldName name of the field containing the extension
   * @param <T> Type of value for the extension
   * @see #register(OpenRtbJsonExtWriter, Class, Class)
   */
  public final <T> OpenRtbJsonFactory register(OpenRtbJsonExtWriter<T> extWriter,
      Class<T> extKlass, Class<? extends Message> msgKlass, String fieldName) {
    Map<String, Map<String, OpenRtbJsonExtWriter<?>>> mapMsg = extWriters.get(msgKlass.getName());
    if (mapMsg == null) {
      extWriters.put(msgKlass.getName(), mapMsg = new LinkedHashMap<>());
    }
    Map<String, OpenRtbJsonExtWriter<?>> mapKlass = mapMsg.get(extKlass.getName());
    if (mapKlass == null) {
      mapMsg.put(extKlass.getName(), mapKlass = new LinkedHashMap<>());
    }
    mapKlass.put(fieldName == null ? FIELDNAME_ALL : fieldName, extWriter);
    return this;
  }

  /**
   * Register an extension writer, not bound to any a field name (so this serializer
   * can be used for any extension of the provided class).
   *
   * @param extWriter code to serialize some {@code extKlass}'s properties
   * @param extKlass class of extension message, e.g. {@code MyImp.class}
   * @param msgKlass class of container message, e.g. {@code Imp.class}
   * @param <T> Type of value for the extension
   * @see #register(OpenRtbJsonExtWriter, Class, Class, String)
   */
  public final <T> OpenRtbJsonFactory register(OpenRtbJsonExtWriter<T> extWriter,
      Class<T> extKlass, Class<? extends Message> msgKlass) {
    return register(extWriter, extKlass, msgKlass, FIELDNAME_ALL);
  }

  /**
   * Creates an {@link OpenRtbJsonWriter}, configured to the current state of this factory.
   */
  public OpenRtbJsonWriter newWriter() {
    return new OpenRtbJsonWriter(new OpenRtbJsonFactory(this));
  }

  /**
   * Creates an {@link OpenRtbJsonReader}, configured to the current state of this factory.
   */
  public OpenRtbJsonReader newReader() {
    return new OpenRtbJsonReader(new OpenRtbJsonFactory(this));
  }

  /**
   * Creates an {@link OpenRtbNativeJsonWriter}, configured to the current state of this factory.
   */
  public OpenRtbNativeJsonWriter newNativeWriter() {
    return new OpenRtbNativeJsonWriter(new OpenRtbJsonFactory(this));
  }

  /**
   * Creates an {@link OpenRtbNativeJsonReader}, configured to the current state of this factory.
   */
  public OpenRtbNativeJsonReader newNativeReader() {
    return new OpenRtbNativeJsonReader(new OpenRtbJsonFactory(this));
  }

  @SuppressWarnings("unchecked")
  final <EB extends ExtendableBuilder<?, EB>>
      Set<OpenRtbJsonExtReader<EB>> getReaders(Class<EB> msgClass) {
    return (Set<OpenRtbJsonExtReader<EB>>) (Set<?>) extReaders.get(msgClass.getName());
  }

  @SuppressWarnings("unchecked")
  final <T> OpenRtbJsonExtWriter<T> getWriter(
      Class<? extends Message> msgClass, Class<?> extClass, @Nullable String fieldName) {
    Map<String, Map<String, OpenRtbJsonExtWriter<?>>> mapMsg = extWriters.get(msgClass.getName());
    if (mapMsg == null) {
      return null;
    }
    Map<String, OpenRtbJsonExtWriter<?>> mapKlass = mapMsg.get(extClass.getName());
    if (mapKlass == null) {
      return null;
    }
    if (fieldName != null && !FIELDNAME_ALL.equals(fieldName)) {
      OpenRtbJsonExtWriter<T> writer = (OpenRtbJsonExtWriter<T>) mapKlass.get(fieldName);
      if (writer != null) {
        return writer;
      }
    }
    return (OpenRtbJsonExtWriter<T>) mapKlass.get(FIELDNAME_ALL);
  }

  /**
   * Returns the {@link JsonFactory} configured for this {@link OpenRtbJsonFactory}.
   * If you didn't set any value with {@link #setJsonFactory(JsonFactory)},
   * will create a default factory.
   */
  public final JsonFactory getJsonFactory() {
    if (jsonFactory == null) {
      jsonFactory = new JsonFactory();
    }
    return jsonFactory;
  }
}
