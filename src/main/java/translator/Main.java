package translator;

import representation2.Generator;
import representation2.GeneratorData;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String source =
                "expr   := term *((PLUS | MINUS) term);\n" +
                        "term   := factor *((MUL | DIV) factor);\n" +
                        "factor := (PLUS | MINUS) factor | INTEGER | LPAREN expr RPAREN;";

        String source2 =
                "factor := INTEGER | ((PLUS | MINUS)) factor |  LPAREN expr RPAREN;";

        String source3 =
                "expr   := term *((PLUS | MINUS) | term);\n" +
                        "term   := factor *((MUL | DIV) *(factor));\n" +
                        "factor := (PLUS | MINUS) factor | INTEGER | LPAREN expr RPAREN;";

        String futureSource1 =
                "expr   := term *((PLUS | MINUS) | term);\n" +
                        "term   := factor *((MUL | DIV) *(factor));\n" +
                        "factor := (PLUS | MINUS) factor | INTEGER | LPAREN expr RPAREN;\n" +
                        "\n" +
                        "PLUS := '+';\n" +
                        "MINUS := '-';\n" +
                        "MUL := '*';\n" +
                        "DIV := '/';\n" +
                        "EQ := ':=';\n" +
                        "LPAREN := '(';\n" +
                        "RPAREN := ')';\n" +
                        "INTEGER := ('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9') *('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9');";

        Lexer lexer = new Lexer(futureSource1);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        String result = interpreter.interpret();
        System.out.println(result);


        /*Set<String> nonterminals = new LinkedHashSet<String>(Arrays.asList(
                "expr", "term", "factor"
        ));*/

        Map<String, String> nonterminalsSourceCode = new HashMap<String, String>() {{
            put("expr", "// todo: fill me");
            put("term", "// todo: fill me");
            put("factor", "// todo: fill me");
        }};

        /*Set<String> terminals = new LinkedHashSet<String>(Arrays.asList(
                "PLUS", "MINUS", "MUL", "DIV", "EQ", "RPAREN", "LPAREN", "INTEGER"
        ));*/

        Map<String, String> terminalsCanStartsWith = new HashMap<String, String>() {{
            put("PLUS", "+");
            put("MINUS", "-");
            put("MUL", "*");
            put("DIV", "/");
            put("EQ", ":");
            put("LPAREN", "(");
            put("RPAREN", ")");
            put("INTEGER", "1234567890");
        }};

        Map<String, String> terminalsSourceCode = new HashMap<String, String>() {{
            put("PLUS", "// todo: fill me");
            put("MINUS", "// todo: fill me");
            put("MUL", "// todo: fill me");
            put("DIV", "// todo: fill me");
            put("EQ", "// todo: fill me");
            put("LPAREN", "// todo: fill me");
            put("RPAREN", "// todo: fill me");
            put("INTEGER", "// todo: fill me");
        }};

        GeneratorData generatorData = new GeneratorData(
                interpreter.getGeneratorData().getNonterminals(),
                nonterminalsSourceCode,
                interpreter.getGeneratorData().getTerminals(),
                terminalsCanStartsWith,
                terminalsSourceCode);

        Generator generator = new Generator(generatorData);
        generator.generate();
    }
}
