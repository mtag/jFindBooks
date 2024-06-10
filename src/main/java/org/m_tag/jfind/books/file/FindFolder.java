package org.m_tag.jfind.books.file;

import jakarta.json.JsonValue;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.find.FindFileIterator;

/**
 * Find books from folder as a file.
 */
public class FindFolder extends Finder {
  /**
   * target folder.
   */
  private final Path folder;
  
  /**
   * constructor.
   *
   * @param folder target folder
   */
  public FindFolder(final Path folder) {
    super();
    this.folder = folder;
  } 
  
  /**
   * constructor.
   *
   * @param value part of json to find books from folder.
   */
  public FindFolder(final JsonValue value) {
    super();
    this.folder = Path.of(readRequiredJsonValue(value, "folder"));
  }

  @Override
  public Stream<Book> find(Query query) throws IOException {
    Stream<Path> stream = new FindFileIterator(folder).stream().filter(query::matches);
    return stream.map(BookFile::new);
  }

}
