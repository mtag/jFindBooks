package org.m_tag.jfind.books.sqlite;

import org.m_tag.jfind.books.Book;

/**
 * Found books from sqlite3 db.
 */
public abstract class SqliteBook extends Book {
  /**
   * constructor.
   *
   * @param author author of book
   * @param title title of book
   * @param size byte size of book
   */
  public SqliteBook(String author, String title, Long size) {
    super(size);
    this.setAuthor(author);
    this.setTitle(title);
  }
}
