package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import etl.util.Sequencer;

public class Ex2MovieTest
{

    @Test
    void reducing()
    {
        final String csv
        = "1,The Deer Hunter,1978\n"
        + "2,Robert De Niro,Mike,35\n\n"
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
    
        SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>
        expected = new TreeMap<>((f1, f2) -> {
            return Long.compare(f1.seq_film(), f2.seq_film());
        });
    
        {
            final var film_sequence = Sequencer.starting(0);

            expected.put(
                new Ex2Movie.Model.Film(film_sequence.next(),
                    "The Deer Hunter", Year.of(1978)
                ),
                List.<Ex2Movie.Model.Cast>of(
                    new Ex2Movie.Model.Cast("Robert De Niro", "Mike", 35),
                    new Ex2Movie.Model.Cast("Meryl Streep", "Linda", 29)
                )
            );
            expected.put(
                new Ex2Movie.Model.Film(film_sequence.next(),
                    "Good Morning, Vietnam", Year.of(1987)
                ),
                List.<Ex2Movie.Model.Cast>of(
                    new Ex2Movie.Model.Cast("Robin Williams", "Adrian", 36)
                )
            );
            expected.put(
                new Ex2Movie.Model.Film(film_sequence.next(),
                    "The Bridges of Madison County", Year.of(1995)
                ),
                List.<Ex2Movie.Model.Cast>of(
                    new Ex2Movie.Model.Cast("Clint Eastwood", "Robert", 57),
                    new Ex2Movie.Model.Cast("Meryl Streep", "Francesca", 46)
                )
            );
            expected.put(
                new Ex2Movie.Model.Film(film_sequence.next(),
                    "The Intern", Year.of(2015)
                ),
                List.<Ex2Movie.Model.Cast>of(
                    new Ex2Movie.Model.Cast("Robert De Niro", "Ben", 72),
                    new Ex2Movie.Model.Cast("Rene Russo", "Fiona", 61),
                    new Ex2Movie.Model.Cast("Anne Hathaway", "Jules", 33)
                )
            );
        }

        final var actual = Ex2Movie.Extracting.reduce(() -> new java.io.StringReader(csv));
        assertTrue(expected.equals(actual));
    }

    @Test
    void extracting_a_text_film()
    {
        final var csv
        = "1,The Deer Hunter,1978\n"
        ;

        final Ex2Movie.Text.Film[] expected = {
            new Ex2Movie.Text.Film("1", "The Deer Hunter", "1978")
        };

        final Ex2Movie.Text.Film[] actual = Ex2Movie.Extracting.texts_film(
            () -> new java.io.StringReader(csv)
        ).toArray(Ex2Movie.Text.Film[]::new);

        assertArrayEquals(expected, actual);
    }
}
