package quabla.output;

import java.io.IOException;

import quabla.InputParam;
import quabla.simulator.AeroParameter;
import quabla.simulator.Atmosphere;
import quabla.simulator.Coordinate;
import quabla.simulator.Logger;
import quabla.simulator.RocketParameter;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.Interpolation;

//outputLineをOutput_log内で実行する
public class OutputLogTrajectory {

	/**
	 *出力するもの
	 *時間,位置x_ENU,位置y_ENU,位置z_ENU,対地速度Vel_x_ENU,対地速度Vel_y_ENU,対地速度Vel_z_ENU,
	 *角速度p,角速度q,角速度r,質量m,高さalttitude,距離downrange,対気速度Vel_air_abs,マッハ数Mach,迎角alpha,横滑り角beta,
	 *方位角azimuth,仰角elevation,ロール角roll,重心位置Lcg,圧力中心位置Lcp,全長安定比Fst,動圧dynamics_pressure,抗力drag,
	 *法線力nomal,横力side,推力thrust,力Force_x_Body,力Force_y_Body,力Force_z_Body,対地加速度Acc_x_ENU,対地加速度Acc_y_ENU,
	 *対地加速度Acc_z_ENU,機体加速度Acc_x_Body,機体加速度Acc_y_Body,機体加速度Acc_z_Body,スカラー加速度Acc_abs
	 * */

	private double dt;
	private String filename;
	private Interpolation pos_ENU_analy, vel_ENU_analy, omega_BODY_analy, quat_analy;
	private InputParam spec;
	private final String[] name = { "time [s]", "x_ENU [m]", "y_ENU [m]", "z_ENU [m]", "Vel_x_ENU [m/s]",
			"Vel_y_ENU [m/s]", "Vel_z_ENU [m/s]", "p [rad/s]", "q [rad/s]", "r [rad/s]", "quat0", "quat1", "quat2",
			"quat3", "quat_norm", "m [kg]", "alttitude [m]", "downrange [m]", "Vel_air_abs [m/s]", "Mach [-]",
			"alpha [deg]", "beta[deg]", "azimuth [deg]", "elevation [deg]", "roll [deg]", "Lcg [m]", "Lcp [m]",
			"Fst [-]", "dynamics_pressure[kPa]", "drag [N]", "nomal [N]", "side [N]", "thrust [N]", "Force_x_Body[N]",
			"Force_y_Body[N]", "Force_z_Body[N]", "Acc_x_ENU[m/s2]", "Acc_y_ENU[m/s2]", "Acc_z_ENU[m/s2]",
			"Acc_x_Body [m/s2]", "Acc_y_Body [m/s2]", "Acc_z_Body [m/s2]", "Acc_abs [m/s2]" };

	/**
	 * ファイル出力するクラスのコンストラクタ
	 *
	 * @param 出力先のファイルパス
	 * @throws IOException 指定されたファイルが存在するが通常ファイルではなくディレクトリである場合、存在せず作成もできない場合、またはなんらかの理由で開くことができない場合
	 * */
	public OutputLogTrajectory(String filename, InputParam spec, Logger logdata) {

		this.filename = filename;
		this.spec = spec;
		dt = this.spec.dt_output;

		pos_ENU_analy = new Interpolation(logdata.time_array, logdata.pos_ENU_log);
		vel_ENU_analy = new Interpolation(logdata.time_array, logdata.vel_ENU_log);
		omega_BODY_analy = new Interpolation(logdata.time_array, logdata.omega_BODY_log);
		quat_analy = new Interpolation(logdata.time_array, logdata.quat_log);

	}

	// Vector での計算に対応
	public void runOutputLine(double time_landing) {
		RocketParameter rocket = new RocketParameter(spec);
		Atmosphere atm = new Atmosphere(spec.temperture0);
		Wind wind = new Wind(spec);
		AeroParameter aero = new AeroParameter(spec);
		double t = 0.0;
		double[] pos_ENU = new double[3];
		double[] vel_ENU = new double[3];
		double[] omega_BODY = new double[3];
		double[] quat = new double[4];
		double quat_norm;
		double[][] dcm_ENU2BODY = new double[3][3];
		double[][] dcm_Body2ENU = new double[3][3];
		double m;
		double Lcg, Lcp, Fst;
		double thrust, pressure_thrust;
		double altitude, downrange;
		double[] g = new double[3];
		double P_air, rho;
		double P_air0 = atm.getAtomosphericPressure(0.0);
		double[] wind_ENU = new double[3];
		double[] vel_air_BODY = new double[3];
		double[] vel_air_ENU = new double[3];
		double Vel_air_abs = 0.0;
		double alpha, beta;
		double Mach, dynamics_pressure;
		double drag, nomal, side;
		double[] attitude = new double[3];
		double[] force_BODY = new double[3];
		//double Force_coriolis[]  = new double[3];
		double[] acc_BODY = new double[3];
		double[] acc_ENU = new double[3];
		double acc_abs;

		OutputCsv flightlog = null;

		try {
			flightlog = new OutputCsv(spec.result_filepath + filename + ".csv", name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			flightlog.outputFirstLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (int i = 0;; i++) {

			//入力したlog以外の値の計算
			t = dt * i;

			pos_ENU = pos_ENU_analy.linearInterpPluralColumns(t);
			vel_ENU = vel_ENU_analy.linearInterpPluralColumns(t);
			omega_BODY = omega_BODY_analy.linearInterpPluralColumns(t);
			quat = Coordinate.nomalizeQuat(quat_analy.linearInterpPluralColumns(t));
			quat_norm = Math.sqrt(Math.pow(quat[0], 2) + Math.pow(quat[1], 2) + Math.pow(quat[2], 2) + Math.pow(quat[3], 2));

			dcm_ENU2BODY = Coordinate.getDCM_ENU2BODYfromQuat(quat);
			dcm_Body2ENU = Coordinate.DCM_ENU2Body2DCM_Body2_ENU(dcm_ENU2BODY);

			m = rocket.getMass(t);
			Lcg = rocket.Lcg(t);

			altitude = pos_ENU[2];
			downrange = Math.sqrt(Math.pow(pos_ENU[0], 2) + Math.pow(pos_ENU[1], 2));

			g[0] = 0.0;
			g[1] = 0.0;
			g[2] = -atm.getGravity(altitude);
			P_air = atm.getAtomosphericPressure(altitude);
			rho = atm.getAirDensity(altitude);

			wind_ENU = Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude));
			for (int j = 0; j < 3; j++) {
				vel_air_ENU[j] = vel_ENU[j] - wind_ENU[j];
				}
			vel_air_BODY = Coordinate.vec_trans(dcm_ENU2BODY, vel_air_ENU);
			Vel_air_abs = Math
					.sqrt(Math.pow(vel_air_BODY[0], 2) + Math.pow(vel_air_BODY[1], 2) + Math.pow(vel_air_BODY[2], 2));
			if (Vel_air_abs <= 0.0) {
				alpha = 0.0;
				beta = 0.0;
			} else {
				alpha = Math.asin(vel_air_BODY[2] / Vel_air_abs);
				beta = Math.asin(vel_air_BODY[1] / Vel_air_abs);
			}
			Mach = Vel_air_abs / atm.getSoundSpeed(altitude);
			dynamics_pressure = 0.5 * rho * Math.pow(Vel_air_abs, 2);
			drag = dynamics_pressure * aero.Cd(Mach) * rocket.S;
			nomal = dynamics_pressure * aero.CNa(Mach) * rocket.S * alpha;
			side = dynamics_pressure * aero.CNa(Mach) * rocket.S * beta;

			Lcp = aero.Lcp(Mach);
			Fst = (Lcp - Lcg) / rocket.L * 100;

			attitude = Coordinate.getEulerFromDCM(dcm_ENU2BODY);

			thrust = rocket.thrust(t);
			if (thrust <= 0.0) {
				thrust = 0.0;
			} else {
				pressure_thrust = (P_air0 - P_air) * rocket.Ae;
				thrust += pressure_thrust;
			}

			force_BODY[0] = thrust - drag;
			force_BODY[1] = -side;
			force_BODY[2] = -nomal;

			acc_ENU = Coordinate.vec_trans(dcm_Body2ENU, force_BODY);
			for (int j = 0; j < 3; j++) {
				acc_ENU[j] = acc_ENU[j] / m + g[j];
			}
			acc_BODY = Coordinate.vec_trans(dcm_ENU2BODY, acc_ENU);
			acc_abs = Math.sqrt(Math.pow(acc_ENU[0], 2) + Math.pow(acc_ENU[1], 2) + Math.pow(acc_ENU[2], 2));

			//出力する値
			double[] result = set_result(t, pos_ENU, vel_ENU, omega_BODY, quat, quat_norm, m, altitude, downrange,
					Vel_air_abs,
					Mach, alpha, beta, attitude, Lcg, Lcp, Fst, dynamics_pressure, drag, nomal, side, thrust,
					force_BODY, acc_ENU,
					acc_BODY, acc_abs);

			try {
				flightlog.outputLine(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//result とnameの要素数が違った時の例外処理

			if (t >= time_landing) {
				break;
			}
		}

		try {
			flightlog.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 *
	 * @param t 時間[s]
	 * @param pos_ENU 位置[m]
	 * @param vel_ENU 対地速度[m/s]
	 * @param omega_BODY 角速度[rad/s](ロール,ピッチ,ヨー)
	 * @param quat クォータニオン
	 * @param quat_norm クォータニオンノルム
	 * @param m 質量[kg]
	 * @param altitude 高さ[m]
	 * @param downrange 距離[m]
	 * @param vel_air_abs 対気速度[m/s]
	 * @param Mach マッハ数[-]
	 * @param alpha 迎角[deg]
	 * @param beta 横滑り角[deg]
	 * @param attitude オイラー角[deg](方位角,仰角,ロール角)
	 * @param Lcg 重心位置[-]
	 * @param Lcp 圧力中心位置[m]
	 * @param Fst 全長安定比[-]
	 * @param dynamics_pressure 動圧[kPa]
	 * @param drag 抗力[N]
	 * @param nomal 法線力[N]
	 * @param side 横力[N]
	 * @param thrust 推力[N]
	 * @param force_BODY 力[N]
	 * @param acc_ENU 対地加速度[m/s2]
	 * @param acc_BODY 機体加速度[m/s2]
	 * @param acc_abs 加速度絶対値[m/s2]
	 * @throws IOException
	 * */

	private double[] set_result(double t, double[] pos_ENU, double[] vel_ENU, double[] omega_BODY, double[] quat,
			double quat_norm, double m, double altitude, double downrange, double vel_air_abs, double Mach,
			double alpha, double beta, double[] attitude, double Lcg, double Lcp, double Fst, double dynamics_pressure,
			double drag, double nomal, double side, double thrust, double[] force_BODY, double[] acc_ENU,
			double[] acc_BODY,
			double acc_abs) {
		double[] result = new double[name.length];

		result[0] = t;
		for (int j = 0; j < 3; j++) {
			result[1 + j] = pos_ENU[j];
			result[4 + j] = vel_ENU[j];
			result[7 + j] = omega_BODY[j];
		}
		for (int j = 0; j < 4; j++) {
			result[10 + j] = quat[j];
		}
		result[14] = quat_norm;
		result[15] = m;
		result[16] = altitude;
		result[17] = downrange;
		result[18] = vel_air_abs;
		result[19] = Mach;
		result[20] = Coordinate.rad2deg(alpha);
		result[21] = Coordinate.rad2deg(beta);
		for (int j = 0; j < 3; j++) {
			result[22 + j] = attitude[j];
		}
		result[25] = Lcg;
		result[26] = Lcp;
		result[27] = Fst;
		result[28] = dynamics_pressure * Math.pow(10, -3);
		result[29] = drag;
		result[30] = nomal;
		result[31] = side;
		result[32] = thrust;
		for (int j = 0; j < 3; j++) {
			result[33 + j] = force_BODY[j];
			result[36 + j] = acc_ENU[j];
			result[39 + j] = acc_BODY[j];
		}
		result[42] = acc_abs;

		return result;
	}

}
