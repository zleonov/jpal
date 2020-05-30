package software.leonov.common.sql;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * Static utility methods for working with {@link Connection}s.
 * 
 * @author Zhenya Leonov
 */
final public class Connections {

    private Connections() {
    }

    /**
     * Returns a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     * SQL statements to the database.
     *
     * @param conn the database connection
     * @param sql  an SQL statement that may contain one or more '?' parameter placeholders
     * @return a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     *         SQL statements to the database
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    public static FluentStatement prepare(final Connection conn, final String sql) throws SQLException {
        checkNotNull(conn, "conn == null");
        checkNotNull(sql, "sql == null");
        return new FluentStatement(conn.prepareStatement(sql), sql);
    }

    /**
     * Creates a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     * SQL statements to the database.
     *
     * @param conn              the database connection
     * @param sql               an SQL statement that may contain one or more '?' parameter placeholders
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be returned; one of
     *                          {@code Statement.RETURN_GENERATED_KEYS} or {@code Statement.NO_GENERATED_KEYS}
     * @return a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     *         SQL statements to the database
     * @throws SQLException                    if a database access error occurs or this method is called on a closed
     *                                         connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method with a constant of
     *                                         {@code Statement.RETURN_GENERATED_KEYS}
     */
    public static FluentStatement prepare(final Connection conn, final String sql, final int autoGeneratedKeys) throws SQLException {
        checkNotNull(conn, "conn == null");
        checkNotNull(sql, "sql == null");
        return new FluentStatement(conn.prepareStatement(sql, autoGeneratedKeys), sql);
    }

    /**
     * Returns a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     * SQL statements to the database.
     *
     * @param conn          the database connection
     * @param sql           an SQL statement that may contain one or more '?' parameter placeholders
     * @param columnIndexes an array of column indexes indicating the columns that should be returned from the inserted row
     *                      or rows
     * @return a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     *         SQL statements to the database
     * @throws SQLException                    if a database access error occurs or this method is called on a closed
     *                                         connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    public static FluentStatement prepare(final Connection conn, final String sql, final int[] columnIndexes) throws SQLException {
        checkNotNull(conn, "conn == null");
        checkNotNull(sql, "sql == null");
        checkNotNull(columnIndexes, "columnIndexes == null");
        return new FluentStatement(conn.prepareStatement(sql, columnIndexes), sql);
    }

    /**
     * Returns a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     * SQL statements to the database.
     *
     * @param conn        the database connection
     * @param sql         an SQL statement that may contain one or more '?' parameter placeholders
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row or
     *                    rows
     * @return a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     *         SQL statements to the database
     * @throws SQLException                    if a database access error occurs or this method is called on a closed
     *                                         connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     */
    public static FluentStatement prepare(final Connection conn, final String sql, final String[] columnNames) throws SQLException {
        checkNotNull(conn, "conn == null");
        checkNotNull(sql, "sql == null");
        checkNotNull(columnNames, "columnNames == null");
        return new FluentStatement(conn.prepareStatement(sql, columnNames), sql);
    }

    /**
     * Returns a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     * SQL statements to the database.
     *
     * @param conn                 the database connection
     * @param sql                  an SQL statement that may contain one or more '?' parameter placeholders
     * @param resultSetType        a result set type; one of {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @return a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     *         SQL statements to the database
     * @throws SQLException                    if a database access error occurs or this method is called on a closed
     *                                         connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type and result set concurrency
     */
    public static FluentStatement prepare(final Connection conn, final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        checkNotNull(conn, "conn == null");
        checkNotNull(sql, "sql == null");
        return new FluentStatement(conn.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
    }

    /**
     * Returns a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     * SQL statements to the database.
     *
     * @param conn                 the database connection
     * @param sql                  an SQL statement that may contain one or more '?' parameter placeholders
     * @param resultSetType        one of the following {@code ResultSet} constants: {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@code ResultSet} constants: {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@code ResultSet} constants:
     *                             {@code ResultSet.HOLD_CURSORS_OVER_COMMIT} or {@code ResultSet.CLOSE_CURSORS_AT_COMMIT}
     * @return a {@code FluentStatement} backed by an underlying {@code PreparedStatement} object for sending parameterized
     *         SQL statements to the database
     * @throws SQLException                    if a database access error occurs or this method is called on a closed
     *                                         connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method or this method is not
     *                                         supported for the specified result set type, result set holdability and
     *                                         result set concurrency
     */
    public static FluentStatement prepare(final Connection conn, final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        checkNotNull(conn, "conn == null");
        checkNotNull(sql, "sql == null");
        return new FluentStatement(conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
    }

}
