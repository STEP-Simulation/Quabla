package quabla.simulator.logger.logger_other_variable;

import quabla.parameter.InputParam;
import quabla.simulator.OtherVariableTrajectory;
import quabla.simulator.logger.LoggerVariable;

public class LoggerOtherVariableTrajectory {

	private OtherVariableTrajectory ovt;

	private int length;

	private LoggerVariable logdata;

	private double[] massLog;
	private double[] lcgLog;
	private double[] lcpLog;
	private double[] IjRollLog;
	private double[] IjPitchLog;
	private double[] altitudeLog;
	private double[] downrangeLog;
	private double[][] velAirENUlog;
	private double[][] velAirBODYlog;
	private double[] velAirAbsLog;
	private double[] alphaLog;
	private double[] betaLog;
	private double[] machLog;
	private double[] dynamicsPressureLog;
	private double[] fstLog;
	private double[] dragLog;
	private double[] normalLog;
	private double[] sideLog;
	private double[] thrustLog;
	private double[][] forceBODYlog;
	private double[][] accENUlog;
	private double[][] accBODYlog;
	private double[] accAbsLog;

	public LoggerOtherVariableTrajectory(InputParam spec, LoggerVariable logdata) {
		ovt = new OtherVariableTrajectory(spec);

		this.logdata = logdata;

		length = logdata.getArrayLength();
		massLog = new double[length];
		lcgLog = new double[length];
		lcpLog = new double[length];
		IjRollLog = new double[length];
		IjPitchLog = new double[length];
		altitudeLog = new double[length];
		downrangeLog = new double[length];
		velAirENUlog = new double[length][3];
		velAirBODYlog = new double[length][3];
		velAirAbsLog = new double[length];
		alphaLog = new double[length];
		betaLog = new double[length];
		machLog = new double[length];
		dynamicsPressureLog = new double[length];
		fstLog = new double[length];
		dragLog = new double[length];
		normalLog = new double[length];
		sideLog = new double[length];
		thrustLog = new double[length];
		forceBODYlog = new double[length][3];
		accENUlog = new double[length][3];
		accBODYlog = new double[length][3];
		accAbsLog = new double[length];

		setOtherVariableLog();
	}

	private void setOtherVariableLog() {

		for(int i = 0; i < length; i++) {
			ovt.setOtherVariable(logdata.getTime(i), logdata.getPosENUlog(i), logdata.getVelENUlog(i), logdata.getQuatLog(i));

			massLog[i] = ovt.getMass();
			lcgLog[i] = ovt.getLcg();
			lcpLog[i] = ovt.getLcp();
			IjRollLog[i] = ovt.getIjRoll();
			IjPitchLog[i] = ovt.getIjPitch();
			altitudeLog[i] = ovt.getAltitude();
			downrangeLog[i] = ovt.getDownrange();
			System.arraycopy(ovt.getVelAirENU(), 0, velAirENUlog[i], 0, 3);
			System.arraycopy(ovt.getVelAirBODY(), 0, velAirBODYlog[i], 0, 3);
			velAirAbsLog[i] = ovt.getVelAirAbs();
			alphaLog[i] = ovt.getAlpha();
			betaLog[i] = ovt.getBeta();
			machLog[i] = ovt.getMach();
			dynamicsPressureLog[i] = ovt.getDynamicsPressure();
			fstLog[i] = ovt.getFst();
			dragLog[i] = ovt.getDrag();
			normalLog[i] = ovt.getNormal();
			sideLog[i] = ovt.getSide();
			thrustLog[i] = ovt.getThrust();
			System.arraycopy(ovt.getForceBODY(), 0, forceBODYlog[i], 0, 3);
			System.arraycopy(ovt.getAccENU(), 0, accENUlog[i], 0, 3);
			System.arraycopy(ovt.getAccBODY(), 0, accBODYlog[i], 0, 3);
			accAbsLog[i] = ovt.getAccAbs();
		}

	}

	public double getMassLog(int index) {
		return massLog[index];
	}

	public double getLcgLog(int index) {
		return lcgLog[index];
	}

	public double getLcpLog(int index) {
		return lcpLog[index];
	}

	public double getIjRollLog(int index) {
		return IjRollLog[index];
	}

	public double getIjPitchLog(int index) {
		return IjPitchLog[index];
	}

	public double getAltitudeLog(int index) {
		return altitudeLog[index];
	}

	public double getDownrangeLog(int index) {
		return downrangeLog[index];
	}

	public double[] getVelAirENUlog(int index) {
		return velAirENUlog[index];
	}

	public double[] getVelAirBODYlog(int index) {
		return velAirBODYlog[index];
	}

	public double getVelAirAbsLog(int index) {
		return velAirAbsLog[index];
	}

	public double getAlphaLog(int index) {
		return alphaLog[index];
	}

	public double getBetaLog(int index) {
		return betaLog[index];
	}

	public double getMachLog(int index) {
		return machLog[index];
	}

	public double getDynamicsPressureLog(int index) {
		return dynamicsPressureLog[index];
	}

	public double getFstLog(int index) {
		return fstLog[index];
	}

	public double getDragLog(int index) {
		return dragLog[index];
	}

	public double getNormalLog(int index) {
		return normalLog[index];
	}

	public double getSideLog(int index) {
		return sideLog[index];
	}

	public double getThrustLog(int index) {
		return thrustLog[index];
	}

	public double[] getForceBODYlog(int index) {
		return forceBODYlog[index];
	}

	public double[] getAccENUlog(int index) {
		return accENUlog[index];
	}

	public double[] getAccBODYlog(int index) {
		return accBODYlog[index];
	}

	public double getAccAbsLog(int index) {
		return accAbsLog[index];
	}

	public double[] getAltitudeLogArray() {
		return altitudeLog;
	}

	public double[] getVelAirAbsLogArray() {
		return velAirAbsLog;
	}

	public double[] getDynamicsPressureLogArray() {
		return dynamicsPressureLog;
	}

	public double[] getMachLogArray() {
		return machLog;
	}

	public void dumpLog() {
		massLog = null;
		lcgLog = null;
		lcpLog = null;
		IjRollLog = null;
		IjPitchLog = null;
		altitudeLog = null;
		downrangeLog = null;
		velAirENUlog = null;
		velAirBODYlog = null;
		velAirAbsLog = null;
		alphaLog = null;
		betaLog = null;
		machLog = null;
		dynamicsPressureLog = null;
		fstLog = null;
		dragLog = null;
		normalLog = null;
		sideLog = null;
		thrustLog = null;
		forceBODYlog = null;
		accENUlog = null;
		accBODYlog = null;
		accAbsLog = null;
	}

}
