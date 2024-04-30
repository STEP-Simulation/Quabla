package quabla.simulator.variable;

import quabla.simulator.Coordinate;
import quabla.simulator.rocket.Rocket;

/**
 * Variable以外の変数を計算するクラス
 * */
public class OtherVariableTrajectory {

	protected final Rocket rocket;

	protected double[] velNED;
	protected double[] attitude;
	protected double mass;
	protected double massFuel;
	protected double massOx;
	protected double massProp;
	protected double lcg;
	protected double lcgFuel;
	protected double lcgOx;
	protected double lcgProp;
	protected double lcp;
	protected double IjRoll, IjPitch;
	protected double[] IjDot = new double[3];
	protected double Cd;
	protected double CNa;
	protected double altitude, downrange;
	protected double[] velAirENU = new double[3];
	protected double[] velAirBODY = new double[3];
	protected double velAirAbs;
	protected double alphaRad, betaRad;
	protected double Mach;
	protected double dynamicsPressure;
	protected double Fst;
	protected double drag, normal, side;
	protected double thrust, thrustMomentum, thrustPressure;
	protected double[] forceBODY = new double[3];
	protected double[] accENU = new double[3];
	protected double[] accBODY = new double[3];
	protected double accAbs;
	protected double[] momentAero = new double[3];
	protected double[] momentAeroDamping = new double[3];
	protected double[] momentJetDamping = new double[3];
	protected double[] momentGyro = new double[3];
	protected double[] moment = new double[3];
	protected double[] omegadot = new double[3];
	protected double[][] dcmNED2BODY;
	protected double[][] dcmBODY2NED;

	protected double[] quatdot;

	protected double P_air, P_air0, gravity,rho;


	public OtherVariableTrajectory(Rocket rocket) {
		this.rocket = rocket;
		P_air0 = rocket.atm.getAtomosphericPressure(0.0);
	}

	public void setOtherVariable(double time, double[] posNED, double[] velBODY, double[] omegaBODY, double[] quat) {

		dcmNED2BODY = Coordinate.getDcmNED2BODYfromQuat(quat);
		dcmBODY2NED = Coordinate.getDcmBODY2NEDfromDcmNED2BODY(dcmNED2BODY);
		attitude = Coordinate.getEulerFromDCM(dcmNED2BODY);
		
		// Mass
		mass     = rocket.getMass(time);
		massFuel = rocket.engine.getMassFuel(time);
		massOx   = rocket.engine.getMassOx(time);
		massProp = massFuel + massOx;
		// MOI (Moment of Inertia)
		IjRoll   = rocket.getIjRoll(time);
		IjPitch  = rocket.getIjPitch(time);
		IjDot[0] = rocket.getIjDotRoll(time);
		IjDot[1] = rocket.getIjDotPitch(time);
		IjDot[2] = rocket.getIjDotPitch(time);
		// C.G. (Center of Gravity)
		lcg      = rocket.getLcg(time);
		lcgFuel  = rocket.engine.lcgFuel;
		lcgOx    = rocket.engine.getLcgOx(time);
		lcgProp  = rocket.getLcgProp(time);
		
		altitude  = - posNED[2];
		downrange = Math.sqrt(
			        posNED[0]*posNED[0]
			      + posNED[1]*posNED[1]
		);
		
		// Atmosphere
		gravity = rocket.atm.getGravity(altitude);
		P_air   = rocket.atm.getAtomosphericPressure(altitude);
		rho     = rocket.atm.getAirDensity(altitude);
		double[] gNED = {0.0, 0.0, gravity};
		
		// Velocity
		velNED = Coordinate.transVector(dcmBODY2NED, velBODY);
		double[] windNED = rocket.wind.getWindNED(altitude);
		for(int i = 0; i < 3; i++) {
			velAirENU[i] = velNED[i] - windNED[i];
		}
		velAirBODY = Coordinate.transVector(dcmNED2BODY, velAirENU);
		// velAirAbs = Math.sqrt(Math.pow(velAirBODY[0], 2) + Math.pow(velAirBODY[1], 2) + Math.pow(velAirBODY[2], 2));
		velAirAbs = Math.sqrt(
			        velAirBODY[0]*velAirBODY[0]
		          + velAirBODY[1]*velAirBODY[1]
		          + velAirBODY[2]*velAirBODY[2]
		);
		if(velAirAbs <= 0.0) {
			alphaRad = 0.0;
			betaRad  = 0.0;
		}else {
			// alphaRad = Math.asin(velAirBODY[2] / velAirAbs);
			alphaRad = Math.atan2(velAirBODY[2], velAirBODY[0]);
			betaRad  = Math.asin(velAirBODY[1] / velAirAbs);
		}

		Mach = velAirAbs / rocket.atm.getSoundSpeed(altitude);
		dynamicsPressure = 0.5 * rho * velAirAbs*velAirAbs;

		lcp = rocket.aero.Lcp(Mach);
		Fst = (lcp - lcg) / rocket.L * 100.0;

		// Aero Coefficient
		Cd  = rocket.aero.Cd(Mach);
		CNa = rocket.aero.CNa(Mach);
		// Aero Force
		drag   = dynamicsPressure * Cd  * rocket.S;
		normal = dynamicsPressure * CNa * rocket.S * alphaRad;
		side   = dynamicsPressure * CNa * rocket.S * betaRad;
		double[] forceAero = {- drag, - side, - normal};

		// Thrust
		thrustMomentum = rocket.engine.thrust(time);
		if(thrustMomentum <= 0.0) {
			thrustPressure = 0.0;
			thrustMomentum = 0.0;
		}else {
			thrustPressure = (P_air0 - P_air) * rocket.engine.Ae;
		}
		thrust = thrustMomentum + thrustPressure;

		// Force @ BODY-coordinate
		forceBODY[0] = forceAero[0] + thrust;
		forceBODY[1] = forceAero[1];
		forceBODY[2] = forceAero[2];

		// Acceleration
		double[] gBODY = Coordinate.transVector(dcmNED2BODY, gNED);
		for (int i = 0; i < accBODY.length; i++) {
			int j = (i + 1) % 3;
			int k = (i + 2) % 3;
			accBODY[i] = - (omegaBODY[j]*velBODY[k] - omegaBODY[k]*velBODY[j])
			           + forceBODY[i] / mass + gBODY[i];
		}
		accENU = Coordinate.transVector(dcmBODY2NED, accBODY);

		accAbs = Math.sqrt(
			     accBODY[0]*accBODY[0] 
			   + accBODY[1]*accBODY[1] 
			   + accBODY[2]*accBODY[2]
		);

		double[] armMoment = {lcg - lcp, 0.0, 0.0};
		for (int i = 0; i < 3; i++) {
			int j = (i + 1) % 3;
			int k = (i + 2) % 3;
			momentAero[i] = armMoment[j] * forceAero[k] - armMoment[k] * forceAero[j];
		}

		momentAeroDamping[0] = dynamicsPressure * rocket.aero.Clp * rocket.S * (.5 * rocket.D*rocket.D / velAirAbs) * omegaBODY[0];
		momentAeroDamping[1] = dynamicsPressure * rocket.aero.Cmq * rocket.S * (.5 * rocket.L*rocket.L / velAirAbs) * omegaBODY[1];
		momentAeroDamping[2] = dynamicsPressure * rocket.aero.Cnr * rocket.S * (.5 * rocket.L*rocket.L / velAirAbs) * omegaBODY[2];

		// momentJetDamping[0] = (- IjDot[0] - mDot * 0.5 * (0.25*Math.pow(rocket.engine.de, 2))) * omega_BODY[0];
		// momentJetDamping[1] = (- IjDot[1] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * omega_BODY[1];
		// momentJetDamping[2] = (- IjDot[2] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * omega_BODY[2];
		momentJetDamping[0] = - IjDot[0] * omegaBODY[0];
		momentJetDamping[1] = - IjDot[1] * omegaBODY[1];
		momentJetDamping[2] = - IjDot[2] * omegaBODY[2];

		double[] Ij = {IjRoll, IjPitch, IjPitch};
		momentGyro[0] = (Ij[1] - Ij[2]) * omegaBODY[1] * omegaBODY[2];
		momentGyro[1] = (Ij[2] - Ij[0]) * omegaBODY[0] * omegaBODY[2];
		momentGyro[2] = (Ij[0] - Ij[1]) * omegaBODY[0] * omegaBODY[1];

		for (int i = 0; i < 3; i++) {
			moment[i] = momentGyro[i] + momentAero[i] + momentAeroDamping[i] + momentJetDamping[i];
			omegadot[i] = moment[i] / Ij[i];
		}

		double[][] tensor = Coordinate.getOmegaTensor(omegaBODY[0], omegaBODY[1], omegaBODY[2]);
		quatdot = Coordinate.transVector(tensor, quat);
		for (int i = 0; i < quatdot.length; i++) {
			quatdot[i] *= 0.5;
		}

	}

	public double[] getVelNED() {
		return velNED;
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

	public double getThrustMomentum() {
		return thrustMomentum;
	}

	public double getThrustPressure() {
		return thrustPressure;
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

	public double[] getOmegaDot() {
		return omegadot;
	}

	public double[] getQuatDot(){
		return quatdot;
	}

	public double getPair() {
		return P_air;
	}
}
