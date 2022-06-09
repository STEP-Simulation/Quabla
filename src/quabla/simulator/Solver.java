package quabla.simulator;

import java.io.IOException;

import quabla.output.OutputFlightlogParachute;
import quabla.output.OutputFlightlogTrajectory;
import quabla.output.OutputTxt;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.dynamics.DynamicsMinuteChangeTrajectory;
import quabla.simulator.dynamics.DynamicsOnLauncher;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.dynamics.DynamicsTipOff;
import quabla.simulator.dynamics.DynamicsTrajectory;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.logger.event_value.EventValueSingle;
import quabla.simulator.numerical_analysis.ODEsolver.AbstractODEsolver;
import quabla.simulator.numerical_analysis.ODEsolver.PredictorCorrector;
import quabla.simulator.numerical_analysis.ODEsolver.RK4;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.VariableParachute;
import quabla.simulator.variable.VariableTrajectory;

/**
 * Solver command functions in single condition, which are needed simulating dynamics and storing.
 * */
public class Solver {

	private final String resultDir;

	private EventValueSingle eventValue;

	private LoggerVariable trajectoryLog;
	private LoggerVariableParachute parachuteLog;

	public Solver(String resultDir) {
		this.resultDir = resultDir;
	}


	public void solveDynamics(Rocket rocket) {
		int index = 0;
		int indexLaunchClear, indexApogee, indexLandingTrajectory, indexLandingParachute, index2ndPara = 0;
		double time = 0.0;
		double h = rocket.dt;
		boolean isTipOff = false;

		VariableTrajectory variableTrajectory = new VariableTrajectory(rocket);

		// Dynamics
		AbstractDynamics dynTrajectory = new DynamicsTrajectory(rocket);
		AbstractDynamics dynOnLauncher = new DynamicsOnLauncher(rocket);
		DynamicsParachute dynParachute = new DynamicsParachute(rocket);

		// ODE solver
		AbstractODEsolver ODEsolver = new RK4(h); // 最初は4次ルンゲクッタで計算
		PredictorCorrector predCorr = new PredictorCorrector(h);

		// Predictor-Corrector法で用いる過去の微分値を保存するための配列
		AbstractDynamicsMinuteChange[] deltaArray = new DynamicsMinuteChangeTrajectory[3];

		// どの飛行状態に遷移したかを判定
		FlightEventJudgement eventJudgement = new FlightEventJudgement(rocket) ;

		trajectoryLog = new LoggerVariable(rocket);
		parachuteLog = new LoggerVariableParachute(rocket);

		// log Initial Variable
		trajectoryLog.log(variableTrajectory);

		//------------------- on Launcher -------------------
		for(;;) {
			index ++;
			time = index * h;

//	        Change ODE solver
//			if(index == 4) {
//				predCorr.setDelta(deltaArray[2], deltaArray[1], deltaArray[0]);
//				// ODE 解法の変更
//				ODEsolver = predCorr;
//			}

			// solve ODE
			AbstractDynamicsMinuteChange delta = ODEsolver.compute(variableTrajectory, dynOnLauncher);
			if(index <= 3) {
				deltaArray[index - 1] = delta;
			}

			variableTrajectory.update(time, delta);
			// store flightlog
			trajectoryLog.log(variableTrajectory);

			// Tip-Off -----------------------------------------
			if(rocket.existTipOff && eventJudgement.judgeTipOff(variableTrajectory) && !isTipOff) {// 1回のみ実行
//				indexTipOff = index;
				dynOnLauncher = new DynamicsTipOff(rocket);
				isTipOff = true;
			}

			if(eventJudgement.judgeLaunchClear(variableTrajectory)) {
				indexLaunchClear = index;
				break;
			}
		}


		//------------------- Trajectory -------------------
		int countTrajectory = 0;// 何回Trajectory開始からループしたかのカウント
		for(;;) {
			index ++;
			time = index * h;
			AbstractDynamicsMinuteChange delta = ODEsolver.compute(variableTrajectory, dynTrajectory);
			variableTrajectory.update(time, delta);
			trajectoryLog.log(variableTrajectory);

			// Predictor-Corrector用の過去の微分値を逐次保存
			if(countTrajectory < 3) {
				deltaArray[countTrajectory] = delta;
			}else {
				deltaArray[0] = deltaArray[1];
				deltaArray[1] = deltaArray[2];
				deltaArray[2] = delta;
			}

			if(eventJudgement.judgeLanding(variableTrajectory)) {
				indexLandingTrajectory = index;
				break;
			}
		}
		trajectoryLog.makeArray();

		// store Ivent Value
		eventValue = new EventValueSingle(trajectoryLog);
		eventValue.setIndexLaunchClear(indexLaunchClear);
		eventValue.setIndexLandingTrajectory(indexLandingTrajectory);

		indexApogee = eventValue.getIndexApogee();
		parachuteLog.copy(indexApogee, trajectoryLog);
		trajectoryLog.dumpArrayList();

		//頂点時のvariableを渡す
		VariableParachute variablePara = new VariableParachute(rocket);
		variablePara.set(trajectoryLog, indexApogee);

		predCorr.setDelta(deltaArray[2].toDeltaPara(), deltaArray[1].toDeltaPara(), deltaArray[0].toDeltaPara()); // Predicto-Correctorのための準備
//		Change ODE solver
//		ODEsolver = predCorr; //Shallow copyだからpredCorrだけ変更すれば，ODEsolverも変更が反映されてるはずだけど一応代入しておく

		index = indexApogee; //indexの更新
		//-------------------  Parachute -------------------
		for( ; ; ) {
			index ++;
			time = index * h;
			variablePara.update(time, ODEsolver.compute(variablePara, dynParachute));
			parachuteLog.log(variablePara);

			if(eventJudgement.judge2ndPara(variablePara)) {
				index2ndPara = index;
			}

			if(eventJudgement.judgeLanding(variablePara)) {
				indexLandingParachute = index;
				break;
			}
		}
		parachuteLog.makeArray();

		eventValue.setIndexLandingParachute(indexLandingParachute);

		eventValue.setIndex2ndPara(index2ndPara);
		eventValue.calculate(trajectoryLog, parachuteLog);

	}

	public EventValueSingle getEventValueSingle() {
		return eventValue;
	}

	public void makeResult() {

		OutputFlightlogTrajectory oft = new OutputFlightlogTrajectory(trajectoryLog, eventValue);
		OutputFlightlogParachute ofp = new OutputFlightlogParachute(parachuteLog, eventValue);
		oft.runOutputLine(resultDir + "flightlog_trajectory.csv");
		ofp.runOutputLine(resultDir + "flightlog_parachute.csv");
	}

	/**
	 * outputResultTxt writes txt file about ivent values.
	 * */
	public void outputResultTxt() {
		OutputTxt resultTxt = null;

		try {
			resultTxt = new OutputTxt(resultDir + "result.txt");
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {

			resultTxt.outputLine(String.format("Launch Clear Time : %.3f [sec]", eventValue.getTimeLaunchClear()));
			resultTxt.outputLine(String.format("Launch Clear Velocity : %.3f [m/s]", eventValue.getVelLaunchClear()));
			resultTxt.outputLine(String.format("Launch Clear Accelaration : %.3f G", eventValue.getAccLaunchClear() / 9.80665));

			resultTxt.outputLine(String.format("Apogee Time : %.3f [sec]", eventValue.getTimeApogee()));
			resultTxt.outputLine(String.format("Apogee Altitude : %.3f [km]", eventValue.getAltApogee()));
			resultTxt.outputLine(String.format("Apogee Downrange : %.3f [km]", eventValue.getDownrangeApogee()));
			resultTxt.outputLine(String.format("Apogee Air Speed : %.3f [m/s]", eventValue.getVelAirApogee()));

			resultTxt.outputLine(String.format("Max Air Speed Time : %.3f [sec]", eventValue.getTimeMaxVelAir()));
			resultTxt.outputLine(String.format("Max Air Speed : %.3f [m/s]", eventValue.getVelAirMax()));
			resultTxt.outputLine(String.format("Max Air Speed Altitude : %.3f [km]", eventValue.getAltitudeMaxVelAir()));

			resultTxt.outputLine(String.format("Max-Q Time : %.3f [sec]", eventValue.getTimeMaxQ()));
			resultTxt.outputLine(String.format("Max-Q Dynamics Pressure : %.3f [kPa]", eventValue.getDynamicsPressureMax()));
			resultTxt.outputLine(String.format("Max-Q Altitude : %.3f [km]", eventValue.getAltitudeMaxQ()));

			resultTxt.outputLine(String.format("Max Mach Time : %.3f [sec]", eventValue.getTimeMaxMach()));
			resultTxt.outputLine(String.format("Max Mach : %.3f [-]", eventValue.getMachMax()));
			resultTxt.outputLine(String.format("Max Mach Altitude : %.3f [km]", eventValue.getAltitudeMaxMach()));

			resultTxt.outputLine(String.format("2nd Parachute Open Time : %.3f [sec]", eventValue.getTime2ndPara()));

			resultTxt.outputLine(String.format("Landing Trajectory Time %.3f [sec]", eventValue.getTimeLandingTrajectory()));
			resultTxt.outputLine(String.format("Landing Trajectory Downrange %.3f [km]", eventValue.getDownrangeLandingTrajectory()));
			double trajectry_point[] = {eventValue.getPosENUlandingTrajectory()[0], eventValue.getPosENUlandingTrajectory()[1], 0.0};
			resultTxt.outputLine(String.format("Landing Trajectory Point : [ %.3f , %.3f ]", trajectry_point[0], trajectry_point[1]));

			resultTxt.outputLine(String.format("Landing Parachute Time %.3f [sec]", eventValue.getTimeLandingParachute()));
			resultTxt.outputLine(String.format("Landing Parachute Downrange %.3f [km]", eventValue.getDownrangeLandingParachute()));
			double parachute_point[] = {eventValue.getPosENUlandingParachute()[0], eventValue.getPosENUlandingParachute()[1], 0.0};
			resultTxt.outputLine(String.format("Landing Parachute Point : [ %.3f , %.3f ]", parachute_point[0], parachute_point[1]));

		}catch(IOException e) {
			throw new RuntimeException(e) ;
		}

		try {
			resultTxt.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

}