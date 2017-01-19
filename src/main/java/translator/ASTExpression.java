package translator;

import java.util.List;


public class ASTExpression implements AST {
    List<AST> childs;

    public ASTExpression(List<AST> childsGroup) {
        this.childs = childsGroup;
    }
}
