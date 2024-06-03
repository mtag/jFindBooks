package org.m_tag.jfind.books.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.BookIterator;
import org.m_tag.jfind.utils.FindIterator;

public class BookFileIetrator implements BookIterator {

  
  private  FindIterator source;
  public BookFileIetrator(final FindIterator source) {
    super();
    this.source = source;
  }
  @Override
  public boolean hasNext() {
    return source.hasNext();
  }

  @Override
  public Book next() {
    Path path = source.next();
    return new BookFile(path);
  }

  @Override
  public void close() throws IOException {
    if (source instanceof  Closeable) {
      ((Closeable) source).close();
    }
  }

}
