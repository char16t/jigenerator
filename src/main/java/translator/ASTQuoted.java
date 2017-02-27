package translator;

public final class ASTQuoted implements AST {
    private final String value;

    public ASTQuoted(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
