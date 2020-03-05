package quabla.simulator.logger.logger_other_variable;

import quabla.parameter.InputParam;
import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.variable.OtherVariableParachute;

public class LoggerOtherVariableParachute{

	private OtherVariableParachute ovp;
	private int length;

	private double[] altitudeLog;
	private double[] downrangeLog;
	private double[][] windENUlog;
	private double[][] velAirENUlog;
	private double[] velAirAbsLog;

	public LoggerOtherVariableParachute(InputParam spec, LoggerVariableParachute logdata) {
		ovp = new OtherVariableParachute(spec);

		length = logdata.getArrayLength();
		altitudeLog = new double[length];
		downrangeLog = new double[length];
		windENUlog = new double[length][3];
		velAirENUlog = new double[length][3];
		velAirAbsLog = new double[length];

		setOtherVariableLog(logdata);
	}

	private void setOtherVariableLog(LoggerVariableParachute logdata) {
		for(int i = 0; i < length; i++) {
			ovp.calculateOtherVariable(logdata.getTime(i), logdata.getPosENUlog(i), logdata.getVelENUlog(i));

			altitudeLog[i] = ovp.getAltitude();
			downrangeLog[i] = ovp.getDownrange();
			System.arraycopy(ovp.getWindENU(), 0, windENUlog[i], 0, 3);
			System.arraycopy(ovp.getVelAirENU(), 0, velAirENUlog[i], 0, 3);
			velAirAbsLog[i] = ovp.getVelAirAbs();
		}
	}

	public double[][] getWindENUarray(){
		return windENUlog;
	}

	public double[][] getVelAirENUArray(){
		return velAirENUlog;
	}

	public double[] getVelAirAbsArray() {
		return velAirAbsLog;
	}

	public double[] getAltitudeArray() {
		return altitudeLog;
	}

	public double[] getDownrangeArray(){
		return downrangeLog;
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
