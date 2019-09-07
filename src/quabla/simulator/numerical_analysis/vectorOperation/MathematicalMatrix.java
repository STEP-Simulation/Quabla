package quabla.simulator.numerical_analysis.vectorOperation;

public class MathematicalMatrix {

	private double[][] matrix;

	public MathematicalMatrix(double[][] matrix) {
		this.matrix = matrix;
	}


	public MathematicalVector dot(MathematicalVector vector){
		double[] vector2 = vector.getValue();
		double[] vector3 = new double[matrix.length];

		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<matrix.length; j++) {
				vector3[i] += matrix[i][j] * vector2[j];
			}
		}

		return new MathematicalVector(vector3);
	}

	public MathematicalMatrix transpose() {
		int length = matrix.length;
		double[][] matrix_transpose = new double[length][length];

		for(int i=0; i<length; i++) {
			for(int j=0; j<length; j++) {
				matrix_transpose[i][j] = matrix[j][i];
			}
		}

		return new MathematicalMatrix(matrix_transpose);
	}

	public double[][] getDouble(){
		return matrix;
	}
}


