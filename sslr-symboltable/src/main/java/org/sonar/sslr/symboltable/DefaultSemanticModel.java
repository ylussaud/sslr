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
package org.sonar.sslr.symboltable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.sonar.sslr.api.AstNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link SemanticModel}.
 */
public class DefaultSemanticModel implements SemanticModel {

  private final Map<AstNode, Scope> astToScope = Maps.newIdentityHashMap();
  private final Map<AstNode, Symbol> astToSymbol = Maps.newIdentityHashMap();
  private final Multimap<Symbol, AstNode> references = HashMultimap.create();

  public Symbol getDeclaredSymbol(AstNode astNode) {
    Symbol result = null;
    while (result == null && astNode != null) {
      result = astToSymbol.get(astNode);
      astNode = astNode.getParent();
    }
    return result;
  }

  public void declareSymbol(AstNode astNode, Symbol symbol) {
    getEnclosingScope(astNode).addSymbol(symbol);
    astToSymbol.put(astNode, symbol);
  }

  public void declareScope(AstNode astNode, Scope scope) {
    Scope enclosingScope = scope.getEnclosingScope();
    if (enclosingScope != null) {
      enclosingScope.addNestedScope(scope);
    }
    astToScope.put(astNode, scope);
  }

  public void declareReference(AstNode astNode, Symbol symbol) {
    references.put(symbol, astNode);
  }

  public Scope getEnclosingScope(AstNode astNode) {
    Scope result = null;
    while (result == null && astNode != null) {
      result = astToScope.get(astNode);
      astNode = astNode.getParent();
    }
    return result;
  }

  public Collection<AstNode> getReferences(Symbol symbol) {
    return references.get(symbol);
  }

  public <T extends Symbol> Collection<T> getSymbols(Class<T> kind) {
    List<T> result = Lists.newArrayList();
    for (Symbol symbol : astToSymbol.values()) {
      if (kind.isInstance(symbol)) {
        result.add((T) symbol);
      }
    }
    return result;
  }

  public <T extends Scope> Collection<T> getScopes(Class<T> kind) {
    List<T> result = Lists.newArrayList();
    for (Scope scope : astToScope.values()) {
      if (kind.isInstance(scope)) {
        result.add((T) scope);
      }
    }
    return result;
  }

}
