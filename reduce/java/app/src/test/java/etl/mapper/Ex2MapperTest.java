package etl.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;
import java.time.Year;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import etl.model.Ex2Actor;
import etl.model.Ex2Cast;
import etl.model.Ex2Film;
import etl.model.Ex2Movie;

public class Ex2MapperTest
{
    final static String movie_csv
    = "1,The Deer Hunter,1978\n"
    + "2,Robert De Niro,Mike,35\n"
    + "2,Meryl Streep,Linda,29\n"
    + "1,\"Good Morning, Vietnam\",1987\n"
    + "2,Robin Williams,Adrian,44\n"
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
    + "bdb8c01fd6a734fd84804ad1835204fdfe9bbe90,Robin Williams,1943\n"
    + "2b4d9c4c36be337efe9dc256846fba4455304d61,Meryl Streep,1949\n"
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
    + "7d6fc9219cbf15473726730ee0444c4dc306e3ca,bdb8c01fd6a734fd84804ad1835204fdfe9bbe90,Adrian\n"
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

    static final String String_Deer_Hunter     = "The Deer Hunter";
    static final String String_Morning_Vietnum = "Good Morning, Vietnam";
    static final String String_Madison_County  = "The Bridges of Madison County";
    static final String String_Intern          = "The Intern";

    static final String code_film = Ex2Movie.RecordKind.CODE_FILM;
    static final String code_cast = Ex2Movie.RecordKind.CODE_CAST;

    static final Ex2Movie.Text.Film       Text_Deer_Hunter
    = new Ex2Movie.Text.Film(code_film, String_Deer_Hunter,     "1978");
    static final Ex2Movie.Text.Film       Text_Morning_Vietnum
    = new Ex2Movie.Text.Film(code_film, String_Morning_Vietnum, "1987");
    static final Ex2Movie.Text.Film       Text_Madison_County
    = new Ex2Movie.Text.Film(code_film, String_Madison_County,  "1995");
    static final Ex2Movie.Text.Film       Text_Intern
    = new Ex2Movie.Text.Film(code_film, String_Intern,          "2015");

    static final Ex2Film.Model Model_Deer_Hunter
    = Ex2Film.Model.instance( String_Deer_Hunter,     Year.of(1978));
    static final Ex2Film.Model Model_Morning_Vietnum
    = Ex2Film.Model.instance( String_Morning_Vietnum, Year.of(1987));
    static final Ex2Film.Model Model_Madison_County
    = Ex2Film.Model.instance( String_Madison_County,  Year.of(1995));
    static final Ex2Film.Model Model_Intern
    = Ex2Film.Model.instance( String_Intern,          Year.of(2015));

    static final String String_Clint_Eastwood = "Clint Eastwood";
    static final String String_Robert_De_Niro = "Robert De Niro";
    static final String String_Robin_Williams = "Robin Williams";
    static final String String_Meryl_Streep   = "Meryl Streep";
    static final String String_Rene_Russo     = "Rene Russo";
    static final String String_Anne_Hathaway  = "Anne Hathaway";

    static final Ex2Actor.Model Model_Clint_Eastwood
    = Ex2Actor.Model.instance( String_Clint_Eastwood, Year.of(1930));
    static final Ex2Actor.Model Model_Robert_De_Niro
    = Ex2Actor.Model.instance( String_Robert_De_Niro, Year.of(1943));
    static final Ex2Actor.Model Model_Robin_Williams
    = Ex2Actor.Model.instance( String_Robin_Williams, Year.of(1943));
    static final Ex2Actor.Model Model_Meryl_Streep
    = Ex2Actor.Model.instance( String_Meryl_Streep,   Year.of(1949));
    static final Ex2Actor.Model Model_Rene_Russo
    = Ex2Actor.Model.instance( String_Rene_Russo,     Year.of(1954));
    static final Ex2Actor.Model Model_Anne_Hathaway
    = Ex2Actor.Model.instance( String_Anne_Hathaway,  Year.of(1982));

    static final Ex2Cast.Model Model_Robert_De_Niro_in_Deer_Hunter
    = Ex2Cast.Model.instance(Model_Deer_Hunter, Model_Robert_De_Niro, "Mike");
    static final Ex2Cast.Model Model_Meryl_Streep_in_Deer_Hunter
    = Ex2Cast.Model.instance(Model_Deer_Hunter, Model_Meryl_Streep, "Linda");
    static final Ex2Cast.Model Model_Robin_Williams_in_Morning_Vietnum
    = Ex2Cast.Model.instance(Model_Morning_Vietnum, Model_Robin_Williams, "Adrian");
    static final Ex2Cast.Model Model_Clint_Eastwood_in_Madison_County
    = Ex2Cast.Model.instance(Model_Madison_County, Model_Clint_Eastwood, "Robert");
    static final Ex2Cast.Model Model_Meryl_Streep_in_Madison_County
    = Ex2Cast.Model.instance(Model_Madison_County, Model_Meryl_Streep, "Francesca");
    static final Ex2Cast.Model Model_Robert_De_Niro_in_Intern
    = Ex2Cast.Model.instance(Model_Intern, Model_Robert_De_Niro, "Ben");
    static final Ex2Cast.Model Model_Rene_Russo_in_Intern
    = Ex2Cast.Model.instance(Model_Intern, Model_Rene_Russo, "Fiona");
    static final Ex2Cast.Model Model_Anne_Hathaway_in_Intern
    = Ex2Cast.Model.instance(Model_Intern, Model_Anne_Hathaway, "Jules");

    static final Ex2Movie.Text.Cast Text_Robert_De_Niro_in_Deer_Hunter
    = new Ex2Movie.Text.Cast(code_cast, String_Robert_De_Niro, "Mike", "35");
    static final Ex2Movie.Text.Cast Text_Meryl_Streep_in_Deer_Hunter
    = new Ex2Movie.Text.Cast(code_cast, String_Meryl_Streep, "Linda", "29");
    static final Ex2Movie.Text.Cast Text_Robin_Williams_in_Morning_Vietnum
    = new Ex2Movie.Text.Cast(code_cast, String_Robin_Williams, "Adrian", "44");
    static final Ex2Movie.Text.Cast Text_Clint_Eastwood_in_Madison_County
    = new Ex2Movie.Text.Cast(code_cast, String_Clint_Eastwood, "Robert", "65");
    static final Ex2Movie.Text.Cast Text_Meryl_Streep_in_Madison_County
    = new Ex2Movie.Text.Cast(code_cast, String_Meryl_Streep, "Francesca", "46");
    static final Ex2Movie.Text.Cast Text_Robert_De_Niro_in_Intern
    = new Ex2Movie.Text.Cast(code_cast, String_Robert_De_Niro, "Ben", "72");
    static final Ex2Movie.Text.Cast Text_Rene_Russo_in_Intern
    = new Ex2Movie.Text.Cast(code_cast, String_Rene_Russo, "Fiona", "61");
    static final Ex2Movie.Text.Cast Text_Anne_Hathaway_in_Intern
    = new Ex2Movie.Text.Cast(code_cast, String_Anne_Hathaway, "Jules", "33");

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

    @Test
    void model_map()
    {
        final var movie_reader = new java.io.StringReader(movie_csv);
        final var text_map = Ex2Movie.Extracting.text_map(() -> movie_reader);
        final var actors = Ex2Mapper.Mapping.actors(text_map);

        SortedMap<Ex2Film.Model, List<Ex2Cast.Model>> expected
        = new TreeMap<Ex2Film.Model, List<Ex2Cast.Model>>();

        {
            expected.put(
                Model_Deer_Hunter,
                List.<Ex2Cast.Model>of(
                    Model_Robert_De_Niro_in_Deer_Hunter,
                    Model_Meryl_Streep_in_Deer_Hunter
                )
            );
            expected.put(
                Model_Morning_Vietnum,
                List.<Ex2Cast.Model>of(
                    Model_Robin_Williams_in_Morning_Vietnum
                )
            );
            expected.put(
                Model_Madison_County,
                List.<Ex2Cast.Model>of(
                    Model_Clint_Eastwood_in_Madison_County,
                    Model_Meryl_Streep_in_Madison_County
                )
            );
            expected.put(
                Model_Intern,
                List.<Ex2Cast.Model>of(
                    Model_Robert_De_Niro_in_Intern,
                    Model_Rene_Russo_in_Intern,
                    Model_Anne_Hathaway_in_Intern
                )
            );
        }

        final var actual = Ex2Mapper.Mapping.model_map(text_map, actors);

        assertEquals(expected, actual);
    }

    @Test
    void actors()
    {
        SortedSet<Ex2Actor.Model> expected = new TreeSet<>();
        {
            expected.add(Model_Robert_De_Niro_in_Deer_Hunter.actor());
            expected.add(Model_Meryl_Streep_in_Deer_Hunter.actor());
            expected.add(Model_Robin_Williams_in_Morning_Vietnum.actor());
            expected.add(Model_Clint_Eastwood_in_Madison_County.actor());
            expected.add(Model_Meryl_Streep_in_Madison_County.actor());
            expected.add(Model_Robert_De_Niro_in_Intern.actor());
            expected.add(Model_Rene_Russo_in_Intern.actor());
            expected.add(Model_Anne_Hathaway_in_Intern.actor());
        }

        final var movie_reader = new java.io.StringReader(movie_csv);
        final var text_map = Ex2Movie.Extracting.text_map(() -> movie_reader);
        final var actual = Ex2Mapper.Mapping.actors(text_map);

        assertEquals(expected, actual);
    }
}
