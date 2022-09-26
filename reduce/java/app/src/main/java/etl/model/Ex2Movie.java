package etl.model;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import etl.util.CloseableSupplier;
import etl.util.IntRange;
import etl.util.ModelReader;
import etl.util.TextHelper;

/**
 * Ex2Movie represents a view of Film-Cast relationship.
 */
public interface Ex2Movie
{
    interface RecordType
    {
        /**
         * The index of the record type field in the Ex2Movie CSV file.
         */
        static final int INDEX = 0;

        /**
         * The string constants of the record type field
         * in the Ex2Movie CSV file.
         */
        static final String
        CODE_FILM = "1",
        CODE_CAST = "2";

        /**
         * Defines {@code enum} constants of {@code RecordType}.
         */
        enum Constant
        {
            FILM(CODE_FILM), CAST(CODE_CAST);

            /**
             * Returns the value of the enum constant.
             * @return the value of the enum constant
             */
            public String code() { return code; }

            private Constant(String code) { this.code = code; }
            private final String code;
        }

        /**
         * Transforms a String to the corresponding {@code RecordType}.
         * @param code a String corresponding to an enum constant
         * @return  an {@code Optional} with the matched {@code RecordType},
         *          or an empty Optional if {@code code} is null or doesn't
         *          matched to any enum constant.
         */
        public static Optional<Constant> of(String code)
        {
            if (code == null) return Optional.empty();

            switch (code) {
                case CODE_FILM: return Optional.of(Constant.FILM);
                case CODE_CAST: return Optional.of(Constant.CAST);
                default: return Optional.empty();
            }
        }
    }


    /**
     * Ex2Movie text records
     */
    interface Text
    {
        /**
         * Ex2Movie.Text.Film record implements the {@code Comparable}
         * interface (therefore, {@code equals()} and {@code hashCode()}
         * as well), because we want to sort a list of films.
         */
        record Film
        (
            String record_type,
            String name,
            String release
        )
            implements Comparable<Film>
        {
            /**
             * The valid length range of {@code name}.
             */
            static final IntRange VALID_LENGTH_RANGE_name
            = IntRange.lower(1).upper(32);

            /**
             * The valid length range of {@code release}.
             */
            static final IntRange VALID_LENGTH_RANGE_release
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
                && release.matches("^[0-9]+$")
                ;
                return validity;
            }

            /**
             * Transforms the CSVRecord to the Text.Film record.
             * @param csv a CSVRecord instance
             * @return    a Text.Film record
             */
            static Film instance(final CSVRecord csv)
            {
                final var text = ModelReader.text(csv, ctor);
                return text.is_valid()
                ? text
                : null;
            }

            private static final Constructor<Text.Film> ctor
            = TextHelper.ctor(Text.Film.class);

            /**
             * Compares this model with the specified Text for order.
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
            public int compareTo(final Film that)
            {
                var comparison = this.release.equals(that.release)
                ? this.name.compareTo(that.name)
                : this.release.compareTo(that.release)
                ;
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
                Film that = (Film) obj;
    
                final var equality
                =  this.name.equals(that.name())
                && this.release.equals(that.release)
                ;
                return equality;
            }
    
            /**
             * Returns a hash code value for the object.
             * @return a hash code value for this object
             */
            @Override
            public int hashCode()
            {
                return name.concat(release).hashCode();
            }
        }

        /**
         * Ex2Movie.Text.Cast record
         */
        record Cast
        (
            String record_type,
            String actor_name,
            String role_name,
            String actor_age
        ) {
            /**
             * The valid length range of {@code actor_name}.
             */
            static final IntRange VALID_LENGTH_RANGE_actor_name
            = IntRange.lower(1).upper(32);

            /**
             * The valid length range of {@code role_name}.
             */
            static final IntRange VALID_LENGTH_RANGE_role_name
            = IntRange.lower(1).upper(32);

            /**
             * The valid length range of {@code actor_then_age}.
             */
            static final IntRange VALID_LENGTH_RANGE_actor_age
            = IntRange.lower(1).upper(3);

            /**
             * Validates the internal state of a Text record.
             * @return  {@code true} if
             *          the {@code actor_name} argument is not null,
             *          1 ≤ {@code actor_name.length()} ≤ 32,
             *          the {@code role_name} argument is not null,
             *          1 ≤ {@code role_name.length()} ≤ 32,
             *          the {@code actor_age} argument is not null, and
             *          1 ≤ {@code actor_age.length()} ≤ 3,
             */
            public boolean is_valid()
            {
                final boolean validity
                =  actor_name != null
                && VALID_LENGTH_RANGE_actor_name.covers(actor_name.length())
                && role_name != null
                && VALID_LENGTH_RANGE_role_name.covers(role_name.length())
                && actor_age != null
                && VALID_LENGTH_RANGE_actor_age.covers(actor_age.length())
                && actor_age.matches("^[0-9]+$")
                ;
                return validity;
            }

            /**
             * Transforms the CSVRecord to the Text.Cast record.
             * @param csv a CSVRecord instance
             * @return    a Text.Cast record
             */
            static Cast instance(final CSVRecord csv)
            {
                final var text = ModelReader.text(csv, ctor);
                return text.is_valid()
                ? text
                : null;
            }

            private static final Constructor<Text.Cast> ctor
            = TextHelper.ctor(Text.Cast.class);
        }
    }

    /**
     * Extracting provides methods for reading Model records
     * from CSV files.
     */
    interface Extracting
    {
        /**
         * Builds a model Map instance by reading a CSV data.
         * @param reader a Reader to read the CSV data
         * @return       a map of {@code <Film, List<Cast>>}
         */
        static SortedMap<Ex2Film.Model, List<Ex2Cast.Model>> model_map(
            final SortedMap<Text.Film, List<Text.Cast>> text_map,
            final SortedSet<Ex2Actor.Model> actors
        ) {
            final SortedMap<Ex2Film.Model, List<Ex2Cast.Model>>
            model_map = text_map.entrySet().stream()
            .reduce(
                new TreeMap<Ex2Film.Model, List<Ex2Cast.Model>>(),
                (accum, entry) -> {
                    final var text_film = entry.getKey();

                    final var model_film = Ex2Film.Model.instance(
                        text_film.name,
                        Year.parse(text_film.release)
                    );

                    final var model_casts = entry.getValue().stream()
                    .map(text_cast -> Ex2Cast.Model.instance(
                        model_film,
                        actor_by_name(text_cast.actor_name, actors).orElse(null),
                        text_cast.role_name
                    ))
                    .collect(Collectors.toList());

                    accum.put(model_film, model_casts);
                    return accum;
                },
                (accum, other) -> {
                    accum.putAll(other);
                    return accum;
                }
            );
            return model_map;
        }

        /**
         * Returns an actor model.
         * @param name   the name of actor to look the specified list for
         * @param actors a list of actors
         * @return       an Optional with the matched actor or
         *               an empty Optional if not found
         */
        static Optional<Ex2Actor.Model> actor_by_name(
            final String name,
            final SortedSet<Ex2Actor.Model> actors
        ) {
            final var match = actors.stream()
            .filter(actor -> name.equals(actor.name()))
            .findFirst()
            ;
            return match;
        }

        /**
         * Collects Ex2Actor.Model instances from
         * {@code SortedMap<Text.Film, List<Text.Cast>>}.
         * @param text_map {@code SortedMap<Text.Film, List<Text.Cast>>}
         * @return a sorted set of Ex2Actor.Model instances
         */
        static SortedSet<Ex2Actor.Model> actors(
            final SortedMap<Text.Film, List<Text.Cast>> text_map
        ) {
            final var actors = text_map.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(cast ->
                    TextHelper.<Long>parse(
                        cast.actor_age,
                        Long::parseLong
                    )
                    .map(age -> Ex2Actor.Model.instance(
                        cast.actor_name,
                        Year.parse(entry.getKey().release()).minusYears(age)
                    ))
                    .orElse(null)
                )
            )
            .collect(Collectors.toCollection(TreeSet::new))
            ;
            return actors;
        }

        /**
         * Builds a Text map instance by reading a CSV data.
         * @param reader a Reader to read the CSV data
         * @return       a map of {@code <Film, List<Cast>>}
         */
        static SortedMap<Text.Film, List<Text.Cast>> text_map(
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
                final SortedMap<Text.Film, List<Text.Cast>>
                map = parser.stream()
                .reduce(
                    new TreeMap<Text.Film, List<Text.Cast>>(),
                    (accum, csv) -> {
                        Ex2Movie.RecordType.of(
                            csv.get(RecordType.INDEX)
                        )
                        .ifPresentOrElse(constant -> {
                            switch (constant) {
                            case FILM:
                                var text_film = Text.Film.instance(csv);
                                accum.put(text_film, new ArrayList<>());
                                break;
                            case CAST:
                                var text_cast = Text.Cast.instance(csv);
                                accum.get(accum.lastKey()).add(text_cast);
                                break;
                            }
                        }, () -> {
                            // ToDo: call the logging subsystem rather than System.err.
                            System.err.printf(
                                "%d: an invalid constant or an empty line\n",
                                csv.getRecordNumber()
                            );
                        });
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
    }
}
