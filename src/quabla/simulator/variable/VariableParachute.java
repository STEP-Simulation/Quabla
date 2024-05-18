package quabla.simulator.variable;

import quabla.simulator.Coordinate;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Payload;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.rocket.wind.AbstractWind;

public class VariableParachute extends AbstractVariable implements Cloneable{

	private AbstractWind wind;

	private double time;

	private MathematicalVector posNED = new MathematicalVector(MathematicalVector.ZERO);
	private double velDescent;

	/**
	 * @param variable 開傘時のvariable
	 * */
	public VariableParachute(VariableParachute variable) {
		wind = variable.wind;
		time = variable.getTime();
		posNED.set(variable.getPosNED().toDouble());
		velDescent = variable.getVelDescent();
	}
	
	public VariableParachute(Rocket rocket) {
		time = 0.0;
		posNED = new MathematicalVector(0.0, 0.0, 0.0);
		velDescent = 0.0;
		wind = rocket.wind;
	}

	public VariableParachute(Payload payload) {

		time = 0.0;
		posNED = new MathematicalVector(0.0, 0.0, 0.0);
		velDescent = 0.0;
		
	}

	public void set(VariableParachute variable) {
		posNED.set(variable.getPosNED().toDouble());
		velDescent = variable.getVelDescent();
	}

	public void set(LoggerVariable logdata, int index) {
		time = logdata.getTime(index);
		posNED = new MathematicalVector(logdata.getPosNEDlog(index));
		double[][] dcmBODY2NED = Coordinate.getDcmBODY2NEDfromDcmNED2BODY(Coordinate.getDcmNED2BODYfromQuat(logdata.getQuatLog(index)));
		double[] velNED = Coordinate.transVector(dcmBODY2NED, logdata.getVelBODYArray()[index]);
		velDescent = velNED[2];
	}

	@Override
	public void setVariable(double time, double[] x) {
		this.time = time;
		this.posNED.set(x[0], x[1], x[2]);
		this.velDescent = x[3];
	}

	public void setTime(double time) {
		this.time = time;
	}

	private void setPosNED(MathematicalVector posNED) {
		this.posNED.set(posNED.toDouble());
	}

	public void setVelDescent(double velDescent) {
		this.velDescent = velDescent;
	}

	@Override
	public double getTime() {
		return time;
	}

	@Override
	public MathematicalVector getPosNED() {
		return posNED;
	}

	@Override
	public MathematicalVector getVelBODY() {
		return null;
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
		return - posNED.toDouble(2);
	}

	@Override
	public double getVelDescent() {
		return velDescent;
	}

	@Override
	public double getDistanceLowerLug() {
		return 0.0;// Parachute開傘時は必ずランチクリアしてるので0を返す
	}

	@Override
	public VariableParachute clone() {

		VariableParachute clone = (VariableParachute) super.clone();
		clone.posNED  = this.posNED.clone();

		return clone;
	}

	@Override
	public double[] toDouble() {
		double[] x = new double[4];
		System.arraycopy(posNED.toDouble(), 0, x, 0, 3);
		x[3] = velDescent;
		return x;
	}

	public void update(double timeStep, AbstractDynamicsMinuteChange delta) {

		AbstractDynamicsMinuteChange delta2 = delta.multiple(timeStep);
		setTime(time + timeStep);
		setPosNED(posNED.add(delta2.getDeltaPosNED()));
		setVelDescent(velDescent + delta2.getDeltaVelDescent());

	}
}
