package dev.morling.onebrc.calculate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StationFileSplitter {
    private final ByteBuffer byteBuffer;
    private final int bufferSize;

    public StationFileSplitter() {
        this(ByteBufferParser.LINE_SIZE);
    }

    public StationFileSplitter(int bufferSize) {
        this.bufferSize = bufferSize;
        byteBuffer = ByteBuffer.allocate(bufferSize);
    }

    public List<Long> split(final File file, final int totalSplits) throws IOException {
        final long length = file.length();

        final InputStream inputStream = new FileInputStream(file);
        final long splitLength = length / totalSplits;

        return IntStream.range(0, totalSplits+1)
            .mapToLong(splitId -> seekSplitOffset(inputStream, length, splitLength, splitId, totalSplits))
            .boxed()
            .collect(Collectors.toList());
    }

    private long seekSplitOffset(final InputStream inputStream, final long length, final long splitLength, final int splitId, final int totalSplits) {
        if (splitId == 0) {
            return 0;
        }
        if (splitId == totalSplits) {
            return length;
        }

        final long buffered = (splitId == 1) ? 0 : bufferSize;

        try {
            inputStream.skip(splitLength-buffered);

            final int size = inputStream.read(byteBuffer.array(), 0, bufferSize);
            if (size == -1) {
                throw new RuntimeException("Can not extract split offset");
            }

            byteBuffer.position(size);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        byteBuffer.flip();
        ByteBufferParser.scan(byteBuffer, StationTemperatureParser.LINE_FEED);
        if (! byteBuffer.hasRemaining()) {
            throw new RuntimeException("Can not find the next line separator");
        }
        final int bufferOffset = byteBuffer.position()-1;

        // return an exclusive end offset
        final long offset = splitLength * splitId;
        return offset + bufferOffset + 1;
    }
}
