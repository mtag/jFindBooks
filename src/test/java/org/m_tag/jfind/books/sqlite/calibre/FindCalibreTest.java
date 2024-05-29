package org.m_tag.jfind.books.sqlite.calibre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

class FindCalibreTest {
  @Test
  void queryAll() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new Book("John Schember", "Quick Start Guide")};
    Query query = new Query();
    try (FindCalibre calibre = new FindCalibre("src/test/resources/metadata.db", query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByAuthor() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new Book("John Schember", "Quick Start Guide")};
    Query query = new Query();
    query.setAuthor("John Schember");
    try (FindCalibre calibre = new FindCalibre("src/test/resources/metadata.db", query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }
  
  @Test
  void queryByTitle() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new Book("John Schember", "Quick Start Guide")};
    Query query = new Query();
    query.setTitle("Quick Start Guide");
    try (FindCalibre calibre = new FindCalibre("src/test/resources/metadata.db", query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }
  
  @Test
  void queryByTitleAndAuthor() throws ClassNotFoundException, IOException, SQLException {
    Book[] expected = {new Book("John Schember", "Quick Start Guide")};
    Query query = new Query();
    query.setAuthor("John Schember");
    query.setTitle("Quick Start Guide");
    try (FindCalibre calibre = new FindCalibre("src/test/resources/metadata.db", query)) {
      final Book[] value = calibre.stream().toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }
}
