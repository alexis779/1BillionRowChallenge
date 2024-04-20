package dev.morling.onebrc.calculate;

import java.util.concurrent.Callable;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

public class ParsingCallable implements Callable<StationOutput> {

    private final StationInput stationInput;
    private final StationFileParser stationFileParser;
    public ParsingCallable(final StationInput stationInput, final StationFileParser stationFileParser) {
        this.stationInput = stationInput;
        this.stationFileParser = stationFileParser;
    }

    @Override
    public StationOutput call() throws Exception {
        return stationFileParser.parse(stationInput);
    }    
}