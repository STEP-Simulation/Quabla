package quabla.simulator.variable;

import quabla.parameter.InputParam;
import quabla.simulator.Coordinate;
import quabla.simulator.RocketParameter;
import quabla.simulator.dynamics.DynamicsMinuteChangeTrajectory;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

/**
 * Variable stores variables of rocket when it flights.
 *
 * <p> When you set initial variables, use {@link #setInitialVariable()}.
 * You are not able to set initial variables at making instance of Variable
 * */
public class Variable {

	private RocketParameter rocket;
	private InputParam spec;

	private double time;
	private final double h;

	//Main parameters
	private MathematicalVector posENU;
	private MathematicalVector velENU;
	private MathematicalVector omegaBODY;
	private MathematicalVector quat;

	private double[] quat0;


	public Variable(InputParam spec,RocketParameter rocket) {
		this.spec = spec;
		this.rocket = rocket;
		h = rocket.dt;

		setInitialVariable();
	}

	public void setInitialVariable() {//初期パラメータの取得
		time = 0.0;

		//Initial Euler Angle
		double azimuth0, elevation0, roll0;
		azimuth0 = Coordinate.deg2rad((- spec.azimuth_launcher + 90.0) + spec.magnetic_dec);
		elevation0 = Coordinate.deg2rad(spec.elevation_launcher);
		if(elevation0 > 0.0) {
			elevation0 *= -1.0;
		}
		roll0 = Math.PI;

		//Initial Position_ENU
		posENU = new MathematicalVector(
				(rocket.L - rocket.lcgBef)*Math.cos(Math.abs(elevation0))*Math.cos(azimuth0),
				(rocket.L - rocket.lcgBef)*Math.cos(Math.abs(elevation0))*Math.sin(azimuth0),
				(rocket.L - rocket.lcgBef)*Math.sin(Math.abs(elevation0)));

		//Initial Attitude with Quaternion
		quat0 = Coordinate.getQuatFromEuler(azimuth0, elevation0, roll0);

		quat = new MathematicalVector(quat0);

		//Initial Velocity
		velENU = new MathematicalVector(0.0, 0.0, 0.0);

		//Initial Anguler Velocity
		omegaBODY = new MathematicalVector(0.0, 0.0, 0.0);

	}

	public void setVariable(Variable variable) {
		this.time = variable.getTime();
		this.posENU = variable.getPosENU();
		this.velENU = variable.getVelENU();
		this.omegaBODY = variable.getOmega_Body();
		this.quat = variable.getQuat();
	}

	public void setVariable(double time, MathematicalVector Pos_ENU, MathematicalVector Vel_ENU, MathematicalVector omega_Body, MathematicalVector quat) {

		this.time = time;
		this.posENU = Pos_ENU;
		this.velENU = Vel_ENU;
		this.omegaBODY = omega_Body;
		this.quat = quat;

	}

	public void setTime(double time) {
		this.time = time;
	}

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

	public MathematicalVector getVelENU() {
		return velENU;
	}

	public void setOmegaBODY(MathematicalVector omegaBODY) {
		this.omegaBODY = omegaBODY;
	}

	public MathematicalVector getOmega_Body() {
		return omegaBODY;
	}

	public void setQuat(MathematicalVector quat) {
		this.quat = quat;
	}

	public MathematicalVector getQuat() {
		return quat;
	}

	/**distance_Bpdy is vector at Body frame , in lift-off
	 * origin(launch point) to C.G.
	 * */
	public MathematicalVector getDistanceBody() {
		MathematicalMatrix dcm_ENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat0));

		return dcm_ENU2BODY.dot(posENU);
	}

	public double getDistanceUpperLug() {
		double upperLugFromCG = rocket.getLcg(time) - rocket.upperLug;

		return getDistanceBody().add(new MathematicalVector(upperLugFromCG, 0.0, 0.0)).toDouble(0);
	}

	public double getDistanceLowerLug() {
		double lowerLugFromCG = rocket.getLcg(time) - rocket.lowerLug;

		return getDistanceBody().add(new MathematicalVector(lowerLugFromCG, 0.0, 0.0)).toDouble(0);
	}

	public double getVelDescet() {
		return velENU.toDouble(2);
	}

	public double getAltitude() {
		return posENU.toDouble(2);
	}

	public Variable getClone() {
		Variable variable2 = new Variable(spec, rocket);
		variable2.setVariable(time, posENU, velENU, omegaBODY, quat);
		return variable2;
	}

	//public

	//TODO DynamicsMinuteChangeからVariableをセット
	public void update(double time,DynamicsMinuteChangeTrajectory delta) {

		setTime(time);
		setPos_ENU(posENU.add(delta.getDeltaPos_ENU().multiply(h)));
		setVelENU(velENU.add(delta.getDeltaVel_ENU().multiply(h)));
		setOmegaBODY(omegaBODY.add(delta.getDeltaOmega_Body().multiply(h)));
		setQuat(quat.add(delta.getDeltaQuat().multiply(h)));

	}


}
