package dev.morling.onebrc.model;

public class StationTemperature {
    private StationHashKey stationHashKey;
    private double temperature;

    public StationTemperature() {
        stationHashKey = null;
        temperature = 0;
    }

    public StationTemperature(StationTemperature stationTemperature) {
        this.stationHashKey = stationTemperature.stationHashKey();
        this.temperature = stationTemperature.temperature();
    }

    public StationHashKey stationHashKey() {
        return stationHashKey;
    }

    public double temperature() {
        return temperature;
    }

    public void stationHashKey(final StationHashKey stationHashKey) {
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
