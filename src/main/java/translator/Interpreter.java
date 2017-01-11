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
        if (node instanceof ASTOther) {
            return ((ASTOther) node).value;
        } else {
            return "";
        }
    }

    public String interpret() throws Exception {
        AST tree = parser.parse();
        if (tree == null) {
            return "";
        }
        return visit(tree);
    }
}
