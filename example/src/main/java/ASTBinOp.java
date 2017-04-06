public class ASTBinOp implements AST {
    private AST node0;
    private Token node1;
    private AST node2;

    public ASTBinOp(AST node0, Token node1, AST node2) {
        this.node0 = node0;
        this.node1 = node1;
        this.node2 = node2;
    }
}
