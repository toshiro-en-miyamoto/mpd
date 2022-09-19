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
     * Ex2Film text record
     */
    record Text (
        String id,
        String name,
        String release
    ) {}

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
         * Constructs a new Film.
         * @param name    the name of the film
         * @param release Year when the film was released
         */
        public Model(String name, Year release)
        {
            this(Sha1.hex_string(name.concat(release.toString())), name, release);
        }

        /**
         * The valid length range of {@code id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_id
        = IntRange.lower(Sha1.HEX_TEXT_LENGTH).upper(Sha1.HEX_TEXT_LENGTH);

        /**
         * The valid length range of {@code name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_name
        = IntRange.lower(1).upper(32);

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
            
            && release != null
            ;
            return validity;
        }

        /**
         * Compares this model with the specified model for order.
         * Firstly, the release year is compared. If they are equal,
         * then the name is compared.
         * @param that the object to be compared
         * @return  zero if both the release year and the name are equal;
         *          if this release year is less or greater than that year,
         *          then a negative or positive integer returns respectively;
         *          if the release year is equal and this name
         *          lexicographically preceedes or follows that name, then
         *          a negative or positive integer returns respectively.
         */
        @Override
        public int compareTo(final Model that)
        {
            var comparison =
            this.release != that.release
            ? this.release.compareTo(that.release)
            : this.name.compareTo(that.name);
            
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

            // the id field
            final var max_len_id = Model.VALID_LENGTH_RANGE_id.upper();
            final var id
            = model.id.length() > max_len_id
            ? model.id.substring(0, max_len_id)
            : model.id
            ;

            // the name field
            final var max_len_name = Model.VALID_LENGTH_RANGE_name.upper();
            final var name
            = model.name.length() > max_len_name
            ? model.name.substring(0, max_len_name)
            : model.name
            ;

            // the release field
            final var release = model.release.toString();

            // mapping Model to Text
            final var text = new Text(id, name, release);

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
