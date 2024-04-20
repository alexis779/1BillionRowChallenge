package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationKey;
import dev.morling.onebrc.model.StationStatistics;
import dev.morling.onebrc.model.StationTemperature;

import java.util.HashMap;
import java.util.Map;

public class MapStationStatisticsAggregator implements StationStatisticsAggregator {
    private final Map<StationKey, StationStatistics> stationStatisticsMap = new HashMap<>();

    @Override
    public void addTemperature(final StationTemperature stationTemperature) {
        final StationKey stationHashKey = stationTemperature.stationHashKey();
        StationStatistics stationStatistics = stationStatisticsMap.get(stationHashKey);
        if (stationStatistics == null) {
            stationStatistics = new StationStatistics(stationHashKey.duplicate());
            stationStatisticsMap.put(stationStatistics.stationHashKey(), stationStatistics);
        }

        stationStatistics.add(stationTemperature.temperature());
    }

    @Override
    public void mergeStations(final Map<StationKey, StationStatistics> stationMap) {
        stationMap.values()
                .forEach(this::addStatistics);
    }

    @Override
    public Map<StationKey, StationStatistics> stationMap() {
        return stationStatisticsMap;
    }

    private void addStatistics(final StationStatistics stationStatistics) {
        stationStatisticsMap.merge(stationStatistics.stationHashKey(), stationStatistics, StationStatistics::add);
    }
}
