package etl.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class Ex2CastTest
{
    @Test
    void longer_role_name_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "1234567890123456789012345678901234567890",
            "123456789012345678901234567890123"
        );
        assertFalse(model.isValid());
    }

    @Test
    void empty_role_name_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "1234567890123456789012345678901234567890",
            ""
        );
        assertFalse(model.isValid());
    }

    @Test
    void null_role_name_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "1234567890123456789012345678901234567890",
            null
        );
        assertFalse(model.isValid());
    }

    @Test
    void max_role_name_len_is_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "1234567890123456789012345678901234567890",
            "12345678901234567890123456789012"
        );
        assertTrue(model.isValid());
    }

    @Test
    void min_role_name_len_is_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "1234567890123456789012345678901234567890",
            "1"
        );
        assertTrue(model.isValid());
    }

    @Test
    void longer_actor_id_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "12345678901234567890123456789012345678901",
            "12345678901234567890123456789012"
        );
        assertFalse(model.isValid());
    }

    @Test
    void shorter_actor_id_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            "123456789012345678901234567890123456789",
            "12345678901234567890123456789012"
        );
        assertFalse(model.isValid());
    }

    @Test
    void null_actor_id_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "1234567890123456789012345678901234567890",
            null,
            "12345678901234567890123456789012"
        );
        assertFalse(model.isValid());
    }

    @Test
    void longer_filmd_id_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "12345678901234567890123456789012345678901",
            "1234567890123456789012345678901234567890",
            "12345678901234567890123456789012"
        );
        assertFalse(model.isValid());
    }

    @Test
    void shorter_film_id_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            "123456789012345678901234567890123456789",
            "1234567890123456789012345678901234567890",
            "12345678901234567890123456789012"
        );
        assertFalse(model.isValid());
    }

    @Test
    void null_film_id_is_not_valid()
    {
        final var model = new Ex2Cast.Model(
            null,
            "1234567890123456789012345678901234567890",
            "12345678901234567890123456789012"
        );
        assertFalse(model.isValid());
    }
}
