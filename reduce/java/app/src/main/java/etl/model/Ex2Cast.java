package etl.model;

import etl.util.IntRange;

/**
 * Ex2Cast represents the Cast relationship.
 */
public interface Ex2Cast
{
    /**
     * Ex2Cast text record
     */
    record Text
    (
        String film_id,
        String actor_id,
        String role_name
    ) {}

    /**
     * Ex2Cast model record
     */
    record Model
    (
        String film_id,
        String actor_id,
        String role_name
    ) {
        /**
         * The valid length range of {@code film_id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_film_id
        = IntRange.lower(8).upper(8);

        /**
         * The valid length range of {@code actor_id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_actor_id
        = IntRange.lower(8).upper(8);

        /**
         * The valid length range of {@code role_name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_role_name
        = IntRange.lower(1).upper(32);

        /**
         * Validates the model record.
         * @return {@code true} if the model record is valid
         */
        boolean isValid()
        {
            boolean validity
            =  film_id != null
            && VALID_LENGTH_RANGE_film_id.covers(film_id.length())

            && actor_id != null
            && VALID_LENGTH_RANGE_actor_id.covers(actor_id.length())

            && role_name != null
            && VALID_LENGTH_RANGE_role_name.covers(role_name.length())
            ;
            return validity;
        }
    }
}
