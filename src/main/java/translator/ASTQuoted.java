package translator;

public class ASTQuoted implements AST {
    private String value;

    public ASTQuoted(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
