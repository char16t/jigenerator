package translator;

import java.util.List;

public class ASTOr implements AST {
    List<AST> expressions;

    public ASTOr(List<AST> expressions) {
        this.expressions = expressions;
    }
}
