package dev.morling.onebrc.calculate;

import java.io.IOException;
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
    private int concurrency;

    /**
     * Size of a buffer used while splitting.
     */
    private final int splitBufferSize;

    public AbstractConcurrentParser(final int concurrency, final int splitBufferSize) {
        this.concurrency = concurrency;
        this.splitBufferSize = splitBufferSize;
    }

    public static int ceilingDivide(long a, long b) {
        return (int) ((a + b - 1) / b);
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
        final StationStatisticsAggregator stationTemperatureAggregator = new MapStationStatisticsAggregator();
        futures.stream()
            .map(this::futureGet)
            .map(StationOutput::stationsMap)
            .forEach(stationTemperatureAggregator::mergeStations);
        
        executorService.shutdown();

        return new StationOutput(stationTemperatureAggregator.stationMap());
    }

    protected abstract int adjustTotalSplits(final int totalSplits, final StationInput stationInput);
    protected abstract StationFileParser createStationFileParser();

    private StationOutput futureGet(Future<StationOutput> future) {
        try {
            return future.get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            throw new RuntimeException(ie);
        } catch (ExecutionException ee) {
            ee.printStackTrace();
            // TODO if a validation error happens or something goes wrong, this error is swallowed and the main thread gets stuck without throwing any error
            throw new RuntimeException(ee);
        }
    }
}
