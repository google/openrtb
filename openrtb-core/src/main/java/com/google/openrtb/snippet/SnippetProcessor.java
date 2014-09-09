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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Supports preprocessing for "snippets" of textual information, in particular for
 * various String fields from the response {@link Bid}s such as the ad markup, URLs and IDs.
 * Additionally, you can use the syntax %{...}% for URL encoding. Nesting can be used, e.g.
 * %{A%{B}%}% will encode A and doubly-encode B. This nesting is typically necessary when URLs
 * have parameter that contain other URLs, so each server decodes and redirects to the next URL.
 */
public abstract class SnippetProcessor {
  private static final Logger logger = LoggerFactory.getLogger(SnippetProcessor.class);
  private static final Escaper escaper = new PercentEscaper("-_.*", true);
  public static final SnippetProcessor NULL = new SnippetProcessor() {
    @Override public String process(SnippetProcessorContext ctx, String snippet) {
      checkNotNull(ctx);
      return checkNotNull(snippet);
    }
    @Override protected void processMacroAt(
        SnippetProcessorContext ctx, StringBuilder sb, SnippetMacroType macroDef) {
    }
  };

  private final ImmutableList<SnippetMacroType> SCAN_MACROS = ImmutableList.copyOf(registerMacros());

  protected List<SnippetMacroType> registerMacros() {
    return ImmutableList.of();
  }

  public static Escaper getEscaper() {
    return escaper;
  }

  /**
   * Processes the raw snippet that was set by the bid, making any transformations necessary.
   */
  public String process(SnippetProcessorContext ctx, String snippet) {
    checkNotNull(ctx);
    StringBuilder sb = new StringBuilder(snippet.length() * 2);
    String currSnippet = snippet;

    while (true) {
      boolean processedMacros = false;
      int snippetPos = 0;

      while (snippetPos < currSnippet.length()) {
        char c = currSnippet.charAt(snippetPos);

        int macroEnd = (c == '$'
            && currSnippet.length() - snippetPos > 1 && currSnippet.charAt(snippetPos + 1) == '{')
            ? processMacroAt(ctx, currSnippet, snippetPos, sb)
            : -1;

        if (macroEnd == -1) {
          sb.append(c);
          ++snippetPos;
        } else {
          snippetPos = macroEnd;
          processedMacros = true;
        }
      }

      if (processedMacros) {
        currSnippet = sb.toString();
        sb.setLength(0);
      } else {
        sb.setLength(0);
        break;
      }
    }

    return urlEncode(currSnippet, sb);
  }

  private int processMacroAt(SnippetProcessorContext ctx,
      String snippet, int macroStart, StringBuilder sb) {
    for (SnippetMacroType macroDef : SCAN_MACROS) {
      if (macroDef.key().regionMatches(0, snippet, macroStart, macroDef.key().length())) {
        processMacroAt(ctx, sb, macroDef);
        return macroStart + macroDef.key().length();
      }
    }

    return -1;
  }

  protected abstract void processMacroAt(SnippetProcessorContext ctx,
      StringBuilder sb, SnippetMacroType macroDef);

  protected static String urlEncode(String snippet, StringBuilder sb) {
    int snippetPos = snippet.indexOf("%{");
    if (snippetPos == -1) {
      return snippet;
    }

    int encodeLevel = 0;
    int encodeStart = 0;
    int lastPos = snippet.length() - 1;

    while (snippetPos <= lastPos) {
      char c = snippet.charAt(snippetPos);

      if (c == '%' && snippetPos < lastPos && snippet.charAt(snippetPos + 1) == '{') {
        sb.append(flushEncoding(snippet, encodeStart, snippetPos, encodeLevel++));
        encodeStart = (snippetPos += 2);
      } else if (c == '}' && snippetPos < lastPos && snippet.charAt(snippetPos + 1) == '%'
          && encodeLevel > 0) {
        sb.append(flushEncoding(snippet, encodeStart, snippetPos, encodeLevel--));
        encodeStart = (snippetPos += 2);
      } else {
        ++snippetPos;
      }
    }

    if (encodeLevel != 0) {
      logger.warn("Unbalanced '%{': {}, snippet:\n{}", encodeLevel, snippet);
    }

    return sb.append(flushEncoding(snippet, encodeStart, snippet.length(), 0)).toString();
  }

  private static String flushEncoding(
      String snippet, int encodeStart, int encodeEnd, int encodeLevel) {
    String substr = snippet.substring(encodeStart, encodeEnd);

    for (int i = 0; i < encodeLevel; ++i) {
      substr = getEscaper().escape(substr);
    }

    return substr;
  }

  @Override
  public final String toString() {
    return toStringHelper().toString();
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).omitNullValues();
  }
}
