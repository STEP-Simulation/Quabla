package quabla.simulator.variable;

import quabla.simulator.rocket.AbstractRocket;
import quabla.simulator.rocket.Atmosphere;
import quabla.simulator.rocket.Payload;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.rocket.wind.AbstractWind;

public class OtherVariableParachute {

	/**保存したい変数
	 * time
	 * pos_ENU
	 * vel_ENU
	 * altitude
	 * downrange
	 * */

	// private Rocket rocket;
	private final AbstractRocket rocket;
	private final Atmosphere atm;
	private final AbstractWind wind;

	private double altitude;
	private double downrange;

	private double[] velNED = new double[3];
	private double[] windNED = new double[3];
	private double[] velAirNED = new double[3];
	private double velAirAbs;
	private double CdS;
	private double drag;
	private double acc;
	private double mass;

	public OtherVariableParachute(Rocket rocket) {
		
		this.atm  = rocket.atm;
		this.wind = rocket.wind;
		this.rocket = rocket;
	}

	public OtherVariableParachute(Payload payload, Atmosphere atm, AbstractWind wind) {
		
		this.atm  = atm;
		this.wind = wind;
		this.rocket = payload;
	}

	public void calculateOtherVariable(double time, double[] posNED, double velDescent) {

		mass = rocket.getMass(time);

		altitude  = - posNED[2];
		downrange = Math.sqrt( posNED[0]*posNED[0] 
		                     + posNED[1]*posNED[1] );

		//Environment
		double g   = atm.getGravity(altitude);
		double rho = atm.getAirDensity(altitude);

		windNED = wind.getWindNED(altitude);
		velNED[0] = windNED[0];
		velNED[1] = windNED[1];
		velNED[2] = velDescent;

		velAirAbs = Math.sqrt( velAirNED[0]*velAirNED[0] 
		                     + velAirNED[1]*velAirNED[1] 
		                     + velAirNED[2]*velAirNED[2] );

		CdS = rocket.getCdS(time, altitude);

		// if (rocket.para2Exist) {
		// 	if (rocket.para2Timer) {
		// 		if(time >= rocket.time_para2) {
		// 			CdS = rocket.CdS1 + rocket.CdS2;
		// 		}else {
		// 			CdS = rocket.CdS1;
		// 		}
		// 	}else {
		// 		if (altitude <= rocket.alt_para2) {
		// 			CdS = rocket.CdS1 + rocket.CdS2;
		// 		}else {
		// 			CdS = rocket.CdS1;
		// 		}
		// 	}
		// }else {
		// 	CdS = rocket.CdS1;
		// }

		drag = 0.5 * rho * CdS * Math.pow(velDescent, 2);
		acc = - drag / mass + g;

	}

	public double getAltitude() {
		return altitude * Math.pow(10, -3);
	}

	public double getDownrange() {
		return downrange * Math.pow(10, -3);
	}

	public double[] getVelNED() {
		return velNED;
	}

	public double[] getWindNED() {
		return windNED;
	}

	public double[] getVelAirNED() {
		return velAirNED;
	}

	public double getVelAirAbs() {
		return velAirAbs;
	}

	public double getMass() {
		return mass;
	}

	public double getDrag() {
		return drag;
	}

	public double getAcc() {
		return acc;
	}

	public double getCdS() {
		return CdS;
	}
}
