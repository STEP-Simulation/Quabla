package quabla.simulator.variable;

import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public abstract class AbstractVariable {

	public abstract AbstractVariable getClone();
	public abstract double[] toDouble();
	public abstract void update(double time, AbstractDynamicsMinuteChange dleta);

	public abstract void setVariable(double time, double[] x);

	public abstract double getTime();
	public abstract MathematicalVector getVelENU();
	public abstract MathematicalVector getOmegaBODY();
	public abstract MathematicalVector getQuat();
	public abstract double getAltitude();
	public abstract double getDistanceLowerLug();
	public abstract double getVelDescent();
}
