package org.m_tag.jfind.books.file;

import jakarta.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.text.TextFindIterator;

/**
 * Find file names from text file.
 */
public class TextFinder extends Finder {

  /**
   * path of text file.
   */
  private final Path textFile;

  /**
   * constructor.
   *
   * @param textFile path of text file.
   */
  public TextFinder(Path textFile) {
    super();
    this.textFile = textFile;
  }

  /**
   * constructor.
   *
   * @param textFile path of text file.
   */
  public TextFinder(File textFile) {
    super();
    this.textFile = textFile.toPath();
  }

  /**
   * constructor.
   *
   * @param type type of Finder
   * @param id id of Finder
   * @param value json value from config.
   */
  public TextFinder(final String type, final String id, final JsonObject value) {
    super(type, id);
    this.textFile = Path.of(Finder.readRequiredJsonValue(value, "file"));
  }

  /**
   * constructor.
   *
   * @param textFile path of text file.
   */
  public TextFinder(String textFile) {
    super();
    this.textFile = Path.of(textFile);
  }
  
  @Override
  public Stream<Book> find(Query query) throws IOException {
    final List<Book> list = new ArrayList<>();
    if (textFile.toFile().exists()) {
      try (final TextFindIterator textFindIterator = new TextFindIterator(textFile);
          final Stream<Path> stream = textFindIterator.stream().filter(query::matches)) {
        // TODO いったんlistにしているのをstream処理に直す
        stream.forEach((path) -> list.add(new BookFile(path)));
      }
    }
    return list.stream();
  }

  @Override
  protected void toString(StringBuilder builder) {
    builder.append(",\"textFile\":\"");
    escape(builder, textFile.toString());
    builder.append('\"');
  }
}
