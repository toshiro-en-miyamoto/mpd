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

    interface Reading
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

        static final ObjectReader csv_reader = new CsvMapper()
        .readerFor(CSV.class)
        .with(
            a()
            // CsvSchema.builder()
            // .addColumn("name")
            // .addColumn("release")
            // .build()
        );

        static CsvSchema a()
        {
            return
            Arrays.stream(CSV.class.getRecordComponents())
            .map(RecordComponent::getName)
            .reduce(
                CsvSchema.builder(),
                (builder, name) -> builder.addColumn(name),
                (a, b) -> a
            )
            .build();
        }

        static Model model(final CSV text)
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
