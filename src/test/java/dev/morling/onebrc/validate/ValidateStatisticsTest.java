package dev.morling.onebrc.validate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ValidateStatisticsTest {
    @Test
    public void testValidate() throws IOException {
        final Path path = Paths.get("data", "output_test.txt");

        final InputStream inputStream1 = new FileInputStream(path.toFile());
        final InputStream inputStream2 = new FileInputStream(path.toFile());
        final ValidateStatistics validateStatistics = new ValidateStatistics();
        Assertions.assertTrue(validateStatistics.isSameOutput(inputStream1, inputStream2));
    }
}
