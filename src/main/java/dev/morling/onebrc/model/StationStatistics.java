package dev.morling.onebrc.model;

public class StationStatistics {
    private final StationHashKey stationHashKey;
    private int count;
    private double min;
    private double max;
    private double sum;
    private double mean;

    public StationStatistics(final StationHashKey stationHashKey) {
        this.stationHashKey = stationHashKey;
        count = 0;
        sum = 0;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
    }

    public StationStatistics(final StationHashKey stationHashKey, final double min, final double mean, final double max) {
        this.stationHashKey = stationHashKey;
        this.min = min;
        this.mean = mean;
        this.max = max;
    }

    public StationHashKey stationHashKey() {
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
}
