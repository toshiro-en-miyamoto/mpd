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
    @Test
    void model_map()
    {
        final String movie_csv
        = "1,The Deer Hunter,1978\n"
        + "2,Robert De Niro,Mike,35\n"
        + "2,Meryl Streep,Linda,29\n"
        + "1,\"Good Morning, Vietnam\",1987\n"
        + "2,Robin Williams,Adrian,36\n"
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
            Ex2Film.Model film;

            film = new Ex2Film.Model("The Deer Hunter", Year.of(1978));
            expected.put(
                film,
                List.<Ex2Cast.Model>of(
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Robert De Niro", actors),
                        "Mike"
                    ),
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Meryl Streep", actors),
                        "Linda"
                    )
                )
            );

            film = new Ex2Film.Model("Good Morning, Vietnam", Year.of(1987));
            expected.put(
                film,
                List.<Ex2Cast.Model>of(
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Robin Williams", actors),
                        "Adrian"
                    )
                )
            );
            
            film = new Ex2Film.Model("The Bridges of Madison County", Year.of(1995));
            expected.put(
                film,
                List.<Ex2Cast.Model>of(
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Clint Eastwood", actors),
                        "Robert"
                    ),
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Meryl Streep", actors),
                        "Francesca"
                    )
                )
            );
            
            film = new Ex2Film.Model("The Intern", Year.of(2015));
            expected.put(
                film,
                List.<Ex2Cast.Model>of(
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Robert De Niro", actors),
                        "Ben"
                    ),
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Rene Russo", actors),
                        "Fiona"
                    ),
                    new Ex2Cast.Model(
                        film.id(),
                        Ex2Movie.Extracting
                            .actor_id_by_name("Anne Hathaway", actors),
                        "Jules"
                    )
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
        + "2,Robin Williams,Adrian,36\n"
        + "1,The Bridges of Madison County,1995\n"
        + "2,Clint Eastwood,Robert,57\n"
        + "2,Meryl Streep,Francesca,46\n"
        + "1,The Intern,2015\n"
        + "2,Robert De Niro,Ben,72\n"
        + "2,Rene Russo,Fiona,61\n"
        + "2,Anne Hathaway,Jules,33\n"
        ;

        SortedSet<Ex2Actor.Model> expected = new TreeSet<>();
        {
            expected.add(new Ex2Actor.Model("Robert De Niro", Year.of(1978-35)));
            expected.add(new Ex2Actor.Model("Meryl Streep", Year.of(1978-29)));
            expected.add(new Ex2Actor.Model("Robin Williams", Year.of(1987-36)));
            expected.add(new Ex2Actor.Model("Clint Eastwood", Year.of(1995-57)));
            expected.add(new Ex2Actor.Model("Meryl Streep", Year.of(1995-46)));
            expected.add(new Ex2Actor.Model("Robert De Niro", Year.of(2015-72)));
            expected.add(new Ex2Actor.Model("Rene Russo", Year.of(2015-61)));
            expected.add(new Ex2Actor.Model("Anne Hathaway", Year.of(2015-33)));
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
        + "2,Robin Williams,Adrian,36\n"
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
                    new Ex2Movie.Text.Cast("2","Robin Williams","Adrian","36")
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
