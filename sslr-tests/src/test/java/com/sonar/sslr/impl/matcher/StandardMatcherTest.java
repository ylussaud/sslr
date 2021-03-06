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

import com.sonar.sslr.impl.ParsingState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;

public class StandardMatcherTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testDoMatch() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Should be implemented in " + StandardMatcher.class.getName());
    ParsingState parsingState = mock(ParsingState.class);
    new StandardMatcher().doMatch(parsingState);
  }

  @Test
  public void testMatchWorker() {
    thrown.expect(UnsupportedOperationException.class);
    ParsingState parsingState = mock(ParsingState.class);
    new StandardMatcher().matchWorker(parsingState);
  }

}
