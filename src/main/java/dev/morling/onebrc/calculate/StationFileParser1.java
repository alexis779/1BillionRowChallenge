package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;
import dev.morling.onebrc.model.StationSortKey;
import dev.morling.onebrc.model.StationTemperature;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StationFileParser1 implements StationFileParser {
    private static final String SEPARATOR = ";";

    private final StationTemperature stationTemperature = new StationTemperature();

    private final StationStatisticsAggregator stationStatisticsAggregator;

    public StationFileParser1(final StationStatisticsAggregator stationStatisticsAggregator) {
        this.stationStatisticsAggregator = stationStatisticsAggregator;
    }

    @Override
    public StationOutput parse(final StationInput stationInput) throws IOException {
        final InputStream inputStream = new FileInputStream(stationInput.file());
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (readLine(bufferedReader)) {}
        }
        return new StationOutput(stationStatisticsAggregator.stationMap());
    }

    private boolean readLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        if (line == null) {
            return false;
        }

        parseLine(line);
        return true;
    }

    private void parseLine(final String line) {
        int separatorIndex = line.indexOf(SEPARATOR);
        final String name = line.substring(0, separatorIndex);
        final String temperatureString = line.substring(separatorIndex + 1);
        final double temperature = Double.parseDouble(temperatureString);
        stationTemperature.stationHashKey(new StationSortKey(name));
        stationTemperature.temperature(temperature);

        // notify the callback
        stationStatisticsAggregator.addTemperature(stationTemperature);
    }
}
