package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChange {

	public MathematicalVector deltaPos_ENU = new MathematicalVector(new double[3]);
	public MathematicalVector deltaVel_ENU = new MathematicalVector(new double[3]);
	public MathematicalVector deltaOmega_Body = new MathematicalVector(new double[3]);
	public MathematicalVector deltaQuat = new MathematicalVector(new double[4]);


	public void setDeltaPos_ENU(MathematicalVector deltaPos_ENU) {
		this.deltaPos_ENU = deltaPos_ENU;
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

}
