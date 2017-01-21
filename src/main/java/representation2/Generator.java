package representation2;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Generator {
    private static final String TEMPLATES_PATH = "src/main/resources/representation2/";
    private static final String OUTPUT_PATH = "output/";

    private Set<String> nonterminals = new LinkedHashSet<String>();
    private Map<String, String> nonterminalsSourceCode = new HashMap<String, String>();
    private Set<String> terminals = new LinkedHashSet<String>();
    private Map<String, String> terminalsCanStartsWith = new HashMap<String, String>();
    private Map<String, String> terminalsSourceCode = new HashMap<String, String>();

    public Generator(Set<String> nonterminals, Map<String, String> nonterminalsSourceCode, Set<String> terminals, Map<String, String> terminalsCanStartsWith, Map<String, String> terminalsSourceCode) {
        this.nonterminals = nonterminals;
        this.nonterminalsSourceCode = nonterminalsSourceCode;
        this.terminals = terminals;
        this.terminalsCanStartsWith = terminalsCanStartsWith;
        this.terminalsSourceCode = terminalsSourceCode;
    }

    public Generator(GeneratorData generatorData) {
        this.nonterminals = generatorData.getNonterminals();
        this.nonterminalsSourceCode = generatorData.getNonterminalsSourceCode();
        this.terminals = generatorData.getTerminals();
        this.terminalsCanStartsWith = generatorData.getTerminalsCanStartsWith();
        this.terminalsSourceCode = generatorData.getTerminalsSourceCode();
    }

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
    }

    public void createTokenTypeFile() {
        createFile("src/main/java/", "TokenType.java", "TokenTypeHeader");
        for (String t : terminals) {
            appendFile("src/main/java/", "TokenType.java", "    " + t + ",\n");
        }
        appendFile("src/main/java/", "TokenType.java", "    EOF\n}");
    }

    public void createLexerFile() {
        createFile("src/main/java/", "Lexer.java", "LexerHeader");
        for (String terminal : terminals) {
            String line =
                    "    public String " +
                            terminal.toLowerCase() +
                            "() throws Exception {\n" +
                            "    this.advance();\n" +
                            terminalsSourceCode.get(terminal) + "\n" +
                            "    this.error();\n" +
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
            orExpr = orExpr.substring(0, orExpr.length()-4);
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

    public void createParserFile() {
        createFile("src/main/java/", "Parser.java", "ParserHeader");

        appendFile("src/main/java/", "Parser.java",
                "    public AST parse() throws Exception {\n" +
                "        AST node = this." + nonterminals.iterator().next() + "();\n" +
                "        if (this.currentToken.type != TokenType.EOF) {\n" +
                "            this.error();\n" +
                "        }\n" +
                "        return node;\n" +
                "    }\n");
        for (String nonterminal : nonterminals) {
            String str = "public AST " + nonterminal + "() throws Exception {\n" +
                    nonterminalsSourceCode.get(nonterminal) + "\n" +
                    "}\n\n";
            appendFile("src/main/java/", "Parser.java", str);
        }
        appendFileWithTemplate("src/main/java/", "Parser.java", "ParserFooter");
    }

    public static void createFile(String path, String name, String template) {
        new File(OUTPUT_PATH + path).mkdirs();
        String data = null;
        try {
            data = IOUtils.toString(new FileReader(TEMPLATES_PATH + template + ".template"));
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

    public static void appendFile(String path, String name, String string) {
        try {
            FileUtils.writeStringToFile(new File(OUTPUT_PATH + path + name), string, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}