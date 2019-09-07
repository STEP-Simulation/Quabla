package quabla.simulator.numerical_analysis;

public class Interpolation {
	double x_array[];
	double y_array[][];
	private int length;
	private int col; //column

	public Interpolation(double[] x_array, double[] y_array){
		length = x_array.length;
		this.x_array = new double[length];
		// Deep Copy
		System.arraycopy(x_array, 0, this.x_array, 0, length);

		this.y_array = new double[length][1];
		for(int i=0; i<length; i++) {
			this.y_array[i][0] = y_array[i];
		}
	}

	public Interpolation(double[] x_array, double[][] y_array) {
		length = x_array.length;
		this.x_array = new double[length];
		System.arraycopy(x_array, 0, this.x_array, 0, length);

		col = y_array[0].length;
		this.y_array = new double[length][col];
		for(int i=0; i<length; i++) {
			System.arraycopy(y_array[i], 0, this.y_array[i], 0, col);
		}
	}

	public double linearInterp1column(double x) {
		double y;
		int count = 0;

		//xが取得データの範囲外の場合,最大値及び最小値をyの値とする
		if(x <= x_array[0]) {
			y = y_array[0][0];
		}else if(x >= x_array[length-1]) {
			y = y_array[length-1][0];
		}else {
			//xが何群目に所属しているかを調べる
			for(int j=0; j<length; j++) {
				if(x >= x_array[j]) {
					count = j;
				}else {
					break;
				}
			}
			y = y_array[count][0] + (y_array[count+1][0] - y_array[count][0])*(x - x_array[count])/(x_array[count+1] - x_array[count]);
		}
		return y;
	}

	public double[] linearInterpPluralColumns(double x) {
		double[] y = new double[col];
		int count = 0;

		for(int i=0; i<col; i++) {
			if(x <= x_array[0]) {
				y[i] = y_array[0][i];
			}else if(x >= x_array[length - 1]) {
				y[i] = y_array[length - 1][i];
			}else {
				for(int j=0; j<length; j++) {
					if(x >= x_array[j]) {
						count = j;
					}else {
						break;
					}
				}
				y[i] = y_array[count][i] + (y_array[count + 1][i] - y_array[count][i])
						*(x - x_array[count])/(x_array[count + 1] - x_array[count]);
			}
		}
		return y;
	}

}
