package org.m_tag.jfind.books;

import java.util.Objects;

/**
 * Result record of found books.
 */
public class Book {
  private String author;
  private String title;

  public String getAuthor() {
    return author;
  }

  public Book() {
    super();
  }

  /**
   * constructor.
   *
   * @param author author
   * @param title title
   */
  public Book(final String author, final String title) {
    this();
    this.author = author;
    this.title = title;
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, title);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Book other = (Book) obj;
    return Objects.equals(author, other.author) && Objects.equals(title, other.title);
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
