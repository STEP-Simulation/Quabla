package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChangeParachute extends AbstractDynamicsMinuteChange{

	private MathematicalVector deltaPosENU = new MathematicalVector(MathematicalVector.ZERO);
	private double deltaVelDescent;

	public DynamicsMinuteChangeParachute() {

	}

	public DynamicsMinuteChangeParachute(MathematicalVector deltaPosENU, double deltaVelDescent) {
		this.deltaPosENU = deltaPosENU;
		this.deltaVelDescent = deltaVelDescent;
	}

	public void setDelta(double[] dx) {
		this.deltaPosENU.set(dx[0], dx[1], dx[2]);
		this.deltaVelDescent = dx[3];
	}

	public void setDeltaPosENU(MathematicalVector deltaPosENU) {
		this.deltaPosENU = deltaPosENU;
	}

	public void setDeltaVelDescent(double deltaVelDescent) {
		this.deltaVelDescent = deltaVelDescent;
	}

	@Override
	public DynamicsMinuteChangeParachute multiple(double a) {
		DynamicsMinuteChangeParachute dmcp = new DynamicsMinuteChangeParachute();
		dmcp.setDeltaPosENU(this.deltaPosENU.multiply(a));
		dmcp.setDeltaVelDescent(this.deltaVelDescent * a);
		return dmcp;
	}

	@Override
	public double[] toDouble() {
		double[] dx = new double[4];
		System.arraycopy(deltaPosENU.toDouble(), 0, dx, 0, 3);
		dx[3] = deltaVelDescent;
		return dx;
	}

	@Override
	public DynamicsMinuteChangeParachute generate(double[] dx) {
		DynamicsMinuteChangeParachute dmcp = new DynamicsMinuteChangeParachute();
		dmcp.setDelta(dx);
		return dmcp;
	}

	@Override
	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosENU, deltaVelDescent);
	}

	@Override
	public MathematicalVector getDeltaPosENU() {
		return deltaPosENU;
	}
	@Override
	public MathematicalVector getDeltaVelENU() {
		return  MathematicalVector.ZERO;
	}

	@Override
	public MathematicalVector getDeltaOmegaBODY() {
		return MathematicalVector.ZERO;
	}

	@Override
	public MathematicalVector getDeltaQuat() {
		return new MathematicalVector(0.0, 0.0, 0.0, 0.0);
	}

	@Override
	public double getDeltaVelDescent() {
		return deltaVelDescent;
	}
}
