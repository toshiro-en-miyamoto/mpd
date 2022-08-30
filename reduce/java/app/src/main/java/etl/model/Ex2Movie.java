package etl.model;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import etl.util.CloseableSupplier;
import etl.util.IntRange;
import etl.util.ModelReader;
import etl.util.Sequencer;
import etl.util.TextHelper;

/**
 * Ex2Movie represents a view of Film-Cast relationship.
 */
public interface Ex2Movie
{
    static final String
    KEYWORD_FILM = "1",
    KEYWORD_CAST = "2";

    /**
     * Record types
     */
    enum RecordType
    {
        FILM(KEYWORD_FILM), CAST(KEYWORD_CAST);

        /**
         * Transforms a {@code String} to the corresponding constant
         * @param keyword of a constant
         * @return        the matched constant or {@code Optional.empty()} if not matched
         */
        public static Optional<RecordType> of(String keyword)
        {
            switch (keyword) {
            case KEYWORD_FILM: return Optional.of(FILM);
            case KEYWORD_CAST: return Optional.of(CAST);
            default: return Optional.empty();
            }
        }

        /**
         * Returns the value of the enum constant.
         * @return the value of the enum constant
         */
        public String keyword() { return keyword; }

        private RecordType(String keyword) { this.keyword = keyword; }
        private final String keyword;
    }

    /**
     * Ex2Movie text records
     */
    interface Text
    {
        /**
         * The index of the record type field in Text.Film and Text.Cast records.
         */
        static final int INDEX_RECORD_TYPE = 0;

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
            String actor_name,
            String role_name,
            String actor_then_age
        ) {}
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
            Long    seq_film,
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
                = actor_name != null
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
         * Builds a Map instance by reading a CSV data.
         * @param reader a Reader to read the CSV data
         * @return       a map of {@code <Film, List<Cast>>}
         */
        static SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>> reduce(
            CloseableSupplier<Reader> reader
        ) {
            final var format = CSVFormat.Builder
            .create()
            .setIgnoreEmptyLines(false)
            .setIgnoreSurroundingSpaces(true)
            .build();
    
            try (
                final var parser = CSVParser.parse(reader.get(), format)
            ) {
                final var film_sequence = Sequencer.starting(0);
    
                final SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>
                map = parser.stream()
                .reduce(
                    new TreeMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>
                    ((f1, f2) -> {
                        return Long.compare(f1.seq_film(), f2.seq_film());
                    }),
                    (accum, csv_record) -> {
                        Ex2Movie.RecordType.of(
                            csv_record.get(Ex2Movie.Text.INDEX_RECORD_TYPE)
                        )
                        .ifPresentOrElse(constant -> {
                            switch (constant) {
                            case FILM:
                                var model_film = Ex2Movie.Extracting.model(
                                    ModelReader.text(
                                        csv_record,
                                        Ex2Movie.Extracting.text_film_ctor
                                    ),
                                    film_sequence.next()
                                );
                                accum.put(model_film, new ArrayList<>());
                                break;
                            case CAST:
                                var model_cast = Ex2Movie.Extracting.model(
                                    ModelReader.text(
                                        csv_record,
                                        Ex2Movie.Extracting.text_cast_ctor
                                    )
                                );
                                accum.get(accum.lastKey()).add(model_cast);
                                break;
                            }
                        },
                        () -> {
                            // ToDo: call the logging subsystem rather than System.err.
                            System.err.printf(
                                "%d: an invalid constant or an empty line\n",
                                csv_record.getRecordNumber()
                            );
                        }
                        );
                        return accum;
                    },
                    (accum, other) -> {
                        accum.putAll(other);
                        return accum;
                    }
                );
                return map;
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * Transforms a Text.Film record to a Model.Film record.
         * @param text a Text.Film record
         * @return     a Model.Film record
         */
        static Model.Film model(final Text.Film text, final Long sequence)
        {
            // An invalid Text yeilds an invalid Model
            if (text == null) return null;

            // String  name
            final var name_length_exclusive
            = Model.Film.VALID_LENGTH_RANGE_name.upper() + 1;

            final var name
            = text.name == null ? null
            : text.name.length() < name_length_exclusive
            ? text.name
            : text.name.substring(0, name_length_exclusive);

            // Year    release
            final var release = TextHelper.<Year>parse(
                text.release,
                year -> Year.parse(year)
            ).orElse(null);

            // mapping Text.Film to Model.Film
            final Model.Film model = new Model.Film(
                sequence,
                name,
                release
            );

            // a null represents an invalid Model
            return model.isValid() ? model : null;
        }

        /**
         * Transforms a Text.Cast record to a Model.Cast record.
         * @param text a Text.Cast record
         * @return     a Model.Cast record
         */
        static Model.Cast model(final Text.Cast text)
        {
            // An invalid Text yeilds an invalid Model
            if (text == null) return null;

            // String  actor_name,
            final var actor_name_length_exclusive
            = Model.Cast.VALID_LENGTH_RANGE_actor_name.upper() + 1;

            final var actor_name
            = text.actor_name == null ? null
            : text.actor_name.length() < actor_name_length_exclusive
            ? text.actor_name
            : text.actor_name.substring(0, actor_name_length_exclusive);

            // String  role_name,
            final var role_name_length_exclusive
            = Model.Cast.VALID_LENGTH_RANGE_role_name.upper() + 1;

            final var role_name
            = text.role_name == null ? null
            : text.role_name.length() < role_name_length_exclusive
            ? text.role_name
            : text.role_name.substring(0, role_name_length_exclusive);

            // Integer actor_then_age
            final var age = TextHelper.parse(
                text.actor_then_age,
                Integer::parseInt
            ).orElse(null);

            // mapping Text.Cast to Model.Cast
            final Model.Cast model = new Model.Cast(
                actor_name,
                role_name,
                age
            );

            // a null represents an invalid Model
            return model.isValid() ? model : null;
        }

        /**
         * Returns a Stream containing Text.Film records extracted out of the
         * CSV file the supplier argument is attached to.
         * @param supplier a Reader attached to a CSV file
         * @return         a Stream containing Text.Film records
         */
        static Stream<Text.Film> texts_film(
            final CloseableSupplier<Reader> supplier
        ) {
            final var texts = ModelReader.stream(
                supplier,
                Text.Film.class,
                Extracting::text_film
            );
            return texts;
        }

        /**
         * Returns a Stream containing Text.Cast records extracted out of the
         * CSV file the supplier argument is attached to.
         * @param supplier a Reader attached to a CSV file
         * @return         a Stream containing Text.Cast records
         */
        static Stream<Text.Cast> texts_cast(
            final CloseableSupplier<Reader> supplier
        ) {
            final var texts = ModelReader.stream(
                supplier,
                Text.Cast.class,
                Extracting::text_cast
            );
            return texts;
        }

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
