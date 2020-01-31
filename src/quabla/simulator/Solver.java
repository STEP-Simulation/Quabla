package quabla.simulator;

import java.io.IOException;

import quabla.output.OutputFlightlogParachute;
import quabla.output.OutputFlightlogTrajectory;
import quabla.output.OutputTxt;
import quabla.parameter.InputParam;
import quabla.simulator.dynamics.AbstractDynamics;
import quabla.simulator.dynamics.DynamicsOnLauncher;
import quabla.simulator.dynamics.DynamicsParachute;
import quabla.simulator.dynamics.DynamicsTrajectory;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.ivent_value.IventValueSingle;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableParachute;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableTrajectory;
import quabla.simulator.numerical_analysis.ODEsolverWithRK4;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class Solver {

	InputParam spec;

	double[] pos_ENU_landing_trajectory = new double[2];
	double[] pos_ENU_landing_parachute = new double[2];

	/*
	private double velLaunchClear;
	private double timeLaunchClear;
	private double timeApogee;
	private double altitudeApogee;
	private double timeLandingTrajectory;
	private double timeLandingParachute;
	*/

	private int indexApogee;
	//TODO 結果保存用のクラス作成

	private IventValueSingle iventValue;

	private LoggerVariable trajectoryLog, parachuteLog;
	private LoggerOtherVariableTrajectory lovt;
	private LoggerOtherVariableParachute lovp;

	public Solver(InputParam spec) {
		this.spec = spec;

		trajectoryLog = new LoggerVariable();
		parachuteLog = new LoggerVariable();
	}


	public void solve_dynamics() {
		int index = 0;
		int indexLaunchClear, indexApogee, indexLandingTrajectory, indexLandingParachute;
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
				indexLaunchClear = index;
				break;
			}
		}


		////////// Trajectory //////////
		for(;;) {
			index ++;
			time = index * h;
			variableTrajectory.renewVariable(time, ODEsolver.runRK4(variableTrajectory, dynTrajectory));
			trajectoryLog.logVariable(variableTrajectory);

			if(eventJudgement.judgeLanding(variableTrajectory)) {
				indexLandingTrajectory = index;
				break;
			}
		}
		trajectoryLog.makeArray();

		lovt = new LoggerOtherVariableTrajectory(spec, trajectoryLog);

		iventValue = new IventValueSingle(trajectoryLog, lovt);

		iventValue.setIndexLaunchClear(indexLaunchClear);
		iventValue.setIndexLandingTrajectory(indexLandingTrajectory);

		iventValue.calculateLaunchClear();
		iventValue.calculateMachMax();
		iventValue.calculateAtVelAirMax();
		iventValue.calculateAtMaxQ();
		iventValue.calculateAtApogee();
		iventValue.calculateLandingTrajectory();

		System.arraycopy(trajectoryLog.getPosENUlog(indexLandingTrajectory), 0, pos_ENU_landing_trajectory, 0, 2);

		indexApogee = iventValue.getIndexApogee();
		parachuteLog.copyLog(indexApogee, trajectoryLog);
		trajectoryLog.dumpArrayList();

		//頂点時のvariableを渡す
		Variable variableParachute = new Variable(spec,rocket);
		variableParachute.setPos_ENU(new MathematicalVector(trajectoryLog.getPosENUlog(indexApogee)));
		variableParachute.setVel_ENU(new MathematicalVector(trajectoryLog.getVelENUlog(indexApogee)));
		variableParachute.setOmega_Body(new MathematicalVector(0.0, 0.0, 0.0));
		variableParachute.setQuat(new MathematicalVector(0.0, 0.0, 0.0, 0.0));

		index = indexApogee; //indexの更新
		////////// Parachute //////////
		for( ; ; ) {
			index ++;
			time = index * h;
			variableParachute.renewVariable(time, ODEsolver.runRK4(variableParachute, dynParachute));
			parachuteLog.logVariable(variableParachute);

			if(eventJudgement.judgeLanding(variableParachute)) {
				indexLandingParachute = index;
				break;
			}
		}
		parachuteLog.makeArray();
		parachuteLog.dumpArrayList();
		System.arraycopy(parachuteLog.getPosENUlog(indexLandingParachute), 0, pos_ENU_landing_parachute, 0, 2);

		lovp = new LoggerOtherVariableParachute(spec, parachuteLog);

		iventValue.setLoggerVariableParachute(parachuteLog, lovp);
		iventValue.setIndexLandingParachute(indexLandingParachute);
		iventValue.calculateLandingParachute();

	}

	public IventValueSingle getIventValueSingle() {
		return iventValue;
	}

/*	public void makeResult() {

		OutputLogTrajectory olt = new OutputLogTrajectory("flightlog_trajectory", spec, trajectoryLog);
		OutputLogParachute olp = new OutputLogParachute("flightlog_parachute", spec, parachuteLog, indexApogee);

		olt.runOutputLine(timeLandingTrajectory);
		olp.runOutputLine(timeLandingParachute, timeApogee);
	}*/

	public void makeResult_() {

		OutputFlightlogTrajectory oft = new OutputFlightlogTrajectory("flightlog_trajectory", spec, trajectoryLog, lovt);
		OutputFlightlogParachute ofp = new OutputFlightlogParachute("flightlog_parachute", spec, parachuteLog, lovp);
		oft.runOutputLine();
		//ofp.runOutputLine();
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
			resultTxt.outputLine(String.format("Launch Clear Accelaration : %.3f [m/s2]", iventValue.getAccLaunchClear()));

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

			resultTxt.outputLine(String.format("Landing Trajectory Time %.3f [sec]", iventValue.getTimeLandingTrajectory()));
			resultTxt.outputLine(String.format("Landing Trajectory Downrange %.3f [km]", iventValue.getDownrangeLandingTrajectory()));

			resultTxt.outputLine(String.format("Landing Parachute Time %.3f [sec]", iventValue.getTimeLandingParachute()));
			resultTxt.outputLine(String.format("Landing Parachute Downrange %.3f [km]", iventValue.getDownrangeLandingParachute()));

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