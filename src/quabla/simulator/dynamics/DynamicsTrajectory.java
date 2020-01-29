package quabla.simulator.dynamics;

import quabla.simulator.AeroParameter;
import quabla.simulator.Atmosphere;
import quabla.simulator.ConstantVariable;
import quabla.simulator.Coordinate;
import quabla.simulator.RocketParameter;
import quabla.simulator.Variable;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalMatrix;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

/**
 * DynamicsTrajectory is an extension of {@link quabla.simulator.dynamics.AbstractDynamics}.
 * */
public class DynamicsTrajectory extends AbstractDynamics {

	private RocketParameter rocket;
	private AeroParameter aero;
	private Atmosphere atm;
	private Wind wind;

	private MathematicalVector
	windENU,
	velAirENU,
	velAirBODY,
	g,
	thrust,
	forceAero,
	forceENU,
	armMoment,
	momentAeroDumping,
	momentJetDumping,
	momentGyro;

	public DynamicsTrajectory(ConstantVariable constant) {
		this.rocket = constant.getRocket();
		this.aero = constant.getAeroParam();
		this.atm = constant.getAtmosphere();
		this.wind = constant.getWind();

		windENU = new MathematicalVector(0.0, 0.0, 0.0);
		velAirENU = new MathematicalVector(0.0, 0.0, 0.0);
		velAirBODY = new MathematicalVector(0.0, 0.0, 0.0);
		thrust = new MathematicalVector(0.0, 0.0, 0.0);
		g = new MathematicalVector(0.0, 0.0, 0.0);
		forceAero = new MathematicalVector(0.0, 0.0, 0.0);
		forceENU = new MathematicalVector(0.0, 0.0, 0.0);
		armMoment = new MathematicalVector(0.0, 0.0, 0.0);
		momentAeroDumping = new MathematicalVector(0.0, 0.0, 0.0);
		momentJetDumping = new MathematicalVector(0.0, 0.0, 0.0);
		momentGyro = new MathematicalVector(0.0, 0.0, 0.0);
	}

	@Override
	public DynamicsMinuteChange calculateDynamics(Variable variable) {

		//TODO インスタンス生成の回数の減少(計算速度に密接に関係)

		// Import variable
		MathematicalVector vel_ENU = variable.getVel_ENU();
		MathematicalVector omega_BODY = variable.getOmega_Body();
		MathematicalVector quat = new MathematicalVector(Coordinate.nomalizeQuat(variable.getQuat().getValue()));
		double altitude = variable.getAltitude();
		double t = variable.getTime();

		double m = rocket.getMass(t);
		double m_dot = rocket.mdot(t);
		double p = omega_BODY.getValue()[0];
		double q = omega_BODY.getValue()[1];
		double r = omega_BODY.getValue()[2];

		//Translation coodinate
		MathematicalMatrix dcm_ENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.getValue()));
		MathematicalMatrix dcm_BODY2ENU = dcm_ENU2BODY.transpose();

		// alpha , beta
		//MathematicalVector wind_ENU = new MathematicalVector(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		windENU.set(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		//MathematicalVector vel_airENU = vel_ENU.substract(wind_ENU);
		//MathematicalVector vel_airBODY = dcm_ENU2BODY.dot(vel_airENU);
		velAirENU.set(vel_ENU.substract(windENU));
		velAirBODY.set(dcm_ENU2BODY.dot(velAirENU));
		double vel_airAbs = velAirBODY.norm();
		double v = velAirBODY.getValue()[1];
		double w = velAirBODY.getValue()[2];

		double alpha , beta; //angle of atack , angle of side-slip
		if(vel_airAbs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			alpha = Math.asin(w / vel_airAbs);
			beta = Math.asin(v / vel_airAbs);
		}

		//Environment
		//MathematicalVector g = new MathematicalVector(0.0 , 0.0 , -atm.getGravity(altitude));
		//double[] gArray = {0.0, 0.0, - atm.getGravity(altitude)};
		g.set(0.0, 0.0, - atm.getGravity(altitude));
		double P0 = atm.getAtomosphericPressure(0.0);
		double P = atm.getAtomosphericPressure(altitude);
		double rho = atm.getAirDensity(altitude);
		double Cs = atm.getSoundSpeed(altitude);
		double Mach = vel_airAbs / Cs;
		double dynamics_pressure = 0.5 * rho * Math.pow(vel_airAbs, 2);

		//Thrust
		//MathematicalVector thrust ;
		if(rocket.thrust(t) > 0.0) {
			double thrust_pressure = (P0 - P)* rocket.Ae;
			thrust.set(rocket.thrust(t) + thrust_pressure, 0.0, 0.0);
		}else {
			//thrust = new MathematicalVector(0.0, 0.0, 0.0);
			thrust.set(0.0, 0.0, 0.0);
		}

		//Aero Force
		double drag = dynamics_pressure * aero.Cd(Mach) * rocket.S;
		double nomal = dynamics_pressure * aero.CNa(Mach) * rocket.S * alpha;
		double side = dynamics_pressure * aero.CNa(Mach) * rocket.S * beta;
		//MathematicalVector f_aero = new MathematicalVector(- drag , - side , - nomal);
		forceAero.set(- drag , - side , - nomal);

		//Newton Equation
		MathematicalVector force_ENU = dcm_BODY2ENU.dot(thrust.add(forceAero));


		//Accelaration
		MathematicalVector acc_ENU = force_ENU.multiply(1/m).add(g);

		//Center of Gravity , Pressure
		double Lcg = rocket.getLcg(t);
		double Lcg_p = rocket.lcgPropBef;
		double Lcp = aero.Lcp(Mach);

		//Inretia Moment
		double Ij_roll = rocket.Ij_roll(t);
		double Ij_pitch = rocket.Ij_pitch(t);
		double[] Ij = {Ij_roll, Ij_pitch, Ij_pitch};

		double Ij_dot[] = rocket.Ij_dot(t);

		//Aero Moment
		//MathematicalVector arm_moment = new MathematicalVector(Lcg - Lcp, 0.0, 0.0);
		armMoment.set(Lcg - Lcp, 0.0, 0.0);
		MathematicalVector moment_aero = armMoment.cross(forceAero);

		//Aero Dumping Moment
		/*MathematicalVector moment_aeroDumping = new MathematicalVector(dynamics_pressure * aero.Clp * rocket.S * (0.5*Math.pow(rocket.d, 2)/vel_airAbs) * p,
				dynamics_pressure * aero.Cmq * rocket.S *(0.5*Math.pow(rocket.l, 2)/vel_airAbs) * q,
				dynamics_pressure * aero.Cnr * rocket.S *(0.5*Math.pow(rocket.l, 2)/vel_airAbs) * r);*/

		momentAeroDumping.set(
				dynamics_pressure * aero.Clp * rocket.S * (0.5*Math.pow(rocket.d, 2)/vel_airAbs) * p,
				dynamics_pressure * aero.Cmq * rocket.S *(0.5*Math.pow(rocket.l, 2)/vel_airAbs) * q,
				dynamics_pressure * aero.Cnr * rocket.S *(0.5*Math.pow(rocket.l, 2)/vel_airAbs) * r);

		//Jet Dumping Moment
		/*MathematicalVector moment_jetDumping = new MathematicalVector((-Ij_dot[0] - m_dot * 0.5 * (0.25*Math.pow(rocket.de, 2))) * p,
				(-Ij_dot[1] - m_dot * (Math.pow(Lcg-Lcg_p, 2) - Math.pow(rocket.l-Lcg_p, 2))) * q,
				(-Ij_dot[2] - m_dot * (Math.pow(Lcg-Lcg_p, 2) - Math.pow(rocket.l-Lcg_p, 2))) * r);*/

		momentJetDumping.set(
				(-Ij_dot[0] - m_dot * 0.5 * (0.25*Math.pow(rocket.de, 2))) * p,
				(-Ij_dot[1] - m_dot * (Math.pow(Lcg-Lcg_p, 2) - Math.pow(rocket.l-Lcg_p, 2))) * q,
				(-Ij_dot[2] - m_dot * (Math.pow(Lcg-Lcg_p, 2) - Math.pow(rocket.l-Lcg_p, 2))) * r);

		/*MathematicalVector moment_gyro = new MathematicalVector((Ij[1] - Ij[2])*q*r,
				(Ij[2] - Ij[0])*p*r,
				(Ij[0] - Ij[1])*p*q);*/
		momentGyro.set(
				(Ij[1] - Ij[2])*q*r,
				(Ij[2] - Ij[0])*p*r,
				(Ij[0] - Ij[1])*p*q);

		MathematicalVector moment = momentGyro.add(moment_aero).add(momentAeroDumping).add(momentJetDumping);
		double[] moment_ = moment.getValue();
		MathematicalVector omegadot = new MathematicalVector(
				moment_[0] / Ij[0],
				moment_[1] / Ij[1],
				moment_[2] / Ij[2]);

		//Kinematics Equation
		MathematicalMatrix tensor = new MathematicalMatrix(Coordinate.Omega_tensor(p, q, r));
		MathematicalVector quatdot = tensor.dot(quat).multiply(0.5);

		DynamicsMinuteChange delta = new DynamicsMinuteChange();
		delta.deltaPos_ENU = vel_ENU;
		delta.deltaVel_ENU = acc_ENU;
		delta.deltaOmega_Body = omegadot;
		delta.deltaQuat = quatdot;

		return delta;
	}
}
