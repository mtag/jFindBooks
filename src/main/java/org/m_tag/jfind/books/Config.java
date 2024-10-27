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

/**
 * config for sqlite3 dbs.
 */
public class Config extends ParallelFinder {

  private static final String TIME_OUT = "timeOut";

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

  private final List<String[]> replaces;
  
  public static final String JFINDBOOKS_JSON = "JFINDBOOKS_JSON";

  /**
   * constructor.
   *
   * @param configPath path of config.json
   * @param timeOut timeout for not found in milliseconds.
   * @throws FileNotFoundException configPath file does not exists.
   */
  private Config(final Map<String, Finder> finders, List<String[]> replaces, int timeOut)  {
    super(finders, timeOut);
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
      Map<String, Finder> finder = new LinkedHashMap<>();
      array.forEach(item -> {
        final JsonObject object = item.asJsonObject();
        finder.put(Finder.readRequiredJsonValue(object, "id"), getFinder(object));
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
      return new Config(finder, replaces, timeOut);
    }
  }
}
