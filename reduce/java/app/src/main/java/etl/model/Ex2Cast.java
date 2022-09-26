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
         * Instanciates an Model instance.
         * @param film      an Ex2Film instance
         * @param actor     an Ex2Actor instance
         * @param role_name the role name of the actor in the film
         * @return  a Model instance or {@code null} if
         *          the {@code film} argument is {@code null},
         *          the {@code actor} argument is {@code null}, or
         *          the {@code role_name} argument is {@code null}
         */
        public static Model instance(
            final Ex2Film.Model film,
            final Ex2Actor.Model actor,
            final String role_name
        ) {
            // an invalid Model if the pre-conditions don't hold
            if (film == null || actor == null || role_name == null) {
                return null;
            }

            // create a Model instane
            final var model = new Model(film, actor, role_name);

            return model;
        }

        /**
         * Compares this model with the specified Model for order.
         * Firstly, the Ex2Film.Model is compared. If they are equal,
         * then the Ex2Actor is compared.
         * @param that the object to be compared
         * @return  zero if both the Ex2Film and the Ex2Acto are equal;
         *          if this Ex2Film is less or greater than that Ex2Film,
         *          then a negative or positive integer returns respectively;
         *          if the Ex2Actor is equal and this role name
         *          lexicographically precedes or follows that name, then
         *          a negative or positive integer returns respectively.
         */
        @Override
        public int compareTo(final Model that)
        {
            var comparison = this.film.equals(that.film)
            ? this.actor.compareTo(that.actor)
            : this.film.compareTo(that.film);

            return comparison;
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * @param that the reference object with which to compare
         * @return {@code true} if the Ex2Film and Ex2Actor fields
         *         are the same of the {@code that}
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
     * Ex2Cast text record
     */
    record Text
    (
        String film_id,
        String actor_id,
        String role_name
    ) {
        /**
         * The valid length range of {@code role_name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_role_name
        = IntRange.lower(1).upper(32);

        /**
         * Validates the internal state of a Text record.
         * @param role_name the role name of the actor in the film
         * @return  {@code true} if
         *          the {@code role_name} argument is not null, and
         *          1 ≤ {@code role_name.length()} ≤ 32
         */
        public boolean is_valid()
        {
            final boolean validity
            =  role_name != null
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
         * @return  a Text record or {@code null} if
         *          the {@code model} is null, or
         *          the post condition for a Text don't hold
         * @see     Text.is_valid()
         */
        static Text text(final Model model)
        {
            // An invalid Model yields an invalid Text
            if (model == null) return null;

            // the role_name field
            final var role_name = model.role_name;

            // mapping Model to Text
            final var text
            = new Text(model.film.id(), model.actor.id(), role_name);

            // an invalid Text if the post-condition doesn't hold
            return text.is_valid()
            ? text
            : null;
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
