package etl.model;

import java.lang.reflect.Constructor;
import java.time.Year;
import java.util.Optional;

import org.apache.commons.csv.CSVRecord;

import etl.util.IntRange;
import etl.util.ModelReader;
import etl.util.TextHelper;

/**
 * Ex2Movie represents a view of Film-Cast relationship.
 */
public interface Ex2Movie
{
    /**
     * Record types
     */
    enum RecordType
    {
        FILM(1), CAST(2);

        /**
         * The valid range of {@code RecordType}.
         */
        public static final IntRange VALID_RANGE_RecordType
        = IntRange.lower(FILM.value()).upper(CAST.value());

        /**
         * Returns the value of the enum constant.
         * @return the value of the enum constant
         */
        public int value() { return value; }

        private RecordType(int value) { this.value = value; }
        private final int value;
    }

    /**
     * Ex2Movie text records
     */
    interface Text
    {
        /**
         * Ex2Movie.Text.Film record
         */
        record Film
        (
            String record_type,
            String name,
            String release
        ) {}

        /**
         * Ex2Movie.Text.Cast record
         */
        record Cast
        (
            String record_type,
            String seq_cast,
            String actor_name,
            String role_name,
            String actor_then_age
        ) {}

        static Optional<RecordType> record_type(final CSVRecord csv)
        {
            final var record_type_value = TextHelper.<Integer>parse(
                csv.get(0),
                Integer::parseInt
            ).orElse(null);
            return Optional.empty();
        }
    }

    /**
     * Ex2Movie model records
     */
    interface Model
    {
        /**
         * Ex2Movie.Model.Film record
         */
        record Film
        (
            String  name,
            Year    release
        ) {
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
                =  name != null
                && VALID_LENGTH_RANGE_name.covers(name.length())

                && release != null
                ;
                return validity;
            }
        }

        /**
         * Ex2Movie.Model.Cast record
         */
        record Cast
        (
            Integer seq_cast,
            String  actor_name,
            String  role_name,
            Integer actor_then_age
        ) {
            /**
             * The valid range of {@code seq_cast}.
             */
            public static final IntRange VALID_RANGE_seq_cast
            = IntRange.lower(1).upper(Integer.MAX_VALUE);

            /**
             * The valid length range of {@code actor_name}.
             */
            public static final IntRange VALID_LENGTH_RANGE_actor_name
            = IntRange.lower(1).upper(32);

            /**
             * The valid length range of {@code role_name}.
             */
            public static final IntRange VALID_LENGTH_RANGE_role_name
            = IntRange.lower(1).upper(32);

            /**
             * The valid range of {@code actor_then_age}.
             */
            public static final IntRange VALID_RANGE_actor_then_age
            = IntRange.lower(0).upper(Integer.MAX_VALUE);

            /**
             * Validates the model record.
             * @return {@code true} if the model record is valid
             */
            boolean isValid()
            {
                boolean validity
                =  seq_cast != null
                && VALID_RANGE_seq_cast.covers(seq_cast)

                && actor_name != null
                && VALID_LENGTH_RANGE_actor_name.covers(actor_name.length())

                && role_name != null
                && VALID_LENGTH_RANGE_role_name.covers(role_name.length())

                && actor_then_age != null
                && VALID_RANGE_actor_then_age.covers(actor_then_age)
                ;
                return validity;
            }
        }
    }

    interface Extracting
    {
        /**
         * Transforms the CSVRecord to the Text.Film record.
         * @param csv a CSVRecord instance
         * @return    a Text.Film record
         */
        static Text.Film text_film(final CSVRecord csv)
        {
            final var text = ModelReader.text(csv, text_film_ctor);
            return text;
        }

        /**
         * Transforms the CSVRecord to the Text.Cast record.
         * @param csv a CSVRecord instance
         * @return    a Text.Cast record
         */
        static Text.Cast text_cast(final CSVRecord csv)
        {
            final var text = ModelReader.text(csv, text_cast_ctor);
            return text;
        }

        /**
         * The canonical constructor of the Text.Film record type.
         */
        static final Constructor<Text.Film> text_film_ctor
        = TextHelper.ctor(Text.Film.class);

        /**
         * The canonical constructor of the Text.Cast record type.
         */
        static final Constructor<Text.Cast> text_cast_ctor
        = TextHelper.ctor(Text.Cast.class);
    }
}
