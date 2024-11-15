package org.m_tag.jfind.books;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Iterator for ParallelFinder.
 *
 * @author mtag@m-tag.org
 */
public class ParallelIterator implements Iterator<Book> {

  private static final int WAIT_PERIOD = 100;
  private final long last;
  private final ExecutorService service;
  private final Deque<Book> queue = new ArrayDeque<>();
  private int count = 0;
  private final List<Future<?>> threads = new ArrayList<>();

  /**
   * constructor.
   *
   * @param finders sub finders to call.
   * @param query search condition.
   * @param timeLimit time limit for query
   * @param maxCount  max record count for each query
   */
  public ParallelIterator(Map<String, Finder> finders, final Query query, 
      final int timeLimit, final int maxCount) {
    super();
    this.last = System.currentTimeMillis() + timeLimit;
    this.service = Executors.newFixedThreadPool(finders.size());
    for (Map.Entry<String, Finder> entry : finders.entrySet()) {
      final Finder finder = entry.getValue();
      threads.add(service.submit(() -> {
        try (Stream<Book> founds = finder.find(query)) {
          founds.forEach(book -> {
            synchronized (this) {
              queue.push(book);
              count++;
              if ((maxCount != ParallelFinder.NO_LIMIT && count > maxCount)
                  || (timeLimit != ParallelFinder.NO_LIMIT && System.currentTimeMillis() > last)) {
                this.service.shutdownNow();
              }
            }
          });
        } catch (ClassNotFoundException | IOException | SQLException ex) {
          throw new FindingException(
              String.format("Error in %s : %s", entry.getKey(), ex.getMessage()), ex);
        }
      }));
    }
    this.service.shutdown();
  }
  
  @Override
  public boolean hasNext() {
    while(true) {
      if (!queue.isEmpty()) {
        // having value
        return true;
      }

      // check the children threads is over?
      boolean finished = isFinished();
      // finished?
      if (finished) {
        return !queue.isEmpty();
      }
      try {
        Thread.sleep(WAIT_PERIOD);
      } catch (InterruptedException e) {
        if (isFinished()) {
          return !queue.isEmpty();
        }
      }
    }
  }

  private boolean isFinished() {
    boolean finished = true;
    for (Future<?> thread : threads) {
      if (!thread.isDone() && !thread.isCancelled()) {
        finished = false;
      }
    }
    return finished;
  }

  @Override
  public Book next() throws NoSuchElementException {
    synchronized (queue) {
      if (queue.isEmpty()) {
        throw new NoSuchElementException("no element now. Call hasNext() before next()");
      }
      final Book value;
      synchronized(this) {
        value = queue.pop();
      }
      return value;
    }
  }

}
