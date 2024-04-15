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
	private double mDot;
	private double lcg;
	private double lcgFuel;
	private double lcgOx;
	private double lcgProp;
	private double lcp;
	private double IjRoll, IjPitch;
	private double[] IjDot = new double[3];
	private double Cd;
	private double CNa;
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
	private double[] momentAero = new double[3];
	private double[] momentAeroDamping = new double[3];
	private double[] momentJetDamping = new double[3];
	private double[] momentGyro = new double[3];
	private double[] moment = new double[3];

	private double P_air, P_air0, gravity,rho;


	public OtherVariableTrajectory(Rocket rocket) {
		this.rocket = rocket;
		P_air0 = rocket.atm.getAtomosphericPressure(0.0);
	}

	public void setOtherVariable(double time, double[] pos_ENU, double[] vel_ENU, double[] omega_BODY, double[] quat) {

		double[][] dcm_ENU2BODY = Coordinate.getDCM_ENU2BODYfromQuat(quat);
		double[][] dcm_BODY2ENU = Coordinate.getDCM_BODY2ENUFromDCM_ENU2BODY(dcm_ENU2BODY);

		attitude = Coordinate.getEulerFromDCM(dcm_ENU2BODY);

		mass = rocket.getMass(time);
		massFuel = rocket.engine.getMassFuel(time);
		massOx = rocket.engine.getMassOx(time);
		massProp = massFuel + massOx;
		mDot = rocket.mdot(time);
		IjRoll = rocket.getIjRoll(time);
		IjPitch = rocket.getIjPitch(time);
		IjDot[0] = rocket.getIjDotRoll(time);
		IjDot[1] = rocket.getIjDotPitch(time);
		IjDot[2] = rocket.getIjDotPitch(time);
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
			// alphaRad = Math.asin(velAirBODY[2] / velAirAbs);
			alphaRad = Math.atan2(velAirBODY[2], velAirBODY[0]);
			betaRad = Math.asin(velAirBODY[1] / velAirAbs);
		}

		Mach = velAirAbs / rocket.atm.getSoundSpeed(altitude);
		dynamicsPressure = 0.5 * rho * Math.pow(velAirAbs, 2);

		lcp = rocket.aero.Lcp(Mach);
		Fst = (lcp - lcg) / rocket.L * 100.0;

		// force
		Cd = rocket.aero.Cd(Mach);
		CNa = rocket.aero.CNa(Mach);
		drag = dynamicsPressure * Cd * rocket.S;
		normal = dynamicsPressure * CNa * rocket.S * alphaRad;
		side = dynamicsPressure * CNa * rocket.S * betaRad;

		double pressureThrust;
		thrust = rocket.engine.thrust(time);
		if(thrust <= 0.0) {
			pressureThrust = 0.0;
			thrust = 0.0;
		}else {
			pressureThrust = (P_air0 - P_air) * rocket.engine.Ae;
			thrust += pressureThrust;
		}

		// forceBODY[0] = thrust - drag;
		// forceBODY[1] = - side;
		// forceBODY[2] = - normal;

		double[] forceAero = {- drag, - side, - normal};
		forceBODY[0] = thrust + forceAero[0];
		forceBODY[1] = forceAero[1];
		forceBODY[2] = forceAero[2];

		accENU = Coordinate.vec_trans(dcm_BODY2ENU, forceBODY);
		for(int i = 0; i < 3; i++) {
			accENU[i] = accENU[i] / mass + g[i];
		}
		accBODY = Coordinate.vec_trans(dcm_ENU2BODY, accENU);
		accAbs = Math.sqrt(Math.pow(accENU[0], 2) + Math.pow(accENU[1], 2) + Math.pow(accENU[2], 2));

		double[] armMoment = {lcg - lcp, 0.0, 0.0};
		for (int i = 0; i < 3; i++) {
			int j = (i + 1) % 3;
			int k = (i + 2) % 3;
			momentAero[i] = armMoment[j] * forceAero[k] - armMoment[k] * forceAero[j];
		}

		momentAeroDamping[0] = dynamicsPressure * rocket.aero.Clp * rocket.S * (0.5*Math.pow(rocket.D, 2)/velAirAbs) * omega_BODY[0];
		momentAeroDamping[1] = dynamicsPressure * rocket.aero.Cmq * rocket.S * (0.5*Math.pow(rocket.L, 2)/velAirAbs) * omega_BODY[1];
		momentAeroDamping[2] = dynamicsPressure * rocket.aero.Cnr * rocket.S * (0.5*Math.pow(rocket.L, 2)/velAirAbs) * omega_BODY[2];

		// momentJetDamping[0] = (- IjDot[0] - mDot * 0.5 * (0.25*Math.pow(rocket.engine.de, 2))) * omega_BODY[0];
		// momentJetDamping[1] = (- IjDot[1] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * omega_BODY[1];
		// momentJetDamping[2] = (- IjDot[2] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * omega_BODY[2];
		momentJetDamping[0] = (- IjDot[0]) * omega_BODY[0];
		momentJetDamping[1] = (- IjDot[1]) * omega_BODY[1];
		momentJetDamping[2] = (- IjDot[2]) * omega_BODY[2];

		double[] Ij = {IjRoll, IjPitch, IjPitch};
		momentGyro[0] = (Ij[1] - Ij[2]) * omega_BODY[1] * omega_BODY[2];
		momentGyro[1] = (Ij[2] - Ij[0]) * omega_BODY[0] * omega_BODY[2];
		momentGyro[2] = (Ij[0] - Ij[1]) * omega_BODY[0] * omega_BODY[1];

		for (int i = 0; i < 3; i++) {
			moment[i] = momentGyro[i] + momentAero[i] + momentAeroDamping[i] + momentJetDamping[i];
		}

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

	public double getCd(){
		return Cd;
	}

	public double getCNa(){
		return CNa;
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

	public double[] getMomentAero() {
		return momentAero;
	}

	public double[] getMomentAeroDamiping() {
		return momentAeroDamping;
	}

	public double[] getMomentJetDamping() {
		return momentJetDamping;
	}

	public double[] getMomentGyro() {
		return momentGyro;
	}

	public double[] getMoment() {
		return moment;
	}

	public double getPair() {
		return P_air;
	}
}
