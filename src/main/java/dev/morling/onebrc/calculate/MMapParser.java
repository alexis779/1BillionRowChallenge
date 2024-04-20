package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MMapParser implements StationFileParser {

    private final StationStatisticsAggregator stationStatisticsAggregator = new MapStationStatisticsAggregator();

    @Override
    public StationOutput parse(final StationInput stationInput) throws IOException {
        final long start = stationInput.start();
        final long end = stationInput.end();

        // skip the characters before the start offset
        final FileInputStream inputStream = new FileInputStream(stationInput.file());
        inputStream.skip(start);

        // memory map a split of the file
        final FileChannel fileChannel = inputStream.getChannel();
        final ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, start, end-start);

        final StationTemperatureParser stationTemperatureParser = new StationTemperatureParser(byteBuffer, stationStatisticsAggregator);
        stationTemperatureParser.parseLines();

        inputStream.close();

        return new StationOutput(stationStatisticsAggregator.stationMap());
    }
}
