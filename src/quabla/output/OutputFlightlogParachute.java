package quabla.output;

import java.io.IOException;

import quabla.parameter.InputParam;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableParachute;

public class OutputFlightlogParachute {

	private String filename;
	private InputParam spec;
	private LoggerVariable lv;
	private LoggerOtherVariableParachute lov;

	private final String[] nameList = {
			"time [sec]",
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

	/**
	 * @param filename 出力するcsvのfile名
	 * @throws IOException
	 * */
	public OutputFlightlogParachute(String filename, InputParam spec, LoggerVariable lv, LoggerOtherVariableParachute lov) {
		this.filename = filename;
		this.spec = spec;
		this.lv = lv;
		this.lov = lov;
	}

	public void runOutputLine() {
		OutputCsv  flightlog = null;
		try {
			flightlog = new OutputCsv(spec.result_filepath + filename + ".csv", nameList);
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		try {
			flightlog.outputFirstLine();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		int length  = lv.getArrayLength();
		for(int i = 0; i < length; i++) {
			double[] result = new double[length];

			result[0] = lv.getTime(i);
			System.arraycopy(lv.getPosENUlog(i), 0, result, 1, 3);

			if(lv.getVelENUlog(i)[2] <= 0.0) {
				System.arraycopy(lov.getWindENUlog(i), 0, result, 4, 2);
				result[6] = lv.getVelENUlog(i)[2];
			}else {//開傘前
				System.arraycopy(lv.getVelENUlog(i), 0, result, 4, 3);
			}

			result[7] = lov.getAltitudeLog(i);
			result[8] = lov.getDownrangeLog(i);
			System.arraycopy(lov.getVelAirENUlog(i), 0, result, 9, 3);
			result[12] = lov.getVelAirAbsLog(i);

			try {
				flightlog.outputLine(result);
			}catch(IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			flightlog.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

}
