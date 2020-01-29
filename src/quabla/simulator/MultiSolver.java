package quabla.simulator;

import java.io.IOException;

import quabla.output.OutputLandingScatter;
import quabla.parameter.InputParam;
import quabla.simulator.logger.ivent_value.IventValueMulti;

public class MultiSolver {
	InputParam spec;
	double speed_min,speed_step;
	int speed_num,angle_num;
	//private double[][] velLaunchClearArray, altApogeeArray;
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
		double[][] wind_map_trajectory = new double[2*speed_num][angle_num+1];
		double[][] wind_map_parachute = new double[2*speed_num][angle_num+1];


		//row:風速, column:風向


		int i = 0;
		for(double speed: speedArray) {
			spec.wind_speed = speed;
			int j = 0;
			for(double azimuth: azimuthArray) {
				spec.wind_azimuth = azimuth;

				//solverのインスタンスの生成
				Solver single_solver = new Solver(spec);//Multi_solverでは各フライトでのlogは保存しない
				single_solver.solve_dynamics();

				ivm.setResultArray(i, j, single_solver.getIventValueSingle());
				single_solver.dump();

				//trajectory,parachuteでの落下地点を取得
				double[] pos_ENU_landing_trajectory = single_solver.pos_ENU_landing_trajectory;
				double[] pos_ENU_landing_parachute = single_solver.pos_ENU_landing_parachute;

				wind_map_trajectory[2*i][j] = pos_ENU_landing_trajectory[0];
				wind_map_trajectory[2*i+1][j] = pos_ENU_landing_trajectory[1];
				wind_map_parachute[2*i][j] = pos_ENU_landing_parachute[0];
				wind_map_parachute[2*i+1][j] = pos_ENU_landing_parachute[1];
				j++;
			}
			i++;
		}

		//ivm.computeMinVelLaunchClear();
		//ivm.computeMaxAltAopgee();
		ivm.outputResultTxt(spec.result_filepath);

		OutputLandingScatter trajectory = new OutputLandingScatter();
		try {
			trajectory.output(spec.result_filepath + "trajectory"+spec.elevation_launcher+"[deg].csv",wind_map_trajectory, speedArray);
		} catch (IOException e) {
			e.printStackTrace();
		}

		OutputLandingScatter parachute = new OutputLandingScatter();
		try {
			parachute.output(spec.result_filepath + "parachute"+spec.elevation_launcher+"[deg].csv",wind_map_parachute, speedArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
