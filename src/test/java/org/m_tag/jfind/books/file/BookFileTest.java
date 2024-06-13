package org.m_tag.jfind.books.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.locate.DbFile;

class BookFileTest {

  @Test
  void findFromDb() throws IOException {
    DbFile file = new DbFile("../jFindUtils/src/test/resources/test.db");

    Query query = new Query();
    query.setReplacement("/home/mtag/eclipse-workspace/jFindUtils/", "../jFindUtils/");
    query.setAuthor("FindFileIterator");
    FindLocate locate = new FindLocate(file);
    Book[] results = locate.find(query).toArray(Book[]::new);
    assertEquals(1, results.length);
    assertTrue(((BookFile) results[0]).getPath().toFile().exists());
  }

  @Test
  void findFromDir() throws IOException {
    Query query = new Query();
    query.setAuthor("BookFile");
    FindFolder find = new FindFolder("src/main/java/");
    Book[] results = find.find(query).toArray(Book[]::new);
    assertEquals(1, results.length);
    assertTrue(((BookFile) results[0]).getPath().toFile().exists());
  }
}
