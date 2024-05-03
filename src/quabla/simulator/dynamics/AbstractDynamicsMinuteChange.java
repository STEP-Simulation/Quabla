package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public abstract class AbstractDynamicsMinuteChange {

	public abstract void set(double[] dx);
	public abstract AbstractDynamicsMinuteChange multiple(double a);
	public abstract double[] toDouble();
	public abstract DynamicsMinuteChangeParachute toDeltaPara();

	public abstract MathematicalVector getDeltaPosNED();
	public abstract MathematicalVector getDeltaVelNED();
	public abstract MathematicalVector getDeltaOmegaBODY();
	public abstract MathematicalVector getDeltaQuat();
	public abstract double getDeltaVelDescent();

	@Override
	public AbstractDynamicsMinuteChange clone() {

		try {
			AbstractDynamicsMinuteChange clone = (AbstractDynamicsMinuteChange) super.clone();
			return clone;

		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			throw new InternalError(e);
		}
	}
}
