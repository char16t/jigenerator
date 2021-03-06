public class Main {
    public static void main(String[] args) throws Exception {
        String source = "";

        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        String result = interpreter.interpret();
        System.out.println(result);

        System.out.println("\n\nHello, Java");
    }
}
