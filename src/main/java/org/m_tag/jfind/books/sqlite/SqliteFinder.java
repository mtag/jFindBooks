package org.m_tag.jfind.books.sqlite;

import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;

/**
 * Finder for Sqlite3.
 */
public abstract class SqliteFinder extends Finder {
  /**
   * sqlite3 db File Name.
   */
  private final String dbFile;

  /**
   * constructor.
   *
   * @param dbFile sqlite3 db File Name
   */
  protected SqliteFinder(final String dbFile) {
    super();
    this.dbFile = dbFile;
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder 
   * @param dbFile sqlite3 db File Name
   */
  protected SqliteFinder(final String type, final String id, final String dbFile) {
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

  /**
   * getter for sqlite3 db File Name.
   *
   * @return sqlite3 db File Name
   */
  protected String getDbFile() {
    return dbFile;
  }

  public abstract SqlIterator iterator(Query query) throws ClassNotFoundException;
}
