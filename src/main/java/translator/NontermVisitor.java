package translator;

import java.util.*;

public class NontermVisitor {
    private String currentNonterm = "";
    private Map<String, LinkedList<String>> nonterminalsCanStartsWith = new HashMap<String, LinkedList<String>>();
    private Map<String, LinkedList<String>> nonterminalsStartsWithTerminals = new HashMap<String, LinkedList<String>>();

    public Map<String, LinkedList<String>> getResult(AST tree) {
        visit(tree);
        return getResultMap();
    }

    // todo: rename me
    private Map<String, LinkedList<String>> getResultMap() {
        Map<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>();

        for (String nonterm : nonterminalsStartsWithTerminals.keySet()) {
            final LinkedList<String> ret = getChildsTerms(nonterm);
            result.put(nonterm, new LinkedList<String>() {{
                addAll(ret);
            }});
        }

        return result;
    }

    // todo: rename me
    private LinkedList<String> getChildsTerms(String nonterm) {
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

    private List<String> termsForNontermSubnode = new LinkedList<String>();
    private List<String> nontermsForNontermSubnode = new LinkedList<String>();
    int isGetStartTermsForNontermSubnodeWorks = 0;

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

    private void visitTerm(ASTTerm node) {
        if (isGetStartTermsForNontermSubnodeWorks == 0) {
            if (nonterminalsCanStartsWith.containsKey(currentNonterm)) {
                boolean termAlreadyAdded = false;
                for (String item : nonterminalsCanStartsWith.get(currentNonterm)) {
                    if (item.equals(node.value)) {
                        termAlreadyAdded = true;
                    }
                }
                if (!termAlreadyAdded) {
                    nonterminalsCanStartsWith.get(currentNonterm).add(node.value);
                }
            }
        }
        boolean termAlreadyAdded2 = false;
        for (String item : this.termsForNontermSubnode) {
            if (item.equals(node.value)) {
                termAlreadyAdded2 = true;
            }
        }
        if (!termAlreadyAdded2) {
            termsForNontermSubnode.add(node.value);
        }
    }

    private void visitNonterm(ASTNonterm node) {
        if (isGetStartTermsForNontermSubnodeWorks == 0) {
            //nonterminalsStartsWithTerminals.put(currentNonterm, node.value);
            if (nonterminalsStartsWithTerminals.containsKey(currentNonterm)) {
                boolean nontermAlreadyAdded = false;
                if (nonterminalsStartsWithTerminals.get(currentNonterm) != null) {
                    for (String item : nonterminalsStartsWithTerminals.get(currentNonterm)) {
                        if (item.equals(node.value)) {
                            nontermAlreadyAdded = true;
                        }
                    }
                    if (!nontermAlreadyAdded) {
                        nonterminalsStartsWithTerminals.get(currentNonterm).add(node.value);
                    }
                }
            }
        }
        boolean termAlreadyAdded2 = false;
        for (String item : this.nontermsForNontermSubnode) {
            if (item.equals(node.value)) {
                termAlreadyAdded2 = true;
            }
        }
        if (!termAlreadyAdded2) {
            nontermsForNontermSubnode.add(node.value);
        }

    }

    private void visitNonermDef(ASTNonermDef node) {
        currentNonterm = node.name;
        nonterminalsCanStartsWith.put(node.name, new LinkedList<String>());
        nonterminalsStartsWithTerminals.put(currentNonterm, new LinkedList<String>());
        visit(node.expr);
    }

    public void visitExpression(ASTExpression node) {
        if (node.childs.size() > 0) {
            visit(node.childs.get(0));
        }
    }

    public void visitOr(ASTOr node) {
        for (AST expr : node.expressions) {
            visit(expr);
        }
    }

    public void visitProgram(ASTProgram node) {
        for (AST child : node.childs) {
            if (child instanceof ASTNonermDef) {
                visit(child);
            }
        }
    }

    public void visitRepeat(ASTRepeat node) {
        visit(node.childs.get(0));
    }

}
