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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports preprocessing for "snippets" of textual information, in particular for
 * various String fields from the response {@link Bid}s such as the ad markup, URLs and IDs.
 * Additionally, you can use the syntax %{...}% for URL encoding. Nesting can be used, e.g.
 * %{A%{B}%}% will encode A and doubly-encode B. This nesting is typically necessary when URLs
 * have parameter that contain other URLs, so each server decodes and redirects to the next URL.
 *
 * <p>This class is threadsafe, and all concrete subclasses have to be too.
 */
public abstract class SnippetProcessor {
  private static final Logger logger = LoggerFactory.getLogger(SnippetProcessor.class);
  private static final Escaper ESCAPER = new PercentEscaper("-_.*", true);

  public static final SnippetProcessor NULL = new SnippetProcessor() {
    @Override public String process(SnippetProcessorContext ctx, String snippet) {
      checkNotNull(ctx);
      return checkNotNull(snippet);
    }

    @Override protected boolean processMacroAt(
        SnippetProcessorContext ctx, SnippetMacroType macroDef) {
      return false;
    }
  };

  private final ImmutableList<SnippetMacroType> scanMacros;

  public SnippetProcessor() {
    List<SnippetMacroType> registered = registerMacros();
    SnippetMacroType[] macros = registered.toArray(new SnippetMacroType[registered.size()]);
    Arrays.sort(macros, new Comparator<SnippetMacroType>() {
      @Override public int compare(SnippetMacroType o1, SnippetMacroType o2) {
        return o1.key().compareTo(o2.key());
      }});
    this.scanMacros = ImmutableList.copyOf(macros);
  }

  protected List<SnippetMacroType> registerMacros() {
    return ImmutableList.of();
  }

  public static Escaper getEscaper() {
    return ESCAPER;
  }

  /**
   * Processes the raw snippet that was set by the bid, making any transformations necessary.
   */
  public String process(SnippetProcessorContext ctx, String snippet) {
    checkNotNull(ctx);
    StringBuilder sb = ctx.builder();
    sb.setLength(0);
    String currSnippet = snippet;

    boolean processedMacros = false;
    int snippetPos = 0;
    int macroPos = currSnippet.indexOf("${");

    while (macroPos != -1) {
      sb.append(currSnippet.substring(snippetPos, macroPos));
      int macroEnd = processMacroAt(ctx, currSnippet, macroPos);

      if (macroEnd == -1) {
        sb.append("${");
        snippetPos = macroPos + 2;
      } else {
        snippetPos = macroEnd;
        processedMacros = true;
      }

      macroPos = currSnippet.indexOf("${", snippetPos);
    }

    if (processedMacros) {
      sb.append(currSnippet, snippetPos, currSnippet.length());
      currSnippet = sb.toString();
    }
    sb.setLength(0);

    String ret = urlEncode(ctx, currSnippet);
    sb.setLength(0);
    return ret;
  }

  private int processMacroAt(SnippetProcessorContext ctx,
      String snippet, int macroStart) {
    SnippetMacroType macroDef = match(snippet, macroStart);
    if (macroDef == null) {
      return -1;
    }

    processMacroAt(ctx, macroDef);

    // Handle recursive macros
    StringBuilder sb = ctx.builder();
    int macroPos = sb.indexOf("${");
    if (macroPos != -1) {
      String recSnippet = sb.substring(macroPos);
      // Avoid infinite recursion if the macro expands to itself!
      if (!macroDef.key().equals(recSnippet)) {
        String recReplaced = process(ctx.rec(), recSnippet);
        if (recReplaced != recSnippet) {
          sb.setLength(macroPos);
          sb.append(recReplaced);
        }
      }
    }

    return macroStart + macroDef.key().length();
  }

  private SnippetMacroType match(String snippet, int macroStart) {
    for (SnippetMacroType macroDef : scanMacros) {
      if (macroDef.key().regionMatches(0, snippet, macroStart, macroDef.key().length())) {
        return macroDef;
      }
    }
    return null;
  }

  protected abstract boolean processMacroAt(
      SnippetProcessorContext ctx, SnippetMacroType macroDef);

  protected static String urlEncode(SnippetProcessorContext ctx, String snippet) {
    int snippetPos = snippet.indexOf("%{");
    if (snippetPos == -1) {
      return snippet;
    }

    StringBuilder sb = ctx.builder();
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
      logger.warn("Unbalanced '%{': level={}, pos={}", encodeLevel, snippetPos);
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

  @Override public final String toString() {
    return toStringHelper().toString();
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).omitNullValues();
  }
}
