package translator;

import java.util.LinkedList;
import java.util.List;

public class ASTASTDef implements AST {
    String name;
    List<String> childs;

    public ASTASTDef(String name, List<String> types) {
        this.name = name;
        this.childs = types;
    }
}
