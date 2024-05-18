package quabla.simulator.variable;

import quabla.simulator.Coordinate;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;

/**
 * Variable stores variables of rocket when it flights.
 *
 * <p> When you set initial variables, use {@link #setInitialVariable()}.
 * You are not able to set initial variables at making instance of Variable
 * */
public class VariableTrajectory extends AbstractVariable implements Cloneable {

	private Rocket rocket;

	private double time;

	//Main parameters
	private MathematicalVector posNED;
	private MathematicalVector velBODY;
	private MathematicalVector omegaBODY;
	private MathematicalVector quat;

	private double[] quat0;
	private double[] posNED0;

	public VariableTrajectory(Rocket rocket) {
		this.rocket = rocket;
		setInitialVariable();
	}

	/**
	 * set Initial Parameters
	 * */ 
	public void setInitialVariable() {
		time = 0.0;

		//Initial Euler Angle
		double azimuth0, elevation0, roll0;
		// ENU (East-North-Up)
		// azimuth0   = Coordinate.deg2rad((- rocket.azimuthLauncher + 90.0) + rocket.magneticDec);
		// elevation0 = - Math.abs(Coordinate.deg2rad(rocket.elevationLauncher));
		// roll0      = Math.PI;
		// NED (North-East-Down)
		azimuth0   = Coordinate.deg2rad((rocket.azimuthLauncher) - rocket.magneticDec);
		elevation0 = Math.abs(Coordinate.deg2rad(rocket.elevationLauncher));
		roll0      = 0.0;

		//Initial Attitude with Quaternion
		quat0 = Coordinate.getQuatFromEuler(azimuth0, elevation0, roll0);
		quat  = new MathematicalVector(quat0);
		double[][] dcm = Coordinate.getDcmBODY2NEDfromDcmNED2BODY(Coordinate.getDcmNED2BODYfromQuat(quat0));
		
		//Initial Position_ENU
		double[] pos0BODY = {rocket.L - rocket.lcgBef, 0., 0.};
		posNED0 = Coordinate.transVector(dcm, pos0BODY);
		posNED = new MathematicalVector(posNED0);

		//Initial Velocity
		velBODY = new MathematicalVector(0.0, 0.0, 0.0);

		//Initial Anguler Velocity
		omegaBODY = new MathematicalVector(0.0, 0.0, 0.0);

	}

	public void setVariable(double time, MathematicalVector posNED, MathematicalVector velNED, MathematicalVector omegaBODY, MathematicalVector quat) {
		this.time = time;
		this.posNED.set(posNED.toDouble());
		this.velBODY.set(velNED.toDouble());
		this.omegaBODY.set(omegaBODY.toDouble());
		this.quat.set(quat.toDouble());
	}

	public void setVariable(double time, double[] x) {
		this.time = time;
		this.posNED.set(x[0], x[1], x[2]);
		this.velBODY.set(x[3], x[4], x[5]);
		this.omegaBODY.set(x[6], x[7], x[8]);
		this.quat.set(x[9], x[10], x[11], x[12]);
	}

	public void setTime(double time) {
		this.time = time;
	}

	@Override
	public double getTime() {
		return time;
	}

	public void setPosNED(MathematicalVector posNED) {
		this.posNED = posNED;
	}

	@Override
	public MathematicalVector getPosNED() {
		return posNED;
	}

	private void setVelBODY(MathematicalVector velBODY) {
		this.velBODY = velBODY;
	}

	@Override
	public MathematicalVector getVelBODY() {
		return velBODY;
	}

	public void setOmegaBODY(MathematicalVector omegaBODY) {
		this.omegaBODY = omegaBODY;
	}

	@Override
	public MathematicalVector getOmegaBODY() {
		return omegaBODY;
	}

	public void setQuat(MathematicalVector quat) {
		this.quat = quat;
	}

	@Override
	public MathematicalVector getQuat() {
		return quat;
	}

	/**distance_Bpdy is vector at Body frame , in lift-off
	 * origin(launch point) to C.G.
	 * */
	private MathematicalVector getDistanceBody() {
		MathematicalMatrix dcmNED2BODY = new MathematicalMatrix(Coordinate.getDcmNED2BODYfromQuat(quat0));
		return dcmNED2BODY.dot(posNED);
	}

	public double getDistanceUpperLug() {
		double upperLugFromCG = rocket.getLcg(time) - rocket.upperLug;

		return getDistanceBody().add(new MathematicalVector(upperLugFromCG, 0.0, 0.0)).toDouble(0);
	}

	@Override
	public double getDistanceLowerLug() {
		double lowerLugFromCG = rocket.getLcg(time) - rocket.lowerLug;

		return getDistanceBody().add(new MathematicalVector(lowerLugFromCG, 0.0, 0.0)).toDouble(0);
	}

	@Override
	public double getVelDescent() {
		return velBODY.toDouble(2); // TODO: Fix velENU
	}

	@Override
	public double getAltitude() {
		// return posENU.toDouble(2);
		return - posNED.toDouble(2);
	}

	@Override 
	public VariableTrajectory clone() {
		
		VariableTrajectory clone = (VariableTrajectory) super.clone();
		clone.posNED    = this.posNED.clone();
		clone.velBODY   = this.velBODY.clone();
		clone.omegaBODY = this.omegaBODY.clone();
		clone.quat      = this.quat.clone();
		clone.quat0     = this.quat0.clone();
		clone.posNED0   = this.posNED0.clone();

		return clone;
	}

	public double[] getInitinalPosNED(){
		return posNED0;
	} 

	public double[] getInitinalQuat(){
		return quat0;
	} 
	
	public double[] toDouble() {
		double[] x = new double[13];
		System.arraycopy(posNED.toDouble(), 0, x, 0, 3);
		System.arraycopy(velBODY.toDouble(), 0, x, 3, 3);
		System.arraycopy(omegaBODY.toDouble(), 0, x, 6, 3);
		System.arraycopy(quat.toDouble(), 0, x, 9, 4);

		return x;
	}

	//TODO: DynamicsMinuteChangeからVariableをセット
	@Override
	public void update(double timeStep, AbstractDynamicsMinuteChange delta) {

		setTime(time + timeStep);
		AbstractDynamicsMinuteChange delta2 = delta.multiple(timeStep);
		setPosNED(posNED.add(delta2.getDeltaPosNED()));
		setVelBODY(velBODY.add(delta2.getDeltaVelNED()));
		setOmegaBODY(omegaBODY.add(delta2.getDeltaOmegaBODY()));
		setQuat(quat.add(delta2.getDeltaQuat()));
		quat.normalize();

	}

}
