package etl.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Sha1 provides utility methods for SHA-1 (Secure Hash Algorithm 1).
 */
public class Sha1
{
    private static final MessageDigest instance = instance();
    private static final HexFormat hex_formatter = HexFormat.of();

    private static MessageDigest instance()
    {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    /**
     * Computes the 20-byte hash using the {@code string} argument, and
     * returns a 40-character hexadecimal {@code String} representing
     * the hash.
     * @param string a String to be digested
     * @return       a String representing the digest in hex
     */
    public static String hex_string(final String string)
    {
        var bytes = string.getBytes();
        var digest = instance.digest(bytes);
        var hex_string = hex_formatter.formatHex(digest);
        return hex_string;
    }
}
