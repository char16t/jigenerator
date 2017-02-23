package translator;

import java.util.List;


public class ASTExpression implements AST {
    private List<AST> childs;

    public ASTExpression(final List<AST> childsGroup) {
        this.childs = childsGroup;
    }
}
