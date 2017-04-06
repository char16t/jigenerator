public class Interpreter {
    Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public String visit(AST node) {
        if (node instanceof ASTUnaryOp) {
            return visitUnaryOp((ASTUnaryOp) node);
        }
        if (node instanceof ASTNum) {
            return visitNum((ASTNum) node);
        }
        if (node instanceof ASTBinOp) {
            return visitBinOp((ASTBinOp) node);
        } else {
            return "";
        }
    }

    public String visitUnaryOp(ASTUnaryOp node) {
        String result = "";
        return result;
    }

    public String visitNum(ASTNum node) {
        String result = "";
        return result;
    }

    public String visitBinOp(ASTBinOp node) {
        String result = "";
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