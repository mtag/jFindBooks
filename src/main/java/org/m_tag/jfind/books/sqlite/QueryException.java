package org.m_tag.jfind.books.sqlite;

import java.sql.SQLException;
import org.m_tag.jfind.ReadingException;

/**
 * Errors on query.
 */
public class QueryException extends ReadingException {

  /**
   * generated serial.
   */
  private static final long serialVersionUID = 9076932004695335839L;

  /**
   * Constructor.
   *
   * @param message message
   * @param cause errors on query.
   */
  public QueryException(String message, SQLException cause) {
    super(message, cause);
  }
}
