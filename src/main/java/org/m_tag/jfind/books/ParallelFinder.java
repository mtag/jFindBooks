package org.m_tag.jfind.books;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * Combined finders for parallel query.
 */
public abstract class ParallelFinder extends Finder {

	protected final Map<String, Finder> finders;

	protected ParallelFinder(Map<String, Finder> finders) {
		super();
		this.finders = finders;
	}

	protected ParallelFinder(String type, String id, Map<String, Finder> finders) {
		super(type, id);
		this.finders = finders;
	}

	@Override
	public Stream<Book> find(final Query query) throws IOException, ClassNotFoundException, SQLException {
		ExecutorService service = Executors.newFixedThreadPool(3);
		final List<Book> results = new ArrayList<>();
		for (Map.Entry<String, Finder> entry : finders.entrySet()) {
			final Finder finder = entry.getValue();
			service.execute(new Runnable() {

				@Override
				public void run() {
					try {
						try(Stream<Book> founds = finder.find(query)) {
							founds.forEach(results::add);
						}
					} catch (ClassNotFoundException | IOException | SQLException ex) {
						throw new FindingException(
								String.format("Error in %s : %s", entry.getKey(), ex.getMessage()),
								ex);
					}
				}
			});
		}
		synchronized (service) {
			while(true) {
				try {
					service.wait(1000);
					break;
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
		}
		return results.stream();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		boolean isFirst = true;
		for (Map.Entry<String, Finder> entry : finders.entrySet()) {
			String value = entry.getValue().toString();
			if (value == null) {
				return value;
			}
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(',');
			}
			builder.append(value);
		}
		builder.append(']');
		return builder.toString();
	}

	@Override
	protected void toString(StringBuilder builder) {
	}

}
