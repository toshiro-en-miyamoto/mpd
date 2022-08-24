package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Test;

import etl.util.ModelReader;
import etl.util.TextHelper;

public class Ex2MovieTest
{
    private static String csv
    = "1,The Deer Hunter,1978\n"
    + "2,1,Robert De Niro,Mike,35\n"
    + "2,2,Meryl Streep,Linda,29\n"
    + "1,\"Good Morning, Vietnam\",1987\n"
    + "2,1,Robin Williams,Adrian,36\n"
    + "1,The Bridges of Madison County,1995\n"
    + "2,1,Clint Eastwood,Robert,57\n"
    + "2,2,Meryl Streep,Francesca,46\n"
    + "1,The Intern,2015\n"
    + "2,1,Robert De Niro,Ben,72\n"
    + "2,2,Rene Russo,Fiona,61\n"
    + "2,3,Anne Hathaway,Jules,33\n"
    ;

    private static
    SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>
    expected = new TreeMap<>((f1, f2) -> {
        return Long.compare(f1.seq_film(), f2.seq_film());
    });

    static {
        expected.put(
            new Ex2Movie.Model.Film(0L,
                "The Deer Hunter", Year.of(1978)
            ),
            List.<Ex2Movie.Model.Cast>of(
                new Ex2Movie.Model.Cast(1, "Robert De Niro", "Mike", 35),
                new Ex2Movie.Model.Cast(2, "Meryl Streep", "Linda", 29)
            )
        );
        expected.put(
            new Ex2Movie.Model.Film(1L,
                "Good Morning, Vietnam", Year.of(1987)
            ),
            List.<Ex2Movie.Model.Cast>of(
                new Ex2Movie.Model.Cast(1, "Robin Williams", "Adrian", 36)
            )
        );
        expected.put(
            new Ex2Movie.Model.Film(2L,
                "The Bridges of Madison County", Year.of(1995)
            ),
            List.<Ex2Movie.Model.Cast>of(
                new Ex2Movie.Model.Cast(1, "Clint Eastwood", "Robert", 57),
                new Ex2Movie.Model.Cast(2, "Meryl Streep", "Francesca", 46)
            )
        );
        expected.put(
            new Ex2Movie.Model.Film(3L, "The Intern", Year.of(2015)),
            List.<Ex2Movie.Model.Cast>of(
                new Ex2Movie.Model.Cast(1, "Robert De Niro", "Ben", 72),
                new Ex2Movie.Model.Cast(2, "Rene Russo", "Fiona", 61),
                new Ex2Movie.Model.Cast(3, "Anne Hathaway", "Jules", 33)
            )
        );
    }

    @Test
    void iterating_csv_lines()
    {
        SortedMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>
        map = new TreeMap<>((f1, f2) -> {
            return Long.compare(f1.seq_film(), f2.seq_film());
        });

        try (
            final var parser = CSVParser.parse(csv, CSVFormat.DEFAULT)
        ) {
            parser.iterator().forEachRemaining(csv_record -> {
                TextHelper.parse(
                    csv_record.get(0),
                    Integer::parseInt
                )
                .ifPresent(record_type -> {
                    Ex2Movie.RecordType.of(record_type)
                    .ifPresent(constant -> {
                        switch (constant) {
                        case FILM:
                            var model_film = Ex2Movie.Extracting.model(
                                ModelReader.text(
                                    csv_record,
                                    Ex2Movie.Extracting.text_film_ctor
                                )
                            );
                            map.put(model_film, new ArrayList<>());
                            break;
                        case CAST:
                            var model_cast = Ex2Movie.Extracting.model(
                                ModelReader.text(
                                    csv_record,
                                    Ex2Movie.Extracting.text_cast_ctor
                                )
                            );
                            map.get(map.lastKey()).add(model_cast);
                            break;
                        default:
                        }
                    });
                });
            });
        } catch (Exception ex) {}

        assertEquals(expected, map);
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
