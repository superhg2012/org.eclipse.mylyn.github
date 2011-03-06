/**
 * 
 */
package org.eclipse.mylyn.github.internal.collections;

/**
 * Predicate used to filter a collection.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
public interface Predicate<T> {

	/**
	 * Apply the filter.
	 * 
	 * @param type
	 *            - type
	 * @return true if the filter match, false otherwise
	 */
	boolean apply(T type);

}
