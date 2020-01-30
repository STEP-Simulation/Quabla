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
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMin(velLaunchClearArray);
		velLaunchClearMin = iva.getMinValue();
		speedVelLCmin = speedArray[iva.getMinRow()];
		azimuthVelLCmin = azimuthArray[iva.getMinColumn()];
	}

	public void computeMaxAltAopgee() {
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(altApogeeArray);
		altMax = iva.getMaxValue();
		speedMaxAlt = speedArray[iva.getMaxRow()];
		azimuthMaxAlt = azimuthArray[iva.getMaxColumn()];
	}

	public void computeMaxVelAirSpeed() {
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(velAirMaxArray);
		velAirMax = iva.getMaxValue();
		speedMaxVelAir = speedArray[iva.getMaxRow()];
		azimuthMaxVelAir = azimuthArray[iva.getMaxColumn()];
	}

	public void computeMaxMach() {
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(machMaxArray);
		machMax = iva.getMaxValue();
		speedMaxMach = speedArray[iva.getMaxRow()];
		azimuthMaxMach = azimuthArray[iva.getMaxColumn()];
	}

	public void computeMaxTimeApogee(){
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(timeApogeeArray);
		timeApogeeMax = iva.getMaxValue();
		speedMaxTimeApogee = speedArray[iva.getMaxRow()];
		azimuthMaxTimeApogee = azimuthArray[iva.getMaxColumn()];
	}

	public void computeMaxTimeLandingTrajectory(){
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(timeLandingTrajectoryArray);
		timeLandingTrajectoryMax = iva.getMaxValue();
		speedMaxTimeLandingTrajectory = speedArray[iva.getMaxRow()];
		azimuthMaxTimeLandingTrajectory = azimuthArray[iva.getMaxColumn()];
	}

	public void computeMaxTimeLandingParachute(){
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(timeLandingParachuteArray);
		timeLandingParachuteMax = iva.getMaxValue();
		speedMaxTimeLandingParachute = speedArray[iva.getMaxRow()];
		azimuthMaxTimeLandingParachute = azimuthArray[iva.getMaxColumn()];
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

class IventValueArrange{
	private int row;
	private double[] dataArray;

	private double speedMax, azimuthMax;
	private double speedMin, azimuthMin;

	private double maxValue, minValue;

	private int
	maxRow, maxCol,
	minRow, minCol;

	IventValueArrange(int row){
		this.row = row;
	}

	public void computeMax(double[][] dataArray) {
		int[] maxColumnArray = new int[row];
		double[] maxValueArray = new double[row];

		for(int i = 0; i < row; i++) {//まず各行ごと(風速ごと)の値を比較する
			ArrayAnalysis aa = new ArrayAnalysis(dataArray[i]);
			aa.calculateMaxValue();
			maxColumnArray[i] = aa.getIndexMaxValue();
			maxValueArray[i] = aa.getMaxValue();//各風速において最大値をとった列(風向)を配列に格納
		}

		ArrayAnalysis aa = new ArrayAnalysis(maxValueArray);//各風速の最大値を比較
		aa.calculateMaxValue();
		maxValue = aa.getMaxValue();//すべての条件の中での最大値
		maxRow = aa.getIndexMaxValue();//最大値をとった行(風速)
		maxCol = maxColumnArray[maxRow];//最大値をとった時の行(風速)の中での最大値をとった列(風向)
	}

	public double getMaxValue() {
		return maxValue;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public int getMaxColumn() {
		return maxCol;
	}

	public void computeMin(double[][] dataArray) {
		int[] minColumnArray = new int[row];
		double[] minValueArray = new double[row];

		for(int i = 0; i < row; i++) {
			ArrayAnalysis aa = new ArrayAnalysis(dataArray[i]);
			aa.calculateMinimumValue();;
			minColumnArray[i] = aa.getIndexMinimumValue();
			minValueArray[i] = aa.getMinValue();
		}

		ArrayAnalysis aa = new ArrayAnalysis(minValueArray);
		aa.calculateMinimumValue();
		minValue = aa.getMinValue();
		minRow = aa.getIndexMinimumValue();
		minCol = minColumnArray[minRow];
	}

	public double getMinValue() {
		return minValue;
	}

	public int getMinRow() {
		return minRow;
	}

	public int getMinColumn() {
		return minCol;
	}
}
