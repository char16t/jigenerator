package translator;

import java.util.List;


public final class ASTExpression implements AST {
    private final List<AST> childs;

    public ASTExpression(final List<AST> childsGroup) {
        this.childs = childsGroup;
    }

    public List<AST> childs() {
        return childs;
    }
}
