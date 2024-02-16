package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationHashKey;
import dev.morling.onebrc.model.StationStatistics;
import dev.morling.onebrc.model.StationTemperature;

import java.util.HashMap;
import java.util.Map;

public class StationTemperatureAggregator implements StationStatisticsAggregator {
    private final Map<StationHashKey, StationStatistics> stationStatisticsMap = new HashMap<>();

    @Override
    public void addTemperature(final StationTemperature stationTemperature) {
        stationStatisticsMap.computeIfAbsent(stationTemperature.stationHashKey(), StationStatistics::new)
                .add(stationTemperature.temperature());
    }

    @Override
    public void mergeStations(final Map<StationHashKey, StationStatistics> stationMap) {
        stationMap.values()
                .forEach(this::addStatistics);
    }

    @Override
    public Map<StationHashKey, StationStatistics> stationMap() {
        return stationStatisticsMap;
    }

    private void addStatistics(final StationStatistics stationStatistics) {
        stationStatisticsMap.merge(stationStatistics.stationHashKey(), stationStatistics, StationStatistics::add);
    }
}
