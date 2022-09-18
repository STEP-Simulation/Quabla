package quabla;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import quabla.simulator.MultiSolver;
import quabla.simulator.Solver;
import quabla.simulator.rocket.Rocket;

public class QUABLA {

	public static String simulationModeCheck;

	public static void main(String[] args) throws IOException {

		String line = args[0];

		String simulationMode;
		String filepathResult;

		while(true) {
			simulationModeCheck = args[1];
			if(simulationModeCheck.equals("single")) {
				simulationMode = "single";
				break;
			}
			else if(simulationModeCheck.equals("multi")) {
				simulationMode = "multi";
				break;
			}
			else {
				System.out.println("Please input single or multi.");
				System.exit(0);
			}
		}

		System.out.println("Running Solver...");

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = null;
		try {
			node = mapper.readTree(new File(line)); //諸元のjsonファイル名を入れる
		}catch(IOException e) {
			e.printStackTrace();
		}

//		String name = node.get("Solver").get("Name").asText();
//		System.out.println("Simulation Mode : " + simulationMode);
//		System.out.println("Model           : " + name);

		long startTime = System.currentTimeMillis();

		switch(simulationMode) {
		case "single":
			//single condition

			filepathResult = args[2];
			String filepathResultFinal = filepathResult + File.separator;

			Rocket rocket = new Rocket(node);
			rocket.outputSpec(filepathResultFinal, simulationMode);
			Solver solver = new Solver(filepathResultFinal);
			solver.solveDynamics(rocket);
			solver.makeResult();
			solver.outputResultTxt();
			break;

		case "multi":
			//multiple condition

			String filepathResultMulti = args[2];
			String filepathResultMultiFinal = filepathResultMulti + File.separator;

			MultiSolver multi_solver = new MultiSolver(filepathResultMultiFinal, node.get("Multi Solver"));
			multi_solver.solveMulti(node);
			break;
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Calculate Time : " + (endTime - startTime) * 1e-03 + " sec");
		System.out.println("Completed!!");
	}
}