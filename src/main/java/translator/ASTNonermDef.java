package translator;

public class ASTNonermDef implements AST {
    AST expr;
    public ASTNonermDef(AST termexpr) {
        this.expr = termexpr;
    }
}
