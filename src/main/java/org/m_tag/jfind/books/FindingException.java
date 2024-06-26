package org.m_tag.jfind.books;

/**
 * Runtime error in finding.
 *
 * @author mtag@m-tag.org
 *
 */
public class FindingException extends RuntimeException {
  /**
   * serial ID.
   */
  private static final long serialVersionUID = -3560259779639772121L;

  /**
   * constructor.
   *
   * @param message error message
   * @param cause source exeception
   */
  public FindingException(String message, Throwable cause) {
    super(message, cause);
  }
}
