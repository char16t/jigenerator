package translator;

public class ASTNonterm implements AST {
    private String value;
    private String localVariableName;

    public ASTNonterm(final String value) {
        this.value = value;
    }

    public ASTNonterm(final String value, final String localVariableName) {
        this.value = value;
        this.localVariableName = localVariableName;
    }
}
