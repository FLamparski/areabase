package nde2.helpers;

import android.util.Log;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nde2.pull.types.DateRange;

/**
 * A collection of functions that have to do with arrays and collections.
 *
 * @author filip
 */
public class ArrayHelpers {

	/**
	 * Reduces the size of an array of 2-arrays of doubles so that only every
	 * nth pair remains. In practice, this can be used to remove vertices of a
	 * geo-coded polygon to reduce GET request string length (used with the
	 * Police Data API).
	 * 
	 * @param original
	 *            The array to be simplified
	 * @param n
	 *            number of elements to skip (hence "every nth pair")
	 * @return A simplified array of 2-arrays of doubles.
	 */
	public static double[][] every_nth_pair(double[][] original, int n) {
		double[][] destination = new double[original.length / n][2];

		for (int i = 0; i < original.length; i += n) {
			try {
				destination[i / n] = original[i];
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
		}

		return destination;
	}

    /**
     * Get the most common element in the given list
     * @param list The list to search
     * @param <T> Type of elements in the list
     * @return The most common element in the list
     */
	public static <T> T mostCommon(List<T> list){
		Map<T, Integer> map = new HashMap<T, Integer>();
		
		for(T e : list){
			Integer c = map.get(e);
			map.put(e, c == null ? 1 : c + 1);
		}
		
		Entry<T, Integer> max = null;
		
		for(Entry<T, Integer> e : map.entrySet()){
            Log.v("mostCommon", "Entry [" + e.getKey().toString() + " => " + e.getValue().toString() + "]");
            if (max == null || e.getValue() > max.getValue()) {
                Log.v("mostCommon", "...is the new most common");
                max = e;
            }
		}
		
		return max.getKey();
	}

    /**
     * Needed for GraphActivity -- unpacks the dateRanges array and maps the date ranges
     * to their textual representation.
     * @param dateRanges the date ranges to unpack
     * @return date ranges mapped to their text representations
     */
    public static BiMap<String, DateRange> remapDateRanges(DateRange... dateRanges){
        BiMap<String, DateRange> newMap = HashBiMap.create();
        for(DateRange dr : dateRanges){
            String s;
            if(dr.getStartDate().compareTo(dr.getEndDate()) == 0){
                s = String.format("%TF", dr.getEndDate());
            } else {
                s = String.format("%TF - %TF", dr.getStartDate(), dr.getEndDate());
            }
            newMap.put(s, dr);
        }
        return newMap;
    }

}
