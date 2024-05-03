package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChangeTrajectory extends AbstractDynamicsMinuteChange implements Cloneable {

	private MathematicalVector deltaPosNED = new MathematicalVector(new double[3]);
	private MathematicalVector deltaVelNED = new MathematicalVector(new double[3]);
	private MathematicalVector deltaOmegaBODY = new MathematicalVector(new double[3]);
	private MathematicalVector deltaQuat = new MathematicalVector(new double[4]);

	@Override
	public void set(double[] dx) {
		this.deltaPosNED.set(dx[0], dx[1], dx[2]);
		this.deltaVelNED.set(dx[3], dx[4], dx[5]);
		this.deltaOmegaBODY.set(dx[6], dx[7], dx[8]);
		this.deltaQuat.set(dx[9], dx[10], dx[11], dx[12]);
	}

	public void setDeltaPosNED(MathematicalVector deltaPosENU) {
		this.deltaPosNED = deltaPosENU;
	}

	public void setDeltaVelNED(MathematicalVector deltaVelNED) {
		this.deltaVelNED = deltaVelNED;
	}

	public void setDeltaOmegaBODY(MathematicalVector deltaOmegaBODY) {
		this.deltaOmegaBODY = deltaOmegaBODY;
	}

	public void setDeltaQuat(MathematicalVector deltaQuat) {
		this.deltaQuat = deltaQuat;
	}

	@Override
	public DynamicsMinuteChangeTrajectory multiple(double a) {
		
		// DynamicsMinuteChangeTrajectory dmct = new DynamicsMinuteChangeTrajectory();
		DynamicsMinuteChangeTrajectory dmct = this.clone();
		dmct.setDeltaPosNED(this.deltaPosNED.multiply(a));
		dmct.setDeltaVelNED(this.deltaVelNED.multiply(a));
		dmct.setDeltaOmegaBODY(this.deltaOmegaBODY.multiply(a));
		dmct.setDeltaQuat(this.deltaQuat.multiply(a));
		// return dmct;

		// DynamicsMinuteChangeTrajectory dmct = this.clone();
		// double[] dx = this.toDouble().clone();
		// for (int i = 0; i < dx.length; i++) {
		// 	dx[i] *= a;
		// }
		// dmct.set(dx);

		return dmct;
	}

	@Override
	public double[] toDouble() {
		double[] dx = new double[13];
		System.arraycopy(deltaPosNED.toDouble(), 0, dx, 0, 3);
		System.arraycopy(deltaVelNED.toDouble(), 0, dx, 3, 3);
		System.arraycopy(deltaOmegaBODY.toDouble(), 0, dx, 6, 3);
		System.arraycopy(deltaQuat.toDouble(), 0, dx, 9, 4);
		return dx;
	}

	@Override
	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelNED.toDouble(2));
	}

	@Override
	public DynamicsMinuteChangeTrajectory clone() {

		DynamicsMinuteChangeTrajectory clone = (DynamicsMinuteChangeTrajectory) super.clone();
		clone.deltaPosNED    = this.deltaPosNED.clone();
		clone.deltaVelNED    = this.deltaVelNED.clone();
		clone.deltaOmegaBODY = this.deltaOmegaBODY.clone();
		clone.deltaQuat      = this.deltaQuat.clone();

		return clone;

	}

	@Override
	public MathematicalVector getDeltaPosNED() {
		return deltaPosNED;
	}

	@Override
	public MathematicalVector getDeltaVelNED() {
		return deltaVelNED;
	}

	@Override
	public MathematicalVector getDeltaOmegaBODY() {
		return deltaOmegaBODY;
	}

	@Override
	public MathematicalVector getDeltaQuat() {
		return deltaQuat;
	}

	@Override
	public double getDeltaVelDescent() {
		return deltaVelNED.toDouble(2);
	}

	public DynamicsMinuteChangeParachute getDelatPar() {
		return new DynamicsMinuteChangeParachute(deltaPosNED, deltaVelNED.toDouble(2));
	}

}
