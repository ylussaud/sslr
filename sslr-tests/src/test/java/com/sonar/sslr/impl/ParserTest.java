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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.test.miniC.MiniCParser.parseFile;
import static com.sonar.sslr.test.miniC.MiniCParser.parseString;
import static org.fest.assertions.Assertions.assertThat;

public class ParserTest {

  @Test(expected = RecognitionException.class)
  public void lexerErrorStringWrappedInRecognitionException() {
    parseString(".");
  }

  @Test(expected = RecognitionException.class)
  public void lexerErrorFileWrappedInRecognitionException() {
    parseFile("/OwnExamples/lexererror.mc");
  }

  @Test
  public void lexerErrorNotWorthToRetry() {
    try {
      parseString(".");
      throw new AssertionError("This should be unreachable!");
    } catch (RecognitionException re) {
      assertThat(re.isToRetryWithExtendStackTrace()).isFalse();
    }
  }

  @Test
  public void parseErrorWorthToRetry() {
    try {
      parseString("<");
      throw new AssertionError("This should be unreachable!");
    } catch (RecognitionException re) {
      assertThat(re.isToRetryWithExtendStackTrace()).isTrue();
    }
  }

  @Test
  public void parse() {
    AstNode compilationUnit = parseString("");
    assertThat(compilationUnit.getNumberOfChildren()).isEqualTo(1);
    assertThat(compilationUnit.getChild(0).is(EOF)).isTrue();
  }

}
