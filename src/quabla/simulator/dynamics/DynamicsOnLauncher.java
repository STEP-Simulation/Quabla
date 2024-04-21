package quabla.simulator.dynamics;

import quabla.simulator.Coordinate;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;


public class DynamicsOnLauncher extends AbstractDynamics {

	private final Rocket rocket ;

	public DynamicsOnLauncher(Rocket rocket) {
		this.rocket = rocket;
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {
		// Import from Variable
		MathematicalVector velENU = variable.getVelENU();
		MathematicalVector quat = variable.getQuat();
		double altitude = variable.getAltitude();
		double t = variable.getTime();

		double m = rocket.getMass(t);

		//Transition Coordinate
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.toDouble()));
		MathematicalMatrix dcmBODY2ENU = dcmENU2BODY.transpose();

		double elevation = Coordinate.deg2rad(Coordinate.getEulerFromDCM(dcmENU2BODY.getDouble())[1]);
		double Z0 = (rocket.L - rocket.lcgBef)*Math.sin(Math.abs(elevation));

		//Wind, Vel_air
		MathematicalVector windENU = new MathematicalVector(rocket.wind.getWindENU(altitude));
		MathematicalVector velAirENU = velENU.sub(windENU);
		MathematicalVector velAirBODY = dcmENU2BODY.dot(velAirENU);
		double velAirAbs = velAirBODY.norm();

		//Environment
		double g = - rocket.atm.getGravity(altitude);
		double P0 = rocket.atm.getAtomosphericPressure(0.0);
		double P = rocket.atm.getAtomosphericPressure(altitude);
		double rho = rocket.atm.getAirDensity(altitude);
		double Cs = rocket.atm.getSoundSpeed(altitude);
		double Mach = velAirAbs / Cs;
		double dynamicPressure = 0.5 * rho * Math.pow(velAirAbs, 2);

		//Thrust
		MathematicalVector thrust ;
		if(rocket.engine.thrust(t) > 0.0) {
			double thrustPressure = (P0 - P)* rocket.engine.Ae;
			thrust = new MathematicalVector(rocket.engine.thrust(t) + thrustPressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		//Aero Force
		double drag = dynamicPressure * rocket.S * rocket.aero.Cd(Mach);
		MathematicalVector forceAero = new MathematicalVector(- drag, 0.0, 0.0);

		//Newton Equation
		MathematicalVector forceBODY =  thrust.add(forceAero);
		MathematicalVector gBODY = new MathematicalVector(Math.abs(g)*Math.sin(elevation), 0.0, 0.0);

		//Acceleration
		MathematicalVector accBDOY = forceBODY.multiply(1 / m).add(gBODY);
		MathematicalVector accENU = dcmBODY2ENU.dot(accBDOY);

		//推力が自重に負けているとき(居座り)
		if(accENU.toDouble(2) <= 0.0 && t < rocket.engine.timeBurnout && altitude <= Z0) {
			accENU = new MathematicalVector(0.0, 0.0, 0.0);
		}

		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		delta.setDeltaVelENU(accENU);
		delta.setDeltaPosENU(velENU);
		delta.setDeltaOmegaBODY(MathematicalVector.ZERO);
		delta.setDeltaQuat(new MathematicalVector(0.0, 0.0, 0.0, 0.0));

		return delta;
	}
}
