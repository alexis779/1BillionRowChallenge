package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;

public class MMapConcurrentParser extends AbstractConcurrentParser {

    public MMapConcurrentParser(int totalSplits) {
        this(totalSplits, ByteBufferParser.LINE_SIZE);
    }

    public MMapConcurrentParser(int totalSplits, int splitBufferSize) {
        super(totalSplits, splitBufferSize);
    }

    @Override
    protected int adjustTotalSplits(int totalSplits, final StationInput stationInput) {
        final long length = stationInput.file()
                .length();

        // TODO this simplifies implementation. Split buffer is used to find the next line within the split.
        if (length < splitBufferSize * totalSplits) {
            totalSplits = ceilingDivide(length, splitBufferSize);
        }

        // each split should not exceed 2 GB
        final int minSplits = ceilingDivide(length, Integer.MAX_VALUE);
        final int q = ceilingDivide(minSplits, totalSplits);

        // there needs to be at least minSplits and each thread should process the same number of splits
        totalSplits *= q;

        return totalSplits;
    }

    @Override
    protected StationFileParser createStationFileParser() {
        return new MMapParser();
    }
}