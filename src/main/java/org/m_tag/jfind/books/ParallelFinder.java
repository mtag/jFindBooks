package org.m_tag.jfind.books;



import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Combined finders for parallel query.
 */
public abstract class ParallelFinder extends Finder {

  protected final Map<String, Finder> finders;

  protected ParallelFinder(Map<String, Finder> finders) {
    super();
    this.finders = finders;
  }

  protected ParallelFinder(String type, String id, Map<String, Finder> finders) {
    super(type, id);
    this.finders = finders;
  }

  @Override
  public Stream<Book> find(Query query) throws IOException, ClassNotFoundException, SQLException  {
    Stream<Book> ret = null;
    for (Map.Entry<String, Finder> entry : finders.entrySet()) {
      Finder finder = entry.getValue();
      Stream<Book> founds = finder.find(query);
      if (ret == null) {
        ret = founds;
      } else {
        ret = Stream.concat(ret, founds).parallel();
      }
    }
    return ret;
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
  protected void toString(StringBuilder builder) {
  }

}
