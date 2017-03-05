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
    private Set<String> tokenVariableNames = new HashSet<>();

    /**
     * Nonterm variable names.
     */
    private Set<String> nontermVariableNames = new HashSet<>();

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
        return this.generatorData;
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
            return this.visitExpression((ASTExpression) node);
        } else if (node instanceof ASTNonermDef) {
            return this.visitNonermDef((ASTNonermDef) node);
        } else if (node instanceof ASTNonterm) {
            return this.visitNonterm((ASTNonterm) node);
        } else if (node instanceof ASTOr) {
            return this.visitOr((ASTOr) node);
        } else if (node instanceof ASTProgram) {
            return this.visitProgram((ASTProgram) node);
        } else if (node instanceof ASTQuoted) {
            return this.visitQuoted((ASTQuoted) node);
        } else if (node instanceof ASTRepeat) {
            return this.visitRepeat((ASTRepeat) node);
        } else if (node instanceof ASTTerm) {
            return this.visitTerm((ASTTerm) node);
        } else if (node instanceof ASTTermDef) {
            return this.visitTermDef((ASTTermDef) node);
        } else if (node instanceof ASTASTDef) {
            return this.visitASTDef((ASTASTDef) node);
        } else if (node instanceof ASTNewNode) {
            return this.visitASTNewNode((ASTNewNode) node);
        } else if (node instanceof ASTReturn) {
            return this.visitASTReturn((ASTReturn) node);
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
        for (final AST child : node.childs()) {
            result += this.visit(child);
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
        this.tokenVariableNames.clear();
        this.nontermVariableNames.clear();

        this.termOrNonterm = 2;
        final String result = visit(node.expr());
        this.generatorData.getNonterminals().add(node.name());

        String tokenVariableNamesString = "";
        for (final String tokenVariableName : this.tokenVariableNames) {
            tokenVariableNamesString += "Token "
                    + tokenVariableName
                    + " = null;\n";
        }

        String nontermVariableNamesString = "";
        for (final String nontermVariableName : this.nontermVariableNames) {
            nontermVariableNamesString += "AST "
                + nontermVariableName + " = null;\n";
        }
        this.generatorData.getNonterminalsSourceCode().put(node.name(),
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
        if (this.termOrNonterm == 2) {
            result = "this." + node.value() + "();\n";
            if (node.localVariableName() != null) {
                this.nontermVariableNames.add(node.localVariableName());
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
        if (this.termOrNonterm == 1) {
            String nodeConditions =
                this.termVisitor.getStartSymbolsForTermSubnode(node);
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
            for (final AST child : node.expressions()) {
                if (child instanceof ASTQuoted
                    || child instanceof ASTExpression) {
                    result += this.visit(child);
                } else {
                    String conditions =
                        this.termVisitor.getStartSymbolsForTermSubnode(child);
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
                        + this.visit(child)
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
        if (this.termOrNonterm == 2) {
            String ifDefention = "if";
            for (final AST child : node.expressions()) {
                final List<String> conditions =
                    this.nontermVisitor.getStartTermsForNontermSubnode(child);
                String conditionString = "";
                for (final String condition : conditions) {
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
                    + this.visit(child)
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
        for (final AST child : node.childs()) {
            result += "    " + this.visit(child);
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
        if (this.termOrNonterm == 1) {
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
        final String result =
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
        if (this.termOrNonterm == 1) {
            final String conditions = this.termVisitor.getStartSymbolsForTermSubnode(node);
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
            for (final AST expr : node.childs()) {
                result += this.visit(expr);
            }
            result += "\n}\n";
        } else if (this.termOrNonterm == 2) {
            List<String> conditions =
                this.nontermVisitor.getStartTermsForNontermSubnode(node);
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
            for (final AST expr : node.childs()) {
                result += this.visit(expr);
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
        if (this.termOrNonterm == 2) {
            result = "this.eat(TokenType." + node.value() + ");\n";
            if (node.localVariableName() != null) {
                this.tokenVariableNames.add(node.localVariableName());
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
        this.termOrNonterm = 1;
        this.generatorData.getTerminals().add(node.head());

        final String result = this.visit(node.expr());
        this.generatorData.getTerminalsSourceCode().put(node.head(), result);

        return result;
    }

    /**
     * Visit ast return tree node.
     *
     * @param node ast return tree node
     * @return result substring
     */
    private String visitASTReturn(final ASTReturn node) {
        final String result = "return " + node.value() + ";\n";
        return result;
    }

    /**
     * Visit newNode tree node.
     *
     * @param node newNode tree node
     * @return result substring
     */
    private String visitASTNewNode(final ASTNewNode node) {
        final String result = node.localVariableName() == null
                ? "new " + node.value() + ";\n"
                : node.localVariableName() + " = new " + node.value() + ";\n";
        if (node.localVariableName() != null) {
            this.nontermVariableNames.add(node.localVariableName());
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
        this.generatorData.getAstNodes().put(
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
        final AST tree = parser.parse();
        if (tree == null) {
            return "";
        }
        final Map<String, String> terminalsCanStartsWith =
            this.termVisitor.getTerminalsCanStartsWith(tree);
        this.generatorData.setNonterminalsCanStartsWith(
            this.nontermVisitor.getResult(tree)
        );
        this.generatorData.setTerminalsCanStartsWith(terminalsCanStartsWith);
        return this.visit(tree);
    }
}
