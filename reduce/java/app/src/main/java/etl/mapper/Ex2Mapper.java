package etl.mapper;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import etl.model.Ex2Actor;
import etl.model.Ex2Cast;
import etl.model.Ex2Film;
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
            final var actors = Ex2Movie.Extracting.actors(text_map);

            try (
                final var printer = new ModelWriter(actor_writer)
            ) {
                actors.stream()
                .forEach(actor -> printer.accept(actor, Ex2Actor.Loading::values))
                ;
            } catch (Exception ex) {}

            final var model_map = Ex2Movie.Extracting.model_map(text_map, actors);

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
}
