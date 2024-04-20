package dev.morling.onebrc.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.stream.IntStream;

public class CreateMeasurements {
    public static void main(String[] args) throws IOException {
        final File inputFile = new File(args[0]);
        final TemperatureGenerator temperatureGenerator = new TemperatureGenerator1(inputFile);

        final File outputFile = new File(args[2]);
        final OutputStream outputStream = new FileOutputStream(outputFile);

        final int measurementsCount = Integer.parseInt(args[1]);
        try (PrintWriter printWriter = new PrintWriter(outputStream)) {
            IntStream.range(0, measurementsCount)
                    .forEach(i -> {
                        printWriter.println(temperatureGenerator.generate());
                    });
        }
    }
}
