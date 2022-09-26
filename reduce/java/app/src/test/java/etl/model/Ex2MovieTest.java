package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class Ex2MovieTest
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

    static final String String_Deer_Hunter     = "The Deer Hunter";
    static final String String_Morning_Vietnum = "Good Morning, Vietnam";
    static final String String_Madison_County  = "The Bridges of Madison County";
    static final String String_Intern          = "The Intern";

    static final String code_film = Ex2Movie.RecordType.CODE_FILM;
    static final String code_cast = Ex2Movie.RecordType.CODE_CAST;

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
    void model_map()
    {
        final var movie_reader = new java.io.StringReader(movie_csv);
        final var text_map = Ex2Movie.Extracting.text_map(() -> movie_reader);
        final var actors = Ex2Movie.Extracting.actors(text_map);

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

        final var actual = Ex2Movie.Extracting.model_map(text_map, actors);

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
        final var actual = Ex2Movie.Extracting.actors(text_map);

        assertEquals(expected, actual);
    }

    @Test
    void text_map()
    {
        SortedMap<Ex2Movie.Text.Film, List<Ex2Movie.Text.Cast>>
        expected = new TreeMap<>();

        {
            expected.put(
                Text_Deer_Hunter,
                List.<Ex2Movie.Text.Cast>of(
                    Text_Robert_De_Niro_in_Deer_Hunter,
                    Text_Meryl_Streep_in_Deer_Hunter
                )
            );
            expected.put(
                Text_Morning_Vietnum,
                List.<Ex2Movie.Text.Cast>of(
                    Text_Robin_Williams_in_Morning_Vietnum
                )
            );
            expected.put(
                Text_Madison_County,
                List.<Ex2Movie.Text.Cast>of(
                    Text_Clint_Eastwood_in_Madison_County,
                    Text_Meryl_Streep_in_Madison_County
                )
            );
            expected.put(
                Text_Intern,
                List.<Ex2Movie.Text.Cast>of(
                    Text_Robert_De_Niro_in_Intern,
                    Text_Rene_Russo_in_Intern,
                    Text_Anne_Hathaway_in_Intern
                )
            );
        }

        final var movie_reader = new java.io.StringReader(movie_csv);
        final var actual = Ex2Movie.Extracting.text_map(() -> movie_reader);

        assertEquals(expected, actual);
    }

    @Test
    void films_in_same_year_are_compared_by_name()
    {
        final var text1 = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "Crimson Tide", "1995"
        );
        final var text2 = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "The Bridges of Madison County", "1995"
        );

        assertTrue(text1.compareTo(text2) < 0);
    }

    @Test
    void films_are_compared_by_release_year()
    {
        final var text1 = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "The Deer Hunter", "1978"
        );
        final var text2 = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "The Bridges of Madison County", "1995"
        );

        assertTrue(text1.compareTo(text2) < 0);
    }

    @Test
    void invalid_actor_age()
    {
        var text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike",
            null
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike",
            ""
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike",
            "1"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike",
            "999"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike",
            "99_"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike",
            "1000"
        );
        assertFalse(text.is_valid());
    }

    @Test
    void invalid_cast_role_name()
    {
        var text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro,
            null,
            "35"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro,
            "",
            "35"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro,
            "1",
            "35"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro,
            "12345678901234567890123456789012",
            "35"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro,
            "123456789012345678901234567890123",
            "35"
        );
        assertFalse(text.is_valid());
    }

    @Test
    void invalid_cast_actor_name()
    {
        var text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            null,
            "Mike", "35"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            "",
            "Mike", "35"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            "1",
            "Mike", "35"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            "12345678901234567890123456789012",
            "Mike", "35"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            "123456789012345678901234567890123",
            "Mike", "35"
        );
        assertFalse(text.is_valid());
    }

    @Test
    void valid_cast()
    {
        var text = new Ex2Movie.Text.Cast(Ex2Movie.RecordType.CODE_CAST,
            String_Robert_De_Niro, "Mike", "35"
        );
        assertTrue(text.is_valid());
    }

    @Test
    void invalid_film_releases()
    {
        var text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter,
            null
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter,
            ""
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter,
            "978"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter,
            "1978"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter,
            "197_"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter,
            "19781"
        );
        assertFalse(text.is_valid());
    }

    @Test
    void invalid_film_names()
    {
        var text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            null,
            "1978"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "",
            "1978"
        );
        assertFalse(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "1",
            "1978"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "12345678901234567890123456789012",
            "1978"
        );
        assertTrue(text.is_valid());

        text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            "123456789012345678901234567890123",
            "1978"
        );
        assertFalse(text.is_valid());
    }

    @Test
    void valid_film()
    {
        var text = new Ex2Movie.Text.Film(Ex2Movie.RecordType.CODE_FILM,
            String_Deer_Hunter, "1978"
        );
        assertTrue(text.is_valid());
    }

    @Test
    void invalid_record_type_codes()
    {
        final String CODE_NULL = null;
        final var const_null = Ex2Movie.RecordType.of(CODE_NULL);
        assertTrue(const_null.isEmpty());

        final String CODE_EMPTY = "";
        final var const_empty = Ex2Movie.RecordType.of(CODE_EMPTY);
        assertTrue(const_empty.isEmpty());

        final String CODE_THREE = "3";
        final var const_three = Ex2Movie.RecordType.of(CODE_THREE);
        assertTrue(const_three.isEmpty());
    }

    @Test
    void valid_record_type_codes()
    {
        final var code_film = Ex2Movie.RecordType.CODE_FILM;
        final var const_film = Ex2Movie.RecordType.of(code_film);
        const_film.ifPresentOrElse(
            constant -> {
                assertEquals(Ex2Movie.RecordType.Constant.FILM, constant);
                assertEquals(code_film, constant.code());
            }, () -> fail()
        );

        final var code_cast = Ex2Movie.RecordType.CODE_CAST;
        final var const_cast = Ex2Movie.RecordType.of(code_cast);
        const_cast.ifPresentOrElse(
            constant -> {
                assertEquals(Ex2Movie.RecordType.Constant.CAST, constant);
                assertEquals(code_cast, constant.code());
            }, () -> fail()
        );
    }
}
