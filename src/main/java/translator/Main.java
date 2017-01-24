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

        String futureSource2 =
                "expr   := term *((PLUS | MINUS) term);\n" +
                        "term   := factor *((MUL | DIV) factor);\n" +
                        "factor := (PLUS | MINUS) factor | INTEGER | LPAREN expr RPAREN;\n" +
                        "testvalue := (AWA |AWB|AWC|AWD|AWE) *(AWA |AWB|AWC|AWD|AWE);\n" + // todo: see ast tree. bug!
                        "\n" +
                        "PLUS := '+';\n" +
                        "MINUS := '-';\n" +
                        "MUL := '*';\n" +
                        "DIV := '/';\n" +
                        "EQ := ':=';\n" +
                        "LPAREN := '(';\n" +
                        "RPAREN := ')';\n" +
                        "INTEGER := ('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9') *('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9');";

        Lexer lexer = new Lexer(futureSource2);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        String result = interpreter.interpret();
        System.out.println(result);

        GeneratorData generatorData = new GeneratorData(
                interpreter.getGeneratorData().getNonterminals(),
                interpreter.getGeneratorData().getNonterminalsCanStartsWith(),
                interpreter.getGeneratorData().getNonterminalsSourceCode(),
                interpreter.getGeneratorData().getTerminals(),
                interpreter.getGeneratorData().getTerminalsCanStartsWith(),
                interpreter.getGeneratorData().getTerminalsSourceCode());

        Generator generator = new Generator(generatorData);
        generator.generate();
    }
}
