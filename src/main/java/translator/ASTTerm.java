package translator;

public final class ASTTerm implements AST {
    private final String value;
    private final String localVariableName;


    public ASTTerm(final String value) {
        this(value, "");
    }

    public ASTTerm(final String value, final String localVariableName) {
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
