package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChangeTrajectory{

	private MathematicalVector deltaPos_ENU = new MathematicalVector(new double[3]);
	private MathematicalVector deltaVel_ENU = new MathematicalVector(new double[3]);
	private MathematicalVector deltaOmega_Body = new MathematicalVector(new double[3]);
	private MathematicalVector deltaQuat = new MathematicalVector(new double[4]);


	public void setDeltaPos_ENU(MathematicalVector deltaPos_ENU) {
		this.deltaPos_ENU = deltaPos_ENU;
	}

	public void setDeltaVelENU(MathematicalVector deltaVelENU) {
		this.deltaVel_ENU = deltaVelENU;
	}

	public void setDeltaOmegaBODY(MathematicalVector deltaOmegaBODY) {
		this.deltaOmega_Body = deltaOmegaBODY;
	}

	public void setDeltaQuat(MathematicalVector deltaQuat) {
		this.deltaQuat = deltaQuat;
	}

	public MathematicalVector getDeltaPos_ENU() {
		return deltaPos_ENU;
	}

	public MathematicalVector getDeltaVel_ENU() {
		return deltaVel_ENU;
	}

	public MathematicalVector getDeltaOmega_Body() {
		return deltaOmega_Body;
	}

	public MathematicalVector getDeltaQuat() {
		return deltaQuat;
	}

	public DynamicsMinuteChangeParachute getDelatPar() {
		return new DynamicsMinuteChangeParachute(deltaPos_ENU, deltaVel_ENU.getValue()[2]);
	}

}
