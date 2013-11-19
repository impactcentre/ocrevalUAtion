/*
 * Copyright (C) 2013 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.util;

/**
 * Counts number of objects of each type
 * @version 2012.06.07
 * @param <Type> the class of objects being counted
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
