package representation2;

import java.util.List;
import java.util.Map;

public class GeneratorData {
    private List<String> nonterminals;
    private Map<String, String> nonterminalsSourceCode;
    private List<String> terminals;
    private Map<String, String> terminalsCanStartsWith;
    private Map<String, String> terminalsSourceCode;

    public GeneratorData(List<String> nonterminals, Map<String, String> nonterminalsSourceCode, List<String> terminals, Map<String, String> terminalsCanStartsWith, Map<String, String> terminalsSourceCode) {
        this.nonterminals = nonterminals;
        this.nonterminalsSourceCode = nonterminalsSourceCode;
        this.terminals = terminals;
        this.terminalsCanStartsWith = terminalsCanStartsWith;
        this.terminalsSourceCode = terminalsSourceCode;
    }

    public List<String> getNonterminals() {
        return nonterminals;
    }

    public void setNonterminals(List<String> nonterminals) {
        this.nonterminals = nonterminals;
    }

    public Map<String, String> getNonterminalsSourceCode() {
        return nonterminalsSourceCode;
    }

    public void setNonterminalsSourceCode(Map<String, String> nonterminalsSourceCode) {
        this.nonterminalsSourceCode = nonterminalsSourceCode;
    }

    public List<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<String> terminals) {
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
}
