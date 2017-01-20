package representation2;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Main {
    private static final String TEMPLATES_PATH = "src/main/resources/representation2/";
    private static final String OUTPUT_PATH = "output/";

    private static final List<String> terminals = new LinkedList<String>(Arrays.asList(
            "PLUS", "MINUS", "MUL", "DIV", "EQ", "RPAREN", "LPAREN", "INTEGER"
    ));
    private static final Map<String, String> terminalsCanStartsWith = new HashMap<String, String>() {{
        put("PLUS", "+");
        put("MINUS", "-");
        put("MUL", "*");
        put("DIV", "/");
        put("EQ", ":");
        put("LPAREN", "(");
        put("RPAREN", ")");
        put("INTEGER", "1234567890");
    }};

    public static void main(String[] args) {
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
    }

    public static void createTokenTypeFile() {
        createFile("src/main/java/", "TokenType.java", "TokenTypeHeader");
        for (String t : terminals) {
            appendFile("src/main/java/", "TokenType.java", "    " + t + ",\n");
        }
        appendFile("src/main/java/", "TokenType.java", "    EOF\n}");
    }

    public static void createLexerFile() {
        createFile("src/main/java/", "Lexer.java", "LexerHeader");
        for (String terminal : terminals) {
            String line =
                    "    public String " +
                            terminal.toLowerCase() +
                            "() throws Exception {\n" +
                            "    this.advance();\n" +
                            "    // todo: fill me\n" +
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
