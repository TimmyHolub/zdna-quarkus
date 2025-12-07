package awlawdhecomin.common.util;

/**
 * Utility for normalizing ASCII/UTF-8 letter bytes to uppercase in-place.
 *
 */
public final class ByteUtils {

    // Bitmask for uppercasing ASCII letters while preserving the UTF-8 high bit
    // 0b01011111 with high bit preserved
    private static final int BYTE_FILTER = (95 | 1 << 7);

    private ByteUtils() {
    }

    /**
     * Normalizes a raw ASCII/UTF-8 letter byte to the internal uppercase representation.
     *
     * @param in byte value (expected ASCII or single-byte UTF-8)
     * @return uppercased letter byte (or original category-preserving value for non-letters)
     */
    public static byte toInternal(byte in) {
        return (byte) (in & BYTE_FILTER);
    }
}
