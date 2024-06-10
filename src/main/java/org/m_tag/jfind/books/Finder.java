package org.m_tag.jfind.books;

import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * base class for all finding classes including Config Class to find books with children.
 */
public abstract class Finder {
  /**
   * constructor.
   */
  protected Finder() {
    super();
  }

  /**
   * find method.
   *
   * @param query search criteria
   * @return found books
   * @throws IOException Error in reading file or db.
   */
  public abstract Stream<Book> find(final Query query) throws IOException;

  protected static String readRequiredJsonValue(JsonValue json, final String key) {
    String id = json.asJsonObject().getString(key);
    if (id == null) {
      throw new IllegalArgumentException(String.format("no %s found:%s", key, json.toString()));
    }
    return id;
  }
}
