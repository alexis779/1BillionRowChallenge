package dev.morling.onebrc.model;

import java.util.Arrays;

import dev.morling.onebrc.calculate.AbstractConcurrentParser;

public class LongArrayHashKey implements StationKey {
    /**
     * 1 KB = 1024 B = 128 * 8
     */
    private static final int LENGTH = 128;

    /**
     * Number of bytes in a long.
     */
    public static final int BYTES_IN_LONG = 8;

    /**
     * Number of bits in a byte.
     */
    public static final int BITS_IN_BYTE = 8;

    /**
     * Byte mask in a Long
     */
    public static final long BYTE_MASK = 0xFF;

    private long[] key;

    /**
     * Number of elements in the array.
     */
    private int longLength;

    /**
     * A long array encodes the sequence of bytes. The last long in the array may need to be truncated.
     */
    private int byteLength;

    private int hashCode;
    private String name;

    public LongArrayHashKey() {
        key = new long[LENGTH];
    }

    public LongArrayHashKey(final LongArrayHashKey longArrayHashKey) {
        this();
        length(longArrayHashKey.byteLength);
        System.arraycopy(longArrayHashKey.key, 0, key, 0, longLength);
        hashCode = longArrayHashKey.hashCode;
        name = longArrayHashKey.name;
    }

    public long[] key() {
        return key;
    }

    public String name() {
        if (name == null) {
            name = new String(toByteArray(key, byteLength));
        }
        return name;
    }

    @Override
    public boolean equals(final Object object) {
        final LongArrayHashKey stationHashKey = (LongArrayHashKey) object;
        return arrayEquals(key, stationHashKey.key, longLength);
    }

    private boolean arrayEquals(final long[] l1, final long[] l2, final int length) {
        for (int i = 0; i < length; i++) {
            if (l1[i] != l2[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public StationKey duplicate() {
        return new LongArrayHashKey(this);
    }

    public void hashCode(final int hashCode) {
        this.hashCode = hashCode;
    }

    public void length(final int byteLength) {
        this.byteLength = byteLength;
        longLength = AbstractConcurrentParser.ceilingDivide(byteLength, BYTES_IN_LONG);
    }

    public void resetName() {
        name = null;
    }

    /**
     * Convert a long array into a byte array.
     * Then wrap the byte array into a string.
     *
     * @param key
     * @param byteLength
     * @return
     */
    private String toByteArray(long[] key, int byteLength) {
        final byte[] bytes = new byte[byteLength];
        for (int i = 0; i < key.length; i++) {
            for (int j = 0; j < BYTES_IN_LONG; j++) {
                final int byteIndex = BYTES_IN_LONG * i + j;
                if (byteIndex >= byteLength) {
                    break;
                }

                final int shiftBits = j * BITS_IN_BYTE;
                final long byteMask = BYTE_MASK << shiftBits;
                bytes[byteIndex] = (byte) ((key[i] & byteMask) >> shiftBits);
            }
        }

        return new String(bytes);
    }
}
