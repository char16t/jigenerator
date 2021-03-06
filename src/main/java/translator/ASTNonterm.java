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
 * AST nonterm.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class ASTNonterm implements AST {
    /**
     * Nonterm name.
     */
    private final String value;

    /**
     * Associated variable name.
     */
    private final String localVariableName;

    /**
     * Ctor.
     *
     * @param value Nonterm name
     */
    public ASTNonterm(final String value) {
        this(value, "");
    }

    /**
     * Ctor.
     *
     * @param value Nonterm name
     * @param localVariableName Associated variable name
     */
    public ASTNonterm(final String value, final String localVariableName) {
        this.value = value;
        this.localVariableName = localVariableName;
    }

    /**
     * Getter.
     *
     * @return Nonterm name
     */
    public String value() {
        return this.value;
    }

    /**
     * Getter.
     *
     * @return Associated variable name
     */
    public String localVariableName() {
        return this.localVariableName;
    }
}
