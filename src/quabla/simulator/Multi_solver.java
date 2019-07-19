package quabla.simulator;

import java.io.IOException;

import quabla.InputParam;
import quabla.output.Output_wind_map;

public class Multi_solver {
	InputParam spec;
	double speed_min,speed_step;
	int speed_num,angle_num;

	public Multi_solver(InputParam spec) {
		this.spec = spec;

		this.spec.Wind_file_exsit = false;//分散の時は強制的にべき法則

		this.speed_min = spec.speed_min;
		this.speed_step = spec.speed_step;
		this.speed_num = spec.speed_num;
		this.angle_num = spec.angle_num;

	}

	public void solve_multi() {
		double wind_map_trajectory[][] = new double[2*speed_num][angle_num+1];
		double wind_map_parachute[][] = new double[2*speed_num][angle_num+1];
		//row:風速, column:風向

		for(int i = 0; i < speed_num; i++) {
			spec.wind_speed = speed_min + i * speed_step;
			for(int j =0; j <= angle_num; j++) {
				spec.wind_azimuth = 2.0 * Math.PI * j / angle_num;

				//solverのインスタンスの生成
				Solver single_solver = new Solver(spec,false);//Multi_solverでは各フライトでのlogは保存しない
				single_solver.solve_dynamics();

				//trajectory,parachuteでの落下地点を取得
				double Pos_ENU_landing_trajectory[] = single_solver.Pos_ENU_landing_trajectory;
				double Pos_ENU_landing_parachute[] = single_solver.Pos_ENU_landing_parachute;

				wind_map_trajectory[2*i][j] = Pos_ENU_landing_trajectory[0];
				wind_map_trajectory[2*i+1][j] = Pos_ENU_landing_trajectory[1];
				wind_map_parachute[2*i][j] = Pos_ENU_landing_parachute[0];
				wind_map_parachute[2*i+1][j] = Pos_ENU_landing_parachute[1];

			}
		}

		Output_wind_map trajectory = new Output_wind_map();
		Output_wind_map parachute = new Output_wind_map();

		try {
			trajectory.output(spec.result_filepath + "trajectory"+spec.elevation_launcher+"[deg].csv",wind_map_trajectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			parachute.output(spec.result_filepath + "parachute"+spec.elevation_launcher+"[deg].csv",wind_map_trajectory);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
