package translator;

import java.util.List;

public class ASTTermDef implements AST {
    AST expr;
    public ASTTermDef(AST termexpr) {
        this.expr = termexpr;
    }
}
