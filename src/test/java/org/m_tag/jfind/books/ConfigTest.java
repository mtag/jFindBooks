package org.m_tag.jfind.books;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigTest {

  @Test
  void test() throws IOException {
    final Query query = new Query();
    query.setAuthor("FindFileIterator");
    query.setReplacement("/home/mtag/eclipse-workspace/jFindUtils/", "../jFindUtils/");
    Config config = new Config(Path.of("src/test/resources/jFindBooks.json"));
    List<Book> books = config.find(query).toList();
    assertEquals(1, books.size());
  }

}
