package quabla.simulator.logger.ivent_value;

import java.io.IOException;

import quabla.output.OutputTxt;
import quabla.simulator.numerical_analysis.ArrayAnalysis;

public class IventValueMulti {

	int row,column;

	private double[] speedArray, azimuthArray;

	private double
	velLaunchClearMin,
	altMax,
	velAirMax,
	machMax,
	timeApogeeMax,
	timeLandingTrajectoryMax,
	timeLandingParachuteMax;

	private double
	speedVelLCmin, azimuthVelLCmin,
	speedMaxAlt, azimuthMaxAlt,
	speedMaxVelAir, azimuthMaxVelAir,
	speedMaxMach, azimuthMaxMach,
	speedMaxTimeApogee, azimuthMaxTimeApogee,
	speedMaxTimeLandingTrajectory, azimuthMaxTimeLandingTrajectory,
	speedMaxTimeLandingParachute, azimuthMaxTimeLandingParachute;


	private double[][]
			velLaunchClearArray,
			altApogeeArray,
			velAirMaxArray,
			machMaxArray,
			timeApogeeArray,
			timeLandingTrajectoryArray,
			timeLandingParachuteArray;

	public IventValueMulti(double[] speedArray, double[] azimuthArray) {
		row = speedArray.length;
		column = azimuthArray.length;

		this.speedArray = new double[row];
		this.azimuthArray = new double[column];

		System.arraycopy(speedArray, 0, this.speedArray, 0, row);
		System.arraycopy(azimuthArray, 0, this.azimuthArray, 0, column - 1);
		this.azimuthArray[column - 1] = 0.0; //風向の最後の要素は北360degなのでそのための補正

		velLaunchClearArray = new double[row][column];
		altApogeeArray = new double[row][column];
		velAirMaxArray = new double[row][column];
		machMaxArray = new double[row][column];
		timeApogeeArray = new double[row][column];
		timeLandingTrajectoryArray = new double[row][column];
		timeLandingParachuteArray = new double[row][column];
	}

	public void setResultArray(int i, int j, IventValueSingle ivs) {
		velLaunchClearArray[i][j] = ivs.getVelLaunchClear();
		altApogeeArray[i][j] = ivs.getAltApogee();
		velAirMaxArray[i][j] = ivs.getVelAirMax();
		machMaxArray[i][j] = ivs.getMachMax();
		timeApogeeArray[i][j] = ivs.getTimeApogee();
		timeLandingTrajectoryArray[i][j] = ivs.getTimeLandingTrajectory();
		timeLandingParachuteArray[i][j] = ivs.getTimeLandingParachute();
	}

	public void computeMinVelLaunchClear() {
		int[] minColumnArray = new int[row];
		double[] minVelLaunchClearArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(velLaunchClearArray[i]);
			aa.calculateMinimumValue();
			minColumnArray[i] = aa.getIndexMinimumValue();
			minVelLaunchClearArray[i] = aa.getMinValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(minVelLaunchClearArray);
		aa.calculateMinimumValue();
		velLaunchClearMin = aa.getMinValue();
		int minRow = aa.getIndexMinimumValue();
		int minColumn = minColumnArray[minRow];

		speedVelLCmin = speedArray[minRow];
		azimuthVelLCmin = azimuthArray[minColumn];
	}

	public void computeMaxAltAopgee() {
		int[] maxColumnArray = new int[row];
		double[] maxAltApogeeArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(altApogeeArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxAltApogeeArray[i] = aa.getMaxValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxAltApogeeArray);
		aa.calculateMaxValue();
		altMax = aa.getMaxValue();
		int maxRow = aa.getIndexMaxValue();
		int maxColumn = maxColumnArray[maxRow];

		speedMaxAlt = speedArray[maxRow];
		azimuthMaxAlt = azimuthArray[maxColumn];
	}

	public void computeMaxVelAirSpeed() {
		int[] maxColumnArray = new int[row];
		double[] maxVelAirMaxArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(velAirMaxArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxVelAirMaxArray[i] = aa.getMaxValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxVelAirMaxArray);
		aa.calculateMaxValue();
		velAirMax = aa.getMaxValue();
		int maxRow = aa.getIndexMaxValue();
		int maxColumn = maxColumnArray[maxRow];

		speedMaxVelAir = speedArray[maxRow];
		azimuthMaxVelAir = azimuthArray[maxColumn];
	}

	public void computeMaxMach() {
		int[] maxColumnArray = new int[row];
		double[] maxMachMaxArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(machMaxArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxMachMaxArray[i] = aa.getMaxValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxMachMaxArray);
		aa.calculateMaxValue();
		machMax = aa.getMaxValue();
		int maxRow = aa.getIndexMaxValue();
		int maxColumn = maxColumnArray[maxRow];

		speedMaxMach = speedArray[maxRow];
		azimuthMaxMach = azimuthArray[maxColumn];
	}

	public void computeMaxTimeApogee(){
		int[] maxColumnArray = new int[row];
		double[] maxTimeApogeeArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(timeApogeeArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxTimeApogeeArray[i] = aa.getMaxValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxTimeApogeeArray);
		aa.calculateMaxValue();
		timeApogeeMax = aa.getMaxValue();
		int maxRow = aa.getIndexMaxValue();
		int maxColumn = maxColumnArray[maxRow];

		speedMaxTimeApogee = speedArray[maxRow];
		azimuthMaxTimeApogee = azimuthArray[maxColumn];
	}

	public void computeMaxTimeLandingTrajectory(){
		int[] maxColumnArray = new int[row];
		double[] maxTimeLandingTrajectoryArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(timeLandingTrajectoryArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxTimeLandingTrajectoryArray[i] = aa.getMaxValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxTimeLandingTrajectoryArray);
		aa.calculateMaxValue();
		timeLandingTrajectoryMax = aa.getMaxValue();
		int maxRow = aa.getIndexMaxValue();
		int maxColumn = maxColumnArray[maxRow];

		speedMaxTimeLandingTrajectory = speedArray[maxRow];
		azimuthMaxTimeLandingTrajectory = azimuthArray[maxColumn];
	}

	public void computeMaxTimeLandingParachute(){
		int[] maxColumnArray = new int[row];
		double[] maxTimeLandingParachuteArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(timeLandingParachuteArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxTimeLandingParachuteArray[i] = aa.getMaxValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxTimeLandingParachuteArray);
		aa.calculateMaxValue();
		timeLandingParachuteMax = aa.getMaxValue();
		int maxRow = aa.getIndexMaxValue();
		int maxColumn = maxColumnArray[maxRow];

		speedMaxTimeLandingParachute = speedArray[maxRow];
		azimuthMaxTimeLandingParachute = azimuthArray[maxColumn];
	}

	public void outputResultTxt(String filepath) {
		this.computeMinVelLaunchClear();
		this.computeMaxAltAopgee();
		this.computeMaxMach();
		this.computeMaxTimeApogee();
		this.computeMaxVelAirSpeed();
		this.computeMaxTimeLandingTrajectory();
		this.computeMaxTimeLandingParachute();

		OutputTxt resultTxt = null;

		try {
			resultTxt = new OutputTxt(filepath + "result.txt");
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {
			resultTxt.outputLine(String.format("Minimum Launch Clear Velocity : %.3f [m/s](%.1f m/s, %.1f deg)", velLaunchClearMin, speedVelLCmin, azimuthVelLCmin));
			resultTxt.outputLine(String.format("Max Altitude : %.3f [km](%.1f m/s, %.1f deg)", altMax, speedMaxAlt, azimuthMaxAlt));
			resultTxt.outputLine(String.format("Max Air Speed : %.3f [m/s](%.1f m/s, %.1f deg)", velAirMax, speedMaxVelAir, azimuthMaxVelAir));
			resultTxt.outputLine(String.format("Max Mach Number : %.3f [-](%.1f m/s, %.1f deg)", machMax, speedMaxMach, azimuthMaxMach));
			resultTxt.outputLine(String.format("Max Time Apogee : %.3f [sec](%.1f m/s, %.1f deg)", timeApogeeMax, speedMaxTimeApogee, azimuthMaxTimeApogee));
			resultTxt.outputLine(String.format("Max Time LandingTrajectory : %.3f [sec](%.1f m/s, %.1f deg)", timeLandingTrajectoryMax, speedMaxTimeLandingTrajectory, azimuthMaxTimeLandingTrajectory));
			resultTxt.outputLine(String.format("Max Time LandingParachute : %.3f [sec](%.1f m/s, %.1f deg)", timeLandingParachuteMax, speedMaxTimeLandingParachute, azimuthMaxTimeLandingParachute));
		}catch(IOException e) {
			throw new RuntimeException(e) ;
		}

		try {
			resultTxt.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

	}
}
