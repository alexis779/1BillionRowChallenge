package dev.morling.onebrc.calculate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class StationFileSplitterTest {
    @Test
    public void split() throws IOException {
        final Path path = Paths.get("data", "measurements_head.csv");
        final File file = path.toFile();
        final int bufferSize = 50;
        final StationFileSplitter stationFileSplitter = new StationFileSplitter(bufferSize);
        final int totalSplits = 3;
        final List<Long> offsets = stationFileSplitter.split(file, totalSplits);
        assertEquals(totalSplits+1, offsets.size());

        IntStream.range(0, totalSplits)
            .forEach(splitId -> printSplit(file, offsets.get(splitId), offsets.get(splitId+1)));
    }

    private void printSplit(File file, long start, long end) {
        try {
            printSplitThrows(file, start, end);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void printSplitThrows(File file, long start, long end) throws IOException {
        try(final InputStream inputStream = new FileInputStream(file)) {
            inputStream.skip(start);
            final byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer, 0, (int) (end-start));
            System.err.println(String.format("Split [%d-%d]:\n%s", start, end, new String(buffer, 0, (int) (end-start))));
            assertEquals(StationTemperatureParser.LINE_FEED, buffer[(int) (end-start-1)]);    
        }
    }
}
