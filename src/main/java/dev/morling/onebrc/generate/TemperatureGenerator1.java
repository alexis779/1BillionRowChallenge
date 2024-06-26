package dev.morling.onebrc.generate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import dev.morling.onebrc.calculate.StationFileParser;
import dev.morling.onebrc.calculate.StationWriter;
import dev.morling.onebrc.calculate.string.SubstringParser;
import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;
import dev.morling.onebrc.model.StationStatistics;
import dev.morling.onebrc.model.StationTemperature;

public class TemperatureGenerator1 implements TemperatureGenerator {
    private static final Random RANDOM = new Random(0);
    private static final int STD_DEV = 10;
    final StationTemperature stationTemperature = new StationTemperature();
    private final List<StationStatistics> stations;

    public TemperatureGenerator1(final File file) throws IOException {
        final StationFileParser stationFileParser = new SubstringParser();
        final StationOutput stationOutput = stationFileParser.parse(new StationInput(file));
        stations = stationOutput.stationsMap()
                .values()
                .stream()
                .toList();
    }

    @Override
    public StationTemperature generate() {
        int station = RANDOM.nextInt(stations.size());

        final StationStatistics stationStatistics = stations.get(station);
        stationTemperature.stationHashKey(stationStatistics.stationHashKey());

        final double randomTemperature = StationWriter.round(stationStatistics.min()
                + RANDOM.nextGaussian() * STD_DEV);
        stationTemperature.temperature(randomTemperature);

        return stationTemperature;
    }
}
