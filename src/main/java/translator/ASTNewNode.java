package translator;

import java.util.LinkedList;
import java.util.List;

public class ASTNewNode implements AST {
    String value;
    List<AST> childs;

    public ASTNewNode() {
        this.value = "";
        this.childs = new LinkedList<AST>();
    }

    public ASTNewNode(String value) {
        this.value = value;
        this.childs = new LinkedList<AST>();
    }

    public ASTNewNode(String value, AST node) {
        this.value = value;
        this.childs = new LinkedList<AST>();
        this.addChild(node);
    }

    public void addChild(AST child) {
        this.childs.add(child);
    }

    public void addChilds(List<AST> childs) {
        this.childs.addAll(childs);
    }
}
