package etl.util;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

/**
 * ModelWriter wraps the CSVPrinter of Apache Commons CSV library
 * so that application code does not depend on it as much as possible.
 */
public class ModelWriter implements AutoCloseable
{
    /**
     * Takes a 'model' record and prints it to java.io.Writer this
     * ModelWriter wraps. The mapper argument is to transform each
     * component of the 'model' record to a string of type Object.
     * A valid 'model' record results in one CSV line followed by a new line
     * character. A 'model' record that is either null or one the mapper
     * failed to map yields one blank line (i.e., only a new line character).
     * Upon calling accept(), a valid Report structure gets available
     * through getReport() method.
     * @param <T>    the type of the 'model' record
     * @param model  an instance of the 'model' record
     * @param mapper a function that transforms each component to String
     */
    public <T extends Record> void accept(
        final T model,
        final Function<T, Object[]> mapper
    ) {
        total_lines++;

        try {
            if (model == null) {
                invalid_lines++;
            } else {
                for (var value : mapper.apply(model)) {
                    printer.print(value);
                }
            }
            printer.println();
        } catch (IOException ex) {
            invalid_lines++;
        }
    }

    /**
     * Returns a Report structure containing the total amount of
     * 'text' records passed to an ModelWriter instance and
     * the number of records that the mapper argument failed to
     * transform.
     * @return the Report structure
     */
    public Report report() {
        return new Report(total_lines, invalid_lines);
    }

    /**
     * Report is a statistics holder that accept() records.
     */
    public static record Report(
        long total_lines,
        long invalid_lines
    ) {}

    /**
     * Creates an ModelWriter wrapping an CSVPrinter.
     * @param supplier   privdes a java.io.Writer to pint
     * @throws Exception if this CSVPrinter constructor or the Writer throws
     */
    public ModelWriter(
        final CloseableSupplier<Writer> supplier
    )
        throws Exception
    {
        final var format = CSVFormat.Builder
        .create()
        .setTrim(true)
        .setRecordSeparator('\n')
        .setQuoteMode(QuoteMode.MINIMAL)
        .setAutoFlush(true)
        .build();

        printer = new CSVPrinter(supplier.get(), format);
    }

    /**
     * Closes the CSVPrinter this ModelWriter wrapps.
     */
    @Override
    public void close() throws Exception {
        printer.close();
    }
    
    private long total_lines = 0L;
    private long invalid_lines = 0L;
    private final CSVPrinter printer;
}
