/**
 * Created by user on 1/4/17.
 */
public class Parser {
    Lexer lexer;
    Token currentToken;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        this.currentToken = this.lexer.getNextToken();
    }

    public void error() throws Exception {
        throw new Exception("Invalid syntax");
    }

    public void eat(TokenType tokenType) throws Exception {
        if (this.currentToken.type == tokenType) {
            this.currentToken = this.lexer.getNextToken();
        } else {
            this.error();
        }
    }

    public AST program() throws Exception {
        AST node = this.rule();
        while (this.currentToken.type == TokenType.NONTERM) {
            this.eat(TokenType.NONTERM);
            // TODO: Make and return ast-tree node
        }
        return node;
    }

    public AST rule() throws Exception {
        Token nonterm = this.currentToken;
        this.eat(TokenType.NONTERM);
        this.eat(TokenType.EQ);
        this.or();
        // TODO: Make and return ast-tree node
    }

    public AST or() throws Exception {
        AST node = this.repeat();
        while (this.currentToken.type == TokenType.LINE) {
            Token token = this.currentToken;
            this.eat(TokenType.LINE);
            // TODO: Make and return ast-tree node
        }
        return node;
    }

    public AST repeat() throws Exception {
        if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            this.factor();
            this.eat(TokenType.RPAREN);
            this.eat(TokenType.STAR);
            // TODO: Make and return ast-tree node
        }
        else {
            this.factor();
            // TODO: Make and return ast-tree node
        }
    }

}
