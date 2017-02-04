package translator;

public class ASTReturn implements AST {
    String value;

    public ASTReturn(String value) {
        this.value = value;
    }
}
