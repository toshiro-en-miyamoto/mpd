package etl.model;

import etl.util.IntRange;
import etl.util.Sha1;
import etl.util.TextHelper;

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
     * Ex2Cast model record  implements the {@code Comparable}
     * interface (therefore, {@code equals()} and {@code hashCode()}
     * as well), because we want to sort a list of casts.
     */
    record Model
    (
        String film_id,
        String actor_id,
        String role_name
    )
        implements Comparable<Model>
    {
        /**
         * The valid length range of {@code film_id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_film_id
        = IntRange.lower(Sha1.HEX_TEXT_LENGTH).upper(Sha1.HEX_TEXT_LENGTH);

        /**
         * The valid length range of {@code actor_id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_actor_id
        = IntRange.lower(Sha1.HEX_TEXT_LENGTH).upper(Sha1.HEX_TEXT_LENGTH);

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

        @Override
        public int compareTo(final Model that)
        {
            var comparison =
            this.film_id.equals(that.film_id)
            ? this.actor_id.compareTo(that.actor_id)
            : this.film_id.compareTo(that.film_id);

            return comparison;
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * @param that the reference object with which to compare
         * @return     {@code true} if this object is the same os the {@code that}
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            Model that = (Model) obj;

            final var equality
            =  this.film_id.equals(that.film_id)
            && this.actor_id.equals(that.actor_id);

            return equality;
        }

        /**
         * Returns a hash code value for the object.
         * @return a hash code value for this object
         */
        @Override
        public int hashCode()
        {
            return film_id.concat(actor_id).hashCode();
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
        static Text text(final Model model)
        {
            // An invalid Model yields an invalid Text
            if (model == null || !model.isValid()) return null;

            // mapping Model to Text
            final var text
            = new Text(model.film_id, model.actor_id, model.role_name);

            return text;
        }

        /**
         * Returns an array of Object instances. The objects are generated
         * from the Model record's coponents.
         * @param model a Model record
         * @return      an array of Object instances
         */
        static Object[] values(final Model model)
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
