package dev.morling.onebrc.generate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateMeasurementsTest {
    @Test
    public void testGenerate() throws IOException {
        final Path path = Paths.get("data", "measurements_seed.csv");

        final TemperatureGenerator temperatureGenerator = new TemperatureGenerator1(path.toFile());
        assertEquals("Paris;23.8", temperatureGenerator.generate().toString());
    }
}
