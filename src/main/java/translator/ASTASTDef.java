package translator;

public class ASTASTDef implements AST {
    String name;
    Integer numChilds;

    public ASTASTDef(String name, Integer numChilds) {
        this.name = name;
        this.numChilds = numChilds;
    }
}
