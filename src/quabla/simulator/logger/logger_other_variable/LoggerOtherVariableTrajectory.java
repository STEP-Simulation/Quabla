package quabla.simulator.logger.logger_other_variable;

import quabla.parameter.InputParam;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.variable.OtherVariableTrajectory;

public class LoggerOtherVariableTrajectory {

	private OtherVariableTrajectory ovt;

	private int length;

	//private LoggerVariable logdata;

	private double[][] attitudeLog;
	private double[] massLog;
	private double[] lcgLog;
	private double[] lcgPropLog;
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
	private double[] pAirLog;

	public LoggerOtherVariableTrajectory(InputParam spec, LoggerVariable logdata) {
		ovt = new OtherVariableTrajectory(spec);

		length = logdata.getArrayLength();
		attitudeLog = new double[length][3];
		massLog = new double[length];
		lcgLog = new double[length];
		lcgPropLog = new double[length];
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
		pAirLog = new double[length];

		setOtherVariableLog(logdata);
	}

	private void setOtherVariableLog(LoggerVariable logdata) {

		for(int i = 0; i < length; i++) {
			ovt.setOtherVariable(logdata.getTime(i), logdata.getPosENUlog(i), logdata.getVelENUlog(i), logdata.getQuatLog(i));

			System.arraycopy(ovt.getAttitude(), 0, attitudeLog[i], 0, 3);
			massLog[i] = ovt.getMass();
			lcgLog[i] = ovt.getLcg();
			lcgPropLog[i] = ovt.getLcgProp();
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
			pAirLog[i] = ovt.getPair();
		}

	}

	public double[][] getAttitudeLogArray(){
		return attitudeLog;
	}

	public double[] getMassLogArray() {
		return massLog;
	}

	public double[] getLcgLogArray() {
		return lcgLog;
	}

	public double[] getLcgPropLogArray() {
		return lcgPropLog;
	}

	public double[] getLcpLogArray() {
		return lcpLog;
	}

	public double[] getIjRollLogArray() {
		return IjRollLog;
	}

	public double[] getIjPitchLogArray	() {
		return IjPitchLog;
	}

	public double[] getAltitudeLogArray() {
		return altitudeLog;
	}

	public double[] getDownrangeLogArray() {
		return downrangeLog;
	}

	public double[][] getVelAirENUlogArray(){
		return velAirENUlog;
	}

	public double[][] getVelAirBODYlogArray(){
		return velAirBODYlog;
	}

	public double[] getVelAirAbsLogArray() {
		return velAirAbsLog;
	}

	public double[] getAlphaLogArray() {
		return alphaLog;
	}

	public double[] getBetaLogArray() {
		return betaLog;
	}

	public double[] getDynamicsPressureLogArray() {
		return dynamicsPressureLog;
	}

	public double[] getMachLogArray() {
		return machLog;
	}

	public double[] getFstLogArray() {
		return fstLog;
	}

	public double[] getDragLogArray() {
		return dragLog;
	}

	public double[] getNormalLogArray(){
		return normalLog;
	}

	public double[] getSideLogArray() {
		return sideLog;
	}

	public double[] getThrustLogArray() {
		return thrustLog;
	}

	public double[][] getForceBODYlogArray(){
		return forceBODYlog;
	}

	public double[][] getAccENUlogArray(){
		return accENUlog;
	}

	public double[][] getAccBODYlogArray(){
		return accBODYlog;
	}

	public double[] getAccAbsLogArray() {
		return accAbsLog;
	}

	public double[] getPairLogArray() {
		return pAirLog;
	}


	public void dumpLog() {
		massLog = null;
		lcgLog = null;
		lcgPropLog = null;
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
