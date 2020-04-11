package quabla;

import java.io.File;

import quabla.parameter.InputParam;
import quabla.simulator.MultiSolver;
import quabla.simulator.Solver;

public class QUABLA {

	private static String dirFilepath;

	public static void main(String[] args) {

		System.out.println("Running Solver...");

		InputParam spec = new InputParam();
		System.out.println("Simulation Mode : " + spec.simulationMode);
		System.out.println("Model : " + spec.dirName);

		long startTime = System.currentTimeMillis();

		switch(spec.simulationMode) {
		case "single":
			//single condition

			//ディレクトリの作成
			dirFilepath = spec.result_filepath + "Result_single_" + spec.dirName;
			makeResultdir(dirFilepath);
			spec.result_filepath = dirFilepath + "\\";

			Solver solver = new Solver(spec);
			solver.solveDynamics();
			solver.makeResult();
			solver.outputResultTxt();
			break;

		case "multi":
			//multiple condition

			//ディレクトリの作成
			dirFilepath = spec.result_filepath + "Result_multi_" + spec.dirName;
			makeResultdir(dirFilepath);
			spec.result_filepath = dirFilepath + "\\";

			MultiSolver multi_solver = new MultiSolver(spec);
			multi_solver.solveMulti();
			break;
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Calculate Time : " + (endTime - startTime) + " [ms]");
		System.out.println("Completed!!");
	}

	public static void makeResultdir(String filepath) {
		String dirFilepathOrg = filepath;
		String dirFilepath_ = filepath;

		File resultDir = new File(dirFilepath_);
		int i = 0;
		while((resultDir = new File(dirFilepath_)).exists()){
			dirFilepath_ = dirFilepathOrg +"_"+ String.format("%02d", i+1);
			i ++;
		}

		if( resultDir.mkdir()) {
			System.out.println("Make Directory : success");
		}else {
			System.out.println("Make Directroy : false");//TODO ディレクトリ作成失敗の時の例外処理
		}

		dirFilepath = dirFilepath_;

	}

}
