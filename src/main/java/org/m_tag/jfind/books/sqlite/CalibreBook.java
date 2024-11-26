package org.m_tag.jfind.books.sqlite;

import java.io.File;
import java.nio.file.Path;

public class CalibreBook extends SqliteBook {
  private final Path file;
  
  public CalibreBook(final String author, final String title, final String path) {
    super(author, title, new File(path).length());
    
    this.file = new File(path).toPath();
  }
}
