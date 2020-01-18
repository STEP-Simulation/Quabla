package quabla.simulator;

import quabla.output.OutputLogParachute;
import quabla.output.OutputLogTrajectory;
import quabla.parameter.InputParam;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsOnLauncher;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.dynamics.DynamicsTrajectory;
import quabla.simulator.numerical_analysis.ODEsolverWithRK4;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class Solver {

	InputParam spec;
	boolean single;
	double[] pos_ENU_landing_trajectory = new double[2];
	double[] pos_ENU_landing_parachute = new double[2];
	//TODO 結果保存用のクラス作成

	public Solver(InputParam spec, boolean single) {
		this.spec = spec;
		this.single = single;
		//TODO singleをなくして,取得用の関数を作る
	}


	public void solve_dynamics() {
		int index = 0;
		int index_launchclear,index_apogee=0,index_LandingTrajectory,index_LandingParachute;
		double time_LaunchClear, time_apogee = 0.0,time_LandingTrajectory,time_LandingParachute;
		double vel_LaunchClear, alt_apogee = 0.0;
		double time = 0.0;
		final double h = spec.dt;

		Atmosphere atm = new Atmosphere(spec.temperture0);
		RocketParameter rocket = new RocketParameter(spec);
		AeroParameter aero = new AeroParameter(spec);
		Wind wind = new Wind(spec);
		ConstantVariable constant = new ConstantVariable(atm, rocket, aero, wind);
		Variable variableTrajectory = new Variable(spec,rocket);

		// Dynamics
		AbstractDynamics dynTrajectory = new DynamicsTrajectory(constant);
		AbstractDynamics dynOnLauncher = new DynamicsOnLauncher(constant);
		AbstractDynamics dynParachute = new DynamicsParachute(constant);

		// Logger
		Logger trajectoryLog = new Logger();
		Logger parachuteLog = new Logger();

		// ODE solver
		ODEsolverWithRK4 ODEsolver = new ODEsolverWithRK4(constant);

		FlightEventJudgement eventJudgement = new FlightEventJudgement(rocket) ;

		// Initial Variable
		variableTrajectory.setInitialVariable();
		trajectoryLog.logVariable(variableTrajectory);


		////////// on Launcher //////////
		for(;;) {
			index ++;
			time = index * h;
			// solve ODE
			variableTrajectory.renewVariable(time, ODEsolver.runRK4(variableTrajectory, dynOnLauncher));
			// store flightlog
			trajectoryLog.logVariable(variableTrajectory);

			if(eventJudgement.judgeLaunchClear(variableTrajectory)) {
				//TODO イベント値の記録用のクラスを作る
				time_LaunchClear = variableTrajectory.getTime();
				index_launchclear = index;
				vel_LaunchClear = variableTrajectory.getVel_ENU().norm();
				break;
			}
		}


		////////// Trajectory //////////
		for(;;) {
			index ++;
			time = index * h;
			variableTrajectory.renewVariable(time, ODEsolver.runRK4(variableTrajectory, dynTrajectory));
			trajectoryLog.logVariable(variableTrajectory);

			if(eventJudgement.judgeApogee(variableTrajectory)) {
				index_apogee = index;
				alt_apogee = variableTrajectory.getAltitude();
				time_apogee = variableTrajectory.getTime();
			}

			if(eventJudgement.judgeLanding(variableTrajectory)) {
				index_LandingTrajectory = index;
				time_LandingTrajectory = variableTrajectory.getTime();
				break;
			}
		}
		System.arraycopy(trajectoryLog.getPos_ENU(index_LandingTrajectory).getValue(), 0, pos_ENU_landing_trajectory, 0, 2);


		parachuteLog.copyLog(index_apogee, trajectoryLog);
		Variable variableParachute = new Variable(spec,rocket);
		//頂点時のvariableを渡す
		variableParachute.setPos_ENU(trajectoryLog.getPos_ENU(index_apogee));;
		variableParachute.setVel_ENU(trajectoryLog.getVel_ENU(index_apogee));
		variableParachute.setOmega_Body(new MathematicalVector(0.0, 0.0, 0.0));
		variableParachute.setQuat(new MathematicalVector(0.0, 0.0, 0.0, 0.0));

		index = index_apogee; //indexの更新
		////////// Parachute //////////
		for( ; ; ) {
			index ++;
			time = index * h;
			variableParachute.renewVariable(time, ODEsolver.runRK4(variableParachute, dynParachute));
			parachuteLog.logVariable(variableParachute);

			if(eventJudgement.judgeLanding(variableParachute)) {
				time_LandingParachute = variableParachute.getTime();
				index_LandingParachute = index;
				break;
			}
		}
		System.arraycopy(parachuteLog.getPos_ENU(index_LandingParachute).getValue(), 0, pos_ENU_landing_parachute, 0, 2);


		//結果の出力
		if(single) {
			trajectoryLog.setArray();
			parachuteLog.setArray();

			OutputLogTrajectory outputLogTrajectory = new OutputLogTrajectory("flightlog_trajectory", spec, trajectoryLog);
			OutputLogParachute outputLogParachute = new OutputLogParachute("flightlog_parachute", spec, parachuteLog, index_apogee);

			System.out.println("----- Result -----");
			System.out.println("Vel_launchclear = "+ vel_LaunchClear+"[m/s]");
			System.out.println("time_launchclear = "+time_LaunchClear+"[s]");
			System.out.println("trajectory landing:"+pos_ENU_landing_trajectory[0]+","+pos_ENU_landing_trajectory[1]);
			System.out.println("max alttitude = "+alt_apogee);
			System.out.println("t_apogee = " + time_apogee + "[s]");
			System.out.println("time landing parachute = " + time_LandingParachute + "[s]");
			System.out.println("------------------");
			//TODO .txtで出力を行う
			//TODO マッハ数、動圧、Max-Qの時間を出力できるようにする

			outputLogTrajectory.runOutputLine(time_LandingTrajectory);
			outputLogParachute.runOutputLine(time_LandingParachute, time_apogee);
		}
	}

	public void getResult() {

	}

}