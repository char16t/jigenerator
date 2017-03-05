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
package translator;

import representation2.Generator;
import representation2.GeneratorData;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Main.
 *
 * @author Valeriy Manenkov (v.manenkov@gmail.com)
 * @version $Id$
 * @since 0.1
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
            final String program = Files.readAllLines(
                new File(args[0]).toPath()
            ).stream().collect(Collectors.joining("\n"));

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
