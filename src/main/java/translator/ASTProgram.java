package translator;

import java.util.List;

public class ASTProgram implements AST {
    private List<AST> childs;

    public ASTProgram(final List<AST> childs) {
        this.childs = childs;
    }
}
