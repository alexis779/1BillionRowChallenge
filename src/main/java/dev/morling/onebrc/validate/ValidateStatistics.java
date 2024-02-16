package dev.morling.onebrc.validate;

import dev.morling.onebrc.model.StationSortKey;
import dev.morling.onebrc.model.StationStatistics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValidateStatistics {
    public static void main(String[] args) throws IOException {
        final InputStream inputStream1 = new FileInputStream(args[0]);
        final InputStream inputStream2 = new FileInputStream(args[1]);
        if (new ValidateStatistics().isSameOutput(inputStream1, inputStream2)) {
            System.out.println("Output is the same");
        } else {
            System.out.println("Output mismatch");
        }
    }

    public boolean isSameOutput(final InputStream inputStream1, final InputStream inputStream2) throws IOException {
        List<StationStatistics> stations1 = parseStationStatistics(inputStream1);
        List<StationStatistics> stations2 = parseStationStatistics(inputStream2);
        if (stations1.size() != stations2.size()) {
            return false;
        }

        return IntStream.range(0, stations1.size())
                .allMatch(i -> isSameStation(stations1.get(i), stations2.get(i)));
    }

    private boolean isSameStation(final StationStatistics station1, final StationStatistics station2) {
        return station1.stationHashKey().equals(station2.stationHashKey()) &&
                station1.min() == station2.min() &&
                station1.mean() == station2.mean() &&
                station1.max() == station2.max();
    }

    private List<StationStatistics> parseStationStatistics(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));
        final String line = bufferedReader.readLine();

        String[] tokens = line.split(", ");

        tokens[0] = tokens[0].replace("{", "");
        tokens[tokens.length-1] = tokens[tokens.length-1].replace("}", "");

        return Arrays.stream(tokens)
                .map(this::parseStationStatistics)
                .collect(Collectors.toList());
    }

    private StationStatistics parseStationStatistics(final String stationString) {
        final String[] tokens = stationString.split("=");
        final String name = tokens[0];
        final String[] temperatureStrings = tokens[1].split("/");
        final double min = Double.parseDouble(temperatureStrings[0]);
        final double mean = Double.parseDouble(temperatureStrings[1]);
        final double max = Double.parseDouble(temperatureStrings[2]);
        return new StationStatistics(new StationSortKey(name), min, mean, max);
    }
}
