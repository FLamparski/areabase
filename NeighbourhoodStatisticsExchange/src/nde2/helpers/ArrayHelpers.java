package nde2.helpers;

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

}
