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

/**
 * DynamicsTrajectory is an extension of {@link quabla.simulator.dynamics.AbstractDynamics}.
 * */
public class DynamicsTrajectory extends AbstractDynamics {

	private RocketParameter rocket;
	private AeroParameter aero;
	private Atmosphere atm;
	private Wind wind;

	public DynamicsTrajectory(ConstantVariable constant) {
		this.rocket = constant.getRocket();
		this.aero = constant.getAeroParam();
		this.atm = constant.getAtmosphere();
		this.wind = constant.getWind();

	}

	@Override
	public DynamicsMinuteChangeTrajectory calculateDynamics(Variable variable) {

		//TODO インスタンス生成の回数の減少(計算速度に密接に関係)

		// Import variable
		MathematicalVector velENU = variable.getVel_ENU();
		MathematicalVector omegaBODY = variable.getOmega_Body();
		MathematicalVector quat = new MathematicalVector(Coordinate.nomalizeQuat(variable.getQuat().getValue()));
		double altitude = variable.getAltitude();
		double t = variable.getTime();

		double m = rocket.getMass(t);
		double mDot = rocket.mdot(t);
		double p = omegaBODY.getValue()[0];
		double q = omegaBODY.getValue()[1];
		double r = omegaBODY.getValue()[2];

		//Translation coodinate
		MathematicalMatrix dcmENU2BODY = new MathematicalMatrix(Coordinate.getDCM_ENU2BODYfromQuat(quat.getValue()));
		MathematicalMatrix dcmBODY2ENU = dcmENU2BODY.transpose();

		// alpha , beta
		MathematicalVector windENU = new MathematicalVector(Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude)));
		MathematicalVector velAirENU = velENU.sub(windENU);
		MathematicalVector velAirBODY = dcmENU2BODY.dot(velAirENU);
		double velAirAbs = velAirBODY.norm();
		double v = velAirBODY.getValue()[1];
		double w = velAirBODY.getValue()[2];

		double alpha , beta; //angle of atack , angle of side-slip
		if(velAirAbs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			alpha = Math.asin(w / velAirAbs);
			beta = Math.asin(v / velAirAbs);
		}

		//Environment
		MathematicalVector g = new MathematicalVector(0.0 , 0.0 , -atm.getGravity(altitude));
		double P0 = atm.getAtomosphericPressure(0.0);
		double P = atm.getAtomosphericPressure(altitude);
		double rho = atm.getAirDensity(altitude);
		double Cs = atm.getSoundSpeed(altitude);
		double Mach = velAirAbs / Cs;
		double dynamicsPressure = 0.5 * rho * Math.pow(velAirAbs, 2);

		//Thrust
		MathematicalVector thrust ;
		if(rocket.thrust(t) > 0.0) {
			double thrustPressure = (P0 - P)* rocket.Ae;
			thrust = new MathematicalVector(rocket.thrust(t) + thrustPressure, 0.0, 0.0);
		}else {
			thrust = new MathematicalVector(0.0, 0.0, 0.0);
		}

		//Aero Force
		double drag = dynamicsPressure * aero.Cd(Mach) * rocket.S;
		double nomal = dynamicsPressure * aero.CNa(Mach) * rocket.S * alpha;
		double side = dynamicsPressure * aero.CNa(Mach) * rocket.S * beta;
		MathematicalVector forceAero = new MathematicalVector(- drag , - side , - nomal);

		//Newton Equation
		MathematicalVector forceENU = dcmBODY2ENU.dot(thrust.add(forceAero));


		//Accelaration
		MathematicalVector accENU = forceENU.multiply(1/m).add(g);

		//Center of Gravity , Pressure
		double lcg = rocket.getLcg(t);
		double lcgProp = rocket.getLcgProp(t);
		double lcp = aero.Lcp(Mach);

		//Inretia Moment
		double IjRoll = rocket.getIjRoll(t);
		double IjPitch = rocket.getIjPitch(t);
		double[] Ij = {IjRoll, IjPitch, IjPitch};

		double IjDotPitch = rocket.getIjDotPitch(t);
		double IjDotRoll = rocket.getIjDotRoll(t);
		double[] IjDot = {IjDotRoll, IjDotPitch, IjDotPitch};

		//Aero Moment
		MathematicalVector armMoment = new MathematicalVector(lcg - lcp, 0.0, 0.0);
		MathematicalVector momentAero = armMoment.cross(forceAero);

		//Aero Dumping Moment
		MathematicalVector momentAeroDamping = new MathematicalVector(
				dynamicsPressure * aero.Clp * rocket.S * (0.5*Math.pow(rocket.D, 2)/velAirAbs) * p,
				dynamicsPressure * aero.Cmq * rocket.S *(0.5*Math.pow(rocket.L, 2)/velAirAbs) * q,
				dynamicsPressure * aero.Cnr * rocket.S *(0.5*Math.pow(rocket.L, 2)/velAirAbs) * r);

		//Jet Dumping Moment
		MathematicalVector momentJetDamping = new MathematicalVector(
				(-IjDot[0] - mDot * 0.5 * (0.25*Math.pow(rocket.de, 2))) * p,
				(-IjDot[1] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * q,
				(-IjDot[2] - mDot * (Math.pow(lcg - lcgProp, 2) - Math.pow(rocket.L - lcgProp, 2))) * r);

		MathematicalVector momentGyro = new MathematicalVector(
				(Ij[1] - Ij[2])*q*r,
				(Ij[2] - Ij[0])*p*r,
				(Ij[0] - Ij[1])*p*q);

		MathematicalVector moment = momentGyro.add(momentAero).add(momentAeroDamping).add(momentJetDamping);
		double[] moment_ = moment.getValue();
		MathematicalVector omegadot = new MathematicalVector(
				moment_[0] / Ij[0],
				moment_[1] / Ij[1],
				moment_[2] / Ij[2]);

		//Kinematics Equation
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
