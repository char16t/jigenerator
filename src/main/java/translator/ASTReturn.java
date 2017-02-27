package translator;

public final class ASTReturn implements AST {
    private final String value;

    public ASTReturn(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
