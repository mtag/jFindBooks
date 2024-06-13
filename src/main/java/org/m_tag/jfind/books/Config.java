package org.m_tag.jfind.books;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.m_tag.jfind.books.file.FindFolder;
import org.m_tag.jfind.books.file.FindLocate;

/**
 * config for sqlite3 dbs.
 */
public class Config extends Finder {


  private static final Map<String, Constructor<? extends Finder>> constructors = new HashMap<>();
  
  static {
    registerFinder("locate", FindLocate.class);
    registerFinder("folder", FindFolder.class);
  }

  private Finder getFinder(JsonValue json) {
    final String type = Finder.readRequiredJsonValue(json, "type").toLowerCase();
    final String id = Finder.readRequiredJsonValue(json, "id").toLowerCase();
    final Constructor<? extends Finder> constructor = constructors.get(type);
    if (constructor == null) {
      throw new IllegalArgumentException(String.format("unknown finder type:%s", type));
    }
    try {
      return constructor.newInstance(type, id, json);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException ex) {
      throw new IllegalArgumentException(String.format("Illegal constructor at type:%s", type), ex);
    }
  }

  protected static void registerFinder(String key, Class<? extends Finder> cl) {
    try {
      final Constructor<? extends Finder> constructor =
          cl.getConstructor(String.class, String.class, JsonValue.class);
      constructors.put(key, constructor);
    } catch (NoSuchMethodException ex) {
      // TODO 直す
      ex.printStackTrace();
    }
  }

  private final Map<String, Finder> finders;

  /**
   * constructor.
   *
   * @param file path of config.json
   * @throws FileNotFoundException configPath file does not exists.
   */
  public Config(final File file) throws FileNotFoundException {
    this(file.toPath());
  }

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
        finder.put(Finder.readRequiredJsonValue(item, "id"), getFinder(item));
      });
      this.finders = finder;
    }
  }

  /**
   * constructor.
   *
   * @param configPath path of config.json
   * @throws FileNotFoundException configPath file does not exists.
   */
  public Config(final String configPath) throws FileNotFoundException {
    this(Path.of(configPath));
  }

  @Override
  public Stream<Book> find(Query query) throws IOException {
    Stream<Book> ret = null;
    for (Map.Entry<String, Finder> entry : finders.entrySet()) {
      Finder finder = entry.getValue();
      Stream<Book> founds = finder.find(query);
      if (ret == null) {
        ret = founds;
      } else {
        ret = Stream.concat(ret, founds);
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
