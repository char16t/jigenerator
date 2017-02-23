package translator;

public class ASTNewNode implements AST {
    private String value;
    private String localVariableName;

    public ASTNewNode(final String value) {
        this.value = value;
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
