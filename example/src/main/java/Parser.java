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

    public AST parse() throws Exception {
        AST node = this.expr();
        if (this.currentToken.type != TokenType.EOF) {
            this.error();
        }
        return node;
    }

    public AST expr() throws Exception {
        Token b = null;
        AST a = null;
        AST c = null;
        a = this.term();
        while (this.currentToken.type == TokenType.PLUS || this.currentToken.type == TokenType.MINUS) {
            if (this.currentToken.type == TokenType.PLUS) {
                b = this.currentToken;
                this.eat(TokenType.PLUS);

            } else if (this.currentToken.type == TokenType.MINUS) {
                b = this.currentToken;
                this.eat(TokenType.MINUS);

            } else {
                this.error();
                return null;
            }
            c = this.term();
            a = new ASTBinOp(a, b, c);

        }
        return a;

    }

    public AST term() throws Exception {
        Token e = null;
        AST d = null;
        AST f = null;
        d = this.factor();
        while (this.currentToken.type == TokenType.DIV || this.currentToken.type == TokenType.MUL) {
            if (this.currentToken.type == TokenType.MUL) {
                e = this.currentToken;
                this.eat(TokenType.MUL);

            } else if (this.currentToken.type == TokenType.DIV) {
                e = this.currentToken;
                this.eat(TokenType.DIV);

            } else {
                this.error();
                return null;
            }
            f = this.factor();
            d = new ASTBinOp(d, e, f);

        }
        return d;

    }

    public AST factor() throws Exception {
        Token a = null;
        Token d = null;
        AST b = null;
        AST c = null;
        AST e = null;
        AST f = null;
        if (this.currentToken.type == TokenType.PLUS || this.currentToken.type == TokenType.MINUS) {
            if (this.currentToken.type == TokenType.PLUS) {
                a = this.currentToken;
                this.eat(TokenType.PLUS);

            } else if (this.currentToken.type == TokenType.MINUS) {
                a = this.currentToken;
                this.eat(TokenType.MINUS);

            } else {
                this.error();
                return null;
            }
            b = this.factor();
            c = new ASTUnaryOp(a, b);
            return c;

        } else if (this.currentToken.type == TokenType.INTEGER) {
            d = this.currentToken;
            this.eat(TokenType.INTEGER);
            e = new ASTNum(d);
            return e;

        } else if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            f = this.expr();
            this.eat(TokenType.RPAREN);
            return f;

        } else {
            this.error();
            return null;
        }
    }

}