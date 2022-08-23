package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Test;

import etl.util.ModelReader;
import etl.util.TextHelper;

public class Ex2MovieTest
{
    static String csv
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

    @Test
    void iterating_csv_lines()
    {
        final var format = CSVFormat.Builder
        .create()
        .build()
        ;

        List<Map<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>> list = new ArrayList<>();

        try (
            final var parser = CSVParser.parse(csv, format)
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
                            var text_film = ModelReader.text(
                                csv_record,
                                Ex2Movie.Extracting.text_film_ctor
                            );
                            var model_film = Ex2Movie.Extracting.model(text_film);
                            var map = new HashMap<Ex2Movie.Model.Film, List<Ex2Movie.Model.Cast>>();
                            map.put(model_film, new ArrayList<>());
                            list.add(map);
                            break;
                        case CAST:
                        default:
                        }
                    });
                });
            });
        } catch (Exception ex) {}
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
