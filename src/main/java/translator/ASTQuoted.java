package translator;

public class ASTQuoted implements AST {
    String value;

    public ASTQuoted(String value) {
        this.value = value;
    }
}
