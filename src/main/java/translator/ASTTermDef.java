package translator;

public class ASTTermDef implements AST {
    private String head;
    private AST expr;

    public ASTTermDef(final String head, final AST expr) {
        this.head = head;
        this.expr = expr;
    }
}
