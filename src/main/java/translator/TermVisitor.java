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

    /**
     * Flag. '1' if method getStartSymbolsForTermSubnode works. Else 0.
     * @// @checkstyle MemberName (3 lines)
     * @// @checkstyle ExplicitInitialization (2 lines)
     */
    private int isGetStartSymbolsForTermSubnodeWorks = 0;

    /**
     * Current terminal.
     */
    private String term = "";

    /**
     * A strings of characters which can starts a terminals.
     * @// @checkstyle MemberName (2 lines)
     */
    private Map<String, String> terminalsCanStartsWith = new HashMap<>();

    /**
     * A string of characters which can start a nonterminal.
     * @// @checkstyle MemberName (2 lines)
     */
    private String startSymbolsForNontermSubnode = "";

    /**
     * Ctor.
     * @param tree Subtree node
     * @return Map. Keys is a terminals. Values is a strings consisting
     *  of characters from which you can start the terminal.
     */
    public Map<String, String> getTerminalsCanStartsWith(final AST tree) {
        this.visit(tree);
        return this.terminalsCanStartsWith;
    }

    /**
     * Notice: run this method only after visiting all AST tree.
     * @param tree Subtree node
     * @return The string consisting of characters from which you can start
     *  the terminal
     */
    public String getStartSymbolsForTermSubnode(final AST tree) {
        this.startSymbolsForNontermSubnode = "";
        this.isGetStartSymbolsForTermSubnodeWorks = 1;
        this.visit(tree);
        this.isGetStartSymbolsForTermSubnodeWorks = 0;
        return this.startSymbolsForNontermSubnode;
    }

    /**
     * Visit abstract node.
     * @param node AST-tree node.
     */
    public void visit(final AST node) {
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

    /**
     * Visit 'expression' node.
     * @param node AST-tree node.
     */
    public void visitExpression(final ASTExpression node) {
        this.visit(node.childs().get(0));
    }

    /**
     * Visit 'or' node.
     * @param node AST-tree node.
     */
    public void visitOr(final ASTOr node) {
        for (final AST expr : node.expressions()) {
            this.visit(expr);
        }
    }

    /**
     * Visit 'program' node.
     * @param node AST-tree node.
     */
    public void visitProgram(final ASTProgram node) {
        for (final AST child : node.childs()) {
            if (child instanceof ASTTermDef) {
                this.visit(child);
            }
        }
    }

    /**
     * Visit 'quoted' node.
     * @// TODO 3/5/17 This TODO has been moved from 'else' branch. See it.
     * @param node AST-tree node
     */
    public void visitQuoted(final ASTQuoted node) {
        if (this.isGetStartSymbolsForTermSubnodeWorks == 0) {
            final String first =
                ((Character) node.value().charAt(0)).toString();
            if (this.terminalsCanStartsWith.containsKey(this.term)
                && !this.terminalsCanStartsWith.get(this.term)
                    .contains(first)) {
                String old = this.terminalsCanStartsWith.get(this.term);
                old += first;
                this.terminalsCanStartsWith.put(this.term, old);
            }
        } else {
            final String first =
                ((Character) node.value().charAt(0)).toString();
            if (!this.startSymbolsForNontermSubnode.contains(first)) {
                this.startSymbolsForNontermSubnode += first;
            }
        }
    }

    /**
     * Visit 'repeat' node.
     * @param node AST-tree node.
     */
    public void visitRepeat(final ASTRepeat node) {
        this.visit(node.childs().get(0));
    }

    /**
     * Visit term definition.
     * @param node AST-tree node.
     */
    public void visitTermDef(final ASTTermDef node) {
        this.term = node.head();
        this.terminalsCanStartsWith.put(node.head(), "");
        this.visit(node.expr());
    }
}
