package awlawdhecomin.analyse.shared.data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.DoubleStream;

/**
 * The Window represents a part of DNA/RNA sequences, witch are currently analysed.
 * <p>
 *
 * <pre>
 *     Data: ACTGTGGTGTTTTGGTGTGTGGCCCGCAAATTTVVTGGG
 *                   |     |
 *     Window:       GTTTTGG
 *                   |>   <| size = 7
 *                   |
 *                   postition = 8
 * </pre>
 */
public interface Window {

    /**
     * This method returns the size of the Window.
     * The size is the number of nucleotides which the Windows contains.
     * @return Window size as a non-negative number.
     */
    int getSize();

    /**
     * Get the absolute position in the sequence.
     * @return The position of the first nucleotide in the Sequence starting from <code>0</code>.
     */
    int getPosition();

    /**
     * Get the nucleotide from a specific position inside the Window.
     * @param index has to be inside the Window.
     *
     * @return The nucleotides of the index.
     */
    Nucleotide get(int index);

    /**
     * This method transforms the Window content to a simple text.
     * <p>
     * This method is designed for the development and debugging purpose mostly.
     *
     * @return Window as String (ACTG)
     */
    String toPlain();

    /**
     * This method iterates over the whole Window nucleotide by nucleotide.
     * @param consumer which processes each nucleotide.
     */
    void forEach(Consumer<Nucleotide> consumer);

    /**
     * This method iterates over the whole Window of nucleotides with indexes one by one.
     * @param biConsumer which processes data getting index and nucleotide.
     */
    void forEach(BiConsumer<Integer, Nucleotide> biConsumer);

    /**
     * This method transforms the Window to the stream of doubles.
     * @param function that transfers a nucleotide to a double.
     * @return The {@link DoubleStream}
     */
    DoubleStream doubleStream(ToDoubleBiFunction<Integer, Nucleotide> function);

    /**
     * Get a sub-window from the Window by the position (start) and the offset.
     * @param start is a number inside the Window.
     * @param offset is the size of the new Window.
     * @return sub-window
     */
    Window getSubWindow(int start, int offset);
}
