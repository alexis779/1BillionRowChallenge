package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

import java.io.IOException;
import java.util.stream.IntStream;

public class StationFileParser3 implements StationFileParser {

    /**
     * Number of concurrent parsers.
     */
    private int concurrency;

    public StationFileParser3(final int concurrency) {
        this.concurrency = concurrency;
    }

    @Override
    public StationOutput parse(final StationInput stationInput) {
        final long size = stationInput.file()
                .length();

        if (size <= StationFileParser2.BUFFER_SIZE) {
            concurrency = 1;
        }

        final StationStatisticsAggregator stationTemperatureAggregator = new StationTemperatureAggregator();
        IntStream.range(0, concurrency)
                .mapToObj(i -> runSync(stationInput, size, i, concurrency))
                .parallel()
                .map(StationOutput::stationsMap)
                .forEach(stationTemperatureAggregator::mergeStations);

        return new StationOutput(stationTemperatureAggregator.stationMap());
    }

    private StationOutput runSync(final StationInput stationInput,
                                   final long size,
                                   final int i,
                                   final int concurrency) {
        final long start = size * i / concurrency;
        final long end = size * (i+1) / concurrency;

        final StationInput futureStationInput = new StationInput(stationInput.file(), start, end);

        final StationFileParser stationFileParser = new StationFileParser2();

        try {
            return stationFileParser.parse(futureStationInput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
