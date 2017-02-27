package translator;

public final class ASTTermDef implements AST {
    private final String head;
    private final AST expr;

    public ASTTermDef(final String head, final AST expr) {
        this.head = head;
        this.expr = expr;
    }

    public String head() {
        return head;
    }

    public AST expr() {
        return expr;
    }
}
