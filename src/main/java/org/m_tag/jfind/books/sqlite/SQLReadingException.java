package org.m_tag.jfind.books.sqlite;

import java.sql.SQLException;
import org.m_tag.jfind.ReadingException;

public class SQLReadingException extends ReadingException {

  public SQLReadingException(String message, SQLException cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   */
  private static final long serialVersionUID = 9076932004695335839L;

}
