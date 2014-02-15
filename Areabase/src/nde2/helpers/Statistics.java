package nde2.helpers;

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
}
