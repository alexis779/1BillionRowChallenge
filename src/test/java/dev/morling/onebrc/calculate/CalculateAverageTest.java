package dev.morling.onebrc.calculate;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.morling.onebrc.calculate.StationWriter.round;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateAverageTest {

    /**
     * The rounding of output values must be done using the semantics of IEEE 754 rounding-direction "roundTowardPositive".
     */
    @Test
    public void testRounding() {
        assertEquals(1.2, round(1.20));
        assertEquals(1.2, round(1.21));
        assertEquals(1.2, round(1.24));
        assertEquals(1.3, round(1.25));
        assertEquals(1.3, round(1.26));
        assertEquals(1.3, round(1.29));

        assertEquals(-1.2, round(-1.20));
        assertEquals(-1.2, round(-1.21));
        assertEquals(-1.2, round(-1.24));
        assertEquals(-1.2, round(-1.25));
        assertEquals(-1.3, round(-1.26));
        assertEquals(-1.3, round(-1.29));
    }

    @Test
    public void testDoubleParsing() {
        int base = 10;

        double f = 0;
        double invPow = 1d / base;
        f += invPow * 7;
        invPow /= base;

        f += invPow * 5;

        f = -f;

        double f2 = Double.parseDouble("-0.7500");

        System.err.println(f + " " + f2);
        //assertEquals(-0.7, 1d * Math.round(10*f) / 10);
    }

    @Test
    public void testParsing() throws IOException {
        final Path path = Paths.get("data", "measurements_test.csv");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        new CalculateAverage()
                .run(path.toFile(), byteArrayOutputStream);

        final String station0 = "Vohipeno=-22.3/-19.7/-17.2";
        final String station1 = "Ōno=35.5/35.8/36.0";

        assertEquals(String.format("{%s, %s}\n", station0, station1), byteArrayOutputStream.toString());
    }
}
