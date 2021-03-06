/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.TokenTypeMatcher;

import java.util.ArrayList;
import java.util.List;

public final class ParsingStackTrace {

  private final StringBuilder stackTrace = new StringBuilder();
  private final ParsingState parsingState;
  private static final int SOURCE_CODE_TOKENS_WINDOW = 30;
  private static final int SOURCE_CODE_LINE_HEADER_WIDTH = 6;

  private ParsingStackTrace(ParsingState parsingState, boolean displaySourceCode) {
    this.parsingState = parsingState;
    if (displaySourceCode) {
      displaySourceCode();
    }
    displayExpectedToken(parsingState.getOutpostMatcher());
    displayButWasToken(parsingState.getOutpostMatcherToken());
  }

  private void displaySourceCode() {
    List<Token> tokens = getTokensToDisplayAroundOutpostMatcherToken();
    stackTrace.append("------");
    int previousLine = -1;
    StringBuilder lineBuilder = new StringBuilder();
    for (Token token : tokens) {
      int currentLine = token.getLine();
      if (currentLine != previousLine) {
        stackTrace.append(lineBuilder.toString() + "\n");
        lineBuilder = new StringBuilder();
        previousLine = currentLine;
        displaySourceCodeLineHeader(lineBuilder, token, parsingState.getOutpostMatcherTokenLine());
      }
      displayToken(lineBuilder, token);
    }
    stackTrace.append(lineBuilder.toString() + "\n");
    stackTrace.append("------\n");
  }

  private void displayToken(StringBuilder lineBuilder, Token token) {
    while (lineBuilder.length() - SOURCE_CODE_LINE_HEADER_WIDTH < token.getColumn()) {
      lineBuilder.append(" ");
    }
    lineBuilder.append(token.getValue());
  }

  private void displaySourceCodeLineHeader(StringBuilder lineBuilder, Token firstTokenInLine, int parsingErrorLine) {
    if (parsingErrorLine != firstTokenInLine.getLine()) {
      String line = Integer.toString(firstTokenInLine.getLine());
      for (int i = 0; i < SOURCE_CODE_LINE_HEADER_WIDTH - line.length() - 1; i++) {
        lineBuilder.append(" ");
      }
      lineBuilder.append(line);
      lineBuilder.append(" ");
    } else {
      lineBuilder.append("-->   ");
    }
  }

  private List<Token> getTokensToDisplayAroundOutpostMatcherToken() {
    List<Token> tokens = new ArrayList<Token>();
    int outpostMatcherTokenIndex = parsingState.getOutpostMatcherTokenIndex();
    for (int i = outpostMatcherTokenIndex - SOURCE_CODE_TOKENS_WINDOW; i <= outpostMatcherTokenIndex + SOURCE_CODE_TOKENS_WINDOW; i++) {
      if (i < 0 || i > parsingState.lexerSize - 1) {
        continue;
      }
      tokens.add(parsingState.readToken(i));
    }
    return tokens;
  }

  private void displayExpectedToken(Matcher matcher) {
    stackTrace.append("Expected : <");
    stackTrace.append(matcher.toString());
    if (matcher instanceof TokenTypeMatcher) {
      stackTrace.append(" type");
    }
    stackTrace.append(">");
  }

  private void displayButWasToken(Token token) {

    stackTrace.append(" but was : <");
    if (token != null) {
      stackTrace.append(token.getValue());
      stackTrace.append(" [" + token.getType() + "]>");
      stackTrace.append(" (");
      if (token.isCopyBook()) {
        stackTrace.append("copy book ");
      }
      stackTrace.append("'" + token.getURI() + "':");
      stackTrace.append(" Line " + token.getLine() + " /");
      stackTrace.append(" Column " + token.getColumn());
      if (token.isCopyBook()) {
        stackTrace.append(" called from file ");
        stackTrace.append("'" + token.getCopyBookOriginalFileName() + "':");
        stackTrace.append(" Line " + token.getCopyBookOriginalLine());
      }
      stackTrace.append(")");
    } else {
      stackTrace.append("EOF>");
      if (parsingState.lexerSize > 0) {
        Token lastToken = parsingState.peekToken(parsingState.lexerSize - 1, null);
        stackTrace.append(" ('" + lastToken.getURI() + "')");
      }
    }
    stackTrace.append("\n");
  }

  public static String generate(ParsingState state) {
    ParsingStackTrace stackTrace = new ParsingStackTrace(state, false);
    return stackTrace.toString();
  }

  @Override
  public String toString() {
    return stackTrace.toString();
  }

  public static String generateFullStackTrace(ParsingState state) {
    if (state.getOutpostMatcher() == null) {
      return "";
    }
    ParsingStackTrace stackTrace = new ParsingStackTrace(state, true);
    return stackTrace.toString();
  }

}
