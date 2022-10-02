package jackson.util;

/**
 * CloseableSupplier provides a functional interface that
 * follows the protocol of Supplier. The resource this supplier-like
 * interface supplies is an AutoCloseable that may throw an Exception
 * so that the resouce can be used in Try Resource blocks.
 */
@FunctionalInterface
public interface CloseableSupplier<T extends AutoCloseable>
{
    /**
     * Returns an AutoCloseable instance that may throw an Exception.
     * @return an AutoCloseable
     * @throws Exception if the resource get() returns throws
     */
    T get() throws Exception;
}

