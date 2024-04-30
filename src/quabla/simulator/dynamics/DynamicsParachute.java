package quabla.simulator.dynamics;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.AbstractVariable;

public class DynamicsParachute extends AbstractDynamics{

	private Rocket rocket;

	MathematicalVector velNED = new MathematicalVector();

	DynamicsMinuteChangeParachute delta = new DynamicsMinuteChangeParachute();

	public DynamicsParachute(Rocket rocket) {
		this.rocket = rocket;
	}

	public DynamicsMinuteChangeParachute calculateDynamics(AbstractVariable variable) {

		// Import variable
		double t = variable.getTime();
		double altitude = variable.getAltitude();
		double velDescent = variable.getVelDescent();

		double m = rocket.getMass(t);

		//Wind , Velocity
		double[] windENU = rocket.wind.getWindNED(altitude);
		velNED.set(windENU[0], windENU[1], velDescent);

		//Environment
		double g = rocket.atm.getGravity(altitude);
		double rho = rocket.atm.getAirDensity(altitude);

		double CdS;
//		if(rocket.para2Exist && altitude <= rocket.alt_para2) {
//			CdS = rocket.CdS1 + rocket.CdS2;
//		}else {
//			CdS = rocket.CdS1;
//		}
		if (rocket.para2Exist) {
			if (rocket.para2Timer) {
				if(t >= rocket.time_para2) {
					CdS = rocket.CdS1 + rocket.CdS2;
				}else {
					CdS = rocket.CdS1;
				}
			}else {
				if (altitude <= rocket.alt_para2) {
					CdS = rocket.CdS1 + rocket.CdS2;
				}else {
					CdS = rocket.CdS1;
				}
			}
		}else {
			CdS = rocket.CdS1;
		}

		double drag = 0.5 * rho * CdS * Math.pow(velDescent, 2);
		double acc = - drag / m + g;

		delta.setDeltaPosNED(velNED);
		delta.setDeltaVelDescent(acc);
		return delta;
	}
}
