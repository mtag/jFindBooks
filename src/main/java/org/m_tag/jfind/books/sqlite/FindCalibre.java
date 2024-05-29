package org.m_tag.jfind.books.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

/**
 * Find book from calibre db.
 */
public class FindCalibre extends FindSqlite {

  public FindCalibre(final String file, final Query query)
      throws ClassNotFoundException, SQLException {
    super(file, query);
  }


  @Override
  protected PreparedStatement prepare(Connection connection, Query query) throws SQLException {
    String sql = "select name as author, title  from books "
        + " inner join books_authors_link on books.id=books_authors_link.book "
        + " inner join authors on books_authors_link.author=authors.id ";
    int author = 0;
    int title = 0;
    if (query.getAuthor() != null) {
      sql += " where name like ? ";
      author = 1;
      if (query.getTitle() != null) {
        sql += " and title like ? ";
        title = 2;
      }
    } else  if (query.getTitle() != null) {
      sql += " where title like ? ";
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

  @Override
  protected Book readRecord(final ResultSet rs) throws SQLException {
    final Book book = new Book();
    book.setAuthor(rs.getString("author"));
    book.setTitle(rs.getString("title"));
    return book;
  }

}
