package translator;

import representation2.GeneratorData;

import java.util.*;

public class Interpreter {
    private GeneratorData generatorData;
    Parser parser;

    TermVisitor termVisitor = new TermVisitor();
    NontermVisitor nontermVisitor = new NontermVisitor();

    int termOrNonterm = 0; /* 0 - none, 1 - term, 2 - nonterm */

    public Interpreter(Parser parser) {
        this.parser = parser;
        this.generatorData = new GeneratorData();
    }

    public GeneratorData getGeneratorData() {
        return generatorData;
    }

    public void setGeneratorData(GeneratorData generatorData) {
        this.generatorData = generatorData;
    }

    public String visit(AST node) {
        if (node instanceof ASTExpression) {
            return visitExpression((ASTExpression) node);
        } else if (node instanceof ASTNonermDef) {
            return visitNonermDef((ASTNonermDef) node);
        } else if (node instanceof ASTNonterm) {
            return visitNonterm((ASTNonterm) node);
        } else if (node instanceof ASTOr) {
            return visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            return visitProgram((ASTProgram) node);
        } else if (node instanceof ASTQuoted) {
            return visitQuoted((ASTQuoted) node);
        } else if (node instanceof ASTRepeat) {
            return visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTerm) {
            return visitTerm((ASTTerm) node);
        } else if (node instanceof ASTTermDef) {
            return visitTermDef((ASTTermDef) node);
        } else if (node instanceof ASTASTDef) {
            return visitASTDef((ASTASTDef) node);
        } else if (node instanceof ASTNewNode) {
            return visitASTNewNode((ASTNewNode) node);
        } else if (node instanceof ASTReturn) {
            return visitASTReturn((ASTReturn) node);
        } else {
            return "";
        }
    }

    public String visitExpression(ASTExpression node) {
        String result = "";
        for (AST child : node.childs) {
            result += visit(child);
        }
        return result;
    }

    private Set<String> tokenVariableNames = new HashSet<String>();
    private Set<String> nontermVariableNames = new HashSet<String>();
    public String visitNonermDef(ASTNonermDef node) {
        tokenVariableNames.clear();
        nontermVariableNames.clear();

        termOrNonterm = 2;
        String result = visit(node.expr);
        generatorData.getNonterminals().add(node.name);

        String tokenVariableNamesString = "";
        for (String tokenVariableName : tokenVariableNames) {
            tokenVariableNamesString += "Token " + tokenVariableName + ";\n";
        }

        String nontermVariableNamesString = "";
        for (String nontermVariableName : nontermVariableNames) {
            nontermVariableNamesString += "AST " + nontermVariableName + ";\n";
        }
        generatorData.getNonterminalsSourceCode().put(node.name,
                tokenVariableNamesString + nontermVariableNamesString + result);
        return result;
    }

    public String visitNonterm(ASTNonterm node) {
        String result = "";
        if (termOrNonterm == 2) {
            result = "this." + node.value + "();\n";
            if (node.localVariableName != null) {
                nontermVariableNames.add(node.localVariableName);
                result = node.localVariableName + " = " + result;
            }
        }
        return result;
    }

    public String visitOr(ASTOr node) {
        String result = "";
        if (termOrNonterm == 1) {
            String nodeConditions = termVisitor.getStartSymbolsForTermSubnode(node);
            String nodeConditionsString = "";
            for (int i = 0; i < nodeConditions.length(); ++i) {
                nodeConditionsString += "this.currentChar.equals('" + nodeConditions.charAt(i) + "') || ";
            }
            if (nodeConditionsString.length() > 4) {
                nodeConditionsString = nodeConditionsString.substring(0, nodeConditionsString.length() - 4);
            }

            String ifDefention = "if";
            for (AST child : node.expressions) {
                if (child instanceof ASTQuoted || child instanceof ASTExpression) {
                    result += visit(child);
                } else {
                    String conditions = termVisitor.getStartSymbolsForTermSubnode(child);
                    String conditionString = "";
                    for (int i = 0; i < conditions.length(); ++i) {
                        conditionString += "this.currentChar.equals('" + conditions.charAt(i) + "') || ";
                    }
                    if (conditionString.length() > 4) {
                        conditionString = conditionString.substring(0, conditionString.length() - 4);
                    }
                    result += ifDefention + " (this.currentChar != null && (" + conditionString + ")) {\n" + visit(child) + "\n}\n";
                    ifDefention = "else if";
                    //result += "if (...) {\n" + visit(child) + "\n}\n";
                }
            }

            result = "if (" + nodeConditionsString + ") { " + result + " } else { this.error(); }";
        }
        if (termOrNonterm == 2) {
            String ifDefention = "if";
            for (AST child : node.expressions) {
                List<String> conditions = nontermVisitor.getStartTermsForNontermSubnode(child);
                String conditionString = "";
                for (String condition : conditions) {
                    conditionString += "this.currentToken.type == TokenType." + condition + " || ";
                }
                if (conditionString.length() > 4) {
                    conditionString = conditionString.substring(0, conditionString.length() - 4);
                }
                result += ifDefention + " (" + conditionString + ") {\n" + visit(child) + "\n}\n";
                ifDefention = "else if";
            }
        }
        return result;
    }

    public String visitProgram(ASTProgram node) {
        String result = "ASTProgram:\n";
        for (AST child : node.childs) {
            result += "    " + visit(child);
        }
        return result;
    }

    public String visitQuoted(ASTQuoted node) {
        String result = "";
        if (termOrNonterm == 1) {
            result = visitQuotedContent(node.value, node.value);
        }
        return result;
    }

    private String visitQuotedContent(String orig, String quoted) {
        if (quoted.length() == 0) {
            return "result += \"" + orig + "\";";
        }
        String result = "if (this.currentChar != null && this.currentChar.equals('" + quoted.charAt(quoted.length() - 1) + "')) {" +
                "this.advance(); " +
                visitQuotedContent(orig, quoted.substring(0, quoted.length() - 1)) +
                " }";

        return result;
    }

    public String visitRepeat(ASTRepeat node) {
        String result = "";
        if (termOrNonterm == 1) {
            String conditions = termVisitor.getStartSymbolsForTermSubnode(node);
            String conditionString = "";
            for (int i = 0; i < conditions.length(); ++i) {
                conditionString += "this.currentChar.equals('" + conditions.charAt(i) + "') || ";
            }
            if (conditionString.length() > 4) {
                conditionString = conditionString.substring(0, conditionString.length() - 4);
            }
            result = "while(this.currentChar != null && (" + conditionString + ")) {\n";
            for (AST expr : node.childs) {
                result += visit(expr);
            }
            result += "\n}\n";
        } else if (termOrNonterm == 2) {
            List<String> conditions = nontermVisitor.getStartTermsForNontermSubnode(node);
            String conditionString = "";
            for (String condition : conditions) {
                conditionString += "this.currentToken.type == TokenType." + condition + " || ";
            }
            if (conditionString.length() > 4) {
                conditionString = conditionString.substring(0, conditionString.length() - 4);
            }

            result = "while(" + conditionString + ") {\n";
            for (AST expr : node.childs) {
                result += visit(expr);
            }
            result += "\n}\n";
        }
        return result;
    }

    public String visitTerm(ASTTerm node) {
        String result = "";
        if (termOrNonterm == 2) {
            result = "this.eat(TokenType." + node.value + ");\n";
            if (node.localVariableName != null) {
                tokenVariableNames.add(node.localVariableName);
                result = node.localVariableName + " = this.currentToken;\n" + result;
            }
        }
        return result;
    }

    public String visitTermDef(ASTTermDef node) {
        termOrNonterm = 1;
        generatorData.getTerminals().add(node.head);

        String result = visit(node.expr);
        generatorData.getTerminalsSourceCode().put(node.head, result);

        return result;
    }

    private String visitASTReturn(ASTReturn node) {
        String result = "return " + node.value + ";\n";
        return result;
    }

    private String visitASTNewNode(ASTNewNode node) {
        String result = node.localVariableName == null ?
                node.value + ";\n" : node.localVariableName + " = " + node.value + ";\n";
        return result;
    }

    public String visitASTDef(ASTASTDef node) {
        generatorData.getAstNodes().put(node.name, node.numChilds);
        return "";
    }

    public String interpret() throws Exception {
        AST tree = parser.parse();
        if (tree == null) {
            return "";
        }
        Map<String, String> terminalsCanStartsWith = termVisitor.getTerminalsCanStartsWith(tree);
        generatorData.setNonterminalsCanStartsWith(nontermVisitor.getResult(tree));
        generatorData.setTerminalsCanStartsWith(terminalsCanStartsWith);
        return visit(tree);
    }
}
