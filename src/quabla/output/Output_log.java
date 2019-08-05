package quabla.output;

import java.io.IOException;

import quabla.InputParam;
import quabla.simulator.Aero_param;
import quabla.simulator.Coodinate;
import quabla.simulator.Environment;
import quabla.simulator.Rocket_param;
import quabla.simulator.Wind;
import quabla.simulator.numerical_analysis.Interpolation;


//outputLineをOutput_log内で実行する
public class Output_log {

	//別クラスに移行=====

	/*
	private BufferedWriter writer;
	private String firstline = "time [s],x_ENU [m],y_ENU [m],z_ENU [m],Vel_x_ENU [m/s],Vel_y_ENU [m/s],Vel_z_ENU [m/s],"
			+ "p [rad/s],q [rad/s],r [rad/s],q0,q1,q2,q3,quat_norm,"
			+ "m [kg],alttitude [m],downrange [m],Vel_air_abs [m/s],Mach [-],"
			+ "alpha [deg],beta[deg],azimuth [deg],elevation [deg],roll [deg],Lcg [m],Lcp [m],Fst [-],dynamics_pressure[kPa],"
			+ "drag [N],nomal [N],side [N],thrust [N], Force_x_Body[N],Force_y_Body[N],Force_z_Body[N],"
			+ "Acc_x_ENU[m/s2],Acc_y_ENU[m/s2],Acc_z_ENU[m/s2],Acc_x_Body [m/s2],Acc_y_Body [m/s2],Acc_z_Body [m/s2],"
			+ "Acc_abs [m/s2]";
	*/
	/**
	 *出力するもの
	 *時間,位置x_ENU,位置y_ENU,位置z_ENU,対地速度Vel_x_ENU,対地速度Vel_y_ENU,対地速度Vel_z_ENU,
	 *角速度p,角速度q,角速度r,質量m,高さalttitude,距離downrange,対気速度Vel_air_abs,マッハ数Mach,迎角alpha,横滑り角beta,
	 *方位角azimuth,仰角elevation,ロール角roll,重心位置Lcg,圧力中心位置Lcp,全長安定比Fst,動圧dynamics_pressure,抗力drag,
	 *法線力nomal,横力side,推力thrust,力Force_x_Body,力Force_y_Body,力Force_z_Body,対地加速度Acc_x_ENU,対地加速度Acc_y_ENU,
	 *対地加速度Acc_z_ENU,機体加速度Acc_x_Body,機体加速度Acc_y_Body,機体加速度Acc_z_Body,スカラー加速度Acc_abs
	 * */
	/*
	private String format = "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,"
			+ "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,"
			+ "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,"
			+ "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,"
			+ "%f,%f,%f";
	*/
	//==============

	private double dt;
	//private double landing_time;
	private Interpolation Pos_X_ENU_analy,Pos_Y_ENU_analy,Pos_Z_ENU_analy;
	private Interpolation Vel_X_ENU_analy,Vel_Y_ENU_analy,Vel_Z_ENU_analy;
	private Interpolation p_analy,q_analy,r_analy;
	private Interpolation quat0_analy,quat1_analy,quat2_analy,quat3_analy;
	private InputParam spec;
	private static String[] name = {"time [s]","x_ENU [m]","y_ENU [m]","z_ENU [m]","Vel_x_ENU [m/s]","Vel_y_ENU [m/s]","Vel_z_ENU [m/s]",
			"p [rad/s]","q [rad/s]","r [rad/s]","quat0","quat1","quat2","quat3","quat_norm","m [kg]","alttitude [m]","downrange [m]",
			"Vel_air_abs [m/s]","Mach [-]","alpha [deg]","beta[deg]","azimuth [deg]","elevation [deg]","roll [deg]",
			"Lcg [m]","Lcp [m]","Fst [-]","dynamics_pressure[kPa]","drag [N]","nomal [N]","side [N]","thrust [N]",
			"Force_x_Body[N]","Force_y_Body[N]","Force_z_Body[N]","Acc_x_ENU[m/s2]","Acc_y_ENU[m/s2]","Acc_z_ENU[m/s2]",
			"Acc_x_Body [m/s2]","Acc_y_Body [m/s2]","Acc_z_Body [m/s2]","Acc_abs [m/s2]"};


	/**
	 * ファイル出力するクラスのコンストラクタ
	 *
	 * @param 出力先のファイルパス
	 * @throws IOException 指定されたファイルが存在するが通常ファイルではなくディレクトリである場合、存在せず作成もできない場合、またはなんらかの理由で開くことができない場合
	 * */
	public Output_log(InputParam spec,int index_max,double time_array[],double Pos_log[][], double Vel_log[][],double omega_log[][],double quat_log[][]) {
	//	writer = new BufferedWriter(new FileWriter(filepath));
		//dt = step;
		this.spec = spec;
		dt = this.spec.dt_output;
		int length = index_max;



		double Pos_X_ENU_log[] = new double[length];
		double Pos_Y_ENU_log[] = new double[length];
		double Pos_Z_ENU_log[] = new double[length];
		double Vel_X_ENU_log[] = new double[length];
		double Vel_Y_ENU_log[] = new double[length];
		double Vel_Z_ENU_log[] = new double[length];
		double p_Body_log[] = new double[length];
		double q_Body_log[] = new double[length];
		double r_Body_log[] = new double[length];
		double quat0_log[] = new double[length];
		double quat1_log[] = new double[length];
		double quat2_log[] = new double[length];
		double quat3_log[]= new double[length];
		for(int i=0; i<length; i++) {
			Pos_X_ENU_log[i] = Pos_log[i][0];
			Pos_Y_ENU_log[i] = Pos_log[i][1];
			Pos_Z_ENU_log[i] = Pos_log[i][2];
			Vel_X_ENU_log[i] = Vel_log[i][0];
			Vel_Y_ENU_log[i] = Vel_log[i][1];
			Vel_Z_ENU_log[i] = Vel_log[i][2];
			 p_Body_log[i] = omega_log[i][0];
			 q_Body_log[i] = omega_log[i][1];
			 r_Body_log[i] = omega_log[i][2];
			 quat0_log[i] = quat_log[i][0];
			 quat1_log[i] = quat_log[i][1];
			 quat2_log[i] = quat_log[i][2];
			 quat3_log[i] = quat_log[i][3];

		}

		//解析用のインスタンスの作成
		Pos_X_ENU_analy = new Interpolation(time_array,Pos_X_ENU_log);
		Pos_Y_ENU_analy = new Interpolation(time_array,Pos_Y_ENU_log);
		Pos_Z_ENU_analy = new Interpolation(time_array,Pos_Z_ENU_log);
		Vel_X_ENU_analy = new Interpolation(time_array,Vel_X_ENU_log);
		Vel_Y_ENU_analy = new Interpolation(time_array,Vel_Y_ENU_log);
		Vel_Z_ENU_analy = new Interpolation(time_array,Vel_Z_ENU_log);
		p_analy = new Interpolation(time_array,p_Body_log);
		q_analy = new Interpolation(time_array,q_Body_log);
		r_analy = new Interpolation(time_array,r_Body_log);
		quat0_analy= new Interpolation(time_array,quat0_log);
		quat1_analy= new Interpolation(time_array,quat1_log);
		quat2_analy= new Interpolation(time_array,quat2_log);
		quat3_analy= new Interpolation(time_array,quat3_log);

	}


	/**
	 * 一行目に書き込む文字列を書き込みます
	 * @throws IOException 入出力エラーが発生した場合
	 * */
	/*
	public void outputFirstLine() throws IOException {
		writer.write(firstline);
		writer.newLine();
	}
	*/

	public void run_output_line(double landing_time) {
		Rocket_param rocket = new Rocket_param(spec);
		Environment env = new Environment(spec.temperture0);
		Wind wind = new Wind(spec);
		Aero_param aero = new Aero_param(spec);
		double t = 0.0;
		double Pos_ENU[] = new double[3];
		double Vel_ENU[] = new double[3];
		double omega_Body[] = new double[3];
		double quat[] = new double[4];
		double quat_norm;
		double DCM_ENU2Body[][] = new double[3][3];
		double DCM_Body2ENU[][] = new double[3][3];
		double m;
		//double p,q,r;
		double Lcg,Lcp,Fst;
		double thrust,pressure_thrust;
		double altitude, downrange;
		double g[] = new double[3];
		double P_air,rho;
		double P_air0 = env.atomospheric_pressure(0);
		double wind_ENU[] = new double[3];
		double Vel_air_Body[] = new double[3];
		double Vel_air_ENU[] = new double[3];
		double Vel_air_abs = 0.0;
		double alpha,beta;
		double Mach,dynamics_pressure;
		double drag,nomal,side;
		double attitude[] = new double[3];
		double Force[] = new double[3];
		//double Force_coriolis[]  = new double[3];
		double Acc_Body[] = new double[3];
		double Acc_ENU[] = new double[3];
		double Acc_abs;
		//double[] result = new double[name.length];

		OutputCsv flightlog = null;

		try {
			 flightlog = new OutputCsv(spec.result_filepath+"flightlog_trajectory.csv",name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			flightlog.outputFirstLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		for(int i=0; ; i++) {

			//入力したlog以外の値の計算
			t = dt * i;

			Pos_ENU[0] = Pos_X_ENU_analy.linear_interpolation(t);
			Pos_ENU[1] = Pos_Y_ENU_analy.linear_interpolation(t);
			Pos_ENU[2] = Pos_Z_ENU_analy.linear_interpolation(t);
			Vel_ENU[0] = Vel_X_ENU_analy.linear_interpolation(t);
			Vel_ENU[1] = Vel_Y_ENU_analy.linear_interpolation(t);
			Vel_ENU[2] = Vel_Z_ENU_analy.linear_interpolation(t);
			omega_Body[0] = p_analy.linear_interpolation(t);
			omega_Body[1] = q_analy.linear_interpolation(t);
			omega_Body[2] = r_analy.linear_interpolation(t);
			quat[0] = quat0_analy.linear_interpolation(t);
			quat[1] = quat1_analy.linear_interpolation(t);
			quat[2] = quat2_analy.linear_interpolation(t);
			quat[3] = quat3_analy.linear_interpolation(t);
			quat_norm = Math.sqrt(Math.pow(quat[0], 2) + Math.pow(quat[1], 2) + Math.pow(quat[2], 2) + Math.pow(quat[3], 2));

			DCM_ENU2Body = Coodinate.quat2DCM_ENU2Body(quat);
			DCM_Body2ENU = Coodinate.DCM_ENU2Body2DCM_Body2_ENU(DCM_ENU2Body);

			m = rocket.mass(t);
			Lcg = rocket.Lcg(t);

			altitude = Pos_ENU[2];
			downrange = Math.sqrt(Math.pow(Pos_ENU[0], 2) + Math.pow(Pos_ENU[1], 2));

			g[0] = 0.0;
			g[1] = 0.0;
			g[2] = - env.gravity(altitude);
			P_air = env.atomospheric_pressure(altitude);
			rho = env.density_air(altitude);

			wind_ENU = Wind.wind_ENU(wind.wind_speed(altitude), wind.wind_direction(altitude));
			for(int j=0;j<3;j++)
				Vel_air_ENU[j] = Vel_ENU[j] - wind_ENU[j];
			Vel_air_Body = Coodinate.vec_trans(DCM_ENU2Body, Vel_air_ENU);
			Vel_air_abs = Math.sqrt(Math.pow(Vel_air_Body[0], 2) + Math.pow(Vel_air_Body[1], 2) + Math.pow(Vel_air_Body[2], 2));
			if(Vel_air_abs <= 0.0) {
				alpha = 0.0;
				beta = 0.0;
			}else {
				alpha =Math.asin(Vel_air_Body[2]/Vel_air_abs);
				beta = Math.asin(Vel_air_Body[1]/Vel_air_abs);
			}
			Mach = Vel_air_abs / env.soundspeed(altitude);
			dynamics_pressure = 0.5 * rho * Math.pow(Vel_air_abs, 2);
			drag = dynamics_pressure * aero.Cd(Mach) * rocket.S;
			nomal = dynamics_pressure * aero.CNa(Mach) * rocket.S * alpha;
			side = dynamics_pressure * aero.CNa(Mach) * rocket.S * beta;

			Lcp = aero.Lcp(Mach);
			Fst = (Lcp - Lcg)/rocket.L*100;

			attitude = Coodinate.DCM2euler(DCM_ENU2Body);

			thrust = rocket.thrust(t);
			if(thrust<=0.0) {
				thrust = 0.0;
			}else {
				pressure_thrust = (P_air0 - P_air)* rocket.Ae;
				thrust += pressure_thrust;
			}

			Force[0] = thrust - drag;
			Force[1] = - side;
			Force[2] = - nomal;

			Acc_ENU = Coodinate.vec_trans(DCM_Body2ENU, Force);
			for(int j=0; j<3; j++) {
				Acc_ENU[j] = Acc_ENU[j]/m +g[j];
			}
			Acc_Body = Coodinate.vec_trans(DCM_ENU2Body, Acc_ENU);
			Acc_abs = Math.sqrt(Math.pow(Acc_ENU[0], 2) + Math.pow(Acc_ENU[1], 2) + Math.pow(Acc_ENU[2], 2));


			//出力する値
			double[] result = set_result(t, Pos_ENU, Vel_ENU,omega_Body, quat, quat_norm,m,altitude,downrange,Vel_air_abs,
					Mach,alpha,beta, attitude,Lcg, Lcp, Fst, dynamics_pressure, drag, nomal, side, thrust, Force,Acc_ENU,
					Acc_Body, Acc_abs);


			try {
				flightlog.outputLine(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//result とnameの要素数が違った時の例外処理


			/*
			//別クラスで処理する
			try {
				output_line(t, Pos_ENU, Vel_ENU,omega_Body, quat, quat_norm,m,altitude,downrange,Vel_air_abs, Mach,
						Coodinate.rad2deg(alpha), Coodinate.rad2deg(beta), attitude,
						 Lcg, Lcp, Fst, dynamics_pressure*Math.pow(10, -3), drag, nomal, side, thrust, Force,Acc_ENU,Acc_Body, Acc_abs);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			*/

			if(t >= landing_time) {
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
	 * @param Pos_ENU 位置[m]
	 * @param Vel_ENU 対地速度[m/s]
	 * @param omega_Body 角速度[rad/s](ロール,ピッチ,ヨー)
	 * @param quat クォータニオン
	 * @param quat_norm クォータニオンノルム
	 * @param m 質量[kg]
	 * @param altitude 高さ[m]
	 * @param downrange 距離[m]
	 * @param Vel_air_abs 対気速度[m/s]
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
	 * @param Force 力[N]
	 * @param Acc_ENU 対地加速度[m/s2]
	 * @param Acc_Body 機体加速度[m/s2]
	 * @param Acc_abs 加速度絶対値[m/s2]
	 * @throws IOException
	 * */

	private static double[] set_result(double t, double[] Pos_ENU, double[] Vel_ENU, double[] omega_Body, double[] quat,
			double quat_norm, double m, double altitude, double downrange, double Vel_air_abs,double Mach,
			double alpha, double beta, double[] attitude, double Lcg,double Lcp,double Fst, double dynamics_pressure,
			double drag, double nomal, double side, double thrust, double[] Force,double[] Acc_ENU,double[] Acc_Body,
			double Acc_abs) {
		double[] result = new double[name.length];

		result[0] = t;
		for(int j=0; j<3; j++) {
			result[1+j] = Pos_ENU[j];
			result[4+j] = Vel_ENU[j];
			result[7+j] = omega_Body[j];
		}
		for(int j=0; j<4; j++) {
			result[10+j] = quat[j];
		}
		result[14] = quat_norm;
		result[15] = m;
		result[16] = altitude;
		result[17] = downrange;
		result[18] = Vel_air_abs;
		result[19] = Mach;
		result[20] = Coodinate.rad2deg(alpha);
		result[21] = Coodinate.rad2deg(beta);
		for(int j=0; j<3; j++) {
			result[22+j] = attitude[j];
		}
		result[25] = Lcg;
		result[26] = Lcp;
		result[27] = Fst;
		result[28] = dynamics_pressure*Math.pow(10, -3);
		result[29] = drag;
		result[30] = nomal;
		result[31] = side;
		result[32] = thrust;
		for(int j=0; j<3; j++) {
			result[33+j] = Force[j];
			result[36+j] = Acc_ENU[j];
			result[39+j] = Acc_Body[j];
		}
		result[42] = Acc_abs;

		return result;
	}

	/*
	//別クラスに移行
	public void output_line(double t,double Pos_ENU[],double Vel_ENU[], double omega_Body[], double[] quat,double quat_norm, double m,
			double altitude, double downrange, double Vel_air_abs,double Mach,double alpha ,double beta,
			double attitude[], double Lcg, double Lcp, double Fst, double dynamics_pressure, double drag,
			double nomal, double side, double thrust, double Force[],double Acc_ENU[],double Acc_Body[],double Acc_abs) throws IOException {

		String linestr = String.format(format, t, Pos_ENU[0], Pos_ENU[1], Pos_ENU[2], Vel_ENU[0], Vel_ENU[1], Vel_ENU[2],
				omega_Body[0], omega_Body[1],omega_Body[2], quat[0], quat[1], quat[2], quat[3],quat_norm, m, altitude,downrange,Vel_air_abs, Mach, alpha, beta, attitude[0],
				attitude[1], attitude[2], Lcg, Lcp, Fst, dynamics_pressure, drag, nomal, side, thrust, Force[0], Force[1],
				Force[2],Acc_ENU[0],Acc_ENU[1],Acc_ENU[2],Acc_Body[0],Acc_Body[1],Acc_Body[2],Acc_abs);

		writer.write(linestr);//文字列をファイルに出力
		writer.newLine();//改行

	}
	*/


	/**
	 * 出力を終えます。
	 *
	 * @throws IOException
	 * */
	/*
	public void close() throws IOException {
		this.writer.close();
	}
	*/


}
