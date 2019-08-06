package quabla;

import java.io.File;

import quabla.simulator.Multi_solver;
import quabla.simulator.Solver;

public class QUABLA {

	private static String dir_filepath;

	public static void main(String[] args) {



		//System.out.println(Simulation);
		System.out.println("Running Solver...");


		InputParam spec = new InputParam();
		//String dir_filepath;
		switch(spec.Mode) {
		case 1:
			//single condition

			//ディレクトリの作成
			dir_filepath = spec.result_filepath + "single_" + spec.dir_name;
			make_resultdir(dir_filepath);
			spec.result_filepath = dir_filepath + "\\";


			Solver solver = new Solver(spec,true);
			solver.solve_dynamics();
			break;

		case 2:
			//multiple condition

			//ディレクトリの作成
			dir_filepath = spec.result_filepath + "multi_" + spec.dir_name;
			make_resultdir(dir_filepath);
			spec.result_filepath = dir_filepath + "\\";

			System.out.println(dir_filepath);


			Multi_solver multi_solver = new Multi_solver(spec);
			multi_solver.solve_multi();
			break;
		}

		System.out.println("Completed!!");


	}

	public static void make_resultdir(String filepath) {
		String dir_filepath_org = filepath;
		String dir_filepath_ = filepath;

		File result_dir = new File(dir_filepath_);
		int i = 0;
		while((result_dir = new File(dir_filepath_)).exists()){
			dir_filepath_ = dir_filepath_org +"_"+ String.format("%02d", i+1);
			i ++;
		}
		result_dir.mkdir();
		dir_filepath = dir_filepath_;

	}

}
