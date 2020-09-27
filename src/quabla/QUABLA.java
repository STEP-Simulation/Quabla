package quabla;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import quabla.simulator.MultiSolver;
import quabla.simulator.Solver;
import quabla.simulator.rocket.Rocket;

public class QUABLA {

	private static String dirFilepath;

	public static void main(String[] args) {

		System.out.println("Running Solver...");

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = null;
		try {
			node = mapper.readTree(new File(args[0]));
		}catch(IOException e) {
			e.printStackTrace();
		}
		String simulationMode = node.get("Solver").get("Simulation Mode").asText();
		String name = node.get("Solver").get("Name").asText();
		System.out.println("Simulation Mode : " + simulationMode);
		System.out.println("Model : " + name);

		long startTime = System.currentTimeMillis();

		switch(simulationMode) {
		case "single":
			//single condition

			//ディレクトリの作成
			String filepathResult = node.get("Solver").get("Result Filepath").asText();
			dirFilepath = filepathResult + "Result_single_" + name;
			makeResultdir(dirFilepath);
			filepathResult = dirFilepath + File.separator;

			Rocket rocket = new Rocket(node);
			Solver solver = new Solver(filepathResult);
			solver.solveDynamics(rocket);
			solver.makeResult();
			solver.outputResultTxt();
			break;

		case "multi":
			//multiple condition

			//ディレクトリの作成
			String filepathResultMulti = node.get("Solver").get("Result Filepath").asText();
			dirFilepath = filepathResultMulti + "Result_multi_" + name;
			makeResultdir(dirFilepath);
			filepathResultMulti = dirFilepath + File.separator;

			MultiSolver multi_solver = new MultiSolver(filepathResultMulti, node.get("Multi Solver"));
			multi_solver.solveMulti(node);
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
			dirFilepath_ = dirFilepathOrg +"_"+ String.format("%02d", i + 1);
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
