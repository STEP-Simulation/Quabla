package quabla.simulator.variable;

import quabla.simulator.Coordinate;
import quabla.simulator.rocket.Rocket;

/**
 * Variable以外の変数を計算するクラス
 * */
public class OtherVariableTrajectory {

	/**保存したい変数(Trajectory)
	 * time 時間
	 * pos_ENU
	 * vel_ENU
	 * omega_BODY
	 * quat
	 * m
	 * Ij_roll
	 * Ij_pitch
	 * altitude
	 * downrange
	 * vel_air_abs
	 * Mach
	 * alpha
	 * beta
	 * attitude
	 * lcg
	 * lcgProp
	 * lcp
	 * Fst
	 * dynamics pressure
	 * drag
	 * normal
	 * side
	 * thrust
	 * acc_ENU
	 * acc_BODY
	 * acc_abs
	 * */

	private Rocket rocket;

	private double[] attitude;
	private double mass;
	private double massFuel;
	private double massOx;
	private double massProp;
	private double lcg;
	private double lcgFuel;
	private double lcgOx;
	private double lcgProp;
	private double lcp;
	private double IjRoll, IjPitch;
	private double altitude, downrange;
	private double[] velAirENU = new double[3];
	private double[] velAirBODY = new double[3];
	private double velAirAbs;
	private double alphaRad, betaRad;
	private double Mach;
	private double dynamicsPressure;
	private double Fst;
	private double drag, normal, side;
	private double thrust;
	private double[] forceBODY = new double[3];
	private double[] accENU = new double[3];
	private double[] accBODY = new double[3];
	private double accAbs;

	private double P_air, P_air0, gravity,rho;


	public OtherVariableTrajectory(Rocket rocket) {
		this.rocket = rocket;
		P_air0 = rocket.atm.getAtomosphericPressure(0.0);
	}

	public void setOtherVariable(double time, double[] pos_ENU, double[] vel_ENU, double[] quat) {

		double[][] dcm_ENU2BODY = Coordinate.getDCM_ENU2BODYfromQuat(quat);
		double[][] dcm_BODY2ENU = Coordinate.getDCM_BODY2ENUFromDCM_ENU2BODY(dcm_ENU2BODY);

		attitude = Coordinate.getEulerFromDCM(dcm_ENU2BODY);

		mass = rocket.getMass(time);
		massFuel = rocket.engine.getMassFuel(time);
		massOx = rocket.engine.getMassOx(time);
		massProp = massFuel + massOx;
		IjRoll = rocket.getIjRoll(time);
		IjPitch = rocket.getIjPitch(time);
		lcg = rocket.getLcg(time);
		lcgFuel = rocket.engine.lcgFuel;
		lcgOx = rocket.engine.getLcgOx(time);
		lcgProp = rocket.getLcgProp(time);

		altitude = pos_ENU[2];
		downrange = Math.sqrt(Math.pow(pos_ENU[0], 2) + Math.pow(pos_ENU[1], 2));

		gravity = rocket.atm.getGravity(altitude);
		double[] g = {0.0, 0.0, - gravity};
		 P_air = rocket.atm.getAtomosphericPressure(altitude);
		rho = rocket.atm.getAirDensity(altitude);

		// velAir , alpha , beta
		double[] wind_ENU = rocket.wind.getWindENU(altitude);
		for(int i = 0; i < 3; i++) {
			velAirENU[i] = vel_ENU[i] - wind_ENU[i];
		}
		velAirBODY = Coordinate.vec_trans(dcm_ENU2BODY, velAirENU);
		velAirAbs = Math.sqrt(Math.pow(velAirBODY[0], 2) + Math.pow(velAirBODY[1], 2) + Math.pow(velAirBODY[2], 2));
		if(velAirAbs <= 0.0) {
			alphaRad = 0.0;
			betaRad = 0.0;
		}else {
			alphaRad = Math.asin(velAirBODY[2] / velAirAbs);
			betaRad = Math.asin(velAirBODY[1] / velAirAbs);
		}

		Mach = velAirAbs / rocket.atm.getSoundSpeed(altitude);
		dynamicsPressure = 0.5 * rho * Math.pow(velAirAbs, 2);

		lcp = rocket.aero.Lcp(Mach);
		Fst = (lcp - lcg) / rocket.L * 100.0;

		// force
		drag = dynamicsPressure * rocket.aero.Cd(Mach) * rocket.S;
		normal = dynamicsPressure * rocket.aero.CNa(Mach) * rocket.S * alphaRad;
		side = dynamicsPressure * rocket.aero.CNa(Mach) * rocket.S * betaRad;

		double pressureThrust;
		thrust = rocket.engine.thrust(time);
		if(thrust <= 0.0) {
			pressureThrust = 0.0;
			thrust = 0.0;
		}else {
			pressureThrust = (P_air0 - P_air) * rocket.engine.Ae;
			thrust += pressureThrust;
		}

		forceBODY[0] = thrust - drag;
		forceBODY[1] = - side;
		forceBODY[2] = - normal;

		accENU = Coordinate.vec_trans(dcm_BODY2ENU, forceBODY);
		for(int i = 0; i < 3; i++) {
			accENU[i] = accENU[i] / mass + g[i];
		}
		accBODY = Coordinate.vec_trans(dcm_ENU2BODY, accENU);
		accAbs = Math.sqrt(Math.pow(accENU[0], 2) + Math.pow(accENU[1], 2) + Math.pow(accENU[2], 2));
	}

	public double[] getAttitude() {
		return attitude;
	}

	public double getMass() {
		return mass;
	}

	public double getMassFuel() {
		return massFuel;
	}

	public double getMassOx() {
		return massOx;
	}

	public double getMassProp() {
		return massProp;
	}

	public double getLcg() {
		return lcg;
	}

	public double getLcgFuel() {
		return rocket.L - lcgFuel;
	}
	public double getLcgOx() {
		return rocket.L - lcgOx;
	}
	public double getLcgProp() {
		return lcgProp;
	}

	public double getLcp() {
		return lcp;
	}

	public double getIjRoll() {
		return IjRoll;
	}

	public double getIjPitch() {
		return IjPitch;
	}

	public double getAltitude() {
		return altitude * Math.pow(10, -3);
	}

	public double getMach() {
		return Mach;
	}

	public double getDownrange() {
		return downrange * Math.pow(10, -3);
	}

	public double[] getVelAirENU() {
		return velAirENU;
	}

	public double[] getVelAirBODY() {
		return velAirBODY;
	}

	public double getVelAirAbs() {
		return velAirAbs;
	}

	public double getAlpha() {
		return Math.toDegrees(alphaRad);
	}

	public double getBeta() {
		return Math.toDegrees(betaRad);
	}

	public double getDynamicsPressure() {
		return dynamicsPressure * Math.pow(10, -3);
	}

	public double getFst() {
		return Fst;
	}

	public double getDrag() {
		return drag;
	}

	public double getNormal() {
		return normal;
	}

	public double getSide() {
		return side;
	}

	public double getThrust() {
		return thrust;
	}

	public double[] getForceBODY(){
		return forceBODY;
	}

	public double[] getAccENU() {
		return accENU;
	}

	public double[] getAccBODY() {
		return accBODY;
	}

	public double getAccAbs() {
		return accAbs;
	}

	public double getPair() {
		return P_air;
	}
}
