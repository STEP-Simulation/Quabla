package quabla.simulator.dynamics;

import quabla.simulator.Coordinate;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;

public class DynamicsTipOff extends AbstractDynamics {

	private Rocket rocket;

	public DynamicsTipOff(Rocket rocket) {
		this.rocket = rocket;
	}

	// TODO: revise velENU

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double distanceLowerLug = variable.getDistanceLowerLug();
		MathematicalVector velENU = variable.getVelBODY();
		MathematicalVector omegaBODY = variable.getOmegaBODY();
		MathematicalVector quat = new MathematicalVector(Coordinate.nomalizeQuat(variable.getQuat().toDouble()));

		double m = rocket.getMass(t);
		double mDot = rocket.mdot(t);
		double p = omegaBODY.toDouble(0);
		double q = omegaBODY.toDouble(1);
		double r = omegaBODY.toDouble(2);

		// Transition coordinate
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDcmNED2BODYfromQuat(quat.toDouble()));
		MathematicalMatrix dcmBODY2ENU = dcmENU2BODY.transpose();

		double[] attitudeDeg = Coordinate.getEulerFromDCM(dcmENU2BODY.getDouble());
		double elevation = Coordinate.deg2rad(attitudeDeg[1]);
		double roll = Coordinate.deg2rad(attitudeDeg[2]);

		// Wind
		MathematicalVector windENU = new MathematicalVector(rocket.wind.getWindNED(altitude));
		MathematicalVector velAirENU = velENU.sub(windENU);
		MathematicalVector velAirBODY = dcmENU2BODY.dot(velAirENU);
		double velAirAbs = velAirBODY.norm();
		double v = velAirBODY.toDouble()[1];
		double w = velAirBODY.toDouble()[2];
		double alpha, beta;
		if(velAirAbs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			alpha = Math.asin(w / velAirAbs);
			beta = Math.asin(v / velAirAbs);
		}

		// Environment
		double g = rocket.atm.getGravity(altitude); // 負の値ではなく絶対値なので注意
		MathematicalVector gBODY = (new MathematicalVector(- Math.sin(elevation), Math.sin(roll) * Math.cos(elevation), 0.0)).multiply(g);
		double P0 = rocket.atm.getAtomosphericPressure(0.0);
		double P = rocket.atm.getAtomosphericPressure(altitude);
		double rho = rocket.atm.getAirDensity(altitude);
		double Cs = rocket.atm.getSoundSpeed(altitude);
		double Mach = velAirAbs / Cs;
		double pressureDynamics = 0.5 * rho * Math.pow(velAirAbs, 2);

		// Thrust
		MathematicalVector thrust ;
		if(rocket.engine.thrust(t) > 0.0) {
			double thrustPressure = (P0 - P)* rocket.engine.Ae;
			thrust = new MathematicalVector(rocket.engine.thrust(t) + thrustPressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		// Aero Force
		double drag = pressureDynamics * rocket.aero.Cd(Mach) * rocket.S;
		double side = pressureDynamics * rocket.aero.CNa(Mach) * rocket.S * beta;
		MathematicalVector forceAero = new MathematicalVector(- drag, - side, 0.0);

		// Newton Equation
		MathematicalVector forceBODY = thrust.add(forceAero);
		MathematicalVector accBODY = forceBODY.multiply(1 / m).add(gBODY);
		MathematicalVector accENU = dcmBODY2ENU.dot(accBODY);

		// Center of Gravity , Pressure
		double lcg = rocket.getLcg(t);
		double lcgProp = rocket.getLcgProp(t);
		double lcp = rocket.aero.Lcp(Mach);

		// Moment of Inertia
		double IjPitch = rocket.getIjPitch(t) + m * Math.pow(distanceLowerLug - lcg, 2);
		double IjRoll = rocket.getIjRoll(t);
		double[] Ij = {IjRoll, IjPitch, IjPitch};

		double IjDotPitch = rocket.getIjDotPitch(t) + m * Math.pow(distanceLowerLug - lcg, 2);// 回転中心を下部ランチラグ周りとして，慣性モーメントに反映
		double IjDotRoll = rocket.getIjDotRoll(t);
		double[] IjDot = {IjDotRoll, IjDotPitch, IjDotPitch};

		// Aero Moment
		MathematicalVector armMoment = new MathematicalVector(distanceLowerLug - lcp, 0.0, 0.0);
		MathematicalVector momentAero = armMoment.cross(forceAero);

		// Aero Damping Moment
		MathematicalVector momentAeroDamping = new MathematicalVector(
				0.0,
				0.0,
				pressureDynamics * rocket.aero.Cnr * rocket.S * (0.5*Math.pow(rocket.L, 2)/velAirAbs) * r);

		// Jet Damping Moment
		MathematicalVector momentJetDamping = new MathematicalVector(
				0.0,
				0.0,
				(- IjDot[2] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * r);

		MathematicalVector momentGyro = new MathematicalVector(
				0.0,
				0.0,
				(Ij[0] - Ij[1])*p*q);

		MathematicalVector moment = momentGyro.add(momentAero).add(momentAeroDamping).add(momentJetDamping);
		MathematicalVector omegadot = new MathematicalVector(
				0.0,
				0.0,
				moment.toDouble()[2] / Ij[2]);

		// Kinematics Equation
		MathematicalMatrix tensor = new MathematicalMatrix(Coordinate.getOmegaTensor(p, q, r));
		MathematicalVector quatdot = tensor.dot(quat).multiply(0.5);

		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		delta.setDeltaPosNED(velENU);
		delta.setDeltaVelNED(accENU);
		delta.setDeltaOmegaBODY(omegadot);
		delta.setDeltaQuat(quatdot);

		return delta;
	}

}
