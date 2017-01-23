package translator;

import java.util.List;

public class ASTRepeat implements AST {
    List<AST> childs;

    public ASTRepeat(List<AST> childs) {
        this.childs = childs;
    }
}
