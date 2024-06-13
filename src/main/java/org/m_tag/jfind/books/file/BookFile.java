package org.m_tag.jfind.books.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.m_tag.jfind.ReadingException;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.find.FindFileIterator;
import org.m_tag.jfind.utils.locate.DbFile;

/**
 * Book as file.
 */
public class BookFile extends Book {
  private final Path path;
  private static final Pattern pattern =
      Pattern.compile("^[(]([^)]+)[)] *\\[([^\\]]+)\\] *(.*) *([v第]\\d.*)[.](rar|zip|7z|lzh)$",
          Pattern.CASE_INSENSITIVE);

  /**
   * constructor.
   *
   * @param path path of the found file.
   */
  public BookFile(final Path path) {
    super();
    this.path = path;

    Path dir = path.getParent();
    String location = dir == null ? null : dir.toString();
    this.setLocation(location);

    Path nameOnly = path.getFileName();
    if (nameOnly == null) {
      throw new ReadingException("no filename in path", new NullPointerException());
    }
    String name = nameOnly.toString();
    Matcher matcher = pattern.matcher(name);
    if (matcher.matches()) {
      this.setAuthor(matcher.group(2));
      this.setTitle(matcher.group(3));
    } else {
      this.setAuthor("");
      this.setTitle(name);
    }
  }

  /**
   * get path of the found file.
   *
   * @return path of the found file
   */
  public Path getPath() {
    return path;
  }

  /**
   * find books from db.
   *
   * @param db locate db
   * @param query query
   * @return stream of found books
   * @throws IOException error on opening db file.
   */
  public static Stream<Book> findDb(final DbFile db, final Query query) throws IOException {
    return db.stream().filter(query::matches).map(BookFile::new);
  }

  /**
   * find books from top directory.
   *
   * @param top top of finding directories.
   * @param query query
   * @return stream of found books
   */
  public static Stream<Book> find(final String top, final Query query) {
    return find(Path.of(top), query);
  }

  /**
   * find books from top directory.
   *
   * @param top top of finding directories.
   * @param query query
   * @return stream of found books
   */
  public static Stream<Book> find(final Path top, final Query query) {
    return new FindFileIterator(top).stream().filter(query::matches).map(BookFile::new);
  }
  
  public String toString() {
    return path.toString();
  }
}
