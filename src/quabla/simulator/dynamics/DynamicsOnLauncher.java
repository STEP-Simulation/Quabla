package quabla.simulator.dynamics;

import quabla.simulator.AeroParameter;
import quabla.simulator.ConstantVariable;
import quabla.simulator.Coordinate;
import quabla.simulator.Atmosphere;
import quabla.simulator.RocketParameter;
import quabla.simulator.Variable;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;


public class DynamicsOnLauncher extends AbstractDynamics {

	private RocketParameter rocket ;
	private AeroParameter aero;
	private Atmosphere atm;
	private Wind wind;

	public DynamicsOnLauncher(ConstantVariable constant) {

		this.rocket = constant.getRocket();
		this.aero = constant.getAeroParam();
		this.atm = constant.getAtmosphere();
		this.wind = constant.getWind();
	}

	@Override
	public DynamicsMinuteChange calculateDynamics(Variable variable) {
		// Import from Varible
		MathematicalVector vel_ENU = variable.getVel_ENU();
		MathematicalVector quat = variable.getQuat();
		double altitude = variable.getAltitude();
		double t = variable.getTime();

		double m = rocket.getMass(t);

		//Tronsition Coodinate
		MathematicalMatrix dcm_ENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.getValue()));
		MathematicalMatrix dcm_BODY2ENU = dcm_ENU2BODY.transpose();

		double elevation = Coordinate.deg2rad(Coordinate.getEulerFromDCM(dcm_ENU2BODY.getDouble())[2]);
		double Z0 = (rocket.l - rocket.lcgBef)*Math.sin(Math.abs(elevation));

		//Wind, Vel_air
		MathematicalVector wind_ENU = new MathematicalVector(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		MathematicalVector vel_airENU = vel_ENU.substract(wind_ENU);
		MathematicalVector vel_airBODY = dcm_ENU2BODY.dot(vel_airENU);
		double vel_airAbs = vel_airBODY.norm();

		//Environment
		double g = -atm.getGravity(altitude);
		double P0 = atm.getAtomosphericPressure(0.0);
		double P = atm.getAtomosphericPressure(altitude);
		double rho = atm.getAirDensity(altitude);
		double Cs = atm.getSoundSpeed(altitude);
		double Mach = vel_airAbs / Cs;
		double dynamic_pressure = 0.5 * rho * Math.pow(vel_airAbs, 2);

		//Thrust
		MathematicalVector thrust ;
		if(rocket.thrust(t) > 0.0) {
			double thrust_pressure = (P0 - P)* rocket.Ae;
			thrust = new MathematicalVector(rocket.thrust(t) + thrust_pressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		//Aero Force
		double drag = dynamic_pressure * rocket.S * aero.Cd(Mach);
		MathematicalVector f_aero = new MathematicalVector(- drag, 0.0, 0.0);

		//Newton Equation
		MathematicalVector force_BODY =  thrust.add(f_aero);
		MathematicalVector force_gravity = new MathematicalVector(Math.abs(g)*Math.sin(elevation), 0.0, 0.0);

		//Accelaration
		MathematicalVector acc_BDOY = force_BODY.multiply(1/m).add(force_gravity);
		MathematicalVector acc_ENU = dcm_BODY2ENU.dot(acc_BDOY);

		//推力が自重に負けているとき(居座り)
		if(acc_ENU.getValue()[2] <= 0.0 && t < rocket.timeBurnout && altitude <= Z0) {
			acc_ENU = new MathematicalVector(0.0, 0.0, 0.0);
		}

		DynamicsMinuteChange delta = new DynamicsMinuteChange();
		delta.deltaVel_ENU = acc_ENU;
		delta.deltaPos_ENU = vel_ENU;
		delta.deltaOmega_Body = new MathematicalVector(0.0, 0.0, 0.0);
		delta.deltaQuat = new MathematicalVector(0.0, 0.0, 0.0, 0.0);

		return delta;
	}
}
