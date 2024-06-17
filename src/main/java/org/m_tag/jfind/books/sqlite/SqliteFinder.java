package org.m_tag.jfind.books.sqlite;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;

public abstract class SqliteFinder extends Finder {
  private final String dbFile;

  protected SqliteFinder(final String dbFile) {
    super();
    this.dbFile = dbFile;
  }

  protected SqliteFinder(String type, String id, String dbFile) {
    super(type, id);
    this.dbFile = dbFile;
  }

  @Override
  public Stream<Book> find(Query query) throws ClassNotFoundException {
    iterator(query);
    try (final SqlIterator iterator = iterator(query)) {
      return iterator.stream();
    }
  }

  protected String getDbFile() {
    return dbFile;
  }

  public abstract SqlIterator iterator(Query query) throws ClassNotFoundException;

}
