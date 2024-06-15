package org.m_tag.jfind.books;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class QueryTest {

  @Test
  void testByAuthor() {
    Query query = new Query();
    query.setAuthor("test");
    assertTrue(query.matches(Path.of("(sample) [a×test] test")));
    assertTrue(!query.matches(Path.of("(sample) [a×tes] test")));
    assertTrue(!query.matches(Path.of("(sample) [a] test")));
    assertTrue(!query.matches(Path.of("(test) [a] sample")));
    assertTrue(!query.matches(Path.of("[a] sample")));
    assertTrue(query.matches(Path.of("[test] sample")));
    assertTrue(query.matches(Path.of("(sample) [a×test×b] xyz")));
  }

}
