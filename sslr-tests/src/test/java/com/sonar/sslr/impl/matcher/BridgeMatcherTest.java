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

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.IdentifierLexer;
import org.junit.Test;

import java.util.List;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.bridge;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.MyPunctuator.CAT;
import static com.sonar.sslr.impl.matcher.MyPunctuator.DOG;
import static com.sonar.sslr.impl.matcher.MyPunctuator.LEFT;
import static com.sonar.sslr.impl.matcher.MyPunctuator.RIGHT;
import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BridgeMatcherTest {

  @Test
  public void shouldMatchSimpleBridge() {
    assertThat(bridge(LEFT, RIGHT), match(createTokens(LEFT, CAT, CAT, DOG, RIGHT)));
  }

  @Test
  public void shouldMatchCompositeBridges() {
    assertThat(bridge(LEFT, RIGHT), match(createTokens(LEFT, LEFT, CAT, LEFT, RIGHT, DOG, RIGHT, RIGHT)));
  }

  @Test
  public void shouldNotMatchBridgeStarter() {
    assertThat(bridge(LEFT, RIGHT), not(match(createTokens(CAT, LEFT, RIGHT))));
  }

  @Test
  public void shouldNotMatchPartialBridge() {
    assertThat(bridge(LEFT, RIGHT), not(match(createTokens(LEFT, LEFT, RIGHT))));
  }

  private static List<Token> createTokens(TokenType... types) {
    List<Token> tokens = Lists.newArrayList();
    for (TokenType type : types) {
      tokens.add(mockToken(type, type.getValue()));
    }
    return tokens;
  }

  @Test
  public void testToString() {
    assertThat(bridge(LEFT, RIGHT).toString()).isEqualTo("bridge(LEFT, RIGHT)");
  }

  @Test
  public void testAstNodeTokens() {
    ParsingState state = new ParsingState(IdentifierLexer.create().lex("one "));
    AstNode astNode = bridge(GenericTokenType.IDENTIFIER, GenericTokenType.EOF).match(state);
    assertThat(state.lexerIndex).isEqualTo(2);
    assertThat(astNode.getChildren().size()).isEqualTo(2);
  }

  @Test
  public void test_toString() {
    TokenType from = mock(TokenType.class);
    when(from.getName()).thenReturn("from");
    TokenType to = mock(TokenType.class);
    when(to.getName()).thenReturn("to");
    assertThat(new BridgeMatcher(from, to).toString()).isEqualTo("bridge(from, to)");
  }

  @Test
  public void test_equals_and_hashCode() {
    TokenType from = mock(TokenType.class);
    TokenType to = mock(TokenType.class);
    Matcher first = new BridgeMatcher(from, to);
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same types
    Matcher second = new BridgeMatcher(from, to);
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different types
    Matcher third = new BridgeMatcher(from, mock(TokenType.class));
    assertThat(first.equals(third)).isFalse();
    Matcher fourth = new BridgeMatcher(mock(TokenType.class), mock(TokenType.class));
    assertThat(first.equals(fourth)).isFalse();
  }

}
