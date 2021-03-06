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
package com.sonar.sslr.test.miniC;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.*;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.Parser;

public class MiniCPreprocessor extends Preprocessor {

  private final List<Token> buffer = Lists.newLinkedList();
  private AstNode structure;
  private Grammar structureGrammar;

  public class MiniCPreprocessorGrammar extends Grammar {

    public Rule defineDirective;
    public Rule binDefinition;

    public MiniCPreprocessorGrammar() {
      defineDirective.is(HASH, "define", binDefinition);
      binDefinition.is(IDENTIFIER);
    }

    @Override
    public Rule getRootRule() {
      return defineDirective;
    }
  }

  @Override
  public void init() {
    buffer.clear();
  }

  private boolean isBufferValid() {
    /* Launch the preprocessor parser on buffer, and set the resulting AstNode on the first token */
    Parser<MiniCPreprocessorGrammar> parser = Parser.builder(new MiniCPreprocessorGrammar()).build();
    try {
      AstNode node = parser.parse(buffer);
      this.structure = node;
      this.structureGrammar = parser.getGrammar();
      return true;
    } catch (RecognitionException re) {
      return false;
    }
  }

  private Trivia preprocessBuffer() {
    /* Here is where we should interpret the tokens, but there is no need in this case */

    /* Push the preprocessed trivia */
    return Trivia.createPreprocessingDirective(structure, structureGrammar);
  }

  @Override
  public PreprocessorAction process(List<Token> tokens) {
    List<Trivia> triviaToInject = new LinkedList<Trivia>();

    if ( !buffer.isEmpty() && (tokens.get(0).getType() == EOF || tokens.get(0).getLine() != buffer.get(0).getLine())) {
      if (isBufferValid()) {
        triviaToInject.add(preprocessBuffer());
      } else {
        throw new IllegalStateException("FIXME Pushback would be required!"); /* FIXME */
      }
      buffer.clear();
    }

    if (buffer.isEmpty()) {
      if (tokens.get(0).getType() == HASH) {
        buffer.add(tokens.get(0));
        return new PreprocessorAction(1, triviaToInject, new LinkedList<Token>());
      }

      return new PreprocessorAction(0, triviaToInject, new LinkedList<Token>());
    } else {
      buffer.add(tokens.get(0));
      return new PreprocessorAction(1, triviaToInject, new LinkedList<Token>());
    }
  }

}
