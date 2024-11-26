package org.m_tag.jfind.books.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;

class FindCalibreTest {
  private static final String CALIBRE_LIBRARY = "./src/test/resources/";

  @Test
  void queryStream() throws ClassNotFoundException {
    Book[] expected = {
      new CalibreBook("John Schember", "Quick Start Guide", "John Schember/Quick Start Guide (1)"),
      new CalibreBook("夏目 漱石", "吾輩は猫である", "Ka Moku  Sou Shaku/Go Hai ha Byou dearu (2)"),
    };
    Query query = new Query();

    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_LIBRARY).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);

      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryAll() throws ClassNotFoundException {
    Book[] expected = {
      new CalibreBook("John Schember", "Quick Start Guide", "John Schember/Quick Start Guide (1)"),
      new CalibreBook("夏目 漱石", "吾輩は猫である", "Ka Moku  Sou Shaku/Go Hai ha Byou dearu (2)"),
    };
    Query query = new Query();
    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_LIBRARY).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByAuthor() throws ClassNotFoundException {
    Book[] expected = {
        new CalibreBook(
            "John Schember", "Quick Start Guide", "John Schember/Quick Start Guide (1)")};
    Query query = new Query();
    query.setAuthor("John Schember");
    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_LIBRARY).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByTitle() throws ClassNotFoundException {
    Book[] expected = {
        new CalibreBook(
            "John Schember", "Quick Start Guide", "John Schember/Quick Start Guide (1)")};
    Query query = new Query();
    query.setTitle("Quick Start Guide");
    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_LIBRARY).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByTitleAndAuthor() throws ClassNotFoundException {
    Book[] expected = {
        new CalibreBook(
            "John Schember", "Quick Start Guide", "John Schember/Quick Start Guide (1)")};
    Query query = new Query();
    query.setAuthor("John");
    query.setTitle("Guide");
    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_LIBRARY).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }

  @Test
  void queryByJapanese() throws ClassNotFoundException {
    Book[] expected = {
        new CalibreBook("夏目 漱石", "吾輩は猫である", "Ka Moku  Sou Shaku/Go Hai ha Byou dearu (2)")};
    Query query = new Query();
    query.setAuthor("夏目");
    query.setTitle("猫");
    try (final Stream<Book> stream = new CalibreFinder(CALIBRE_LIBRARY).find(query)) {
      final Book[] value = stream.toArray(Book[]::new);
      assertEquals(expected.length, value.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], value[i]);
      }
    }
  }
}
