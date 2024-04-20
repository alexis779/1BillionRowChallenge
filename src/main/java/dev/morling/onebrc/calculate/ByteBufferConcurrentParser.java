package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;

public class ByteBufferConcurrentParser extends AbstractConcurrentParser {

    /**
     * 256 MB
     */
    public static final int BUFFER_SIZE = 1 << 28;

    /**
     * Size of each chunks within the split.
     */
    private final int bufferSize;

    public ByteBufferConcurrentParser(final int totalSplits) {
        this(totalSplits, BUFFER_SIZE);
    }

    public ByteBufferConcurrentParser(final int totalSplits, final int bufferSize) {
        super(totalSplits, bufferSize);
        this.bufferSize = bufferSize;
    }

    @Override
    protected int adjustTotalSplits(int totalSplits, final StationInput stationInput) {
        final long length = stationInput.file()
                .length();

        // each split should fit at least 1 full buffer, except for the last split
        if (length < bufferSize * totalSplits) {
            totalSplits = ceilingDivide(length, bufferSize);
        }

        return totalSplits;
    }

    @Override
    protected StationFileParser createStationFileParser() {
        return new ByteBufferParser(bufferSize);
    }
}
