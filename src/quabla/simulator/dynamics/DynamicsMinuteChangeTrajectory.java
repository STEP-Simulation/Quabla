package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChangeTrajectory extends AbstractDynamicsMinuteChange{

	private MathematicalVector deltaPosENU = new MathematicalVector(new double[3]);
	private MathematicalVector deltaVelENU = new MathematicalVector(new double[3]);
	private MathematicalVector deltaOmegaBODY = new MathematicalVector(new double[3]);
	private MathematicalVector deltaQuat = new MathematicalVector(new double[4]);

	public void setDelta(double[] dx) {
		this.deltaPosENU.set(dx[0], dx[1], dx[2]);
		this.deltaVelENU.set(dx[3], dx[4], dx[5]);
		this.deltaOmegaBODY.set(dx[6], dx[7], dx[8]);
		this.deltaQuat.set(dx[9], dx[10], dx[11], dx[12]);
	}

	public void setDeltaPosENU(MathematicalVector deltaPosENU) {
		this.deltaPosENU = deltaPosENU;
	}

	public void setDeltaVelENU(MathematicalVector deltaVelENU) {
		this.deltaVelENU = deltaVelENU;
	}

	public void setDeltaOmegaBODY(MathematicalVector deltaOmegaBODY) {
		this.deltaOmegaBODY = deltaOmegaBODY;
	}

	public void setDeltaQuat(MathematicalVector deltaQuat) {
		this.deltaQuat = deltaQuat;
	}

	@Override
	public DynamicsMinuteChangeTrajectory multiple(double a) {
		DynamicsMinuteChangeTrajectory dmct = new DynamicsMinuteChangeTrajectory();
		dmct.setDeltaPosENU(this.deltaPosENU.multiply(a));
		dmct.setDeltaVelENU(this.deltaVelENU.multiply(a));
		dmct.setDeltaOmegaBODY(this.deltaOmegaBODY.multiply(a));
		dmct.setDeltaQuat(this.deltaQuat.multiply(a));
		return dmct;
	}

	@Override
	public double[] toDouble() {
		double[] dx = new double[13];
		System.arraycopy(deltaPosENU.toDouble(), 0, dx, 0, 3);
		System.arraycopy(deltaVelENU.toDouble(), 0, dx, 3, 3);
		System.arraycopy(deltaOmegaBODY.toDouble(), 0, dx, 6, 3);
		System.arraycopy(deltaQuat.toDouble(), 0, dx, 9, 4);
		return dx;
	}

	@Override
	public DynamicsMinuteChangeParachute toDeltaPara() {
		return new DynamicsMinuteChangeParachute(deltaPosENU, deltaVelENU.toDouble(2));
	}

	@Override
	/**
	 * 受け取ったDouble値のdxから新しいDynamicsMinuteChangeTrajectoryを生成
	 * */
	public DynamicsMinuteChangeTrajectory generate(double[] dx) {
		DynamicsMinuteChangeTrajectory dmct = new DynamicsMinuteChangeTrajectory();
		dmct.setDelta(dx);
		return dmct;
	}

	@Override
	public MathematicalVector getDeltaPosENU() {
		return deltaPosENU;
	}

	@Override
	public MathematicalVector getDeltaVelENU() {
		return deltaVelENU;
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
		return deltaVelENU.toDouble(2);
	}

	public DynamicsMinuteChangeParachute getDelatPar() {
		return new DynamicsMinuteChangeParachute(deltaPosENU, deltaVelENU.toDouble(2));
	}

}
