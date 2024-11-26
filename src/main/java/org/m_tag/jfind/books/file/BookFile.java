package org.m_tag.jfind.books.file;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.m_tag.jfind.books.Book;

/**
 * Book as file.
 */
public class BookFile extends Book {
  private final Path path;
  private static final Pattern pattern = Pattern.compile(
      "^([(（]([^)）]+)[)）] *)*\\[([^\\]]+)\\] *(.*) *([v第]\\d.*)?[.](rar|zip|7z|lzh)$",
      Pattern.CASE_INSENSITIVE);

  /**
   * constructor.
   *
   * @param path path of the found file.
   */
  public BookFile(final Path path) {
    super(getSize(path));
    this.path = path;

    final Path dir = path.getParent();
    final String location = dir == null ? null : dir.toString();
    this.setLocation(location);

    final Path nameOnly = path.getFileName();
    String name;
    if (nameOnly == null) {
      // drive letter only (c:\)
      name = path.toString();
    } else {
      name = nameOnly.toString();

      final Matcher matcher = pattern.matcher(name);
      if (matcher.matches()) {
        this.setAuthor(matcher.group(3));
        this.setTitle(matcher.group(4));
        return;
      }
    }

    this.setAuthor("");
    this.setTitle(name);
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
   * get size of found file.
   *
   * @return file size.
   */
  private static Long getSize(Path path) {
    final File file = path.toFile();
    final Long size;
    if (!file.exists()) {
      size = null;
    } else if (file.isDirectory()) {
      size = 0L;
    } else {
      size = file.length();
    }
    return size;
  }
  
  @Override
  public String toString() {
    return String.format("%d %s", getSize(), path.toString());
  }
}
