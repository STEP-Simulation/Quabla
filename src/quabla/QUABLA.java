package quabla;

import quabla.simulator.Solver;

public class QUABLA {

	public static void main(String[] args) {
		//System.out.println("This is main method.");
	//	int Simulation =1;
		/**
		 * Simulation =1 : Trajectory
		 * Simulation =2 : Parachute
		 * */


		int MODE=1;
		/**
		 * MODE =1 : single condition
		 * MODE =2 : multiple condition
		 * */

		String filepath = "C://Users/zoooi/Documents/STEP/機体班/シュミレーション/";//出力先のファイルパスを指定
		String filename = "result.csv";//単一条件の時の計算結果のファイルの名前




		//System.out.println(Simulation);
		System.out.println("Running Solver...");

		//single
		InputParam spec = new InputParam();
		Solver solver = new Solver(spec,true);
		solver.solve_dynamics();

		System.out.println("Completed!!");
		System.out.println("Saved " + filepath);

	}

}
