package org.m_tag.jfind.books.sqlite;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

/**
 * Find books from sqlite3 db.
 */
public abstract class SqlIterator implements Iterator<Book>, Closeable  {
  private Book book = null;
  private  Connection connection;
  private  ResultSet resultSet;

  private  PreparedStatement statement;
  private Stream<Book> stream = null;
  private Query query;
  private final String url;

  /**
   * find books in SQLite3 db file.
   *
   * @param file database file
   * @param query query
   * @throws ClassNotFoundException Cannot load JDBC driver
   * @throws SQLException query error
   */
  protected SqlIterator(final String file, final Query query)
      throws ClassNotFoundException {
    super();
    Class.forName("org.sqlite.JDBC");
    this.url = "jdbc:sqlite:" + file;
    this.book = null;
    this.query = query;
  }

  @Override
  public void close()  {
    if (stream != null) {
      return;
    }
    try {
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      if (statement != null) {
        statement.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  protected abstract PreparedStatement prepare(Connection connection, Query query)
      throws SQLException;

  @Override
  public boolean hasNext() {
    try {
      if (book != null) {
        return true;
      }
      if (this.connection == null) {
        this.connection = DriverManager.getConnection(url);
        this.statement = prepare(connection, query);
        this.resultSet = statement.executeQuery();
      }
      if (!resultSet.next()) {
        return false;
      }
      this.book = readRecord(resultSet);
      return true;
    } catch (SQLException ex) {
      throw new QueryException("Error in reading record", ex);
    }
  }

  @Override
  public Book next() {
    if (this.book != null || hasNext()) {
      Book value = this.book;
      this.book = null;
      return value;
    }
    throw new NoSuchElementException();
  }


  /**
   * create stream for listed files.
   *
   * @return stream for listed files.
   */
  public Stream<Book> stream() {
    if (stream != null) {
      throw new UnsupportedOperationException("stream() is called twice");
    }
    final Spliterator<Book> spliterator =
        Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED | Spliterator.NONNULL);
    stream = StreamSupport.stream(spliterator, false).onClose(() -> {
      this.stream = null;
      this.close();
    });
    return stream;
  }

  /**
   * read resultSet and create book from the record.
   *
   * @param rs resultSet
   * @return book based on the read record.
   * @throws SQLException errors in reading field.
   */
  protected Book readRecord(final ResultSet rs) throws SQLException {
    return new SqliteBook(
        rs.getString("author"), //$NON-NLS-1$
        rs.getString("title")); //$NON-NLS-1$
  }
}
