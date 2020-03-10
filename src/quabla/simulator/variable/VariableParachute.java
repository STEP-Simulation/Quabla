package quabla.simulator.variable;

import quabla.parameter.InputParam;
import quabla.simulator.Wind;
import quabla.simulator.dynamics.DynamicsMinuteChangeParachute;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class VariableParachute {

	private Wind wind;

	private double time;
	private double h;

	private MathematicalVector posENU;
	private MathematicalVector velENU;
	private double velDescent;
	private double[] windENU = new double[2];

	/**
	 * @param variable 開傘時のvariable
	 * */
	public VariableParachute(Variable variable) {
		time = variable.getTime();
		posENU.set(variable.getPos_ENU());
		velDescent = variable.getVelDescet();
	}

	public VariableParachute(VariableParachute variable) {
		time = variable.getTime();
		posENU = variable.getPosENU();
		velDescent = variable.getVelDescent();
	}

	public VariableParachute(InputParam spec) {
		time = 0.0;
		posENU = new MathematicalVector(0.0, 0.0, 0.0);
		velENU = new MathematicalVector(0.0, 0.0, 0.0);
		velDescent = 0.0;
		wind = new Wind(spec);
		h = spec.dt;
	}

	public void set(VariableParachute variable) {
		posENU = variable.getPosENU();
		velDescent = variable.getVelDescent();
	}

	public void setTime(double time) {
		this.time = time;
	}

	public void setPosENU(MathematicalVector posENU) {
		this.posENU = posENU;
	}

	public void setVelDescent(double velDescent) {
		this.velDescent = velDescent;
	}

	public double getTime() {
		return time;
	}

	public MathematicalVector getPosENU() {
		return posENU;
	}

	public MathematicalVector getVelENU() {
		return velENU;
	}
	//TODO オーバーライド

	public double getAltitude() {
		return posENU.getValue()[2];
	}

	public double getVelDescent() {
		return velDescent;
	}

	public VariableParachute getClone() {
		VariableParachute variable2 = new VariableParachute(this);
		return variable2;
	}

	public void update(double time, DynamicsMinuteChangeParachute delta) {
		this.time = time;
		posENU = posENU.add(delta.getDeltaPosENU().multiply(h));
		velDescent = velDescent + delta.getDeltaVelDescent() * h;
		double altitude = getAltitude();
		System.arraycopy(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)), 0, windENU, 0, 2);
		velENU.set(windENU[0], windENU[1], velDescent);
	}
}
