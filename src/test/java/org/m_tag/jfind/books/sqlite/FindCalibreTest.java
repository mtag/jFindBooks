package org.m_tag.jfind.books.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

class FindCalibreTest {
  private static final String CALIBRE_METADATA = "./src/test/resources/metadata.db";

  @Test
  void queryStream() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {
      new SqliteBook("John Schember", "Quick Start Guide"),
      new SqliteBook("夏目 漱石", "吾輩は猫である"),
    };
    Query query = new Query();

    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_METADATA).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);

      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryAll() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {
      new SqliteBook("John Schember", "Quick Start Guide"),
      new SqliteBook("夏目 漱石", "吾輩は猫である"),
    };
    Query query = new Query();
    try (SqlIterator calibre = new CalibreIterator(CALIBRE_METADATA, query)) {
      final Stream<Book> stream = calibre.stream();
      final Book[] value = stream.toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByAuthor() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new SqliteBook("John Schember", "Quick Start Guide")};
    Query query = new Query();
    query.setAuthor("John Schember");
    try (SqlIterator calibre = new CalibreIterator(CALIBRE_METADATA, query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByTitle() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new SqliteBook("John Schember", "Quick Start Guide")};
    Query query = new Query();
    query.setTitle("Quick Start Guide");
    try (SqlIterator calibre = new CalibreIterator(CALIBRE_METADATA, query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByTitleAndAuthor() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new SqliteBook("John Schember", "Quick Start Guide")};
    Query query = new Query();
    query.setAuthor("John");
    query.setTitle("Guide");
    try (SqlIterator calibre = new CalibreIterator(CALIBRE_METADATA, query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }


  @Test
  void queryByJapanese() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new SqliteBook("夏目 漱石", "吾輩は猫である")};
    Query query = new Query();
    query.setAuthor("夏目");
    query.setTitle("猫");
    try (SqlIterator calibre = new CalibreIterator(CALIBRE_METADATA, query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }
}
