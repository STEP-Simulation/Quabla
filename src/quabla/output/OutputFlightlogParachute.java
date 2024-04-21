package quabla.output;

import java.io.IOException;

import quabla.simulator.logger.LoggerVariableParachute;

public class OutputFlightlogParachute {

	private int index;
	private double[] result;

	private final String[] nameList = {
			"time [sec]",
			"time_step [sec]",
			"pos_east [m]",
			"pos_north [m]",
			"pos_up [m]",
			"vel_east [m/s]",
			"vel_north [m/s]",
			"vel_up [m/s]",
			"altitude [km]",
			"downrange [km]",
			"vel_air_east [m/s]",
			"vel_air_north [m/s]",
			"vel_air_up [m/s]",
			"vel_air_abs [m/s]"
	};

	private double[]   timeArray;
	private double[]   timeStepArray;
	private double[][] posENUArray;
	private double[][] velENUArray;
	private double[]   altitudeArray;
	private double[]   downrangeArray;
	private double[][] velAirENUArray;
	private double[]   velAirAbsArray;
	
	/**
	 * @param filename 出力するcsvのfile名
	 * @throws IOException
	 * */
	public OutputFlightlogParachute(LoggerVariableParachute lv) {

		timeArray = lv.getTimeArray().clone();
		timeStepArray = lv.getTimeStepArray();
		posENUArray = lv.getPosENUArray().clone();
		velENUArray = lv.getVelENUArray().clone();
		altitudeArray = lv.getAltitudeArray().clone();
		downrangeArray = lv.getDownrangeArray().clone();
		velAirENUArray = lv.getVelAirENUArray().clone();
		velAirAbsArray = lv.getVelAirAbsArray().clone();

	}

	public void runOutputLine(String filepath) {
		OutputCsv flightlog = null;
		try {
			flightlog = new OutputCsv(filepath, nameList);
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {
			flightlog.outputFirstLine();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		for(int i = 0; i < timeArray.length; i++) {

			result = new double[nameList.length];
			index = 0;

			storeResultArray(timeArray[i]);
			storeResultArray(timeStepArray[i]);
			storeResultArray(posENUArray[i]);
			storeResultArray(velENUArray[i]);
			storeResultArray(altitudeArray[i]);
			storeResultArray(downrangeArray[i]);
			storeResultArray(velAirENUArray[i]);
			storeResultArray(velAirAbsArray[i]);

			try {
				flightlog.outputLine(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
		try {
			flightlog.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	private void storeResultArray(double var){

		result[index] = var;
		index ++;
	}

	private void storeResultArray(double[] var){

		System.arraycopy(var, 0, result, index, var.length);
		index += var.length;
	}
}
