package org.m_tag.jfind.books;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * query for finding books.
 */
public class Query {
  private String author;
  private String title;
  private boolean exists = true;
  private List<String[]> replaces;
  private final List<Pattern> patterns = new ArrayList<>();
  private String keyword;
  private boolean caseSensitive;

  public Query() {
    super();
    updatePattern();
  }

  public List<String[]> getReplaces() {
    return replaces;
  }

  public void setReplaces(List<String[]> replaces) {
    this.replaces = replaces;
  }

  public boolean isExists() {
    return exists;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
    updatePattern();
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
    updatePattern();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
    updatePattern();
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
    updatePattern();
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    updatePattern();
  }

  /**
   * matching found path and query.
   *
   * @param path found path
   * @return matched or not
   */
  public boolean matches(final Path path) {
    if (patterns.isEmpty()) {
      return true;
    }
    String name = path.getFileName().toString();
    for (Pattern pattern : patterns) {
      Matcher matcher = pattern.matcher(name);
      if (!matcher.matches()) {
        // and search
        return false;
      }
    }
    return true;
  }

  private String escape(String word) {
    return Pattern.quote(word);
  }

  private void updatePattern() {
    synchronized (patterns) {
      patterns.clear();

      if (keyword != null) {
        patterns.add(Pattern.compile(".*(" + escape(keyword) + ").*", 0));
      }
      if (title != null) {
        patterns.add(Pattern.compile(".*\\].*(" + escape(title) + ").*", 0));
      }
      if (author != null) {
        StringBuilder builder = new StringBuilder();
        builder.append(".*\\[[^\\[\\]]*(");
        builder.append(escape(author));
        builder.append(")[^\\[\\]]*\\].*");
        patterns.add(Pattern.compile(builder.toString()));
      }
    }
  }

  /**
   * main for command line.
   *
   * @param args arguments from command line
   * @throws IOException IO error
   * @throws SQLException Error in select from rdb.
   * @throws ClassNotFoundException failed to load JDBC driver.
   */
  public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
    String file = null;
    final Query query = new Query();
    query.setExists(false);
    int i = 0;
    while (i < args.length) {
      String arg = args[i];
      switch (arg) {
        case "-f":
          file = args[++i];
          break;
        case "-a":
          query.setAuthor(args[++i]);
          break;
        case "-t":
          query.setTitle(args[++i]);
          break;
        case "-k":
          query.setKeyword(args[++i]);
          break;
        default:
          query.setKeyword(arg);
          break;
      }
      i++;
    }
    if (file == null) {
      file = getDefaultConfig();
    }
    Config.getConfig(Path.of(file)).find(query)
        .forEach(book -> System.out.println(book.toString()));
  }

  public static String getDefaultConfig() {
    final Map<String, String> env = System.getenv();
    String file = env.get(Config.JFINDBOOKS_JSON);
    if (file == null) {
      String homeConfig = null;
      if (env.containsKey("HOME")) {
        homeConfig = env.get("HOME") + File.separatorChar + ".jfindbook.json";
      } else if (env.containsKey("USERPROFILE")) {
        homeConfig = env.get("USERPROFILE") + File.separatorChar + "jfindbook.json";
      }
      if (homeConfig != null && new File(homeConfig).exists()) {
        file = homeConfig;
      }
      if (file == null) {
        System.err.println(
            String.format("Usage: java %s -f json [-a author] [-t title] [-k keywork] [keyword]",
                Query.class.getName()));
        System.exit(-1);
      }
    }
    return file;
  }
}
