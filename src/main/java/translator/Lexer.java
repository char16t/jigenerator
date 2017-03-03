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

/**
 * Lexer. Converts raw text to tokens
 */
public final class Lexer {
    /**
     * The input text.
     */
    private final String text;

    /**
     * Current position in input text.
     */
    private Integer pos;

    /**
     * Current character in input text.
     */
    private Character currentChar;

    /**
     * Constructor.
     *
     * @param text input text.
     */
    public Lexer(final String text) {
        this.text = text;
        this.pos = 0;
        this.currentChar = this.text.charAt(this.pos);
    }

    /**
     * Getter.
     *
     * @return input text
     */
    public String text() {
        return text;
    }


    /**
     * Getter.
     *
     * @return current position in input text
     */
    public Integer getPos() {
        return pos;
    }

    /**
     * Setter.
     *
     * @param pos current position in input text
     */
    public void setPos(final Integer pos) {
        this.pos = pos;
    }

    /**
     * Getter.
     *
     * @return current character in input text
     */
    public Character getCurrentChar() {
        return currentChar;
    }

    /**
     * Setter.
     *
     * @param currentChar current character in input text
     */
    public void setCurrentChar(final Character currentChar) {
        this.currentChar = currentChar;
    }

    /**
     * @throws Exception throws exception when error occurs lexical analysis
     */
    private void error() throws Exception {
        throw new Exception("Invalid character");
    }

    /**
     * Advance the 'pos' pointer and set the 'currentChar' field.
     */
    private void advance() {
        this.pos += 1;
        if (this.pos > this.text.length() - 1) {
            this.currentChar = null;
        } else {
            this.currentChar = this.text.charAt(this.pos);
        }
    }

    /**
     * Advance the 'pos' pointer and set the 'currentChar' field while current
     * char is a whitespace symbol.
     */
    private void skipWhitespace() {
        while (this.currentChar != null
                && Character.isWhitespace(this.currentChar)) {
            this.advance();
        }
    }

    /**
     * @return the term consumed from the input.
     */
    private String term() {
        String result = "";
        while (this.currentChar != null
                && Character.isUpperCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    /**
     * @return the nonterm consumed from the input.
     */
    private String nonterm() {
        String result = "";
        while (this.currentChar != null
                && Character.isLowerCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    /**
     * @return symbol sequence ':='
     * @throws Exception when failed to find a sequence of characters ':=' from
     *                   the current position
     */
    private String eq() throws Exception {
        String result = "";
        if (this.currentChar.equals(':')) {
            this.advance();
            if (this.currentChar.equals('=')) {
                this.advance();
                return ":=";
            }
        }
        error();
        return result;
    }

    /**
     * @return left parenthesis character
     * @throws Exception when failed to find a left parenthesis character from
     *                   the current position
     */
    private String lparen() throws Exception {
        if (this.currentChar.equals('(')) {
            return "(";
        }
        error();
        return null;
    }

    /**
     * @return right parenthesis character
     * @throws Exception when failed to find a right parenthesis character from
     *                   the current position
     */
    private String rparen() throws Exception {
        if (this.currentChar.equals(')')) {
            return ")";
        }
        error();
        return null;
    }

    /**
     * @return strt character
     * @throws Exception when failed to find a star character from the current
     *                   position
     */
    private String star() throws Exception {
        if (this.currentChar.equals('*')) {
            return "*";
        }
        error();
        return null;
    }

    /**
     * @return quoted symbol sequence
     * @throws Exception when failed to find a quoted character sequence from
     *                   the current position
     */
    private String quoted() throws Exception {
        String result = "";

        if (this.currentChar != '\'') {
            this.error();
        }
        this.advance();

        while (this.currentChar != '\'') {
            result += this.currentChar.toString();
            this.advance();
        }
        this.advance();

        return result;
    }

    /**
     * @return name of ast-tree node
     */
    private String astname() {
        String result = "";
        if (this.currentChar == '@') {
            this.advance();
        }
        while (this.currentChar != null
                && (Character.isLowerCase(this.currentChar)
                || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    /**
     * @return the name of the field node of the AST tree
     */
    private String astargtype() {
        String result = "";
        if (this.currentChar == '%') {
            this.advance();
        }
        if (this.currentChar == 't' || this.currentChar == 'n') {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    /**
     * @return the name of the variable the return value
     */
    private String ret() {
        String result = "";
        if (this.currentChar == '$') {
            this.advance();
        }
        while (this.currentChar != null
                && (Character.isLowerCase(this.currentChar)
                || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    // TODO: need for review grammars

    /**
     * @return part of the original Java code, which creates a new node ADT-tree
     */
    private String call() {
        String result = "";
        if (this.currentChar == '#') {
            this.advance();
        }
        while (this.currentChar != null
                && (Character.isLowerCase(this.currentChar)
                || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        if (this.currentChar == '(') {
            result += '(';
            this.advance();
        }

        while (this.currentChar != null
                && this.currentChar != ')'
                && (Character.isLowerCase(this.currentChar)
                || Character.isUpperCase(this.currentChar)
                || this.currentChar == ' '
                || this.currentChar == ',')) {
            result += this.currentChar;
            this.advance();
        }

        if (this.currentChar == ')') {
            result += ')';
            this.advance();
        }
        return "AST" + result;
    }

    /**
     * @return name of variable
     */
    private String name() {
        String result = "";
        if (this.currentChar == '[') {
            this.advance();
        }
        while (this.currentChar != null
                && (Character.isLowerCase(this.currentChar)
                || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        if (this.currentChar == ']') {
            this.advance();
        }
        return result;
    }

    /**
     * @return a (multidigit) integer consumed from the input.
     * @throws Exception when failed to find a digit character sequence from
     *                   the current position.
     */
    private String integer() throws Exception {
        String result = "";
        if (this.currentChar.equals('0')
                || this.currentChar.equals('1')
                || this.currentChar.equals('2')
                || this.currentChar.equals('3')
                || this.currentChar.equals('4')
                || this.currentChar.equals('5')
                || this.currentChar.equals('6')
                || this.currentChar.equals('7')
                || this.currentChar.equals('8')
                || this.currentChar.equals('9')) {
            if (this.currentChar.equals('0')) {
                this.advance();
                result += "0";
            }
            if (this.currentChar.equals('1')) {
                this.advance();
                result += "1";
            }
            if (this.currentChar.equals('2')) {
                this.advance();
                result += "2";
            }
            if (this.currentChar.equals('3')) {
                this.advance();
                result += "3";
            }
            if (this.currentChar.equals('4')) {
                this.advance();
                result += "4";
            }
            if (this.currentChar.equals('5')) {
                this.advance();
                result += "5";
            }
            if (this.currentChar.equals('6')) {
                this.advance();
                result += "6";
            }
            if (this.currentChar.equals('7')) {
                this.advance();
                result += "7";
            }
            if (this.currentChar.equals('8')) {
                this.advance();
                result += "8";
            }
            if (this.currentChar.equals('9')) {
                this.advance();
                result += "9";
            }
        } else {
            this.error();
        }
        while (this.currentChar.equals('0')
                || this.currentChar.equals('1')
                || this.currentChar.equals('2')
                || this.currentChar.equals('3')
                || this.currentChar.equals('4')
                || this.currentChar.equals('5')
                || this.currentChar.equals('6')
                || this.currentChar.equals('7')
                || this.currentChar.equals('8')
                || this.currentChar.equals('9')) {
            if (this.currentChar.equals('0')
                    || this.currentChar.equals('1')
                    || this.currentChar.equals('2')
                    || this.currentChar.equals('3')
                    || this.currentChar.equals('4')
                    || this.currentChar.equals('5')
                    || this.currentChar.equals('6')
                    || this.currentChar.equals('7')
                    || this.currentChar.equals('8')
                    || this.currentChar.equals('9')) {
                if (this.currentChar.equals('0')) {
                    this.advance();
                    result += "0";
                }
                if (this.currentChar.equals('1')) {
                    this.advance();
                    result += "1";
                }
                if (this.currentChar.equals('2')) {
                    this.advance();
                    result += "2";
                }
                if (this.currentChar.equals('3')) {
                    this.advance();
                    result += "3";
                }
                if (this.currentChar.equals('4')) {
                    this.advance();
                    result += "4";
                }
                if (this.currentChar.equals('5')) {
                    this.advance();
                    result += "5";
                }
                if (this.currentChar.equals('6')) {
                    this.advance();
                    result += "6";
                }
                if (this.currentChar.equals('7')) {
                    this.advance();
                    result += "7";
                }
                if (this.currentChar.equals('8')) {
                    this.advance();
                    result += "8";
                }
                if (this.currentChar.equals('9')) {
                    this.advance();
                    result += "9";
                }
            } else {
                this.error();
            }
        }

        return result;
    }

    /**
     * Lexical analyzer. This method is responsible for breaking a sentence
     * apart into tokens. One token at a time.
     *
     * @return next avialable token
     * @throws Exception when cannot read the next token from the current
     *                   position in the text
     */
    public Token getNextToken() throws Exception {
        while (this.currentChar != null) {
            if (Character.isWhitespace(this.currentChar)) {
                this.skipWhitespace();
                continue;
            }

            if (Character.isUpperCase(this.currentChar)) {
                return new Token(TokenType.TERM, this.term());
            }

            if (Character.isLowerCase(this.currentChar)) {
                return new Token(TokenType.NONTERM, this.nonterm());
            }

            if (this.currentChar.equals('\'')) {
                return new Token(TokenType.QUOTED, this.quoted());
            }

            if (this.currentChar.equals('#')) {
                return new Token(TokenType.CALL, this.call());
            }

            if (this.currentChar.equals(':')) {
                return new Token(TokenType.EQ, this.eq());
            }

            if (this.currentChar.equals('|')) {
                this.advance();
                return new Token(TokenType.LINE, "|");
            }

            if (this.currentChar.equals('*')) {
                this.advance();
                return new Token(TokenType.STAR, "*");
            }

            if (this.currentChar.equals('(')) {
                this.advance();
                return new Token(TokenType.LPAREN, "(");
            }

            if (this.currentChar.equals(')')) {
                this.advance();
                return new Token(TokenType.RPAREN, ")");
            }

            if (this.currentChar.equals(';')) {
                this.advance();
                return new Token(TokenType.SEMI, ";");
            }

            if (this.currentChar.equals('@')) {
                return new Token(TokenType.ASTNAME, this.astname());
            }

            if (this.currentChar.equals(',')) {
                this.advance();
                return new Token(TokenType.COMMA, ",");
            }

            if (this.currentChar.equals('$')) {
                return new Token(TokenType.RET, this.ret());
            }

            if (this.currentChar.equals('%')) {
                return new Token(TokenType.ASTARGTYPE, this.astargtype());
            }

            if (this.currentChar.equals('[')) {
                return new Token(TokenType.NAME, this.name());
            }

            if (this.currentChar.equals('0')
                    || this.currentChar.equals('1')
                    || this.currentChar.equals('2')
                    || this.currentChar.equals('3')
                    || this.currentChar.equals('4')
                    || this.currentChar.equals('5')
                    || this.currentChar.equals('6')
                    || this.currentChar.equals('7')
                    || this.currentChar.equals('8')
                    || this.currentChar.equals('9')) {
                return new Token(TokenType.INTEGER, this.integer());
            }

            this.error();
        }

        return new Token(TokenType.EOF, null);
    }

}
