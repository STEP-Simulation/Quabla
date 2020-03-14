package quabla.simulator.dynamics;

import quabla.simulator.AeroParameter;
import quabla.simulator.Atmosphere;
import quabla.simulator.ConstantVariable;
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

	public DynamicsTipOff(ConstantVariable constant) {
		this.rocket = constant.getRocket();
		this.aero = constant.getAeroParam();
		this.atm = constant.getAtmosphere();
		this.wind = constant.getWind();
	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(Variable variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double distance_LowerLug = variable.getDistanceLowerLug();
		double distance_UpperLug = variable.getDistanceUpperLug();
		MathematicalVector vel_ENU = variable.getVel_ENU();
		MathematicalVector omega_BODY = variable.getOmega_Body();
		MathematicalVector quat = new MathematicalVector(Coordinate.nomalizeQuat(variable.getQuat().getValue()));

		double m = rocket.getMass(t);
		double mDot = rocket.mdot(t);
		double p = omega_BODY.getValue()[0];
		double q = omega_BODY.getValue()[1];
		double r = omega_BODY.getValue()[2];

		// Transition coordinate
		MathematicalMatrix dcm_ENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.getValue()));
		MathematicalMatrix dcm_BODY2ENU = dcm_ENU2BODY.transpose();

		// wind
		MathematicalVector wind_ENU = new MathematicalVector(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		MathematicalVector vel_airENU = vel_ENU.sub(wind_ENU);
		MathematicalVector vel_airBODY = dcm_ENU2BODY.dot(vel_airENU);
		double vel_airAbs = vel_airBODY.norm();
		double v = vel_airBODY.getValue()[1];
		double w = vel_airBODY.getValue()[2];
		double alpha, beta;
		if(vel_airAbs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			alpha = Math.asin(w / vel_airAbs);
			beta = Math.asin(v / vel_airAbs);
		}

		// Environment
		MathematicalVector g = new MathematicalVector(0.0 , 0.0 , -atm.getGravity(altitude));
		double P0 = atm.getAtomosphericPressure(0.0);
		double P = atm.getAtomosphericPressure(altitude);
		double rho = atm.getAirDensity(altitude);
		double Cs = atm.getSoundSpeed(altitude);
		double Mach = vel_airAbs / Cs;
		double dynamics_pressure = 0.5 * rho * Math.pow(vel_airAbs, 2);

		//Thrust
		MathematicalVector thrust ;
		if(rocket.thrust(t) > 0.0) {
			double thrust_pressure = (P0 - P)* rocket.Ae;
			thrust = new MathematicalVector(rocket.thrust(t) + thrust_pressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		// Aero Force
		double drag = dynamics_pressure * aero.Cd(Mach) * rocket.S;
		//double nomal = dynamics_pressure * aero.CNa(Mach) * rocket.S * alpha;
		double side = dynamics_pressure * aero.CNa(Mach) * rocket.S * beta;
		MathematicalVector f_aero = new MathematicalVector(- drag, 0.0, 0.0);

		DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();


		return delta;
	}

}
