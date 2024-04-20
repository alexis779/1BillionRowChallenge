package dev.morling.onebrc.calculate.string;

public class SplitParser extends AbstractStringParser {

    @Override
    protected void loadRow(final String line) {
        final String[] tokens = line.split(SEPARATOR);
        name = tokens[0];
        temperatureString = tokens[1];
    }
}
