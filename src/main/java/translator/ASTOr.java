package translator;

import java.util.List;

public final class ASTOr implements AST {
    private final List<AST> expressions;

    public ASTOr(final List<AST> expressions) {
        this.expressions = expressions;
    }

    public List<AST> expressions() {
        return expressions;
    }
}
