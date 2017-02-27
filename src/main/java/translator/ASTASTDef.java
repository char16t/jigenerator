package translator;

import java.util.List;

public final class ASTASTDef implements AST {
    private final String name;
    private final List<String> childs;

    public ASTASTDef(final String name, final List<String> types) {
        this.name = name;
        this.childs = types;
    }

    public String name() {
        return name;
    }

    public List<String> childs() {
        return childs;
    }
}
