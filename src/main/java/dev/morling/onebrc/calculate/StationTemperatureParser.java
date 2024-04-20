package dev.morling.onebrc.calculate;

import java.nio.ByteBuffer;

import dev.morling.onebrc.model.BaseInteger;
import dev.morling.onebrc.model.ByteArrayHashKey;
import dev.morling.onebrc.model.StationTemperature;

public class StationTemperatureParser {

    public static final byte LINE_FEED = (byte) '\n';
    private static final byte SEMI_COLON = (byte) ';';
    private static final byte MINUS = (byte) '-';
    private static final byte DOT = (byte) '.';
    private static final byte ZERO = (byte) '0';

    private static final int BASE = 10;
    private static final int PRIME = 31;

    private final ByteArrayHashKey byteArrayHashKey = new ByteArrayHashKey();
    private final StationTemperature stationTemperature = new StationTemperature(byteArrayHashKey);
    private final BaseInteger baseInteger = new BaseInteger();

    private final ByteBuffer byteBuffer;
    private final StationStatisticsAggregator stationStatisticsAggregator;

    public StationTemperatureParser(final ByteBuffer byteBuffer,
                                    final StationStatisticsAggregator stationStatisticsAggregator) {
        this.byteBuffer = byteBuffer;
        this.stationStatisticsAggregator = stationStatisticsAggregator;
    }

    public void parseLines() {
        while (byteBuffer.hasRemaining()) {
            parseLine();
        }
    }

    private void parseLine() {
        parseStationName();
        stationTemperature.stationHashKey(byteArrayHashKey);

        final double temperature = parseDouble();
        stationTemperature.temperature(temperature);

        // notify the callback
        stationStatisticsAggregator.addTemperature(stationTemperature);
    }

    private void parseStationName() {
        // parse station name
        int start = byteBuffer.position();

        // compute rolling hash while scanning
        int hash = 1;
        byte current;
        while (byteBuffer.hasRemaining() && (current = byteBuffer.get()) != SEMI_COLON) {
            hash = PRIME * hash + current;
        }
        byteArrayHashKey.hashCode(hash);

        int length = byteBuffer.position()-1 - start;
        byteArrayHashKey.length(length);

        // load slice from buffer into the byte array
        byteBuffer.get(start, byteArrayHashKey.key(), 0, length);

        byteArrayHashKey.resetName();
    }

    private double parseDouble() {
        final boolean isNegative = byteBuffer.get(byteBuffer.position()) == MINUS;
        if (isNegative) {
            byteBuffer.get();
        }

        final double temperature = parseInt() + parseFraction();
        return isNegative ? -temperature : temperature;
    }

    private int parseInt() {
        parseBaseInteger();
        return baseInteger.sum();
    }

    /**
     * This recursive function reverts the order of the byte sequence.
     *
     * It needs to reduce the list of digits from lowest order first to highest order last.
     */
    private void parseBaseInteger() {
        final byte current = byteBuffer.get();
        if (current == DOT) {
            baseInteger.power(1);
            baseInteger.sum(0);
            return;
        }
        parseBaseInteger();

        final int digit = parseDigit(current);
        baseInteger.sum(baseInteger.sum() + baseInteger.power() * digit);
        baseInteger.power(BASE * baseInteger.power());
    }

    /**
     * TODO: this fraction string parsing suffers from precision loss in double type.
     *
     * Rounding will fail on "-0.75" -> -0.7500000000000001 -> -0.8
     * The correct rounding for -0.75 is -0.7.
     *
     */
    private double parseFraction() {
        double sum = 0;
        double invPow = 1d / BASE;
        byte current;
        while (byteBuffer.hasRemaining() && (current = byteBuffer.get()) != LINE_FEED) {
            sum += invPow * parseDigit(current);
            invPow /= BASE;
        }
        return sum;
    }

    private int parseDigit(final byte dByte) {
        return dByte - ZERO;
    }
}
