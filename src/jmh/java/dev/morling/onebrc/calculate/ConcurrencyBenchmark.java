package dev.morling.onebrc.calculate;

import java.io.File;
import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import dev.morling.onebrc.model.StationInput;

@BenchmarkMode(Mode.SingleShotTime)
@Fork(1)
@Warmup(iterations=0)
@Measurement(iterations=1)
@State(Scope.Benchmark)
public class ConcurrencyBenchmark {
    private static final String FILE_PATH = "data/measurements_1B.csv";

    private final StationInput stationInput = new StationInput(new File(FILE_PATH));

    @Param(value={ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" })
    private int concurrency;

    @Benchmark
    public void byteBuffer() throws IOException {
        final StationFileParser stationFileParser = new ByteBufferConcurrentParser(concurrency);
        stationFileParser.parse(stationInput);
    }

    @Benchmark
    public void mmap() throws IOException {
        final StationFileParser stationFileParser = new MMapConcurrentParser(concurrency);
        stationFileParser.parse(stationInput);
    }
}
