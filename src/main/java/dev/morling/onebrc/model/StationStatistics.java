package dev.morling.onebrc.model;

public class StationStatistics {
    private final StationKey stationHashKey;
    private int count;
    private double min;
    private double max;
    private double sum;
    private double mean;

    public StationStatistics(final StationKey stationHashKey) {
        this.stationHashKey = stationHashKey;
        count = 0;
        sum = 0;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
    }

    public StationStatistics(final StationKey stationHashKey, final double min, final double mean, final double max) {
        this.stationHashKey = stationHashKey;
        this.min = min;
        this.mean = mean;
        this.max = max;
    }

    public StationKey stationHashKey() {
        return stationHashKey;
    }

    public int count() {
        return count;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public double sum() {
        return sum;
    }
    public double mean() { return mean; }

    public void add(final double temperature) {
        count++;
        sum += temperature;
        if (temperature < min) {
            min = temperature;
        }
        if (temperature > max) {
            max = temperature;
        }
    }

    public StationStatistics add(final StationStatistics stationStatistics) {
        count += stationStatistics.count();
        sum += stationStatistics.sum();
        if (stationStatistics.min() < min) {
            min = stationStatistics.min();
        }
        if (stationStatistics.max() > max) {
            max = stationStatistics.max();
        }
        return this;
    }

    @Override
    public boolean equals(Object object) {
        final StationStatistics other = (StationStatistics) object;
        return stationHashKey.equals(other.stationHashKey) &&
            min == other.min &&
            mean == other.mean &&
            max == other.max;
    }

    @Override
    public String toString() {
        return String.format("%s=%.1f/%.1f/%.1f", stationHashKey, min, mean, max);
    }
}
