package etl.model;

import java.time.Year;

import etl.util.IntRange;

/**
 * Ex2Film represents the Film entity.
 */
public interface Ex2Film
{
    /**
     * Ex2Film text record
     */
    record Text (
        String id,
        String name,
        String release
    ) {}

    /**
     * Ex2Film model record
     */
    record Model (
        String id,
        String name,
        Year release
    ) {
        /**
         * The valid length range of {@code id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_id
        = IntRange.lower(8).upper(8);

        /**
         * The valid length range of {@code name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_name
        = IntRange.lower(1).upper(32);

        /**
         * Validate the model record.
         * @return {@code true} if the model record is valid
         */
        boolean isValid()
        {
            boolean validity
            =  id != null
            && VALID_LENGTH_RANGE_id.covers(id.length())

            && name != null
            && VALID_LENGTH_RANGE_name.covers(name.length())

            && release != null
            ;
            return validity;
        }
    }
}
