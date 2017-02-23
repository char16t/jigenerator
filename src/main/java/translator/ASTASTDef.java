package translator;

import java.util.List;

public class ASTASTDef implements AST {
    private String name;
    private List<String> childs;

    public ASTASTDef(final String name, final List<String> types) {
        this.name = name;
        this.childs = types;
    }
}
