package quabla.output;

import java.io.IOException;

import quabla.parameter.InputParam;
import quabla.simulator.Wind;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.numerical_analysis.Interpolation;

public class OutputLogParachute {

	private double dt;
	private String filename;
	private Interpolation pos_ENU_analy,vel_ENU_analy;
	private InputParam spec;
	private String[] name = {"time [s]", "x_ENU [m]", "y_ENU [m]", "z_ENU [m]",
			"vel_x_ENU [m/s]", "vel_y_ENU [m/s]", "vel_z_ENU [m/s]", "altitude [m]", "downrange [m]"};

	public OutputLogParachute(String filename, InputParam spec, LoggerVariable logdata, int indexApogee){
		this.filename = filename;
		this.spec = spec;
		dt = spec.dt_output;
		pos_ENU_analy = new Interpolation(logdata.getTimeArray(), logdata.getPosENUArray());
		vel_ENU_analy = new Interpolation(logdata.getTimeArray(), logdata.getVelENUArray());
	}


	public void runOutputLine(double time_landing,double time_apogee) {

		Wind wind = new Wind(spec);
		double time;
		double[] pos_ENU = new double[3];
		double[] vel_ENU = new double[3];
		double[] wind_ENU = new double[3];
		double altitude, downrange;

		OutputCsv flightlog = null;

		try {
			 flightlog = new OutputCsv(spec.result_filepath+ filename +".csv",name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			flightlog.outputFirstLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for(int i=0; ; i++) {
			time = i * dt;

			pos_ENU = pos_ENU_analy.linearInterpPluralColumns(time);
			vel_ENU = vel_ENU_analy.linearInterpPluralColumns(time);

			altitude = pos_ENU[2];
			downrange = Math.sqrt(Math.pow(pos_ENU[0], 2) + Math.pow(pos_ENU[1], 2));

			wind_ENU = Wind.wind_ENU(wind.getWindSpeed(altitude), wind.getWindDirection(altitude));

			if(time >= time_apogee) {
				vel_ENU[0] = wind_ENU[0];
				vel_ENU[1] = wind_ENU[1];
			}

			double[] result = {time, pos_ENU[0], pos_ENU[1], pos_ENU[2], vel_ENU[0], vel_ENU[1], vel_ENU[2],
					altitude, downrange};

			try {
				flightlog.outputLine(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//resultとnameの要素数が違った時の例外処理

			if(time >= time_landing) {
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
