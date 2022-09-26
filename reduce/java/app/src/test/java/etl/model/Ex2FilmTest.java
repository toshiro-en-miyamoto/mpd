package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Ex2FilmTest
{
    @Test
    void films_in_same_year_are_sorted_by_name()
    {
        final var model1 = Ex2Film.Model.instance("The Deer Hunter", Year.of(1978));
        final var model2 = Ex2Film.Model.instance("\"Good Morning, Vietnam\"", Year.of(1987));
        final var model31 = Ex2Film.Model.instance("Crimson Tide", Year.of(1995));
        final var model32 = Ex2Film.Model.instance("The Bridges of Madison County", Year.of(1995));
        final var model4 = Ex2Film.Model.instance("The Intern", Year.of(2015));

        final var expected = List.of(model1, model2, model31, model32, model4);

        final Ex2Film.Model models1[] = {model31, model4, model1, model32, model2};
        final var actual1 = Arrays.asList(models1);
        assertNotEquals(expected, actual1);

        Collections.sort(actual1);
        assertEquals(expected, actual1);


        final Ex2Film.Model models2[] = {model4, model1, model32, model2, model31};
        final var actual2 = Arrays.asList(models2);
        assertNotEquals(expected, actual2);

        Collections.sort(actual2);
        assertEquals(expected, actual2);
    }

    @Test
    void films_are_sorted_by_year()
    {
        final var model1 = Ex2Film.Model.instance("The Deer Hunter", Year.of(1978));
        final var model2 = Ex2Film.Model.instance("\"Good Morning, Vietnam\"", Year.of(1987));
        final var model3 = Ex2Film.Model.instance("The Bridges of Madison County", Year.of(1995));

        final var expected = List.of(model1, model2, model3);

        // [1] 123
        final Ex2Film.Model models123[] = {model1, model2, model3};
        final var actual123 = Arrays.asList(models123);
        assertEquals(expected, actual123);

        Collections.sort(actual123);
        assertEquals(expected, actual123);

        // [2] 132
        final Ex2Film.Model models132[] = {model1, model3, model2};
        final var actual132 = Arrays.asList(models132);
        assertNotEquals(expected, actual132);

        Collections.sort(actual132);
        assertEquals(expected, actual132);

        // [3] 213
        final Ex2Film.Model models213[] = {model2, model1, model3};
        final var actual213 = Arrays.asList(models213);
        assertNotEquals(expected, actual213);

        Collections.sort(actual213);
        assertEquals(expected, actual213);

        // [4] 231
        final Ex2Film.Model models231[] = {model2, model3, model1};
        final var actual231 = Arrays.asList(models231);
        assertNotEquals(expected, actual231);

        Collections.sort(actual231);
        assertEquals(expected, actual231);

        // [5] 312
        final Ex2Film.Model models312[] = {model3, model1, model2};
        final var actual312 = Arrays.asList(models312);
        assertNotEquals(expected, actual312);

        Collections.sort(actual312);
        assertEquals(expected, actual312);

        // [6] 321
        final Ex2Film.Model models321[] = {model3, model2, model1};
        final var actual321 = Arrays.asList(models321);
        assertNotEquals(expected, actual321);

        Collections.sort(actual321);
        assertEquals(expected, actual321);
    }

    @Test
    void films_in_same_year_are_compared_by_name()
    {
        final var model1 = Ex2Film.Model.instance("Crimson Tide", Year.of(1995));
        final var model2 = Ex2Film.Model.instance("The Bridges of Madison County", Year.of(1995));

        assertTrue(model1.compareTo(model2) < 0);
    }

    @Test
    void films_are_compared_by_release_year()
    {
        final var model1 = Ex2Film.Model.instance("The Deer Hunter", Year.of(1978));
        final var model2 = Ex2Film.Model.instance("The Bridges of Madison County", Year.of(1995));

        assertTrue(model1.compareTo(model2) < 0);
    }

    @Test
    void null_release_is_not_valid()
    {
        final var model = Ex2Film.Model.instance("The Deer Hunter", null);
        assertNull(model);
    }

    @Test
    void longer_name_is_not_valid()
    {
        final var model = Ex2Film.Model.instance("123456789012345678901234567890123", Year.of(2033));
        assertNotNull(model);
        final var text = Ex2Film.Loading.text(model);
        assertNull(text);
    }

    @Test
    void empty_name_is_not_valid()
    {
        final var model = Ex2Film.Model.instance("", Year.of(2000));
        assertNotNull(model);
        final var text = Ex2Film.Loading.text(model);
        assertNull(text);
    }

    @Test
    void null_name_is_not_valid()
    {
        final var model = Ex2Film.Model.instance(null, Year.of(1999));
        assertNull(model);
    }

    @Test
    void max_name_len_is_valid()
    {
        final var model = Ex2Film.Model.instance("12345678901234567890123456789012", Year.of(2032));
        assertNotNull(model);
        final var text = Ex2Film.Loading.text(model);
        assertNotNull(text);
        assertTrue(text.is_valid());
    }

    @Test
    void min_name_len_is_valid()
    {
        final var model = Ex2Film.Model.instance("1", Year.of(2001));
        assertNotNull(model);
        final var text = Ex2Film.Loading.text(model);
        assertNotNull(text);
        assertTrue(text.is_valid());
    }
}
