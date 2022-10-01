package rmw.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import rmw.model.Ex2Film.Model;
import rmw.model.Ex2Film.Reading;

public class Ex2FilmTest
{
    final static String film_csv
    = "The Deer Hunter,1978\n"
    + "\"Good Morning, Vietnam\",1987\n"
    + "The Bridges of Madison County,1995\n"
    + "The Intern,2015\n"
    ;

    static final String String_Deer_Hunter     = "The Deer Hunter";
    static final String String_Morning_Vietnum = "Good Morning, Vietnam";
    static final String String_Madison_County  = "The Bridges of Madison County";
    static final String String_Intern          = "The Intern";

    static final Reading.CSV Text_Deer_Hunter
    = new Reading.CSV(String_Deer_Hunter,     "1978");
    static final Reading.CSV Text_Morning_Vietnum
    = new Reading.CSV(String_Morning_Vietnum, "1987");
    static final Reading.CSV Text_Madison_County
    = new Reading.CSV(String_Madison_County,  "1995");
    static final Reading.CSV Text_Intern
    = new Reading.CSV(String_Intern,          "2015");

    static final List<Reading.CSV> expected_text_list = List.of(
        Text_Deer_Hunter,
        Text_Morning_Vietnum,
        Text_Madison_County,
        Text_Intern
    );

    static final Model Model_Deer_Hunter
    = new Model(String_Deer_Hunter,     Year.of(1978));
    static final Model Model_Morning_Vietnum
    = new Model(String_Morning_Vietnum, Year.of(1987));
    static final Model Model_Madison_County
    = new Model(String_Madison_County,  Year.of(1995));
    static final Model Model_Intern
    = new Model(String_Intern,          Year.of(2015));

    static final List<Model> expected_model_list = List.of(
        Model_Deer_Hunter,
        Model_Morning_Vietnum,
        Model_Madison_County,
        Model_Intern
    );

    @Test
    void hello_world()
    {

        try {
            final var actual_text_list
            = Reading.csv_reader.<Reading.CSV>readValues(film_csv)
            .readAll().stream()
            .map(csv -> csv.is_valie() ? csv : null)
            .collect(Collectors.toList())
            ;
            assertEquals(expected_text_list, actual_text_list);

            final var actual_model_list
            = Reading.csv_reader.<Reading.CSV>readValues(film_csv)
            .readAll().stream()
            .map(Reading::model)
            .collect(Collectors.toList())
            ;
            assertEquals(expected_model_list, actual_model_list);
        } catch (Exception ex) {
            fail();
        }
    }
}
