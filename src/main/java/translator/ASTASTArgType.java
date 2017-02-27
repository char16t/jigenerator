package translator;

/**
 * AST node argument type.
 * type is 't' if argument is term
 * tyle is 'n' if argument is nonterm
 */
public final class ASTASTArgType implements AST {
    /**
     * type is 't' if argument is term;
     * tyle is 'n' if argument is nonterm.
     */
    private final String type;

    /**
     * Constructor.
     *
     * @param type value of ast node argument type.
     */
    public ASTASTArgType(final String type) {
        this.type = type;
    }
}
