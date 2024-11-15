package org.m_tag.jfind.books;

import jakarta.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * base class for all finding classes including Config Class to find books with children.
 */
public abstract class Finder {
  protected static String readRequiredJsonValue(JsonObject json, final String key) {
    String id = json.getString(key);
    if (id == null) {
      throw new IllegalArgumentException(
          String.format("no reqruired attribute %s found:%s", key, json.toString()));
    }
    return id;
  }
  
  private String id;
  private String type;

  /**
   * constructor.
   */
  protected Finder() {
    this(null, null);
  }
  
  /**
   * constructor.
   */
  protected Finder(final String type, final String id) {
    super();
    this.type = type;
    this.id = id;
  }
  
  /**
   * find method.
   *
   * @param query search criteria
   * @return found books
   * @throws IOException Error in reading file or db.
   * @throws SQLException Error in select from rdb.
   * @throws ClassNotFoundException failed to load JDBC driver.
   */
  public abstract Stream<Book> find(final Query query)
      throws IOException, ClassNotFoundException, SQLException;
  
  protected abstract void toString(StringBuilder builder);
  
  @Override
  public String toString() {
    if (id == null || type == null) {
      return super.toString();
    }
    StringBuilder builder = new StringBuilder();
    builder.append('{');
    builder.append("\"id\":\"");
    builder.append(id);
    builder.append("\",\"type\":\"");
    builder.append(type);
    builder.append('\"');
    toString(builder);
    builder.append('}');
    return builder.toString();
  }

  protected void escape(StringBuilder builder, final String pathString) {
    if (File.separatorChar == '\\') {
      builder.append(pathString.replace('\\', '/'));
    } else {
      builder.append(pathString);
    }
  }
}
