package etl.model;

import java.time.Year;

import etl.util.IntRange;
import etl.util.Sha1;
import etl.util.TextHelper;

/**
 * Ex2Film represents the Film entity.
 */
public interface Ex2Film
{
    /**
     * Ex2Film model record implements the {@code Comparable}
     * interface (therefore, {@code equals()} and {@code hashCode()}
     * as well), because we want to sort a list of films.
     */
    record Model
    (
        String id,
        String name,
        Year   release
    )
        implements Comparable<Model>
    {
        /**
         * Instanciates an Model instance.
         * @param name    the name of the film
         * @param release the year when the film was released
         * @return  a Model instance or {@code null} if
         *          {@code name} is {@code null}, or
         *          {@code release} is {@code null}
         */
        public static Model instance(String name, Year release)
        {
            // an invalid Model if the pre-conditions don't hold
            if (name == null || release == null) {
                return null;
            }

            // the id field
            final var id
            = Sha1.hex_string(name.concat(release.toString()));

            // create a Model instance
            final var model = new Model(id, name, release);

            return model;
        }

        /**
         * Compares this model with the specified Model for order.
         * Firstly, the release year is compared. If they are equal,
         * then the name is compared.
         * @param that the object to be compared
         * @return  zero if both the release year and the name are equal;
         *          if this release year is less or greater than that year,
         *          then a negative or positive integer returns respectively;
         *          if the release year is equal and this name
         *          lexicographically precedes or follows that name, then
         *          a negative or positive integer returns respectively.
         */
        @Override
        public int compareTo(final Model that)
        {
            final var comparison = this.release.equals(that.release)
            ? this.name.compareTo(that.name)
            : this.release.compareTo(that.release);
            
            return comparison;
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * @param that the reference object with which to compare
         * @return {@code true} if the {@code id} field of this object
         *         is equals to the field of the {@code that}
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            final Model that = (Model) obj;

            final var equality = this.id.equals(that.id);
            return equality;
        }

        /**
         * Returns a hash code value for the object.
         * @return a hash code value for this object
         */
        @Override
        public int hashCode()
        {
            return id.hashCode();
        }
    }

    /**
     * Ex2Film text record
     */
    record Text
    (
        String id,
        String name,
        String release
    ) {
        /**
         * The valid length range of {@code name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_name
        = IntRange.lower(1).upper(32);

        /**
         * The valid length range of {@code release}.
         */
        public static final IntRange VALID_LENGTH_RANGE_release
        = IntRange.lower(4).with_same_upper();

        /**
         * Validates the internal state of a Text record.
         * @return  {@code true} if
         *          the {@code name} argument is not null,
         *          1 ≤ {@code name.length()} ≤ 32,
         *          the {@code release} argument is not null, and
         *          {@code release.length()} == 4
         */
        public boolean is_valid()
        {
            final boolean validity
            =  name != null
            && VALID_LENGTH_RANGE_name.covers(name.length())
            && release != null
            && VALID_LENGTH_RANGE_release.covers(release.length())
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
            // an invalid Model yields an invalid Text
            if (model == null) return null;

            // the release field
            final var release = model.release.toString();

            // mapping Model to Text
            final var text
            = new Text(model.id, model.name, release);

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
