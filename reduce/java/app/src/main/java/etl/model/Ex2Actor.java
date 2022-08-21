package etl.model;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import etl.util.IntRange;

/**
 * Ex2Actor represents the Actor entity.
 */
public interface Ex2Actor
{
    /**
     * Ex2Actor Text record
     */
    record Text
    (
        String id,
        String name,
        String born,
        String died
    ) {}

    /**
     * Ex2Actor model record
     */
    record Model
    (
        String id,
        String name,
        LocalDate born,
        Year died
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
         * Returns {@code died} as an {@code Optional<Year>}.
         * @return
         */
        public Optional<Year> optional_died()
        {
            return Optional.ofNullable(died);
        }

        /**
         * Validates the model record.
         * @return {@code true} if the model record is valid
         */
        boolean isValid()
        {
            boolean validity
            =  id != null
            && VALID_LENGTH_RANGE_id.covers(id.length())

            && name != null
            && VALID_LENGTH_RANGE_name.covers(name.length())

            && born != null

            // the died field is optional
            ;
            return validity;
        }
    }
}
