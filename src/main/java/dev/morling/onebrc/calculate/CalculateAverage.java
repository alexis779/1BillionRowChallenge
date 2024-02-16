package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CalculateAverage {
    public static void main(String[] args) throws IOException {
        final String fileName = args[0];
        final File file = new File(fileName);
        final OutputStream outputStream = System.out;

        new CalculateAverage()
                .run(file, outputStream);
    }

    public void run(final File file, final OutputStream outputStream) throws IOException {
        final StationInput input = new StationInput(file);
        final StationFileParser stationFileParser = new StationFileParser2();
        final StationOutput output = stationFileParser.parse(input);

        final StationWriter stationWriter = new StationWriter();
        stationWriter.printStations(output.stationsMap(), outputStream);
    }
}
