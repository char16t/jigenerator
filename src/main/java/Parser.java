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
        String value = "";
        while (this.currentToken.type == TokenType.NONTERM) {
            AST node = this.rule();
            value += " " + node.value;
        }
        return new AST(value);
    }

    public AST rule() throws Exception {
        this.eat(TokenType.NONTERM);
        this.eat(TokenType.EQ);
        AST exprNode = this.expr();
        this.eat(TokenType.SEMI);
        String value = "NONTERM := " + exprNode.value + "\n";
        return new AST(value);
    }

    public AST expr() throws Exception {
        String value = "";
        while (this.currentToken.type == TokenType.STAR ||
                this.currentToken.type == TokenType.NONTERM ||
                this.currentToken.type == TokenType.TERM ||
                this.currentToken.type == TokenType.LPAREN) {
            AST node = this.expr2();
            value += node.value;
        }

        while (this.currentToken.type == TokenType.LINE) {
            this.eat(TokenType.LINE);
            AST node = this.expr();
            value += " | " + node.value;
        }
        return new AST(value);
    }

    public AST expr2() throws Exception {
        String value = "";
        String subvalue = "";

        if (this.currentToken.type == TokenType.NONTERM ||
                this.currentToken.type == TokenType.TERM ||
                this.currentToken.type == TokenType.LPAREN) {
            AST expr3Node = this.expr3();
            value += expr3Node.value;
        }
        if (this.currentToken.type == TokenType.STAR) {
            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            AST expr3Node = this.expr3();
            // TODO: Dirty hack, but it works! Need to fix grammar rule
            if (this.currentToken.type == TokenType.NONTERM ||
                    this.currentToken.type == TokenType.TERM ||
                    this.currentToken.type == TokenType.LPAREN ||
                    this.currentToken.type == TokenType.STAR ||
                    this.currentToken.type == TokenType.LINE) {
                AST t = this.expr();
                subvalue += t.value;
            }
            this.eat(TokenType.RPAREN);
            value += "*(" + expr3Node.value + " " + subvalue + ") ";
        }

        return new AST(value);
    }

    public AST expr3() throws Exception {
        String value = "";
        if (this.currentToken.type == TokenType.NONTERM || this.currentToken.type == TokenType.TERM) {
            AST atomNode = this.atom();
            value += atomNode.value;
        }
        else if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            AST exprNode = this.expr();
            this.eat(TokenType.RPAREN);
            value += "(" + exprNode.value + ")";
        }

        AST node = new AST(value);
        return node;
    }

    public AST atom() throws Exception {
        String value = "";
        if (this.currentToken.type == TokenType.NONTERM) {
            value += this.currentToken.value;
            this.eat(TokenType.NONTERM);
        }

        if (this.currentToken.type == TokenType.TERM) {
            value += this.currentToken.value;
            this.eat(TokenType.TERM);
        }

        return new AST(value);
    }

    public AST parse() throws Exception {
        AST node = this.program();
        if (this.currentToken.type != TokenType.EOF) {
            this.error();
        }
        return node;
    }

}
