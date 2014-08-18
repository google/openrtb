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

package com.google.openrtb.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Some conveniences for protoc-generated classes.
 */
public final class ProtoUtils {
  public static final Predicate<FieldDescriptor> NOT_EXTENSION = new Predicate<FieldDescriptor>() {
    @Override public boolean apply(FieldDescriptor fd) {
      assert fd != null;
      return !fd.isExtension();
    }};

  private ProtoUtils() {
  }

  /**
   * Given a message-or-builder, returns a message, invoking the builder if necessary.
   */
  @SuppressWarnings("unchecked")
  public static <I extends MessageLiteOrBuilder, O extends MessageLite> O built(I msg) {
    return msg instanceof MessageLite.Builder
        ? (O) ((MessageLite.Builder) msg).build()
        : (O) msg;
  }

  /**
   * Given a message-or-builder, return a builder, invoking toBuilder() if necessary.
   */
  @SuppressWarnings("unchecked")
  public static <I extends MessageLiteOrBuilder, O extends MessageLite.Builder> O builder(I msg) {
    return msg instanceof MessageLite
        ? (O) ((MessageLite) msg).toBuilder()
        : (O) msg;
  }

  /**
   * Updates every builder from a sequence.
   *
   * @param objs List of builders to update
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   * object, and it's expected to return {@code true} if some update was made
   * @return {@code true} if at least one object was updated
   */
  public static <B extends MessageLite.Builder> boolean update(
      Iterable<B> objs, Function<B, Boolean> updater) {
    checkNotNull(updater);
    boolean updated = false;

    for (B obj : objs) {
      updated |= updater.apply(obj);
    }
    return updated;
  }

  /**
   * Runs a filter through a sequence of objects.
   *
   * @param objs Message-or-builder objects
   * @param filter Filter function, will return {@code true} for objects that are to be
   * retained in the sequence, {@code false} for objects to discard
   * @return Retained objects. If at least one element doesn't pass the filter,
   * this will be a new {@link List} that contains only the elements that pass the filter.
   * If all elements pass the filter, returns the same, unmodified input sequence
   */
  public static <M extends MessageLiteOrBuilder>
  Iterable<M> filter(Iterable<M> objs, Predicate<M> filter) {

    int i = 0;
    for (M obj : objs) {
      if (!filter.apply(obj)) {
        // At least one discarded object, go to slow-path.
        return filterFrom(objs, filter, i);
      }
      ++i;
    }

    // Optimized common case: all items filtered, return the input sequence.
    return objs;
  }

  private static <M extends MessageLiteOrBuilder> List<M> filterFrom(
      Iterable<M> objs, Predicate<M> filter, int firstDiscarded) {
    List<M> filtered = (objs instanceof Collection)
        ? new ArrayList<M>(((Collection<?>) objs).size() - 1)
        : new ArrayList<M>();

    Iterator<M> iter = objs.iterator();
    for (int i = 0; i < firstDiscarded; ++i) {
      filtered.add(iter.next());
    }

    iter.next(); // Ignore object at firstDiscarded position

    while (iter.hasNext()) {
      M obj = iter.next();

      if (filter.apply(obj)) {
        filtered.add(obj);
      }
    }

    return filtered;
  }

  /**
   * Returns a copy of a {@link Message} that contains only fields that pass a predicate.
   *
   * @param msg Message object
   * @param clearEmpty {@code true} will cause {@code null} to be returned if all fields from
   * {@code msg} are removed; {@code false} will return an "empty" message in that case
   * @param predicate Will be applied to every field from {@code msg}, returning {@code true}
   * for fields that are to be retained, {@code false} to discard the field
   * @return Message object that contains only the filtered fields from {@code msg}.
   * If all fields are filtered (none discarded), returns the same {@code msg} object.
   * If no fields are filtered (all discarded), returns {@code null} or a default instance
   * of {@code msg}'s message type depending on the value of {@code clearEmpty}
   */
  public static @Nullable <M extends Message> M filter(
      M msg, boolean clearEmpty, Predicate<FieldDescriptor> predicate) {

    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = entry.getKey();

      if (!predicate.apply(fd)) {
        // At least one discarded field, go to slow-path.
        return filterFrom(msg, clearEmpty, predicate, fd);
      }
    }

    // Optimized common case: all items filtered, return the input sequence.
    return msg;
  }

  private static @Nullable <M extends Message> M filterFrom(
      M msg, boolean clearEmpty, Predicate<FieldDescriptor> predicate,
      FieldDescriptor firstDiscarded) {

    // At least one field was discarded; we have to work harder and maybe create
    // a new message that will contain only the retained filters. Ude a lazy-allocated
    // builder to also optimize the scenario of all fields being discarded.

    Message.Builder builder = null;
    boolean prefiltered = true;

    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = entry.getKey();
      boolean filtered;

      if (prefiltered) {
        if (fd == firstDiscarded) {
          filtered = false;
          prefiltered = false;
        } else {
          filtered = true;
        }
      } else {
        filtered = predicate.apply(fd);
      }

      if (filtered) {
        Object value = entry.getValue();

        if (fd.getType() == FieldDescriptor.Type.MESSAGE) {
          if (fd.isRepeated()) {
            for (Object obj : ((Iterable<?>) value)) {
              Message child = filter((Message) obj, clearEmpty, predicate);
              if (child != null) {
                builder = builder == null ? msg.newBuilderForType() : builder;
                builder.addRepeatedField(fd, child);
              }
            }
          } else {
            Message child = filter((Message) value, clearEmpty, predicate);
            if (child != null) {
              builder = builder == null ? msg.newBuilderForType() : builder;
              builder.setField(fd, child);
            }
          }
        } else {
          builder = builder == null ? msg.newBuilderForType() : builder;
          builder.setField(fd, value);
        }
      }
    }

    if (builder == null) {
      if (clearEmpty) {
        return null;
      } else {
        @SuppressWarnings("unchecked")
        M ret = (M) msg.getDefaultInstanceForType();
        return ret;
      }
    } else {
      @SuppressWarnings("unchecked")
      M ret = (M) builder.build();
      return ret;
    }
  }
}
