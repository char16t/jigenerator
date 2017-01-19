package translator;

import java.util.List;


public class ASTOrExpr implements AST {
    List<AST> childs;

    public ASTOrExpr(List<AST> childsGroup) {
        this.childs = childsGroup;
    }
}
