package software.leonov.common.sql;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * A {@code FluentStatement} is a {@link PreparedStatement} which provides <i>setXXXX</i> methods which automatically
 * increment the parameter index after each call and return {@code this} instance for users who prefer
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface" target="_blank">fluency</a> as a style.
 * <p>
 * When the {@code FluentStatement} is created the parameter index is set to 1. It can be arbitrarily changed by calling
 * {@link #setParameterIndex(int)}. Calls to {@link #addBatch()}, {@link #clearBatch()}, and {@link #executeBatch()}
 * will reset the parameter index to 1.
 * <p>
 * Instances of this class can be obtained by calling the various {@link Connections#prepare(Connection, String)
 * Connections.prepare} methods.
 * <p>
 * Additional functionality:
 * <ul>
 * <li>The ability to register a callback function via the {@link #onAddBatch(Runnable)} method which will be executed
 * after each call to {@code addBatch}.</li>
 * <li>The ability to retrieve the current batch size since the last call to {@link #executeBatch()} via the
 * {@link #batchSize()} method.</li>
 * <li>The ability to retrieve the the original SQL used to create this {@code FluentStatement} via {@link #sql()}.</li>
 * </ul>
 * <p>
 * <b>Note:</b> While some driver implementations allow users to call {@link Statement#executeQuery(String)},
 * {@link Statement#executeUpdate(String)}, {@link Statement#execute(String)}, {@link Statement#addBatch(String)},
 * {@link Statement#executeUpdate(String, int)}, {@link Statement#executeUpdate(String, int[])},
 * {@link Statement#executeQuery(String, String[])}, {@link Statement#execute(String, int)},
 * {@link Statement#execute(String, int[])}, {@link Statement#execute(String, String[])},
 * {@link Statement#execute(String)} reserved specifically for {@link Statement} implementations, this class strictly
 * adheres to the API specification which states that these <i>methods cannot be called on a
 * {@code PreparedStatement}s</i>. Calling said methods will result in {@code SQLException}s.
 * 
 * @author Zhenya Leonov
 */
public final class FluentStatement extends ForwardingPreparedStatement {

    final private PreparedStatement statement;
    final private String sql;
    private Runnable callback = null;
    private int batchSz = 0;
    private int parameterIndex = 1;

    FluentStatement(final PreparedStatement statement, final String sql) {
        this.statement = statement;
        this.sql = sql;
    }

    @Override
    protected PreparedStatement delegate() {
        return statement;
    }

    /**
     * Returns the sql that was used to create this {@code FluentStatement} or {@code null}.
     * 
     * @return the sql that was used to create this {@code FluentStatement} or {@code null}
     */
    public String sql() {
        return sql;
    }

    /**
     * Returns the current batch size.
     * 
     * @return the current batch size
     */
    public int batchSize() {
        return batchSz;
    }

    @Override
    public void addBatch() throws SQLException {
        delegate().addBatch();
        parameterIndex = 1;
        batchSz++;
        if (callback != null)
            callback.run();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        final int[] ints = delegate().executeBatch();
        parameterIndex = 1;
        batchSz = 0;
        return ints;
    }

    @Override
    public void clearBatch() throws SQLException {
        delegate().clearBatch();
        parameterIndex = 1;
        batchSz = 0;
    }

    /**
     * Registers a callback function which will be invoked every time {@link #addBatch()} or {@link #addBatch(String)} is
     * called.
     * 
     * @param callback the callback function
     * @return this {@code FluentStatement} instance
     */
    public FluentStatement onAddBatch(final Runnable callback) {
        checkNotNull(callback, "callback == null");
        this.callback = callback;
        return this;
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public boolean execute(String sql) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public int executeUpdate(String sql, String columnNames[]) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");

    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public boolean execute(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    /**
     * <b>Note:</b> This method cannot be called on a PreparedStatement.
     * 
     * @throws SQLException always
     */
    @Override
    public boolean execute(String sql, String columnNames[]) throws SQLException {
        throw new SQLException("this method cannot be called on a PreparedStatement");
    }

    public FluentStatement set(Array x) throws SQLException {
        delegate().setArray(parameterIndex++, x);
        return this;
    }

    public FluentStatement setAsciiStream(InputStream x) throws SQLException {
        delegate().setAsciiStream(parameterIndex++, x);
        return this;
    }

    public FluentStatement setAsciiStream(InputStream x, int length) throws SQLException {
        delegate().setAsciiStream(parameterIndex++, x, length);
        return this;
    }

    public FluentStatement setAsciiStream(InputStream x, long length) throws SQLException {
        delegate().setAsciiStream(parameterIndex++, x, length);
        return this;
    }

    public FluentStatement set(BigDecimal x) throws SQLException {
        delegate().setBigDecimal(parameterIndex++, x);
        return this;
    }

    public FluentStatement setBinStream(InputStream x) throws SQLException {
        delegate().setBinaryStream(parameterIndex++, x);
        return this;
    }

    public FluentStatement setBinStream(InputStream x, int length) throws SQLException {
        delegate().setBinaryStream(parameterIndex++, x, length);
        return this;
    }

    public FluentStatement setBinStream(InputStream x, long length) throws SQLException {
        delegate().setBinaryStream(parameterIndex++, x, length);
        return this;
    }

    public FluentStatement set(Blob x) throws SQLException {
        delegate().setBlob(parameterIndex++, x);
        return this;
    }

    public FluentStatement setBlob(InputStream inputStream) throws SQLException {
        delegate().setBlob(parameterIndex++, inputStream);
        return this;
    }

    public FluentStatement setBlob(InputStream inputStream, long length) throws SQLException {
        delegate().setBlob(parameterIndex++, inputStream, length);
        return this;
    }

    public FluentStatement set(boolean x) throws SQLException {
        delegate().setBoolean(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(byte x) throws SQLException {
        delegate().setByte(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(byte[] x) throws SQLException {
        delegate().setBytes(parameterIndex++, x);
        return this;
    }

    public FluentStatement setCharStream(Reader reader) throws SQLException {
        delegate().setCharacterStream(parameterIndex++, reader);
        return this;
    }

    public FluentStatement setCharStream(Reader reader, int length) throws SQLException {
        delegate().setCharacterStream(parameterIndex++, reader, length);
        return this;
    }

    public FluentStatement setCharStream(Reader reader, long length) throws SQLException {
        delegate().setCharacterStream(parameterIndex++, reader, length);
        return this;
    }

    public FluentStatement set(Clob x) throws SQLException {
        delegate().setClob(parameterIndex++, x);
        return this;
    }

    public FluentStatement setClob(Reader reader) throws SQLException {
        delegate().setClob(parameterIndex++, reader);
        return this;
    }

    public FluentStatement setClob(Reader reader, long length) throws SQLException {
        delegate().setClob(parameterIndex++, reader, length);
        return this;
    }

    public FluentStatement set(Date x) throws SQLException {
        delegate().setDate(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(Date x, Calendar cal) throws SQLException {
        delegate().setDate(parameterIndex++, x, cal);
        return this;
    }

    public FluentStatement set(double x) throws SQLException {
        delegate().setDouble(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(float x) throws SQLException {
        delegate().setFloat(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(int x) throws SQLException {
        delegate().setInt(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(long x) throws SQLException {
        delegate().setLong(parameterIndex++, x);
        return this;
    }

    public FluentStatement setNCharacterStream(Reader value) throws SQLException {
        delegate().setNCharacterStream(parameterIndex++, value);
        return this;
    }

    public FluentStatement setNCharacterStream(Reader value, long length) throws SQLException {
        delegate().setNCharacterStream(parameterIndex++, value, length);
        return this;
    }

    public FluentStatement setNClob(NClob value) throws SQLException {
        delegate().setNClob(parameterIndex++, value);
        return this;
    }

    public FluentStatement setNClob(Reader reader) throws SQLException {
        delegate().setNClob(parameterIndex++, reader);
        return this;
    }

    public FluentStatement setNClob(Reader reader, long length) throws SQLException {
        delegate().setNClob(parameterIndex++, reader, length);
        return this;
    }

    public FluentStatement setNString(String value) throws SQLException {
        delegate().setNString(parameterIndex++, value);
        return this;
    }

    public FluentStatement setNull(int sqlType) throws SQLException {
        delegate().setNull(parameterIndex++, sqlType);
        return this;
    }

    public FluentStatement setNull(int sqlType, String typeName) throws SQLException {
        delegate().setNull(parameterIndex++, sqlType, typeName);
        return this;
    }

    public FluentStatement set(Object x) throws SQLException {
        delegate().setObject(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(Object x, int targetSqlType) throws SQLException {
        delegate().setObject(parameterIndex++, x, targetSqlType);
        return this;
    }

    public FluentStatement set(Object x, SQLType targetSqlType) throws SQLException {
        delegate().setObject(parameterIndex++, x, targetSqlType);
        return this;
    }

    public FluentStatement set(Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        delegate().setObject(parameterIndex++, x, targetSqlType, scaleOrLength);
        return this;
    }

    public FluentStatement set(Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        delegate().setObject(parameterIndex++, x, targetSqlType, scaleOrLength);
        return this;
    }

    public FluentStatement set(Ref x) throws SQLException {
        delegate().setRef(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(RowId x) throws SQLException {
        delegate().setRowId(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(SQLXML xmlObject) throws SQLException {
        delegate().setSQLXML(parameterIndex++, xmlObject);
        return this;
    }

    public FluentStatement set(short x) throws SQLException {
        delegate().setShort(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(String x) throws SQLException {
        delegate().setString(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(Time x) throws SQLException {
        delegate().setTime(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(Time x, Calendar cal) throws SQLException {
        delegate().setTime(parameterIndex++, x, cal);
        return this;
    }

    public FluentStatement set(Timestamp x) throws SQLException {
        delegate().setTimestamp(parameterIndex++, x);
        return this;
    }

    public FluentStatement set(Timestamp x, Calendar cal) throws SQLException {
        delegate().setTimestamp(parameterIndex++, x, cal);
        return this;
    }

    public FluentStatement set(URL x) throws SQLException {
        delegate().setURL(parameterIndex++, x);
        return this;
    }

    public FluentStatement setParameterIndex(final int parameterIndex) {
        checkArgument(parameterIndex > 0, "parameterIndex <= 0");
        this.parameterIndex = parameterIndex;
        return this;
    }

    @Deprecated
    public FluentStatement setUnicodeStream(InputStream x, int length) throws SQLException {
        delegate().setUnicodeStream(parameterIndex++, x, length);
        return this;
    }

    @Override
    public String toString() {
        return "delegate().toString(): " + super.toString();
    }

}
