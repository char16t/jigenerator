package representation2;

import java.util.*;

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