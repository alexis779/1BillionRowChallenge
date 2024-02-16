package dev.morling.onebrc.model;

import java.util.Map;

public record StationOutput(Map<StationHashKey, StationStatistics> stationsMap) {
}
