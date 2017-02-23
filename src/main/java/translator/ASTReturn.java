package translator;

public class ASTReturn implements AST {
    private String value;

    public ASTReturn(final String value) {
        this.value = value;
    }
}
