public class Main {
    public static void main(String[] args) throws Exception {
        String source =
                "expr   := term ((PLUS | MINUS) term)*\n" +
                "term   := factor ((MUL | DIV) factor)*\n" +
                "factor := (PLUS | MINUS) factor | INTEGER | LPAREN expr RPAREN";

        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        interpreter.interpret();
    }
}
