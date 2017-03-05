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
package representation;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Java project generator data.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class GeneratorData {

    /**
     * Set of nonterminals.
     */
    private Set<String> nonterminals;

    /**
     * List of tokens, which can begin a nonterminals.
     */
    private Map<String, LinkedList<String>> nonterminalsCanStartsWith;

    /**
     * Generated source code for nonterminals.
     */
    private Map<String, String> nonterminalsSourceCode;

    /**
     * Set of terminals.
     */
    private Set<String> terminals;

    /**
     * List (string) of symbols, which can begin a terminals.
     */
    private Map<String, String> terminalsCanStartsWith;

    /**
     * Generated source code for terminals.
     */
    private Map<String, String> terminalsSourceCode;

    /**
     * AST nodes and their constructor arguments
     */
    private Map<String, LinkedList<String>> astNodes;

    /**
     * Ctor.
     */
    public GeneratorData() {
        this.nonterminals = new LinkedHashSet<String>();
        this.nonterminalsCanStartsWith
            = new LinkedHashMap<String, LinkedList<String>>();
        this.nonterminalsSourceCode = new HashMap<String, String>();
        this.terminals = new LinkedHashSet<String>();
        this.terminalsCanStartsWith = new HashMap<String, String>();
        this.terminalsSourceCode = new HashMap<String, String>();
        this.astNodes = new HashMap<String, LinkedList<String>>();
    }

    /**
     * Ctor.
     *
     * @param nonterminals Set of nonterminals
     * @param nonterminalsSourceCode Generated source code for nonterminals
     * @param terminals Set of terminals
     * @param terminalsCanStartsWith List (string) of symbols, which can begin
     *                               a terminals
     * @param terminalsSourceCode Generated source code for terminals
     * @param astNodes AST nodes and their constructor arguments
     */
    public GeneratorData(
        final Set<String> nonterminals,
        final Map<String, LinkedList<String>> nonterminalsCanStartsWith,
        final Map<String, String> nonterminalsSourceCode,
        final Set<String> terminals,
        final Map<String, String> terminalsCanStartsWith,
        final Map<String, String> terminalsSourceCode,
        final Map<String, LinkedList<String>> astNodes
    ) {
        this.nonterminals = nonterminals;
        this.nonterminalsCanStartsWith = nonterminalsCanStartsWith;
        this.nonterminalsSourceCode = nonterminalsSourceCode;
        this.terminals = terminals;
        this.terminalsCanStartsWith = terminalsCanStartsWith;
        this.terminalsSourceCode = terminalsSourceCode;
        this.astNodes = astNodes;
    }

    /**
     * Getter.
     *
     * @return Set of nonterminals
     */
    public Set<String> getNonterminals() {
        return nonterminals;
    }

    /**
     * Setter.
     *
     * @param nonterminals Set of nonterminals
     */
    public void setNonterminals(final Set<String> nonterminals) {
        this.nonterminals = nonterminals;
    }

    /**
     * Getter.
     *
     * @return Generated source code for terminals
     */
    public Map<String, String> getNonterminalsSourceCode() {
        return nonterminalsSourceCode;
    }

    /**
     * Setter.
     *
     * @param nonterminalsSourceCode Generated source code for terminals
     */
    public void setNonterminalsSourceCode(
        final Map<String, String> nonterminalsSourceCode
    ) {
        this.nonterminalsSourceCode = nonterminalsSourceCode;
    }

    /**
     * Getter.
     *
     * @return Set of terminals
     */
    public Set<String> getTerminals() {
        return terminals;
    }

    /**
     * Setter.
     *
     * @param terminals Set of terminals
     */
    public void setTerminals(final Set<String> terminals) {
        this.terminals = terminals;
    }

    /**
     * Getter.
     *
     * @return List (string) of symbols, which can begin a terminals.
     */
    public Map<String, String> getTerminalsCanStartsWith() {
        return terminalsCanStartsWith;
    }

    /**
     * Setter.
     *
     * @param terminalsCanStartsWith List (string) of symbols, which can begin
     *                               a terminals.
     */
    public void setTerminalsCanStartsWith(
        final Map<String, String> terminalsCanStartsWith
    ) {
        this.terminalsCanStartsWith = terminalsCanStartsWith;
    }

    /**
     * Getter.
     *
     * @return Generated source code for terminals
     */
    public Map<String, String> getTerminalsSourceCode() {
        return terminalsSourceCode;
    }

    /**
     * Setter.
     *
     * @param terminalsSourceCode Generated source code for terminals
     */
    public void setTerminalsSourceCode(
        final Map<String, String> terminalsSourceCode
    ) {
        this.terminalsSourceCode = terminalsSourceCode;
    }

    /**
     * Getter.
     *
     * @return List of tokens, which can begin a nonterminals
     */
    public Map<String, LinkedList<String>> getNonterminalsCanStartsWith() {
        return nonterminalsCanStartsWith;
    }

    /**
     * Setter.
     *
     * @param nonterminalsCanStartsWith List of tokens, which can begin
     *                                  a nonterminals.
     */
    public void setNonterminalsCanStartsWith(
        final Map<String, LinkedList<String>> nonterminalsCanStartsWith
    ) {
        this.nonterminalsCanStartsWith = nonterminalsCanStartsWith;
    }

    /**
     * Getter.
     *
     * @return AST nodes and their constructor arguments
     */
    public Map<String, LinkedList<String>> getAstNodes() {
        return astNodes;
    }

    /**
     * Setter.
     *
     * @param astNodes AST nodes and their constructor arguments
     */
    public void setAstNodes(final Map<String, LinkedList<String>> astNodes) {
        this.astNodes = astNodes;
    }
}
