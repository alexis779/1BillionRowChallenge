package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.ByteArrayHashKey;
import dev.morling.onebrc.model.StationTemperature;

public class StationTemperatureParser {

    public static final byte LINE_FEED = (byte) '\n';
    private static final byte SEMI_COLON = (byte) ';';
    private static final byte MINUS = (byte) '-';
    private static final byte DOT = (byte) '.';
    private static final byte ZERO = (byte) '0';

    private static final int BASE = 10;

    private final StationTemperature stationTemperature = new StationTemperature();

    private final ByteBuffer byteBuffer;
    private final StationStatisticsAggregator stationStatisticsAggregator;
    private final long end;
    public StationTemperatureParser(final ByteBuffer byteBuffer,
                                    final StationStatisticsAggregator stationStatisticsAggregator) {
        this(byteBuffer, stationStatisticsAggregator, -1);
    }

    public StationTemperatureParser(final ByteBuffer byteBuffer,
                                    final StationStatisticsAggregator stationStatisticsAggregator,
                                    final long end) {
        this.byteBuffer = byteBuffer;
        this.stationStatisticsAggregator = stationStatisticsAggregator;
        this.end = end;
    }

    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    public boolean parseLines() {
        while (byteBuffer.hasNext() && beforeEnd()) {
            parseLine();
        }
        return beforeEnd();
    }

    private boolean beforeEnd() {
        return end == -1 || byteBuffer.offset() < end;
    }

    private void parseLine() {
        final byte[] name = scanStationName();
        final double temperature = scanDouble();
        stationTemperature.stationHashKey(new ByteArrayHashKey(name));
        stationTemperature.temperature(temperature);

        // notify the callback
        stationStatisticsAggregator.addTemperature(stationTemperature);
    }

    private byte[] scanStationName() {
        // parse station name
        int start = byteBuffer.offset();
        byteBuffer.scan(SEMI_COLON);
        int length = byteBuffer.offset() - start;
        final byte[] name = new byte[length];
        System.arraycopy(byteBuffer.buffer(), start, name, 0, length);
        byteBuffer.skip();
        return name;
    }

    private double scanDouble() {
        int start = byteBuffer.offset();
        byteBuffer.scan(LINE_FEED);
        final double temperature = parseDouble(byteBuffer.buffer(), start, byteBuffer.offset()-1);
        byteBuffer.skip();
        return temperature;
    }

    /**
     * TODO this double string parsing suffers from precision loss.
     *
     * Rounding will fail on "-0.75" -> -0.7500000000000001 -> -0.8
     * The correct rounding for -0.75 is -0.7.
     *
     * @param dBuffer
     * @param start
     * @param end
     * @return
     */
    public static double parseDouble(byte[] dBuffer, int start, int end) {
        /*
        final byte[] doubleBuffer = new byte[end+1-start];
        System.arraycopy(dBuffer, start, doubleBuffer, 0, end+1-start);
        return Double.parseDouble(new String(doubleBuffer));
         */
        if (dBuffer[start] == MINUS) {
            return -parsePositiveDouble(dBuffer, start+1, end);
        }
        return parsePositiveDouble(dBuffer, start, end);
    }
    private static double parsePositiveDouble(byte[] dBuffer, int start, int end) {
        int offset = start+1;
        while (dBuffer[offset] != DOT) {
            offset++;
        }
        return toDouble(dBuffer, start, offset, end);
    }

    private static double toDouble(byte[] dBuffer, int start, int offset, int end) {
        int i = parseInt(dBuffer, start, offset-1);
        double f = parseFraction(dBuffer, offset+1, end);
        return i + f;
    }

    private static int parseInt(byte[] dBuffer, int start, int end) {
        /*
        int i = 0;
        int pow = 1;
        for (int digit = end; digit >= start; digit--) {
            i += pow * parseDigit(dBuffer[digit]);
            pow *= BASE;
        }
        return i;
         */
        if (end - start > 1) {
            throw new RuntimeException("Int size not supported");
        }

        int digit1 = parseDigit(dBuffer[end]);
        if (start == end) {
            return digit1;
        }

        int digit2 = parseDigit(dBuffer[start]);
        return BASE * digit2 + digit1;
    }

    private static double parseFraction(byte[] dBuffer, int start, int end) {
        /*
        double f = 0;
        double invPow = 1d / BASE;
        for (int decimal = start; decimal <= end; decimal++) {
            f += invPow * parseDigit(dBuffer[decimal]);
            invPow /= BASE;
        }
        return f;

         */
        if (end - start > 0) {
            throw new RuntimeException("Fraction size not supported");
        }

        return 1d * parseDigit(dBuffer[start]) / BASE;
    }

    private static int parseDigit(byte dByte) {
        return dByte - ZERO;
    }
}
