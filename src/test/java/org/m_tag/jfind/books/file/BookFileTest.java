package org.m_tag.jfind.books.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.m_tag.jfind.books.Book;
import org.m_tag.jfind.books.Query;
import org.m_tag.jfind.utils.locate.DbFile;

class BookFileTest {

  @Test
  void findFromDb() throws IOException {
    List<String[]> replaces = new ArrayList<>(1);
    replaces.add(new String[] {"/home/mtag/eclipse-workspace/", "../"});
    Query query = new Query();
    query.setReplaces(replaces);
    query.setKeyword("FindFileIterator");
    DbFile file = new DbFile("../jFindUtils/src/test/resources/test.db");
    LocateFinder locate = new LocateFinder(file);
    Book[] results = locate.find(query).toArray(Book[]::new);
    assertEquals(1, results.length);
    assertTrue(((BookFile) results[0]).getPath().toFile().exists());
  }

  @Test
  void findFromDir() throws IOException {
    Query query = new Query();
    query.setKeyword("BookFile");
    FolderFinder find = new FolderFinder("src/main/java/");
    Book[] results = find.find(query).toArray(Book[]::new);
    assertEquals(1, results.length);
    assertTrue(((BookFile) results[0]).getPath().toFile().exists());
  }
}
