package quabla.simulator.logger.logger_other_variable;

import quabla.parameter.InputParam;
import quabla.simulator.OtherVariableParachute;
import quabla.simulator.logger.LoggerVariable;

public class LoggerOtherVariableParachute{

	private OtherVariableParachute ovp;
	private LoggerVariable logdata;
	private int length;

	private double[] altitudeLog;
	private double[] downrangeLog;
	private double[][] windENUlog;
	private double[][] velAirENUlog;
	private double[] velAirAbsLog;

	public LoggerOtherVariableParachute(InputParam spec, LoggerVariable logdata) {
		this.logdata = logdata;
		ovp = new OtherVariableParachute(spec);

		length = logdata.getArrayLength();
		altitudeLog = new double[length];
		downrangeLog = new double[length];
		windENUlog = new double[length][3];
		velAirENUlog = new double[length][3];
		velAirAbsLog = new double[length];

		setOtherVariableLog();
	}

	private void setOtherVariableLog() {
		for(int i = 0; i < length; i++) {
			ovp.calculateOtherVariable(logdata.getTime(i), logdata.getPosENUlog(i), logdata.getVelENUlog(i));

			altitudeLog[i] = ovp.getAltitude();
			downrangeLog[i] = ovp.getDownrange();
			System.arraycopy(ovp.getWindENU(), 0, windENUlog[i], 0, 3);
			System.arraycopy(ovp.getVelAirENU(), 0, velAirENUlog[i], 0, 3);
			velAirAbsLog[i] = ovp.getVelAirAbs();
		}
	}

	public double getAltitudeLog(int index) {
		return altitudeLog[index];
	}

	public double getDownrangeLog(int index) {
		return downrangeLog[index];
	}

	public double[] getWindENUlog(int index) {
		return windENUlog[index];
	}

	public double[] getVelAirENUlog(int index) {
		return velAirENUlog[index];
	}

	public double getVelAirAbsLog(int index) {
		return velAirAbsLog[index];
	}

	public void dumpLog() {
		altitudeLog = null;
		downrangeLog = null;
		windENUlog = null;
		velAirENUlog = null;
		velAirAbsLog = null;
		downrangeLog = null;
		windENUlog = null;
		velAirENUlog = null;
		velAirAbsLog = null;
	}

}
