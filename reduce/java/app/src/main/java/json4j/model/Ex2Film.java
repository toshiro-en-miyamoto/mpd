package json4j.model;

import java.time.Year;

import jackson.util.IntRange;
import jackson.util.TextHelper;

public interface Ex2Film
{
    record Model
    (
        String name,
        Year release
    ) {
        boolean is_valid()
        {
            boolean validity
            =  name != null
            && release != null
            ;
            return validity;
        }
    }

    interface Input
    {
        record CSV
        (
            String name,
            String release
        ) {
            static final IntRange valid_length_NAME
            = IntRange.builder().lower(1).upper(32).build();
            static final IntRange valid_length_RELEASE
            = IntRange.builder().lower_upper(4).build();

            boolean is_valid()
            {
                boolean validity
                =  valid_length_NAME.covers(name.length())
                && valid_length_RELEASE.covers(release.length())
                ;
                return validity;
            }
        }
    }

    interface Reading
    {
        static Input.CSV csv(final String line)
        {
            return null;
        }

        static Model model(final Input.CSV text)
        {
            if (text == null) return null;

            final var release
            = TextHelper.<Year>parse(text.release, Year::parse);

            final var model
            = new Model(text.name, release.orElse(null));

            return model.is_valid()
            ? model : null;
        }
    }
}
