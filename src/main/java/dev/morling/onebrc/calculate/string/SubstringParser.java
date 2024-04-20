package dev.morling.onebrc.calculate.string;

public class SubstringParser extends AbstractStringParser {

    @Override
    protected void loadRow(final String line) {
        final int separatorIndex = line.indexOf(SEPARATOR);
        name = line.substring(0, separatorIndex);
        temperatureString = line.substring(separatorIndex + 1);
    }
}
