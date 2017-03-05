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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Nonterminals visitor.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class NontermVisitor {
    private int isGetStartTermsForNontermSubnodeWorks = 0;
    private String currentNonterm = "";
    private Map<String, LinkedList<String>> nonterminalsCanStartsWith
        = new HashMap<>();
    private Map<String, LinkedList<String>> nonterminalsStartsWithTerminals
        = new HashMap<>();
    private List<String> termsForNontermSubnode = new LinkedList<>();
    private List<String> nontermsForNontermSubnode = new LinkedList<>();

    public Map<String, LinkedList<String>> getResult(final AST tree) {
        this.visit(tree);
        return getResultMap();
    }

    // todo: rename me
    private Map<String, LinkedList<String>> getResultMap() {
        final Map<String, LinkedList<String>> result
            = new HashMap<>();

        for (String nonterm : this.nonterminalsStartsWithTerminals.keySet()) {
            final LinkedList<String> ret = getChildsTerms(nonterm);
            result.put(nonterm, new LinkedList<String>() {{
                addAll(ret);
            }});
        }

        return result;
    }

    // todo: rename me
    private LinkedList<String> getChildsTerms(final String nonterm) {
        final LinkedList<String> result = new LinkedList<>();
        final LinkedList<String> list =
            this.nonterminalsStartsWithTerminals.get(nonterm);
        for (String s : list) {
            result.addAll(this.nonterminalsCanStartsWith.get(s));
            result.addAll(getChildsTerms(s));
        }
        result.addAll(this.nonterminalsCanStartsWith.get(nonterm));
        // clear double values
        Set<String> hs = new HashSet<>(result);
        result.clear();
        result.addAll(hs);
        return result;
    }

    // run this method only after visiting all AST tree
    public List<String> getStartTermsForNontermSubnode(final AST tree) {
        this.termsForNontermSubnode.clear();
        this.nontermsForNontermSubnode.clear();
        this.isGetStartTermsForNontermSubnodeWorks = 1;
        this.visit(tree);
        this.isGetStartTermsForNontermSubnodeWorks = 0;
        for (final String nonterminal : this.nontermsForNontermSubnode) {
            final LinkedList<String> ret = getChildsTerms(nonterminal);
            this.termsForNontermSubnode.addAll(ret);
        }
        // clear double values
        final Set<String> hs = new HashSet<>(this.termsForNontermSubnode);
        this.termsForNontermSubnode.clear();
        this.termsForNontermSubnode.addAll(hs);
        return this.termsForNontermSubnode;
    }

    /**
     * Visit abstract node.
     * @param node AST-tree node
     */
    public void visit(final AST node) {
        if (node instanceof ASTExpression) {
            this.visitExpression((ASTExpression) node);
        } else if (node instanceof ASTNonermDef) {
            this.visitNonermDef((ASTNonermDef) node);
        } else if (node instanceof ASTNonterm) {
            this.visitNonterm((ASTNonterm) node);
        } else if (node instanceof ASTOr) {
            this.visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            this.visitProgram((ASTProgram) node);
        } else if (node instanceof ASTRepeat) {
            this.visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTerm) {
            this.visitTerm((ASTTerm) node);
        }
    }

    /**
     * Visit 'term' node.
     * @param node AST-tree 'term' node
     */
    private void visitTerm(final ASTTerm node) {
        if (this.isGetStartTermsForNontermSubnodeWorks == 0) {
            if (this.nonterminalsCanStartsWith
                .containsKey(this.currentNonterm)) {
                boolean termAlreadyAdded = false;
                for (final String item
                    : this.nonterminalsCanStartsWith.get(this.currentNonterm)) {
                    if (item.equals(node.value())) {
                        termAlreadyAdded = true;
                    }
                }
                if (!termAlreadyAdded) {
                    this.nonterminalsCanStartsWith.get(this.currentNonterm)
                        .add(node.value());
                }
            }
        }
        boolean termAlreadyAdded2 = false;
        for (final String item : this.termsForNontermSubnode) {
            if (item.equals(node.value())) {
                termAlreadyAdded2 = true;
            }
        }
        if (!termAlreadyAdded2) {
            this.termsForNontermSubnode.add(node.value());
        }
    }

    /**
     * Visit 'nonterm' node.
     * @param node AST-tree 'nonterm' node
     */
    private void visitNonterm(final ASTNonterm node) {
        if (this.isGetStartTermsForNontermSubnodeWorks == 0) {
            if (this.nonterminalsStartsWithTerminals.containsKey(currentNonterm)) {
                boolean nontermAlreadyAdded = false;
                if (this.nonterminalsStartsWithTerminals
                    .get(this.currentNonterm) != null) {
                    for (final String item
                        : this.nonterminalsStartsWithTerminals
                        .get(this.currentNonterm)) {
                        if (item.equals(node.value())) {
                            nontermAlreadyAdded = true;
                        }
                    }
                    if (!nontermAlreadyAdded) {
                        this.nonterminalsStartsWithTerminals
                            .get(this.currentNonterm)
                            .add(node.value());
                    }
                }
            }
        }
        boolean termAlreadyAdded2 = false;
        for (final String item : this.nontermsForNontermSubnode) {
            if (item.equals(node.value())) {
                termAlreadyAdded2 = true;
            }
        }
        if (!termAlreadyAdded2) {
            this.nontermsForNontermSubnode.add(node.value());
        }

    }

    /**
     * Visit 'nonterm definition' node.
     * @param node AST-tree node
     */
    private void visitNonermDef(final ASTNonermDef node) {
        this.currentNonterm = node.name();
        this.nonterminalsCanStartsWith.put(node.name(), new LinkedList<String>());
        this.nonterminalsStartsWithTerminals.put(
            this.currentNonterm,
            new LinkedList<String>()
        );
        this.visit(node.expr());
    }

    /**
     * Visit 'expression' node.
     * @param node AST-tree 'expression' node
     */
    public void visitExpression(final ASTExpression node) {
        if (node.childs().size() > 0) {
            this.visit(node.childs().get(0));
        }
    }

    /**
     * Visit 'or' node.
     * @param node AST-tree 'or' node
     */
    public void visitOr(final ASTOr node) {
        for (final AST expr : node.expressions()) {
            this.visit(expr);
        }
    }

    /**
     * Visit 'program' node.
     * @param node AST-tree 'program' node
     */
    public void visitProgram(final ASTProgram node) {
        for (final AST child : node.childs()) {
            if (child instanceof ASTNonermDef) {
                this.visit(child);
            }
        }
    }

    /**
     * Visit 'repeat' node.
     * @param node AST-tree 'repeat' node
     */
    public void visitRepeat(final ASTRepeat node) {
        this.visit(node.childs().get(0));
    }

}
