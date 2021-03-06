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
import com.sonar.sslr.api.GenericTokenType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;

public class RuleDefinitionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testEmptyIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'JavaClassDefinition' should at least contains one matcher.");
    javaClassDefinition.is();
  }

  @Test
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is("option1");
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'JavaClassDefinition' has already been defined somewhere in the grammar.");
    javaClassDefinition.is("option2");
  }

  @Test
  public void testIs() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule())).isEqualTo("MyRule.is(\"option1\")");
  }

  @Test
  public void testOverride() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule())).isEqualTo("MyRule.is(\"option1\")");
    myRule.override("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule())).isEqualTo("MyRule.is(\"option2\")");
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'MyRule' has already been defined somewhere in the grammar.");
    myRule.is("option3");
  }

  @Test
  public void testMock() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("foo");
    myRule.is("foo");
    myRule.mock();
    assertThat(MatcherTreePrinter.print(myRule.getRule())).isEqualTo("foo.is(or(\"foo\", \"FOO\"))");
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule 'foo' has already been defined somewhere in the grammar.");
    myRule.is("bar");
  }

  @Test
  public void testSkipFromAst() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null)).isFalse();

    ruleBuilder.skip();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null)).isTrue();
  }

  @Test
  public void testSkipFromAstIf() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.skipIfOneChild();

    AstNode parent = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "parent"));
    AstNode child1 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child1.addChild(child2);

    assertThat(ruleBuilder.hasToBeSkippedFromAst(parent)).isFalse();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child2)).isFalse();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child1)).isTrue();
  }
}
