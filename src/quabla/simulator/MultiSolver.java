package quabla.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;

import quabla.output.OutputLandingScatter;
import quabla.simulator.logger.event_value.EventValueMulti;
import quabla.simulator.logger.event_value.EventValueSingle;
import quabla.simulator.rocket.Rocket;

/**
 * MultiSolver manages solver and store values in multiple conditions.
 * */
public class MultiSolver {

	double speedMin,speedStep,azimuthBase;
	int numSpeed,numAzimuth;
	private double[] speedArray, azimuthArray;
	private String filepathResult;

	private final boolean existPayload;

	private final int procCpuMax;

	private EventValueMulti evm;

	public MultiSolver(String filepath, JsonNode multiCond, boolean existPayload, int procCpuMax) {
		filepathResult = filepath;

		this.speedMin    = multiCond.get("Minimum Wind Speed [m/s]").asDouble();
		this.speedStep   = multiCond.get("Step Wind Speed [m/s]").asDouble();
		this.numSpeed    = multiCond.get("Number of Wind Speed").asInt();
		this.numAzimuth  = multiCond.get("Number of Wind Azimuth").asInt();
		this.azimuthBase = multiCond.get("Base Wind Azimuth [deg]").asDouble();

		this.existPayload = existPayload;

		speedArray = new double[numSpeed];
		azimuthArray = new double[numAzimuth + 1];

		for(int i = 0; i < numSpeed; i++) {
			speedArray[i] = speedMin + i * speedStep;
		}
		for(int i = 0; i <= numAzimuth; i++) {
			azimuthArray[i] = (360.0 * i / numAzimuth) + azimuthBase;
			if (azimuthArray[i] >= 360.) {
				azimuthArray[i] -= 360.;
			}
		}

		evm = new EventValueMulti(speedArray, azimuthArray, existPayload);

		this.procCpuMax = procCpuMax;

	}

	public void solveMulti(JsonNode spec) {

		Rocket dummy = new Rocket(spec);
		dummy.outputSpec(filepathResult, "multi");
		dummy = null;
		ExecutorService executor = Executors.newFixedThreadPool(procCpuMax);
		List<InnerMultiSolver> taskList = new ArrayList<>();
		
		// Prepare task for multi-thread
		int k = 0;
		for (double speed : speedArray) {
			for (double angle : azimuthArray) {
				Solver solver = new Solver(filepathResult);
				Rocket rocket = new Rocket(spec);
				rocket.wind.setRefWind(speed, angle);
				taskList.add(new InnerMultiSolver(k, solver, rocket));
				k++;
			}
		}
		
		// Run threads
		displayProcess(-1, speedArray.length * azimuthArray.length);
		try {
			// 受け取ったFutureを格納するリスト
			List<Future<EventValueSingle>> futureList = new ArrayList<>();
			for (InnerMultiSolver task: taskList) {
				
				Future<EventValueSingle> future = executor.submit(task);
				futureList.add(future);
			}
			
			k = 0;
			for (Future<EventValueSingle> future : futureList) {
				
				int row = k / (numAzimuth+1);
				int col = k % (numAzimuth+1);
				
				try {

					// Record flight event results in single solver
					evm.setResultArray(row, col, future.get());
					displayProcess(k, speedArray.length * azimuthArray.length);
					k++;
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		} finally{
			executor.shutdown();
		}
		
		double[][] windMapTrajectory = getWindMap(evm.getPosNEDlandTrajectory());
		double[][] windMapParachute = getWindMap(evm.getPosNEDlandParachute());
		
		evm.outputResultTxt(filepathResult);
		evm.outputCsv(filepathResult);
		
		double launchElev = spec.get("Launch Condition").get("Launch Elevation [deg]").asDouble();
		OutputLandingScatter trajectory = new OutputLandingScatter();
		try {
			trajectory.output(filepathResult + "trajectory"+ launchElev +"[deg].csv",windMapTrajectory, speedArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OutputLandingScatter parachute = new OutputLandingScatter();
		try {
			parachute.output(filepathResult + "parachute"+ launchElev +"[deg].csv",windMapParachute, speedArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (existPayload) {
			
			double[][] windMapPayload = getWindMap(evm.getPosNEDlandPaylaod());
			OutputLandingScatter payload = new OutputLandingScatter();
			try {
				payload.output(filepathResult + "payload"+ launchElev +"[deg].csv", windMapPayload, speedArray);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void displayProcess(int numSpeed, int numMax) {
		String process = "";
		double processDouble = (double)(numSpeed + 1) / (double)numMax ;

		for(int i = 0; i < 40; i++) {
			if(i < (int)(processDouble * 40)) {
				process += "-";
			}
		}
		process += ">";
		for(int i = 0; i < 40; i++) {
			if (i >= (int)(processDouble * 40)) {
				process += " ";
			}
		}

		if (processDouble == 1.0) {
			System.out.println(String.format("[Solver] %3d", (int)(processDouble * 100)) + "%" + "|" + process + "|");
		}else {
			System.out.print(String.format("[Solver] %3d", (int)(processDouble * 100)) + "%" + "|" + process + "|" + "\r");
		}

	}

	/**
	 * @param posLandingArray 落下地点の3次元配列
	 * @return windMap 落下地点の2次元配列
	 * */
	private double[][] getWindMap(double[][][] posLandingArray) {
		double[][] windMap = new double[2 * numSpeed][numAzimuth + 1];
		/*
		 * 1st column : per wind speed
		 * 2nd columun : per wind angle
		 * */
		int i = 0;
		for(double[][] posSpeed : posLandingArray) { //風速ごとの要素
			int j = 0;
			for(double[] posAngle : posSpeed) { //各風速における風向ごとの要素
				windMap[2 * i][j] = posAngle[0];
				windMap[2 * i + 1][j] = posAngle[1];
				j ++;
			}
			i ++;
		}
		return windMap;
	}
}

class InnerMultiSolver implements Callable<EventValueSingle> {

	// private final int no;
	private final Rocket rocket;
	private final Solver solver;

	public InnerMultiSolver(int no, Solver solver, Rocket rocket){
		// this.no = no;
		this.rocket = rocket;
		this.solver = solver;
	}

	@Override
	public EventValueSingle call(){
		// System.out.println(" No." + no + " start  id:" + Thread.currentThread().getId());
		// long startTime = System.currentTimeMillis();
		solver.solveDynamics(rocket);
		solver.dumpFlightLog();
		// long endTime = System.currentTimeMillis();
		// System.out.println(" No." + no + " end  id:" + Thread.currentThread().getId() + " Elp. : " + (endTime - startTime) * 1e-03 + " [sec]");
		return solver.getEventValueSingle();
	}
}