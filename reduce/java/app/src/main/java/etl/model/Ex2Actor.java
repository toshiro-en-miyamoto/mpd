package etl.model;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;

import etl.util.CloseableSupplier;
import etl.util.IntRange;
import etl.util.ModelReader;
import etl.util.TextHelper;

/**
 * Ex2Actor represents the Actor entity.
 */
public interface Ex2Actor
{
    /**
     * Ex2Actor Text record
     */
    record Text
    (
        String id,
        String name,
        String born,
        String died
    ) {}

    /**
     * Ex2Actor model record
     */
    record Model
    (
        String id,
        String name,
        LocalDate born,
        Year died
    ) {
        /**
         * The valid length range of {@code id}.
         */
        public static final IntRange VALID_LENGTH_RANGE_id
        = IntRange.lower(8).upper(8);

        /**
         * The valid length range of {@code name}.
         */
        public static final IntRange VALID_LENGTH_RANGE_name
        = IntRange.lower(1).upper(32);

        /**
         * Returns {@code died} as an {@code Optional<Year>}.
         * @return
         */
        public Optional<Year> optional_died()
        {
            return Optional.ofNullable(died);
        }

        /**
         * Validates the model record.
         * @return {@code true} if the model record is valid
         */
        boolean isValid()
        {
            boolean validity
            =  id != null
            && VALID_LENGTH_RANGE_id.covers(id.length())

            && name != null
            && VALID_LENGTH_RANGE_name.covers(name.length())

            && born != null

            // the died field is optional
            ;
            return validity;
        }
    }

    /**
     * Extracting provides methods for reading Text and
     * Model reocrds from CSV files.
     */
    interface Extracting
    {
        static Ex2Actor.Model model(final Ex2Actor.Text text)
        {
            // An invalid Text yeilds an invalid Model
            if (text == null) return null;

            // the id field
            final var id_length_exclusive
            = Model.VALID_LENGTH_RANGE_id.upper() + 1;

            final var id
            = text.id == null ? null
            : text.id.length() < id_length_exclusive
            ? text.id
            : text.id.substring(0, id_length_exclusive)
            ;

            // the name field
            final var name_length_exclusive
            = Model.VALID_LENGTH_RANGE_name.upper() + 1;

            final var name
            = text.name == null ? null
            : text.name.length() < name_length_exclusive
            ? text.name
            : text.name.substring(0, name_length_exclusive)
            ;

            // the born field
            final var born = TextHelper.<LocalDate>parse(
                text.born,
                date -> LocalDate.parse(
                    date,
                    DateTimeFormatter.ISO_LOCAL_DATE
                )
            )
            .orElse(null);

            // the died field
            final var died = TextHelper.<Year>parse(
                text.died,
                year -> Year.parse(year)
            )
            .orElse(null);

            // mapping Text to Model
            final Model model = new Model(
                id,
                name,
                born,
                died
            );

            // a null represents an invalid Model
            return model.isValid() ? model : null;
        }

        /**
         * Transforms the CSVRecord to the Text record.
         * @param csv a CSVRecord instance
         * @return    a Text record
         */
        static Ex2Actor.Text text(final CSVRecord csv)
        {
            final var text = ModelReader.text(csv, text_ctor);
            return text;
        }

        /**
         * The canonical constrctor of this Text record type.
         */
        static final Constructor<Ex2Actor.Text> text_ctor
        = TextHelper.ctor(Ex2Actor.Text.class);

        /**
         * Returns a Stream containing Model records extracted out of the
         * CSV file the supplier argument is attached to.
         * @param supplier a Reader attached to a CSV file
         * @return         a Stream containing Model records
         */
        static Stream<Model> models(
            final CloseableSupplier<Reader> supplier
        ) {
            final var models = ModelReader.stream(
                supplier,
                Ex2Actor.Text.class,
                Ex2Actor.Extracting::text,
                Ex2Actor.Extracting::model
            );
            return models;
        }
    }
}
