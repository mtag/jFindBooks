package org.m_tag.jfind.books;

import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Result record of found books.
 */
public class Book {
  private static final Pattern pattern =
      Pattern.compile("^[(]([^)]+)[)] *\\[([^\\]]+)\\] *(.*) *([v第]\\d.*)[.](rar|zip|7z|lzh)$",
          Pattern.CASE_INSENSITIVE);
  private String author;

  private String title;

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

  /**
   * constructor.
   *
   * @param path filename
   */
  public Book(final Path path) {
    this(path.toString());
  }

  /**
   * constructor.
   *
   * @param name filename
   */
  public Book(final String name) {
    this();
    Matcher matcher = pattern.matcher(name);
    if (matcher.matches()) {
      this.author = matcher.group(2);
      this.title = matcher.group(3);
    } else {
      this.title = name;
    }
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
