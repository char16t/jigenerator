package translator;

public class ASTNonermDef implements AST {
    private String name;
    private AST expr;

    public ASTNonermDef(final String name, final AST expr) {
        this.name = name;
        this.expr = expr;
    }
}
