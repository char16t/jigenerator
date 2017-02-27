package translator;

public final class ASTNonermDef implements AST {
    private final String name;
    private final AST expr;

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
