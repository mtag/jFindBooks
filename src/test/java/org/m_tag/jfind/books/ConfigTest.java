package org.m_tag.jfind.books;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigTest {

  @Test
  void test() throws IOException, ClassNotFoundException, SQLException {
    final Query query = new Query();
    query.setKeyword("FindFileIterator");
    Config config = Config.getConfig(Path.of("src/test/resources/jFindBooks.json"));
    List<Book> books = config.find(query).toList();
    assertEquals(3, books.size());
  }

}
