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

        Lexer lexer = new Lexer(source3);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        String result = interpreter.interpret();
        System.out.println(result);
    }
}
