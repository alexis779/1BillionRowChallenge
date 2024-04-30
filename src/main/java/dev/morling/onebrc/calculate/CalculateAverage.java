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

        final CalculateAverage calculateAverage = new CalculateAverage();
        calculateAverage.parse(file, outputStream);
    }

    public void parse(final File file, final OutputStream outputStream) throws IOException {
        final StationFileParser stationFileParser = new MMapConcurrentParser(6);

        final StationInput stationInput = new StationInput(file);
        final StationOutput stationOutput = stationFileParser.parse(stationInput);

        final StationWriter stationWriter = new StationWriter();
        stationWriter.printStations(stationOutput.stationsMap(), outputStream);
    }
}
