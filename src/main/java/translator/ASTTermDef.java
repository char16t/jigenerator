package translator;

public class ASTTermDef implements AST {
    String head;
    AST expr;

    public ASTTermDef(String head, AST expr) {
        this.head = head;
        this.expr = expr;
    }
}
