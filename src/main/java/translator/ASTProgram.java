package translator;

import java.util.List;

public final class ASTProgram implements AST {
    private final List<AST> childs;

    public ASTProgram(final List<AST> childs) {
        this.childs = childs;
    }

    public List<AST> childs() {
        return childs;
    }
}
