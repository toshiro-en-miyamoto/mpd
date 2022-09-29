package etl.mapper;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import etl.model.Ex2Actor;
import etl.model.Ex2Cast;
import etl.model.Ex2Film;
import etl.model.Ex2Movie;
import etl.util.CloseableSupplier;
import etl.util.ModelWriter;
import etl.util.TextHelper;

/**
 * {@code mapper.Ex2Mapper} reands
 * <ul>
 * <li>Ex2Movie
 * <li>Ex2Film
 * <li>ExActor
 * </ul>
 * and produces {@code Ex2Cast}.
 */
public interface Ex2Mapper
{
    /**
     * Drives producing Ex2Cast from Ex2Movie, Ex2Film, and Ex2Actor.
     * @param args  [0]: path to movie.csv, [1]: path to film.csv
     *              [2]: path to actor.csv, [3]: path to cast.csv
     */
    public static void main(String[] args)
    {
        final Path movie_path = Path.of(args[0]);
        final Path film_path  = Path.of(args[1]);
        final Path actor_path = Path.of(args[2]);
        final Path cast_path  = Path.of(args[3]);

        // preconditions:
        if (!Files.exists(movie_path)) {
            // to migrate to the logging subsystem once it is designed
            System.err.printf(
                "%s does not exist.\n",
                movie_path.toAbsolutePath().toString()
            );
            System.exit(-1);
        }

        // logic
        final CloseableSupplier<Reader> movie_reader
        = () -> Files.newBufferedReader(movie_path, StandardCharsets.UTF_8);

        final CloseableSupplier<Writer> film_writer
        = () -> Files.newBufferedWriter(film_path, StandardCharsets.UTF_8);

        final CloseableSupplier<Writer> actor_writer
        = () -> Files.newBufferedWriter(actor_path, StandardCharsets.UTF_8);

        final CloseableSupplier<Writer> cast_writer
        = () -> Files.newBufferedWriter(cast_path, StandardCharsets.UTF_8);

        Driving.job(movie_reader, film_writer, actor_writer, cast_writer);
    }

    /**
     * The place where the logic to drive a mapping job is implemeted.
     */
    interface Driving
    {
        static void job(
            CloseableSupplier<Reader> movie_reader,
            CloseableSupplier<Writer> film_writer,
            CloseableSupplier<Writer> actor_writer,
            CloseableSupplier<Writer> cast_writer
        ) {
            final var text_map = Ex2Movie.Extracting.text_map(movie_reader);
            final var actors = Mapping.actors(text_map);

            try (
                final var printer = new ModelWriter(actor_writer)
            ) {
                actors.stream()
                .forEach(actor -> printer.accept(actor, Ex2Actor.Loading::values))
                ;
            } catch (Exception ex) {}

            final var model_map = Mapping.model_map(text_map, actors);

            try (
                final var printer = new ModelWriter(film_writer)
            ) {
                model_map.entrySet().stream()
                .map(Map.Entry::getKey)
                .forEach(film -> printer.accept(film, Ex2Film.Loading::values))
                ;    
            } catch (Exception ex) {}

            try (
                final var printer = new ModelWriter(cast_writer)
            ) {
                model_map.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .forEach(cast -> printer.accept(cast, Ex2Cast.Loading::values))
                ;
            } catch (Exception ex) {}
        }
    }

    interface Mapping
    {
        /**
         * Builds a model Map instance by reading a CSV data.
         * @param reader a Reader to read the CSV data
         * @return       a map of {@code <Film, List<Cast>>}
         */
        static SortedMap<Ex2Film.Model, List<Ex2Cast.Model>> model_map(
            final SortedMap<Ex2Movie.Text.Film, List<Ex2Movie.Text.Cast>> text_map,
            final SortedSet<Ex2Actor.Model> actors
        ) {
            if (text_map == null || actors == null) return null;

            final SortedMap<Ex2Film.Model, List<Ex2Cast.Model>>
            model_map = text_map.entrySet().stream()
            .reduce(
                new TreeMap<Ex2Film.Model, List<Ex2Cast.Model>>(),
                (accum, entry) -> {
                    final var text_film = entry.getKey();

                    final var model_film = Ex2Film.Model.instance(
                        text_film.name(),
                        Year.parse(text_film.release())
                    );

                    final var model_casts = entry.getValue().stream()
                    .map(text_cast -> Ex2Cast.Model.instance(
                        model_film,
                        actor_by_name(text_cast.actor_name(), actors).orElse(null),
                        text_cast.role_name()
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
            final SortedMap<Ex2Movie.Text.Film, List<Ex2Movie.Text.Cast>> text_map
        ) {
            final var actors = text_map.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(cast ->
                    TextHelper.<Long>parse(
                        cast.actor_age(),
                        Long::parseLong
                    )
                    .map(age -> Ex2Actor.Model.instance(
                        cast.actor_name(),
                        Year.parse(entry.getKey().release()).minusYears(age)
                    ))
                    .orElse(null)
                )
            )
            .collect(Collectors.toCollection(TreeSet::new))
            ;
            return actors;
        }
    }
}
