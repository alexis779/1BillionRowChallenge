package dev.morling.onebrc.calculate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

public abstract class AbstractConcurrentParser implements StationFileParser {

    /**
     * Number of worker threads.
     */
    private final int concurrency;

    /**
     * Size of a buffer used to find the next end of line while splitting.
     */
    protected final int splitBufferSize;

    public AbstractConcurrentParser(final int concurrency, final int splitBufferSize) {
        this.concurrency = concurrency;
        this.splitBufferSize = splitBufferSize;
    }

    public static int ceilingDivide(long a, long b) {
        return (int) ((a + b - 1) / b);
    }
    public static int ceilingDivide(int a, int b) {
        return (a + b - 1) / b;
    }

    @Override
    public StationOutput parse(final StationInput stationInput)  throws IOException {
        final int totalSplits = adjustTotalSplits(concurrency, stationInput);

        // make sure each splits ends on a full line
        final StationFileSplitter stationFileSplitter = new StationFileSplitter(splitBufferSize);
        final List<Long> offsets = stationFileSplitter.split(stationInput.file(), totalSplits);

        // start concurrent workers
        final ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
        final List<Future<StationOutput>> futures = IntStream.range(0, totalSplits)
            .mapToObj(splitId -> new StationInput(stationInput.file(), offsets.get(splitId), offsets.get(splitId+1)))
            .map(splitStationInput -> new ParsingCallable(splitStationInput, createStationFileParser()))
            .map(executorService::submit)
            .collect(Collectors.toList());

        // wait for each workers to complete their task
        final List<StationOutput> stationOutputs = new ArrayList<>();
        for (final Future<StationOutput> future : futures) {
            try {
                final StationOutput stationOutput = future.get();
                stationOutputs.add(stationOutput);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        final StationStatisticsAggregator stationTemperatureAggregator = new MapStationStatisticsAggregator();
        stationOutputs.stream()
            .map(StationOutput::stationsMap)
            .forEach(stationTemperatureAggregator::mergeStations);

        return new StationOutput(stationTemperatureAggregator.stationMap());
    }

    protected abstract int adjustTotalSplits(final int totalSplits, final StationInput stationInput);
    protected abstract StationFileParser createStationFileParser();
}
