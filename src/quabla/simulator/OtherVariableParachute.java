package quabla.simulator;

import quabla.parameter.InputParam;

public class OtherVariableParachute {

	/**保存したい変数
	 * time
	 * pos_ENU
	 * vel_ENU
	 * altitude
	 * downrange
	 * */

	//private InputParam spec;
	private Wind wind;

	private double altitude;
	private double downrange;
	//private double velDescent;

	private double[] windENU = new double[3];
	private double[] velAirENU = new double[3];
	private double velAirAbs;

	public OtherVariableParachute(InputParam spec) {

		//this.spec = spec;
		wind = new Wind(spec);
	}

	public void calculateOtherVariable(double time, double[] pos_ENU, double[] vel_ENU) {
		altitude = pos_ENU[2];
		downrange = Math.sqrt(Math.pow(pos_ENU[0], 2) + Math.pow(pos_ENU[1], 2));
		//velDescent = vel_ENU[2];

		windENU = Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude));
		//velAirENU[2] = vel_ENU[2] - windENU[2];
		if(vel_ENU[2] <= 0.0) {
			velAirENU[0] = windENU[0];
			velAirENU[1] = windENU[1];
			velAirENU[2] = vel_ENU[2] - windENU[2];
		}else {
			for(int i = 0; i < 3; i++) {
				velAirENU[i] = vel_ENU[i] - windENU[i];
			}
		}
		velAirAbs = Math.sqrt(Math.pow(velAirENU[0], 2) + Math.pow(velAirENU[1], 2) + Math.pow(velAirENU[2], 2));
	}

	public double getAltitude() {
		return altitude;
	}

	public double getDownrange() {
		return downrange;
	}

	public double[] getWindENU() {
		return windENU;
	}

	public double[] getVelAirENU() {
		return velAirENU;
	}

	public double getVelAirAbs() {
		return velAirAbs;
	}
}
