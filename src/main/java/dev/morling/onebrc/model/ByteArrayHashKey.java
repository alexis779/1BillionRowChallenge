package dev.morling.onebrc.model;

import java.util.Arrays;

public class ByteArrayHashKey implements StationHashKey {
    private final byte[] key;
    private final int hashCode;
    private String name;

    public ByteArrayHashKey(final byte[] key) {
        this.key = key;
        hashCode = Arrays.hashCode(key);
    }

    public byte[] key() {
        return key;
    }

    public String name() {
        if (name == null) {
            name = new String(key);
        }
        return name;
    }

    @Override
    public boolean equals(final Object object) {
        final ByteArrayHashKey stationHashKey = (ByteArrayHashKey) object;
        return Arrays.equals(key, stationHashKey.key());
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return name();
    }
}
