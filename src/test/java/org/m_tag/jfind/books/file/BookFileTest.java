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
    String dbPaths = 
        String.join(File.pathSeparator,
            new String[] {
                "../jFindUtils/src/test/resources/test.db",
                "/home/mtag/eclipse-workspace/jFindUtils/",
                "../jFindUtils/"});
    DbFile file = new DbFile(dbPaths);

    Query query = new Query();
    query.setAuthor("FindFileIterator");
    Book[] results = BookFile.findDb(file, query).toArray(Book[]::new);
    assertEquals(1, results.length);
    assertTrue(((BookFile) results[0]).getPath().toFile().exists());
  }
  
  @Test
  void findFromDir() throws IOException {
    Query query = new Query();
    query.setAuthor("BookFile");
    Book[] results = BookFile.find(Path.of("src/main/java/"), query).toArray(Book[]::new);
    assertEquals(1, results.length);
    assertTrue(((BookFile) results[0]).getPath().toFile().exists());
  }
}
