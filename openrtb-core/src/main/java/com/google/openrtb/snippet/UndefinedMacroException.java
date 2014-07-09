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

package com.google.openrtb.snippet;

import javax.annotation.Nullable;

/**
 * A snippet macro cannot be used, because its value was not defined correctly.
 */
public class UndefinedMacroException extends RuntimeException {
  private final SnippetMacroType key;

  public UndefinedMacroException(SnippetMacroType key) {
    super("Macro " + key + " was not correctly defined and cannot be used");
    this.key = key;
  }

  public UndefinedMacroException(SnippetMacroType key, @Nullable String message) {
    super(message);
    this.key = key;
  }

  public final SnippetMacroType key() {
    return key;
  }
}
