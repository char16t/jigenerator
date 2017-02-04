package translator;

public class ASTNonterm implements AST {
    String value;
    String localVariableName;

    public ASTNonterm(String value) {
        this.value = value;
    }

    public ASTNonterm(String value, String localVariableName) {
        this.value = value;
        this.localVariableName = localVariableName;
    }
}
