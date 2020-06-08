package quabla.simulator.rocket;

import quabla.parameter.InputParam;
import quabla.simulator.Coordinate;
import quabla.simulator.GetCsv;
import quabla.simulator.numerical_analysis.Interpolation;

/**
 * Wind deals with wind, e.g. wind speed, direction.
 * */
public class Wind {

	Interpolation speed_analy, direction_analy;
	boolean Wind_file_exist;
	private double wind_speed,
	Zr,
	wind_azimuth,
	/** exponetial of power function*/
	Cdv;
	private static double magnetic_dec;

	//落下分散の時は,spec.wind_file_exist, spec.wind_speed, spec.wind_azimuthを書き換える

	public Wind(InputParam spec) {

		this.Wind_file_exist = spec.Wind_file_exsit;
		this.wind_speed = spec.wind_speed;
		this.Zr = spec.Zr;
		this.wind_azimuth = spec.wind_azimuth ;
		this.Cdv = spec.Cdv;
		magnetic_dec = spec.magnetic_dec;

		if(spec.Wind_file_exsit) {
			/* 1st Column : altitude [m]
			 * 2nd Column : wind speed [m/s]
			 * 3rd Column : direction [deg] **/
			double[][] wind_data = GetCsv.get3ColumnArray(spec.wind_file);
			double[] alt_array = new double[wind_data.length];
			double[] speed_array = new double[wind_data.length];
			double[] direction_array = new double[wind_data.length];
			for(int i = 0; i < wind_data.length ; i++) {
				alt_array[i] = wind_data[i][0];
				speed_array[i] = wind_data[i][1];
				direction_array[i] = wind_data[i][2];
			}
			this.speed_analy = new Interpolation(alt_array, speed_array);
			this.direction_analy = new Interpolation(alt_array, direction_array);
		}

	}


	/**
	 * @param alt [m]
	 * @return speed [m/s]
	 * */
	public double getWindSpeed(double alt) {
		double speed;

		if(Wind_file_exist) {
			speed = speed_analy.linearInterp1column(alt);
		}else {// power law
			speed = getWindSpeedPowerLaw(alt, wind_speed, Zr, Cdv);
		}

		return speed;
	}


	/**
	 * @param alt [m]
	 * @return direction [rad]
	 * */
	public double getWindDirection(double alt) {
		double direction;

		if(Wind_file_exist) {
			direction = direction_analy.linearInterp1column(alt);
		}else {// power law
			direction = wind_azimuth;
		}

		return direction;
	}


	/**
	 * This function calcurate wind speed, if wind model is selected "power law".
	 * @param alt [m]
	 * @param ref_wind_speed [m/s]
	 * @param ref_alt [m] Refarence altitude
	 * @param wind_pow_exp [-]
	 * @return speed [m/s]
	 * If alt is below 0m, wind speed is 0 m/s.
	 * */
	private static double getWindSpeedPowerLaw(double alt, double ref_wind_speed, double ref_alt, double wind_pow_exp) {
		double windSpeed;
		if(alt <= 0.0) {
			windSpeed = 0.0;
		}else {
			windSpeed = ref_wind_speed * Math.pow(alt/ref_alt , 1.0/wind_pow_exp);
		}

		return windSpeed;
	}


	/**
	 * @param speed [m/s]
	 * @param direction [deg]
	 * @return wind_ENU
	 * */
	public static double[] windENU(double speed, double direction) {
		//wind_azimuth [deg] 磁北から時計回りを正
		double[] wind_ENU = new double[3];
		double wind_azimuth_ENU;//[rad] 磁東から反時計回りを正

		//-をつけて風向からの風になる
		wind_azimuth_ENU = Coordinate.deg2rad(- direction + 90.0 + magnetic_dec);
		wind_ENU[0] = - speed * Math.cos(wind_azimuth_ENU);
		wind_ENU[1] = - speed * Math.sin(wind_azimuth_ENU);
		wind_ENU[2] = 0.0;
		return wind_ENU;
	}


}
