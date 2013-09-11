/**
 * Copyright (C) 2010 Rafael C. Carrasco
 * This code can be distributed or modified
 * under the terms of the GNU General Public License V3.
 */
package eu.digitisation.util;

/**
 * Counts number of objects of each type
 * @author Rafael C. Carrasco
 * @version 2012.06.07
 */
public class Counter<Type> extends java.util.TreeMap<Type, Integer> { 
    /**
     * Increment the count for an object with the given value
     * @param object the object whose count will be incremented
     * @param value the delta value
     */
    public void add(Type object, int value) {
	int storedValue;
        if ( containsKey(object) ) {
            storedValue = get(object);
        } else {
            storedValue = 0;
        }
        put(object, storedValue + value);
    }

    /**
     * Add one to the count for an object 
     * @param object the object whose count will be incremented
     */
    public void inc (Type object) {
	add(object, 1);
    }
    
    /**
     * Subtract one to the count for an object 
     * @param object the object whose count will be decremented
     */
    public void dec (Type object) {
	add(object, -1);
    }

    /**
     * Increment the count for an object with the value stored in
     * another counter.
     * @param counter the counter whose values will be added to this
     * one.
     */
    public void add(Counter<Type> counter) {
	for (Type object: counter.keySet()){
	    add(object, counter.get(object));
	}
    }
    
    /**
     *
     * @param object
     * @return the value of the counter for that object, or 0 if not stored
     */
    public int value(Type object) {
       Integer val = get(object);
       return (val == null) ? 0 : val;
    }
}
