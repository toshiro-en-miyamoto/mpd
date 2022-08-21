package etl.util;

/**
 * Sequencer provides a sequence number generator.
 */
public class Sequencer
{
    /**
     * Instanciates a sequence number generator.
     * @param seq the first number this instance returns
     * @return    a sequence number generator instance
     */
    public static Sequencer starting(long seq)
    {
        return new Sequencer(seq);
    }

    /**
     * Returns a sequence number starting from the number
     * specified when instanciated.
     * @return a sequence number
     */
    public long next() { return seq++; }

    private Sequencer(long seq) { this.seq = seq; }
    private long seq;
}
