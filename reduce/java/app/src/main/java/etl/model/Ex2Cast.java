package etl.model;

import etl.util.IntRange;
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
     * Ex2Cast model record implements the {@code Comparable}
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
         * The valid length range of {@code role_name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_role_name
        = IntRange.lower(1).upper(32);

        public static Model instance(
            final Ex2Film.Model film,
            final Ex2Actor.Model actor,
            final String role_name
        ) {
            if (role_name == null) return null;
            if (role_name.length() < VALID_LENGTH_RANGE_role_name.lower()) return null;
            if (role_name.length() > VALID_LENGTH_RANGE_role_name.upper()) return null;

            final var instance = new Ex2Cast.Model(
                film.id(), actor.id(), role_name
            );
            return instance;
        }

        // boolean isValid() is not defined because the constuctor guarantees
        // validity of model instances

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
            if (model == null) return null;

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
