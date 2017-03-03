/**
 * MIT License
 *
 * Copyright (c) 2017 Valeriy Manenkov and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package translator;

import java.util.HashMap;
import java.util.Map;

/**
 * Terminals visitor.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class TermVisitor {
    int isGetStartSymbolsForTermSubnodeWorks = 0;
    private String currentTerm = "";
    private Map<String, String> terminalsCanStartsWith = new HashMap<String, String>();
    private String startSymbolsForNontermSubnode = "";

    public Map<String, String> getTerminalsCanStartsWith(AST tree) {
        visit(tree);
        return terminalsCanStartsWith;
    }

    // run this method only after visiting all AST tree
    public String getStartSymbolsForTermSubnode(AST tree) {
        this.startSymbolsForNontermSubnode = "";

        isGetStartSymbolsForTermSubnodeWorks = 1;
        visit(tree);
        isGetStartSymbolsForTermSubnodeWorks = 0;

        return startSymbolsForNontermSubnode;
    }

    public void visit(AST node) {
        if (node instanceof ASTExpression) {
            this.visitExpression((ASTExpression) node);
        } else if (node instanceof ASTOr) {
            this.visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            this.visitProgram((ASTProgram) node);
        } else if (node instanceof ASTQuoted) {
            this.visitQuoted((ASTQuoted) node);
        } else if (node instanceof ASTRepeat) {
            this.visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTermDef) {
            this.visitTermDef((ASTTermDef) node);
        }
    }

    public void visitExpression(final ASTExpression node) {
        visit(node.childs().get(0));
    }

    public void visitOr(final ASTOr node) {
        for (AST expr : node.expressions()) {
            visit(expr);
        }
    }

    public void visitProgram(final ASTProgram node) {
        for (AST child : node.childs()) {
            if (child instanceof ASTTermDef) {
                visit(child);
            }
        }
    }

    public void visitQuoted(final ASTQuoted node) {
        if (isGetStartSymbolsForTermSubnodeWorks == 0) {
            String firstChar = ((Character) node.value().charAt(0)).toString();
            if (terminalsCanStartsWith.containsKey(currentTerm)
                    && !terminalsCanStartsWith.get(currentTerm).contains(firstChar)) {
                String old = terminalsCanStartsWith.get(currentTerm);
                old += firstChar;
                terminalsCanStartsWith.put(currentTerm, old);
            }
        } else {
            // TODO
            String firstChar = ((Character) node.value().charAt(0)).toString();
            if (!startSymbolsForNontermSubnode.contains(firstChar)) {
                startSymbolsForNontermSubnode += firstChar;
            }
        }


    }

    public void visitRepeat(final ASTRepeat node) {
        visit(node.childs().get(0));
    }

    public void visitTermDef(final ASTTermDef node) {
        currentTerm = node.head();
        terminalsCanStartsWith.put(node.head(), "");
        visit(node.expr());
    }
}
