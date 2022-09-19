package etl.model;

import etl.util.IntRange;
import etl.util.TextHelper;

/**
 * Ex3Cast represents the Cast relationship.
 */
public interface Ex3Cast
{
    /**
     * Ex3Cast text record
     */
    record Text
    (
        String film_id,
        String actor_id,
        String role_name
    ) {}

    /**
     * Ex3Cast model record
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

    /**
     * Loading provides methods for writing Model records
     * to CSV files.
     */
    interface Loading
    {
        /**
         * Transforms a Model record to a Text record.
         * @param model a Model record
         * @return      a Text record
         */
        static Ex3Cast.Text text(final Ex3Cast.Model model)
        {
            // An invalid Model yields an invalid Text
            if (model == null || !model.isValid()) return null;

            // mapping Model to Text
            final var text = new Text(
                model.film_id,
                model.actor_id,
                model.role_name
            );

            return text;
        }

        /**
         * Returns an array of Object instances. The objects are generated
         * from the Model record's coponents.
         * @param model a Model record
         * @return      an array of Object instances
         */
        static Object[] values(final Ex3Cast.Model model)
        {
            final var text = text(model);
            final var values = TextHelper.values(
                text,
                TextHelper.getters(Text.class)
            );
            return values;
        }
    }
}
