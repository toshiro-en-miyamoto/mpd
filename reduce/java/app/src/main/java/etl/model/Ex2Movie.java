package etl.model;

/**
 * Ex2Movie represents a view of Film-Cast relationship.
 */
public interface Ex2Movie
{
    /**
     * Record types
     */
    enum RecordType
    {
        FILM(1), CAST(2);

        /**
         * Instantiates the enum constant.
         * @param value of the enum constant
         */
        RecordType(int value) { this.value = value; }

        /**
         * Returns the value of the enum constant.
         * @return the value of the enum constant
         */
        public int value() { return value; }

        private final int value;
    }

    /**
     * Ex2Movie text records
     */
    interface Text
    {

        /**
         * Ex2Movie.Text.Film record
         */
        record Film
        (
            String record_type,
            String seq_film,
            String name,
            String release
        ) {}

        /**
         * Ex2Movie.Text.Cast record
         */
        record Cast
        (
            String record_type,
            String seq_film,
            String seq_cast,
            String actor_name,
            String role_name,
            String actor_then_age
        ) {}
    }
}
