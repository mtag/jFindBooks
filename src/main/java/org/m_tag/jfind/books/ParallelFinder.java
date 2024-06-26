package org.m_tag.jfind.books;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Combined finders for parallel query.
 */
public abstract class ParallelFinder extends Finder {

  static final int NO_LIMIT = Integer.MAX_VALUE;

  protected final Map<String, Finder> finders;

  private final int limit;

  protected ParallelFinder(Map<String, Finder> finders) {
    this(finders, NO_LIMIT);
  }

  protected ParallelFinder(Map<String, Finder> finders, int limit) {
    super();
    this.finders = finders;
    this.limit = limit;
  }

  @Override
  public Stream<Book> find(final Query query)
      throws IOException, ClassNotFoundException, SQLException {
    Iterator<Book> iterator = new ParallelIterator(finders, query, limit);
    Spliterator<Book> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
    return StreamSupport.stream(spliterator, false);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('[');
    boolean isFirst = true;
    for (Map.Entry<String, Finder> entry : finders.entrySet()) {
      String value = entry.getValue().toString();
      if (value == null) {
        return value;
      }
      if (isFirst) {
        isFirst = false;
      } else {
        builder.append(',');
      }
      builder.append(value);
    }
    builder.append(']');
    return builder.toString();
  }

  @Override
  protected void toString(StringBuilder builder) {}

}
