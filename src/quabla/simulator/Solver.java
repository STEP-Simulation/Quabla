package quabla.simulator;

import java.io.IOException;

import quabla.InputParam;
import quabla.output.Output_log;
import quabla.simulator.numerical_analysis.EditArray;

public class Solver {

	/**
	 * on_launcher
	 * trajectory
	 * parachute
	 * */


	/**
	 * Solverの中でfor分でdynamicsを動かす感じで
	 * */

	/**
	 * logの記録をどうするか
	 * 配列に格納するか、逐次出力するか
	 * */

	InputParam spec;
	boolean single;
	//Wind wind;
	double azimuth0 , elevation0 , roll0;
	double Pos_ENU_landing_trajectory[] = new double[2];
	double Pos_ENU_landing_parachute[] = new double[2];

	public Solver(InputParam spec, boolean single) {
		this.spec = spec;
		this.single = single;

		//wind
		//this.wind = new Wind(spec);

		//Initial attitude
		this.azimuth0 = Coodinate.deg2rad((-spec.azimuth_launcher + 90.0) + spec.magnetic_dec);
		this.elevation0 = Coodinate.deg2rad( spec.elevation_launcher);
		if (elevation0 > 0.0)
			elevation0 *= -1.0;
		this.roll0 = Math.PI;//[rad]

	}


	public void solve_dynamics() {

		double X0_ENU , Y0_ENU , Z0_ENU;
		double x[] = new double[19];
		double dx[] = new double[19];
		double quat0[] = new double[4];
		double altitude ;
		double distance_Body_roll,distance_lower_lug;
		int index = 0;
		int index_launchclear,index_apogee=0,index_landing_trajectory,index_landing_parachute;
		double time_launchclear, time_apogee = 0.0,time_landing_trajectory,time_landing_parachute;
		double Vel_launchclear, alt_apogee=0.0;
		double time_array[] = new double[spec.n] ;
		double Pos_ENU_log[][] = new double[spec.n][3];
		double Vel_ENU_log[][] = new double[spec.n][3];
		double omega_Body_log[][] = new double[spec.n][3];
		double quat_log[][] = new double[spec.n][4];
		double Pos_ENU_parachute_log[][] = new double[spec.n][3];
		double t = 0.0;

		for(int i = 0; i < 19; i++) {
			x[i] = 0.0;
			dx[i] = 0.0;
		}


		Environment env = new Environment(spec.temperture0);
		Rocket_param rocket = new Rocket_param(spec);
		Aero_param aero = new Aero_param(spec);
		Wind wind = new Wind(spec);


		//initial position
		X0_ENU = (rocket.L - rocket.Lcg_0)*Math.cos(Math.abs(elevation0))*Math.cos(azimuth0);
		Y0_ENU = (rocket.L - rocket.Lcg_0)*Math.cos(Math.abs(elevation0))*Math.sin(azimuth0);
		Z0_ENU = (rocket.L - rocket.Lcg_0)*Math.sin(Math.abs(elevation0));

		//initial attitude
		quat0 = Coodinate.euler2quat(azimuth0, elevation0, roll0);


		//on launcher===============================
		double X0[] = {X0_ENU, Y0_ENU, Z0_ENU, 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0};

		for(int i = 0; i < 12 ; i++) {
			x[i] = X0[i];
		}
		for(;;) {
			t = index * spec.dt;

			dx = Dynamics.on_luncher(x, t, rocket, env, aero , wind , quat0);
			for(int j=0; j<12; j++)
				x[j] += dx[j] * rocket.dt;

			//flight log=========================
			time_array[index] = t;
			for(int j=0; j<3; j++) {
				Pos_ENU_log[index][j] = x[j];
				Vel_ENU_log[index][j] = x[6+j];
				omega_Body_log[index][j] = 0.0;
			}
			for(int j=0; j<4; j++)
				quat_log[index][j] = quat0[j];
			//===================================


			//launch clear判定
			distance_Body_roll = x[3];
			distance_lower_lug = (rocket.L-rocket.lower_lug) + distance_Body_roll;
			if(distance_lower_lug >= spec.length_Launcher) {//下部ラグがレールを抜けたときランチクリア
				time_launchclear = t;
				index_launchclear = index;
				Vel_launchclear = Math.sqrt(x[6]*x[6] + x[7]*x[7] + x[8]*x[8]);
				break;
			}

			index ++;
		}



		for(int i=0; i<19; i++) {
			x[i] = 0.0;
		}
		//trajectoryにPos_ENU,Vel_ENU,omega_Body,quatを渡す
		for(int i=0; i<3; i++) {
			x[i] = Pos_ENU_log[index_launchclear][i];
			x[3+i] = Vel_ENU_log[index_launchclear][i];
			x[6+i] = omega_Body_log[index_launchclear][i];
		}
		for(int i=0; i<4; i++)
			x[9+i] = quat_log[index_launchclear][i];



		//trajectory==================================
		for(;;) {
			t = index * rocket.dt;

			dx = Dynamics.trajectory(x, t, rocket, env, aero, wind);
			for(int j=0; j<13; j++)
				x[j] += dx[j] * rocket.dt;

			//flight log==================
			time_array[index] = t;
			altitude = x[2];
			for(int j=0; j<3; j++) {
				Pos_ENU_log[index][j] = x[j];
				Vel_ENU_log[index][j] = x[3+j];
				omega_Body_log[index][j] = x[6+j];
			}
			for(int j=0; j<4; j++)
				quat_log[index][j] = x[9+j];
			//============================

			//頂点判定
			if(altitude >= alt_apogee) {
				index_apogee = index;
				alt_apogee = altitude;
				time_apogee = t;
			}

			//着地判定
			if(t > rocket.t_burnout && altitude <=0.0) {
				index_landing_trajectory = index;
				time_landing_trajectory = t;

				break;
			}

			index ++;
		}
		for(int i=0; i<2; i++)
			Pos_ENU_landing_trajectory[i] = Pos_ENU_log[index_landing_trajectory][i];
		//=========================================


		altitude = 0.0;
		for(int i=0; i<19; i++) {
			x[i] = 0.0;
		}
		//parachuteにPos_ENU,Vel_descentを渡す
		for(int i=0; i<3; i++)
			x[i] = Pos_ENU_log[index_apogee][i];
		x[3] = Vel_ENU_log[index_apogee][2];




		index = index_apogee;
		//parachute===============================
		for( ; ; ) {
			t = index * rocket.dt;

			dx = Dynamics.parachute(x, t, rocket, env, wind);
			for(int j=0; j<4; j++)
				x[j] += dx[j] * rocket.dt;

			//flight log=====================

			altitude = x[2];
			for(int j=0; j<3; j++) {
				Pos_ENU_parachute_log[index][j] = x[j];
			}

			//着地判定
			if(t > rocket.t_burnout && altitude <= 0.0) {
				time_landing_parachute = t;
				index_landing_parachute = index;
				break;
			}
			index ++;
		}
		for(int i=0; i<2; i++)
			Pos_ENU_landing_parachute[i] = Pos_ENU_parachute_log[index_landing_parachute][i];
		//========================================


		//結果の出力
		if(single) {
			Output_log output_log = null;

			/**
			 * spec
			 * time_array
			 * Pos_ENU_log
			 * Vel_ENU_log
			 * omega_Body_log
			 * quat_log
			 * をOutput_logに送る
			 * それ以外のlogはOutput_log内で計算
			 * */

			time_array = EditArray.cut_array(index_landing_trajectory, time_array);

			try {
				output_log = new Output_log(spec.result_filepath+"trajectory_log_data.csv",spec,index_landing_trajectory,time_array,Pos_ENU_log,Vel_ENU_log,omega_Body_log,quat_log);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			System.out.println("Vel_launchclear = "+ Vel_launchclear+"[m/s]");
			System.out.println("time_launchclear = "+time_launchclear+"[s]");
			System.out.println("trajectory landing:"+Pos_ENU_landing_trajectory[0]
					+","+Pos_ENU_landing_trajectory[1]);
			System.out.println("max alttitude = "+alt_apogee);
			System.out.println("t_apogee = "+time_apogee );
			System.out.println("time landing parachute = "+time_landing_parachute+"[s]");
			//ToDo .txtで出力を行う
			//ToDo マッハ数、動圧、Max-Qの時間を出力できるようにする

			try {
				output_log.outputFirstLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			output_log.run_output_line(time_landing_trajectory);


		}

	}

}


