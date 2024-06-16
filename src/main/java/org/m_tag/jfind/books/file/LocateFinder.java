package org.m_tag.jfind.books.file;

import static org.m_tag.jfind.utils.FilterMethods.exists;

import jakarta.json.JsonObject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.locate.DbFile;

/**
 * Find books from locate db.
 */
public class LocateFinder extends Finder {
  

  /**
   * db file.
   */
  private final DbFile db;

  /**
   * constructor.
   *
   * @param db locate db
   */
  public LocateFinder(DbFile db) {
    super("", "");
    this.db = db;
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder 
   * @param object json value from config.
   */
  public LocateFinder(final String type, final String id, final JsonObject object) {
    super(type, id);
    this.db = newDbFile(object);
  }

  private static DbFile newDbFile(final JsonObject json) {
    final String dbFile = Finder.readRequiredJsonValue(json, "file");
    final Path path = Path.of(dbFile);
    if (!path.toFile().exists()) {
      throw new IllegalArgumentException(String.format("locate db file %s is not exist.", dbFile));
    }
    
    Charset charset;
    if (!json.containsKey("charset")) {
      charset = StandardCharsets.UTF_8;
    } else {
      charset = Charset.forName(json.getString("charset"));
    }
    return new DbFile(path, charset);
  }

  /**
   * find books from db.
   *
   * @param query query
   * @return stream of found books
   * @throws IOException error on opening db file.
   */
  public Stream<Book> find(final Query query) throws IOException {
    final Stream<Path> stream;
    final String from = query.getReplaceFrom();
    final String to = query.getReplaceTo();
    if (from != null && to != null) {
      stream = db.stream(new String[] {from, to});
    } else {
      stream = db.stream();
    }
    Stream<Path> matched = stream.filter(query::matches);
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
  }
}
