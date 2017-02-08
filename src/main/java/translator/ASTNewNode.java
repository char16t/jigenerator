package translator;

public class ASTNewNode implements AST {
    String value;
    String localVariableName;

    public ASTNewNode(String value) {
        this.value = value;
    }

    public ASTNewNode(String value, String localVariableName) {
        this.value = value;
        this.localVariableName = localVariableName;
    }
}
