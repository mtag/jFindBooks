package org.m_tag.jfind.books;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
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
  
  /**
   * get boolean value from JsonObject.
   *
   * @param id id of the Finder
   * @param object json Object for the finder
   * @param fieldName field name to get value
   * @param defaultValue default Value if not exists.
   * @return specified value in json object (or default).
   */
  protected static boolean getBooleanValue(
      final String id, final JsonObject object, final String fieldName, boolean defaultValue) {
    boolean booleanValue;
    if (!object.containsKey(fieldName)) {
      booleanValue = true;
    } else {
      final JsonValue value = object.get(fieldName);
      if (value.getValueType() == ValueType.TRUE) {
        booleanValue = true;
      } else if (value.getValueType() == ValueType.FALSE) {
        booleanValue = false;
      } else {
        throw new IllegalArgumentException(
            String.format(
                "finder id=%s has illegal value for checkExistance:%s",
                id, value.toString()));
      }
    }
    return booleanValue;
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
  protected Finder(final String type, final String id) throws IllegalArgumentException {
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
