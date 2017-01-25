package translator;

import java.util.HashMap;
import java.util.Map;

public class TermVisitor {
    private String currentTerm = "";
    private Map<String, String> terminalsCanStartsWith = new HashMap<String, String>();

    public Map<String, String> getTerminalsCanStartsWith(AST tree) {
        visit(tree);
        return terminalsCanStartsWith;
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

    public void visitExpression(ASTExpression node) {
        visit(node.childs.get(0));
    }

    public void visitOr(ASTOr node) {
        for (AST expr : node.expressions) {
            visit(expr);
        }
    }

    public void visitProgram(ASTProgram node) {
        for (AST child : node.childs) {
            if (child instanceof ASTTermDef) {
                visit(child);
            }
        }
    }

    public void visitQuoted(ASTQuoted node) {
        String firstChar = ((Character) node.value.charAt(0)).toString();
        if (terminalsCanStartsWith.containsKey(currentTerm) &&
                !terminalsCanStartsWith.get(currentTerm).contains(firstChar)) {
            String old = terminalsCanStartsWith.get(currentTerm);
            old += firstChar;
            terminalsCanStartsWith.put(currentTerm, old);
        }
    }

    public void visitRepeat(ASTRepeat node) {
        visit(node.childs.get(0));
    }

    public void visitTermDef(ASTTermDef node) {
        currentTerm = node.head;
        terminalsCanStartsWith.put(node.head, "");
        visit(node.expr);
    }
}