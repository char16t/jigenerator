package translator;

public class ASTNonermDef implements AST {
    private String name;
    private AST expr;

    public ASTNonermDef(final String name, final AST expr) {
        this.name = name;
        this.expr = expr;
    }

    public String name() {
        return name;
    }

    public AST expr() {
        return expr;
    }
}
