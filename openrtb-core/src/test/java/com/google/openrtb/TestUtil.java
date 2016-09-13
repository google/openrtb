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

package com.google.openrtb;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * General test utilities.
 */
public class TestUtil {
  private static final class SomethingElse {}
  /** Guaranteed to fail instanceof or isAssignableFrom() test for any other type in the heap. */
  public static Object SOMETHING_ELSE = new SomethingElse();

  /**
   * Tests the all-common and important equals(), hashCode(), toString(), and compareTo() methods.
   *
   * @param single Some object.
   * @return a map that contains <code>{ single : "0" }</code>.
   */
  @SuppressWarnings("unchecked")
  public static <T> Map<T, String> testCommonMethods(T single) {
    Map<T, String> hash = new HashMap<>();
    hash.put(single, "0");
    assertThat(hash.get(single)).isEqualTo("0");
    assertThat(single.toString()).isNotNull();

    // Testing equals(), do not use is[Not]EqualTo()!
    assertThat(single.equals(single)).isTrue();
    assertThat(single.equals(null)).isFalse();

    if (single instanceof Comparable<?>) {
      @SuppressWarnings("rawtypes")
      Comparable comp = (Comparable) single;
      assertThat(comp.compareTo(comp)).isEqualTo(0);
    }
    return hash;
  }

  /**
   * Tests the all-common and important equals(), hashCode(), toString(), and compareTo() methods.
   *
   * @param different1 Some object, optionally Comparable.
   * @param different2 Some independent object that's not equal to different1. If the objects
   *     are Comparable, you should provide different1 < different2.
   * @return a map that contains <code>{ different1 : "0", different2 : "1" }</code>.
   */
  @SuppressWarnings("unchecked")
  public static <T> Map<T, String> testCommonMethods(T different1, T different2) {
    // Testing equals(), do not use is[Not]EqualTo()!
    assertThat(different1.equals(different1)).isTrue();
    assertThat(different1.equals(SOMETHING_ELSE)).isFalse();
    assertThat(different1.equals(different2)).isFalse();

    assertThat(different1.toString()).isNotNull();

    Map<T, String> hash = testCommonMethods(different1);
    hash.put(different1, "0"); // replaces
    hash.put(different2, "1"); // adds
    assertThat(hash).hasSize(2);
    assertThat(hash.get(different1)).isEqualTo("0"); // still there
    assertThat(hash.get(different2)).isEqualTo("1");

    if (different1 instanceof Comparable<?>) {
      @SuppressWarnings("rawtypes")
      Comparable compSmaller = (Comparable) different1;
      @SuppressWarnings("rawtypes")
      Comparable compBigger = (Comparable) different2;

      assertThat(compSmaller.compareTo(compBigger)).isLessThan(0);
    }

    return hash;
  }

  /**
   * Tests the all-common and important equals(), hashCode(), toString(), and compareTo() methods.
   *
   * @param equal1 Some object, optionally Comparable.
   * @param equal2 Some independent object that's equal to equal1
   * @param different Some independent object that's not equal to equal1 or equal2. If the objects
   *     are Comparable, you should provide equals1 < different.
   */
  @SuppressWarnings("unchecked")
  public static <T> Map<T, String> testCommonMethods(T equal1, T equal2, T different) {
    // Testing equals(), do not use is[Not]EqualTo()!
    assertThat(equal1.equals(equal2)).isTrue();

    assertThat(equal2.hashCode()).isEqualTo(equal1.hashCode());
    Map<T, String> hash = testCommonMethods(equal1, different);
    hash.put(equal2, "2");
    assertThat(hash.get(equal1)).isEqualTo("2");
    assertThat(hash.get(equal2)).isEqualTo("2");
    assertThat(hash.get(different)).isEqualTo("1");

    if (equal1 instanceof Comparable<?>) {
      @SuppressWarnings("rawtypes")
      Comparable compEqual1 = (Comparable) equal1;
      @SuppressWarnings("rawtypes")
      Comparable compEqual2 = (Comparable) equal2;

      assertThat(compEqual1.compareTo(compEqual2)).isEqualTo(0);
    }

    return hash;
  }

  public static <E extends Exception> void testCommonException(Class<E> klass) {
    try {
      String msg = "junk";
      Exception cause = new Exception();

      try {
        Constructor<E> constr1 = klass.getConstructor();
        E e = constr1.newInstance();
        testCommonException(e);
      } catch (NoSuchMethodException e) { }

      try {
        Constructor<E> constr2 = klass.getConstructor(String.class);
        testParameterAnnotations(constr2);
        E e = constr2.newInstance(msg);
        testCommonException(e);
        assertThat(e.getMessage()).isEqualTo(msg);
      } catch (NoSuchMethodException e) { }

      try {
        Constructor<E> constr3 = klass.getConstructor(Throwable.class);
        testParameterAnnotations(constr3);
        E e = constr3.newInstance(cause);
        testCommonException(e);
        assertThat(e.getCause()).isEqualTo(cause);
      } catch (NoSuchMethodException e) { }

      try {
        Constructor<E> constr4 = klass.getConstructor(String.class, Throwable.class);
        testParameterAnnotations(constr4);
        E e = constr4.newInstance(msg, cause);
        testCommonException(e);
        assertThat(e.getMessage()).isEqualTo(msg);
        assertThat(e.getCause()).isEqualTo(cause);
      } catch (NoSuchMethodException e) { }
    } catch (ReflectiveOperationException e) {
      fail(e.getMessage());
    }
  }

  public static void testCommonException(Exception e) {
    try {
      e.toString();
      throw e;
    } catch (Exception ee) {
      assertThat(e).isSameAs(ee);
    }
  }

  public static void testCommonEnum(Enum<?>[] e) {
    for (int i = 1; i < e.length; ++i) {
      testCommonMethods(e[i - 1], e[i]);
    }
  }

  private static <T extends Throwable> void testParameterAnnotations(Constructor<T> constr) {
    Annotation[][] anns = constr.getParameterAnnotations();

    for (Annotation[] ann : anns) {
      assertThat(ann.length == 1 && ann[0] instanceof Nullable).isTrue();
    }
  }
}
