package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    void casts_in_a_movie_are_sorted_by_name()
    {
        final var model1 = Robert_De_Niro_in_Intern;
        final var model2 = Rene_Russo_in_Intern;
        final var model3 = Anne_Hathaway_in_Intern;

        final var expected = List.of(model1, model2, model3);

        // [1] 123
        final Ex2Cast.Model models123[] = {model1, model2, model3};
        final var actual123 = Arrays.asList(models123);
        assertEquals(expected, actual123);

        Collections.sort(actual123);
        assertEquals(expected, actual123);

        // [2] 132
        final Ex2Cast.Model models132[] = {model1, model3, model2};
        final var actual132 = Arrays.asList(models132);
        assertNotEquals(expected, actual132);

        Collections.sort(actual132);
        assertEquals(expected, actual132);

        // [3] 213
        final Ex2Cast.Model models213[] = {model2, model1, model3};
        final var actual213 = Arrays.asList(models213);
        assertNotEquals(expected, actual213);

        Collections.sort(actual213);
        assertEquals(expected, actual213);

        // [4] 231
        final Ex2Cast.Model models231[] = {model2, model3, model1};
        final var actual231 = Arrays.asList(models231);
        assertNotEquals(expected, actual231);

        Collections.sort(actual231);
        assertEquals(expected, actual231);

        // [5] 312
        final Ex2Cast.Model models312[] = {model3, model1, model2};
        final var actual312 = Arrays.asList(models312);
        assertNotEquals(expected, actual312);

        Collections.sort(actual312);
        assertEquals(expected, actual312);

        // [6] 321
        final Ex2Cast.Model models321[] = {model3, model2, model1};
        final var actual321 = Arrays.asList(models321);
        assertNotEquals(expected, actual321);

        Collections.sort(actual321);
        assertEquals(expected, actual321);
    }

    @Test
    void casts_are_sorted_by_film()
    {
        final var model1 = Robert_De_Niro_in_Deer_Hunter;
        final var model2 = Clint_Eastwood_in_Madison_County;
        final var model3 = Robert_De_Niro_in_Intern;

        final var expected = List.of(model1, model2, model3);

        // [1] 123
        final Ex2Cast.Model models123[] = {model1, model2, model3};
        final var actual123 = Arrays.asList(models123);
        assertEquals(expected, actual123);

        Collections.sort(actual123);
        assertEquals(expected, actual123);

        // [2] 132
        final Ex2Cast.Model models132[] = {model1, model3, model2};
        final var actual132 = Arrays.asList(models132);
        assertNotEquals(expected, actual132);

        Collections.sort(actual132);
        assertEquals(expected, actual132);

        // [3] 213
        final Ex2Cast.Model models213[] = {model2, model1, model3};
        final var actual213 = Arrays.asList(models213);
        assertNotEquals(expected, actual213);

        Collections.sort(actual213);
        assertEquals(expected, actual213);

        // [4] 231
        final Ex2Cast.Model models231[] = {model2, model3, model1};
        final var actual231 = Arrays.asList(models231);
        assertNotEquals(expected, actual231);

        Collections.sort(actual231);
        assertEquals(expected, actual231);

        // [5] 312
        final Ex2Cast.Model models312[] = {model3, model1, model2};
        final var actual312 = Arrays.asList(models312);
        assertNotEquals(expected, actual312);

        Collections.sort(actual312);
        assertEquals(expected, actual312);

        // [6] 321
        final Ex2Cast.Model models321[] = {model3, model2, model1};
        final var actual321 = Arrays.asList(models321);
        assertNotEquals(expected, actual321);

        Collections.sort(actual321);
        assertEquals(expected, actual321);
    }

    @Test
    void casts_in_a_film_are_compared_by_actor()
    {
        assertTrue(Robert_De_Niro.compareTo(Meryl_Streep) < 0);
        assertTrue(Robert_De_Niro_in_Deer_Hunter.compareTo(Meryl_Streep_in_Deer_Hunter) < 0);
    }

    @Test
    void casts_are_compared_by_film()
    {
        assertTrue(Deer_Hunter.compareTo(Intern) < 0);
        assertTrue(Robert_De_Niro_in_Deer_Hunter.compareTo(Robert_De_Niro_in_Intern) < 0);
    }

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
