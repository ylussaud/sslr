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
package com.sonar.sslr.impl.analysis;

import java.io.PrintStream;
import java.util.List;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;

public final class OrAnalyserStream {

  private OrAnalyserStream() {
  }

  public static void print(OrAnalyser orAnalyser, PrintStream stream) {
    printEmptyViolations(orAnalyser, stream);
    printPrefixViolations(orAnalyser, stream);
    printPotentialPrefixViolations(orAnalyser, stream);
  }

  private static void printEmptyViolations(OrAnalyser orAnalyser, PrintStream stream) {
    if (orAnalyser.getEmptyAlternativeViolations().isEmpty()) {
      stream.println("Empty violations: None");
      stream.println();
      return;
    }

    stream.println("Empty violations (" + orAnalyser.getEmptyAlternativeViolations().size() + ")");
    stream.println("----------------");

    printViolations(orAnalyser.getEmptyAlternativeViolations(), stream);

    stream.println();
  }

  private static void printPrefixViolations(OrAnalyser orAnalyser, PrintStream stream) {
    if (orAnalyser.getPrefixAlternativeViolations().isEmpty()) {
      stream.println("Prefix violations: None");
      stream.println();
      return;
    }

    stream.println("Prefix violations (" + orAnalyser.getPrefixAlternativeViolations().size() + ")");
    stream.println("-----------------");

    printViolations(orAnalyser.getPrefixAlternativeViolations(), stream);

    stream.println();
  }

  private static void printPotentialPrefixViolations(OrAnalyser orAnalyser, PrintStream stream) {
    if (orAnalyser.getPotentialPrefixAlternativeViolations().isEmpty()) {
      stream.println("Potential prefix violations: None");
      stream.println();
      return;
    }

    stream.println("Potential prefix violations (" + orAnalyser.getPotentialPrefixAlternativeViolations().size() + ")");
    stream.println("---------------------------");

    printViolations(orAnalyser.getPotentialPrefixAlternativeViolations(), stream);

    stream.println();
  }

  private static void printViolations(List<Violation> violations, PrintStream stream) {
    for (Violation violation : violations) {
      stream.println(" Violation confidence: " + violation.getConfidence());
      stream.println(" In rule: " + (violation.getParentRule() == null ? "[Not Available]" : violation.getParentRule().getName()));
      stream.println(" On or matcher: " + MatcherTreePrinter.print(violation.getRelatedMatcher(0)));
      stream.println(" On alternative: " + MatcherTreePrinter.print(violation.getAffectedMatcher()));

      if (violation.getRelatedMatcher(1) != null) {
        stream.println(" Caused by alternative: " + MatcherTreePrinter.print(violation.getRelatedMatcher(1)));
      }

      if (violation.getProperty(OrAnalyser.PREFIX_EXAMPLE) != null) {
        /* There is an example available */
        stream.print(" Example: ");

        @SuppressWarnings("unchecked")
        List<Token> exampleTokens = (List<Token>) violation.getProperty(OrAnalyser.PREFIX_EXAMPLE);
        for (Token token : exampleTokens) {
          stream.print(token.getValue());
          stream.print(' ');
        }

        stream.println();
      }

      stream.println();
    }
  }

}
