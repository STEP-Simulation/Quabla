package quabla.simulator.numerical_analysis;

import java.util.Arrays;

/**
 * Interpolation is a class for function interpolation.
 * */
public class Interpolation {
	double xArray[];
	double yArray1d[];
	double yArray[][];
	private int length;
	private int col; //column

	public Interpolation(double[] xArray, double[] yArray) {
		
		length = xArray.length;
		this.xArray   = new double[length];
		this.yArray1d = new double[length];
		// Deep Copy
		System.arraycopy(xArray, 0, this.xArray, 0, length);
		System.arraycopy(yArray, 0, this.yArray1d, 0, length);

	}

	public Interpolation(double[] xArray, double[][] yArray) {
		length = xArray.length;
		this.xArray = new double[length];
		System.arraycopy(xArray, 0, this.xArray, 0, length);

		col = yArray[0].length;
		this.yArray = new double[length][col];
		for (int i = 0; i < length; i++) {
			System.arraycopy(yArray[i], 0, this.yArray[i], 0, col);
		}
	}

	/** 1次元の線形補間 */
	public double linearInterp1column(double x) {
		int count = 0;

		if (x <= xArray[0]) {
			return yArray1d[0];
		} else if (x >= xArray[length - 1]) {
			return yArray1d[length - 1];
		} else {
			count = Arrays.binarySearch(xArray, x);
			count = Math.min(Math.abs(count), Math.abs(~ count)) - 1;

			return yArray1d[count]
			    + (yArray1d[count + 1] - yArray1d[count]) * (x - xArray[count]) / (xArray[count + 1] - xArray[count]);
		}
	}

	/** 他次元の線形補間 */
	public double[] linearInterpPluralColumns(double x) {
		double[] y = new double[col];
		int count = 0;

		for (int i = 0; i < col; i++) {
			if (x <= xArray[0]) {
				y[i] = yArray[0][i];
			} else if (x >= xArray[length - 1]) {
				y[i] = yArray[length - 1][i];
			} else {
				for (int j = 0; j < length; j++) {
					if (x >= xArray[j]) {
						count = j;
					} else {
						break;
					}
				}
				y[i] = yArray[count][i] + (yArray[count + 1][i] - yArray[count][i])
						* (x - xArray[count]) / (xArray[count + 1] - xArray[count]);
			}
		}
		return y;
	}

}
