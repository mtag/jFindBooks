package org.m_tag.jfind.books.sqlite;

import jakarta.json.JsonObject;
import java.nio.file.Path;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;

/**
 * finder with calibre db.
 */
public class CalibreFinder extends SqliteFinder {
  /**
   * calibre data folder.
   */
  private final Path directory;

  /**
   * constructor.
   *
   * @param metadata sqlite3 db File Name
   */
  public CalibreFinder(final String directoryName, final String metadata) {
    super(metadata);
    this.directory =  Path.of(directoryName);
  }

  /**
   * constructor.
   *
   * @param directoryName calibre data folder.
   */
  public CalibreFinder(final String directoryName) {
    this(directoryName, directoryName + "/metadata.db");
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder
   * @param object json value from config.
   */
  public CalibreFinder(final String type, final String id, final JsonObject object) {
    this(type, id, Finder.readRequiredJsonValue(object, "metadata"));
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder
   * @param directoryName calibre data folder.
  */
  public CalibreFinder(final String type, final String id, final String directoryName) {
    super(type, id, directoryName + "/metadata.db");
    this.directory =  Path.of(directoryName);
  }

  @Override
  protected void toString(StringBuilder builder) {
    builder.append(",\"metadata\":\"");
    escape(builder, getDbFile());
    builder.append('\"');
  }
  
  @Override
  public SqlIterator iterator(Query query) throws ClassNotFoundException {
    return new CalibreIterator(getDbFile(), query, directory);
  }
}
