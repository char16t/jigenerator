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
}
