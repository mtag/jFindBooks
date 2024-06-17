package org.m_tag.jfind.books.sqlite;

import jakarta.json.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;

/**
 * finder with calibre db.
 */
public class CalibreFinder extends SqliteFinder {

  /**
   * constructor.
   *
   * @param metadata sqlite3 db File Name
   */
  public CalibreFinder(final String metadata) {
    super(metadata);
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder
   * @param object json value from config.
   */
  public CalibreFinder(final String type, final String id, final JsonObject object) {
    super(type, id, Finder.readRequiredJsonValue(object, "metadata"));
  }

  @Override
  protected void toString(StringBuilder builder) {
    builder.append(",\"metadata\":\"");
    escape(builder, getDbFile());
    builder.append('\"');
  }
  
  @Override
  public SqlIterator iterator(Query query) throws ClassNotFoundException {
    return new SqlIterator(getDbFile(), query) {
      @Override
      protected PreparedStatement prepare(final Connection connection, final Query query)
          throws SQLException {
        String sql = "select name as author, title  from books " //$NON-NLS-1$
            + " inner join books_authors_link on books.id=books_authors_link.book " //$NON-NLS-1$
            + " inner join authors on books_authors_link.author=authors.id "; //$NON-NLS-1$
        int author = 0;
        int title = 0;
        final boolean hasKeyword = query.getKeyword() != null;
        if (hasKeyword || query.getAuthor() != null) {
          sql += " where name like ? "; //$NON-NLS-1$
          author = 1;
          if (hasKeyword || query.getTitle() != null) {
            sql += " and title like ? "; //$NON-NLS-1$
            title = 2;
          }
        } else if (query.getTitle() != null) {
          sql += " where title like ? "; //$NON-NLS-1$
          title = 1;
        }
        final PreparedStatement prepared = connection.prepareStatement(sql);
        return setValues(prepared, query, author, title);
      }
    };
  }
}
