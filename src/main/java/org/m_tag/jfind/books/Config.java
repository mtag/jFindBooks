package org.m_tag.jfind.books;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.m_tag.jfind.books.file.FolderFinder;
import org.m_tag.jfind.books.file.LocateFinder;
import org.m_tag.jfind.books.file.TextFinder;
import org.m_tag.jfind.books.sqlite.CalibreFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * config for sqlite3 dbs.
 */
public class Config extends ParallelFinder {
  /**
   * logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(Config.class);

  private static final String TIME_OUT = "timeOut";
  private static final String MAX_COUNT = "maxCount";

  private static final Map<String, Constructor<? extends Finder>> constructors = new HashMap<>();

  static {
    registerFinder("locate", LocateFinder.class);
    registerFinder("folder", FolderFinder.class);
    registerFinder("calibre", CalibreFinder.class);
    registerFinder("text", TextFinder.class);
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
      logger.error(String.format("Illegal constructor at type:%s, %s", type, ex.getMessage()), ex);
      return null;
    } catch (InvocationTargetException ex) {
      logger.error(String.format("Error in constructor at type:%s, %s", type, ex.getMessage()), ex);
      return null;
    }
  }

  protected static void registerFinder(String key, Class<? extends Finder> cl) {
    try {
      final Constructor<? extends Finder> constructor =
          cl.getConstructor(String.class, String.class, JsonObject.class);
      constructors.put(key, constructor);
    } catch (NoSuchMethodException ex) {
      logger.error("No construcotor found", ex);
    }
  }

  private final List<String[]> replaces;
  
  public static final String JFINDBOOKS_JSON = "JFINDBOOKS_JSON";

  /**
   * constructor.
   *
   * @param configPath path of config.json
   * @param timeOut timeout for not found in milliseconds.
   * @param maxCount max record count for search.
   * @throws FileNotFoundException configPath file does not exists.
   */
  private Config(final Map<String, Finder> finders, List<String[]> replaces,
      int timeOut, int maxCount)  {
    super(finders, timeOut, maxCount);
    this.replaces = replaces;
  }

  @Override
  public Stream<Book> find(Query query) throws IOException, SQLException {
    query.setReplaces(replaces);
    return super.find(query);
  }

  /**
   * create Config instance.
   *
   * @param pathName path name of config.json
   * @return Config instance
   * @throws IOException error in reading config file 
   */
  public static Config getConfig(final String pathName) throws IOException {
    return getConfig(Path.of(pathName));
  }
  
  /**
   * create Config instance.
   *
   * @param configPath path of config.json
   * @return Config instance
   * @throws IOException error in reading config file 
   */
  public static Config getConfig(final Path configPath) throws IOException {
    try (final JsonReader reader = Json.createReader(new FileInputStream(configPath.toFile()))) {
      JsonObject top = reader.readObject();
      JsonArray array =  top.getJsonArray("finders");
      Map<String, Finder> finders = new LinkedHashMap<>();
      array.forEach(item -> {
        final JsonObject object = item.asJsonObject();
        final Finder newFinder = getFinder(object);
        if (newFinder != null) {
          finders.put(Finder.readRequiredJsonValue(object, "id"), newFinder);
        }
      });
      List<String[]> replaces = new ArrayList<>();
      if (top.containsKey("replaces")) {
        JsonArray replaceArray = top.getJsonArray("replaces");
        replaceArray.forEach(item -> {
          final JsonObject object = item.asJsonObject();
          String[] fromTo = new String[] {
              readRequiredJsonValue(object, "from"),
              readRequiredJsonValue(object, "to")
          };
          replaces.add(fromTo);
        });
      }
      int timeOut;
      if (top.containsKey(TIME_OUT)) {
        timeOut = Integer.parseInt(top.getString(TIME_OUT));
      } else {
        timeOut = ParallelFinder.NO_LIMIT;
      }     
      int maxCount;
      if (top.containsKey(MAX_COUNT)) {
        maxCount = Integer.parseInt(top.getString(MAX_COUNT));
      } else {
        maxCount = ParallelFinder.NO_LIMIT;
      }
      return new Config(finders, replaces, timeOut, maxCount);
    }
  }
}
