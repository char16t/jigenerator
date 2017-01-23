package translator;

import representation2.GeneratorData;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 1/5/17.
 */
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
        } else {
            return "";
        }
    }

    public String visitExpression(ASTExpression node) {
        String result =  "";
        for (AST child : node.childs) {
            result += visit(child);
        }
        return result;
    }

    public String visitNonermDef(ASTNonermDef node) {
        termOrNonterm = 2;
        String result = "/*\n" + visit(node.expr) + " */";
        generatorData.getNonterminals().add(node.name);
        generatorData.getNonterminalsSourceCode().put(node.name, result);
        return result;
    }

    public String visitNonterm(ASTNonterm node) {
        String result = "";
        if (termOrNonterm == 2) {
            result = "this." + node.value + "();\n";
        }
        return result;
    }

    public String visitOr(ASTOr node) {
        String result = "";
        if (termOrNonterm == 1) {
            for (AST child : node.expressions) {
                // todo: use 'else if' constructor for second and next conditions
                result += "if (...) {\n" + visit(child) + "\n}\n";
            }
        }
        if (termOrNonterm == 2) {
            for (AST child : node.expressions) {
            /*
            List<String> conditions = nontermVisitor.getStartTermsForNontermSubnode(child);
            String conditionString = "";
            for (String condition : conditions) {
                conditionString += "TokenType." + condition + " || ";
            }
            if (conditionString.length() > 4) {
                conditionString.substring(0, conditionString.length() - 4);
            }
            */
                // todo: use 'else if' constructor for second and next conditions
                result += "if (...) {\n" + visit(child) + "\n}\n";
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
            return "return \"" + orig + "\";";
        }
        String result = "if (this.currentChar.equals('" + quoted.charAt(quoted.length()-1) + "') {" +
                "this.advance(); " +
                visitQuotedContent(orig, quoted.substring(0, quoted.length()-1)) +
                " }";

        return result;
    }

    public String visitRepeat(ASTRepeat node) {
        String result = "";
        if (termOrNonterm == 1) {
            result = "while(...) {\n";
            for (AST expr : node.childs) {
                result += visit(expr);
            }
            result += "\n}\n";
        } else if (termOrNonterm == 2) {
            result = "while(...) {\n";
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
        }
        return result;
    }

    public String visitTermDef(ASTTermDef node) {
        termOrNonterm = 1;
        generatorData.getTerminals().add(node.head);

        String result = "/*\n" + visit(node.expr) + "\n*/\n";
        generatorData.getTerminalsSourceCode().put(node.head, result);

        return result;
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
