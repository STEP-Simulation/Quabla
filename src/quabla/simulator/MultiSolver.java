package quabla.simulator;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

import quabla.output.OutputLandingScatter;
import quabla.simulator.logger.event_value.EventValueMulti;
import quabla.simulator.rocket.Rocket;

/**
 * MultiSolver mangages solver and store values in multiple conditions.
 * */
public class MultiSolver {
	double speed_min,speed_step,base_azimuth;
	int speed_num,angle_num;
	private double[] speedArray, azimuthArray;
	private String filepathResult;

	private EventValueMulti evm;

	public MultiSolver(String filepath, JsonNode multiCond) {
		filepathResult = filepath;

		this.speed_min = multiCond.get("Minimum Wind Speed [m/s]").asDouble();
		this.speed_step = multiCond.get("Step Wind Speed [m/s]").asDouble();
		this.speed_num = multiCond.get("Number of Wind Speed").asInt();
		this.angle_num = multiCond.get("Number of Wind Azimuth").asInt();
		this.base_azimuth = multiCond.get("Base Wind Azimuth [deg]").asDouble();

		speedArray = new double[speed_num];
		azimuthArray = new double[angle_num + 1];

		for(int i = 0; i < speed_num; i++) {
			speedArray[i] = speed_min + i * speed_step;
		}
		for(int i = 0; i <= angle_num; i++) {
			if(((360.0 * i / angle_num) + base_azimuth) <= 360) {
				azimuthArray[i] = (360.0 * i / angle_num) + base_azimuth;
			}else {
				azimuthArray[i] = (360.0 * i / angle_num) + base_azimuth - 360;
			}
		}

		evm = new EventValueMulti(speedArray, azimuthArray);

	}

	public void solveMulti(JsonNode spec) {

		int i = 0;
		for(double speed: speedArray) {
			int j = 0;
			for(double azimuth: azimuthArray) {
				Rocket rocket = new Rocket(spec);
				if(i==0 && j==0) {
					rocket.outputSpec(filepathResult, "multi");
				}
				rocket.wind.setRefWind(speed, azimuth);;
				//solverのインスタンスの生成
				Solver single_solver = new Solver(filepathResult);//Multi_solverでは各フライトでのlogは保存しない
				single_solver.solveDynamics(rocket);

				evm.setResultArray(i, j, single_solver.getEventValueSingle());
				j++;
			}
			displayProcess(i);
			i++;
		}

		double[][] windMapTrajectory = getWindMap(evm.getPosENUlandTrajectory());
		double[][] windMapParachute = getWindMap(evm.getPosENUlandParachute());

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
	}

	/**
	 * This fanction display progress bar.
	 * */
	private void displayProcess(int numSpeed) {
		String process = "";
		double processDouble = (double)(numSpeed + 1) / (double)speed_num ;

		for(int i = 0; i < 30; i++) {
			if(i < (int)(processDouble * 30)) {
				process += "*";
			}else {
				process += " ";
			}
		}

		if (processDouble == 1.0) {
			System.out.println(String.format("%3d", (int)(processDouble * 100)) + "%" + "|" + process + "|");
		}else {
			System.out.print(String.format("%3d", (int)(processDouble * 100)) + "%" + "|" + process + "|" + "\r");
		}

	}

	/**
	 * @param posLandingArray 落下地点の3次元配列
	 * @return windMap 落下地点の2次元配列
	 * */
	private double[][] getWindMap(double[][][] posLandingArray) {
		double[][] windMap = new double[2 * speed_num][angle_num + 1];
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
