package dev.morling.onebrc.model;

public class ByteArrayHashKey implements StationKey {
    /**
     * 1 KB
     */
    private static final int LENGTH = 1024;

    private byte[] key;
    private int length;
    private int hashCode;
    private String name;

    public ByteArrayHashKey() {
        key = new byte[LENGTH];
    }

    private ByteArrayHashKey(final ByteArrayHashKey byteArrayHashKey) {
        this();
        System.arraycopy(byteArrayHashKey.key, 0, key, 0, byteArrayHashKey.length);
        this.length = byteArrayHashKey.length;
        this.hashCode = byteArrayHashKey.hashCode;
        this.name = byteArrayHashKey.name;
    }

    public byte[] key() {
        return key;
    }

    public String name() {
        if (name == null) {
            name = new String(key, 0, length);
        }
        return name;
    }

    @Override
    public boolean equals(final Object object) {
        final ByteArrayHashKey stationHashKey = (ByteArrayHashKey) object;
        return arrayEquals(key, stationHashKey.key, length);
    }

    private boolean arrayEquals(final byte[] b1, final byte[] b2, final int l) {
        for (int i = 0; i < length; i++) {
            if (b1[i] != b2[i]) {
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

    public void hashCode(final int hashCode) {
        this.hashCode = hashCode;
    }

    public void length(final int length) {
        this.length =  length;
    }
    public void resetName() {
        name = null;
    }

    @Override
    public StationKey duplicate() {
        return new ByteArrayHashKey(this);
    }
}
