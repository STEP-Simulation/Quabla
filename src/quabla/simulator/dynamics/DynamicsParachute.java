package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;

public class DynamicsParachute extends AbstractDynamics{

	private Rocket rocket;

	MathematicalVector velENU = new MathematicalVector();

	DynamicsMinuteChangeParachute delta = new DynamicsMinuteChangeParachute();

	public DynamicsParachute(Rocket rocket) {
		this.rocket = rocket;
	}

	public DynamicsMinuteChangeParachute calculateDynamics(AbstractVariable variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double VelDescent = variable.getVelDescent();

		double m = rocket.getMass(t);

		//Wind , Velocity
		double[] wind_ENU = rocket.wind.getWindENU(altitude);
		velENU.set(wind_ENU[0], wind_ENU[1], VelDescent);

		//Environment
		double g = rocket.atm.getGravity(altitude);
		double rho = rocket.atm.getAirDensity(altitude);

		double CdS;
		if(rocket.para2Exist && altitude <= rocket.alt_para2) {
			CdS = rocket.CdS1 + rocket.CdS2;
		}else {
			CdS = rocket.CdS1;
		}

		double drag = 0.5 * rho * CdS * Math.pow(VelDescent, 2);
		double Acc = drag / m - g;

		delta.setDeltaPosENU(velENU);
		delta.setDeltaVelDescent(Acc);
		return delta;
	}
}
