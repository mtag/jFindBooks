package org.m_tag.jfind.books.file;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.m_tag.jfind.ReadingException;
import org.m_tag.jfind.books.Book;

/**
 * Book as file.
 */
public class BookFile extends Book {
  private final Path path;
  private static final Pattern pattern =
      Pattern.compile("^[(（]([^)）]+)[)）] *\\[([^\\]]+)\\] *(.*) *([v第]\\d.*)?[.](rar|zip|7z|lzh)$",
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

  @Override
  public String toString() {
    File file = path.toFile();
    long size;
    if (!file.exists()) {
      size = -1;
    } else if (file.isDirectory()) {
      size = 0;
    } else {
      size = file.length();
    }
    return String.format("%d %s", size, path.toString());
  }
}
