package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.parameter.InputParam;
import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.OtherVariableTrajectory;
import quabla.simulator.variable.VariableTrajectory;

/**
 * 飛翔中のvariableの値を記録するためのクラス
 *
 * */
public class LoggerVariable {

	private double[] timeArray;
	private double[][] posENUlog;
	private double[][] velENUlog;
	private double[][] omegaBODYlog;
	private double[][] quatLog;

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

	private int length;

	private ArrayList<Double> timeLogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> posENULogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> velENUlogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> omegaBODYlogArraylist = new ArrayList<>();
	private ArrayList<MathematicalVector> quatLogArrayList = new ArrayList<>();

	private final OtherVariableTrajectory ovt;

	public LoggerVariable(InputParam spec) {
		ovt = new OtherVariableTrajectory(spec);
	}

	public void log(VariableTrajectory variable) {

		timeLogArrayList.add(variable.getTime());
		posENULogArrayList.add(variable.getPosENU());
		velENUlogArrayList.add(variable.getVelENU());
		omegaBODYlogArraylist.add(variable.getOmegaBODY());
		quatLogArrayList.add(variable.getQuat());
	}
	/**
	 * ArrayListをdouble型の配列へ変換する
	 * */
	public void makeArray() {
		length = timeLogArrayList.size();

		timeArray = new double[length];
		posENUlog = new double[length][3];
		velENUlog = new double[length][3];
		omegaBODYlog = new double[length][3];
		quatLog = new double[length][4];

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

		for(int i = 0; i < length; i++) {
			timeArray[i] = timeLogArrayList.get(i);
			System.arraycopy(posENULogArrayList.get(i).toDouble(), 0, posENUlog[i], 0, 3);
			System.arraycopy(velENUlogArrayList.get(i).toDouble(), 0, velENUlog[i], 0, 3);
			System.arraycopy(omegaBODYlogArraylist.get(i).toDouble(), 0, omegaBODYlog[i], 0, 3);
			System.arraycopy(quatLogArrayList.get(i).toDouble(), 0, quatLog[i], 0, 4);

			ovt.setOtherVariable(timeArray[i], posENUlog[i], velENUlog[i], quatLog[i]);
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

	public int getArrayLength() {
		return length;
	}

	public double getTimeArrayList(int index) {
		return timeLogArrayList.get(index);
	}

	public MathematicalVector getPosENU(int index) {
		return posENULogArrayList.get(index);
	}

	public MathematicalVector getVelENU(int index) {
		return velENUlogArrayList.get(index);
	}

	public MathematicalVector getOmegaBODY(int index) {
		return omegaBODYlogArraylist.get(index);
	}

	private MathematicalVector getQuat(int index) {
		return quatLogArrayList.get(index);
	}

	public double getTime(int index) {
		return timeArray[index];
	}

	public double[] getPosENUlog(int index) {
		return posENUlog[index];
	}

	public double[] getVelENUlog(int index) {
		return velENUlog[index];
	}

	public double[] getOmegaBODYlog(int index) {
		return omegaBODYlog[index];
	}

	public double[] getQuatLog(int index) {
		return quatLog[index];
	}

	public double[] getTimeArray() {
		return timeArray;
	}

	public double[][] getPosENUArray(){
		return posENUlog;
	}

	public double[][] getVelENUArray(){
		return velENUlog;
	}

	public double[][] getOmegaBODYArray(){
		return omegaBODYlog;
	}

	public double[][] getQuatArray(){
		return quatLog;
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

	/**使わなくなったArrayListをnullにする
	 * 意図的にガベージコレクションの対象にして省メモリ化を図る
	 * */
	public void dumpArrayList() {
		timeLogArrayList = null;
		posENULogArrayList = null;
		velENUlogArrayList = null;
		omegaBODYlogArraylist = null;
		quatLogArrayList = null;
	}
}
