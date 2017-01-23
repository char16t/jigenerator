package translator;

import java.util.List;

public class ASTProgram implements AST {
    List<AST> childs;

    public ASTProgram(List<AST> childs) {
        this.childs = childs;
    }
}
