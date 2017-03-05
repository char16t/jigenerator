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

import representation.GeneratorData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interpreter. Interpret token sequence
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class Interpreter {
    /**
     * Parsers provides a token sequence.
     */
    private final Parser parser;

    /**
     * TermVisitor can visit only terms in ast-tree.
     */
    private final TermVisitor termVisitor = new TermVisitor();

    /**
     * NontermVisitor can visit only nonterms in ast-tree.
     */
    private final NontermVisitor nontermVisitor = new NontermVisitor();

    /**
     * 0 - none;
     * 1 - term;
     * 2 - nonterm.
     */
    private int termOrNonterm = 0;

    /**
     * Generator data.
     */
    private GeneratorData generatorData;

    /**
     * Token variable names.
     */
    private Set<String> tokenVariableNames = new HashSet<String>();

    /**
     * Nonterm variable names.
     */
    private Set<String> nontermVariableNames = new HashSet<String>();

    /**
     * Constructor.
     *
     * @param parser
     */
    public Interpreter(final Parser parser) {
        this.parser = parser;
        this.generatorData = new GeneratorData();
    }

    /**
     * Getter.
     *
     * @return generatorData
     */
    public GeneratorData getGeneratorData() {
        return generatorData;
    }

    /**
     * Setter.
     *
     * @param generatorData
     */
    public void setGeneratorData(final GeneratorData generatorData) {
        this.generatorData = generatorData;
    }

    /**
     * Visit abstact tree node.
     *
     * @param node abstact tree node
     * @return result string
     */
    private String visit(final AST node) {
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

    /**
     * Visit expression tree node.
     *
     * @param node expression node
     * @return result substring
     */
    private String visitExpression(final ASTExpression node) {
        String result = "";
        for (AST child : node.childs()) {
            result += visit(child);
        }
        return result;
    }

    /**
     * Visit nonterm defenition tree node.
     *
     * @param node nonterm defenition tree node
     * @return result substring
     */
    private String visitNonermDef(final ASTNonermDef node) {
        tokenVariableNames.clear();
        nontermVariableNames.clear();

        termOrNonterm = 2;
        String result = visit(node.expr());
        generatorData.getNonterminals().add(node.name());

        String tokenVariableNamesString = "";
        for (String tokenVariableName : tokenVariableNames) {
            tokenVariableNamesString += "Token "
                    + tokenVariableName
                    + " = null;\n";
        }

        String nontermVariableNamesString = "";
        for (String nontermVariableName : nontermVariableNames) {
            nontermVariableNamesString += "AST "
                + nontermVariableName + " = null;\n";
        }
        generatorData.getNonterminalsSourceCode().put(node.name(),
                tokenVariableNamesString + nontermVariableNamesString + result);
        return result;
    }

    /**
     * Visit nonterm tree node.
     *
     * @param node nonterm tree node
     * @return result substring
     */
    private String visitNonterm(final ASTNonterm node) {
        String result = "";
        if (termOrNonterm == 2) {
            result = "this." + node.value() + "();\n";
            if (node.localVariableName() != null) {
                nontermVariableNames.add(node.localVariableName());
                result = node.localVariableName() + " = " + result;
            }
        }
        return result;
    }

    /**
     * Visit 'or' tree node.
     *
     * @param node 'or' tree node
     * @return result substring
     */
    private String visitOr(final ASTOr node) {
        String result = "";
        if (termOrNonterm == 1) {
            String nodeConditions =
                termVisitor.getStartSymbolsForTermSubnode(node);
            String nodeConditionsString = "";
            for (int i = 0; i < nodeConditions.length(); ++i) {
                nodeConditionsString += "this.currentChar.equals('"
                    + nodeConditions.charAt(i) + "') || ";
            }
            if (nodeConditionsString.length() > 4) {
                nodeConditionsString =
                    nodeConditionsString.substring(
                        0, nodeConditionsString.length() - 4
                    );
            }

            String ifDefention = "if";
            for (AST child : node.expressions()) {
                if (child instanceof ASTQuoted
                    || child instanceof ASTExpression) {
                    result += visit(child);
                } else {
                    String conditions =
                        termVisitor.getStartSymbolsForTermSubnode(child);
                    String conditionString = "";
                    for (int i = 0; i < conditions.length(); ++i) {
                        conditionString += "this.currentChar.equals('"
                            + conditions.charAt(i) + "') || ";
                    }
                    if (conditionString.length() > 4) {
                        conditionString = conditionString.substring(
                            0, conditionString.length() - 4
                        );
                    }
                    result += ifDefention
                        + " (this.currentChar != null && ("
                        + conditionString
                        + ")) {\n"
                        + visit(child)
                        + "\n}\n";
                    ifDefention = "else if";
                    //result += "if (...) {\n" + visit(child) + "\n}\n";
                }
            }

            result = "if ("
                + nodeConditionsString
                + ") { "
                + result
                + " } else { this.error(); }";
        }
        if (termOrNonterm == 2) {
            String ifDefention = "if";
            for (AST child : node.expressions()) {
                List<String> conditions =
                    nontermVisitor.getStartTermsForNontermSubnode(child);
                String conditionString = "";
                for (String condition : conditions) {
                    conditionString += "this.currentToken.type == TokenType."
                        + condition + " || ";
                }
                if (conditionString.length() > 4) {
                    conditionString = conditionString.substring(
                        0,
                        conditionString.length() - 4
                    );
                }
                result += ifDefention
                    + " ("
                    + conditionString
                    + ") {\n"
                    + visit(child)
                    + "\n}\n";
                ifDefention = "else if";
            }
            result += " else { this.error(); return null; }";
        }
        return result;
    }

    /**
     * Visit program tree node.
     *
     * @param node program tree node
     * @return result substring
     */
    private String visitProgram(final ASTProgram node) {
        String result = "ASTProgram:\n";
        for (AST child : node.childs()) {
            result += "    " + visit(child);
        }
        return result;
    }

    /**
     * Visit quoted tree node.
     *
     * @param node quoted tree node
     * @return result substring
     */
    private String visitQuoted(final ASTQuoted node) {
        String result = "";
        if (termOrNonterm == 1) {
            result = visitQuotedContent(node.value(), node.value());
        }
        return result;
    }

    /**
     * Generates string for quoted tree node. Recursive method.
     */
    private String visitQuotedContent(final String orig, final String quoted) {
        if (quoted.length() == 0) {
            return "result += \"" + orig + "\";";
        }
        String result =
            "if (this.currentChar != null && this.currentChar.equals('"
                + quoted.charAt(quoted.length() - 1) + "')) {"
                + "this.advance(); "
                + visitQuotedContent(orig, quoted.substring(
            0, quoted.length() - 1))
                + " }";

        return result;
    }

    /**
     * Visit repeat tree node.
     *
     * @param node repeat tree node
     * @return result substring
     */
    private String visitRepeat(final ASTRepeat node) {
        String result = "";
        if (termOrNonterm == 1) {
            String conditions = termVisitor.getStartSymbolsForTermSubnode(node);
            String conditionString = "";
            for (int i = 0; i < conditions.length(); ++i) {
                conditionString += "this.currentChar.equals('"
                        + conditions.charAt(i)
                        + "') || ";
            }
            if (conditionString.length() > 4) {
                conditionString = conditionString.substring(
                    0,
                    conditionString.length() - 4
                );
            }
            result =
                "while(this.currentChar != null && ("
                + conditionString
                + ")) {\n";
            for (AST expr : node.childs()) {
                result += visit(expr);
            }
            result += "\n}\n";
        } else if (termOrNonterm == 2) {
            List<String> conditions =
                nontermVisitor.getStartTermsForNontermSubnode(node);
            String conditionString = "";
            for (String condition : conditions) {
                conditionString +=
                    "this.currentToken.type == TokenType."
                    + condition
                    + " || ";
            }
            if (conditionString.length() > 4) {
                conditionString = conditionString.substring(
                    0,
                    conditionString.length() - 4
                );
            }

            result = "while(" + conditionString + ") {\n";
            for (AST expr : node.childs()) {
                result += visit(expr);
            }
            result += "\n}\n";
        }
        return result;
    }

    /**
     * Visit term tree node.
     *
     * @param node term tree node
     * @return result substring
     */
    private String visitTerm(final ASTTerm node) {
        String result = "";
        if (termOrNonterm == 2) {
            result = "this.eat(TokenType." + node.value() + ");\n";
            if (node.localVariableName() != null) {
                tokenVariableNames.add(node.localVariableName());
                result = node.localVariableName()
                        + " = this.currentToken;\n"
                        + result;
            }
        }
        return result;
    }

    /**
     * Visit term defenition tree node.
     *
     * @param node term defenition tree node
     * @return result substring
     */
    private String visitTermDef(final ASTTermDef node) {
        termOrNonterm = 1;
        generatorData.getTerminals().add(node.head());

        String result = visit(node.expr());
        generatorData.getTerminalsSourceCode().put(node.head(), result);

        return result;
    }

    /**
     * Visit ast return tree node.
     *
     * @param node ast return tree node
     * @return result substring
     */
    private String visitASTReturn(final ASTReturn node) {
        String result = "return " + node.value() + ";\n";
        return result;
    }

    /**
     * Visit newNode tree node.
     *
     * @param node newNode tree node
     * @return result substring
     */
    private String visitASTNewNode(final ASTNewNode node) {
        String result = node.localVariableName() == null
                ? "new " + node.value() + ";\n"
                : node.localVariableName() + " = new " + node.value() + ";\n";
        if (node.localVariableName() != null) {
            nontermVariableNames.add(node.localVariableName());
        }
        return result;
    }

    /**
     * Visit astDef tree node.
     *
     * @param node astDef tree node
     * @return result substring
     */
    private String visitASTDef(final ASTASTDef node) {
        generatorData.getAstNodes().put(
            node.name(),
            (LinkedList<String>) node.childs()
        );
        return "";
    }

    /**
     * Interpret token sequence.
     *
     * @return result string
     * @throws Exception when parsing error
     */
    public String interpret() throws Exception {
        AST tree = parser.parse();
        if (tree == null) {
            return "";
        }
        Map<String, String> terminalsCanStartsWith =
            termVisitor.getTerminalsCanStartsWith(tree);
        generatorData.setNonterminalsCanStartsWith(
            nontermVisitor.getResult(tree)
        );
        generatorData.setTerminalsCanStartsWith(terminalsCanStartsWith);
        return visit(tree);
    }
}
