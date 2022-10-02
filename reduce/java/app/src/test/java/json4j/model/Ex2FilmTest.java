package json4j.model;

import java.io.Reader;
import java.time.Year;
import java.util.List;

import json4j.model.Ex2Film.Input;
import json4j.model.Ex2Film.Model;
import json4j.util.CloseableSupplier;

public class Ex2FilmTest
{
    final static String film_csv
    = ""
    + "The Deer Hunter,1978\n"
    + "\"Good Morning, Vietnam\",1987\n"
    + "The Bridges of Madison County,1995\n"
    + "The Intern,2015\n"
    ;
    final CloseableSupplier<Reader> film_reader
    = () -> new java.io.StringReader(film_csv);

    static final String String_Deer_Hunter     = "The Deer Hunter";
    static final String String_Morning_Vietnum = "Good Morning, Vietnam";
    static final String String_Madison_County  = "The Bridges of Madison County";
    static final String String_Intern          = "The Intern";

    static final Input.CSV CSV_Deer_Hunter
    = new Input.CSV(String_Deer_Hunter,     "1978");
    static final Input.CSV CSV_Morning_Vietnum
    = new Input.CSV(String_Morning_Vietnum, "1987");
    static final Input.CSV CSV_Madison_County
    = new Input.CSV(String_Madison_County,  "1995");
    static final Input.CSV CSV_Intern
    = new Input.CSV(String_Intern,          "2015");

    static final List<Input.CSV> expected_csv_list = List.of(
        CSV_Deer_Hunter,
        CSV_Morning_Vietnum,
        CSV_Madison_County,
        CSV_Intern
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
}
