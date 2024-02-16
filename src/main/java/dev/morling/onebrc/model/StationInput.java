package dev.morling.onebrc.model;

import java.io.File;

public record StationInput(File file, long start, long end) {
    public StationInput(File file) {
        this(file, 0, file.length());
    }
}
