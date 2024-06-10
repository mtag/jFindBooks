package org.m_tag.jfind.books.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.m_tag.jfind.books.Query;

/**
 * Find book from calibre db.
 */
public class CalibreIterator extends SqliteAbstractIterator {


  /**
   * find books in Calibre db file.
   *
   * @param file database file
   * @param query query
   * @throws ClassNotFoundException Cannot load JDBC driver
   * @throws SQLException query error
   */
  public CalibreIterator(final String file, final Query query)
      throws ClassNotFoundException, SQLException {
    super(file, query);
  }


  @Override
  protected PreparedStatement prepare(final Connection connection, final Query query)
      throws SQLException {
    String sql = "select name as author, title  from books " //$NON-NLS-1$
        + " inner join books_authors_link on books.id=books_authors_link.book " //$NON-NLS-1$
        + " inner join authors on books_authors_link.author=authors.id "; //$NON-NLS-1$
    int author = 0;
    int title = 0;
    if (query.getAuthor() != null) {
      sql += " where name like ? "; //$NON-NLS-1$
      author = 1;
      if (query.getTitle() != null) {
        sql += " and title like ? "; //$NON-NLS-1$
        title = 2;
      }
    } else if (query.getTitle() != null) {
      sql += " where title like ? "; //$NON-NLS-1$
      title = 1;
    }
    final PreparedStatement prepared = connection.prepareStatement(sql);
    if (author != 0) {
      prepared.setString(author, '%' + query.getAuthor() + '%');
    }
    if (title != 0) {
      prepared.setString(title, '%' + query.getTitle() + '%');
    }
    return prepared;
  }
}
