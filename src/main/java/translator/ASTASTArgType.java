package translator;

/**
 * AST node argument type.
 * type is 't' if argument is term
 * tyle is 'n' if argument is nonterm
 */
public class ASTASTArgType implements AST {
    /**
     * type is 't' if argument is term;
     * tyle is 'n' if argument is nonterm.
     */
    private String type;

    /**
     * Constructor.
     *
     * @param type value of ast node argument type.
     */
    public ASTASTArgType(final String type) {
        this.type = type;
    }
}
