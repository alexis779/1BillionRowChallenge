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

import dev.morling.onebrc.calculate.string.SplitParser;
import dev.morling.onebrc.calculate.string.SubstringParser;
import dev.morling.onebrc.model.StationInput;

@BenchmarkMode(Mode.SingleShotTime)
@Fork(1)
@Warmup(iterations=0)
@Measurement(iterations=1)
@State(Scope.Benchmark)
public class ComparativeBenchmark {
    private static final String FILE_PATH = "data/measurements_1B.csv";

    private final StationInput stationInput = new StationInput(new File(FILE_PATH));

    @Benchmark
    public void baseline() throws IOException {
        CalculateAverage_baseline.main(new String[] { FILE_PATH });
    }

    @Benchmark
    public void split() throws IOException {
        final StationFileParser stationFileParser = new SplitParser();
        stationFileParser.parse(stationInput);
    }

    @Benchmark
    public void substring() throws IOException {
        final StationFileParser stationFileParser = new SubstringParser();
        stationFileParser.parse(stationInput);
    }

    @Benchmark
    public void byteBuffer1() throws IOException {
        final StationFileParser stationFileParser = new ByteBufferParser(1);
        stationFileParser.parse(stationInput);
    }

    @Benchmark
    public void byteBuffer6() throws IOException {
        final StationFileParser stationFileParser = new ByteBufferParser(6);
        stationFileParser.parse(stationInput);
    }

    @Benchmark
    public void mmap1() throws IOException {
        final StationFileParser stationFileParser = new MMapConcurrentParser(1);
        stationFileParser.parse(stationInput);
    }

    @Benchmark
    public void mmap6() throws IOException {
        final StationFileParser stationFileParser = new MMapConcurrentParser(6);
        stationFileParser.parse(stationInput);
    }
}
