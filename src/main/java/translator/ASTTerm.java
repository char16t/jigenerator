package translator;

public class ASTTerm implements AST {
    private String value;
    private String localVariableName;


    public ASTTerm(final String value) {
        this.value = value;
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
