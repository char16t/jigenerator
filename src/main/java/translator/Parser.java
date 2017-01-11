package translator;

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
            ASTOther node = (ASTOther) this.rule();
            value += " " + node.value;
        }

        while (this.currentToken.type == TokenType.TERM) {
            ASTOther node = (ASTOther) this.termdef();
            value += " " + node.value;
        }

        return new ASTOther(value);
    }

    public AST rule() throws Exception {
        this.eat(TokenType.NONTERM);
        this.eat(TokenType.EQ);
        ASTOther exprNode = (ASTOther) this.expr();
        this.eat(TokenType.SEMI);
        String value = "NONTERM := " + exprNode.value + "\n";
        return new ASTOther(value);
    }

    public AST expr() throws Exception {
        String value = "";
        while (this.currentToken.type == TokenType.STAR ||
                this.currentToken.type == TokenType.NONTERM ||
                this.currentToken.type == TokenType.TERM ||
                this.currentToken.type == TokenType.LPAREN) {
            ASTOther node = (ASTOther) this.expr2();
            value += node.value;
        }

        while (this.currentToken.type == TokenType.LINE) {
            this.eat(TokenType.LINE);
            ASTOther node = (ASTOther) this.expr();
            value += " | " + node.value;
        }
        return new ASTOther(value);
    }

    public AST expr2() throws Exception {
        String value = "";
        String subvalue = "";

        if (this.currentToken.type == TokenType.NONTERM ||
                this.currentToken.type == TokenType.TERM ||
                this.currentToken.type == TokenType.LPAREN) {
            ASTOther expr3Node = (ASTOther) this.expr3();
            value += expr3Node.value;
        }
        if (this.currentToken.type == TokenType.STAR) {
            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            ASTOther expr3Node = (ASTOther) this.expr3();
            if (this.currentToken.type == TokenType.NONTERM ||
                    this.currentToken.type == TokenType.TERM ||
                    this.currentToken.type == TokenType.LPAREN ||
                    this.currentToken.type == TokenType.STAR ||
                    this.currentToken.type == TokenType.LINE) {
                ASTOther t = (ASTOther) this.expr();
                subvalue += t.value;
            }
            this.eat(TokenType.RPAREN);
            value += "*(" + expr3Node.value + " " + subvalue + ") ";
        }

        return new ASTOther(value);
    }

    public AST expr3() throws Exception {
        String value = "";
        if (this.currentToken.type == TokenType.NONTERM || this.currentToken.type == TokenType.TERM) {
            ASTOther atomNode = (ASTOther) this.atom();
            value += atomNode.value;
        }
        else if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            ASTOther exprNode = (ASTOther) this.expr();
            this.eat(TokenType.RPAREN);
            value += "(" + exprNode.value + ")";
        }

        AST node = new ASTOther(value);
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

        return new ASTOther(value);
    }

    public AST termdef() throws Exception {
        this.eat(TokenType.TERM);
        this.eat(TokenType.EQ);
        ASTOther exprNode = (ASTOther) this.termexpr();
        this.eat(TokenType.SEMI);
        String value = "TERM := " + exprNode.value + "\n";
        return new ASTOther(value);
    }

    public AST termexpr() throws Exception {
        String value = "";
        while (this.currentToken.type == TokenType.STAR ||
                this.currentToken.type == TokenType.QUOTED ||
                this.currentToken.type == TokenType.LPAREN) {
            ASTOther node = (ASTOther) this.termexpr2();
            value += node.value;
        }

        while (this.currentToken.type == TokenType.LINE) {
            this.eat(TokenType.LINE);
            ASTOther node = (ASTOther) this.termexpr();
            value += " | " + node.value;
        }
        return new ASTOther(value);
    }

    public AST termexpr2() throws Exception {
        String value = "";
        String subvalue = "";

        if (this.currentToken.type == TokenType.QUOTED ||
                this.currentToken.type == TokenType.LPAREN) {
            ASTOther expr3Node = (ASTOther) this.termexpr3();
            value += expr3Node.value;
        }
        if (this.currentToken.type == TokenType.STAR) {
            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            ASTOther expr3Node = (ASTOther) this.termexpr3();
            if (this.currentToken.type == TokenType.QUOTED ||
                    this.currentToken.type == TokenType.LPAREN ||
                    this.currentToken.type == TokenType.STAR ||
                    this.currentToken.type == TokenType.LINE) {
                ASTOther t = (ASTOther) this.termexpr();
                subvalue += t.value;
            }
            this.eat(TokenType.RPAREN);
            value += "*(" + expr3Node.value + " " + subvalue + ") ";
        }

        return new ASTOther(value);
    }

    public AST termexpr3() throws Exception {
        String value = "";

        ASTNewNode node;
        if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            node = (ASTNewNode) this.termexpr();
            this.eat(TokenType.RPAREN);
            value += "(" + node.value + ")";
        } else {
            node = (ASTNewNode) this.termatom();
            value += node.value;
        }

        AST retNode = new ASTNewNode(value, node);
        return retNode;
    }

    public AST termatom() throws Exception {
        String value = "";

        if (this.currentToken.type == TokenType.QUOTED) {
            value += this.currentToken.value;
            this.eat(TokenType.QUOTED);
        }

        return new ASTNewNode(value);
    }

    public AST parse() throws Exception {
        AST node = this.program();
        if (this.currentToken.type != TokenType.EOF) {
            this.error();
        }
        return node;
    }

}
