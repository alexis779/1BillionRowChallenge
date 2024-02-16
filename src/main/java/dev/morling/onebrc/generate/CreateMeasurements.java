package dev.morling.onebrc.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.stream.IntStream;

public class CreateMeasurements {
    public static void main(String[] args) throws IOException {
        final File file = new File(args[0]);
        final TemperatureGenerator temperatureGenerator = new TemperatureGenerator1(file);

        final int measurementsCount = Integer.parseInt(args[1]);
        final String outputFile = args[0];

        final OutputStream outputStream = new FileOutputStream(outputFile);
        try (PrintWriter printWriter = new PrintWriter(outputStream)) {
            IntStream.range(0, measurementsCount)
                    .forEach(i -> {
                        printWriter.println(temperatureGenerator.generate());
                    });
        }
    }
}
