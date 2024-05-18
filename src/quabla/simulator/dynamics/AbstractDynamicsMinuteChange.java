package quabla.simulator.dynamics;

public abstract class AbstractDynamicsMinuteChange {

	public abstract void set(double[] dx);
	public abstract AbstractDynamicsMinuteChange multiple(double a);
	public abstract double[] toDouble();
	public abstract DynamicsMinuteChangeParachute toDeltaPara();

	public abstract double[] getDeltaPosNED();
	public abstract double[] getDeltaVelNED();
	public abstract double[] getDeltaOmegaBODY();
	public abstract double[] getDeltaQuat();
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
