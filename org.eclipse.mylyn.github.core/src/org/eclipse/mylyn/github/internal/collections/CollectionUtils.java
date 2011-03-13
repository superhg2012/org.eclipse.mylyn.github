/**
 * 
 */
package org.eclipse.mylyn.github.internal.collections;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility operation for collections
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public class CollectionUtils {

	private CollectionUtils() {

	}

	/**
	 * Filter a collection.
	 * 
	 * @param <T>
	 *            - generic type
	 * @param target
	 *            - target collection
	 * @param predicate
	 *            - predicate
	 * @return a filtered collection that may be empty
	 */
	public static <T> Collection<T> filter(Collection<T> target,
			Predicate<T> predicate) {
		Collection<T> filteredCollection = new ArrayList<T>();
		for (T t : target) {
			if (predicate.apply(t)) {
				filteredCollection.add(t);
			}
		}
		return filteredCollection;
	}

}
