package org.m_tag.jfind.books;

import jakarta.json.JsonValue;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * base class for all finding classes including Config Class to find books with children.
 */
public abstract class Finder {
  protected static String readRequiredJsonValue(JsonValue json, final String key) {
    String id = json.asJsonObject().getString(key);
    if (id == null) {
      throw new IllegalArgumentException(String.format("no %s found:%s", key, json.toString()));
    }
    return id;
  }
  
  private String id;
  private String type;

  /**
   * constructor.
   */
  Finder() {
    this(null, null);
  }
  
  /**
   * constructor.
   */
  protected Finder(String type, String id) {
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
   */
  public abstract Stream<Book> find(final Query query) throws IOException;
  
  protected abstract void toString(StringBuilder builder);
  
  @Override
  public String toString() {
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
