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
package org.sonar.sslr.matchers;

import com.sonar.sslr.api.Rule;
import org.sonar.sslr.internal.matchers.BasicMatcherContext;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.MatcherContext;

/**
 * <p>This class is not intended to be subclassed by clients.</p>
 */
public class ParseRunner {

  private final Matcher rootMatcher;

  public ParseRunner(Rule rule) {
    this.rootMatcher = (Matcher) rule;
  }

  public ParsingResult parse(char[] input) {
    MatcherContext matcherContext = new BasicMatcherContext(input, rootMatcher);
    boolean matched = matcherContext.runMatcher() && matcherContext.length() == 0;
    return new ParsingResult(matched);
  }

}
