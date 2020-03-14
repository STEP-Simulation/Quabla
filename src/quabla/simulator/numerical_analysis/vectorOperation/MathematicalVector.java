package quabla.simulator.numerical_analysis.vectorOperation;

public class MathematicalVector {

	private double[] vector;
	private int length;

	//TODO 子クラスに3dVectorとかQuaternion作りたいなぁ

	public MathematicalVector(double[] vector) {
		length = vector.length;
		this.vector = new double[length];
		for(int i=0; i<length; i++) {
			this.vector[i] = vector[i];
		}
	}

	public MathematicalVector(double x, double y, double z) {
		vector = new double[3];
		length = 3;
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
	}

	public MathematicalVector(double w, double x, double y, double z) {
		vector = new double[4];
		length = 4;
		vector[0] = w;
		vector[1] = x;
		vector[2] = y;
		vector[3] = z;
	}

	public void set(MathematicalVector vector2) {
		for(int i = 0; i < length; i++) {
			vector[i] = vector2.getValue()[i];
		}
	}

	public void set(double[] vector2) {
		for(int i = 0; i < length; i++) {
			vector[i]  = vector2[i];
		}
	}

	public void set(double x, double y, double z) {
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
	}

	public MathematicalVector multiply(double a) {
		double[] vector2 = new double[length];

		for(int i=0; i<length; i++) {
			vector2[i] = a * this.vector[i] ;
		}

		return new MathematicalVector(vector2);
	}

	public MathematicalVector add(MathematicalVector vector) {
		double[] vector2 = new double[length];

		for(int i=0; i<length; i++) {
			vector2[i] = this.vector[i] + vector.getValue()[i];
		}

		return new MathematicalVector(vector2);
	}

	public MathematicalVector sub(MathematicalVector vector) {
		double[] vector2 = new double[length];

		for(int i=0; i<length; i++) {
			vector2[i] = this.vector[i] - vector.getValue()[i];
		}

		return new MathematicalVector(vector2);
	}


	/**
	 * Makes inner product of vector.
	 * */
	public double dot(MathematicalVector vector) {
		double innerProduct = 0.0;

		for(int i=0; i<length; i++) {
			innerProduct += this.vector[i] * vector.getValue()[i];
		}

		return innerProduct;
	}


	/**
	 * Makes outer product of vector
	 * */
	public MathematicalVector cross(MathematicalVector vector) {
		// TODO length=3以外での例外処理
		double[] vector2 = new double[length];
		double[] vector_double = vector.getValue();

		vector2[0] = this.vector[1]*vector_double[2] - this.vector[2]*vector_double[1];
		vector2[1] = this.vector[2]*vector_double[0] - this.vector[0]*vector_double[2];
		vector2[2] = this.vector[0]*vector_double[1] - this.vector[1]*vector_double[0];

		return new MathematicalVector(vector2);
	}

	public double norm() {
		double norm = 0.0;
		for(int i=0; i<length; i++) {
			norm += Math.pow(vector[i], 2);
		}
		norm = Math.sqrt(norm);

		return norm;
	}

	public double[] getValue() {
		return vector;
	}
}
