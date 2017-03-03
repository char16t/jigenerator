/**
 * MIT License
 *
 * Copyright (c) 2017 Valeriy Manenkov and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package translator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Parser.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class Parser {

    /**
     * Lexer.
     */
    private final Lexer lexer;

    /**
     * Current token.
     */
    private Token currentToken;

    /**
     * Ctor.
     *
     * @param lexer Lexer
     * @throws Exception when cannot read the next token
     */
    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        this.currentToken = this.lexer.getNextToken();
    }

    /**
     * Error, call it when something wrong.
     *
     * @throws Exception always
     */
    private void error() throws Exception {
        throw new Exception("Invalid syntax");
    }

    /**
     * Eat next token.
     *
     * @param tokenType
     * @throws Exception If next token and expected are different
     */
    private void eat(TokenType tokenType) throws Exception {
        if (this.currentToken.type() == tokenType) {
            this.currentToken = this.lexer.getNextToken();
        } else {
            this.error();
        }
    }

    /**
     * 'program' nonterm implementation
     *
     * @return 'program' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'astdef' nonterm implementation
     *
     * @return 'astdef' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'rule' nonterm implementation
     *
     * @return 'rule' node
     * @throws Exception when something is wrong
     */
    private AST rule() throws Exception {
        String head = this.currentToken.value();
        this.nonterm();
        this.eat(TokenType.EQ);
        AST exprNode = this.expr();
        this.eat(TokenType.SEMI);
        return new ASTNonermDef(head, exprNode);
    }

    /**
     * 'expr' nonterm implementation
     *
     * @return 'expr' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'expr2' nonterm implementation
     *
     * @return 'expr2' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'expr3' nonterm implementation
     *
     * @return 'expr3' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'atom' nonterm implementation
     *
     * @return 'atom' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'nonterm' nonterm implementation
     *
     * @return 'nonterm' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'call' nonterm implementation
     *
     * @return 'call' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'termdef' nonterm implementation
     *
     * @return 'termdef' node
     * @throws Exception when something is wrong
     */
    private AST termdef() throws Exception {
        String head = this.currentToken.value();
        this.term();
        this.eat(TokenType.EQ);
        AST node = this.termexpr();
        this.eat(TokenType.SEMI);
        return new ASTTermDef(head, node);
    }

    /**
     * 'termexpr' nonterm implementation
     *
     * @return 'termexpr' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'termexpr2' nonterm implementation
     *
     * @return 'termexpr2' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'termexpr3' nonterm implementation
     *
     * @return 'termexpr3' node
     * @throws Exception when something is wrong
     */
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

    /**
     * 'termatom' nonterm implementation
     *
     * @return 'termatom' node
     * @throws Exception when something is wrong
     */
    private AST termatom() throws Exception {
        String value = "";

        if (this.currentToken.type() == TokenType.QUOTED) {
            value += this.currentToken.value();
            this.eat(TokenType.QUOTED);
        }

        return new ASTQuoted(value);
    }

    /**
     * 'term' nonterm implementation
     *
     * @return 'term' node
     * @throws Exception when something is wrong
     */
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

    /**
     * Parse program.
     *
     * @return root node of ast-tree
     * @throws Exception when something is wrong
     */
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
