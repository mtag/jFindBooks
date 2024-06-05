package org.m_tag.jfind.books.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

class FindCalibreTest {
  @Test
  void queryAll() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {
        new SqliteBook("John Schember", "Quick Start Guide"),
        new SqliteBook("夏目 漱石", "吾輩は猫である"),
        };
    Query query = new Query();
    try (SqliteAbstractIterator calibre =
        new CalibreIterator(CalibreIterator.getDefaultMetadata(), query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
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
    try (SqliteAbstractIterator calibre = new CalibreIterator(query)) {
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
    try (SqliteAbstractIterator calibre =
        new CalibreIterator(CalibreIterator.getDefaultMetadata(), query)) {
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
    try (SqliteAbstractIterator calibre =
        new CalibreIterator(CalibreIterator.getDefaultMetadata(), query)) {
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
    try (SqliteAbstractIterator calibre =
        new CalibreIterator(CalibreIterator.getDefaultMetadata(), query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }
}
