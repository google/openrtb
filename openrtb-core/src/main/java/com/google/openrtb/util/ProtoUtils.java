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

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

/**
 * Some conveniences for protoc-generated classes.
 */
public final class ProtoUtils {
  public static final Predicate<FieldDescriptor> NOT_EXTENSION = fd -> !fd.isExtension();

  private ProtoUtils() {
  }

  /**
   * Given a message-or-builder, returns a message, invoking the builder if necessary.
   */
  @SuppressWarnings("unchecked")
  public static <I extends MessageLiteOrBuilder, O extends MessageLite> O built(@Nullable I msg) {
    return msg instanceof MessageLite.Builder
        ? (O) ((MessageLite.Builder) msg).build()
        : (O) msg;
  }

  /**
   * Given a message-or-builder, return a builder, invoking toBuilder() if necessary.
   */
  @SuppressWarnings("unchecked")
  public static <I extends MessageLiteOrBuilder, O extends MessageLite.Builder> O builder(
      @Nullable I msg) {
    return msg instanceof MessageLite
        ? (O) ((MessageLite) msg).toBuilder()
        : (O) msg;
  }

  /**
   * Updates every builder from a sequence.
   *
   * @param objs List of builders to update
   * @param updater Update function. The {@code apply()} method can decide or not to update each
   *     object, and it's expected to return {@code true} if some update was made
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
   * @param filter Function that returns {@code true} to retain an object, {@code false} to discard
   * @return Retained objects. If some elements are retained and others are discarded,
   *     this will be a new, mutable {@link List} that contains only the retained elements.
   *     If all elements are retained, returns the same, unmodified input sequence.
   *     If all elements are discarded, returns an immutable, empty sequence
   */
  public static <M extends MessageLiteOrBuilder>
      List<M> filter(List<M> objs, Predicate<M> filter) {
    checkNotNull(filter);

    for (int i = 0; i < objs.size(); ++i) {
      if (!filter.test(objs.get(i))) {
        // At least one discarded object, go to slow-path.
        return filterFrom(objs, filter, i);
      }
    }

    // Optimized common case: all items filtered, return the input sequence.
    return objs;
  }

  private static <M extends MessageLiteOrBuilder> List<M> filterFrom(
      List<M> objs, Predicate<M> filter, int firstDiscarded) {
    List<M> filtered;

    if (firstDiscarded == 0) {
      filtered = null;
    } else {
      filtered = new ArrayList<>(objs.size() - 1);
      for (int i = 0; i < firstDiscarded; ++i) {
        filtered.add(objs.get(i));
      }
    }

    for (int i = firstDiscarded + 1; i < objs.size(); ++i) {
      M obj = objs.get(i);

      if (filter.test(obj)) {
        if (filtered == null) {
          filtered = new ArrayList<>(objs.size() - i);
        }
        filtered.add(obj);
      }
    }

    return filtered == null ? ImmutableList.<M>of() : filtered;
  }

  /**
   * Returns a copy of a {@link Message} that contains only fields that pass a filter.
   * This will be executed recursively for fields which are child messages.
   *
   * @param msg Message object
   * @param clearEmpty {@code true} will cause {@code null} to be returned if all fields from
   * {@code msg} are removed; {@code false} will return an "empty" message in that case
   * @param filter Function that returns {@code true} to retain a field, {@code false} to discard
   * @return Message with the retained fieldsfrom {@code msg}.
   *     If some fields are retained and others discarded, returns a new message object.
   *     If all fields are retained, returns the same {@code msg} object.
   *     If all fields are discarded, returns {@code null} if {@code clearEmpty==true}
   *     or a default instance of {@code msg}'s message type if {@code clearEmpty==false}
   */
  @Nullable public static <M extends Message> M filter(
      M msg, boolean clearEmpty, Predicate<FieldDescriptor> filter) {
    checkNotNull(filter);

    int i = 0;
    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = entry.getKey();

      if (!filter.test(fd)) {
        // At least one field discarded, go to slow-path.
        return filterFrom(msg, clearEmpty, filter, i, true);
      } else if (fd.getType() == FieldDescriptor.Type.MESSAGE) {
        // At least one field may have children, go to slow-path.
        return filterFrom(msg, clearEmpty, filter, i, false);
      }

      ++i;
    }

    // Optimized common case: all items filtered, return the input sequence.
    return msg;
  }

  @Nullable private static <M extends Message> M filterFrom(
      M msg, boolean clearEmpty, Predicate<FieldDescriptor> filter, int first, boolean discard) {

    // At least one field may be discarded; we have to work harder and maybe create
    // a new message that will contain only the retained filters. Use a lazy-allocated
    // builder to also optimize the scenario of all fields being discarded.

    Message.Builder builder = first == 0 ? null : msg.newBuilderForType();
    Iterator<Map.Entry<FieldDescriptor, Object>> iter = msg.getAllFields().entrySet().iterator();

    for (int i = 0; i < first; ++i) {
      filterValue(clearEmpty, filter, builder, iter.next());
    }

    if (discard) {
      iter.next();
    }

    boolean updated = discard;
    while (iter.hasNext()) {
      Map.Entry<FieldDescriptor, Object> entry = iter.next();

      if (filter.test(entry.getKey())) {
        if (builder == null) {
          builder = msg.newBuilderForType();
        }
        updated |= filterValue(clearEmpty, filter, builder, entry);
      } else {
        updated = true;
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
    } else if (updated) {
      @SuppressWarnings("unchecked")
      M ret = (M) builder.build();
      return ret;
    } else {
      return msg;
    }
  }

  protected static boolean filterValue(boolean clearEmpty, Predicate<FieldDescriptor> filter,
      Message.Builder builder, Map.Entry<FieldDescriptor, Object> entry) {
    FieldDescriptor fd = entry.getKey();
    Object value = entry.getValue();
    boolean updated = false;

    if (fd.getType() == FieldDescriptor.Type.MESSAGE) {
      if (fd.isRepeated()) {
        for (Object obj : ((Iterable<?>) value)) {
          Message child = filter((Message) obj, clearEmpty, filter);
          if (child == null) {
            updated = true;
          } else {
            updated = child != obj;
            builder.addRepeatedField(fd, child);
          }
        }
      } else {
        Message child = filter((Message) value, clearEmpty, filter);
        if (child == null) {
          updated = true;
        } else {
          updated = child != value;
          builder.setField(fd, child);
        }
      }
    } else {
      builder.setField(fd, value);
    }

    return updated;
  }
}
