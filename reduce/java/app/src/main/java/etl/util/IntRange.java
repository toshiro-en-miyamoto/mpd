package etl.util;

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
public class IntRange implements Comparable<IntRange>
{
    /**
     * Instantiates a range {@code ⟦lower, Z.MAX_VALUE⟧}.
     * @param lower the lower bound inclusive
     * @return a range {@code ⟦lower, Z.MAX_VALUE⟧}
     */
    public static IntRange lower(int lower)
    {
        final var range = new IntRange(lower, MAX_VALUE);
        return range;
    }

    /**
     * Returns a range {@code ⟦this.lower, upper⟧}.
     * @param upper the upper bound inclusive
     * @return a range
     * @throws IllegalArgumentException
     *      if {@code this.lower ≤ upper} does not hold
     */
    public IntRange upper(int upper) throws IllegalArgumentException
    {
        if (upper < this.lower) throw new IllegalArgumentException();

        final var clone = new IntRange(this.lower, upper);
        return clone;
    }

    /**
     * Returns a range {@code ⟦this.lower, this.lower⟧}.
     * @return a range
     */
    public IntRange with_same_upper()
    {
        final var clone = new IntRange(this.lower, this.lower);
        return clone;
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
     * Tells if this range covers {@code that}. 
     * @param that a value to be evaluated
     * @return {@code true} if the value is in this range
     */
    public boolean covers(int that)
    {
        return (lower <= that) && (that <= upper);
    }

    /**
     * Returns the lower bound inclusive.
     * @return the lower bound inclusive
     */
    public int lower() { return lower; }

    /**
     * Returns the upper bound inclusive.
     * @return the upper bound inclusive
     */
    public int upper() { return upper; }

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
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare
     * @return true if this object is equal to the obj argument;
     *         false otherwise
     */
    @Override public boolean equals(final Object obj)
    {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof IntRange)) return false;
        IntRange that = (IntRange) obj;
        return this.lower == that.lower && this.upper == that.upper; 
    }

    /**
     * Returns a hash code value for this Range.
     * @return a hash code value for this Range
     */
    @Override public int hashCode()
    {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + Integer.hashCode(lower);
        hash = prime + hash + Integer.hashCode(upper);
        return hash;
    }

    /*
     * Pre-conditions:
     *  {@code lower ≤ upper}
     */
    private IntRange(int lower, int upper) throws IllegalArgumentException
    {
        if (lower > upper) {
            throw new IllegalArgumentException();
        } else {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private static final int MAX_VALUE = Integer.MAX_VALUE;

    private int lower;
    private int upper;
}
