package quabla.simulator;

import java.io.IOException;

import quabla.output.OutputLandingScatter;
import quabla.parameter.InputParam;
import quabla.simulator.logger.ivent_value.IventValueMulti;

public class MultiSolver {
	InputParam spec;
	double speed_min,speed_step;
	int speed_num,angle_num;
	private double[] speedArray, azimuthArray;

	private IventValueMulti ivm;

	public MultiSolver(InputParam spec) {
		this.spec = spec;

		this.spec.Wind_file_exsit = false;//分散の時は強制的にべき法則

		this.speed_min = spec.speed_min;
		this.speed_step = spec.speed_step;
		this.speed_num = spec.speed_num;
		this.angle_num = spec.angle_num;

		speedArray = new double[speed_num];
		azimuthArray = new double[angle_num + 1];

		for(int i = 0; i < speed_num; i++) {
			speedArray[i] = speed_min + i * speed_step;
		}
		for(int i = 0; i <= angle_num; i++) {
			azimuthArray[i] = 360.0 * i / angle_num;
		}

		ivm = new IventValueMulti(speedArray, azimuthArray);

	}

	public void solveMulti() {

		int i = 0;
		for(double speed: speedArray) {
			spec.wind_speed = speed;
			int j = 0;
			for(double azimuth: azimuthArray) {
				spec.wind_azimuth = azimuth;

				//solverのインスタンスの生成
				Solver single_solver = new Solver(spec);//Multi_solverでは各フライトでのlogは保存しない
				single_solver.solveDynamics();

				ivm.setResultArray(i, j, single_solver.getIventValueSingle());
				single_solver.dump();
				j++;
			}
			displayProcess(i);
			i++;
		}

		double[][] windMapTrajectory = getWindMap(ivm.getPosENUlandTrajectory());
		double[][] windMapParachute = getWindMap(ivm.getPosENUlandParachute());

		ivm.outputResultTxt(spec.result_filepath);
		ivm.outputCsv(spec.result_filepath);

		OutputLandingScatter trajectory = new OutputLandingScatter();
		try {
			trajectory.output(spec.result_filepath + "trajectory"+spec.elevation_launcher+"[deg].csv",windMapTrajectory, speedArray);
		} catch (IOException e) {
			e.printStackTrace();
		}

		OutputLandingScatter parachute = new OutputLandingScatter();
		try {
			parachute.output(spec.result_filepath + "parachute"+spec.elevation_launcher+"[deg].csv",windMapParachute, speedArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void displayProcess(int numSpeed) {
		String process = "";
		double processDouble = (double)(numSpeed + 1) / (double)speed_num ;
		for(int i = 0; i < 10; i++) {
			if(i < (int)(processDouble * 10)) {
				process += "█";
			}else {
				process += " ";
			}
		}
		System.out.println("|" + process + "|" + String.format("%d", (int)(processDouble * 100)) + "%");
	}

	private double[][] getWindMap(double[][][] posLandingArray) {
		double[][] windMap = new double[2 * speed_num][angle_num + 1];
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
