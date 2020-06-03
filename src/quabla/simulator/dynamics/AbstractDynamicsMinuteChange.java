package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public abstract class AbstractDynamicsMinuteChange {

	public abstract AbstractDynamicsMinuteChange multiple(double a);
	public abstract double[] toDouble();
	public abstract AbstractDynamicsMinuteChange generate(double[] dx);
	public abstract DynamicsMinuteChangeParachute toDeltaPara();

	public abstract MathematicalVector getDeltaPosENU();
	public abstract MathematicalVector getDeltaVelENU();
	public abstract MathematicalVector getDeltaOmegaBODY();
	public abstract MathematicalVector getDeltaQuat();
	public abstract double getDeltaVelDescent();
}
