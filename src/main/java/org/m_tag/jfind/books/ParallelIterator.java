package org.m_tag.jfind.books;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Iterator for ParallelFinder.
 *
 * @author mtag@m-tag.org
 */
public class ParallelIterator implements Iterator<Book> {

	private final ExecutorService service;
	private final Deque<Book> queue = new ArrayDeque<>();

	/**
	 * constructor.
	 *
	 * @param finders sub finders to call.
	 * @param query search condition.
	 */
	public ParallelIterator(Map<String, Finder> finders, final Query query) {
		super();
		this.service = Executors.newFixedThreadPool(finders.size());
		for (Map.Entry<String, Finder> entry : finders.entrySet()) {
			final Finder finder = entry.getValue();
			service.execute(() -> {
				try {
					try (Stream<Book> founds = finder.find(query)) {
						founds.forEach(book -> {
							synchronized (queue) {
								queue.push(book);
							}
						});
					}
				} catch (ClassNotFoundException | IOException | SQLException ex) {
					throw new FindingException(String.format("Error in %s : %s", entry.getKey(), ex.getMessage()), ex);
				}

			});
		}
		this.service.shutdown();
	}

	@Override
	public boolean hasNext() {
		if (!queue.isEmpty()) {
			return true;
		}

        try {
            // waiting 10second to find. If no new founded book in the period, quit all threads to find.
            if (service.awaitTermination(10, TimeUnit.SECONDS)) {
            	// finished all threads successfully.
            	return !queue.isEmpty();
            }
            // If timed out, check the queue.
    		if (!queue.isEmpty()) {
    			return true;
    		}
    		// If no books found in this period, shutdown finding.
            service.shutdownNow();
            return false;
        } catch (InterruptedException e) {
            // If interrupted , shutdown all threads.
        	service.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
	}

	@Override
	public Book next() throws NoSuchElementException {
		synchronized (queue) {
			if (queue.isEmpty()) {
				throw new NoSuchElementException("no element now. Call hasNext() before next()");
			}
			return queue.pop();
		}
	}

}
