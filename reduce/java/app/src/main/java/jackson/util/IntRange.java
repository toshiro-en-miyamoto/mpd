package jackson.util;

/**
 * Range is an integer interval {@code ⟦a, b⟧}, where {@code a:Z} and
 * {@code b:Z} are the lower and upper bound inclusive respectively.
 * The pre-condition for valid ranges is {@code a ≤ b}.
 * <p>
 * Take {@code ⟦1, 4⟧}. It is a set: {@code {1, 2, 3, 4}}. Thus, the
 * size of the range (or the number of integers covered by the range)
 * is {@code b - a + 1}.
 * <p>
 * You may want to interpret the upper bound as exclusive, then the
 * size is {@code b - a}.
 */
public record IntRange (
    int lower,
    int upper
)
    implements Comparable<IntRange>
{
    /**
     * Instanciates a Builder {@code ⟦MIN_VALUE, MAX_VALUE⟧}.
     * @return  a Builder instance
     */
    public static Builder builder()
    {
        return new Builder(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Tells if this range covers {@code that}. 
     * @param that a value to be evaluated
     * @return {@code true} if the value is in this range
     */
    public boolean covers(int that)
    {
        return (lower <= that) && (that <= upper);
    }

    /**
     * Returns a stream containing values covered by this range.
     * @return a stream containing values covered by this range
     */
    public java.util.stream.IntStream values()
    {
        final var values = java.util.stream.IntStream
        .rangeClosed(lower, upper)
        ;
        return values;
    }

    /**
     * Compares this object with the specified object for order.
     * @param that the Range to be compared
     * @return a negative integer, zero, or a positive integer as this
     * range is less than, equal to, or greater than the that argument
     */
    @Override public int compareTo(final IntRange that)
    {
        int result = Integer.compare(this.lower, that.lower);
        if (result == 0) {
            result = Integer.compare(this.upper, that.upper);
        }
        return result;
    }

    /**
     * Provides methods to build a range.
     */
    public static class Builder
    {
        /**
         * Instantiates a range {@code ⟦this.lower, this.upper⟧}.
         * @return  a range {@code ⟦this.lower, this.upper⟧}
         */
        public IntRange build()
        {
            return new IntRange(lower, upper);
        }

        /**
         * Instantiates a Builder {@code ⟦value, value⟧}.
         * @param value the lower and upper bounds inclusive
         * @return  a Builder {@code ⟦value, value⟧}
         */
        public Builder lower_upper(int value)
        {
            return new Builder(value, value);
        }

        /**
         * Instantiates a Builder {@code ⟦this.lower, upper⟧}.
         * @param upper the upper bound inclusive
         * @return  a Builder {@code ⟦this.lower, upper⟧}
         * @throws  IllegalArgumentException if
         *          this.lower > upper
         */
        public Builder upper(int upper)
        {
            return new Builder(lower, upper);
        }

        /**
         * Instantiates a Builder {@code ⟦lower, this.upper⟧}.
         * @param lower the lower bound inclusive
         * @return  a Builder {@code ⟦lower, this.upper⟧}
         * @throws  IllegalArgumentException if
         *          lower > this.upper
         */
        public Builder lower(int lower)
        {
            return new Builder(lower, upper);
        }

        private Builder(int lower, int upper)
        throws IllegalArgumentException
        {
            if (lower > upper) {
                throw new IllegalArgumentException();
            }

            this.lower = lower;
            this.upper = upper;
        }

        private int lower;
        private int upper;
    }
}
