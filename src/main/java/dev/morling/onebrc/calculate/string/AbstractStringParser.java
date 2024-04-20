package dev.morling.onebrc.calculate.string;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import dev.morling.onebrc.calculate.MapStationStatisticsAggregator;
import dev.morling.onebrc.calculate.StationFileParser;
import dev.morling.onebrc.calculate.StationStatisticsAggregator;
import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;
import dev.morling.onebrc.model.StationSortKey;
import dev.morling.onebrc.model.StationTemperature;

public abstract class AbstractStringParser implements StationFileParser {
    protected static final String SEPARATOR = ";";

    private final StationSortKey stationSortKey = new StationSortKey();
    private final StationTemperature stationTemperature = new StationTemperature(stationSortKey);

    private final StationStatisticsAggregator stationStatisticsAggregator = new MapStationStatisticsAggregator();

    protected String name;
    protected String temperatureString;

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
        // store name & temperature attributes
        loadRow(line);

        final double temperature = Double.parseDouble(temperatureString);

        stationSortKey.name(name);
        stationTemperature.temperature(temperature);

        // notify the callback
        stationStatisticsAggregator.addTemperature(stationTemperature);
    }

    protected abstract void loadRow(final String line);
}
