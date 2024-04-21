package quabla.simulator.dynamics;

import quabla.simulator.Coordinate;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;

/**
 * DynamicsTrajectory is an extension of {@link quabla.simulator.dynamics.AbstractDynamics}.
 * */
public class DynamicsTrajectory extends AbstractDynamics {

	private Rocket rocket;

	MathematicalVector velENU = new MathematicalVector();
	MathematicalVector velBODY = new MathematicalVector();
	MathematicalVector omegaBODY = new MathematicalVector();
	MathematicalVector velAirENU = new MathematicalVector();
	MathematicalVector velAirBODY = new MathematicalVector();

	MathematicalVector windENU = new MathematicalVector();
	MathematicalVector gENU  = new MathematicalVector();
	MathematicalVector thrust = new MathematicalVector ();
	MathematicalVector forceAero = new MathematicalVector ();
	MathematicalVector forceENU = new MathematicalVector ();
	MathematicalVector forceBODY = new MathematicalVector ();
	MathematicalVector accENU = new MathematicalVector ();
	MathematicalVector accBODY = new MathematicalVector ();
	MathematicalVector armMoment = new MathematicalVector();
	MathematicalVector momentAero = new MathematicalVector();
	MathematicalVector momentAeroDamping = new MathematicalVector();
	MathematicalVector momentJetDamping = new MathematicalVector();
	MathematicalVector momentGyro = new MathematicalVector();
	MathematicalVector omegadot = new MathematicalVector();

	DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();

	public DynamicsTrajectory(Rocket rocket) {
		this.rocket = rocket;
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {

		// ----------------------------------------------------------------
		//  Import variable
		// ----------------------------------------------------------------
		velENU = variable.getVelENU();
		omegaBODY = variable.getOmegaBODY();
		MathematicalVector quat = new MathematicalVector(Coordinate.nomalizeQuat(variable.getQuat().toDouble()));
		double altitude = variable.getAltitude();
		double t = variable.getTime();
		// ----------------------------------------------------------------

		double m = rocket.getMass(t);
		double p = omegaBODY.toDouble(0);
		double q = omegaBODY.toDouble(1);
		double r = omegaBODY.toDouble(2);

		// Translation coordinate
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.toDouble()));
		MathematicalMatrix dcmBODY2ENU = dcmENU2BODY.transpose();

		// alpha , beta
		windENU.set(rocket.wind.getWindENU(altitude));
		velAirENU = velENU.sub(windENU);
		velAirBODY = dcmENU2BODY.dot(velAirENU);
		double velAirAbs = velAirBODY.norm();
		double u = velAirBODY.toDouble(0);
		double v = velAirBODY.toDouble(1);
		double w = velAirBODY.toDouble(2);

		double alpha , beta; //angle of attack , angle of side-slip
		if(velAirAbs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			// alpha = Math.asin(w / velAirAbs);
			alpha = Math.atan2(w, u);
			beta = Math.asin(v / velAirAbs);
		}

		// Environment
		gENU.set(0.0 , 0.0 , - rocket.atm.getGravity(altitude));
		double P0 = rocket.atm.getAtomosphericPressure(0.0);
		double P = rocket.atm.getAtomosphericPressure(altitude);
		double rho = rocket.atm.getAirDensity(altitude);
		double Cs = rocket.atm.getSoundSpeed(altitude);
		double Mach = velAirAbs / Cs;
		double pressureDynamics = 0.5 * rho * Math.pow(velAirAbs, 2);

		// Thrust
		double thrustMomentum = rocket.engine.thrust(t);
		if(thrustMomentum > 0.0) {
			double thrustPressure = (P0 - P)* rocket.engine.Ae;
			thrust.set(thrustMomentum + thrustPressure, 0.0, 0.0);
		}else {
			thrust.set(MathematicalVector.ZERO);
		}

		// Aero Force
		double drag = pressureDynamics * rocket.aero.Cd(Mach) * rocket.S;
		double normal = pressureDynamics * rocket.aero.CNa(Mach) * rocket.S * alpha;
		double side = pressureDynamics * rocket.aero.CNa(Mach) * rocket.S * beta;
		forceAero.set(- drag , - side , - normal);

		// Newton Equation
		forceENU = dcmBODY2ENU.dot(thrust.add(forceAero));
		// forceBODY = thrust.add(forceAero);

		// Accelaration
		accENU = forceENU.multiply(1 / m).add(gENU);
		// accBODY = (forceBODY.multiply(1 / m)).add(dcmENU2BODY.dot(gENU));

		// Center of Gravity , Pressure
		double lcg = rocket.getLcg(t);
		double lcp = rocket.aero.Lcp(Mach);

		// Momento of Inertia
		double IjRoll = rocket.getIjRoll(t);
		double IjPitch = rocket.getIjPitch(t);
		double[] Ij = {IjRoll, IjPitch, IjPitch};

		double IjDotPitch = rocket.getIjDotPitch(t);
		double IjDotRoll = rocket.getIjDotRoll(t);
		double[] IjDot = {IjDotRoll, IjDotPitch, IjDotPitch};

		// Aero Moment
		armMoment.set(lcg - lcp, 0.0, 0.0);
		momentAero = armMoment.cross(forceAero);

		// Aero Damping Moment
		momentAeroDamping .set(
				pressureDynamics * rocket.aero.Clp * rocket.S * (0.5*Math.pow(rocket.D, 2)/velAirAbs) * p,
				pressureDynamics * rocket.aero.Cmq * rocket.S * (0.5*Math.pow(rocket.L, 2)/velAirAbs) * q,
				pressureDynamics * rocket.aero.Cnr * rocket.S * (0.5*Math.pow(rocket.L, 2)/velAirAbs) * r);

		// Jet Damping Moment
		// momentJetDamping.set(
		// 		(- IjDot[0] - mDot * 0.5 * (0.25*Math.pow(rocket.engine.de, 2))) * p,
		// 		(- IjDot[1] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * q,
		// 		(- IjDot[2] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * r);
		momentJetDamping.set(
				(- IjDot[0]) * p,
				(- IjDot[1]) * q,
				(- IjDot[2]) * r);

		momentGyro.set(
				(Ij[1] - Ij[2])*q*r,
				(Ij[2] - Ij[0])*p*r,
				(Ij[0] - Ij[1])*p*q);

		MathematicalVector moment = momentGyro.add(momentAero).add(momentAeroDamping).add(momentJetDamping);

		omegadot.set(
				moment.toDouble(0) / Ij[0],
				moment.toDouble(1) / Ij[1],
				moment.toDouble(2) / Ij[2]);

		// Kinematics Equation
		MathematicalMatrix tensor = new MathematicalMatrix(Coordinate.Omega_tensor(p, q, r));
		MathematicalVector quatdot = tensor.dot(quat).multiply(0.5);

		// Store Minute Change
		delta.setDeltaPosENU(velENU);
		delta.setDeltaVelENU(accENU);
		delta.setDeltaOmegaBODY(omegadot);
		delta.setDeltaQuat(quatdot);

		return delta;
	}
}
