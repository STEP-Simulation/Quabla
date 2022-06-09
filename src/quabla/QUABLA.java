package quabla;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import quabla.simulator.MultiSolver;
import quabla.simulator.Solver;
import quabla.simulator.rocket.Rocket;

public class QUABLA {

	private static String dirFilepath;
	public static String simulationModeCheck;
	public static Double height;
	public static Double heightlanding;

	public static void main(String[] args) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //System.out.println("Please input path of paramater json file:");
        //String line = reader.readLine();

		String line = args[0];
		height = Double.parseDouble(args[2]);
		heightlanding = Double.parseDouble(args[3]);

		String simulationMode;
		String filepathResult;
		while(true) {
			//System.out.println("Please input simulation mode (single or multi):");
			//simulationModeCheck = reader.readLine();
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

		String name = node.get("Solver").get("Name").asText();
		System.out.println("Simulation Mode : " + simulationMode);
		System.out.println("Model : " + name);

		long startTime = System.currentTimeMillis();

		switch(simulationMode) {
		case "single":
			//single condition

			//ディレクトリの作成
			filepathResult = node.get("Solver").get("Result Filepath").asText();
			//filepathResult = args[2];
			dirFilepath = filepathResult + File.separator + "Result_single_" + name;
			makeResultdir(dirFilepath);
			String filepathResultFinal = dirFilepath + File.separator;

			Resultpath resultpath = new Resultpath();
			resultpath.mode = "single";
			resultpath.path = filepathResultFinal;
			mapper.writeValue(new File(filepathResult + File.separator + "resultpath.json"),resultpath);

			Rocket rocket = new Rocket(node);
			Solver solver = new Solver(filepathResultFinal);
			solver.solveDynamics(rocket);
			solver.makeResult();
			solver.outputResultTxt();
			break;

		case "multi":
			//multiple condition

			//ディレクトリの作成
			String filepathResultMulti = node.get("Solver").get("Result Filepath").asText();
			dirFilepath = filepathResultMulti + File.separator + "Result_multi_" + name;
			makeResultdir(dirFilepath);
			String filepathResultMultiFinal = dirFilepath + File.separator;

			Resultpath resultpath2 = new Resultpath();
			resultpath2.mode = "multi";
			resultpath2.path = filepathResultMultiFinal;
			mapper.writeValue(new File(filepathResultMulti + File.separator + "resultpath.json"),resultpath2);

			MultiSolver multi_solver = new MultiSolver(filepathResultMultiFinal, node.get("Multi Solver"));
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

class Resultpath{
	public String mode;
	public String path;
}
