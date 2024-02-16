package dev.morling.onebrc.calculate;

import dev.morling.onebrc.model.StationInput;
import dev.morling.onebrc.model.StationOutput;

import java.io.IOException;

public interface StationFileParser {
    StationOutput parse(StationInput input) throws IOException;
}
