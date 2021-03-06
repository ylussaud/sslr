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

/**
 * Entity of program, which can be referenced.
 *
 * @see AbstractSymbol
 * @see AbstractScopedSymbol
 */
public interface Symbol {

  /**
   * Returns enclosing scope.
   */
  Scope getEnclosingScope();

  /**
   * Returns key of this symbol.
   * Key must be unique among symbols of same kind in same scope.
   * For example it can be variable name or signature of method.
   */
  String getKey();

}
