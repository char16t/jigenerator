package translator;

import java.util.List;

public final class ASTRepeat implements AST {
    private final List<AST> childs;

    public ASTRepeat(final List<AST> childs) {
        this.childs = childs;
    }

    public List<AST> childs() {
        return childs;
    }
}
