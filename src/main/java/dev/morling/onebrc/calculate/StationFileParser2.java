package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StationFileParser2 implements StationFileParser {

    /**
     * 256 MB
     */
    public static final int BUFFER_SIZE = 1 << 28;

    /**
     * 1 MB
     */
    private static final int LINE_SIZE = 1 << 20;

    private final StationStatisticsAggregator stationStatisticsAggregator = new StationTemperatureAggregator();

    /**
     * Parser for a single line overlapping 2 consecutive chunks.
     */
    private final StationTemperatureParser singleLineParser = new StationTemperatureParser(
            new ByteBuffer(LINE_SIZE), stationStatisticsAggregator);

    @Override
    public StationOutput parse(final StationInput stationInput) throws IOException {
        final File file = stationInput.file();

        final InputStream inputStream = new FileInputStream(file);
        inputStream.skip(stationInput.start());

        final StationTemperatureParser batchParser = createBatchParser(stationStatisticsAggregator, stationInput.end());

        // this is false when we should skip the first line
        boolean parseFirstLine = stationInput.start() == 0;

        while (loadChunk(inputStream, batchParser, parseFirstLine) &&
                batchParser.parseLines()) {
            parseFirstLine = true;
        }
        return new StationOutput(stationStatisticsAggregator.stationMap());
    }

    private StationTemperatureParser createBatchParser(final StationStatisticsAggregator stationStatisticsAggregator, long end) {
        return new StationTemperatureParser(new ByteBuffer(BUFFER_SIZE), stationStatisticsAggregator, end);
    }

    private boolean loadChunk(final InputStream inputStream,
                              final StationTemperatureParser batchParser,
                              final boolean parseFirstLine) throws IOException {
        final ByteBuffer byteBuffer = batchParser.byteBuffer();

        // read the next chunk
        final int size = inputStream.read(byteBuffer.buffer(), 0, BUFFER_SIZE);
        if (size == -1) {
            // parse the last line of the last chunk
            singleLineParser.parseLines();
            return false;
        }

        byteBuffer.offset(0);
        byteBuffer.size(size);

        // adjust beginning and end positions in the buffer
        trimChunk(batchParser, parseFirstLine);

        return true;
    }

    /**
     * Trim the beginning and the end of the chunk to make sure the buffer only contains full lines.
     */
    private void trimChunk(final StationTemperatureParser stationTemperatureParser,
                           final boolean parseFirstLine) {
        final ByteBuffer byteBuffer = stationTemperatureParser.byteBuffer();
        final ByteBuffer singleLineByteBuffer = singleLineParser.byteBuffer();

        byte[] buffer = byteBuffer.buffer();

        // append the 2nd part of the 1st line located at the beginning of the chunk
        int start = byteBuffer.offset();
        byteBuffer.scan(StationTemperatureParser.LINE_FEED);
        int end = byteBuffer.offset();
        int length = end-start;
        int newSize = singleLineByteBuffer.size() + length;
        if (newSize > singleLineByteBuffer.buffer().length) {
            throw new RuntimeException("Trim buffer is full");
        }
        System.arraycopy(buffer, start, singleLineByteBuffer.buffer(), singleLineByteBuffer.size(), length);
        singleLineByteBuffer.size(singleLineByteBuffer.size() + length);

        // parse the first line if it is the beginning of the file
        // otherwise, skip the characters
        if (parseFirstLine) {
            singleLineParser.parseLines();
        }

        singleLineByteBuffer.offset(0);
        singleLineByteBuffer.size(0);

        byteBuffer.skip();

        // append the 1st part of the last line located at the end of the chunk
        start = byteBuffer.reverseIndex(StationTemperatureParser.LINE_FEED) + 1;
        end = byteBuffer.size();
        length = end-start;
        newSize = singleLineByteBuffer.size() + length;
        if (newSize > singleLineByteBuffer.buffer().length) {
            throw new RuntimeException("Trim buffer is full");
        }
        System.arraycopy(buffer, start, singleLineByteBuffer.buffer(), singleLineByteBuffer.size(), length);
        singleLineByteBuffer.size(newSize);
        byteBuffer.size(start);
    }
}
