package org.m_tag.jfind.books.sqlite;

import jakarta.json.JsonObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;

/**
 * finder with calibre db.
 */
public class CalibreFinder extends Finder {
  private final String metadata;

  public CalibreFinder(String metadata) {
    super();
    this.metadata = metadata;
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder
   * @param object json value from config.
   */
  public CalibreFinder(final String type, final String id, final JsonObject object) {
    super(type, id);
    this.metadata = Finder.readRequiredJsonValue(object, "metadata");
  }

  @Override
  public Stream<Book> find(Query query) throws IOException, ClassNotFoundException, SQLException {
    try (final CalibreIterator iterator = new CalibreIterator(metadata, query)) {
      return iterator.stream();
    }
  }

  @Override
  protected void toString(StringBuilder builder) {
    builder.append(",\"metadata\":\"");
    escape(builder, metadata);
    builder.append('\"');
  }
}
