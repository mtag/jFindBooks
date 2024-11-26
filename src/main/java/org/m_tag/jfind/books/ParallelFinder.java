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

  private final int timeLimit;
  
  private final int maxCount;

  protected ParallelFinder(Map<String, Finder> finders) {
    this(finders, NO_LIMIT, NO_LIMIT);
  }

  protected ParallelFinder(Map<String, Finder> finders, int timeLimit, int maxCount) {
    super();
    this.finders = finders;
    this.timeLimit = timeLimit;
    this.maxCount = maxCount;
  }

  @Override
  public Stream<Book> find(final Query query)
      throws IOException, SQLException {
    int max;
    if (query.getMaxCount() == NO_LIMIT) {
      max = this.maxCount;
    } else if (this.maxCount == NO_LIMIT) {
      max = query.getMaxCount();
    } else {
      max = Math.min(this.maxCount, query.getMaxCount());
    }
    Iterator<Book> iterator = new ParallelIterator(finders, query, timeLimit, max);
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
