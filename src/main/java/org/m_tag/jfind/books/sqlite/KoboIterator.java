package org.m_tag.jfind.books.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

public class KoboIterator extends SqlIterator  {

  public KoboIterator(String file, Query query) throws ClassNotFoundException, SQLException {
    super(file, query);
  }

  @Override
  protected PreparedStatement prepare(final Connection connection, final Query query)
      throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Book readRecord(ResultSet rs) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

}
