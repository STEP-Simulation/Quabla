package quabla.simulator.dynamics;

import quabla.simulator.AeroParameter;
import quabla.simulator.Atmosphere;
import quabla.simulator.Coordinate;
import quabla.simulator.RocketParameter;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.Variable;

public class DynamicsTipOff extends AbstractDynamics {

	private RocketParameter rocket;
	private AeroParameter aero;
	private Atmosphere atm;
	private Wind wind;

	public DynamicsTipOff(RocketParameter rocket, AeroParameter aero, Atmosphere atm, Wind wind) {
		this.rocket = rocket;
		this.aero = aero;
		this.atm = atm;
		this.wind = wind;
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(Variable variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double distanceLowerLug = variable.getDistanceLowerLug();
		double distanceUpperLug = variable.getDistanceUpperLug();
		MathematicalVector velENU = variable.getVel_ENU();
		MathematicalVector omegaBODY = variable.getOmega_Body();
		MathematicalVector quat = new MathematicalVector(Coordinate.nomalizeQuat(variable.getQuat().getValue()));

		double m = rocket.getMass(t);
		double mDot = rocket.mdot(t);
		double p = omegaBODY.getValue()[0];
		double q = omegaBODY.getValue()[1];
		double r = omegaBODY.getValue()[2];

		// Transition coordinate
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.getValue()));
		MathematicalMatrix dcmBODY2ENU = dcmENU2BODY.transpose();

		// Wind
		MathematicalVector windENU = new MathematicalVector(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		MathematicalVector velAirENU = velENU.sub(windENU);
		MathematicalVector velAirBODY = dcmENU2BODY.dot(velAirENU);
		double velAirAbs = velAirBODY.norm();
		double v = velAirBODY.getValue()[1];
		double w = velAirBODY.getValue()[2];
		double alpha, beta;
		if(velAirAbs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			alpha = Math.asin(w / velAirAbs);
			beta = Math.asin(v / velAirAbs);
		}

		// Environment
		MathematicalVector g = new MathematicalVector(0.0 , 0.0 , -atm.getGravity(altitude));
		double P0 = atm.getAtomosphericPressure(0.0);
		double P = atm.getAtomosphericPressure(altitude);
		double rho = atm.getAirDensity(altitude);
		double Cs = atm.getSoundSpeed(altitude);
		double Mach = velAirAbs / Cs;
		double pressureDynamics = 0.5 * rho * Math.pow(velAirAbs, 2);

		// Thrust
		MathematicalVector thrust ;
		if(rocket.thrust(t) > 0.0) {
			double thrustPressure = (P0 - P)* rocket.Ae;
			thrust = new MathematicalVector(rocket.thrust(t) + thrustPressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		// Aero Force
		double drag = pressureDynamics * aero.Cd(Mach) * rocket.S;
		double normal = pressureDynamics * aero.CNa(Mach) * rocket.S * alpha;
		double side = pressureDynamics * aero.CNa(Mach) * rocket.S * beta;
		MathematicalVector forceAero = new MathematicalVector(- drag, - side, - normal);

		// Newton Equation
		MathematicalVector forceENU = dcmBODY2ENU.dot(thrust.add(forceAero));
		MathematicalVector accENU = (forceENU.multiply(1 / m)).add(g);

		// Center of Gravity , Pressure
		double lcg = rocket.getLcg(t);
		double lcgProp = rocket.getLcgProp(t);
		double lcp = aero.Lcp(Mach);

		double pointPivot = distanceLowerLug;

		// Moment of Inertia
		double IjPitch = rocket.getIjPitch(t) + m * Math.pow(distanceLowerLug - lcg, 2);
		double IjRoll = rocket.getIjRoll(t);
		double[] Ij = {IjRoll, IjPitch, IjPitch};

		double IjDotPitch = rocket.getIjDotPitch(t) + m * Math.pow(distanceLowerLug - lcg, 2);
		double IjDotRoll = rocket.getIjDotRoll(t);
		double[] IjDot = {IjDotRoll, IjDotPitch, IjDotPitch};

		// Aero Moment
		MathematicalVector armMoment = new MathematicalVector(distanceLowerLug - lcp, 0.0, 0.0);
		MathematicalVector momentAero = armMoment.cross(forceAero);

		// Aero Damping Moment
		MathematicalVector momentAeroDamping = new MathematicalVector(
				0.0,
				0.0,
				pressureDynamics * aero.Cnr * rocket.S *(0.5*Math.pow(rocket.L, 2)/velAirAbs) * r);

		// Jet Damping Moment
		MathematicalVector momentJetDamping = new MathematicalVector(
				0.0,
				0.0,
				(-IjDot[2] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * r);

		MathematicalVector momentGyro = new MathematicalVector(
				0.0,
				0.0,
				(Ij[0] - Ij[1])*p*q);

		MathematicalVector moment = momentGyro.add(momentAero).add(momentAeroDamping).add(momentJetDamping);
		MathematicalVector omegadot = new MathematicalVector(
				0.0,
				0.0,
				moment.getValue()[2] / Ij[2]);

		// Kinematics Equation
		MathematicalMatrix tensor = new MathematicalMatrix(Coordinate.Omega_tensor(p, q, r));
		MathematicalVector quatdot = tensor.dot(quat).multiply(0.5);

		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		delta.setDeltaPos_ENU(velENU);
		delta.setDeltaVelENU(accENU);
		delta.setDeltaOmegaBODY(omegadot);
		delta.setDeltaQuat(quatdot);

		return delta;
	}

}
