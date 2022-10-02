package json4j.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Records supports easy handling of Java records, of which every component
 * is of type String. This project calls such Java records 'text' records.
 */
public interface TextHelper
{
    /**
     * Returns all values of a 'text' record as an array of Object, which
     * can be used as the argument of DataPrinter.accept().
     * Returns an empty array of Object[] if the text argument is null.
     * @param <T>     the type of the 'text' record
     * @param text    an instance of the 'text' record
     * @param getters a list of all getters the 'text' record has
     * @return        an array of all values the text argument holds
     */
    static <T extends Record> Object[] values(T text, List<Method> getters)
    {
        // preconditions:
        if (text == null) return new Object[] {};
        assert(getters != null);

        // logic:
        final var values = getters
        .stream()
        .map(method -> {
            try {
                return method.invoke(text);
            } catch (Exception ex) {
                return null;
            }
        })
        .toArray(Object[]::new);

        return values;
    }

    /**
     * Returns all getters of the 'text' record type has.
     * @param <T>   the type of the 'text' record
     * @param clazz a class instance of the type
     * @return      a list of all getters of the type
     */
    static <T extends Record> List<Method> getters(Class<T> clazz)
    {
        // preconditions:
        assert(clazz != null);

        // logic:
        final var getters = Arrays
        .stream(clazz.getRecordComponents())
        .map(RecordComponent::getAccessor)
        .toList();

        return getters;
    }

    /**
     * Returns the canonical constractor of a 'text' record type.
     * @param <T>   the type of the 'text' record
     * @param clazz a class instance of the type
     * @return      the canonical constructor of the type
     */
    static <T extends Record> Constructor<T> ctor(Class<T> clazz)
    {
        // preconditions:
        assert(clazz != null);

        // logic:
        final Class<?>[] paramTypes = Arrays
        .stream(clazz.getRecordComponents())
        .map(RecordComponent::getType)
        .toArray(Class<?>[]::new);

        Constructor<T> ctor;
        try {
            ctor = clazz.getDeclaredConstructor(paramTypes);
        } catch (Exception ex) {
            ctor = null;
        }

        return ctor;
    }

    /**
     * Parses a string according to the type argument T.
     * If the provided mapper succeeds to parse the string argument,
     * this method returns an Optional that holds the mapping result.
     * Blank string (""), null string, or a string value of the format
     * that the mapper cannot parse results in an empty Optional.
     * @param <T>    a type the string argument represents
     * @param string a string to be parsed
     * @param mapper a Function to map the string to the type object
     * @return       an Optional that may hold the parse result
     */
    static <T>
    Optional<T> parse(final String string, final Function<String, T> mapper)
    {
        // preconditions:
        if (string == null || string.length() == 0) {
            return Optional.empty();
        }

        assert(mapper != null);

        // logic:
        Optional<T> result;
        try {
            result = Optional.ofNullable(mapper.apply(string));
        } catch (Exception ex) {
            result = Optional.empty();
        }

        return result;
    }

    /**
     * Interprets an integer an array of bits, and returns an array of integers.
     * Each integer in the list represents one bit in the 'integer' argument.
     * Evaluation of the 'integer' argument always start at a 4-bit bounday,
     * which is determined by the 'int_cnt' argument. For example, given
     * '0x0000_036E' or '0b_0000_0000_0000_0000___0000_0011_0110_1110' as the
     * 'integer' argument, then
     * <p>{@code
     * cnt   evaluating returns
     *  10   0x36E      {0,0,1,1,0,1,1,0,1,1}
     *   9   0x36E      {0,0,1,1,0,1,1,0,1}
     *   8   0x6E       {0,1,1,0,1,1,1,0}
     *   7   0x6E       {0,1,1,0,1,1,1}
     *   6   0x6E       {0,1,1,0,1,1}
     *   5   0x6E       {0,1,1,0,1}
     *   4   0xE        {1,1,1,0}
     * }
     * <p>The 'bit_cnt` argument must be {@code 0 < bit_count ≤ 32}, otherwise
     * an empty list is returned.
     * @param int_value a number to be interpred as a list of bits
     * @param bit_cnt   a number of bits to be interpreted
     * @return          an array of bit-values represented by integers
     */
    static int[] bits(int int_value, int bit_cnt)
    {
        if ( bit_cnt <= 0 || 32 < bit_cnt) return new int[] {};
        final int start = ((32 - bit_cnt)>>2)<<2;

        final var bits = IntStream
        .iterate(31 - start, distance -> distance - 1)
        .limit(bit_cnt)
        .map(distance -> (int_value >>> distance) & 1)
        .toArray()
        ;

        return bits;
    }
}
