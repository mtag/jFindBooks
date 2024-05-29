package org.m_tag.jfind.books.sqlite;

import java.io.Closeable;
import java.io.IOException;
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
import org.m_tag.jfind.ReadingException;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

/**
 * Find books from sqlite3 db.
 */
public abstract class FindSqlite implements Iterator<Book>, Closeable {
  private Book book = null;
  private final Connection connection;
  private final ResultSet resultSet;

  private final PreparedStatement statement;

  protected FindSqlite(final String file, final Query query)
      throws ClassNotFoundException, SQLException {
    super();
    Class.forName("org.sqlite.JDBC");
    final String url = "jdbc:sqlite:" + file;
    this.connection = DriverManager.getConnection(url);
    this.statement = prepare(connection, query);
    this.resultSet = statement.executeQuery();
    this.book = null;
  }

  @Override
  public void close() throws IOException {
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
      if (!resultSet.next()) {
        return false;
      }
      this.book = readRecord(resultSet);
      return true;
    } catch (SQLException ex) {
      throw new ReadingException("Error in reading record", ex);
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

  protected abstract Book readRecord(final ResultSet rs) throws SQLException;

  /**
   * create stream for listed files.
   *
   * @return stream for listed files.
   */
  public Stream<Book> stream() {
    final Spliterator<Book> spliterator =
        Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED | Spliterator.NONNULL);
    return StreamSupport.stream(spliterator, false);
  }
}
