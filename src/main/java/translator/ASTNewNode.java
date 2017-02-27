package translator;

public final class ASTNewNode implements AST {
    private final String value;
    private final String localVariableName;

    public ASTNewNode(final String value) {
        this(value, "");
    }

    public ASTNewNode(final String value, final String localVariableName) {
        this.value = value;
        this.localVariableName = localVariableName;
    }

    public String value() {
        return value;
    }

    public String localVariableName() {
        return localVariableName;
    }
}
