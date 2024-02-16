package dev.morling.onebrc.calculate;

public class ByteBuffer {

    private final byte[] buffer;
    int offset;
    int size;

    public ByteBuffer(int bufferSize) {
        this.buffer = new byte[bufferSize];
    }

    public ByteBuffer(final byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] buffer() {
        return buffer;
    }

    public int size() {
        return size;
    }

    public void size(final int size) {
        this.size = size;
    }

    public int offset() {
        return offset;
    }

    public void offset(final int offset) {
        this.offset = offset;
    }

    public boolean hasNext() {
        return offset < size;
    }

    public void scan(byte separator) {
        while (offset < size && buffer[offset] != separator) {
            offset++;
        }
    }

    public int reverseIndex(byte separator) {
        int last = size-1;
        while (last >= 0 && buffer[last] != separator) {
            last--;
        }
        return last;
    }

    public void skip() {
        if (offset == size) {
            return;
        }
        offset++;
    }
}
