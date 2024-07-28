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
  /**
   * get config file path for cli applications.
   *
   * @param args command line arguments.
   * @return config path.
   */
  public static String getDefaultConfig(String[] args) {
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
      file = getDefaultConfig(args);
    }
    Config.getConfig(Path.of(file)).find(query)
        .forEach(book -> System.out.println(book.toString()));
  }
  
  private String author;
  private boolean caseSensitive;
  private boolean exists = true;
  private String keyword;
  private final List<Pattern> patterns = new ArrayList<>();

  private List<String[]> replaces;

  private String title;

  public Query() {
    super();
    updatePattern();
  }

  private String escape(String word) {
    return Pattern.quote(word);
  }

  private void extracted(StringBuilder builder, String fieldName, String field) {
    if (field != null) {
      builder.append('\"');
      builder.append(fieldName);
      builder.append("\":\"");
      builder.append(field);
      builder.append("\",");
    }
  }

  public String getAuthor() {
    return author;
  }

  public String getKeyword() {
    return keyword;
  }

  public List<String[]> getReplaces() {
    return replaces;
  }

  public String getTitle() {
    return title;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public boolean isExists() {
    return exists;
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

  public void setAuthor(String author) {
    this.author = author;
    updatePattern();
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    updatePattern();
  }

  public void setExists(boolean exists) {
    this.exists = exists;
    updatePattern();
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
    updatePattern();
  }

  public void setReplaces(List<String[]> replaces) {
    this.replaces = replaces;
  }

  public void setTitle(String title) {
    this.title = title;
    updatePattern();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('{');
    builder.append("\"caseSensitive\":");
    builder.append(caseSensitive);
    builder.append(',');
    extracted(builder, "author", author);
    extracted(builder, "title", title);
    extracted(builder, "keyword", keyword);
    builder.delete(builder.length() - 1, builder.length());
    builder.append('}');
    return builder.toString();
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
}
