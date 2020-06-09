package software.leonov.common.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Static utility methods for working with {@link ResultSet}s.
 * 
 * @author Zhenya Leonov
 */
public final class ResultSets {

    private ResultSets() {
    }

    /**
     * Closes the specified {@code ResultSet} if it is not {@code null}. If an {@code SQLException} is thrown it will be
     * {@link Throwable#addSuppressed(Throwable) appended} to the previously thrown exception.
     * <p>
     * This method is primarily useful when cleaning up resources in a try block because of an exception that occurred
     * earlier.
     * 
     * @param rs the specified {@code ResultSet}
     * @param e  the exception thrown earlier
     */
    public static void close(final ResultSet rs, final Exception e) {
        try {
            if (rs != null)
                rs.close();
        } catch (final SQLException ex) {
            e.addSuppressed(ex);
        }
    }

}
