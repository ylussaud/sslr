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
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;

public class AutoCompleterTest {

  @Test
  public void testCaseEmpty() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        opt(and("hello"))
        );

    assertMatches(auto, new String[][] { {} }, new String[][] {});
  }

  @Test
  public void testCase1() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and("hello")
        );

    assertMatches(auto, new String[][] { { "hello" } }, new String[][] {});
  }

  @Test
  public void testCase2() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(
            "hello",
            opt("buddy"),
            or(
                "Olivier",
                "Freddy"
            )
        )
        );

    assertMatches(auto, new String[][] { { "hello", "Olivier" }, { "hello", "Freddy" }, { "hello", "buddy", "Olivier" },
        { "hello", "buddy", "Freddy" } }, new String[][] {});
  }

  @Test
  public void testCase3() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(
            one2n("hello"),
            "world"
        )
        );

    assertMatches(auto, new String[][] { { "hello", "world" }, { "hello", "hello", "world" }, { "hello", "hello", "hello", "world" },
        { "hello", "hello", "hello", "hello", "world" } }, new String[][] { { "hello", "hello", "hello", "hello", "hello" } });
  }

  @Test
  public void testCase4() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(
            "hi",
            o2n("folks")
        )
        );

    assertMatches(auto, new String[][] { { "hi" } }, new String[][] {});
  }

  @Test
  public void testCase5() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(
            "hi",
            opt("folks")
        )
        );

    assertMatches(auto, new String[][] { { "hi" } }, new String[][] {});
  }

  @Test
  public void testCase6() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(
            o2n("hello"),
            "hello"
        )
        );

    assertMatches(auto, new String[][] {}, new String[][] { { "hello", "hello", "hello", "hello", "hello" } });
  }

  @Test
  public void testCase7() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        or(
            "fail",
            and("fail", "ure")
        )
        );

    assertMatches(auto, new String[][] { { "fail" } }, new String[][] {});
  }

  @Test
  public void testPredicateCase1() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("hello"), "world")
        );

    assertMatches(auto, new String[][] { { "world" } }, new String[][] {});
  }

  @Test
  public void testPredicateCase2() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("hello", "world")), "foo")
        );

    assertMatches(auto, new String[][] { { "foo" } }, new String[][] {});
  }

  @Test
  public void testCaseTokenValueWithDouleQuotes() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and("fail\"")
        );

    assertMatches(auto, new String[][] { { "fail\"" } }, new String[][] {});
  }

  private enum MyTokenType implements TokenType {
    ADD("+"), SUB("-");

    private final String value;

    private MyTokenType(String value) {
      this.value = value;
    }

    public String getName() {
      return name();
    }

    public String getValue() {
      return value;
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

  }

  @Test
  public void testCaseTokenType() {
    AutoCompleter auto = new AutoCompleter();

    auto.autoComplete(
        and(
            or(
                MyTokenType.ADD,
                MyTokenType.SUB
            ),
            "number"
        )
        );

    assertMatches(auto, new String[][] { { "+", "number" }, { "-", "number" } }, new String[][] {});
  }

  @Test
  public void testCasePartial1() {
    AutoCompleter auto = new AutoCompleter();

    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(mockToken(GenericTokenType.LITERAL, "token1"));
    tokens.add(mockToken(GenericTokenType.LITERAL, "token2"));
    tokens.add(mockToken(GenericTokenType.LITERAL, "token4"));

    auto.autoComplete(
        and(
            "token1",
            "token2",
            or(
                "token3",
                "token4"
            ),
            opt("detect")
        ),
        tokens
        );

    assertMatches(auto, new String[][] { { "token1", "token2", "token4" } }, new String[][] {});
  }

  @Test
  public void testCasePartial2() {
    AutoCompleter auto = new AutoCompleter();

    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(mockToken(GenericTokenType.LITERAL, "token1"));
    tokens.add(mockToken(GenericTokenType.LITERAL, "token2"));

    auto.autoComplete(
        and(
            "token1",
            "token2",
            or(
                "token3",
                "token4"
            ),
            opt("detect")
        ),
        tokens
        );

    assertMatches(auto, new String[][] { { "token1", "token2", "token3" }, { "token1", "token2", "token4" } }, new String[][] {});
  }

  @Test
  public void testCasePartial3() {
    AutoCompleter auto = new AutoCompleter();

    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(mockToken(GenericTokenType.LITERAL, "token1"));
    tokens.add(mockToken(GenericTokenType.LITERAL, "token2"));

    auto.autoComplete(
        and(
            "token1",
            "token2",
            or(
                "token3",
                "token4"
            ),
            one2n("detect")
        ),
        tokens
        );

    assertMatches(auto, new String[][] { { "token1", "token2", "token3", "detect" }, { "token1", "token2", "token4", "detect" } },
        new String[][] {});
  }

  private void assertMatches(AutoCompleter auto, String[][] fullMatches, String[][] partialMatches) {
    assertThat(auto.getFullMatches().size()).isEqualTo(fullMatches.length);
    assertThat(auto.getPartialMatches().size()).isEqualTo(partialMatches.length);

    /* Compare the full matches */
    for (List<Token> list : auto.getFullMatches()) {
      boolean found = false;
      for (String[] fullMatch : fullMatches) {
        if (fullMatch.length == list.size()) {
          /* Compare token by token */
          int i;
          for (i = 0; i < fullMatch.length; i++) {
            if ( !fullMatch[i].equals(list.get(i).getValue())) {
              break;
            }
          }

          if (i == fullMatch.length) {
            found = true;
            break;
          }
        }
      }

      if ( !found) {
        StringBuilder errorMessage = new StringBuilder(System.getProperty("line.separator"));
        errorMessage.append("Expected a full match corresponding to:");
        errorMessage.append(System.getProperty("line.separator"));
        errorMessage.append('\t');

        for (Token token : list) {
          errorMessage.append(token.getValue());
          errorMessage.append(" ");
        }

        throw new AssertionError(errorMessage.toString());
      }
    }

    /* Compare the partial matches */
    for (List<Token> list : auto.getPartialMatches()) {
      boolean found = false;
      for (String[] partialMatch : partialMatches) {
        if (partialMatch.length == list.size()) {
          /* Compare token by token */
          int i;
          for (i = 0; i < partialMatch.length; i++) {
            if ( !partialMatch[i].equals(list.get(i).getValue())) {
              break;
            }
          }

          if (i == partialMatch.length) {
            found = true;
            break;
          }
        }
      }

      if ( !found) {
        StringBuilder errorMessage = new StringBuilder(System.getProperty("line.separator"));
        errorMessage.append("Expected a partial match corresponding to:");
        errorMessage.append(System.getProperty("line.separator"));
        errorMessage.append('\t');

        for (Token token : list) {
          errorMessage.append(token.getValue());
          errorMessage.append(" ");
        }

        throw new AssertionError(errorMessage.toString());
      }
    }
  }

}
