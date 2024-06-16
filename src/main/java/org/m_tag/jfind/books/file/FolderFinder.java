package org.m_tag.jfind.books.file;

import jakarta.json.JsonObject;
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
public class FolderFinder extends Finder {
  /**
   * target folder.
   */
  private final Path folder;
  
  /**
   * constructor.
   *
   * @param folderName target folder
   */
  public FolderFinder(final String folderName) {
    this(Path.of(folderName));
  }
  
  /**
   * constructor.
   *
   * @param folder target folder
   */
  public FolderFinder(final Path folder) {
    super("", "");
    this.folder = folder;
  } 
  
  /**
   * constructor.
   *
   * @param value part of json to find books from folder.
   */
  public FolderFinder(final String type, final String id, final JsonObject value) {
    super(type, id);
    this.folder = Path.of(Finder.readRequiredJsonValue(value, "folder"));
  }

  @Override
  public Stream<Book> find(Query query) throws IOException {
    Stream<Path> stream = new FindFileIterator(folder).stream().filter(query::matches);
    return stream.map(BookFile::new);
  }

  @Override
  protected void toString(StringBuilder builder) {
    builder.append(",\"folder\":\"");
    escape(builder, folder.toString());
    builder.append('\"');
  }
}
