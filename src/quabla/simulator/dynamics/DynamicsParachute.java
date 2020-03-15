package quabla.simulator.dynamics;

import quabla.simulator.Atmosphere;
import quabla.simulator.RocketParameter;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.VariableParachute;

public class DynamicsParachute {

	private RocketParameter rocket;
	private Atmosphere atm;
	private Wind wind;

	public DynamicsParachute(RocketParameter rocket, Atmosphere atm, Wind wind) {
		this.rocket = rocket;
		this.atm = atm;
		this.wind = wind;
	}

	public DynamicsMinuteChangeParachute calculateDynamics(VariableParachute variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double VelDescent = variable.getVelDescent();

		double m = rocket.getMass(t);

		//Wind , Velocity
		double[] wind_ENU = Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude));
		MathematicalVector vel_ENU = new MathematicalVector(wind_ENU[0], wind_ENU[1], VelDescent);

		//Environment
		double g = atm.getGravity(altitude);
		double rho = atm.getAirDensity(altitude);

		double CdS;
		if(rocket.para2Exist && altitude <= rocket.alt_para2) {
			CdS = rocket.CdS1 + rocket.CdS2;
		}else {
			CdS = rocket.CdS1;
		}

		double drag = 0.5 * rho * CdS * Math.pow(VelDescent, 2);
		double Acc = drag / m - g;

		//DynamicsMinuteChangeTrajectory delta = new DynamicsMinuteChangeTrajectory();
		/*delta.deltaPos_ENU = vel_ENU;
		delta.deltaVel_ENU = new MathematicalVector(0.0, 0.0, Acc);
		delta.deltaOmega_Body = new MathematicalVector(0.0, 0.0, 0.0);
		delta.deltaQuat = new MathematicalVector(0.0, 0.0, 0.0, 0.0);*/
		/*delta.setDeltaPos_ENU(vel_ENU);
		delta.setDeltaVelENU(new MathematicalVector(0.0, 0.0, Acc));
		delta.setDeltaOmegaBODY(new MathematicalVector(0.0, 0.0, 0.0));
		delta.setDeltaQuat(new MathematicalVector(0.0, 0.0, 0.0, 0.0));*/
		DynamicsMinuteChangeParachute delta = new DynamicsMinuteChangeParachute();
		delta.setDeltaPosENU(vel_ENU);
		delta.setDeltaVelDescent(Acc);



		return delta;
	}
}
