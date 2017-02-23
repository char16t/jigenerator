package translator;

import java.util.List;

public class ASTOr implements AST {
    private List<AST> expressions;

    public ASTOr(final List<AST> expressions) {
        this.expressions = expressions;
    }
}
