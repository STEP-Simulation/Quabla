package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChangeParachute extends AbstractDynamicsMinuteChange implements Cloneable {

	private MathematicalVector deltaPosNED = new MathematicalVector(MathematicalVector.ZERO);
	private double deltaVelDescent;

	public DynamicsMinuteChangeParachute() {

	}

	public DynamicsMinuteChangeParachute(MathematicalVector deltaPosNED, double deltaVelDescent) {
		this.deltaPosNED = deltaPosNED;
		this.deltaVelDescent = deltaVelDescent;
	}

	@Override
	public void set(double[] dx) {
		this.deltaPosNED.set(dx[0], dx[1], dx[2]);
		this.deltaVelDescent = dx[3];
	}

	public void setDeltaPosNED(MathematicalVector deltaPosNED) {
		this.deltaPosNED = deltaPosNED;
	}

	public void setDeltaPosNED(double[] deltaPosNED) {
		this.deltaPosNED = new MathematicalVector(deltaPosNED);
	}

	public void setDeltaVelDescent(double deltaVelDescent) {
		this.deltaVelDescent = deltaVelDescent;
	}

	@Override
	public DynamicsMinuteChangeParachute multiple(double a) {
		// DynamicsMinuteChangeParachute dmcp = new DynamicsMinuteChangeParachute();
		DynamicsMinuteChangeParachute dmcp = this.clone();
		dmcp.setDeltaPosNED(this.deltaPosNED.multiply(a));
		dmcp.setDeltaVelDescent(this.deltaVelDescent * a);
		// return dmcp;
		
		// DynamicsMinuteChangeParachute dmcp = this.clone();
		// double[] dx = this.toDouble().clone();
		// for (int i = 0; i < dx.length; i++) {
		// 	dx[i] *= a;
		// }
		// dmcp.set(this.toDouble());

		return dmcp;

	}

	@Override
	public double[] toDouble() {
		double[] dx = new double[4];
		System.arraycopy(deltaPosNED.toDouble(), 0, dx, 0, 3);
		dx[3] = deltaVelDescent;
		return dx;
	}

	@Override
	public DynamicsMinuteChangeParachute clone() {

		DynamicsMinuteChangeParachute clone = (DynamicsMinuteChangeParachute) super.clone();
		clone.deltaPosNED = this.deltaPosNED.clone();

		return clone;
 	}

	@Override
	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelDescent);
	}

	@Override
	public MathematicalVector getDeltaPosNED() {
		return deltaPosNED;
	}
	@Override
	public MathematicalVector getDeltaVelNED() {
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
