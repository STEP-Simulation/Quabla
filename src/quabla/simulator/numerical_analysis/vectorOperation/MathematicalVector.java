package quabla.simulator.numerical_analysis.vectorOperation;

public class MathematicalVector implements Cloneable {

	// Constant Vector
	public static final MathematicalVector ZERO = new MathematicalVector(0.0, 0.0, 0.0);

	private double[] vector;
	private int length;

	//TODO 子クラスに3dVectorとかQuaternion作りたいなぁ

	public MathematicalVector(double[] vector) {
		length = vector.length;
		this.vector = new double[length];
		System.arraycopy(vector, 0, this.vector, 0, length);
	}

	public MathematicalVector(MathematicalVector vector) {
		length = vector.toDouble().length;
		this.vector = new double[length];
		System.arraycopy(vector.toDouble(), 0, this.vector, 0, length);
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

	public MathematicalVector() {
		this(0.0, 0.0, 0.0);
		length = 3;
	}

	public void set(MathematicalVector vector2) {
		System.arraycopy(vector2.toDouble(), 0, vector, 0, length);
	}

	public void set(double[] vector2) {
		System.arraycopy(vector2, 0, vector, 0, length);
	}

	public void set(double x, double y, double z) {
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
	}

	public void set(double x, double y, double z, double w) {
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
		vector[3] = w;
	}

	public MathematicalVector multiply(double a) {
		double[] vectorNew = new double[length];
		int i = 0;
		for(double vi : vector) {
			vectorNew[i] = a * vi;
			i ++;
		}
		return new MathematicalVector(vectorNew);
	}

	public MathematicalVector add(MathematicalVector vector) {
		double[] vectorNew = new double[length];
		for(int i=0; i<length; i++) {
			vectorNew[i] = this.vector[i] + vector.toDouble(i);
		}

		return new MathematicalVector(vectorNew);
	}

	public MathematicalVector sub(MathematicalVector vector) {
		double[] vectorNew = new double[length];
		for(int i=0; i<length; i++) {
			vectorNew[i] = this.vector[i] - vector.toDouble(i);
		}

		return new MathematicalVector(vectorNew);
	}


	/**
	 * Makes inner product of vector.
	 * */
	public double dot(MathematicalVector vector) {
		double innerProduct = 0.0;

		for(int i=0; i<length; i++) {
			innerProduct += this.vector[i] * vector.toDouble(i);
		}

		return innerProduct;
	}


	/**
	 * Makes outer product of vector
	 * */
	public MathematicalVector cross(MathematicalVector vector) {
		double[] vectorNew = new double[length];
		// TODO length=3以外での例外処理
		vectorNew[0] = this.vector[1]*vector.toDouble(2) - this.vector[2]*vector.toDouble(1);
		vectorNew[1] = this.vector[2]*vector.toDouble(0) - this.vector[0]*vector.toDouble(2);
		vectorNew[2] = this.vector[0]*vector.toDouble(1)- this.vector[1]*vector.toDouble(0);

		return new MathematicalVector(vectorNew);
	}

	public double norm() {
		double norm = 0.0;
		for(int i=0; i<length; i++) {
			norm += Math.pow(vector[i], 2);
		}
		norm = Math.sqrt(norm);

		return norm;
	}

	public void normalize() {

		double norm = this.norm();

		for (int i = 0; i < vector.length; i++) {
			vector[i] /= norm;
		}

	}

	public double[] toDouble() {
		return vector;
	}

	public double toDouble(int i) {
		return vector[i];
	}

	@Override
	public MathematicalVector clone(){

		try {

			MathematicalVector clone = (MathematicalVector) super.clone();
			clone.vector = this.vector.clone();
			return clone;
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			throw new InternalError(e);
		}
	}
}
