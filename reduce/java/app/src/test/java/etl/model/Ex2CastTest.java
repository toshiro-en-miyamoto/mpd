package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;

import org.junit.jupiter.api.Test;

public class Ex2CastTest
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


    static final String movie_csv
    = ""
    + "1,The Deer Hunter,1978\n"
    + "2,Robert De Niro,Mike,35\n\n"
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
    
    @Test
    void longer_role_name_is_not_valid()
    {
        final var model = Ex2Cast.Model.instance(Deer_Hunter, Robert_De_Niro,
            "123456789012345678901234567890123"
        );
        assertNull(model);
    }

    @Test
    void empty_role_name_is_not_valid()
    {
        final var model = Ex2Cast.Model.instance(Deer_Hunter, Robert_De_Niro,
            ""
        );
        assertNull(model);
    }

    @Test
    void null_role_name_is_not_valid()
    {
        final var model = Ex2Cast.Model.instance(Deer_Hunter, Robert_De_Niro,
            null
        );
        assertNull(model);
    }

    @Test
    void max_role_name_len_is_valid()
    {
        final var model = Ex2Cast.Model.instance(Deer_Hunter, Robert_De_Niro,
            "12345678901234567890123456789012"
        );
        assertNotNull(model);
    }

    @Test
    void min_role_name_len_is_valid()
    {
        final var model = Ex2Cast.Model.instance(Deer_Hunter, Robert_De_Niro,
            "1"
        );
        assertNotNull(model);
    }
}
