package awlawdhecomin.analyse.shared.data;

public enum Nucleotide {

    A('A', 'T', 'U'),
    B('B'),
    C('C', 'G', 'G'),
    D('D'),
    G('G', 'C', 'C'),
    H('H'),
    K('K'),
    M('M'),
    N('N'),
    R('R'),
    S('S'),
    T('T', 'A'),
    U('U', Nucleotide.ASCIINULL, 'A'),
    V('V'),
    W('W'),
    Y('Y'),
    NONE();

    static final private char ASCIINULL = (char) 0;

    // mask to normalize ASCII/UTF-8 letters to uppercase (preserves highest bit)
    private static final int BYTE_FILTER = 95 | (1 << 7);

    private final byte rawByte;//internal raw representation
    private final byte dnaSupplement;
    private final byte rnaSupplement;

    Nucleotide() {
        this(ASCIINULL);
    }

    Nucleotide(char raw) {
        this(raw, ASCIINULL);
    }

    Nucleotide(char raw, char dSupplement) {
        this(raw, dSupplement, ASCIINULL);
    }

    Nucleotide(char raw, char dSupplement, char rSupplement) {
        this.rawByte = (byte) raw;
        this.dnaSupplement = (byte) dSupplement;
        this.rnaSupplement = (byte) rSupplement;
    }

    /**
     * Normalize raw byte (ASCII or UTF-8) to internal uppercase format
     */
    private static byte toInternal(byte in) {
        return (byte) (in & BYTE_FILTER);
    }

    /**
     * Faster way to get nucleic from internal format string
     *
     * @param b byte of internal format data
     * @return nucleic base
     */
    public static Nucleotide getFromInternalFormat(byte b) {
        switch (b) {
            case 65:
                return A;
            case 66:
                return B;
            case 67:
                return C;
            case 68:
                return D;
            case 71:
                return G;
            case 72:
                return H;
            case 75:
                return K;
            case 77:
                return M;
            case 78:
                return N;
            case 82:
                return R;
            case 83:
                return S;
            case 84:
                return T;
            case 85:
                return U;
            case 86:
                return V;
            case 87:
                return W;
            case 89:
                return Y;
            default:
                return NONE;
        }
    }

    /**
     * Slower but safe mapping from raw byte input
     */
    public static Nucleotide get(byte b) {
        return getFromInternalFormat(toInternal(b));
    }

    public byte toByte() {
        return rawByte;
    }
}
