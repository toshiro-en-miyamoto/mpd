package etl.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class Ex2MapperTest
{
    static final String movie_csv
    = ""
    + "1,The Deer Hunter,1978\n"
    + "2,Robert De Niro,Mike,35\n\n"
    + "2,Meryl Streep,Linda,29\n"
    + "1,\"Good Morning, Vietnam\",1987\n"
    + "2,Robin Williams,Adrian,36\n"
    + "1,The Bridges of Madison County,1995\n"
    + "2,Clint Eastwood,Robert,65\n"
    + "2,Meryl Streep,Francesca,46\n"
    + "1,The Intern,2015\n"
    + "2,Robert De Niro,Ben,72\n"
    + "2,Rene Russo,Fiona,61\n"
    + "2,Anne Hathaway,Jules,33\n"
    ;

    static final String film_csv
    = ""
    + "17c988ef040c39534fb79cb0c50469f8f714d354,The Deer Hunter,1978\n"
    + "7d6fc9219cbf15473726730ee0444c4dc306e3ca,\"Good Morning, Vietnam\",1987\n"
    + "0aa964061fee6ceb2b6441636d297407891c0bd9,The Bridges of Madison County,1995\n"
    + "d56c04f51408505c8edfe0ab629b481a275d284b,The Intern,2015\n"
    ;

    static final String actor_csv
    = ""
    + "742ab3d2bf94406094358c8c58f404bfe4cb31c0,Clint Eastwood,1930\n"
    + "6c35e841a827e227fe308f6c619058f5ddd215cf,Robert De Niro,1943\n"
    + "2b4d9c4c36be337efe9dc256846fba4455304d61,Meryl Streep,1949\n"
    + "154e9ebdd4652d85e164a4c8a7b4da85a5006068,Robin Williams,1951\n"
    + "221601dd46f25dd57242bc0e284ab8fd4d1ccee5,Rene Russo,1954\n"
    + "1649cbcb825a3fc654169e7e9d202813b62f33cf,Anne Hathaway,1982\n"
    ;

    static final String cast_csv
    = ""
    // The Deer Hunter
    // Robert De Niro, Mike
    // Meryl Streep, Linda
    + "17c988ef040c39534fb79cb0c50469f8f714d354,6c35e841a827e227fe308f6c619058f5ddd215cf,Mike\n"
    + "17c988ef040c39534fb79cb0c50469f8f714d354,2b4d9c4c36be337efe9dc256846fba4455304d61,Linda\n"
    // Good Morning, Vietnam
    // Robin Williams, Adrian
    + "7d6fc9219cbf15473726730ee0444c4dc306e3ca,154e9ebdd4652d85e164a4c8a7b4da85a5006068,Adrian\n"
    // The Bridges of Madison County
    // Clint Eastwood, Robert
    // Meryl Streep, Francesca
    + "0aa964061fee6ceb2b6441636d297407891c0bd9,742ab3d2bf94406094358c8c58f404bfe4cb31c0,Robert\n"
    + "0aa964061fee6ceb2b6441636d297407891c0bd9,2b4d9c4c36be337efe9dc256846fba4455304d61,Francesca\n"
    // The Intern
    // Robert De Niro, Ben
    // Rene Russo, Fiona
    // Anne Hathaway, Jules
    + "d56c04f51408505c8edfe0ab629b481a275d284b,6c35e841a827e227fe308f6c619058f5ddd215cf,Ben\n"
    + "d56c04f51408505c8edfe0ab629b481a275d284b,221601dd46f25dd57242bc0e284ab8fd4d1ccee5,Fiona\n"
    + "d56c04f51408505c8edfe0ab629b481a275d284b,1649cbcb825a3fc654169e7e9d202813b62f33cf,Jules\n"
    ;

    @Test
    void driving()
    {
        final var film_writer = new StringWriter();
        final var actor_writer = new StringWriter();
        final var cast_writer = new StringWriter();

        Ex2Mapper.Driving.job(
            () -> new java.io.StringReader(movie_csv),
            () -> film_writer,
            () -> actor_writer,
            () -> cast_writer
        );

        final var actual_film = film_writer.toString();
        assertEquals(film_csv, actual_film);

        final var actual_actor = actor_writer.toString();
        assertEquals(actor_csv, actual_actor);

        final var actual_cast = cast_writer.toString();
        assertEquals(cast_csv, actual_cast);
    }
}
