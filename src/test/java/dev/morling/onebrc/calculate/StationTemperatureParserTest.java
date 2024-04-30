package dev.morling.onebrc.calculate;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.morling.onebrc.model.StationKey;
import dev.morling.onebrc.model.StationStatistics;

public class StationTemperatureParserTest {

    @Test
    public void testParseLine() {
        final String city = "México City";
        final String row = String.format("%s;12.7", city);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(row.getBytes());
        final StationStatisticsAggregator stationStatisticsAggregator = new MapStationStatisticsAggregator();
        final StationTemperatureParser stationTemperatureParser = new StationTemperatureParser(byteBuffer, stationStatisticsAggregator);
        stationTemperatureParser.parseLines();

        final Map<StationKey, StationStatistics> stationMap = stationStatisticsAggregator.stationMap();
        final List<String> stationNames = stationMap.keySet()
            .stream()
            .map(StationKey::name)
            .collect(Collectors.toList());
        Assertions.assertEquals(List.of(city), stationNames);
    }
}
