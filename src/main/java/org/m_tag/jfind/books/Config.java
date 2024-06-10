package org.m_tag.jfind.books;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.m_tag.jfind.books.file.FindFolder;
import org.m_tag.jfind.books.file.FindLocate;

/**
 * config for sqlite3 dbs.
 */
public class Config extends Finder {

  private final Map<String, Finder> finds;

  /**
   * constructor.
   *
   * @param configPath path of config.json
   * @throws FileNotFoundException configPath file does not exists.
   */
  public Config(final Path configPath) throws FileNotFoundException {
    super();
    try (final JsonReader reader = Json.createReader(new FileInputStream(configPath.toFile()))) {
      JsonArray array = reader.readArray();
      Map<String, Finder> finder = new LinkedHashMap<>();
      array.forEach(item -> {
        final String id = readRequiredJsonValue(item, "id");
        final String type = readRequiredJsonValue(item, "id").toLowerCase();
        switch (type) {
          case "locate":
            finder.put(id, new FindLocate(item));
            break;
          case "folder":
            finder.put(id, new FindFolder(item));
            break;
          default:
            throw new IllegalArgumentException(String.format("unknown finder type:%s", type));
        }
      });
      this.finds = finder;
    }
  }

  @Override
  public Stream<Book> find(Query query) throws IOException {
    Stream<Book> ret = null;
    for (Map.Entry<String, Finder> entry : finds.entrySet()) {
      Finder find = entry.getValue();
      Stream<Book>  founds = find.find(query);
      if (ret == null) {
        ret = founds;
      } else {
        ret = Stream.concat(ret, founds);
      }
    }
    return ret;
  }

}
