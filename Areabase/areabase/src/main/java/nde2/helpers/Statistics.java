package nde2.helpers;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A collection of functions that perform statistical things with data
 * 
 * @author filip
 * 
 */
public class Statistics {

	/**
	 * @return An average of the series
	 */
	public static double average(Collection<? extends Number> series) {
		int n = series.size();
		double sum = 0;
		for (Number e : series) {
			sum += e.doubleValue();
		}
		return sum / n;
	}

	/**
	 * Calculates the linear regression gradient for the data given
	 * 
	 * @return the ordinary least squares gradient
	 * @param data
	 *            the dataset to calculate the ols for
	 */
	public static double linearRegressionGradient(
			Map<? extends Number, ? extends Number> data) {
		/*
		 * OLS linear regression: (y - ybar) = (Sxy / Sxx) * (x - xbar) we need
		 * to find Sxy / Sxx Sum(x*y) Sxy = ---------- - (xbar*ybar) n
		 * 
		 * Sum(x^2) Sxx = ---------- - (xbar^2) n
		 * 
		 * where x is the date Long (key) and y is the Integer value.
		 */

		double Sxy = 0d;
		double Sxx = 0d;
		double sumXY = 0d;
		double sumXX = 0d;
		double sumX = 0d;
		double sumY = 0d;
		double x, y;
		int n = data.size();

		for (Entry<? extends Number, ? extends Number> point : data.entrySet()) {
			x = (double) point.getKey().doubleValue();
			y = (double) point.getValue().doubleValue();
			sumX += x;
			sumY += y;
			sumXY += x * y;
			sumXX += x * x;
		}

		double xbar = sumX / n;
		double ybar = sumY / n;

		Sxy = sumXY / n - xbar * ybar;
		Sxx = sumXX / n - xbar * xbar;

		return Sxy / Sxx;
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	/**
	 * Calculates the "distance" (a measure of similarity) between two strings.
	 * 
	 * Sourced from
	 * https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings
	 * /Levenshtein_distance#Java
	 * 
	 * @param str1 one of the strings
	 * @param str2 other string
	 * @return The similarity score between two strings
	 */
	public static int computeLevenshteinDistance(String str1, String str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
		for (int j = 1; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

		for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1]
                                + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                : 1));
            }
        }

		return distance[str1.length()][str2.length()];
	}
}
