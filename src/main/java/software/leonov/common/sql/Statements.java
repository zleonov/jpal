package software.leonov.common.sql;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Static utility methods for working with {@link Statement}s.
 * 
 * @author Zhenya Leonov
 */
final public class Statements {

    private Statements() {
    }

    /**
     * Closes the specified {@code Statement} if it is not {@code null}. If an {@code SQLException} is thrown it will be
     * {@link Throwable#addSuppressed(Throwable) appended} to the previously thrown exception.
     * <p>
     * This method is primarily useful when cleaning up resources in a try block because of an exception that occurred
     * earlier.
     * 
     * @param stmt the specified {@code Statement}
     * @param e    the exception thrown earlier
     */
    public static void close(final Statement stmt, final Exception e) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (final SQLException ex) {
            e.addSuppressed(ex);
        }
    }

}
