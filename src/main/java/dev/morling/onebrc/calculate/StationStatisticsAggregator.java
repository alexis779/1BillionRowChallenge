package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationKey;
import dev.morling.onebrc.model.StationStatistics;
import dev.morling.onebrc.model.StationTemperature;

import java.util.Map;

public interface StationStatisticsAggregator {
    void addTemperature(final StationTemperature stationTemperature);
    void mergeStations(final Map<StationKey, StationStatistics> stations);
    Map<StationKey, StationStatistics> stationMap();
}
