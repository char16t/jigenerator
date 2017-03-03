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
package representation2;

import java.util.*;

/**
 * Java project generator data.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class GeneratorData {
    private Set<String> nonterminals;
    private Map<String, LinkedList<String>> nonterminalsCanStartsWith;
    private Map<String, String> nonterminalsSourceCode;
    private Set<String> terminals;
    private Map<String, String> terminalsCanStartsWith;
    private Map<String, String> terminalsSourceCode;
    private Map<String, LinkedList<String>> astNodes;

    public GeneratorData() {
        this.nonterminals = new LinkedHashSet<String>();
        this.nonterminalsCanStartsWith = new LinkedHashMap<String, LinkedList<String>>();
        this.nonterminalsSourceCode = new HashMap<String, String>();
        this.terminals = new LinkedHashSet<String>();
        this.terminalsCanStartsWith = new HashMap<String, String>();
        this.terminalsSourceCode = new HashMap<String, String>();
        this.astNodes = new HashMap<String, LinkedList<String>>();
    }

    public GeneratorData(Set<String> nonterminals, Map<String, LinkedList<String>> nonterminalsCanStartsWith, Map<String, String> nonterminalsSourceCode, Set<String> terminals, Map<String, String> terminalsCanStartsWith, Map<String, String> terminalsSourceCode, Map<String, LinkedList<String>> astNodes) {
        this.nonterminals = nonterminals;
        this.nonterminalsCanStartsWith = nonterminalsCanStartsWith;
        this.nonterminalsSourceCode = nonterminalsSourceCode;
        this.terminals = terminals;
        this.terminalsCanStartsWith = terminalsCanStartsWith;
        this.terminalsSourceCode = terminalsSourceCode;
        this.astNodes = astNodes;
    }

    public Set<String> getNonterminals() {
        return nonterminals;
    }

    public void setNonterminals(Set<String> nonterminals) {
        this.nonterminals = nonterminals;
    }

    public Map<String, String> getNonterminalsSourceCode() {
        return nonterminalsSourceCode;
    }

    public void setNonterminalsSourceCode(Map<String, String> nonterminalsSourceCode) {
        this.nonterminalsSourceCode = nonterminalsSourceCode;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(Set<String> terminals) {
        this.terminals = terminals;
    }

    public Map<String, String> getTerminalsCanStartsWith() {
        return terminalsCanStartsWith;
    }

    public void setTerminalsCanStartsWith(Map<String, String> terminalsCanStartsWith) {
        this.terminalsCanStartsWith = terminalsCanStartsWith;
    }

    public Map<String, String> getTerminalsSourceCode() {
        return terminalsSourceCode;
    }

    public void setTerminalsSourceCode(Map<String, String> terminalsSourceCode) {
        this.terminalsSourceCode = terminalsSourceCode;
    }

    public Map<String, LinkedList<String>> getNonterminalsCanStartsWith() {
        return nonterminalsCanStartsWith;
    }

    public void setNonterminalsCanStartsWith(Map<String, LinkedList<String>> nonterminalsCanStartsWith) {
        this.nonterminalsCanStartsWith = nonterminalsCanStartsWith;
    }

    public Map<String, LinkedList<String>> getAstNodes() {
        return astNodes;
    }

    public void setAstNodes(Map<String, LinkedList<String>> astNodes) {
        this.astNodes = astNodes;
    }
}
