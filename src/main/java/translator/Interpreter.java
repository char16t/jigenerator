package translator;

/**
 * Created by user on 1/5/17.
 */
public class Interpreter {
    Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public static String visit(AST node) {
        if (node instanceof ASTExpression) {
            return visitExpression((ASTExpression) node);
        } else if (node instanceof ASTNonermDef) {
            return visitNonermDef((ASTNonermDef) node);
        } else if (node instanceof ASTNonterm) {
            return visitNonterm((ASTNonterm) node);
        } else if (node instanceof ASTOr) {
            return visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            return visitProgram((ASTProgram) node);
        } else if (node instanceof ASTQuoted) {
            return visitQuoted((ASTQuoted) node);
        } else if (node instanceof ASTRepeat) {
            return visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTerm) {
            return visitTerm((ASTTerm) node);
        } else if (node instanceof ASTTermDef) {
            return visitTermDef((ASTTermDef) node);
        } else {
            return "";
        }
    }

    public static String visitExpression(ASTExpression node) {
        String result =  "ASTExpression:\n";
        for (AST child : node.childs) {
            result += "            " + visit(child);
        }
        return result;
    }

    public static String visitNonermDef(ASTNonermDef node) {
        String result = "ASTNontermDef:\n        " + visit(node.expr);
        return result;
    }

    public static String visitNonterm(ASTNonterm node) {
        return "ASTNonterm\n";
    }

    public static String visitOr(ASTOr node) {
        return "ASTOr\n";
    }

    public static String visitProgram(ASTProgram node) {
        String result = "ASTProgram:\n";
        for (AST child : node.childs) {
            result += "    " + visit(child);
        }
        return result;
    }

    public static String visitQuoted(ASTQuoted node) {
        return "ASTQuoted\n";
    }

    public static String visitRepeat(ASTRepeat node) {
        return "ASTRepeat\n";
    }

    public static String visitTerm(ASTTerm node) {
        return "ASTTerm\n";
    }

    public static String visitTermDef(ASTTermDef node) {
        String result = "ASTTermDef:\n        " + visit(node.expr);
        return result;
    }

    public String interpret() throws Exception {
        AST tree = parser.parse();
        if (tree == null) {
            return "";
        }
        return visit(tree);
    }
}
