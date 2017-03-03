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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Java project generator.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class Generator {

    /**
     * Templates path.
     */
    private static final String TEMPLATES_PATH = "src/main/resources/representation2/";

    /**
     * Output path.
     */
    private static String OUTPUT_PATH = "output/";

    /**
     * Set of nonterminals.
     */
    private Set<String> nonterminals = new LinkedHashSet<String>();

    /**
     * Generated source code for nonterminals.
     */
    private Map<String, String> nonterminalsSourceCode = new HashMap<String, String>();

    /**
     * Set of terminals.
     */
    private Set<String> terminals = new LinkedHashSet<String>();

    /**
     * List (string) of symbols, which can begin a terminals.
     */
    private Map<String, String> terminalsCanStartsWith
            = new HashMap<String, String>();

    /**
     * Generated source code for terminals.
     */
    private Map<String, String> terminalsSourceCode
            = new HashMap<String, String>();

    /**
     * AST nodes and their constructor arguments
     */
    private Map<String, LinkedList<String>> astNodes
            = new HashMap<String, LinkedList<String>>();

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
    public Generator(Set<String> nonterminals,
                     Map<String, String> nonterminalsSourceCode,
                     Set<String> terminals,
                     Map<String, String> terminalsCanStartsWith,
                     Map<String, String> terminalsSourceCode,
                     Map<String, LinkedList<String>> astNodes) {
        this.nonterminals = nonterminals;
        this.nonterminalsSourceCode = nonterminalsSourceCode;
        this.terminals = terminals;
        this.terminalsCanStartsWith = terminalsCanStartsWith;
        this.terminalsSourceCode = terminalsSourceCode;
        this.astNodes = astNodes;
    }

    /**
     * Ctor.
     *
     * @param outputPath Output path
     * @param generatorData Generator data
     */
    public Generator(String outputPath, GeneratorData generatorData) {
        this.nonterminals = generatorData.getNonterminals();
        this.nonterminalsSourceCode = generatorData.getNonterminalsSourceCode();
        this.terminals = generatorData.getTerminals();
        this.terminalsCanStartsWith = generatorData.getTerminalsCanStartsWith();
        this.terminalsSourceCode = generatorData.getTerminalsSourceCode();
        this.astNodes = generatorData.getAstNodes();

        this.OUTPUT_PATH = outputPath;
    }

    /**
     * Create new file from template.
     *
     * @param path Directory
     * @param name File name
     * @param template Template name
     */
    public static void createFile(String path, String name, String template) {
        new File(OUTPUT_PATH + path).mkdirs();
        String data = null;
        try {
            data = IOUtils.toString(
                    new FileReader(
                            TEMPLATES_PATH
                                    + template
                                    + ".template"
                    )
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new File(OUTPUT_PATH + path + name).createNewFile();
            FileOutputStream out = new FileOutputStream(OUTPUT_PATH + path + name);
            out.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Append file with content.
     *
     * @param path Directory
     * @param name File name
     * @param string Content
     */
    public static void appendFile(String path, String name, String string) {
        try {
            FileUtils.writeStringToFile(new File(OUTPUT_PATH + path + name), string, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Append file with template.
     *
     * @param path Directory
     * @param name File name
     * @param template Template name
     */
    public static void appendFileWithTemplate(String path, String name, String template) {
        String string = null;
        try {
            string = IOUtils.toString(new FileReader(TEMPLATES_PATH + template + ".template"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.writeStringToFile(new File(OUTPUT_PATH + path + name), string, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate Java project. Main method of this class.
     */
    public void generate() {
        try {
            FileUtils.deleteDirectory(new File(OUTPUT_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        createFile("", "pom.xml", "pom.xml");
        createFile("src/main/java/", "Token.java", "Token.java");
        createFile("src/main/java/", "Main.java", "Main.java");
        createTokenTypeFile();
        createLexerFile();
        createFile("src/main/java/", "AST.java", "AST");
        createParserFile();
        createASTTreeNodeFiles();
        createInterpreterFile();
    }

    /**
     * Generate Interpreter.java file.
     */
    private void createInterpreterFile() {
        String visitMethodContent = "";
        for (String node : astNodes.keySet()) {
            visitMethodContent += "if (node instanceof AST" + node + ") {\n";
            visitMethodContent += "    return visit" + node + "((AST" + node + ") node);\n";
            visitMethodContent += "}\n";
        }
        if (!visitMethodContent.equals("")) {
            visitMethodContent += "else {\n";
            visitMethodContent += "    return \"\";\n";
            visitMethodContent += "}\n";
        }

        String visitMethodsStubs = "";
        for (String node : astNodes.keySet()) {
            visitMethodsStubs += "public String visit" + node + "(AST" + node + " node) {\n";
            visitMethodsStubs += "    String result = \"\";\n";
            visitMethodsStubs += "    return result;\n";
            visitMethodsStubs += "}\n\n";
        }

        createFile("src/main/java/", "Interpreter.java", "InterpreterHeader");
        appendFile("src/main/java/", "Interpreter.java", "public String visit(AST node) {\n");
        appendFile("src/main/java/", "Interpreter.java", visitMethodContent);
        appendFile("src/main/java/", "Interpreter.java", "}\n");
        appendFile("src/main/java/", "Interpreter.java", visitMethodsStubs);
        appendFileWithTemplate("src/main/java/", "Interpreter.java", "InterpreterFooter");
    }

    /**
     * Generate AST*.java files.
     */
    private void createASTTreeNodeFiles() {
        for (String node : astNodes.keySet()) {
            Integer numArguments = astNodes.get(node).size();
            LinkedList<String> types = astNodes.get(node);

            // constructor args
            String argsString = "";
            for (int i = 0; i < numArguments; i++) {
                argsString += types.get(i).equals("n") ? "AST node" + i + ", " : "Token node" + i + ", ";
            }
            if (argsString.length() > 2) {
                argsString = argsString.substring(0, argsString.length() - 2);
            }

            // fields
            String fieldsString = "";
            for (int i = 0; i < numArguments; i++) {
                fieldsString += types.get(i).equals("n") ?
                        "private AST node" + i + ";\n" :
                        "private Token node" + i + ";\n";
            }

            // constructor content
            String constructorContent = "";
            for (int i = 0; i < numArguments; i++) {
                constructorContent += "this.node" + i + " = node" + i + ";\n";
            }

            String filename = "AST" + node + ".java";
            createFile("src/main/java/", filename, "Empty");
            appendFile("src/main/java/", filename, "public class AST" + node + " implements AST {\n");
            appendFile("src/main/java/", filename, fieldsString);
            appendFile("src/main/java/", filename, "public AST" + node + "(" + argsString + ") {\n");
            appendFile("src/main/java/", filename, constructorContent);
            appendFile("src/main/java/", filename, "}\n");
            appendFile("src/main/java/", filename, "}\n");
        }
    }

    /**
     * Generate TokenType.java file.
     */
    public void createTokenTypeFile() {
        createFile("src/main/java/", "TokenType.java", "TokenTypeHeader");
        for (String t : terminals) {
            appendFile("src/main/java/", "TokenType.java", "    " + t + ",\n");
        }
        appendFile("src/main/java/", "TokenType.java", "    EOF\n}");
    }

    /**
     * Generate Lexer.java file.
     */
    public void createLexerFile() {
        createFile("src/main/java/", "Lexer.java", "LexerHeader");
        for (String terminal : terminals) {
            String line =
                    "    public String " +
                            terminal.toLowerCase() +
                            "() throws Exception {\n" +
                            "    String result = \"\";\n" +
                            terminalsSourceCode.get(terminal) + "\n" +
                            "    return result;" +
                            "    }\n\n";
            appendFile("src/main/java/", "Lexer.java", line);
        }
        appendFile("src/main/java/", "Lexer.java",
                "    public Token getNextToken() throws Exception {\n" +
                        "        while (this.currentChar != null) {\n" +
                        "             if (Character.isWhitespace(this.currentChar)) {\n" +
                        "                 this.skipWhitespace();\n" +
                        "                 continue;\n" +
                        "             }\n");

        for (String terminal : terminals) {
            String orExpr = "";

            String cases = terminalsCanStartsWith.get(terminal);
            for (int i = 0; i < cases.length(); i++) {
                orExpr += "this.currentChar.equals('" + cases.charAt(i) + "') || ";
            }
            orExpr = orExpr.substring(0, orExpr.length() - 4);
            appendFile("src/main/java/", "Lexer.java", "if (" + orExpr + ") {\n");
            appendFile("src/main/java/", "Lexer.java",
                    "    return new Token(TokenType." + terminal + ", this." + terminal.toLowerCase() + "());\n");
            appendFile("src/main/java/", "Lexer.java", "}\n\n");
        }

        appendFile("src/main/java/", "Lexer.java", "            this.error();\n        }\n");
        appendFile("src/main/java/", "Lexer.java", "        return new Token(TokenType.EOF, null);\n");
        appendFile("src/main/java/", "Lexer.java", "    }\n");
        appendFileWithTemplate("src/main/java/", "Lexer.java", "LexerFooter");
    }

    /**
     * Generate parser.java file.
     */
    public void createParserFile() {
        createFile("src/main/java/", "Parser.java", "ParserHeader");

        appendFile("src/main/java/", "Parser.java",
            "    public AST parse() throws Exception {\n"
            + "        AST node = this." + nonterminals.iterator().next() + "();\n"
            + "        if (this.currentToken.type != TokenType.EOF) {\n"
            + "            this.error();\n"
            + "        }\n"
            + "        return node;\n"
            + "    }\n");
        for (String nonterminal : nonterminals) {
            String str = "public AST "
                + nonterminal
                + "() throws Exception {\n"
                + nonterminalsSourceCode.get(nonterminal)
                + "\n"
                + "}\n\n";
            appendFile("src/main/java/", "Parser.java", str);
        }
        appendFileWithTemplate("src/main/java/", "Parser.java", "ParserFooter");
    }
}
