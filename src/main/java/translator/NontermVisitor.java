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
        = new HashMap<String, LinkedList<String>>();
    private Map<String, LinkedList<String>> nonterminalsStartsWithTerminals
        = new HashMap<String, LinkedList<String>>();
    private List<String> termsForNontermSubnode = new LinkedList<String>();
    private List<String> nontermsForNontermSubnode = new LinkedList<String>();

    public Map<String, LinkedList<String>> getResult(AST tree) {
        visit(tree);
        return getResultMap();
    }

    // todo: rename me
    private Map<String, LinkedList<String>> getResultMap() {
        Map<String, LinkedList<String>> result
            = new HashMap<String, LinkedList<String>>();

        for (String nonterm : nonterminalsStartsWithTerminals.keySet()) {
            final LinkedList<String> ret = getChildsTerms(nonterm);
            result.put(nonterm, new LinkedList<String>() {{
                addAll(ret);
            }});
        }

        return result;
    }

    // todo: rename me
    private LinkedList<String> getChildsTerms(final String nonterm) {
        LinkedList<String> result = new LinkedList<String>();
        LinkedList<String> list = nonterminalsStartsWithTerminals.get(nonterm);
        for (String s : list) {
            result.addAll(nonterminalsCanStartsWith.get(s));
            result.addAll(getChildsTerms(s));
        }
        result.addAll(nonterminalsCanStartsWith.get(nonterm));

        // clear double values
        Set<String> hs = new HashSet<String>(result);
        result.clear();
        result.addAll(hs);

        return result;
    }

    // run this method only after visiting all AST tree
    public List<String> getStartTermsForNontermSubnode(AST tree) {
        termsForNontermSubnode.clear();
        nontermsForNontermSubnode.clear();

        isGetStartTermsForNontermSubnodeWorks = 1;
        visit(tree);
        isGetStartTermsForNontermSubnodeWorks = 0;
        for (String nonterminal : nontermsForNontermSubnode) {
            LinkedList<String> ret = getChildsTerms(nonterminal);
            termsForNontermSubnode.addAll(ret);
        }

        // clear double values
        Set<String> hs = new HashSet<String>(termsForNontermSubnode);
        termsForNontermSubnode.clear();
        termsForNontermSubnode.addAll(hs);
        return termsForNontermSubnode;
    }

    public void visit(AST node) {
        if (node instanceof ASTExpression) {
            visitExpression((ASTExpression) node);
        } else if (node instanceof ASTNonermDef) {
            visitNonermDef((ASTNonermDef) node);
        } else if (node instanceof ASTNonterm) {
            visitNonterm((ASTNonterm) node);
        } else if (node instanceof ASTOr) {
            visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            visitProgram((ASTProgram) node);
        } else if (node instanceof ASTRepeat) {
            visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTerm) {
            visitTerm((ASTTerm) node);
        }
    }

    private void visitTerm(final ASTTerm node) {
        if (isGetStartTermsForNontermSubnodeWorks == 0) {
            if (nonterminalsCanStartsWith.containsKey(currentNonterm)) {
                boolean termAlreadyAdded = false;
                for (String item
                    : nonterminalsCanStartsWith.get(currentNonterm)) {
                    if (item.equals(node.value())) {
                        termAlreadyAdded = true;
                    }
                }
                if (!termAlreadyAdded) {
                    nonterminalsCanStartsWith.get(currentNonterm)
                        .add(node.value());
                }
            }
        }
        boolean termAlreadyAdded2 = false;
        for (String item : this.termsForNontermSubnode) {
            if (item.equals(node.value())) {
                termAlreadyAdded2 = true;
            }
        }
        if (!termAlreadyAdded2) {
            termsForNontermSubnode.add(node.value());
        }
    }

    private void visitNonterm(final ASTNonterm node) {
        if (isGetStartTermsForNontermSubnodeWorks == 0) {
            if (nonterminalsStartsWithTerminals.containsKey(currentNonterm)) {
                boolean nontermAlreadyAdded = false;
                if (nonterminalsStartsWithTerminals
                    .get(currentNonterm) != null) {
                    for (String item
                        : nonterminalsStartsWithTerminals.get(currentNonterm)) {
                        if (item.equals(node.value())) {
                            nontermAlreadyAdded = true;
                        }
                    }
                    if (!nontermAlreadyAdded) {
                        nonterminalsStartsWithTerminals.get(currentNonterm)
                            .add(node.value());
                    }
                }
            }
        }
        boolean termAlreadyAdded2 = false;
        for (String item : this.nontermsForNontermSubnode) {
            if (item.equals(node.value())) {
                termAlreadyAdded2 = true;
            }
        }
        if (!termAlreadyAdded2) {
            nontermsForNontermSubnode.add(node.value());
        }

    }

    private void visitNonermDef(final ASTNonermDef node) {
        currentNonterm = node.name();
        nonterminalsCanStartsWith.put(node.name(), new LinkedList<String>());
        nonterminalsStartsWithTerminals.put(
            currentNonterm,
            new LinkedList<String>()
        );
        visit(node.expr());
    }

    public void visitExpression(final ASTExpression node) {
        if (node.childs().size() > 0) {
            visit(node.childs().get(0));
        }
    }

    public void visitOr(final ASTOr node) {
        for (AST expr : node.expressions()) {
            visit(expr);
        }
    }

    public void visitProgram(final ASTProgram node) {
        for (AST child : node.childs()) {
            if (child instanceof ASTNonermDef) {
                visit(child);
            }
        }
    }

    public void visitRepeat(final ASTRepeat node) {
        visit(node.childs().get(0));
    }

}
