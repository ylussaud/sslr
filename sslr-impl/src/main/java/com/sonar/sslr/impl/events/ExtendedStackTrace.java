/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.ArrayDeque;

import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class ExtendedStackTrace implements ParsingEventListener {
	ArrayDeque<RuleImpl> longestStack = new ArrayDeque<RuleImpl>();

	public void enterRule(RuleImpl rule, ParsingState parsingState) {
		System.out.println("enterRule" + rule + ", at " + parsingState.lexerIndex);
		
		longestStack.push(rule);
	}

	public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState) {
		System.out.println("exitWithMatchRule" + rule + ", till " + parsingState.lexerIndex);
		
		/* The rule matched, so we made some progress, keep it in the longest stack trace */
	}

	public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
		System.out.println("exitWithoutMatchRule" + rule);
		
		/* The rule did actually not match, no progress, remove it from the longest stack trace */
		longestStack.pop();
	}

	public void printStackTrace() {
		ArrayDeque<RuleImpl> printStack = new ArrayDeque<RuleImpl>(longestStack);
		while (!printStack.isEmpty()) {
			RuleImpl rule = printStack.pop();
			System.out.println(rule);
		}
	}
}