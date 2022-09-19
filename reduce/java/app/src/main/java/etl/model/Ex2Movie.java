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
import etl.util.ModelReader;
import etl.util.TextHelper;

/**
 * Ex2Movie represents a view of Film-Cast relationship.
 */
public interface Ex2Movie
{
    static final String
    REC_T_CODE_FILM = "1",
    REC_T_CODE_CAST = "2";

    /**
     * Record types
     */
    enum RecordType
    {
        FILM(REC_T_CODE_FILM), CAST(REC_T_CODE_CAST);

        /**
         * Transforms a String to the corresponding RecordType.
         * @param code a String corresponding to an enum constant
         * @return     an Optional with the matched RecordType, or
         *             an empty Optional if not matched
         */
        public static Optional<RecordType> of(String code)
        {
            switch (code) {
                case REC_T_CODE_FILM: return Optional.of(FILM);
                case REC_T_CODE_CAST: return Optional.of(CAST);
                default: return Optional.empty();
            }
        }

        /**
         * Returns the value of the enum constant.
         * @return the value of the enum constant
         */
        public String code() { return code; }

        private RecordType(String code) { this.code = code; }
        private final String code;
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
    {}

    interface Extracting
    {
        /**
         * Builds a model Map instance by reading a CSV data.
         * @param reader a Reader to read the CSV data
         * @return       a map of {@code <Film, List<Cast>>}
         */
        static SortedMap<Ex2Film.Model, List<Ex2Cast.Model>> model_map(
            SortedMap<Text.Film, List<Text.Cast>> text_map,
            SortedSet<Ex2Actor.Model> actors
        ) {
            final SortedMap<Ex2Film.Model, List<Ex2Cast.Model>>
            model_map = text_map.entrySet().stream()
            .reduce(
                new TreeMap<Ex2Film.Model, List<Ex2Cast.Model>>(),
                (accum, entry) -> {
                    final var text_film = entry.getKey();
                    final var model_film = new Ex2Film.Model(
                        text_film.name,
                        Year.parse(text_film.release)
                    );
                    final var model_casts = entry.getValue().stream()
                    .map(text_cast -> new Ex2Cast.Model(
                        model_film.id(),
                        actors.stream()
                            .filter(actor ->
                                actor.name().equals(text_cast.actor_name)
                            )
                            .map(Ex2Actor.Model::id)
                            .findFirst()
                            .orElse(null),
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
         * Collects Ex2Actor.Model instances from
         * {@code SortedMap<Text.Film, List<Text.Cast>>}.
         * @param text_map {@code SortedMap<Text.Film, List<Text.Cast>>}
         * @return a sorted set of Ex2Actor.Model instances
         */
        static SortedSet<Ex2Actor.Model> actors(
            SortedMap<Text.Film, List<Text.Cast>> text_map
        ) {
            final var actors = text_map.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(cast ->
                    TextHelper.<Long>parse(
                        cast.actor_then_age,
                        Long::parseLong
                    )
                    .map(age -> new Ex2Actor.Model(
                        cast.actor_name,
                        Year.parse(entry.getKey().release())
                            .minusYears(age)
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
                    (accum, csv_record) -> {
                        Ex2Movie.RecordType.of(
                            csv_record.get(Ex2Movie.Text.INDEX_RECORD_TYPE)
                        )
                        .ifPresentOrElse(constant -> {
                            switch (constant) {
                            case FILM:
                                var text_film = ModelReader.text(
                                    csv_record,
                                    Ex2Movie.Extracting.text_film_ctor
                                );
                                accum.put(text_film, new ArrayList<>());
                                break;
                            case CAST:
                                var text_cast = ModelReader.text(
                                    csv_record,
                                    Ex2Movie.Extracting.text_cast_ctor
                                );
                                accum.get(accum.lastKey()).add(text_cast);
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
