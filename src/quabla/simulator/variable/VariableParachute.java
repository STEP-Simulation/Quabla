package quabla.simulator.variable;

import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.rocket.wind.AbstractWind;

public class VariableParachute extends AbstractVariable{

	private AbstractWind wind;

	private double time;
	private double h;

	private MathematicalVector posENU = new MathematicalVector(MathematicalVector.ZERO);
	private MathematicalVector velENU = new MathematicalVector(MathematicalVector.ZERO);
	private double velDescent;
	private double[] windENU = new double[2];

	/**
	 * @param variable 開傘時のvariable
	 * */
	/*
	public VariableParachute(VariableTrajectory variable) {
		time = variable.getTime();
		posENU.set(variable.getPosENU());
		velDescent = variable.getVelDescent();
	}*/

	public VariableParachute(VariableParachute variable) {
		wind = variable.wind;
		time = variable.getTime();
		posENU.set(variable.getPosENU().toDouble());
		velDescent = variable.getVelDescent();
	}
	
	public VariableParachute(Rocket rocket, double dt) {
		time = 0.0;
		posENU = new MathematicalVector(0.0, 0.0, 0.0);
		velENU = new MathematicalVector(0.0, 0.0, 0.0);
		velDescent = 0.0;
		wind = rocket.wind;
//		h = rocket.dt;
		h = dt;
	}

	public void set(VariableParachute variable) {
		posENU.set(variable.getPosENU().toDouble());
		velDescent = variable.getVelDescent();
	}

	public void set(LoggerVariable logdata, int index) {
		posENU = new MathematicalVector(logdata.getPosENUlog(index));
		velDescent = logdata.getVelENUlog(index)[2];
	}

	@Override
	public void setVariable(double time, double[] x) {
		this.time = time;
		this.posENU.set(x[0], x[1], x[2]);
		this.velDescent = x[3];
	}

	public void setTime(double time) {
		this.time = time;
	}

	private void setPosENU(MathematicalVector posENU) {
		this.posENU.set(posENU.toDouble());
	}

	public void setVelDescent(double velDescent) {
		this.velDescent = velDescent;
	}

	@Override
	public double getTime() {
		return time;
	}

	public MathematicalVector getPosENU() {
		return posENU;
	}

	@Override
	public MathematicalVector getVelENU() {
		return velENU;
	}

	@Override
	public MathematicalVector getOmegaBODY() {
		return MathematicalVector.ZERO;// Parachute開傘は回転なし
	}

	@Override
	public MathematicalVector getQuat() {
		return new MathematicalVector(0.0, 0.0, 0.0, 0.0);
	}

	@Override
	public double getAltitude() {
		return posENU.toDouble(2);
	}

	@Override
	public double getVelDescent() {
		return velDescent;
	}

	@Override
	public double getDistanceLowerLug() {
		return 0.0;// Parachute開傘時は必ずランチクリアしてるので0を返す
	}

	public VariableParachute getClone() {
		VariableParachute variable2 = new VariableParachute(this);
		return variable2;
	}

	@Override
	public double[] toDouble() {
		double[] x = new double[4];
		System.arraycopy(posENU.toDouble(), 0, x, 0, 3);
		x[3] = velDescent;
		return x;
	}

	public void update(double time, AbstractDynamicsMinuteChange delta) {
//		this.time = time;
		setTime(time);
//		posENU = posENU.add(delta.getDeltaPosENU().multiply(h));
		setPosENU(posENU.add(delta.getDeltaPosENU().multiply(h)));
//		velDescent = velDescent + delta.getDeltaVelDescent() * h;
		setVelDescent(velDescent + delta.getDeltaVelDescent() * h);
		double altitude = getAltitude();
		System.arraycopy(wind.getWindENU(altitude), 0, windENU, 0, 2);
		velENU.set(windENU[0], windENU[1], velDescent);
	}
}
