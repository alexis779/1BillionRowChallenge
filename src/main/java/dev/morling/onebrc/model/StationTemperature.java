package dev.morling.onebrc.model;

public class StationTemperature {
    private StationKey stationHashKey;
    private double temperature;

    public StationTemperature() {}

    public StationTemperature(final StationKey stationHashKey) {
        this.stationHashKey = stationHashKey;
        temperature = 0;
    }

    public StationTemperature(final StationTemperature stationTemperature) {
        this.stationHashKey = stationTemperature.stationHashKey();
        this.temperature = stationTemperature.temperature();
    }

    public StationKey stationHashKey() {
        return stationHashKey;
    }

    public double temperature() {
        return temperature;
    }

    public void stationHashKey(final StationKey stationHashKey) {
        this.stationHashKey = stationHashKey;
    }

    public void temperature(final double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return String.format("%s;%.1f", stationHashKey, temperature);
    }
}
