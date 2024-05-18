package quabla.simulator.variable;

// import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public abstract class AbstractVariable {

	public abstract double[] toDouble();
	// public abstract void update(double time, AbstractDynamicsMinuteChange delta);
	public abstract void update(double time, double[] delta);

	public abstract void setVariable(double time, double[] x);

	public abstract double getTime();
	public abstract MathematicalVector getPosNED();
	public abstract MathematicalVector getVelBODY();
	public abstract MathematicalVector getOmegaBODY();
	public abstract MathematicalVector getQuat();
	public abstract double getAltitude();
	public abstract double getDistanceLowerLug();
	public abstract double getVelDescent();

	@Override
	public AbstractVariable clone() {

		try {
			
			AbstractVariable clone = (AbstractVariable) super.clone();
			return clone;

		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			throw new InternalError(e);
		}
	}
}
