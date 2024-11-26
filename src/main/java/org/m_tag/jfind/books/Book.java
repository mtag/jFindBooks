package org.m_tag.jfind.books;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Result record of found books.
 */
public abstract class Book implements Comparable<Book> {
  private String[] authors;

  private String title;
  
  private String location;
  
  private final Long size;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * constructor.
   *
   * @param size byte size of book
   */
  protected Book(Long size) {
    super();
    this.size = size;
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
    return Arrays.equals(authors, other.authors) && Objects.equals(title, other.title);
  }

  public String[] getAuthors() {
    return authors;
  }

  public String getTitle() {
    return title;
  }

  public Long getSize() {
    return size;
  }

  @Override
  public int hashCode() {
    return Objects.hash(authors, title);
  }

  @Override
  public int compareTo(Book o) {
    int result = toString().compareTo(o.toString());
    if (result == 0) {
      result = location.compareTo(o.location);
    }
    return result;
  }

  /**
   * comma not included in parenthesis.
   */
  private static final Pattern COMMA = Pattern.compile("( *[,×;] *)(?!([^,*]+\\)))");
  
  protected void setAuthor(String author) {
    this.setAuthors(COMMA.split(author));
  }
  
  protected void setAuthors(String[] authors) {
    this.authors = authors;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (authors != null  && authors.length > 0) {
      builder.append('[');
      builder.append(String.join("×", authors));
      builder.append("] ");
    }
    builder.append(title);
    return builder.toString();
  }

  protected void setTitle(String title) {
    this.title = title;
  }
}
