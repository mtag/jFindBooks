package org.m_tag.jfind.books;

import java.nio.file.Path;

/**
 * query for finding books.
 */
public class Query {
  private String author;
  private String title;
  private boolean exists = true;
  private String replaceFrom;
  private String replaceTo;
  
  public void setReplacement(String from, String to) {
    this.replaceFrom = from;
    this.replaceTo = to;
  }

  public String getReplaceFrom() {
    return replaceFrom;
  }

  public String getReplaceTo() {
    return replaceTo;
  }

  public boolean isExists() {
    return exists;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
  }

  public String getAuthor() {
    return author;
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
  
  /**
   * matching found path and query.
   *
   * @param path found path
   * @return matched or not
   */
  public boolean matches(final Path path) {
    String name = path.getFileName().toString();
    return (author != null && name.contains(author))
        || (title != null && name.contains(title));
  }
}
