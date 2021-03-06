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
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.fest.assertions.Assertions.assertThat;

public class AstCreatorTest {

  @Test
  public void test() throws Exception {
    String inputString = "20 * 2 + 2 - var";
    ExpressionGrammar grammar = new ExpressionGrammar();
    char[] input = inputString.toCharArray();
    MatcherContext matcherContext = new BasicMatcherContext(input, (Matcher) grammar.root);
    matcherContext.runMatcher();

    URI uri = new File("/tmp/test.txt").toURI();
    AstNode astNode = AstCreator.create(uri, input, matcherContext.getNode());
    System.out.println(AstXmlPrinter.print(astNode));

    Token tokenWithTrivia = astNode.findFirstChild(GenericTokenType.LITERAL).getToken();
    assertThat(tokenWithTrivia.getTrivia()).hasSize(1);
  }

}
