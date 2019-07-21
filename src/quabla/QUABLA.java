package quabla;

import quabla.simulator.Solver;

public class QUABLA {

	public static void main(String[] args) {



		//System.out.println(Simulation);
		System.out.println("Running Solver...");

		//single
		InputParam spec = new InputParam();
		Solver solver = new Solver(spec,true);
		solver.solve_dynamics();

		System.out.println("Completed!!");
	//	System.out.println("Saved " + filepath);

	}

}
