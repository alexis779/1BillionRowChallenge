package dev.morling.onebrc.calculate;

import static dev.morling.onebrc.calculate.StationWriter.round;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

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

        final int bufferSize = 50;
        final StationFileParser stationFileParser = new MMapConcurrentParser(1, bufferSize);
        final CalculateAverage calculateAverage = new CalculateAverage(stationFileParser);
        calculateAverage.parse(path.toFile(), byteArrayOutputStream);

        final String station0 = "Vohipeno=-22.3/-19.7/-17.2";
        final String station1 = "Ōno=35.5/35.8/36.0";

        assertEquals(String.format("{%s, %s}\n", station0, station1), byteArrayOutputStream.toString());
    }

    @Test
    public void testParsingLargeFile() throws IOException {
        final Path path = Paths.get("data", "measurements_head.csv");
        final File file = path.toFile();

        assertEquals(163, file.length());
        final StationInput stationInput = new StationInput(file);

        final int totalSplits = 3;
        final int bufferSize = 50;

        final StationOutput bbOutput = new ByteBufferParser(bufferSize)
            .parse(stationInput);
        final StationOutput bbcOutput = new ByteBufferConcurrentParser(totalSplits, bufferSize)
            .parse(stationInput);
        assertEquals(bbOutput, bbcOutput);

        final StationOutput mmOutput = new MMapParser()
            .parse(stationInput);
        final StationOutput mmcOutput = new MMapConcurrentParser(totalSplits, bufferSize)
            .parse(stationInput);
        assertEquals(mmOutput, mmcOutput);

        assertEquals(bbOutput, mmOutput);
    }

    @Test
    public void coreNumber() {
        final int processors = Runtime.getRuntime().availableProcessors();
        System.err.println(String.format("Number of processors %d", processors));
    }

    @Test
    public void sampleOutput() throws IOException {
        final Path path = Paths.get("data", "measurements_head.csv");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        new CalculateAverage()
                .parse(path.toFile(), byteArrayOutputStream);

        System.err.println(byteArrayOutputStream.toString());
    }

    @Test
    public void testErrorHandling() throws IOException {
        final Path path = Paths.get("data", "measurements_test.csv");
        final StationInput stationInput = new StationInput(path.toFile());

        final StationFileParser stationFileParser = new AbstractConcurrentParser(1, 1024) {

            @Override
            protected int adjustTotalSplits(int totalSplits, StationInput stationInput) {
                return totalSplits;
            }

            @Override
            protected StationFileParser createStationFileParser() {
                return new StationFileParser() {

                    @Override
                    public StationOutput parse(StationInput input) throws IOException {
                        throw new RuntimeException("Something went wrong");
                    }

                };
            }

        };

        final StationOutput stationOutput = stationFileParser.parse(stationInput);
        assertTrue(stationOutput.stationsMap().isEmpty());
    }
}
