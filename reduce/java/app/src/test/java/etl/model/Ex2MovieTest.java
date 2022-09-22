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
    static final Ex2Film.Model Deer_Hunter
    = Ex2Film.Model.instance("The Deer Hunter",               Year.of(1978));
    static final Ex2Film.Model Morning_Vietnum
    = Ex2Film.Model.instance("\"Good Morning, Vietnam\"",     Year.of(1987));
    static final Ex2Film.Model Madison_County
    = Ex2Film.Model.instance("The Bridges of Madison County", Year.of(1995));
    static final Ex2Film.Model Intern
    = Ex2Film.Model.instance("The Intern",                    Year.of(2015));

    static final Ex2Actor.Model Clint_Eastwood
    = Ex2Actor.Model.instance("Clint Eastwood", Year.of(1930));
    static final Ex2Actor.Model Robert_De_Niro
    = Ex2Actor.Model.instance("Robert De Niro", Year.of(1943));
    static final Ex2Actor.Model Robin_Williams
    = Ex2Actor.Model.instance("Robin Williams", Year.of(1943));
    static final Ex2Actor.Model Meryl_Streep
    = Ex2Actor.Model.instance("Meryl Streep",   Year.of(1949));
    static final Ex2Actor.Model Rene_Russo
    = Ex2Actor.Model.instance("Rene Russo",     Year.of(1954));
    static final Ex2Actor.Model Anne_Hathaway
    = Ex2Actor.Model.instance("Anne Hathaway",  Year.of(1982));

    static final Ex2Cast.Model Robert_De_Niro_in_Deer_Hunter
    = Ex2Cast.Model.instance(Deer_Hunter, Robert_De_Niro, "Mike");
    static final Ex2Cast.Model Meryl_Streep_in_Deer_Hunter
    = Ex2Cast.Model.instance(Deer_Hunter, Meryl_Streep, "Linda");
    static final Ex2Cast.Model Robin_Williams_in_Morning_Vietnum
    = Ex2Cast.Model.instance(Morning_Vietnum, Robin_Williams, "Adrian");
    static final Ex2Cast.Model Clint_Eastwood_in_Madison_County
    = Ex2Cast.Model.instance(Madison_County, Clint_Eastwood, "Robert");
    static final Ex2Cast.Model Meryl_Streep_in_Madison_County
    = Ex2Cast.Model.instance(Madison_County, Meryl_Streep, "Francesca");
    static final Ex2Cast.Model Robert_De_Niro_in_Intern
    = Ex2Cast.Model.instance(Intern, Robert_De_Niro, "Ben");
    static final Ex2Cast.Model Rene_Russo_in_Intern
    = Ex2Cast.Model.instance(Intern, Rene_Russo, "Fiona");
    static final Ex2Cast.Model Anne_Hathaway_in_Intern
    = Ex2Cast.Model.instance(Intern, Anne_Hathaway, "Jules");

    @Test
    void model_map()
    {
        final String movie_csv
        = "1,The Deer Hunter,1978\n"
        + "2,Robert De Niro,Mike,35\n"
        + "2,Meryl Streep,Linda,29\n"
        + "1,\"Good Morning, Vietnam\",1987\n"
        + "2,Robin Williams,Adrian,44\n"
        + "1,The Bridges of Madison County,1995\n"
        + "2,Clint Eastwood,Robert,57\n"
        + "2,Meryl Streep,Francesca,46\n"
        + "1,The Intern,2015\n"
        + "2,Robert De Niro,Ben,72\n"
        + "2,Rene Russo,Fiona,61\n"
        + "2,Anne Hathaway,Jules,33\n"
        ;

        final var movie_reader = new java.io.StringReader(movie_csv);
        final var text_map = Ex2Movie.Extracting.text_map(() -> movie_reader);
        final var actors = Ex2Movie.Extracting.actors(text_map);

        SortedMap<Ex2Film.Model, List<Ex2Cast.Model>> expected
        = new TreeMap<Ex2Film.Model, List<Ex2Cast.Model>>();

        {
            expected.put(
                Deer_Hunter,
                List.<Ex2Cast.Model>of(
                    Robert_De_Niro_in_Deer_Hunter,
                    Meryl_Streep_in_Deer_Hunter
                )
            );
            expected.put(
                Morning_Vietnum,
                List.<Ex2Cast.Model>of(
                    Robin_Williams_in_Morning_Vietnum
                )
            );
            expected.put(
                Madison_County,
                List.<Ex2Cast.Model>of(
                    Clint_Eastwood_in_Madison_County,
                    Meryl_Streep_in_Madison_County
                )
            );
            expected.put(
                Intern,
                List.<Ex2Cast.Model>of(
                    Robert_De_Niro_in_Intern,
                    Rene_Russo_in_Intern,
                    Anne_Hathaway_in_Intern
                )
            );
        }

        final var actual = Ex2Movie.Extracting.model_map(text_map, actors);

        assertEquals(expected, actual);
    }

    @Test
    void actors()
    {
        final String movie_csv
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

        SortedSet<Ex2Actor.Model> expected = new TreeSet<>();
        {
            expected.add(Robert_De_Niro_in_Deer_Hunter.actor());
            expected.add(Meryl_Streep_in_Deer_Hunter.actor());
            expected.add(Robin_Williams_in_Morning_Vietnum.actor());
            expected.add(Clint_Eastwood_in_Madison_County.actor());
            expected.add(Meryl_Streep_in_Madison_County.actor());
            expected.add(Robert_De_Niro_in_Intern.actor());
            expected.add(Rene_Russo_in_Intern.actor());
            expected.add(Anne_Hathaway_in_Intern.actor());
        }

        final var movie_reader = new java.io.StringReader(movie_csv);
        final var text_map = Ex2Movie.Extracting.text_map(() -> movie_reader);
        final var actual = Ex2Movie.Extracting.actors(text_map);

        assertEquals(expected, actual);
    }

    @Test
    void text_map()
    {
        final String movie_csv
        = "1,The Deer Hunter,1978\n"
        + "2,Robert De Niro,Mike,35\n"
        + "2,Meryl Streep,Linda,29\n"
        + "1,\"Good Morning, Vietnam\",1987\n"
        + "2,Robin Williams,Adrian,44\n"
        + "1,The Bridges of Madison County,1995\n"
        + "2,Clint Eastwood,Robert,57\n"
        + "2,Meryl Streep,Francesca,46\n"
        + "1,The Intern,2015\n"
        + "2,Robert De Niro,Ben,72\n"
        + "2,Rene Russo,Fiona,61\n"
        + "2,Anne Hathaway,Jules,33\n"
        ;

        SortedMap<Ex2Movie.Text.Film, List<Ex2Movie.Text.Cast>>
        expected = new TreeMap<>();

        {
            expected.put(
                new Ex2Movie.Text.Film("1","The Deer Hunter", "1978"),
                List.<Ex2Movie.Text.Cast>of(
                    new Ex2Movie.Text.Cast("2","Robert De Niro","Mike","35"),
                    new Ex2Movie.Text.Cast("2","Meryl Streep","Linda","29")
                )
            );
            expected.put(
                new Ex2Movie.Text.Film("1","Good Morning, Vietnam", "1987"),
                List.<Ex2Movie.Text.Cast>of(
                    new Ex2Movie.Text.Cast("2","Robin Williams","Adrian","44")
                )
            );
            expected.put(
                new Ex2Movie.Text.Film("1","The Bridges of Madison County","1995"),
                List.<Ex2Movie.Text.Cast>of(
                    new Ex2Movie.Text.Cast("2","Clint Eastwood","Robert","57"),
                    new Ex2Movie.Text.Cast("2","Meryl Streep","Francesca","46")
                )
            );
            expected.put(
                new Ex2Movie.Text.Film("1","The Intern","2015"),
                List.<Ex2Movie.Text.Cast>of(
                    new Ex2Movie.Text.Cast("2","Robert De Niro","Ben","72"),
                    new Ex2Movie.Text.Cast("2","Rene Russo","Fiona","61"),
                    new Ex2Movie.Text.Cast("2","Anne Hathaway","Jules","33")
                )
            );
        }

        final var movie_reader = new java.io.StringReader(movie_csv);
        final var actual = Ex2Movie.Extracting.text_map(() -> movie_reader);

        assertEquals(expected, actual);
    }
}
