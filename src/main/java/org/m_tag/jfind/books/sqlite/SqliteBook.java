package org.m_tag.jfind.books.sqlite;

import org.m_tag.jfind.books.Book;

public class SqliteBook extends Book {

  public SqliteBook() {
    super();
    // TODO Auto-generated constructor stub
  }

  public SqliteBook(String author, String title) {
    this();
    this.setAuthor(author);
    this.setTitle(title);
  }
}
