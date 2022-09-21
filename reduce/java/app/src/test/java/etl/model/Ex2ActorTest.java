package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Ex2ActorTest
{
    @Test
    void actors_with_same_age_are_sorted_by_name()
    {
        final var model1 = Ex2Actor.Model.instance("Clint Eastwood", Year.of(1930));
        final var model21 = Ex2Actor.Model.instance("Robert De Niro", Year.of(1943));
        final var model22 = Ex2Actor.Model.instance("Robin Williams", Year.of(1943));
        final var model3 = Ex2Actor.Model.instance("Meryl Streep", Year.of(1949));

        final var expected = List.of(model1, model21, model22, model3);

        final Ex2Actor.Model models1[] = {model22, model3, model21, model1};
        final var actual1 = Arrays.asList(models1);
        assertNotEquals(expected, actual1);

        Collections.sort(actual1);
        assertEquals(expected, actual1);


        final Ex2Actor.Model models2[] = {model3, model21, model1, model22};
        final var actual2 = Arrays.asList(models2);
        assertNotEquals(expected, actual2);

        Collections.sort(actual2);
        assertEquals(expected, actual2);
    }

    @Test
    void actors_are_sorted_by_year()
    {
        final var model1 = Ex2Actor.Model.instance("Clint Eastwood", Year.of(1930));
        final var model2 = Ex2Actor.Model.instance("Robert De Niro", Year.of(1943));
        final var model3 = Ex2Actor.Model.instance("Meryl Streep", Year.of(1949));

        final var expected = List.of(model1, model2, model3);

        // [1] 123
        final Ex2Actor.Model models123[] = {model1, model2, model3};
        final var actual123 = Arrays.asList(models123);
        assertEquals(expected, actual123);

        Collections.sort(actual123);
        assertEquals(expected, actual123);

        // [2] 132
        final Ex2Actor.Model models132[] = {model1, model3, model2};
        final var actual132 = Arrays.asList(models132);
        assertNotEquals(expected, actual132);

        Collections.sort(actual132);
        assertEquals(expected, actual132);

        // [3] 213
        final Ex2Actor.Model models213[] = {model2, model1, model3};
        final var actual213 = Arrays.asList(models213);
        assertNotEquals(expected, actual213);

        Collections.sort(actual213);
        assertEquals(expected, actual213);

        // [4] 231
        final Ex2Actor.Model models231[] = {model2, model3, model1};
        final var actual231 = Arrays.asList(models231);
        assertNotEquals(expected, actual231);

        Collections.sort(actual231);
        assertEquals(expected, actual231);

        // [5] 312
        final Ex2Actor.Model models312[] = {model3, model1, model2};
        final var actual312 = Arrays.asList(models312);
        assertNotEquals(expected, actual312);

        Collections.sort(actual312);
        assertEquals(expected, actual312);

        // [6] 321
        final Ex2Actor.Model models321[] = {model3, model2, model1};
        final var actual321 = Arrays.asList(models321);
        assertNotEquals(expected, actual321);

        Collections.sort(actual321);
        assertEquals(expected, actual321);
    }

    @Test
    void actors_with_same_age_are_compared_by_name()
    {
        final var model1 = Ex2Actor.Model.instance("Robert De Niro", Year.of(1943));
        final var model2 = Ex2Actor.Model.instance("Robin Williams", Year.of(1943));

        assertTrue(model1.compareTo(model2) < 0);
    }

    @Test
    void actors_are_compared_by_born_year()
    {
        final var model1 = Ex2Actor.Model.instance("Robert De Niro", Year.of(1943));
        final var model2 = Ex2Actor.Model.instance("Meryl Streep", Year.of(1949));

        assertTrue(model1.compareTo(model2) < 0);
    }

    @Test
    void null_born_is_not_valid()
    {
        final var model = Ex2Actor.Model.instance("Clint Eastwood", null);
        assertNull(model);
    }

    @Test
    void longer_name_is_not_valid()
    {
        final var model = Ex2Actor.Model.instance("123456789012345678901234567890123", Year.of(2033));
        assertNull(model);
    }

    @Test
    void empty_name_is_not_valid()
    {
        final var model = Ex2Actor.Model.instance("", Year.of(2000));
        assertNull(model);
    }

    @Test
    void null_name_is_not_valid()
    {
        final var model = Ex2Actor.Model.instance(null, Year.of(1999));
        assertNull(model);
    }

    @Test
    void max_name_len_is_valid()
    {
        final var model = Ex2Actor.Model.instance("12345678901234567890123456789012", Year.of(2032));
        assertNotNull(model);
    }

    @Test
    void min_name_len_is_valid()
    {
        final var model = Ex2Actor.Model.instance("1", Year.of(2001));
        assertNotNull(model);
    }
}
