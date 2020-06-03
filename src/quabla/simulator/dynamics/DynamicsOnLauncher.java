package quabla.simulator.dynamics;

import quabla.simulator.AeroParameter;
import quabla.simulator.Atmosphere;
import quabla.simulator.Coordinate;
import quabla.simulator.RocketParameter;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.AbstractVariable;


public class DynamicsOnLauncher extends AbstractDynamics {

	private final RocketParameter rocket ;
	private final AeroParameter aero;
	private final Atmosphere atm;
	private final Wind wind;

	public DynamicsOnLauncher(RocketParameter rocket, AeroParameter aero, Atmosphere atm, Wind wind) {
		this.rocket = rocket;
		this.aero = aero;
		this.atm = atm;
		this.wind = wind;
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(AbstractVariable variable) {
		// Import from Varible
		MathematicalVector velENU = variable.getVelENU();
		MathematicalVector quat = variable.getQuat();
		double altitude = variable.getAltitude();
		double t = variable.getTime();

		double m = rocket.getMass(t);

		//Tronsition Coodinate
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.toDouble()));
		MathematicalMatrix dcmBODY2ENU = dcmENU2BODY.transpose();

		double elevation = Coordinate.deg2rad(Coordinate.getEulerFromDCM(dcmENU2BODY.getDouble())[1]);
		double Z0 = (rocket.L - rocket.lcgBef)*Math.sin(Math.abs(elevation));

		//Wind, Vel_air
		MathematicalVector windENU = new MathematicalVector(Wind.windENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		MathematicalVector velAirENU = velENU.sub(windENU);
		MathematicalVector velAirBODY = dcmENU2BODY.dot(velAirENU);
		double velAirAbs = velAirBODY.norm();

		//Environment
		double g = - atm.getGravity(altitude);
		double P0 = atm.getAtomosphericPressure(0.0);
		double P = atm.getAtomosphericPressure(altitude);
		double rho = atm.getAirDensity(altitude);
		double Cs = atm.getSoundSpeed(altitude);
		double Mach = velAirAbs / Cs;
		double dynamicPressure = 0.5 * rho * Math.pow(velAirAbs, 2);

		//Thrust
		MathematicalVector thrust ;
		if(rocket.thrust(t) > 0.0) {
			double thrustPressure = (P0 - P)* rocket.Ae;
			thrust = new MathematicalVector(rocket.thrust(t) + thrustPressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		//Aero Force
		double drag = dynamicPressure * rocket.S * aero.Cd(Mach);
		MathematicalVector forceAero = new MathematicalVector(- drag, 0.0, 0.0);

		//Newton Equation
		MathematicalVector forceBODY =  thrust.add(forceAero);
		MathematicalVector gBODY = new MathematicalVector(Math.abs(g)*Math.sin(elevation), 0.0, 0.0);

		//Accelaration
		MathematicalVector accBDOY = forceBODY.multiply(1 / m).add(gBODY);
		MathematicalVector accENU = dcmBODY2ENU.dot(accBDOY);

		//推力が自重に負けているとき(居座り)
		if(accENU.toDouble(2) <= 0.0 && t < rocket.timeBurnout && altitude <= Z0) {
			accENU = new MathematicalVector(0.0, 0.0, 0.0);
		}

		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		delta.setDeltaVelENU(accENU);
		delta.setDeltaPos_ENU(velENU);
		delta.setDeltaOmegaBODY(MathematicalVector.ZERO);
		delta.setDeltaQuat(new MathematicalVector(0.0, 0.0, 0.0, 0.0));

		return delta;
	}
}
