package org.m_tag.jfind.books.file;

import static org.m_tag.jfind.utils.FilterMethods.exists;

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.locate.DbFile;

/**
 * Find books from locate db.
 */
public class FindLocate extends Finder {
  

  /**
   * db file.
   */
  private final DbFile db;

  /**
   * constructor.
   *
   * @param db locate db
   */
  public FindLocate(DbFile db) {
    super("", "");
    this.db = db;
  }

  /**
   * constructor.
   *
   * @param json json value from config.
   */
  public FindLocate(final String type, final String id, final JsonValue json) {
    super(type, id);
    this.db = createDbFile(json);
  }

  private static DbFile createDbFile(final JsonValue json) {
    final String dbFile = Finder.readRequiredJsonValue(json, "file");
    final JsonArray replaces = json.asJsonObject().get("replaces").asJsonArray();
    final String[][] array = new String[replaces.size()][];
    for (int i = 0; i < array.length; i++) {
      final JsonValue item = replaces.get(i);
      final String from = Finder.readRequiredJsonValue(item, "from");
      final String to = Finder.readRequiredJsonValue(item, "to");
      array[i] = new String[] {from, to};
    }
    final File file = new File(dbFile);
    if (!file.exists()) {
      throw new IllegalArgumentException(String.format("locate db file %s is not exist.", dbFile));
    }
    return new DbFile(file, array);
  }

  /**
   * find books from db.
   *
   * @param query query
   * @return stream of found books
   * @throws IOException error on opening db file.
   */
  public Stream<Book> find(final Query query) throws IOException {
    Stream<Path> matched = db.stream().filter(query::matches);
    if (query.isExists()) {
      matched = matched.filter(path -> exists(path));
    }
    return matched.map(BookFile::new);
  }

  @Override
  protected void toString(StringBuilder builder) {
    builder.append(",\"file\":\"");
    escape(builder, db.getPath().toString());
    builder.append('\"');
    String[][] replacements = db.getReplacements();
    if (replacements != null && replacements.length > 0) {
      builder.append(",\"replaces\":[");
      boolean isFirst = true;
      for (String[] replacement : replacements) {
        if (isFirst) {
          isFirst = false;
        } else {
          builder.append(',');
        }
        builder.append("{\"from\":\"");
        builder.append(replacement[0]);
        builder.append("\",\"to\":\"");
        escape(builder, replacement[1]);
        builder.append("\"}");
      }
      builder.append("]");
    }
  }
}
