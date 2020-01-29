package quabla;

import java.io.File;

import quabla.parameter.InputParam;
import quabla.simulator.MultiSolver;
import quabla.simulator.Solver;

public class QUABLA {

	private static String dir_filepath;

	public static void main(String[] args) {

		System.out.println("Running Solver...");

		InputParam spec = new InputParam();
		//String dir_filepath;
		switch(spec.Mode) {
		case 1:
			//single condition

			//TODO single solver用のクラスの作成

			//ディレクトリの作成
			dir_filepath = spec.result_filepath + "Result_single_" + spec.dir_name;
			make_resultdir(dir_filepath);
			spec.result_filepath = dir_filepath + "\\";

			Solver solver = new Solver(spec);
			solver.solve_dynamics();

			//solver.makeResult();
			solver.makeResult_();
			solver.outputResultTxt();
			break;

		case 2:
			//multiple condition

			//ディレクトリの作成
			dir_filepath = spec.result_filepath + "Result_multi_" + spec.dir_name;
			make_resultdir(dir_filepath);
			spec.result_filepath = dir_filepath + "\\";

			MultiSolver multi_solver = new MultiSolver(spec);
			multi_solver.solveMulti();
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

		if( result_dir.mkdir()) {
			System.out.println("Make Directory : success");
		}else {
			System.out.println("Make Directroy : false");//TODO ディレクトリ作成失敗の時の例外処理
		}

		dir_filepath = dir_filepath_;

	}

}
