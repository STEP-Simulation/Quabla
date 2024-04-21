package quabla.simulator;

import java.io.IOException;

import quabla.output.OutputCsv;
import quabla.output.OutputFlightlogParachute;
import quabla.output.OutputFlightlogTrajectory;
import quabla.output.OutputTxt;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.AbstractDynamicsMinuteChange;
import quabla.simulator.dynamics.DynamicsMinuteChangeParachute;
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

	private boolean is2ndPara;
	private double timeBurnAct;

	public Solver(String resultDir) {
		this.resultDir = resultDir;
	}


	public void solveDynamics(Rocket rocket) {
		int index = 0;
		int indexLaunchClear, indexLandingTrajectory, indexLandingParachute, index2ndPara = 0, index1stPara;
		double h = rocket.dt;
		boolean isTipOff = false;
		is2ndPara = rocket.para2Exist;
		timeBurnAct = rocket.engine.timeActuate;

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
		trajectoryLog.log(variableTrajectory, 0.0);

		// **************************************************************************************** /
		//    on Launcher                                                                           /
		// **************************************************************************************** /
		for(;;) {
			index ++;

	        // Change ODE solver
			if(index == 4) {
				predCorr.setDelta(deltaArray[2], deltaArray[1], deltaArray[0]);
				// ODE 解法の変更
				ODEsolver = predCorr;
			}

			//  solve ODE
			AbstractDynamicsMinuteChange delta = ODEsolver.compute(variableTrajectory, dynOnLauncher);
			
			// 最初の3回はRunge-Kuttaで解く
			if(index <= 3) {
				deltaArray[index - 1] = delta;
			}

			variableTrajectory.update(ODEsolver.getTimeStep(), delta);
			// store flightlog
			trajectoryLog.log(variableTrajectory, ODEsolver.getTimeStep());

			// ------------------------------------------------------------------------------------ /
			//    Tip-Off                                                                             /
			// ------------------------------------------------------------------------------------ /
			if(rocket.existTipOff && eventJudgement.judgeTipOff(variableTrajectory) && !isTipOff) {// 1回のみ実行
				// indexTipOff = index;
				dynOnLauncher = new DynamicsTipOff(rocket);
				isTipOff = true;
			}

			if(eventJudgement.judgeLaunchClear(variableTrajectory)) {
				indexLaunchClear = index;
				break;
			}
		}


		// **************************************************************************************** /
		//    Trajectory                                                                            /
		// **************************************************************************************** /
		boolean flag = true;
		for(;;) {
			index ++;

			AbstractDynamicsMinuteChange delta = ODEsolver.compute(variableTrajectory, dynTrajectory);
			variableTrajectory.update(ODEsolver.getTimeStep(), delta);
			trajectoryLog.log(variableTrajectory, ODEsolver.getTimeStep());

			if (flag && eventJudgement.judgeApogee(variableTrajectory)) {
				// double timeStepApogee = ODEsolver.getTimeStep();
				// ODEsolver.setTimeStep(timeStepApogee);
				predCorr.effectiveATS();
				flag = false;
			}

			if(eventJudgement.judgeLanding(variableTrajectory)) {
				indexLandingTrajectory = index;
				break;
			}
		}
		trajectoryLog.makeArray();

		// store Event Value
		eventValue = new EventValueSingle(trajectoryLog, rocket);
		eventValue.setIndexLaunchClear(indexLaunchClear);
		eventValue.setIndexLandingTrajectory(indexLandingTrajectory);

		// indexApogee = eventValue.getIndexApogee();
		index1stPara = eventValue.getIndex1stPara();
		eventValue.setIndex1stPara(index1stPara);
		parachuteLog.copy(index1stPara, trajectoryLog);
		trajectoryLog.dumpArrayList();

		// Change ODE solver
		h = 0.05;
		ODEsolver = new RK4(h);
		predCorr = new PredictorCorrector(h);
		predCorr.effectiveATS();
		
		// Parachute 用の変数にパラシュート放出時の変数を渡す
		VariableParachute variablePara = new VariableParachute(rocket);
		variablePara.set(trajectoryLog, index1stPara);

		// indexの更新
		index = index1stPara;
		int index_para = 0;
		deltaArray = new DynamicsMinuteChangeParachute[3];

		// **************************************************************************************** /
		//    Parachute                                                                             /
		// **************************************************************************************** /
		for( ; ; ) {
			index ++;
			index_para ++;
			
			// Change ODE solver
			if(index_para == 4) {
				predCorr.setDelta(deltaArray[2], deltaArray[1], deltaArray[0]);
				ODEsolver = predCorr;
			}
			
			// solve ODE
			AbstractDynamicsMinuteChange delta = ODEsolver.compute(variablePara, dynParachute);
			if(index_para <= 3) {
				deltaArray[index_para - 1] = delta;
			}
			
			variablePara.update(ODEsolver.getTimeStep(), delta);
			parachuteLog.log(variablePara, ODEsolver.getTimeStep());

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

		OutputFlightlogTrajectory oft = new OutputFlightlogTrajectory(trajectoryLog);
		OutputFlightlogParachute ofp = new OutputFlightlogParachute(parachuteLog);
		oft.runOutputLine(resultDir + "flightlog_trajectory.csv");
		ofp.runOutputLine(resultDir + "flightlog_parachute.csv");
	}

	/**
	 * outputResultTxt writes txt file about event values.
	 * */
	public void outputResultTxt() {
		OutputTxt resultTxt = null;

		try {
			resultTxt = new OutputTxt(resultDir + "result.txt");
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {

			resultTxt.outputLine("----------------------- * Static Margin * ---------------------- ");
			
			resultTxt.outputLine(String.format(" Minimum Static Margin, Fst : %.3f [-]", eventValue.getFstMin()));
			resultTxt.outputLine(String.format(" Max Static Margin, Fst     : %.3f [-]", eventValue.getFstMax()));
			
			resultTxt.outputLine("\n----------------------- * Launch Clear * ----------------------- ");
			
			resultTxt.outputLine(String.format(" Launch Clear Time         : %.3f [sec]", eventValue.getTimeLaunchClear()));
			resultTxt.outputLine(String.format(" Launch Clear Velocity     : %.3f [m/s]", eventValue.getVelLaunchClear()));
			resultTxt.outputLine(String.format(" Launch Clear Acceleration : %.3f G", eventValue.getAccLaunchClear() / 9.80665));
			
			resultTxt.outputLine("\n-------------------------- * Apogee * -------------------------- ");
			
			resultTxt.outputLine(String.format(" Apogee Time      : %.3f [sec]", eventValue.getTimeApogee()));
			resultTxt.outputLine(String.format(" Apogee Altitude  : %.3f [km]", eventValue.getAltApogee()));
			resultTxt.outputLine(String.format(" Apogee Downrange : %.3f [km]", eventValue.getDownrangeApogee()));
			resultTxt.outputLine(String.format(" Apogee Air Speed : %.3f [m/s]", eventValue.getVelAirApogee()));
			
			resultTxt.outputLine("\n----------------------- * Max Air Speed * ---------------------- ");
			
			resultTxt.outputLine(String.format(" Max Air Speed Time     : %.3f [sec]", eventValue.getTimeMaxVelAir()));
			resultTxt.outputLine(String.format(" Max Air Speed          : %.3f [m/s]", eventValue.getVelAirMax()));
			resultTxt.outputLine(String.format(" Max Air Speed Altitude : %.3f [km]", eventValue.getAltitudeMaxVelAir()));
			
			resultTxt.outputLine("\n--------------------------- * Max-Q * -------------------------- ");
			
			resultTxt.outputLine(String.format(" Max-Q Time              : %.3f [sec]", eventValue.getTimeMaxQ()));
			resultTxt.outputLine(String.format(" Max-Q Dynamics Pressure : %.3f [kPa]", eventValue.getDynamicsPressureMax()));
			resultTxt.outputLine(String.format(" Max-Q Altitude          : %.3f [km]", eventValue.getAltitudeMaxQ()));
			
			resultTxt.outputLine("\n------------------------- * Max Mach * ------------------------- ");
			
			resultTxt.outputLine(String.format(" Max Mach Time     : %.3f [sec]", eventValue.getTimeMaxMach()));
			resultTxt.outputLine(String.format(" Max Mach          : %.3f [-]", eventValue.getMachMax()));
			resultTxt.outputLine(String.format(" Max Mach Altitude : %.3f [km]", eventValue.getAltitudeMaxMach()));
			
			resultTxt.outputLine("\n--------------------- * Max Normal Force * --------------------- ");
			
			resultTxt.outputLine(String.format(" Max Normal Force Time     : %.3f [sec]", eventValue.getTimeNormalMax()));
			resultTxt.outputLine(String.format(" Max Normal Force          : %.3f [N]", eventValue.getNormalMax()));
			resultTxt.outputLine(String.format(" Max Normal Force Altitude : %.3f [km]", eventValue.getAltitudeNormalMax()));
			
			resultTxt.outputLine("\n---------------------- * Max Side Force * ---------------------- ");
			
			resultTxt.outputLine(String.format(" Max Side Force Time     : %.3f [sec]", eventValue.getTimeSideMach()));
			resultTxt.outputLine(String.format(" Max Side Force          : %.3f [N]", eventValue.getSideMax()));
			resultTxt.outputLine(String.format(" Max Side Force Altitude : %.3f [km]", eventValue.getAltitudeSideMax()));
			
			resultTxt.outputLine("\n---------------------- * Parachute Open * ---------------------- ");
			
			resultTxt.outputLine(String.format(" 1st Parachute Open Time : %.3f [sec]", eventValue.getTime1stPara()));
			if (is2ndPara) {
				resultTxt.outputLine(String.format(" 2nd Parachute Open Time : %.3f [sec]", eventValue.getTime2ndPara()));
			}
			
			resultTxt.outputLine("\n-------------------- * Landing Trajectory * -------------------- ");
			
			resultTxt.outputLine(String.format(" Landing Trajectory Time      : %.3f [sec]", eventValue.getTimeLandingTrajectory()));
			resultTxt.outputLine(String.format(" Landing Trajectory Downrange : %.3f [km]", eventValue.getDownrangeLandingTrajectory()));
			double pointTrajectory[] = {eventValue.getPosENUlandingTrajectory()[0], eventValue.getPosENUlandingTrajectory()[1], 0.0};
			resultTxt.outputLine(String.format(" Landing Trajectory Point     : [ %.3f , %.3f ]", pointTrajectory[0], pointTrajectory[1]));
			resultTxt.outputLine(String.format(" Landing Trajectory LLH       : [ %.8f , %.8f ]", ENUtoLLH.ENU2LLH(pointTrajectory)[0], ENUtoLLH.ENU2LLH(pointTrajectory)[1]));
			
			resultTxt.outputLine("\n-------------------- * Landing Parachute * -------------------- ");
			
			resultTxt.outputLine(String.format(" Landing Parachute Time             : %.3f [sec]", eventValue.getTimeLandingParachute()));
			resultTxt.outputLine(String.format(" Landing Parachute Velocity Descent : %.3f [m/s]", eventValue.getVelDescentLandingParachute()));
			resultTxt.outputLine(String.format(" Landing Parachute Downrange        : %.3f [km]", eventValue.getDownrangeLandingParachute()));
			double pointParachute[] = {eventValue.getPosENUlandingParachute()[0], eventValue.getPosENUlandingParachute()[1], 0.0};
			resultTxt.outputLine(String.format(" Landing Parachute Point            : [ %.3f , %.3f ]", pointParachute[0], pointParachute[1]));
			resultTxt.outputLine(String.format(" Landing Parachute LLH              : [ %.8f , %.8f ]", ENUtoLLH.ENU2LLH(pointParachute)[0], ENUtoLLH.ENU2LLH(pointParachute)[1]));

		}catch(IOException e) {
			throw new RuntimeException(e) ;
		}

		try {
			resultTxt.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void outputResultSummary(){

		OutputCsv summary = null;

		String[] name = {
			"Time Launch Clear [sec]",
			"Time Engine Actuate [sec]",
			"Time Apogee [sec]",
			"Time 1st Parachute Open [sec]",
			"Time 2nd Parachute Open [sec]"
		};

		try {
			summary = new OutputCsv(resultDir + "summary.csv", name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			summary.outputFirstLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		double[] results = {
			eventValue.getTimeLaunchClear(),
			timeBurnAct,
			eventValue.getTimeApogee(),
			eventValue.getTime1stPara(),
			eventValue.getTime2ndPara()
		};


		try {
			summary.outputLine(results);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			summary.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}