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
 * Token type.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public enum TokenType {
    /**
     * Nonterminal.
     */
    NONTERM,

    /**
     * Terminal.
     */
    TERM,

    /**
     * Equals symbol.
     */
    EQ,

    /**
     * Left paren.
     */
    LPAREN,

    /**
     * Right paren.
     */
    RPAREN,

    /**
     * Star symbol '*'
     */
    STAR,

    /**
     * Vertical line '|'
     */
    LINE,

    /**
     * Semicolon.
     */
    SEMI,

    /**
     * Quoted expression.
     */
    QUOTED,

    /**
     * AST node name.
     */
    ASTNAME,

    /**
     * Integer number.
     */
    INTEGER,

    /**
     * Comma.
     */
    COMMA,

    /**
     * Local variable name.
     */
    NAME,

    /**
     * Return statement.
     */
    RET,

    /**
     * Call ast node constructor.
     */
    CALL,

    /**
     * Type of ast node constructor's argument
     */
    ASTARGTYPE,

    /**
     * End of file.
     */
    EOF
}
