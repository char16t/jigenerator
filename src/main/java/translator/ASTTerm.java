package translator;

public class ASTTerm implements AST {
    String value;
    String localVariableName;


    public ASTTerm(String value) {
        this.value = value;
    }

    public ASTTerm(String value, String localVariableName) {
        this.value = value;
        this.localVariableName = localVariableName;
    }
}
