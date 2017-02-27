package translator;

import representation2.Generator;
import representation2.GeneratorData;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main.
 */
public final class Main {
    /**
     * Output stream.
     */
    private final PrintStream stdout;

    /**
     * Input arguments.
     */
    private final String[] args;

    /**
     * Entry point.
     *
     * @param stdout Output stream
     * @param args   Input arguments
     */
    public Main(final PrintStream stdout, final String... args) {
        this.stdout = stdout;
        this.args = args;
    }

    /**
     * Entry point.
     *
     * @param args Input arguments
     */
    public static void main(final String... args) throws Exception {
        new Main(System.out, args).exec();
    }

    /**
     * Entry point.
     */
    public void exec() throws Exception {
        if (args.length == 2) {
            final String program = Files.readAllLines(new File(args[0]).toPath()).stream().collect(Collectors.joining("\n"));

            Lexer lexer = new Lexer(program);
            Parser parser = new Parser(lexer);
            Interpreter interpreter = new Interpreter(parser);
            String result = interpreter.interpret();

            GeneratorData generatorData = new GeneratorData(
                    interpreter.getGeneratorData().getNonterminals(),
                    interpreter.getGeneratorData().getNonterminalsCanStartsWith(),
                    interpreter.getGeneratorData().getNonterminalsSourceCode(),
                    interpreter.getGeneratorData().getTerminals(),
                    interpreter.getGeneratorData().getTerminalsCanStartsWith(),
                    interpreter.getGeneratorData().getTerminalsSourceCode(),
                    interpreter.getGeneratorData().getAstNodes());

            Generator generator = new Generator(args[1] + "/", generatorData);
            generator.generate();
        }
    }
}
