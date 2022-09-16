package etl.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class Ex2MapperTest
{
    static final String movie_csv
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

    static final String film_csv
    = "e59ef957,The Deer Hunter,1978\n"
    + "e26705b0,\"Good Morning, Vietnam\",1987\n"
    + "615df8f4,The Bridges of Madison County,1995\n"
    + "095fe76f,The Intern,2015\n"
    ;

    static final String actor_csv
    = "bf6edc9b,Anne Hathaway,1982-11-12,\n"
    + "b18c86f9,Clint Eastwood,1930-05-31,\n"
    + "1d1aac52,Meryl Streep,1949-06-22,\n"
    + "b018008a,Rene Russo,1954-02-17,\n"
    + "089dac8a,Robert De Niro,1943-08-17,\n"
    + "86c9cdb3,Robin Williams,1951-07-21,2014\n"
    ;

    static final String cast_csv
    // The Intern
    // Robert De Niro, Ben
    // Anne Hathaway, Jules
    // Rene Russo, Fiona
    = "095fe76f,e59ef957,Ben\n"
    + "095fe76f,bf6edc9b,Jules\n"
    + "095fe76f,b018008a,Fiona\n"
    // The Bridges of Madison County
    // Clint Eastwood, Robert
    // Meryl Streep, Francesca
    + "615df8f4,b18c86f9,Robert\n"
    + "615df8f4,1d1aac52,Francesca\n"
    // Good Morning, Vietnam
    // Robin Williams, Adrian
    + "e26705b0,86c9cdb3,Adrian\n"
    // The Deer Hunter
    // Robert De Niro, Mike
    // Meryl Streep, Linda
    + "e59ef957,089dac8a,Mike\n"
    + "e59ef957,1d1aac52,Linda\n"
    ;

    @Test
    void driving()
    {
        final var writer = new StringWriter();
        Ex2Mapper.Driving.job(
            () -> new java.io.StringReader(movie_csv),
            () -> new java.io.StringReader(film_csv),
            () -> new java.io.StringReader(actor_csv),
            () -> writer
        );

        final var actual = writer.toString();
        assertEquals(cast_csv, actual);
    }
}
