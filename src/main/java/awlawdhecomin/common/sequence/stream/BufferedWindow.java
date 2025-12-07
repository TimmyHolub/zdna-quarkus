package awlawdhecomin.common.sequence.stream;

import awlawdhecomin.common.sequence.nucleotide.Nucleotide;
import awlawdhecomin.common.sequence.nucleotide.NucleotideBuffer;
import awlawdhecomin.common.sequence.stream.exception.WindowStreamException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public class BufferedWindow implements Window {

    private final NucleotideBuffer buffer;

    private BufferedWindow(NucleotideBuffer buffer) {
        try {
            buffer.reset();
        } catch (java.nio.InvalidMarkException e) {
            throw new WindowStreamException("Buffer is not set correctly, missing start mark.", e);
        }
        this.buffer = buffer;
    }

    public static Window wrap(String plainData) {
        ByteBuffer bf = ByteBuffer.wrap(plainData.getBytes());
        bf.mark();
        return wrap(bf);
    }

    static Window wrap(ByteBuffer buffer) {
        return new BufferedWindow(NucleotideBuffer.wrap(buffer));
    }

    @Override
    public int getSize() {
        return buffer.reset().remaining();
    }

    @Override
    public int getPosition() {
        return buffer.reset().getPosition();
    }

    @Override
    public Nucleotide get(int index) {
        if (0 > index || index >= getSize()) {
            throw new IndexOutOfBoundsException("Index is out of windows size");
        }
        int markPosition = buffer.reset().getPosition();
        return buffer.get(markPosition + index);
    }

    @Override
    public String toPlain() {
        return StandardCharsets.UTF_8.decode(buffer.asByteBuffer()).toString();
    }

    @Override
    public void forEach(Consumer<Nucleotide> consumer) {
        this.forEach((i, n) -> consumer.accept(n));
    }

    @Override
    public void forEach(BiConsumer<Integer, Nucleotide> biConsumer) {
        NucleotideBuffer duplicate = buffer.duplicate().reset();
        int index = 0;
        while (duplicate.getPosition() < duplicate.getLimit()) {
            biConsumer.accept(index++, duplicate.get());
        }
    }

    @Override
    public DoubleStream doubleStream(ToDoubleBiFunction<Integer, Nucleotide> function) {
        // TODO refaktoring
        NucleotideBuffer duplicate = buffer.duplicate().reset();
        Spliterator.OfDouble spliterator = new Spliterator.OfDouble() {
            int index = 0;

            @Override
            public OfDouble trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(DoubleConsumer action) {
                if (duplicate.getPosition() >= duplicate.getLimit())
                    return false;
                Nucleotide nucleotide = duplicate.get();
                double value = function.applyAsDouble(index++, nucleotide);
                action.accept(value);
                return true;
            }

            @Override
            public long estimateSize() {
                return getSize();
            }

            @Override
            public int characteristics() {
                return IMMUTABLE | ORDERED | NONNULL | SIZED;
            }
        };

        return StreamSupport.doubleStream(spliterator, false);
    }

    @Override
    public Window getSubWindow(int start, int offset) {
        this.buffer.rewind();
        NucleotideBuffer subBuffer = this.buffer.duplicate();
        try {
            subBuffer.setPosition(start);
            subBuffer.setLimit(offset);
            subBuffer.mark();
        } catch (IllegalArgumentException e) {
            // offset is larger than buffer size
            offset = this.buffer.getCapacity();
            subBuffer.setPosition(start);
            subBuffer.setLimit(offset);
            subBuffer.mark();
        }

        return new BufferedWindow(subBuffer);
    }
}
