package quabla.simulator.numerical_analysis;

public class ArrayAnalysis {

	double[] array1d;
	double[][] array2d;

	private double valueMax, valueMin;
	private int indexMaxValue, indexMinValue;

	int length;

	public ArrayAnalysis(double[] array) {
		length = array.length;

		array1d = new double[length];
		System.arraycopy(array, 0, array1d, 0, length);
	}

	public void calculateMaxValue() {
		valueMax = array1d[0];
		indexMaxValue = 0;

		for(int i = 0; i < length; i++) {
			if(valueMax <= array1d[i]) {
				valueMax = array1d[i];
				indexMaxValue = i;
			}
		}
	}

	public void calculateMinimumValue() {
		valueMin = array1d[0];
		indexMinValue= 0;

		for(int i = 0; i < length; i++) {
			if(valueMin >= array1d[i]) {
				valueMin = array1d[i];
				indexMinValue = i;
			}
		}
	}

	public double getMaxValue() {
		return valueMax;
	}

	public double getMinValue() {
		return valueMin;
	}

	public int getIndexMaxValue() {
		return indexMaxValue;
	}

	public int getIndexMinimumValue() {
		return indexMinValue;
	}

}
