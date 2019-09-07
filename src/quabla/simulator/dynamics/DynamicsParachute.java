package quabla.simulator.dynamics;

import quabla.simulator.ConstantVariable;
import quabla.simulator.Atmosphere;
import quabla.simulator.RocketParameter;
import quabla.simulator.Variable;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class DynamicsParachute extends AbstractDynamics {

	private RocketParameter rocket;
	private Atmosphere atm;
	private Wind wind;

	public DynamicsParachute(ConstantVariable constant) {

		this.rocket = constant.getRocket();
		this.atm = constant.getAtmosphere();
		this.wind = constant.getWind();
	}

	@Override
	public DynamicsMinuteChange calculateDynamics(Variable variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double VelDescent = variable.getVelDescet();

		double m = rocket.getMass(t);

		//Wind , Velocity
		double[] wind_ENU = Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude));
		MathematicalVector vel_ENU = new MathematicalVector(wind_ENU[0], wind_ENU[1], VelDescent);

		//Environment
		double g = atm.getGravity(altitude);
		double rho = atm.getAirDensity(altitude);

		double CdS;
		if(rocket.para2_exist && altitude <= rocket.alt_para2) {
			CdS = rocket.CdS1 + rocket.CdS2;
		}else {
			CdS = rocket.CdS1;
		}

		double drag = 0.5 * rho * CdS * Math.pow(VelDescent, 2);
		double Acc = drag / m - g;

		DynamicsMinuteChange delta = new DynamicsMinuteChange();
		delta.deltaPos_ENU = vel_ENU;
		delta.deltaVel_ENU = new MathematicalVector(0.0, 0.0, Acc);
		delta.deltaOmega_Body = new MathematicalVector(0.0, 0.0, 0.0);
		delta.deltaQuat = new MathematicalVector(0.0, 0.0, 0.0, 0.0);

		return delta;
	}
}
