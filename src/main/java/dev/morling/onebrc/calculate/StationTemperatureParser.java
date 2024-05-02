package dev.morling.onebrc.calculate;

import java.nio.ByteBuffer;

import dev.morling.onebrc.model.LongArrayHashKey;
import dev.morling.onebrc.model.StationTemperature;

public class StationTemperatureParser {

    public static final byte LINE_FEED = (byte) '\n';
    private static final byte SEMI_COLON = (byte) ';';
    private static final byte MINUS = (byte) '-';
    private static final byte DOT = (byte) '.';
    private static final byte ZERO = (byte) '0';

    private static final int BASE = 10;
    /**
     * Max number in base 10 supported on 32 bits is 999 999 999.
     * Adding another 9 digit would overflow 4 GB = 2 ** 32
     */
    private static final int MAX_POW = 100_000_000; // 10^8
    private static final int PRIME = 31;

    private final LongArrayHashKey longArrayHashKey = new LongArrayHashKey();
    private final StationTemperature stationTemperature = new StationTemperature(longArrayHashKey);

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
        stationTemperature.stationHashKey(longArrayHashKey);

        final double temperature = parseDouble();
        stationTemperature.temperature(temperature);

        // notify the callback
        stationStatisticsAggregator.addTemperature(stationTemperature);
    }

    private void parseStationName() {
        final int start = byteBuffer.position();

        // rolling hash
        long hashCode = 1;

        // long buffer
        final long[] key = longArrayHashKey.key();
        // number of longs so far
        int longPosition = 0;
        // long being currently loaded
        long longCurrent = 0;
        // byte index in the long
        int j = 0;

        byte current;
        int shiftBits = 0;
        while ((current = byteBuffer.get()) != SEMI_COLON) {
            // apply bit mask, since negative values will have 1's on the left of the binary representation of the byte
            final long b = current & 0xFFL;
            longCurrent |= b << shiftBits;
            shiftBits += LongArrayHashKey.BITS_IN_BYTE;
            j++;

            if (j == LongArrayHashKey.BYTES_IN_LONG) {
                key[longPosition++] = longCurrent;
                hashCode = updateHashCode(hashCode, longCurrent);
                longCurrent = 0;
                j = 0;
                shiftBits = 0;
            }
        }
        if (longCurrent != 0) {
            key[longPosition] = longCurrent;
            hashCode = updateHashCode(hashCode, longCurrent);
        }

        longArrayHashKey.hashCode((int) hashCode);

        final int length = byteBuffer.position()-1 - start;
        longArrayHashKey.length(length);
    }

    private long updateHashCode(final long hashCode, final long current) {
        return (hashCode * PRIME + current) % Integer.MAX_VALUE;
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
        int sum = 0;
        int pow = MAX_POW;
        byte current;
        while ((current = byteBuffer.get()) != DOT) {
            final int digit = parseDigit(current);
            sum += digit * pow;
            pow /= BASE;
        }
        pow *= BASE;
        // truncate the trailing 0s
        sum /= pow;
        return sum;
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
        // need to check if buffer is full for the last line hitting the end of the stream
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
