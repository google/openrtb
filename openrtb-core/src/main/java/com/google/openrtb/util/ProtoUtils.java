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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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

  @SuppressWarnings("unchecked")
  public static <I extends MessageLiteOrBuilder, O extends MessageLite> O built(I msg) {
    return msg instanceof MessageLite.Builder
        ? (O) ((MessageLite.Builder) msg).build()
        : (O) msg;
  }

  @SuppressWarnings("unchecked")
  public static <I extends MessageLiteOrBuilder, O extends MessageLite.Builder> O builder(I msg) {
    return msg instanceof MessageLite
        ? (O) ((MessageLite) msg).toBuilder()
        : (O) msg;
  }

  /**
   * Executes an Updater on every builder from a sequence.
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
   * Runs a filter through a list of objects. If at least one element doesn't pass the filter,
   * returns a new list that contains only the elements that pass the filter.
   * If all elements pass the filter, returns the same, unmodified input list.
   */
  public static <M extends MessageLiteOrBuilder>
  List<M> filter(List<M> objs, Predicate<M> filter) {
    for (ListIterator<M> iter = objs.listIterator(); iter.hasNext(); ) {
      if (!filter.apply(iter.next())) {
        List<M> filtered = new ArrayList<>(objs.size() - 1);
        filtered.addAll(objs.subList(0, iter.previousIndex()));

        while (iter.hasNext()) {
          M obj = iter.next();

          if (filter.apply(obj)) {
            filtered.add(obj);
          }
        }

        return filtered;
      }
    }

    return objs;
  }

  /**
   * Returns a copy of a {@link Message} that contains only fields that pass a predicate.
   */
  public static <M extends Message> M filter(
      M msg, boolean clearEmpty, Predicate<FieldDescriptor> predicate) {
    Message.Builder builder = msg.newBuilderForType();

    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor fd = entry.getKey();

      if (predicate.apply(fd)) {
        final Object value = entry.getValue();

        if (fd.getType() == FieldDescriptor.Type.MESSAGE) {
          if (fd.isRepeated()) {
            for (Object obj : ((Iterable<?>) value)) {
              Message child = filter((Message) obj, clearEmpty, predicate);
              if (child != null) {
                builder.addRepeatedField(fd, child);
              }
            }
          } else {
            Message child = filter((Message) value, clearEmpty, predicate);
            if (child != null) {
              builder.setField(fd, child);
            }
          }
        } else {
          builder.setField(fd, value);
        }
      }
    }

    if (clearEmpty && builder.getAllFields().isEmpty()) {
      return null;
    } else {
      @SuppressWarnings("unchecked")
      M ret = (M) builder.build();
      return ret;
    }
  }
}
