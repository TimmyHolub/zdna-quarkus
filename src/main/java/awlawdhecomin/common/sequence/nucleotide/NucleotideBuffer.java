package awlawdhecomin.common.sequence.nucleotide;

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

/**
 * ByteBuffer wrapper created to solve <a href="https://github.com/apache/felix/pull/114">https://github.com/apache/felix/pull/114</a>
 */
public class NucleotideBuffer {

    private final ByteBuffer buffer;

    public NucleotideBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static NucleotideBuffer wrap(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return wrap(buffer);
    }

    public static NucleotideBuffer wrap(ByteBuffer buffer) {
        return new NucleotideBuffer(buffer);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#slice()}.
     *
     * @return new NucleotideBuffer
     */
    public NucleotideBuffer slice() {
        ByteBuffer result = buffer.slice();
        return wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#duplicate()}
     *
     * @return new NucleotideBuffer
     */
    public NucleotideBuffer duplicate() {
        ByteBuffer result = buffer.duplicate();
        return (result == buffer) ? this : wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#get()}.
     * <p>
     * Reads the byte at this buffer's current position, and then increments the position.
     *
     * @return The Nucleotide at the buffer's current position
     */
    public Nucleotide get() {
        byte b = buffer.get();
        return Nucleotide.get(b);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#get(int)}.
     * <p>
     * Reads the byte at this buffer's absolute position.
     *
     * @return The Nucleotide at the buffer's current position
     */
    public Nucleotide get(int index) {
        byte b = buffer.get(index);
        return Nucleotide.get(b);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#capacity()}.
     *
     * @return The capacity of this buffer
     */
    public int getCapacity() {
        return buffer.capacity();
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#position()}.
     *
     * @return The position of this buffer
     */
    public int getPosition() {
        return buffer.position();
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#position(int)}.
     * <p>
     * Sets this buffer's position.  If the mark is defined and larger than the
     * new position then it is discarded.
     *
     * @param newPosition The new position value; must be non-negative
     *                    and no larger than the current limit
     */
    public void setPosition(int newPosition) {
        buffer.position(newPosition);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#limit()}.
     * <p>
     * Returns this buffer's limit.
     *
     * @return The limit of this buffer
     */
    public int getLimit() {
        return buffer.limit();
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#limit(int)}.
     * <p>
     * Sets this buffer's limit.  If the position is larger than the new limit
     * then it is set to the new limit.  If the mark is defined and larger than
     * the new limit then it is discarded.
     *
     * @param newLimit The new limit value; must be non-negative
     *                 and no larger than this buffer's capacity
     */
    public void setLimit(int newLimit) {
        buffer.limit(newLimit);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#mark()}.
     * <p>
     * Sets this buffer's mark at its position.
     *
     * @return This buffer
     */
    public NucleotideBuffer mark() {
        ByteBuffer result = buffer.mark();
        return (result == buffer) ? this : wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#reset()}.
     * <p>
     * Invoking this method neither changes nor discards the mark's
     * value. </p>
     *
     * @return The limit of this buffer
     * @throws InvalidMarkException If the mark has not been set
     */
    public NucleotideBuffer reset() {
        ByteBuffer result = buffer.reset();
        return (result == buffer) ? this : wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#clear()}.
     * <p>
     * Clears this buffer.  The position is set to zero, the limit is set to
     * the capacity, and the mark is discarded.
     *
     * <p> Invoke this method before using a sequence of channel-read or
     * <i>put</i> operations to fill this buffer.  For example:
     *
     * <blockquote><pre>
     * buf.clear();     // Prepare buffer for reading
     * in.read(buf);    // Read data</pre></blockquote>
     *
     * <p> This method does not actually erase the data in the buffer, but it
     * is named as if it did because it will most often be used in situations
     * in which that might as well be the case. </p>
     *
     * @return This buffer
     */
    public NucleotideBuffer clear() {
        ByteBuffer result = buffer.clear();
        return (result == buffer) ? this : wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#flip()}.
     * <p>
     * Flips this buffer.  The limit is set to the current position and then
     * the position is set to zero.  If the mark is defined then it is
     * discarded.
     *
     * <p> After a sequence of channel-read or <i>put</i> operations, invoke
     * this method to prepare for a sequence of channel-write or relative
     * <i>get</i> operations.  For example:
     *
     * <blockquote><pre>
     * buf.put(magic);    // Prepend header
     * in.read(buf);      // Read data into rest of buffer
     * buf.flip();        // Flip buffer
     * out.write(buf);    // Write header + data to channel</pre></blockquote>
     *
     * <p> This method is often used in conjunction with the {@link
     * ByteBuffer#compact compact} method when transferring data from
     * one place to another.  </p>
     *
     * @return This buffer
     */
    public NucleotideBuffer flip() {
        ByteBuffer result = buffer.flip();
        return (result == buffer) ? this : wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#rewind()}.
     * <p>
     * Rewinds this buffer.  The position is set to zero and the mark is
     * discarded.
     *
     * <p> Invoke this method before a sequence of channel-write or <i>get</i>
     * operations, assuming that the limit has already been set
     * appropriately.  For example:
     *
     * <blockquote><pre>
     * out.write(buf);    // Write remaining data
     * buf.rewind();      // Rewind buffer
     * buf.get(array);    // Copy data into array</pre></blockquote>
     *
     * @return This buffer
     */
    public NucleotideBuffer rewind() {
        ByteBuffer result = buffer.rewind();
        return (result == buffer) ? this : wrap(result);
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#remaining()}.
     * <p>
     * Returns the number of elements between the current position and the
     * limit.
     *
     * @return The number of elements remaining in this buffer
     */
    public int remaining() {
        return buffer.remaining();
    }

    /**
     * Decorate wrapped Buffer method {@link ByteBuffer#hasRemaining()}.
     * <p>
     * Tells whether there are any elements between the current position and
     * the limit.
     *
     * @return {@code true} if, and only if, there is at least one element
     * remaining in this buffer
     */
    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    /**
     * This method casts {@link NucleotideBuffer} to {@link ByteBuffer}.
     *
     * @return
     */
    public ByteBuffer asByteBuffer() {
        return buffer;
    }

}
