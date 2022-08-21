package etl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * StringView, together with IntRange, abstracts the viewport of a text,
 * making operations on char sequences easier and less error-pront.
 */
public interface StringView
{
    /**
     * Return a String which is copied from the text argument but partially
     * replaced with the replacement argument.
     * The range argument specifies the part of the array to be replaced.
     * If the codepoints of the replacement sequence is longer than the range,
     * the sequence is truncated according to the range to modify the
     * copied array.
     * If the replacement sequence is shorter than the range,
     * (range - replacement_codepoint_array.length) instances of
     * codepoint specified by the padding argument follow the replacement
     * to make up for the shortfall.
     * @param text the character sequence supposed to be covered by the range
     * @param range       covers the sequnce to be transformed
     * @param replacement the sequence with which the range is replaced
     * @param padding     specifies the codepoint to fill out the shortfall
     * @return            a String containing the transformed sequence
     */
    static String apply(
        final CharSequence text,
        final IntRange     range,
        final CharSequence replacement,
        int padding
    ) {
        final var text_array = text.codePoints().toArray();
        final var replacement_array = replacement.codePoints().toArray();

        final var left_lower = 0;
        final var left_upper = range.lower();
        final var left_range = IntRange.lower(left_lower).upper(left_upper);
        final var left_stream = stream(text_array, left_range);

        final var right_lower = range.upper();
        final var right_upper = text_array.length;
        final var right_range = IntRange.lower(right_lower).upper(right_upper);
        final var right_stream = stream(text_array, right_range);

        final IntStream middle_stream = stream(text_array, range)
        .map(new IntUnaryOperator()
            {
                private int index = 0;

                @Override public int applyAsInt(int ignored)
                {
                    int val
                    = index < replacement_array.length
                    ? replacement_array[index]
                    : padding;

                    index++;
                    return val;
                }            
            }
        );

        final var whole = IntStream.concat(
            left_stream,
            IntStream.concat(middle_stream, right_stream)
        )
        .collect(
            StringBuilder::new,
            StringBuilder::appendCodePoint,
            StringBuilder::append
        )
        .toString();

        return whole;
    }

    /**
     * Returns a list of Range instances.
     * All instance has the same size; the block_size argument.
     * The first range covers [0, size) of the text argument,
     * the second [1 * size, size), the thrid [2 * size, size) ,and so on.
     * The last range covers [(n - 1) * size, text.length),
     * thus its size may be less than the size of the block argument.
     * @param text  the character sequence supposed to be covered by the range
     * @param block_length the size of each block
     * @return      a list of ranges, covering the text as a whole
     */
    static List<IntRange> ranges_by_length(
        final CharSequence text,
        int block_length
    ) {
        final var array = text.codePoints().toArray();
        final int upper = array.length;

        final var ranges = IntStream
        .iterate(0, index -> index + block_length)
        .takeWhile(index -> index < upper)
        .mapToObj(index -> {
            final int remaining = upper - index;

            final int length
            = remaining < block_length
            ? remaining : block_length;

            return IntRange.lower(index).upper(index + length);
        })
        .collect(
            ArrayList<IntRange>::new,
            (list, range) -> list.add(range),
            ArrayList<IntRange>::addAll
        );

        return ranges;
    }

    /**
     * Copies these ranges of a given text into String instances.
     * The Strings are mapped to the same name as the corresponding range.
     * @param text   the character sequence supposed to be covered by the range
     * @param ranges a list of [range name, range] entries
     * @return       a list of [range name, string] entries
     */
    static Map<String, String> strings(
        final CharSequence text,
        final Map<String, IntRange> ranges
    ) {
        final var strings = ranges.entrySet().stream()
        .collect(
            HashMap<String, String>::new,
            (map, entry) -> map.put(
                entry.getKey(),
                string(text, entry.getValue())
            ),
            HashMap<String, String>::putAll
        );

        return strings;
    }

    /**
     * Copies these ranges of a given text into String instances.
     * @param text   the character sequence supposed to be covered by the range
     * @param ranges a list of ranges
     * @return       a list of strings
     */
    static List<String> strings(
        final CharSequence text,
        final List<IntRange> ranges
    ) {
        final var strings = ranges.stream()
        .collect(
            ArrayList<String>::new,
            (list, range) -> list.add(string(text, range)),
            ArrayList<String>::addAll
        );

        return strings;
    }

    /**
     * Returns a String containing characters extracted out of the text
     * argument according to the range argument.
     * @param text  the character sequence supposed to be covered by the range
     * @param range specifies codepoints to be contained in the sequence
     * @return      a character sequence covered by the range
     */
    static String string(final CharSequence text, final IntRange range)
    {
        final var array = text.codePoints().toArray();
        return string(array, range);
    }

    private static String string(final int[] array, final IntRange range)
    {
        final var adapted_range = adapted(array, range);
        final var lower = adapted_range.lower();
        final var length = adapted_range.upper() - lower;

        final var string = new String(array, lower, length);
        return string;
    }

    private static IntStream stream(final int[] array, final IntRange range)
    {
        final var adapted_range = adapted(array, range);
        final var lower = adapted_range.lower();
        final var length = adapted_range.upper() - lower;

        final var stream = IntStream.of(array)
        .skip(lower)
        .limit(length);

        return stream;
    }

    private static IntRange adapted(final int[] array, final IntRange range)
    {
        final int lower = range.lower();
        final int adapted_lower = array.length < lower ? array.length : lower;

        final int upper = range.upper();
        final int adapted_upper = array.length < upper ? array.length : upper;

        final var adapted
        = lower == adapted_lower && upper == adapted_upper
        ? range
        : IntRange.lower(adapted_lower).upper(adapted_upper);

        return adapted;
    }
}
