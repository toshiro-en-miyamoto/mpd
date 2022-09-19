package etl.mapper;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

import etl.model.Ex2Actor;
import etl.model.Ex2Cast;
import etl.model.Ex3Film;
import etl.model.Ex2Movie;
import etl.util.CloseableSupplier;
import etl.util.ModelWriter;

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
        Path movie_path = Path.of(args[0]);
        Path film_path  = Path.of(args[1]);
        Path actor_path = Path.of(args[2]);
        Path cast_path  = Path.of(args[3]);

        // preconditions:
        if (!Files.exists(movie_path)) {
            // to migrate to the logging subsystem once it is designed
            System.err.printf(
                "%s does not exist.\n",
                movie_path.toAbsolutePath().toString()
            );
            System.exit(-1);
        }
        if (!Files.exists(film_path)) {
            // to migrate to the logging subsystem once it is designed
            System.err.printf(
                "%s does not exist.\n",
                film_path.toAbsolutePath().toString()
            );
            System.exit(-1);
        }
        if (!Files.exists(actor_path)) {
            // to migrate to the logging subsystem once it is designed
            System.err.printf(
                "%s does not exist.\n",
                actor_path.toAbsolutePath().toString()
            );
            System.exit(-1);
        }

        // logic
        final CloseableSupplier<Reader> movie_reader
        = () -> Files.newBufferedReader(movie_path, StandardCharsets.UTF_8);

        final CloseableSupplier<Reader> film_reader
        = () -> Files.newBufferedReader(film_path, StandardCharsets.UTF_8);

        final CloseableSupplier<Reader> actor_reader
        = () -> Files.newBufferedReader(actor_path, StandardCharsets.UTF_8);

        final CloseableSupplier<Writer> cast_writer
        = () -> Files.newBufferedWriter(cast_path, StandardCharsets.UTF_8);

        Driving.job(movie_reader, film_reader, actor_reader, cast_writer);
    }

    /**
     * The place where the logic to drive a mapping job is implemeted.
     */
    interface Driving
    {
        static void job(
            CloseableSupplier<Reader> movie_reader,
            CloseableSupplier<Reader> film_reader,
            CloseableSupplier<Reader> actor_reader,
            CloseableSupplier<Writer> cast_writer
        ) {
            try (
                final var printer = new ModelWriter(cast_writer)
            ) {
                final var movies = Ex2Movie.Extracting
                .reduce(movie_reader);

                final var films = Ex3Film.Extracting
                .models(film_reader)
                .collect(Collectors.toList());

                final var actors = Ex2Actor.Extracting
                .models(actor_reader)
                .collect(Collectors.toList());

                Mapping.casting(movies, films, actors).stream()
                .forEach(cast_model ->
                    printer.accept(cast_model, Ex2Cast.Loading::values)
                )
                ;

            } catch (Exception ex) {}
        }
    }

    /**
     * The place where the logic to produce Ex2Cast.Model instances
     * is implemented.
     */
    interface Mapping
    {
        /**
         * Maps a SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>
         * to a list of Ex2Cast.Model records.
         * @param movies a map from Ex2Movie.Model.Film to List<Ex2Movie.Model.Cast>
         * @param films  a list of Ex2Film.Model
         * @param actors a list of Ex2Actor.Model
         * @return       a list of Ex2Cast.Model records
         */
        static List<Ex2Cast.Model> casting(
            SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>> movies,
            List<Ex3Film.Model> films,
            List<Ex2Actor.Model> actors
        ) {
            var casting = movies.entrySet().stream()
            .flatMap(entry ->
                entry.getValue().stream()
                .map(cast -> new Ex2Cast.Model(
                    films.stream()
                        .filter(film -> film.name().equals(entry.getKey().name()))
                        .findFirst()
                        .map(Ex3Film.Model::id)
                        .orElse(null),
                    actors.stream()
                        .filter(actor -> actor.name().equals(cast.actor_name()))
                        .findFirst()
                        .map(Ex2Actor.Model::id)
                        .orElse(null),
                    cast.role_name()
                ))
            )
            .collect(Collectors.toList());

            return casting;
        }
    }
}
