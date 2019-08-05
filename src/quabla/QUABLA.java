package quabla;

import quabla.simulator.Multi_solver;
import quabla.simulator.Solver;

public class QUABLA {

	public static void main(String[] args) {



		//System.out.println(Simulation);
		System.out.println("Running Solver...");


		InputParam spec = new InputParam();
		switch(spec.Mode) {
		case 1: //single condition
				Solver solver = new Solver(spec,true);
				solver.solve_dynamics();
				break;

		case 2: //multiple condition
				Multi_solver multi_solver = new Multi_solver(spec);
				multi_solver.solve_multi();
				break;
		}

		System.out.println("Completed!!");

	}

}
