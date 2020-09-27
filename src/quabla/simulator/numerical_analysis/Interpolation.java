package quabla.simulator.numerical_analysis;

/**
 * Interpolation is a class for function interpolation.
 * */
public class Interpolation {
	double xArray[];
	double yArray[][];
	private int length;
	private int col; //column

	public Interpolation(double[] xArray, double[] yArray) {
		length = xArray.length;
		this.xArray = new double[length];
		// Deep Copy
		System.arraycopy(xArray, 0, this.xArray, 0, length);

		this.yArray = new double[length][1];
		for (int i = 0; i < length; i++) {
			this.yArray[i][0] = yArray[i];
		}
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
		double y;
		int count = 0;

		//xが取得データの範囲外の場合,最大値及び最小値をyの値とする
		if (x <= xArray[0]) {
			y = yArray[0][0];
		} else if (x >= xArray[length - 1]) {
			y = yArray[length - 1][0];
		} else {
			//xが何群目に所属しているかを調べる
			for (int j = 0; j < length; j++) {
				if (x >= xArray[j]) {
					count = j;
				} else {
					break;
				}
			}
			y = yArray[count][0] + (yArray[count + 1][0] - yArray[count][0]) * (x - xArray[count])
					/ (xArray[count + 1] - xArray[count]);
		}
		return y;
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
