package quabla.simulator;

import java.io.IOException;

import quabla.output.OutputFlightlogParachute;
import quabla.output.OutputFlightlogTrajectory;
import quabla.output.OutputTxt;
import quabla.parameter.InputParam;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsMinuteChangeTrajectory;
import quabla.simulator.dynamics.DynamicsOnLauncher;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.dynamics.DynamicsTipOff;
import quabla.simulator.dynamics.DynamicsTrajectory;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.logger.ivent_value.IventValueSingle;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableParachute;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableTrajectory;
import quabla.simulator.numerical_analysis.ODEsolver.AbstractODEsolver;
import quabla.simulator.numerical_analysis.ODEsolver.PredictorCorrector;
import quabla.simulator.numerical_analysis.ODEsolver.RK4;
import quabla.simulator.variable.Variable;
import quabla.simulator.variable.VariableParachute;

public class Solver {

	InputParam spec;

	private IventValueSingle iventValue;

	private LoggerVariable trajectoryLog;
	private LoggerVariableParachute parachuteLog;
	private LoggerOtherVariableTrajectory lovt;
	private LoggerOtherVariableParachute lovp;

	public Solver(InputParam spec) {
		this.spec = spec;

		trajectoryLog = new LoggerVariable();
		parachuteLog = new LoggerVariableParachute();
	}


	public void solveDynamics() {
		int index = 0;
		int indexTipOff, indexLaunchClear, indexApogee, indexLandingTrajectory, indexLandingParachute, index2ndPara = 0;
		double time = 0.0;
		final double h = spec.dt;
		boolean isTipOff = false;

		Atmosphere atm = new Atmosphere(spec.temperture0);
		RocketParameter rocket = new RocketParameter(spec);
		AeroParameter aero = new AeroParameter(spec);
		Wind wind = new Wind(spec);
		Variable variableTrajectory = new Variable(spec, rocket);

		// Dynamics
		AbstractDynamics dynTrajectory = new DynamicsTrajectory(rocket, aero, atm, wind);
		AbstractDynamics dynOnLauncher = new DynamicsOnLauncher(rocket, aero, atm, wind);
		DynamicsParachute dynParachute = new DynamicsParachute(rocket, atm, wind);

		// ODE solver
		AbstractODEsolver ODEsolver = new RK4(h);
		PredictorCorrector predCorr = new PredictorCorrector(h);

		// Predictor-Corrector法で用いる過去の微分値を保存するための配列
		DynamicsMinuteChangeTrajectory[] deltaArray = new DynamicsMinuteChangeTrajectory[3];

		FlightEventJudgement eventJudgement = new FlightEventJudgement(rocket) ;

		// Initial Variable
		trajectoryLog.log(variableTrajectory);

		//------------------- on Launcher -------------------
		for(;;) {
			index ++;
			time = index * h;

			if(index == 4) {
				predCorr.setTra(deltaArray[2], deltaArray[1], deltaArray[0]);
				// ODE 解法の変更
				ODEsolver = predCorr;
			}

			// solve ODE
			DynamicsMinuteChangeTrajectory delta = ODEsolver.compute(variableTrajectory, dynOnLauncher);
			if(index <= 3) {
				deltaArray[index - 1] = delta;
			}

			variableTrajectory.update(time, delta);
			// store flightlog
			trajectoryLog.log(variableTrajectory);

			// Tip-Off -----------------------------------------
			if(spec.tip_off_exist && eventJudgement.judgeTipOff(variableTrajectory) && !isTipOff) {// 1回のみ実行
				indexTipOff = index;
				dynOnLauncher = new DynamicsTipOff(rocket, aero, atm, wind);
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
			DynamicsMinuteChangeTrajectory delta = ODEsolver.compute(variableTrajectory, dynTrajectory);
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

		lovt = new LoggerOtherVariableTrajectory(spec, trajectoryLog);

		// store Ivent Value
		iventValue = new IventValueSingle(trajectoryLog, lovt);
		iventValue.setIndexLaunchClear(indexLaunchClear);
		iventValue.setIndexLandingTrajectory(indexLandingTrajectory);

		indexApogee = iventValue.getIndexApogee();
		parachuteLog.copy(indexApogee, trajectoryLog);
		trajectoryLog.dumpArrayList();

		//頂点時のvariableを渡す
		VariableParachute variablePara = new VariableParachute(spec);
		variablePara.set(trajectoryLog, indexApogee);

		predCorr.setDeltaPar(deltaArray[2].getDelatPar(), deltaArray[1].getDelatPar(), deltaArray[0].getDelatPar()); // Predicto-Correctorのための準備
		ODEsolver = predCorr; //Shallow copyだからpredCorrだけ変更すれば，ODEsolverも変更が反映されてるはずだけど一応代入しておく

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

		lovp = new LoggerOtherVariableParachute(spec, parachuteLog);

		iventValue.setLoggerVariableParachute(parachuteLog, lovp);
		iventValue.setIndexLandingParachute(indexLandingParachute);

		iventValue.setIndex2ndPara(index2ndPara);

	}

	public IventValueSingle getIventValueSingle() {
		return iventValue;
	}

	public void makeResult() {

		OutputFlightlogTrajectory oft = new OutputFlightlogTrajectory(spec, trajectoryLog, lovt,iventValue);
		OutputFlightlogParachute ofp = new OutputFlightlogParachute(spec, parachuteLog, lovp, iventValue);
		oft.runOutputLine(spec.result_filepath + "flightlog_trajectory.csv");
		ofp.runOutputLine(spec.result_filepath + "flightlog_parachute.csv");
	}

	public void outputResultTxt() {
		OutputTxt resultTxt = null;

		try {
			resultTxt = new OutputTxt(spec.result_filepath + "result.txt");
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {

			resultTxt.outputLine(String.format("Launch Clear Time : %.3f [sec]", iventValue.getTimeLaunchClear()));
			resultTxt.outputLine(String.format("Launch Clear Velocity : %.3f [m/s]", iventValue.getVelLaunchClear()));
			resultTxt.outputLine(String.format("Launch Clear Accelaration : %.3f G", iventValue.getAccLaunchClear() / 9.80665));

			resultTxt.outputLine(String.format("Apogee Time : %.3f [sec]", iventValue.getTimeApogee()));
			resultTxt.outputLine(String.format("Apogee Altitude : %.3f [km]", iventValue.getAltApogee()));
			resultTxt.outputLine(String.format("Apogee Downrange : %.3f [km]", iventValue.getDownrangeApogee()));
			resultTxt.outputLine(String.format("Apogee Air Speed : %.3f [m/s]", iventValue.getVelAirApogee()));

			resultTxt.outputLine(String.format("Max Air Speed Time : %.3f [sec]", iventValue.getTimeMaxVelAir()));
			resultTxt.outputLine(String.format("Max Air Speed : %.3f [m/s]", iventValue.getVelAirMax()));
			resultTxt.outputLine(String.format("Max Air Speed Altitude : %.3f [km]", iventValue.getAltitudeMaxVelAir()));

			resultTxt.outputLine(String.format("Max-Q Time : %.3f [sec]", iventValue.getTimeMaxQ()));
			resultTxt.outputLine(String.format("Max-Q Dynamics Pressure : %.3f [kPa]", iventValue.getDynamicsPressureMax()));
			resultTxt.outputLine(String.format("Max-Q Altitude : %.3f [km]", iventValue.getAltitudeMaxQ()));

			resultTxt.outputLine(String.format("Max Mach Time : %.3f [sec]", iventValue.getTimeMaxMach()));
			resultTxt.outputLine(String.format("Max Mach : %.3f [-]", iventValue.getMachMax()));
			resultTxt.outputLine(String.format("Max Mach Altitude : %.3f [km]", iventValue.getAltitudeMaxMach()));

			resultTxt.outputLine(String.format("2nd Parachute Open Time : %.3f [sec]", iventValue.getTime2ndPara()));

			resultTxt.outputLine(String.format("Landing Trajectory Time %.3f [sec]", iventValue.getTimeLandingTrajectory()));
			resultTxt.outputLine(String.format("Landing Trajectory Downrange %.3f [km]", iventValue.getDownrangeLandingTrajectory()));
			resultTxt.outputLine(String.format("Landing Trajectory Point : [ %.3f , %.3f ]", iventValue.getPosENUlandingTrajectory()[0], iventValue.getPosENUlandingTrajectory()[1]));

			resultTxt.outputLine(String.format("Landing Parachute Time %.3f [sec]", iventValue.getTimeLandingParachute()));
			resultTxt.outputLine(String.format("Landing Parachute Downrange %.3f [km]", iventValue.getDownrangeLandingParachute()));
			resultTxt.outputLine(String.format("Landing Parachute Point : [ %.3f , %.3f ]", iventValue.getPosENUlandingParachute()[0], iventValue.getPosENUlandingParachute()[1]));

		}catch(IOException e) {
			throw new RuntimeException(e) ;
		}

		try {
			resultTxt.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void dump() {
		lovt.dumpLog();
		lovp.dumpLog();
	}

}