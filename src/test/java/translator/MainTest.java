package translator;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainTest {
    @Test
    public void execTest1() throws Exception {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final Path outputDir = Files.createTempDirectory("aaa_");
            final String inputFile = this.getClass().getResource("/translator/source9.grammar").getPath();
            new Main(System.out, inputFile, outputDir.toString()).exec();
            // TODO: Check generated sources
        }
    }
}
