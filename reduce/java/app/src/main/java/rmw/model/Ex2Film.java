package rmw.model;

import java.lang.reflect.RecordComponent;
import java.time.Year;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import rmw.util.IntRange;
import rmw.util.TextHelper;

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

            boolean is_valie()
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

        static final ObjectReader csv_in = new CsvMapper()
        .readerFor(Input.CSV.class)
        .with(
            CsvSchema.builder()
            .addColumns(
                () -> Arrays
                    .stream(Input.CSV.class.getRecordComponents())
                    .map(RecordComponent::getName)
                    .iterator(),
                CsvSchema.ColumnType.STRING
            )
            .build()
        );

        static Input.CSV csv(final String line)
        {
            try {
                final var csv = csv_in.<Input.CSV>readValue(line);
                return csv;
            } catch (Exception ex) {
                return null;
            }
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
