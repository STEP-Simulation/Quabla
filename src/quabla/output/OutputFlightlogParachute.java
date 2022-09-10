package quabla.output;

import java.io.IOException;

import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.logger.event_value.EventValueSingle;
import quabla.simulator.numerical_analysis.Interpolation;

public class OutputFlightlogParachute {

	private final double TIME_STEP_OUTPUT = 0.01;

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

	private Interpolation
	posENUanaly,
	velENUanaly,
	windENUanaly,
	altitudeAnaly,
	downrangeAnaly,
	velAirENUanaly,
	velAirAbsAnaly;

	private double timeApogee, timeLandingParachute;

	/**
	 * @param filename 出力するcsvのfile名
	 * @throws IOException
	 * */
	public OutputFlightlogParachute(LoggerVariableParachute lv, EventValueSingle ivs) {

		timeApogee = ivs.getTimeApogee();
		timeLandingParachute = ivs.getTimeLandingParachute();

		posENUanaly = new Interpolation(lv.getTimeArray(), lv.getPosENUArray());
		velENUanaly = new Interpolation(lv.getTimeArray(), lv.getVelENUArray());
		windENUanaly = new Interpolation(lv.getTimeArray(), lv.getWindENUarray());
		altitudeAnaly = new Interpolation(lv.getTimeArray(), lv.getAltitudeArray());
		downrangeAnaly = new Interpolation(lv.getTimeArray(), lv.getDownrangeArray());
		velAirENUanaly = new Interpolation(lv.getTimeArray(), lv.getVelAirENUArray());
		velAirAbsAnaly = new Interpolation(lv.getTimeArray(), lv.getVelAirAbsArray());
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

		double time;

		for(int i = 0; ; i++) {
			time = i * TIME_STEP_OUTPUT;

			double[] result = new double[13];

			result[0] = time;
			System.arraycopy(posENUanaly.linearInterpPluralColumns(time), 0, result, 1, 3);
			if(time <= timeApogee) {
				System.arraycopy(velENUanaly.linearInterpPluralColumns(time), 0, result, 4, 3);
			}else {
				System.arraycopy(windENUanaly.linearInterpPluralColumns(time), 0, result, 4, 2);
				result[6] = velENUanaly.linearInterpPluralColumns(time)[2];
			}
			result[7] = altitudeAnaly.linearInterp1column(time);
			result[8] = downrangeAnaly.linearInterp1column(time);
			System.arraycopy(velAirENUanaly.linearInterpPluralColumns(time), 0, result, 9, 3);
			result[12] = velAirAbsAnaly.linearInterp1column(time);

			try {
				flightlog.outputLine(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			if(time >= timeLandingParachute) {
				break;
			}

		}
		try {
			flightlog.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
