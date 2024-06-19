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

	private static final int WAIT_PERIOD = 100;
	private final int limit;
	private final ExecutorService service;
	private final Deque<Book> queue = new ArrayDeque<>();

	/**
	 * constructor.
	 *
	 * @param finders sub finders to call.
	 * @param query   search condition.
	 */
	public ParallelIterator(Map<String, Finder> finders, final Query query, final int limit) {
		super();
		this.limit = limit;
		this.service = Executors.newFixedThreadPool(finders.size());
		for (Map.Entry<String, Finder> entry : finders.entrySet()) {
			final Finder finder = entry.getValue();
			service.execute(() -> {
				try (Stream<Book> founds = finder.find(query)) {
					founds.forEach(book -> {
						synchronized (queue) {
							queue.push(book);
						}
					});
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
			// waiting 10second to find. If no new founded book in the period, quit all
			// threads to find.
			for(int i=0; i<limit; i+=WAIT_PERIOD) {
				if (service.awaitTermination(WAIT_PERIOD, TimeUnit.MILLISECONDS)) {
					// finished all threads successfully.
					return !queue.isEmpty();
				}
				// If timed out, check the queue.
				if (!queue.isEmpty()) {
					return true;
				}
				// item not received. retry.
			}
			// If no books found in WAIT_FOR_FOUND, shutdown finding.
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
