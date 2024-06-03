package org.m_tag.jfind.books;

import java.util.Objects;

/**
 * Result record of found books.
 */
public abstract class Book {
  private String author;

  private String title;
  
  private String location;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  protected Book() {
    super();
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

  public String getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, title);
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
