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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

public abstract class TokenMatcher extends StandardMatcher {

  private final boolean hasToBeSkippedFromAst;

  protected TokenMatcher(boolean hasToBeSkippedFromAst) {
    this.hasToBeSkippedFromAst = hasToBeSkippedFromAst;
  }

  @Override
  protected final MatchResult doMatch(ParsingState parsingState) {
    enterEvent(parsingState);
    int startingIndex = parsingState.lexerIndex;
    Token token = parsingState.peekTokenIfExists(parsingState.lexerIndex, this);
    if (token != null && isExpectedToken(token)) {
      token = parsingState.popToken(this);
      AstNode astNode = hasToBeSkippedFromAst ? null : new AstNode(token);
      exitWithMatchEvent(parsingState, astNode);
      return MatchResult.succeed(parsingState, startingIndex, astNode);
    } else {
      exitWithoutMatchEvent(parsingState);
      return MatchResult.fail(parsingState, startingIndex);
    }
  }

  protected abstract boolean isExpectedToken(Token token);

}
