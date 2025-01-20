package org.m_tag.jfind.books.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Finder;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.locate.DbFile;

class BookFileTest {

  @Test
  void findFromDb() throws IOException, ClassNotFoundException, SQLException {
    List<String[]> replaces = new ArrayList<>(1);
    replaces.add(new String[] {"/home/mtag/eclipse-workspace/", "../"});
    Query query = new Query();
    query.setReplaces(replaces);
    query.setKeyword("Find");
    DbFile file = new DbFile("../jFindUtils/src/test/resources/test.db");
    Finder locate = new LocateFinder(file);
    Book[] results = locate.find(query).toArray(Book[]::new);
    assertEquals(2, results.length);
    final BookFile bookFile0 = (BookFile) results[0];
    assertTrue(bookFile0.getPath().toFile().exists());
    assertTrue(bookFile0.getPath().endsWith("FindIterator.java"));
    assertEquals(1537, bookFile0.getSize());
    final BookFile bookFile1 = (BookFile) results[1];
    assertTrue(bookFile1.getPath().toFile().exists());
    assertTrue(bookFile1.getPath().endsWith("FindFileIterator.java"));
    assertEquals(2950, bookFile1.getSize());
  }

  @Test
  void findFromDir() throws IOException {
    Query query = new Query();
    query.setKeyword("BookFile");
    FolderFinder find = new FolderFinder("src/main/java/");
    Book[] results = find.find(query).toArray(Book[]::new);
    assertEquals(1, results.length);
    final BookFile bookFile = (BookFile) results[0];
    assertTrue(bookFile.getPath().toFile().exists());
    assertEquals(1799, bookFile.getSize());
  }
  
  @Test
  void findFromTextFile() throws IOException {
    Query query = new Query();
    query.setKeyword("textfile");
    TextFinder find = new TextFinder("src/test/resources/textfile.txt");
    Book[] results = find.find(query).toArray(Book[]::new);
    assertEquals(1, results.length);
    final BookFile bookFile = (BookFile) results[0];
    assertTrue(bookFile.getPath().toFile().exists());
    assertEquals(113, bookFile.getSize());
  }
}
