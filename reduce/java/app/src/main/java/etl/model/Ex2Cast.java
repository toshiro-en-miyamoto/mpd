package etl.model;

import java.util.Objects;

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
        Ex2Film.Model  film,
        Ex2Actor.Model actor,
        String         role_name
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

            if (!VALID_LENGTH_RANGE_role_name.covers(role_name.length()))
                return null;

            final var instance = new Ex2Cast.Model(
                film, actor, role_name
            );
            return instance;
        }

        // boolean isValid() is not defined because the constuctor guarantees
        // validity of model instances

        @Override
        public int compareTo(final Model that)
        {
            var comparison =
            this.film.equals(that.film)
            ? this.actor.compareTo(that.actor)
            : this.film.compareTo(that.film);

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
            =  this.film.equals(that.film)
            && this.actor.equals(that.actor);

            return equality;
        }

        /**
         * Returns a hash code value for the object.
         * @return a hash code value for this object
         */
        @Override
        public int hashCode()
        {
            return Objects.hash(film, actor);
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
            = new Text(model.film.id(), model.actor.id(), model.role_name);

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
