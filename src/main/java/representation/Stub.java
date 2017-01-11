package representation;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Stub {
    public static void main(String[] args) {
        List<String> nonterminals = new LinkedList<String>();
        Map<String, String> terminals = new LinkedHashMap<String, String>();

        nonterminals.add("expr");
        nonterminals.add("term");
        nonterminals.add("factor");

        terminals.put("PLUS", "+");
        terminals.put("MINUS", "-");
        terminals.put("MUL", "*");
        terminals.put("DIV", "/");
        terminals.put("EQ", ":=");
        terminals.put("LPAREN", "(");
        terminals.put("RPAREN", ")");
        terminals.put("INTEGER", "aaaaaggrh");

        // ==========================
        // 1. Generate TokenType.java
        // ==========================
        String tokenTypeClass = "public enum TokenType {\n";
        for (String terminal : terminals.keySet()) {
            tokenTypeClass += "    " + terminal + ",\n";
        }
        tokenTypeClass += "\tEOF\n}";
        //System.out.println(tokenTypeClass);


        // ===========================
        // 2. Methods for Lexer.java
        // ===========================
        String lexerClassMethods = "";
        for (String terminal : terminals.keySet()) {
            lexerClassMethods +=
                    "public String " +
                            terminal.toLowerCase() +
                            "() throws Exception {\n" +
                            "    String str = \"" + terminals.get(terminal) + "\";\n" +
                            "    for (int i=0; i < str.length(); ++i) {\n" +
                            "        if (str.charAt(i) != this.currentChar) {\n" +
                            "            error();\n" +
                            "        }\n" +
                            "        this.advance();\n" +
                            "    }\n" +
                            "    return str;\n" +
                            "}\n\n";
        }
        lexerClassMethods += "public Token getNextToken() throws Exception {\n" +
                "    while (this.currentChar != null) {\n" +
                "        if (Character.isWhitespace(this.currentChar)) {\n" +
                "            this.skipWhitespace();\n" +
                "            continue;\n" +
                "        }\n\n";

        for (String terminal : terminals.keySet()) {
            lexerClassMethods +=
                    "        if (this.currentChar.equals('" + terminals.get(terminal).substring(0,1) + "')) {\n" +
                    "            return new Token(TokenType." + terminal.toUpperCase() + ", this." + terminal.toLowerCase() + "());\n" +
                    "        }\n\n";
        }

        lexerClassMethods +=
                "        this.error();\n" +
                "    }\n" +
                "    return new Token(TokenType.EOF, null);\n" +
                "}\n";
        //System.out.println(lexerClassMethods);

        // =========================================
        // 3. Atom operations
        // =========================================
        // atom operations: nonterm
        String nontermSample = "INTEGER";
        String nontermSampleString = "this.eat(TokenType." + nontermSample + ");\n";
        //System.out.println(nontermSampleString);

        // atom operations: or
        List<String> orOperation = new LinkedList<String>();
        orOperation.add("MUL");
        orOperation.add("DIV");
        orOperation.add("OTHER");
        orOperation.add("OP");
        String orOperationString = "";

        orOperationString +=
                "if (this.currentToken.type == TokenType." + orOperation.get(0) + ") {\n" +
                        "    // ...\n" +
                        "}\n";
        for (String operand : orOperation.subList(1,orOperation.size())) {
            orOperationString +=
                    "else if (this.currentToken.type == TokenType." + operand + ") {\n" +
                    "    // ...\n" +
                    "}\n";
        }
        //System.out.println(orOperationString);

        // atom operations: repeat
        List<String> repeatCanStartsWith = new LinkedList<String>();
        repeatCanStartsWith.add("MUL");
        repeatCanStartsWith.add("DIV");
        String repeatOperationString = "while (this.currentToken.type == TokenType." +
                repeatCanStartsWith.get(0);

        for (String variant : repeatCanStartsWith.subList(1, repeatCanStartsWith.size())) {
            repeatOperationString += " || this.currentToken.type == TokenType." + variant;
        }

        repeatOperationString += ") {\n" +
                "}\n";
        System.out.println(repeatOperationString);
    }
}
