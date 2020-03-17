package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsMinuteChangeParachute{

	private MathematicalVector deltaPosENU;
	private double deltaVelDescent;

	public DynamicsMinuteChangeParachute() {

	}

	public DynamicsMinuteChangeParachute(MathematicalVector deltaPosENU, double deltaVelDescent) {
		this.deltaPosENU = deltaPosENU;
		this.deltaVelDescent = deltaVelDescent;
	}

	public void setDeltaPosENU(MathematicalVector deltaPosENU) {
		this.deltaPosENU = deltaPosENU;
	}

	public void setDeltaVelDescent(double deltaVelDescent) {
		this.deltaVelDescent = deltaVelDescent;
	}

	public MathematicalVector getDeltaPosENU() {
		return deltaPosENU;
	}

	public double getDeltaVelDescent() {
		return deltaVelDescent;
	}
}
