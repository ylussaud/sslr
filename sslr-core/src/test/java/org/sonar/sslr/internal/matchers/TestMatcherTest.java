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

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestMatcherTest {

  private Matcher subMatcher;
  private MatcherContext context, subContext;
  private TestMatcher matcher;

  @Before
  public void setUp() {
    subMatcher = mock(Matcher.class);
    subContext = mock(MatcherContext.class);
    matcher = new TestMatcher(subMatcher);
    context = mock(MatcherContext.class);
    when(context.getSubContext(subMatcher)).thenReturn(subContext);
  }

  @Test
  public void should_match() {
    when(subContext.runMatcher()).thenReturn(true);
    assertThat(matcher.match(context)).isTrue();
    verify(subContext).runMatcher();
    verify(context).resetIndex();
  }

  @Test
  public void should_not_match() {
    when(subContext.runMatcher()).thenReturn(false);
    assertThat(matcher.match(context)).isFalse();
    verify(subContext).runMatcher();
    verify(context, never()).resetIndex();
  }

}
