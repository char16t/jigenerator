package translator;

public class ASTNonermDef implements AST {
    String name;
    AST expr;

    public ASTNonermDef(String name, AST expr) {
        this.name = name;
        this.expr = expr;
    }
}
