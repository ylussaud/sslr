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
package org.sonar.sslr.internal.toolkit;

import com.google.common.annotations.VisibleForTesting;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ToolkitPresenter {

  public final SourceCodeModel model;
  public ToolkitView view = null;

  public ToolkitPresenter(SourceCodeModel model) {
    this.model = model;
  }

  public void setView(ToolkitView view) {
    checkNotNull(view);
    this.view = view;
  }

  @VisibleForTesting
  void checkInitialized() {
    checkState(view != null, "the view must be set before the presenter can be ran");
  }

  @VisibleForTesting
  void initUncaughtExceptionsHandler() {
    Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void uncaughtException(Thread t, Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);

        view.appendToConsole(result.toString());
        view.setFocusOnConsoleView();
      }
    });
  }

  public void run(String title) {
    checkInitialized();

    initUncaughtExceptionsHandler();

    view.setTitle(title);
    view.displayHighlightedSourceCode("");
    view.displayAst(null);
    view.displayXml("");
    view.disableXPathEvaluateButton();
    view.run();
  }

  public void onSourceCodeOpenButtonClick() {
    File fileToParse = view.pickFileToParse();
    if (fileToParse != null) {
      view.clearConsole();
      model.setSourceCode(fileToParse, Charset.defaultCharset());
      view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
      view.displayAst(model.getAstNode());
      view.displayXml(model.getXml());
      view.scrollSourceCodeTo(new Point(0, 0));
      view.setFocusOnAbstractSyntaxTreeView();
      view.enableXPathEvaluateButton();
    }
  }

  public void onSourceCodeParseButtonClick() {
    view.clearConsole();
    String sourceCode = view.getSourceCode();
    model.setSourceCode(sourceCode);
    Point sourceCodeScrollbarPosition = view.getSourceCodeScrollbarPosition();
    view.displayHighlightedSourceCode(model.getHighlightedSourceCode());
    view.displayAst(model.getAstNode());
    view.displayXml(model.getXml());
    view.scrollSourceCodeTo(sourceCodeScrollbarPosition);
    view.setFocusOnAbstractSyntaxTreeView();
    view.enableXPathEvaluateButton();
  }

  public void onXPathEvaluateButtonClick() {
    String xpath = view.getXPath();
    AstNodeXPathQuery<Object> xpathQuery = AstNodeXPathQuery.create(xpath);

    view.clearConsole();
    view.clearAstSelections();
    view.clearSourceCodeHighlights();

    AstNode firstAstNode = null;
    for (Object resultObject : xpathQuery.selectNodes(model.getAstNode())) {
      if (resultObject instanceof AstNode) {
        AstNode resultAstNode = (AstNode) resultObject;

        if (firstAstNode == null) {
          firstAstNode = resultAstNode;
        }

        view.selectAstNode(resultAstNode);
        view.highlightSourceCode(resultAstNode);
      }
    }

    view.scrollAstTo(firstAstNode);
    view.scrollSourceCodeTo(firstAstNode);

    view.setFocusOnAbstractSyntaxTreeView();
  }

  public void onSourceCodeKeyTyped() {
    view.displayAst(null);
    view.displayXml("");
    view.clearSourceCodeHighlights();
    view.disableXPathEvaluateButton();
  }

  public void onSourceCodeTextCursorMoved() {
    view.clearAstSelections();
    AstNode astNode = view.getAstNodeFollowingCurrentSourceCodeTextCursorPosition();
    view.selectAstNode(astNode);
    view.scrollAstTo(astNode);
  }

  public void onAstSelectionChanged() {
    view.clearSourceCodeHighlights();

    AstNode firstAstNode = null;

    for (AstNode astNode : view.getSelectedAstNodes()) {
      if (firstAstNode == null) {
        firstAstNode = astNode;
      }

      view.highlightSourceCode(astNode);
    }

    view.scrollSourceCodeTo(firstAstNode);
  }

}
