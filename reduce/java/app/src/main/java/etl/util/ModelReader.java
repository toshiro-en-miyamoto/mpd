package etl.util;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


/**
 * ModelReader wraps the CSVParser of Apache Commons CSV library
 * so that application code does not depend on it as much as possible.
 */
public interface ModelReader
{
    /**
     * Return a Stream containing instances of a 'model' record that are filled
     * with values of a CSV file.
     * @param <T>          a 'text' record type corresponding the CSV file
     * @param <I>          an 'model' record type corresponding the CSV file
     * @param supplier     provides a java.io.Reader attached to the CSV file
     * @param text_class   the class instance of the 'text' record type
     * @param text_mapper  a Function that maps a CSVRecord to a 'text' record
     * @param model_mapper a Function that maps a 'text' to an 'model' record
     * @return             a Stream containing 'model' records
     */
    static <T extends Record, I extends Record> Stream<I> stream(
        CloseableSupplier<Reader> supplier,
        final Class<T> text_class,
        final Function<CSVRecord, T> text_mapper,
        final Function<T, I> model_mapper
    ) {
        Stream<I> info_stream = Stream.<I>empty();

        try (
            final var reader = supplier.get();
            final var parser = CSVParser.parse(reader, format(text_class))
        ) {
            info_stream = parser.getRecords().stream()
            .map(text_mapper::apply)
            .map(model_mapper::apply);
        } catch (Exception ex) {
        }

        return info_stream;
    }

    /**
     * Return a Stream containing instances of a 'text' record that are filled
     * with values of a CSV file.
     * @param <T>         a 'text' record type corresponding the CSV file
     * @param supplier    provides a java.io.Reader attached to the CSV file
     * @param text_class  the class instance of the 'text' record type
     * @param text_mapper a Function that maps a CSVRecord to a 'text' record
     * @return            a Stream containing 'text' record instances
     */
    static <T extends Record> Stream<T> stream(
        CloseableSupplier<Reader> supplier,
        final Class<T> text_class,
        final Function<CSVRecord, T> text_mapper
    ) {
        Stream<T> text_stream = Stream.empty();

        try (
            final var reader = supplier.get();
            final var parser = CSVParser.parse(reader, format(text_class))
        ) {
            text_stream = parser.getRecords().stream()
            .map(text_mapper::apply);
        } catch (Exception ex) {
        }

        return text_stream;
    }

    /**
     * Transforms a CSVRecord to a 'text' record.
     * @param <T>  the type of the 'text' record
     * @param csv  the CSVRecord instance to be transformed
     * @param ctor the canincal constructor of the 'text' record type
     * @return     an instance of 'text' record with values in the CSV record
     */
    static <T extends Record> T text(
        final CSVRecord csv,
        final Constructor<T> ctor
    ) {
        var components = csv.stream()
        .<String>map(value -> value.length() == 0 ? null : value)
        .toArray(Object[]::new);

        T instance;
        try {
            instance = ctor.newInstance(components);
        } catch (Exception ex) {
            instance = null;
        }

        return instance;
    }

    /**
     * Returns a CSVFormat for reading CSV files.
     * The format has not only the features defined by {@code format_builder()}
     * but also a list of record component names as the header.
     * @param text_class the class instance of the 'text' record type
     * @return  a CSVFormat for reading CSV files that convey
     *          data of the specified text class
     */
    static CSVFormat format(final Class<? extends Record> text_class)
    {
        final var componentNames = Arrays
        .stream(text_class.getRecordComponents())
        .map(RecordComponent::getName)
        .toArray(String[]::new);

        final var format = format_builder()
        .setHeader(componentNames)
        .build();

        return format;
    }

    /**
     * Returns a CSVFormat for reading CSV files.
     * The format only has the features defined by {@code format_builder()}.
     * @return  a CSVFormat for reading CSV files
     */
    static CSVFormat default_format()
    {
        final var format = format_builder()
        .build()
        ;
        return format;
    }

    /**
     * Returns a CSVFormat.Builder that defines a default set of format
     * features, which
     * <ul>
     * <li>ignore empty lines
     * <li>ignore surrounding whitespaces of each CSV field
     * </ul>
     * @return  a CSVFormat.Builder instance
     */
    static CSVFormat.Builder format_builder()
    {
        final var builder = CSVFormat.Builder
        .create()
        .setIgnoreEmptyLines(false)
        .setIgnoreSurroundingSpaces(true)
        ;
        return builder;
    }
}
