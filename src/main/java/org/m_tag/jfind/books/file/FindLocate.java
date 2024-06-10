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
    super();
    this.db = db;
  }

  /**
   * constructor.
   *
   * @param json json value from config.
   */
  public FindLocate(JsonValue json) {
    this(createDbFile(json));
  }

  private static DbFile createDbFile(JsonValue json) {
    final String dbFile = readRequiredJsonValue(json, "file");
    final JsonArray replaces = json.asJsonObject().get("replaces").asJsonArray();
    final String[][] array = new String[replaces.size()][];
    for (int i = 0; i < array.length; i++) {
      final JsonValue item = replaces.get(i);
      final String from = readRequiredJsonValue(item, "from");
      final String to = readRequiredJsonValue(item, "to");
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
}
