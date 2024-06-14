package org.m_tag.jfind.books;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.m_tag.jfind.books.file.FindFolder;
import org.m_tag.jfind.books.file.FindLocate;

/**
 * config for sqlite3 dbs.
 */
public class Config extends ParallelFinder {


  private static final Map<String, Constructor<? extends Finder>> constructors = new HashMap<>();

  static {
    registerFinder("locate", FindLocate.class);
    registerFinder("folder", FindFolder.class);
  }

  private static Finder getFinder(JsonObject json) {
    final String type = Finder.readRequiredJsonValue(json, "type").toLowerCase();
    final String id = Finder.readRequiredJsonValue(json, "id").toLowerCase();
    final Constructor<? extends Finder> constructor = constructors.get(type);
    if (constructor == null) {
      throw new IllegalArgumentException(String.format("unknown finder type:%s", type));
    }
    try {
      return constructor.newInstance(type, id, json);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
      throw new IllegalArgumentException(
          String.format("Illegal constructor at type:%s, %s", type, ex.getMessage()), ex);
    } catch (InvocationTargetException ex) {
      throw new IllegalArgumentException(
          String.format("Error in constructor at type:%s, %s", type, ex.getMessage()), ex);
    }
  }

  protected static void registerFinder(String key, Class<? extends Finder> cl) {
    try {
      final Constructor<? extends Finder> constructor =
          cl.getConstructor(String.class, String.class, JsonObject.class);
      constructors.put(key, constructor);
    } catch (NoSuchMethodException ex) {
      // TODO 直す
      ex.printStackTrace();
    }
  }

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
    super(getFinders(configPath));
  }

  private static Map<String, Finder> getFinders(final Path configPath)
      throws FileNotFoundException {
    try (final JsonReader reader = Json.createReader(new FileInputStream(configPath.toFile()))) {
      JsonArray array = reader.readArray();
      Map<String, Finder> finder = new LinkedHashMap<>();
      array.forEach(item -> {
        final JsonObject object = item.asJsonObject();
        finder.put(Finder.readRequiredJsonValue(object, "id"), getFinder(object));
      });
      return finder;
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
}
