/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public class RuleMatcher extends CompositeMatcher {

  protected String rule;

  public void setRule(String name) {
    rule = name;
  }

  @Override
  public final AstNode match(AstNode node) {
    if(node.getName().equals(rule)){
      return matchUnderlyingMatcher(node);
    }
    if (node.hasChildren()) {
      for (AstNode child : node.getChildren()) {
        if (child.getName().equals(rule)) {
          return matchUnderlyingMatcher(child);
        }
      }
      for (AstNode child : node.getChildren()) {
        return match(child);
      }
    }
    return null;
  }
  
  private AstNode matchUnderlyingMatcher(AstNode node){
    if(matcher != null && matcher.match(node) == null){
      return null;
    }
    return node;
  }

}
