package translator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        this.currentToken = this.lexer.getNextToken();
    }

    private void error() throws Exception {
        throw new Exception("Invalid syntax");
    }

    private void eat(TokenType tokenType) throws Exception {
        if (this.currentToken.type() == tokenType) {
            this.currentToken = this.lexer.getNextToken();
        } else {
            this.error();
        }
    }

    private AST program() throws Exception {
        List<AST> childs = new LinkedList<AST>();

        String value = "";
        while (this.currentToken.type() == TokenType.NONTERM) {
            childs.add(this.rule());
        }

        while (this.currentToken.type() == TokenType.TERM) {
            childs.add(this.termdef());
        }

        while (this.currentToken.type() == TokenType.ASTNAME) {
            childs.add(this.astdef());
        }


        return new ASTProgram(childs);
    }

    private AST astdef() throws Exception {
        String name = this.currentToken.value();
        this.eat(TokenType.ASTNAME);
        this.eat(TokenType.LPAREN);

        List<String> types = new LinkedList<String>();
        String type = this.currentToken.value();
        this.eat(TokenType.ASTARGTYPE);
        types.add(type);
        while (this.currentToken.type() == TokenType.COMMA) {
            this.eat(TokenType.COMMA);
            type = this.currentToken.value();
            this.eat(TokenType.ASTARGTYPE);
            types.add(type);
        }

        this.eat(TokenType.RPAREN);
        this.eat(TokenType.SEMI);
        return new ASTASTDef(name, types);
    }

    private AST rule() throws Exception {
        String head = this.currentToken.value();
        this.nonterm();
        this.eat(TokenType.EQ);
        AST exprNode = this.expr();
        this.eat(TokenType.SEMI);
        return new ASTNonermDef(head, exprNode);
    }

    private AST expr() throws Exception {
        String value = "";
        List<AST> childs = new LinkedList<AST>();
        List<AST> childsGroup = new LinkedList<AST>();

        while (this.currentToken.type() == TokenType.STAR
                || this.currentToken.type() == TokenType.NONTERM
                || this.currentToken.type() == TokenType.TERM
                || this.currentToken.type() == TokenType.LPAREN
                || this.currentToken.type() == TokenType.CALL
                || this.currentToken.type() == TokenType.RET) {

            childsGroup.add(this.expr2());
        }

        childs.add(new ASTExpression(childsGroup));
        while (this.currentToken.type() == TokenType.LINE) {
            this.eat(TokenType.LINE);

            AST exprNode = this.expr();
            if (exprNode instanceof ASTOr) {
                for (AST expression : ((ASTOr) exprNode).expressions()) {
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

    private AST expr2() throws Exception {
        String value = "";
        String subvalue = "";
        List<AST> childs = new LinkedList<AST>();

        if (this.currentToken.type() == TokenType.NONTERM
                || this.currentToken.type() == TokenType.TERM
                || this.currentToken.type() == TokenType.CALL
                || this.currentToken.type() == TokenType.RET
                || this.currentToken.type() == TokenType.LPAREN) {
            return this.expr3();
        }
        if (this.currentToken.type() == TokenType.STAR) {
            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            childs.add(this.expr3());
            if (this.currentToken.type() == TokenType.NONTERM
                    || this.currentToken.type() == TokenType.TERM
                    || this.currentToken.type() == TokenType.CALL
                    || this.currentToken.type() == TokenType.RET
                    || this.currentToken.type() == TokenType.LPAREN
                    || this.currentToken.type() == TokenType.STAR
                    || this.currentToken.type() == TokenType.LINE) {
                childs.add(this.expr());
            }
            this.eat(TokenType.RPAREN);

            if (childs.size() == 2 && childs.get(1) instanceof ASTOr) {
                AST firstNode = childs.get(0);
                ASTOr secondNode = (ASTOr) childs.get(1);

                List<AST> exprChilds = new LinkedList<AST>();
                exprChilds.add(firstNode);
                AST expr = new ASTExpression(exprChilds);
                secondNode.expressions().remove(0);
                secondNode.expressions().add(0, expr);
                childs.remove(0);
            }

            return new ASTRepeat(childs);
        }

        throw new Exception("[expr2] Sorry...");
    }

    private AST expr3() throws Exception {
        String value = "";
        if (this.currentToken.type() == TokenType.NONTERM
                || this.currentToken.type() == TokenType.TERM
                || this.currentToken.type() == TokenType.CALL
                || this.currentToken.type() == TokenType.RET) {
            return this.atom();
        } else if (this.currentToken.type() == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            AST node = this.expr();
            this.eat(TokenType.RPAREN);
            return node;
        }

        throw new Exception("[expr3] Sorry...");
    }

    private AST atom() throws Exception {
        String value = "";
        if (this.currentToken.type() == TokenType.NONTERM) {
            return this.nonterm();
        }

        if (this.currentToken.type() == TokenType.TERM) {
            return this.term();
        }

        if (this.currentToken.type() == TokenType.CALL) {
            return this.call();
        }

        if (this.currentToken.type() == TokenType.RET) {
            value += this.currentToken.value();
            this.eat(TokenType.RET);
            return new ASTReturn(value);
        }

        throw new Exception("[atom] Sorry...");
    }

    private AST nonterm() throws Exception {
        String value = "";
        String localVariableName = "";

        if (this.currentToken.type() == TokenType.NONTERM) {
            value += this.currentToken.value();
            this.eat(TokenType.NONTERM);
        }

        if (this.currentToken.type() == TokenType.NAME) {
            localVariableName += this.currentToken.value();
            this.eat(TokenType.NAME);
        }

        return !localVariableName.equals("") ? new ASTNonterm(value, localVariableName) : new ASTNonterm(value);
    }

    private AST call() throws Exception {
        String value = "";
        String localVariableName = "";

        if (this.currentToken.type() == TokenType.CALL) {
            value += this.currentToken.value();
            this.eat(TokenType.CALL);
        }

        if (this.currentToken.type() == TokenType.NAME) {
            localVariableName += this.currentToken.value();
            this.eat(TokenType.NAME);
        }

        return !localVariableName.equals("")
                ? new ASTNewNode(value, localVariableName)
                : new ASTNewNode(value);
    }

    private AST termdef() throws Exception {
        String head = this.currentToken.value();
        this.term();
        this.eat(TokenType.EQ);
        AST node = this.termexpr();
        this.eat(TokenType.SEMI);
        return new ASTTermDef(head, node);
    }

    private AST termexpr() throws Exception {
        List<AST> childs = new LinkedList<AST>();
        List<AST> childsGroup = new LinkedList<AST>();

        String value = "";
        while (this.currentToken.type() == TokenType.STAR
                || this.currentToken.type() == TokenType.QUOTED
                || this.currentToken.type() == TokenType.LPAREN) {
            childsGroup.add(this.termexpr2());
        }

        childs.add(new ASTExpression(childsGroup));
        while (this.currentToken.type() == TokenType.LINE) {
            this.eat(TokenType.LINE);
            AST exprNode = this.termexpr();
            if (exprNode instanceof ASTOr) {
                for (AST expression : ((ASTOr) exprNode).expressions()) {
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

    private AST termexpr2() throws Exception {
        String value = "";
        String subvalue = "";

        if (this.currentToken.type() == TokenType.QUOTED
                || this.currentToken.type() == TokenType.LPAREN) {
            return this.termexpr3();
        }
        if (this.currentToken.type() == TokenType.STAR) {
            List<AST> childs = new LinkedList<AST>();

            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            childs.add(this.termexpr3());
            if (this.currentToken.type() == TokenType.QUOTED
                    || this.currentToken.type() == TokenType.LPAREN
                    || this.currentToken.type() == TokenType.STAR
                    || this.currentToken.type() == TokenType.LINE) {
                childs.add(this.termexpr());
            }
            this.eat(TokenType.RPAREN);

            if (childs.size() == 2 && childs.get(1) instanceof ASTOr) {
                AST firstNode = childs.get(0);
                ASTOr secondNode = (ASTOr) childs.get(1);

                List<AST> exprChilds = new LinkedList<AST>();
                exprChilds.add(firstNode);
                AST expr = new ASTExpression(exprChilds);
                secondNode.expressions().remove(0);
                secondNode.expressions().add(0, expr);
                childs.remove(0);
            }

            return new ASTRepeat(childs);
        }
        throw new Exception("[termexpr2] Sorry...");
    }

    private AST termexpr3() throws Exception {
        String value = "";

        AST node;
        if (this.currentToken.type() == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            node = this.termexpr();
            this.eat(TokenType.RPAREN);
            return node;
        } else {
            return this.termatom();
        }

    }

    private AST termatom() throws Exception {
        String value = "";

        if (this.currentToken.type() == TokenType.QUOTED) {
            value += this.currentToken.value();
            this.eat(TokenType.QUOTED);
        }

        return new ASTQuoted(value);
    }

    private AST term() throws Exception {
        String value = "";
        String localVariableName = "";

        if (this.currentToken.type() == TokenType.TERM) {
            value += this.currentToken.value();
            this.eat(TokenType.TERM);
        }

        if (this.currentToken.type() == TokenType.NAME) {
            localVariableName += this.currentToken.value();
            this.eat(TokenType.NAME);
        }

        return !localVariableName.equals("")
                ? new ASTTerm(value, localVariableName)
                : new ASTTerm(value);
    }

    public AST parse() throws Exception {
        AST node = this.program();
        if (this.currentToken.type() != TokenType.EOF) {
            this.error();
        }
        node = unionDoubleProductions((ASTProgram) node);
        return node;
    }

    /*
     * Поддержка повторяющихся продукций для нетерминалов
     * Эти действия выполняются после построения AST-дерева в Parser:
     * 0. Наткнулись на узел factor, который уже существует
     * 1. Создать узел Or
     * 1-а. Если первый узел в старом Or является Expression, перененести его в новый создаваемый узел
     * 1-б. Если первый узел в старом Or является Or, перенести все его дочерние Expression в новый узел
     * 2-а. Если первый узел в новом Or является Expression, добавить его в новый создаваемый узел
     * 2-б. Если первый узел в новом Or является Or, добавить все его дочерние Expression в новый узел
     * 3. Добавить новый узел factor и добавить его в Program, старые использованные удалить
    **/
    private AST unionDoubleProductions(final ASTProgram node) {
        Map<String, List<AST>> nonterms = new HashMap<String, List<AST>>();
        List<String> nontermsForRemove = new LinkedList<String>();
        ASTOr orNode = null;

        for (final AST child : node.childs()) {
            if (child instanceof ASTNonermDef) {
                String nontermName = ((ASTNonermDef) child).name();
                if (nonterms.containsKey(nontermName)) {
                    nonterms.get(nontermName).add(child);
                } else {
                    List<AST> nodes = new LinkedList<AST>() {{
                        add(child);
                    }};
                    nonterms.put(nontermName, nodes);
                }
            }
        }

        for (final String nonterm : nonterms.keySet()) {
            List<AST> astList = nonterms.get(nonterm);
            if (astList.size() > 1) {
                List<AST> expressions = new LinkedList<AST>();
                for (AST nontermDef : astList) {
                    AST expr = ((ASTNonermDef) nontermDef).expr();
                    if (expr instanceof ASTExpression) {
                        expressions.add(expr);
                    } else if (expr instanceof ASTOr) {
                        expressions.addAll(((ASTOr) expr).expressions());
                    }
                }
                orNode = new ASTOr(expressions);
                nontermsForRemove.add(nonterm);
                node.childs().add(new ASTNonermDef(nonterm, orNode));
            }
        }

        // remove nonterms
        for (String nontermForRemove : nontermsForRemove) {
            node.childs().removeAll(nonterms.get(nontermForRemove));
        }

        return node;
    }
}
