package quabla.simulator;

import quabla.InputParam;
import quabla.simulator.dynamics.DynamicsMinuteChange;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

//TODO 変数のカプセル化
public class Variable implements Cloneable {

	private RocketParameter rocket;
	private InputParam spec;


	private double time;

	//Main parameters
	private MathematicalVector pos_ENU;
	private MathematicalVector vel_ENU;
	private MathematicalVector omega_Body;
	private MathematicalVector quat;

	private double[] quat0;


	public Variable(InputParam spec,RocketParameter rocket) {
		this.spec = spec;
		this.rocket = rocket;
	}

	public void setInitialVariable() {
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
		pos_ENU = new MathematicalVector((rocket.L - rocket.Lcg_0)*Math.cos(Math.abs(elevation0))*Math.cos(azimuth0),
				(rocket.L - rocket.Lcg_0)*Math.cos(Math.abs(elevation0))*Math.sin(azimuth0),
				(rocket.L - rocket.Lcg_0)*Math.sin(Math.abs(elevation0)));

		//Initial Attitude with Quaternion
		quat0 = Coordinate.getQuatFromEuler(azimuth0, elevation0, roll0);

		quat = new MathematicalVector(quat0);

		//Initial Velocity
		vel_ENU = new MathematicalVector(0.0, 0.0, 0.0);

		//Initial Anguler Velocity
		omega_Body = new MathematicalVector(0.0, 0.0, 0.0);

	}

	public void setVariable(Variable variable) {
		this.time = variable.getTime();
		this.pos_ENU = variable.getPos_ENU();
		this.vel_ENU = variable.getVel_ENU();
		this.omega_Body = variable.getOmega_Body();
		this.quat = variable.getQuat();
	}

	public void setVariable(double time, MathematicalVector Pos_ENU, MathematicalVector Vel_ENU, MathematicalVector omega_Body, MathematicalVector quat) {

		this.time = time;
		this.pos_ENU = Pos_ENU;
		this.vel_ENU = Vel_ENU;
		this.omega_Body = omega_Body;
		this.quat = quat;

	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getTime() {
		return time;
	}

	public void setPos_ENU(MathematicalVector Pos_ENU) {
		this.pos_ENU = Pos_ENU;
	}

	public MathematicalVector getPos_ENU() {
		return pos_ENU;
	}

	public void setVel_ENU(MathematicalVector Vel_ENU) {
		this.vel_ENU = Vel_ENU;
	}

	public MathematicalVector getVel_ENU() {
		return vel_ENU;
	}

	public void setOmega_Body(MathematicalVector omega_Body) {
		this.omega_Body = omega_Body;
	}

	public MathematicalVector getOmega_Body() {
		return omega_Body;
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
		MathematicalMatrix DCM_ENU2Body = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat0));

		return DCM_ENU2Body.dot(pos_ENU);
	}

	public double getDistanceUpperLug() {
		double upperLugFromCG = rocket.Lcg(time) - rocket.upper_lug;

		return getDistanceBody().add(new MathematicalVector(upperLugFromCG, 0.0, 0.0)).getValue()[0];
	}

	public double getDistanceLowerLug() {
		double lowerLugFromCG = rocket.Lcg(time) - rocket.lower_lug;

		return getDistanceBody().add(new MathematicalVector(lowerLugFromCG, 0.0, 0.0)).getValue()[0];
	}

	public double getVelDescet() {
		return vel_ENU.getValue()[2];
	}

	public double getAltitude() {
		return pos_ENU.getValue()[2];
	}

	//TODO clone()をオーバーライド
/*
	@Override
	public Variable clone() {

		return (Variable) super.clone();
	}*/

	public Variable setClone() {
		Variable variable2 = new Variable(spec, rocket);
		variable2.setVariable(time, pos_ENU, vel_ENU, omega_Body, quat);

		return variable2;
	}



	//TODO DynamicsMinuteChangeからVariableをセット
	public void renewVariable(double time,DynamicsMinuteChange delta) {
		//double h = rocket.dt;

		setTime(time);
		setPos_ENU(pos_ENU.add(delta.deltaPos_ENU));
		setVel_ENU(vel_ENU.add(delta.deltaVel_ENU));
		setOmega_Body(omega_Body.add(delta.deltaOmega_Body));
		setQuat(quat.add(delta.deltaQuat));

	}


}
