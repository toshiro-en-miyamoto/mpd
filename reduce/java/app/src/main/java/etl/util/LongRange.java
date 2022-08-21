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
public class LongRange implements Comparable<LongRange>
{
    /**
     * Instantiates a range {@code ⟦lower, Z.MAX_VALUE⟧}.
     * @param lower the lower bound inclusive
     * @return a range {@code ⟦lower, Z.MAX_VALUE⟧}
     */
    public static LongRange lower(long lower)
    {
        final var range = new LongRange(lower, MAX_VALUE);
        return range;
    }

    /**
     * Returns a range {@code ⟦this.lower, upper⟧}.
     * @param upper the upper bound inclusive
     * @return a range
     * @throws IllegalArgumentException
     *      if {@code this.lower ≤ upper} does not hold
     */
    public LongRange upper(long upper) throws IllegalArgumentException
    {
        if (upper < this.lower) throw new IllegalArgumentException();

        var clone = this.clone();
        clone.upper = upper;
        return clone;
    }

    /**
     * Returns a stream containing values covered by this range.
     * @return a stream containing values covered by this range
     */
    public java.util.stream.LongStream values()
    {
        final var values = java.util.stream.LongStream
        .rangeClosed(lower, upper)
        ;
        return values;
    }

    /**
     * Tells if this range covers {@code that}. 
     * @param that a value to be evaluated
     * @return {@code true} if the value is in this range
     */
    public boolean covers(long that)
    {
        return (lower <= that) && (that <= upper);
    }

    /**
     * Returns the lower bound inclusive.
     * @return the lower bound inclusive
     */
    public long lower() { return lower; }

    /**
     * Returns the upper bound exclusive.
     * @return the upper bound exclusive
     */
    public long upper() { return upper; }

    /**
     * Compares this object with the specified object for order.
     * @param that the Range to be compared
     * @return     a negative integer, zero, or a positive integer as this
     * range is less than, equal to, or greater than the that argument
     */
    @Override public int compareTo(final LongRange that)
    {
        int result = Long.compare(this.lower, that.lower);
        if (result == 0) {
            result = Long.compare(this.upper, that.upper);
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
        if (!(obj instanceof LongRange)) return false;
        LongRange that = (LongRange) obj;
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
        hash = prime * hash + Long.hashCode(lower);
        hash = prime + hash + Long.hashCode(upper);
        return hash;
    }

    /**
     * Creates and returns a copy of this range.
     * @return a clone of this range
     */
    @Override public LongRange clone()
    {
        return new LongRange(lower, upper);
    }

    /*
     * Pre-conditions:
     *  {@code lower ≤ upper}
     */
    private LongRange(long lower, long upper) throws IllegalArgumentException
    {
        if (lower > upper) {
            throw new IllegalArgumentException();
        } else {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private static final long MAX_VALUE = Long.MAX_VALUE;

    private long lower;
    private long upper;
}
