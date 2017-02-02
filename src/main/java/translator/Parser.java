package translator;

import java.util.LinkedList;
import java.util.List;

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
        List<AST> childs = new LinkedList<AST>();

        String value = "";
        while (this.currentToken.type == TokenType.NONTERM) {
            childs.add(this.rule());
        }

        while (this.currentToken.type == TokenType.TERM) {
            childs.add(this.termdef());
        }

        while (this.currentToken.type == TokenType.ASTNAME) {
            childs.add(this.astdef());
        }


        return new ASTProgram(childs);
    }

    public AST astdef() throws Exception {
        String name = this.currentToken.value;
        this.eat(TokenType.ASTNAME);
        this.eat(TokenType.LPAREN);
        String numChilds = this.currentToken.value;
        this.eat(TokenType.INTEGER);
        this.eat(TokenType.RPAREN);
        this.eat(TokenType.SEMI);
        return new ASTASTDef(name, Integer.parseInt(numChilds));
    }

    public AST rule() throws Exception {
        String head = this.currentToken.value;
        this.eat(TokenType.NONTERM);
        this.eat(TokenType.EQ);
        AST exprNode = this.expr();
        this.eat(TokenType.SEMI);
        return new ASTNonermDef(head, exprNode);
    }

    public AST expr() throws Exception {
        String value = "";
        List<AST> childs = new LinkedList<AST>();
        List<AST> childsGroup = new LinkedList<AST>();

        while (this.currentToken.type == TokenType.STAR ||
                this.currentToken.type == TokenType.NONTERM ||
                this.currentToken.type == TokenType.TERM ||
                this.currentToken.type == TokenType.LPAREN) {

            childsGroup.add(this.expr2());
        }

        childs.add(new ASTExpression(childsGroup));
        while (this.currentToken.type == TokenType.LINE) {
            this.eat(TokenType.LINE);

            AST exprNode = this.expr();
            if (exprNode instanceof ASTOr) {
                for (AST expression : ((ASTOr) exprNode).expressions) {
                    childs.add(expression);
                }
            } else {
                childs.add(exprNode);
            }
        }
        if (childs.size() > 1 || childs.size() == 0) {
            return new ASTOr(childs);
        } else {
            return childs.get(0);
        }
    }

    public AST expr2() throws Exception {
        String value = "";
        String subvalue = "";
        List<AST> childs = new LinkedList<AST>();

        if (this.currentToken.type == TokenType.NONTERM ||
                this.currentToken.type == TokenType.TERM ||
                this.currentToken.type == TokenType.LPAREN) {
            return this.expr3();
        }
        if (this.currentToken.type == TokenType.STAR) {
            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            childs.add(this.expr3());
            if (this.currentToken.type == TokenType.NONTERM ||
                    this.currentToken.type == TokenType.TERM ||
                    this.currentToken.type == TokenType.LPAREN ||
                    this.currentToken.type == TokenType.STAR ||
                    this.currentToken.type == TokenType.LINE) {
                childs.add(this.expr());
            }
            this.eat(TokenType.RPAREN);

            if (childs.size() == 2 && childs.get(1) instanceof ASTOr) {
                AST firstNode = childs.get(0);
                ASTOr secondNode = (ASTOr) childs.get(1);

                List<AST> exprChilds = new LinkedList<AST>();
                exprChilds.add(firstNode);
                AST expr = new ASTExpression(exprChilds);
                secondNode.expressions.remove(0);
                secondNode.expressions.add(0, expr);
                childs.remove(0);
            }

            return new ASTRepeat(childs);
        }

        throw new Exception("[expr2] Sorry...");
    }

    public AST expr3() throws Exception {
        String value = "";
        if (this.currentToken.type == TokenType.NONTERM || this.currentToken.type == TokenType.TERM) {
            return this.atom();
        } else if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            AST node = this.expr();
            this.eat(TokenType.RPAREN);
            return node;
        }

        throw new Exception("[expr3] Sorry...");
    }

    public AST atom() throws Exception {
        String value = "";
        if (this.currentToken.type == TokenType.NONTERM) {
            value += this.currentToken.value;
            this.eat(TokenType.NONTERM);
            return new ASTNonterm(value);
        }

        if (this.currentToken.type == TokenType.TERM) {
            value += this.currentToken.value;
            this.eat(TokenType.TERM);
            return new ASTTerm(value);
        }

        throw new Exception("[atom] Sorry...");
    }

    public AST termdef() throws Exception {
        String head = this.currentToken.value;
        this.eat(TokenType.TERM);
        this.eat(TokenType.EQ);
        AST node = this.termexpr();
        this.eat(TokenType.SEMI);
        return new ASTTermDef(head, node);
    }

    public AST termexpr() throws Exception {
        List<AST> childs = new LinkedList<AST>();
        List<AST> childsGroup = new LinkedList<AST>();

        String value = "";
        while (this.currentToken.type == TokenType.STAR ||
                this.currentToken.type == TokenType.QUOTED ||
                this.currentToken.type == TokenType.LPAREN) {
            childsGroup.add(this.termexpr2());
        }

        childs.add(new ASTExpression(childsGroup));
        while (this.currentToken.type == TokenType.LINE) {
            this.eat(TokenType.LINE);
            AST exprNode = this.termexpr();
            if (exprNode instanceof ASTOr) {
                for (AST expression : ((ASTOr) exprNode).expressions) {
                    childs.add(expression);
                }
            } else {
                childs.add(exprNode);
            }
        }

        if (childs.size() > 1 || childs.size() == 0) {
            return new ASTOr(childs);
        } else {
            return childs.get(0);
        }
    }

    public AST termexpr2() throws Exception {
        String value = "";
        String subvalue = "";

        if (this.currentToken.type == TokenType.QUOTED ||
                this.currentToken.type == TokenType.LPAREN) {
            return this.termexpr3();
        }
        if (this.currentToken.type == TokenType.STAR) {
            List<AST> childs = new LinkedList<AST>();

            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            childs.add(this.termexpr3());
            if (this.currentToken.type == TokenType.QUOTED ||
                    this.currentToken.type == TokenType.LPAREN ||
                    this.currentToken.type == TokenType.STAR ||
                    this.currentToken.type == TokenType.LINE) {
                childs.add(this.termexpr());
            }
            this.eat(TokenType.RPAREN);

            if (childs.size() == 2 && childs.get(1) instanceof ASTOr) {
                AST firstNode = childs.get(0);
                ASTOr secondNode = (ASTOr) childs.get(1);

                List<AST> exprChilds = new LinkedList<AST>();
                exprChilds.add(firstNode);
                AST expr = new ASTExpression(exprChilds);
                secondNode.expressions.remove(0);
                secondNode.expressions.add(0, expr);
                childs.remove(0);
            }

            return new ASTRepeat(childs);
        }
        throw new Exception("[termexpr2] Sorry...");
    }

    public AST termexpr3() throws Exception {
        String value = "";

        AST node;
        if (this.currentToken.type == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            node = this.termexpr();
            this.eat(TokenType.RPAREN);
            return node;
        } else {
            return this.termatom();
        }

    }

    public AST termatom() throws Exception {
        String value = "";

        if (this.currentToken.type == TokenType.QUOTED) {
            value += this.currentToken.value;
            this.eat(TokenType.QUOTED);
        }

        return new ASTQuoted(value);
    }

    public AST parse() throws Exception {
        AST node = this.program();
        if (this.currentToken.type != TokenType.EOF) {
            this.error();
        }
        return node;
    }

}
