package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationKey;
import dev.morling.onebrc.model.StationStatistics;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class StationWriter {
    /**
     * Let's define `decimals` as the number of digits to keep in the fractional part of the number.
     * Here `decimals = 1`.
     * The multiplying factor before rounding the fraction is `10 ** decimals`.
     */
    private static final int FRACTIONS = 10;

    public void printStations(final Map<StationKey, StationStatistics> stationMap, final OutputStream outputStream) {
        try (PrintWriter printWriter = new PrintWriter(outputStream)) {
            printWriter.print("{");

            final String stationOutput = stationMap.values()
                    .stream()
                    .sorted(Comparator.comparing(stationStatistics -> stationStatistics.stationHashKey().name()))
                    .map(this::formatStatistics)
                    .collect(Collectors.joining(", "));

            printWriter.print(stationOutput);

            printWriter.println("}");
        }
    }

    private String formatStatistics(StationStatistics stationStatistics) {
        double min = round(stationStatistics.min());
        double mean = round(round(stationStatistics.sum()) / stationStatistics.count());
        double max = round(stationStatistics.max());
        return String.format("%s=%.1f/%.1f/%.1f", stationStatistics.stationHashKey().name(), min, mean, max);
    }

    public static double round(double d) {
        d *= FRACTIONS;
        int di = (int) d;
        di += roundUp(d - di);
        return 1d * di / FRACTIONS;
    }


    /**
     * To break up ties, always round up.
     * @param f the fractional part of the number
     *
     * @return the offset to add to the integer part of the number
     */
    private static int roundUp(double f) {
        if (f >= 0) {
            return f >= 0.5 ? 1 : 0;
        } else {
            return f < -0.5 ? -1 : 0;
        }
    }
}
