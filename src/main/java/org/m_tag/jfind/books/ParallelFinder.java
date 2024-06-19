package org.m_tag.jfind.books;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
		Iterator<Book> iterator = new ParallelIterator(finders, query);
		Spliterator<Book> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
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
