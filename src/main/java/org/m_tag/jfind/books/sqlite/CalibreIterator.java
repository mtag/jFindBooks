package org.m_tag.jfind.books.sqlite;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

public final class CalibreIterator extends SqlIterator {

  private Path directory;

  CalibreIterator(final String file, final Query query, final Path directory) 
      throws ClassNotFoundException {
    super(file, query);
    this.directory = directory;
  }

  @Override
  protected PreparedStatement prepare(final Query query)
      throws SQLException {
    String sql = "select name as author, title, path from books " //$NON-NLS-1$
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
    return prepareAndSetValues(sql, query, author, title);
  }

  @Override
  protected Book readRecord(final ResultSet rs) throws SQLException {
    return new CalibreBook(rs.getString("author"), //$NON-NLS-1$
        rs.getString("title"), //$NON-NLS-1$
        getFile(rs) //$NON-NLS-1$
        );
  }

  private String getFile(final ResultSet rs) throws SQLException {
    return directory.toAbsolutePath().toString() + '/' + rs.getString("path");
  }
}