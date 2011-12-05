/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.language;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.lexer.LexerMatchers.*;
import static com.sonar.sslr.test.miniC.MiniLanguageLexer.Keywords.*;
import static com.sonar.sslr.test.miniC.MiniLanguageLexer.Literals.*;
import static com.sonar.sslr.test.miniC.MiniLanguageLexer.Punctuators.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.test.miniC.MiniLanguageLexer;

public class MiniLanguageLexerTest {

  Lexer lexer = MiniLanguageLexer.getLexer();

  @Test
  public void lexIdentifiers() {
    assertThat(lexer.lex("abc"), hasToken("abc", IDENTIFIER));
    assertThat(lexer.lex("abc0"), hasToken("abc0", IDENTIFIER));
    assertThat(lexer.lex("abc_0"), hasToken("abc_0", IDENTIFIER));
    assertThat(lexer.lex("i"), hasToken("i", IDENTIFIER));
  }

  @Test
  public void lexIntegers() {
    assertThat(lexer.lex("0"), hasToken("0", INTEGER));
    assertThat(lexer.lex("000"), hasToken("000", INTEGER));
    assertThat(lexer.lex("1234"), hasToken("1234", INTEGER));
  }

  @Test
  public void lexKeywords() {
    assertThat(lexer.lex("int"), hasToken(INT));
    assertThat(lexer.lex("void"), hasToken(VOID));
    assertThat(lexer.lex("return"), hasToken(RETURN));
    assertThat(lexer.lex("if"), hasToken(IF));
    assertThat(lexer.lex("else"), hasToken(ELSE));
    assertThat(lexer.lex("while"), hasToken(WHILE));
    assertThat(lexer.lex("break"), hasToken(BREAK));
    assertThat(lexer.lex("continue"), hasToken(CONTINUE));
  }

  @Test
  public void lexPunctuators() {
    assertThat(lexer.lex("("), hasToken(PAREN_L));
    assertThat(lexer.lex(")"), hasToken(PAREN_R));
    assertThat(lexer.lex("{"), hasToken(BRACE_L));
    assertThat(lexer.lex("}"), hasToken(BRACE_R));
    assertThat(lexer.lex("="), hasToken(EQ));
    assertThat(lexer.lex(","), hasToken(COMMA));
    assertThat(lexer.lex(";"), hasToken(SEMICOLON));
    assertThat(lexer.lex("+"), hasToken(ADD));
    assertThat(lexer.lex("-"), hasToken(SUB));
    assertThat(lexer.lex("*"), hasToken(MUL));
    assertThat(lexer.lex("/"), hasToken(DIV));
    assertThat(lexer.lex("<"), hasToken(LT));
    assertThat(lexer.lex("<="), hasToken(LTE));
    assertThat(lexer.lex(">"), hasToken(GT));
    assertThat(lexer.lex(">="), hasToken(GTE));
    assertThat(lexer.lex("=="), hasToken(EQEQ));
    assertThat(lexer.lex("!="), hasToken(NE));
    assertThat(lexer.lex("++"), hasToken(INC));
    assertThat(lexer.lex("--"), hasToken(DEC));
  }

}