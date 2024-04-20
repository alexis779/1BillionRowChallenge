package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBufferParser implements StationFileParser {

    /**
     * 1 MB
     */
    public static final int LINE_SIZE = 1 << 20;

    private final StationStatisticsAggregator stationStatisticsAggregator = new MapStationStatisticsAggregator();

    /**
     * Buffer where file content is loaded.
     */
    private final ByteBuffer byteBuffer;
    /**
     * Parser for all full lines in the chunk.
     */
    private final StationTemperatureParser chunkParser;

    /**
     * Buffer where single line is loaded.
     */
    private final ByteBuffer singleLineByteBuffer = ByteBuffer.allocateDirect(LINE_SIZE);
    /**
     * Parser of the line overlapping 2 consecutive chunks.
     */
    private final StationTemperatureParser singleLineParser = new StationTemperatureParser(singleLineByteBuffer, stationStatisticsAggregator);

    public ByteBufferParser(final int bufferSize) {
        byteBuffer = ByteBuffer.allocateDirect(bufferSize);

        chunkParser = new StationTemperatureParser(byteBuffer, stationStatisticsAggregator);
    }

    @Override
    public StationOutput parse(final StationInput stationInput) throws IOException {
        final FileInputStream inputStream = new FileInputStream(stationInput.file());

        final long start = stationInput.start();
        final long end = stationInput.end();

        // skip the characters before the start offset
        inputStream.skip(start);

        final FileChannel fileChannel = inputStream.getChannel();

        int chunkId = 0;
        // number of chunks within the split
        final int totalChunks = AbstractConcurrentParser.ceilingDivide(end - start, byteBuffer.capacity());

        long offset = start;
        while (true) {
            final int size = loadChunk(fileChannel);
            if (size == -1) {
                break;
            }

            if (offset + size > end) {
                // strip off the characters after the end offset
                int extra = (int) (offset + size - end);
                byteBuffer.limit(size - extra);
            }

            parseChunk(chunkId, totalChunks);

            offset += size;
            if (offset > end) {
                break;
            }
            chunkId++;
        }

        inputStream.close();

        return new StationOutput(stationStatisticsAggregator.stationMap());
    }

    private int loadChunk(final FileChannel fileChannel) throws IOException {
        // read the next chunk
        byteBuffer.clear();

        return fileChannel.read(byteBuffer);
    }

    private void parseChunk(final int chunkId, final int totalChunks) {
        byteBuffer.flip();

        if (chunkId > 0) {
            // store the end of the overlapping line located at the start of the chunk
            trimStart();
            // flush the buffer containing the full line
            singleLineByteBuffer.flip();
            singleLineParser.parseLines();
        }
        if (chunkId+1 < totalChunks) {
            // store the start of the overlapping line located at the end of the chunk
            trimEnd();
        }

        chunkParser.parseLines();
    }

    private void trimStart() {
        // append the first line of the current chunk to the last line of the previous chunk
        final int start = byteBuffer.position();
        scan(byteBuffer, StationTemperatureParser.LINE_FEED);

        final int end = byteBuffer.position();
        byteBuffer.position(start);
        final int limit = byteBuffer.limit();
        byteBuffer.limit(end);

        singleLineByteBuffer.put(byteBuffer);
        byteBuffer.position(end);
        byteBuffer.limit(limit);
    }

    private void trimEnd() {
        // keep track of current position
        final int position = byteBuffer.position();

        // copy the last line of the current chunk
        final int start = reverseIndex(StationTemperatureParser.LINE_FEED) + 1;

        // set to the position where we want to start writing from
        byteBuffer.position(start);

        singleLineByteBuffer.clear();

        // copy the remaining bytes from the source buffer into the target buffer
        singleLineByteBuffer.put(byteBuffer);

        // reset to previous position
        byteBuffer.position(position);

        // truncate buffer to the end on the full line
        byteBuffer.limit(start-1);
    }

    public static void scan(final ByteBuffer byteBuffer, final byte separator) {
        while (byteBuffer.hasRemaining() && byteBuffer.get() != separator) {}
    }

    public int reverseIndex(byte separator) {
        int last = byteBuffer.limit()-1;
        while (last >= byteBuffer.position() && byteBuffer.get(last) != separator) {
            last--;
        }
        if (last == byteBuffer.position() - 1) {
            return -1;
        }
        return last;
    }
}
