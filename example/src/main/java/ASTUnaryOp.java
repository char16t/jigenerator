public class ASTUnaryOp implements AST {
    private Token node0;
    private AST node1;

    public ASTUnaryOp(Token node0, AST node1) {
        this.node0 = node0;
        this.node1 = node1;
    }
}
