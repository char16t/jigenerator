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
    public Parser(final Lexer lexer) throws Exception {
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
     * @param tokenType Token type
     * @throws Exception If next token and expected are different
     */
    private void eat(final TokenType tokenType) throws Exception {
        if (this.currentToken.type() == tokenType) {
            this.currentToken = this.lexer.getNextToken();
        } else {
            this.error();
        }
    }

    /**
     * Is a 'program' nonterm implementation.
     *
     * @return The 'program' node
     * @throws Exception when something is wrong
     */
    private AST program() throws Exception {
        final List<AST> childs = new LinkedList<>();
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
     * Is a 'astdef' nonterm implementation.
     *
     * @return The 'astdef' node
     * @throws Exception when something is wrong
     */
    private AST astdef() throws Exception {
        final String name = this.currentToken.value();
        this.eat(TokenType.ASTNAME);
        this.eat(TokenType.LPAREN);
        final List<String> types = new LinkedList<>();
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
     * Is a 'rule' nonterm implementation
     *
     * @return The 'rule' node
     * @throws Exception when something is wrong
     */
    private AST rule() throws Exception {
        final String head = this.currentToken.value();
        this.nonterm();
        this.eat(TokenType.EQ);
        final AST exprNode = this.expr();
        this.eat(TokenType.SEMI);
        return new ASTNonermDef(head, exprNode);
    }

    /**
     * Is a 'expr' nonterm implementation
     *
     * @return The 'expr' node
     * @throws Exception when something is wrong
     */
    private AST expr() throws Exception {
        final List<AST> childs = new LinkedList<>();
        final List<AST> childsGroup = new LinkedList<>();
        while (this.currentToken.type() == TokenType.STAR
            || this.currentToken.type() == TokenType.NONTERM
            || this.currentToken.type() == TokenType.TERM
            || this.currentToken.type() == TokenType.LPAREN
            || this.currentToken.type() == TokenType.CALL
            || this.currentToken.type() == TokenType.RET
        ) {
            childsGroup.add(this.expr2());
        }
        childs.add(new ASTExpression(childsGroup));
        while (this.currentToken.type() == TokenType.LINE) {
            this.eat(TokenType.LINE);
            final AST exprNode = this.expr();
            if (exprNode instanceof ASTOr) {
                for (final AST expression : ((ASTOr) exprNode).expressions()) {
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
     * Is a 'expr2' nonterm implementation
     *
     * @return The 'expr2' node
     * @throws Exception when something is wrong
     */
    private AST expr2() throws Exception {
        final List<AST> childs = new LinkedList<>();
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
                final AST firstNode = childs.get(0);
                final ASTOr secondNode = (ASTOr) childs.get(1);
                final List<AST> exprChilds = new LinkedList<>();
                exprChilds.add(firstNode);
                final AST expr = new ASTExpression(exprChilds);
                secondNode.expressions().remove(0);
                secondNode.expressions().add(0, expr);
                childs.remove(0);
            }
            return new ASTRepeat(childs);
        }
        throw new Exception("[expr2] Sorry...");
    }

    /**
     * Is a 'expr3' nonterm implementation
     *
     * @return The 'expr3' node
     * @throws Exception when something is wrong
     */
    private AST expr3() throws Exception {
        if (this.currentToken.type() == TokenType.NONTERM
                || this.currentToken.type() == TokenType.TERM
                || this.currentToken.type() == TokenType.CALL
                || this.currentToken.type() == TokenType.RET) {
            return this.atom();
        } else if (this.currentToken.type() == TokenType.LPAREN) {
            this.eat(TokenType.LPAREN);
            final AST node = this.expr();
            this.eat(TokenType.RPAREN);
            return node;
        }
        throw new Exception("[expr3] Sorry...");
    }

    /**
     * Is a 'atom' nonterm implementation
     *
     * @return The 'atom' node
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
     * Is a 'nonterm' nonterm implementation
     *
     * @return The 'nonterm' node
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
        // @checkstyle AvoidInlineConditionals (3 lines)
        return !localVariableName.equals("")
            ? new ASTNonterm(value, localVariableName)
            : new ASTNonterm(value);
    }

    /**
     * Is a 'call' nonterm implementation
     *
     * @return The 'call' node
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
        // @checkstyle AvoidInlineConditionals (3 lines)
        return !localVariableName.equals("")
            ? new ASTNewNode(value, localVariableName)
            : new ASTNewNode(value);
    }

    /**
     * Is a 'termdef' nonterm implementation
     *
     * @return The 'termdef' node
     * @throws Exception when something is wrong
     */
    private AST termdef() throws Exception {
        final String head = this.currentToken.value();
        this.term();
        this.eat(TokenType.EQ);
        final AST node = this.termexpr();
        this.eat(TokenType.SEMI);
        return new ASTTermDef(head, node);
    }

    /**
     * Is a 'termexpr' nonterm implementation
     *
     * @return The 'termexpr' node
     * @throws Exception when something is wrong
     */
    private AST termexpr() throws Exception {
        final List<AST> childs = new LinkedList<>();
        final List<AST> childsGroup = new LinkedList<>();
        while (
            this.currentToken.type() == TokenType.STAR
            || this.currentToken.type() == TokenType.QUOTED
            || this.currentToken.type() == TokenType.LPAREN
        ) {
            childsGroup.add(this.termexpr2());
        }
        childs.add(new ASTExpression(childsGroup));
        while (this.currentToken.type() == TokenType.LINE) {
            this.eat(TokenType.LINE);
            final AST exprNode = this.termexpr();
            if (exprNode instanceof ASTOr) {
                for (final AST expression : ((ASTOr) exprNode).expressions()) {
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
     * Is a 'termexpr2' nonterm implementation
     *
     * @return The 'termexpr2' node
     * @throws Exception when something is wrong
     */
    private AST termexpr2() throws Exception {
        if (this.currentToken.type() == TokenType.QUOTED
                || this.currentToken.type() == TokenType.LPAREN) {
            return this.termexpr3();
        }
        if (this.currentToken.type() == TokenType.STAR) {
            final List<AST> childs = new LinkedList<>();
            this.eat(TokenType.STAR);
            this.eat(TokenType.LPAREN);
            childs.add(this.termexpr3());
            if (this.currentToken.type() == TokenType.QUOTED
                || this.currentToken.type() == TokenType.LPAREN
                || this.currentToken.type() == TokenType.STAR
                || this.currentToken.type() == TokenType.LINE
            ) {
                childs.add(this.termexpr());
            }
            this.eat(TokenType.RPAREN);
            if (childs.size() == 2 && childs.get(1) instanceof ASTOr) {
                final AST firstNode = childs.get(0);
                final ASTOr secondNode = (ASTOr) childs.get(1);
                final List<AST> exprChilds = new LinkedList<>();
                exprChilds.add(firstNode);
                final AST expr = new ASTExpression(exprChilds);
                secondNode.expressions().remove(0);
                secondNode.expressions().add(0, expr);
                childs.remove(0);
            }
            return new ASTRepeat(childs);
        }
        throw new Exception("[termexpr2] Sorry...");
    }

    /**
     * Is a 'termexpr3' nonterm implementation.
     *
     * @return The 'termexpr3' node
     * @throws Exception when something is wrong
     */
    private AST termexpr3() throws Exception {
        final AST node;
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
     * Is a 'termatom' nonterm implementation.
     *
     * @return The 'termatom' node
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
     * Is a 'term' nonterm implementation
     *
     * @return The 'term' node
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
        // @checkstyle AvoidInlineConditionals (3 lines)
        return !"".equals(localVariableName)
            ? new ASTTerm(value, localVariableName)
            : new ASTTerm(value);
    }

    /**
     * Parse program.
     *
     * @return Root node of ast-tree
     * @throws Exception when something is wrong
     * @// @checkstyle MethodsOrder (3 lines)
     */
    public AST parse() throws Exception {
        AST node = this.program();
        if (this.currentToken.type() != TokenType.EOF) {
            this.error();
        }
        node = this.unionDoubleProductions((ASTProgram) node);
        return node;
    }

    /**
     * Description on russian available below.
     * Поддержка повторяющихся продукций для нетерминалов
     * Эти действия выполняются после построения AST-дерева в Parser:
     * 0. Наткнулись на узел factor, который уже существует
     * 1. Создать узел Or
     * 1-а. Если первый узел в старом Or является Expression, перененести его
     *      в новый создаваемый узел
     * 1-б. Если первый узел в старом Or является Or, перенести все его дочерние
     *      Expression в новый узел
     * 2-а. Если первый узел в новом Or является Expression, добавить его в
     *      новый создаваемый узел
     * 2-б. Если первый узел в новом Or является Or, добавить все его дочерние
     *      Expression в новый узел
     * 3. Добавить новый узел factor и добавить его в Program, старые
     *    использованные удалить.
     *
     * @param node AST-tree node
     * @return AST-tree node
     * @// @checkstyle ExecutableStatementCount (5 lines)
     * @// @checkstyle LocalFinalVariableName (43 lines)
     * @// @checkstyle LocalVariableName (42 lines)
     */
    private AST unionDoubleProductions(final ASTProgram node) {
        final Map<String, List<AST>> nonterms = new HashMap<>();
        final List<String> nontermsForRemove = new LinkedList<>();
        ASTOr orNode;
        for (final AST child : node.childs()) {
            if (child instanceof ASTNonermDef) {
                final String nontermName = ((ASTNonermDef) child).name();
                if (nonterms.containsKey(nontermName)) {
                    nonterms.get(nontermName).add(child);
                } else {
                    // @checkstyle WhitespaceAround (8 lines)
                    // @checkstyle Indentation (7 line)
                    final List<AST> nodes = new LinkedList<AST>() {{
                        add(child);
                    }};
                    nonterms.put(nontermName, nodes);
                }
            }
        }
        for (final String nonterm : nonterms.keySet()) {
            final List<AST> astList = nonterms.get(nonterm);
            if (astList.size() > 1) {
                final List<AST> expressions = new LinkedList<>();
                for (final AST nontermDef : astList) {
                    final AST expr = ((ASTNonermDef) nontermDef).expr();
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
        // @checkstyle MethodBodyComments (1 line)
        // remove nonterms
        for (final String nonterm : nontermsForRemove) {
            node.childs().removeAll(nonterms.get(nonterm));
        }
        return node;
    }
}
