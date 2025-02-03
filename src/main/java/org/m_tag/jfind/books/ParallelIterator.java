package org.m_tag.jfind.books;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterator for ParallelFinder.
 *
 * @author mtag@m-tag.org
 */
public class ParallelIterator implements Iterator<Book> {
  /**
   * logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(ParallelIterator.class);

  private static final int WAIT_PERIOD = 100;
  private final long last;
  private final ExecutorService service;
  private final Map<String, Book> queue = new LinkedHashMap<>();
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
            logger.debug("found book:{}",  book);
            synchronized (queue) {
              final String location = book.getLocation();
              if (queue.containsKey(location)) {
                logger.debug("already queued:{}:{}", location, book);
              } else {
                logger.debug("queueing book:{}:{}", queue.size(), book);
                queue.put(location, book);
                if ((maxCount != ParallelFinder.NO_LIMIT && queue.size() >= maxCount)
                    || System.currentTimeMillis() > last) {
                  logger.debug("Cutting iteration due to count over the limit :{}", maxCount);
                  this.service.shutdownNow();
                }
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
    while (true) {
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
      Map.Entry<String, Book> entry = queue.entrySet().iterator().next();
      queue.remove(entry.getKey());
      return entry.getValue();
    }
  }

}
