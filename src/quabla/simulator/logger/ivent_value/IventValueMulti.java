package quabla.simulator.logger.ivent_value;

import java.io.IOException;

import quabla.output.OutputCsv;
import quabla.output.OutputTxt;
import quabla.simulator.numerical_analysis.ArrayAnalysis;

/**
 * 落下分散計算時のイベント値の処理
 * csvファイルへの保存，全条件での最大値や最小値及びその時の条件を計算
 * */
public class IventValueMulti {

	int row,column;

	private double[] speedArray, azimuthArray;

	private double
	velLaunchClearMin,
	altMax,
	velAirMax,
	machMax,
	timeApogeeMax,
	time2ndParaMax,
	timeLandingTrajectoryMax,
	timeLandingParachuteMax;

	private double
	speedVelLCmin, azimuthVelLCmin,
	speedMaxAlt, azimuthMaxAlt,
	speedMaxVelAir, azimuthMaxVelAir,
	speedMaxMach, azimuthMaxMach,
	speedMaxTimeApogee, azimuthMaxTimeApogee,
	speedMaxTime2ndPara, azimuthMaxTime2ndPara,
	speedMaxTimeLandingTrajectory, azimuthMaxTimeLandingTrajectory,
	speedMaxTimeLandingParachute, azimuthMaxTimeLandingParachute;


	private double[][]
			velLaunchClearArray,
			altApogeeArray,
			velAirMaxArray,
			machMaxArray,
			timeApogeeArray,
			time2ndParaArray,
			timeLandingTrajectoryArray,
			timeLandingParachuteArray;

	private double[][][] posENUlandingTrajectory, posENUlandingParachute;

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
		time2ndParaArray = new double[row][column];
		timeLandingTrajectoryArray = new double[row][column];
		timeLandingParachuteArray = new double[row][column];
		posENUlandingTrajectory = new double[row][column][2];
		posENUlandingParachute = new double[row][column][2];
	}

	public void setResultArray(int i, int j, IventValueSingle ivs) {
		velLaunchClearArray[i][j] = ivs.getVelLaunchClear();
		altApogeeArray[i][j] = ivs.getAltApogee();
		velAirMaxArray[i][j] = ivs.getVelAirMax();
		machMaxArray[i][j] = ivs.getMachMax();
		timeApogeeArray[i][j] = ivs.getTimeApogee();
		time2ndParaArray[i][j] = ivs.getTime2ndPara();
		timeLandingTrajectoryArray[i][j] = ivs.getTimeLandingTrajectory();
		timeLandingParachuteArray[i][j] = ivs.getTimeLandingParachute();
		System.arraycopy(ivs.getPosENUlandingTrajectory(), 0, posENUlandingTrajectory[i][j], 0, 2);
		System.arraycopy(ivs.getPosENUlandingParachute(), 0, posENUlandingParachute[i][j], 0, 2);
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

	public void computeMaxTime2ndPara() {
		IventValueArrange iva = new IventValueArrange(row);
		iva.computeMax(time2ndParaArray);
		time2ndParaMax = iva.getMaxValue();
		speedMaxTime2ndPara = speedArray[iva.getMaxRow()];
		azimuthMaxTime2ndPara = azimuthArray[iva.getMaxColumn()];
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

	public double[][][] getPosENUlandTrajectory(){
		return posENUlandingTrajectory;
	}

	public double[][][] getPosENUlandParachute(){
		return posENUlandingParachute;
	}

	public void outputResultTxt(String filepath) {
		this.computeMinVelLaunchClear();
		this.computeMaxAltAopgee();
		this.computeMaxMach();
		this.computeMaxTimeApogee();
		this.computeMaxTime2ndPara();
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
			resultTxt.outputLine(String.format("Max Time 2nd Parachute Separation : %.3f [sec](%.1f m/s, %.1f deg)", time2ndParaMax, speedMaxTime2ndPara, azimuthMaxTime2ndPara));
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

	public void outputCsv(String filepath) {
		OutputCsvMulti ocAltApogee = new OutputCsvMulti(filepath + "altApogee.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmVelLaunchClear = new OutputCsvMulti(filepath + "velLaunchClear.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmVelAirMax = new OutputCsvMulti(filepath + "velAirMax.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmMachMax = new OutputCsvMulti(filepath + "machMax.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmTimeApogee = new OutputCsvMulti(filepath + "timeApogee.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmTime2ndPara = new OutputCsvMulti(filepath + "time2ndPara.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmTimeLandingTrajectory = new OutputCsvMulti(filepath + "timeLandingTrajectory.csv", speedArray, azimuthArray);
		OutputCsvMulti ocmTimeLandingParachute = new OutputCsvMulti(filepath + "timeLandingParachute.csv", speedArray, azimuthArray);

		ocAltApogee.runOutputLine(altApogeeArray);
		ocmVelLaunchClear.runOutputLine(velLaunchClearArray);
		ocmVelAirMax.runOutputLine(velAirMaxArray);
		ocmMachMax.runOutputLine(machMaxArray);
		ocmTimeApogee.runOutputLine(timeApogeeArray);
		ocmTime2ndPara.runOutputLine(time2ndParaArray);
		ocmTimeLandingTrajectory.runOutputLine(timeLandingTrajectoryArray);
		ocmTimeLandingParachute.runOutputLine(timeLandingParachuteArray);
	}
}

class IventValueArrange{
	private int row;

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

class OutputCsvMulti{

	private String filepath;
	private double[] speedArray;
	private double[] azimuthArray;
	private String[] nameList;

	public OutputCsvMulti(String filepath, double[] speedArray, double[] azimuthArray) {
		this.filepath = filepath;

		this.nameList = new String[azimuthArray.length];
		this.speedArray = new double[speedArray.length];
		this.azimuthArray = new double[azimuthArray.length - 1];//360degの時の値は保存しない
		System.arraycopy(azimuthArray, 0, this.azimuthArray, 0, azimuthArray.length - 1);
		System.arraycopy(speedArray, 0, this.speedArray, 0, speedArray.length);
		nameList[0] = " ";
		for(int i = 0; i < this.azimuthArray.length; i++) {
			nameList[i + 1] = String.valueOf(azimuthArray[i]);
		}
	}

	public void runOutputLine(double[][] dataArray) {
		OutputCsv oc = null;

		int row = dataArray.length;
		int col = dataArray[0].length;
		try {
			oc = new OutputCsv(filepath, nameList);
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {
			oc.outputFirstLine();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		for(int i = 0; i < row; i++	) {
			double[] result = new double[col];
			result[0] = speedArray[i];
			System.arraycopy(dataArray[i], 0, result, 1, col - 1);
			try {
				oc.outputLine(result);
			}catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			oc.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
