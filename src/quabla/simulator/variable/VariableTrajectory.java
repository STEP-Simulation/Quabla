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
public class VariableTrajectory extends AbstractVariable{

	private Rocket rocket;

	private double time;
	private final double h;

	//Main parameters
	private MathematicalVector posENU;
	private MathematicalVector velENU;
	private MathematicalVector omegaBODY;
	private MathematicalVector quat;
	private double e,n,u,q;

	private double[] quat0;


	public VariableTrajectory(Rocket rocket) {
		this.rocket = rocket;
		h = rocket.dt;
		setInitialVariable();
	}

	public void setInitialVariable() {//初期パラメータの取得
		time = 0.0;

		//Initial Euler Angle
		double azimuth0, elevation0, roll0;
		azimuth0 = Coordinate.deg2rad((- rocket.azimuthLauncher + 90.0) + rocket.magneticDec);
		elevation0 = Coordinate.deg2rad(rocket.elevationLauncher);
		if(elevation0 > 0.0) {
			elevation0 *= -1.0;
		}
		roll0 = Math.PI;

		//Initial Position_ENU
		e = (rocket.L - rocket.lcgBef)*Math.cos(Math.abs(elevation0))*Math.cos(azimuth0);
		n = (rocket.L - rocket.lcgBef)*Math.cos(Math.abs(elevation0))*Math.sin(azimuth0);
		u = (rocket.L - rocket.lcgBef)*Math.sin(Math.abs(elevation0));
		posENU = new MathematicalVector(e,n,u);

		//Initial Attitude with Quaternion
		quat0 = Coordinate.getQuatFromEuler(azimuth0, elevation0, roll0);

		quat = new MathematicalVector(quat0);

		//Initial Velocity
		velENU = new MathematicalVector(0.0, 0.0, 0.0);

		//Initial Anguler Velocity
		omegaBODY = new MathematicalVector(0.0, 0.0, 0.0);

	}

	public void setVariable(VariableTrajectory variable) {
		this.time = variable.getTime();
		this.posENU = variable.getPosENU();
		this.velENU = variable.getVelENU();
		this.omegaBODY = variable.getOmegaBODY();
		this.quat = variable.getQuat();
	}

	public void setVariable(double time, MathematicalVector Pos_ENU, MathematicalVector Vel_ENU, MathematicalVector omega_Body, MathematicalVector quat) {
		this.time = time;
		this.posENU.set(Pos_ENU.toDouble());
		this.velENU.set(Vel_ENU.toDouble());
		this.omegaBODY.set(omega_Body.toDouble());
		this.quat.set(quat.toDouble());
	}

	public void setVariable(double time, double[] x) {
		this.time = time;
		this.posENU.set(x[0], x[1], x[2]);
		this.velENU.set(x[3], x[4], x[5]);
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

	public void setPos_ENU(MathematicalVector posENU) {
		this.posENU = posENU;
	}

	public MathematicalVector getPosENU() {
		return posENU;
	}

	public void setVelENU(MathematicalVector velENU) {
		this.velENU = velENU;
	}

	@Override
	public MathematicalVector getVelENU() {
		return velENU;
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
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat0));
		return dcmENU2BODY.dot(posENU);
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
		return velENU.toDouble(2);
	}

	@Override
	public double getAltitude() {
		return posENU.toDouble(2);
	}

	public VariableTrajectory getClone() {
		VariableTrajectory variable2 = new VariableTrajectory(rocket);
		variable2.setVariable(time, posENU, velENU, omegaBODY, quat);
		return variable2;
	}

	public double[] toDouble() {
		double[] x = new double[13];
		System.arraycopy(posENU.toDouble(), 0, x, 0, 3);
		System.arraycopy(velENU.toDouble(), 0, x, 3, 3);
		System.arraycopy(omegaBODY.toDouble(), 0, x, 6, 3);
		System.arraycopy(quat.toDouble(), 0, x, 9, 4);

		return x;
	}

	//TODO DynamicsMinuteChangeからVariableをセット
	@Override
	public void update(double time, AbstractDynamicsMinuteChange delta) {

		setTime(time);
		setPos_ENU(posENU.add(delta.getDeltaPosENU().multiply(h)));
		setVelENU(velENU.add(delta.getDeltaVelENU().multiply(h)));
		setOmegaBODY(omegaBODY.add(delta.getDeltaOmegaBODY().multiply(h)));
		setQuat(quat.add(delta.getDeltaQuat().multiply(h)));

	}


}
