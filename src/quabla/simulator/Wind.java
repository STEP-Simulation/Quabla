package quabla.simulator;

import quabla.InputParam;
import quabla.simulator.numerical_analysis.Interpolation;

public class Wind {

	double wind_data[][] ;
	Interpolation speed_analy, direction_analy;
	boolean Wind_file_exist;
	private double wind_speed, Zr, wind_azimuth,Cdv;
	private static double magnetic_dec;

	//分散の時の風向,風速をどう変えるか
	//風の情報関連をいつセットするか

	//落下分散の時は,spec.wind_file_exist, spec.wind_speed, spec.wind_azimuthを書き換える

	public Wind(InputParam spec) {


		this.Wind_file_exist = spec.Wind_file_exsit;
		this.wind_speed = spec.wind_speed;
		this.Zr = spec.Zr;
		this.wind_azimuth = spec.wind_azimuth ;
		this.Cdv = spec.Cdv;
		magnetic_dec = spec.magnetic_dec;

		if(spec.Wind_file_exsit) {
			wind_data = GetCsv.get3ColumnArray(spec.wind_file);
			double alt_array[] = new double[wind_data.length];
			double speed_array[] = new double[wind_data.length];
			double direction_array[] = new double[wind_data.length];
			for(int i = 0; i < wind_data.length ; i++) {
				alt_array[i] = wind_data[i][0];
				speed_array[i] = wind_data[i][1];
				direction_array[i] = wind_data[i][2];
			}
			this.speed_analy = new Interpolation(alt_array, speed_array);
			this.direction_analy = new Interpolation(alt_array, direction_array);
		}

	}

	public double wind_speed(double alt) {
		double speed;

		if(Wind_file_exist) {
			speed = speed_analy.linear_interpolation(alt);
		}else {//べき法則
			speed = power_law(alt, wind_speed, Zr, Cdv);
		}

		return speed;
	}

	public double wind_direction(double alt) {
		double direction;

		if(Wind_file_exist) {
			direction = direction_analy.linear_interpolation(alt);
		}else {//べき法則
			direction = wind_azimuth;
		}


		//return Coodinate.deg2rad(direction);//radで返す
		return direction;
	}


	public static double power_law(double altitude, double ref_wind_speed, double ref_altitude, double wind_pow_exp) {
		double speed;
		speed = ref_wind_speed * Math.pow(altitude/ref_altitude , 1.0/wind_pow_exp);
		return speed;
	}

	public static double[] wind_ENU(double speed, double direction) {
		//wind_azimuth [deg] 磁北から時計回りを正
		double wind_ENU[] = new double[3];
		double wind_azimuth_ENU;//[rad] 磁東から反時計回りを正
		//todo 磁気偏角

		//-をつけて風向からの風になる
		wind_azimuth_ENU = Coordinate.deg2rad(- direction + 90.0 + magnetic_dec);
		wind_ENU[0] = - speed * Math.cos(wind_azimuth_ENU);
		wind_ENU[1] = - speed * Math.sin(wind_azimuth_ENU);
		wind_ENU[2] = 0.0;
		return wind_ENU;
	}


}
