package translator;

public final class ASTNonterm implements AST {
    private final String value;
    private final String localVariableName;

    public ASTNonterm(final String value) {
        this(value, "");
    }

    public ASTNonterm(final String value, final String localVariableName) {
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
