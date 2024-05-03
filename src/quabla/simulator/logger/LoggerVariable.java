package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.OtherVariableOnLauncher;
import quabla.simulator.variable.OtherVariableTrajectory;
import quabla.simulator.variable.VariableTrajectory;

/**
 * 飛翔中のvariableの値を記録するためのクラス
 *
 * */
public class LoggerVariable {

	private final Rocket rocket;

	private double[] timeArray;
	private double[] timeStepArray;
	private double[][] posNEDlog;
	private double[][] velNEDlog;
	private double[][] velBODYlog;
	private double[][] omegaBODYlog;
	private double[][] quatLog;

	private double[][] attitudeLog;
	private double[] massLog;
	private double[] massFuelLog;
	private double[] massOxLog;
	private double[] massPropLog;
	private double[] lcgLog;
	private double[] lcgFuelLog;
	private double[] lcgOxLog;
	private double[] lcgPropLog;
	private double[] lcpLog;
	private double[] IjRollLog;
	private double[] IjPitchLog;
	private double[] CdLog;
	private double[] CNaLog;
	private double[] altitudeLog;
	private double[] downrangeLog;
	private double[][] velAirNEDlog;
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
	private double[] thrustMomentumLog;
	private double[] thrustPressureLog;
	private double[][] forceBODYlog;
	private double[][] accENUlog;
	private double[][] accBODYlog;
	private double[] accAbsLog;
	private double[][] momentAeroLog;
	private double[][] momentAeroDampingLog;
	private double[][] momentJetDampingLog;
	private double[][] momentGyroLog;
	private double[][] momentLog;
	private double[] pAirLog;

	private int length;

	private ArrayList<Double> timeLogArrayList = new ArrayList<>();
	private ArrayList<Double> timeStepLogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> posNEDLogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> velBODYlogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> omegaBODYlogArraylist = new ArrayList<>();
	private ArrayList<MathematicalVector> quatLogArrayList = new ArrayList<>();

	private final OtherVariableTrajectory ovt;
	private OtherVariableTrajectory ov;
	// private OtherVariableOnLauncher ovo;

	public LoggerVariable(Rocket rocket) {
		ovt = new OtherVariableTrajectory(rocket);
		this.rocket = rocket;
	}

	public void log(VariableTrajectory variable, double timeStep) {

		timeLogArrayList.add(variable.getTime());
		timeStepLogArrayList.add(timeStep);
		posNEDLogArrayList.add(variable.getPosNED());
		velBODYlogArrayList.add(variable.getVelBODY());
		omegaBODYlogArraylist.add(variable.getOmegaBODY());
		quatLogArrayList.add(variable.getQuat());
	}
	/**
	 * ArrayListをdouble型の配列へ変換する
	 * */
	public void makeArray(int indexLaunchClear) {
		length = timeLogArrayList.size();

		// TODO: store  Flight Condition

		timeArray = new double[length];
		timeStepArray = new double[length];
		posNEDlog = new double[length][3];
		velNEDlog = new double[length][3];
		velBODYlog = new double[length][3];
		omegaBODYlog = new double[length][3];
		quatLog = new double[length][4];

		attitudeLog = new double[length][3];
		massLog = new double[length];
		massFuelLog = new double[length];
		massOxLog = new double[length];
		massPropLog = new double[length];
		lcgLog = new double[length];
		lcgFuelLog = new double[length];
		lcgOxLog = new double[length];
		lcgPropLog = new double[length];
		lcpLog = new double[length];
		IjRollLog = new double[length];
		IjPitchLog = new double[length];
		CdLog = new double[length];
		CNaLog = new double[length];
		altitudeLog = new double[length];
		downrangeLog = new double[length];
		velAirNEDlog = new double[length][3];
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
		thrustMomentumLog = new double[length];
		thrustPressureLog = new double[length];
		forceBODYlog = new double[length][3];
		accENUlog = new double[length][3];
		accBODYlog = new double[length][3];
		accAbsLog = new double[length];
		momentAeroLog = new double[length][3];
		momentAeroDampingLog = new double[length][3];
		momentJetDampingLog = new double[length][3];
		momentGyroLog = new double[length][3];
		momentLog = new double[length][3];
		pAirLog = new double[length];

		boolean flag = true;
		ov = new OtherVariableOnLauncher(rocket);

		for(int i = 0; i < length; i++) {
			if (flag && i > indexLaunchClear) {
				ov = ovt;
				flag = false;
			}

			timeArray[i] = timeLogArrayList.get(i);
			timeStepArray[i] = timeStepLogArrayList.get(i);
			System.arraycopy(posNEDLogArrayList.get(i).toDouble(), 0, posNEDlog[i], 0, 3);
			System.arraycopy(velBODYlogArrayList.get(i).toDouble(), 0, velBODYlog[i], 0, 3);
			System.arraycopy(omegaBODYlogArraylist.get(i).toDouble(), 0, omegaBODYlog[i], 0, 3);
			System.arraycopy(quatLogArrayList.get(i).toDouble(), 0, quatLog[i], 0, 4);

			ov.setOtherVariable(timeArray[i], posNEDlog[i], velBODYlog[i], omegaBODYlog[i], quatLog[i]);
			System.arraycopy(ov.getAttitude(), 0, attitudeLog[i], 0, 3);
			massLog[i] = ov.getMass();
			massFuelLog[i] = ov.getMassFuel();
			massOxLog[i] = ov.getMassOx();
			massPropLog[i] = ov.getMassProp();
			lcgLog[i] = ov.getLcg();
			lcgFuelLog[i] = ov.getLcgFuel();
			lcgOxLog[i] = ov.getLcgOx();
			lcgPropLog[i] = ov.getLcgProp();
			lcpLog[i] = ov.getLcp();
			IjRollLog[i] = ov.getIjRoll();
			IjPitchLog[i] = ov.getIjPitch();
			CdLog[i] = ov.getCd();
			CNaLog[i] = ov.getCNa();
			altitudeLog[i] = ov.getAltitude();
			downrangeLog[i] = ov.getDownrange();
			System.arraycopy(ov.getVelAirENU(), 0, velAirNEDlog[i], 0, 3);
			System.arraycopy(ov.getVelAirBODY(), 0, velAirBODYlog[i], 0, 3);
			velAirAbsLog[i] = ov.getVelAirAbs();
			alphaLog[i] = ov.getAlpha();
			betaLog[i] = ov.getBeta();
			machLog[i] = ov.getMach();
			dynamicsPressureLog[i] = ov.getDynamicsPressure();
			fstLog[i] = ov.getFst();
			dragLog[i] = ov.getDrag();
			normalLog[i] = ov.getNormal();
			sideLog[i] = ov.getSide();
			thrustLog[i] = ov.getThrust();
			thrustMomentumLog[i] = ov.getThrustMomentum();
			thrustPressureLog[i] = ov.getThrustPressure();
			System.arraycopy(ov.getForceBODY(), 0, forceBODYlog[i], 0, 3);
			System.arraycopy(ov.getAccENU(), 0, accENUlog[i], 0, 3);
			System.arraycopy(ov.getAccBODY(), 0, accBODYlog[i], 0, 3);
			System.arraycopy(ov.getVelNED(), 0, velNEDlog[i], 0, 3);
			accAbsLog[i] = ov.getAccAbs();
			System.arraycopy(ov.getMomentAero(), 0, momentAeroLog[i], 0, 3);
			System.arraycopy(ov.getMomentAeroDamiping(), 0, momentAeroDampingLog[i], 0, 3);
			System.arraycopy(ov.getMomentJetDamping(), 0, momentJetDampingLog[i], 0, 3);
			System.arraycopy(ov.getMomentGyro(), 0, momentGyroLog[i], 0, 3);
			System.arraycopy(ov.getMoment(), 0, momentLog[i], 0, 3);
			pAirLog[i] = ov.getPair();

		}
	}

	public int getArrayLength() {
		return length;
	}

	public double getTimeArrayList(int index) {
		return timeLogArrayList.get(index);
	}

	public double getTimeStepArrayList(int index) {
		return timeStepLogArrayList.get(index);
	}

	public MathematicalVector getPosNED(int index) {
		return posNEDLogArrayList.get(index);
	}

	public MathematicalVector getVelNED(int index) {
		return velBODYlogArrayList.get(index);
	}

	public MathematicalVector getOmegaBODY(int index) {
		return omegaBODYlogArraylist.get(index);
	}

	public double getTime(int index) {
		return timeArray[index];
	}

	public double getTimeStep(int index) {
		return timeStepArray[index];
	}

	public double[] getPosNEDlog(int index) {
		return posNEDlog[index];
	}

	public double[] getVelNEDlog(int index) {
		return velNEDlog[index];
	}

	public double[] getOmegaBODYlog(int index) {
		return omegaBODYlog[index];
	}

	public double[] getQuatLog(int index) {
		return quatLog[index];
	}

	public double getMassLog(int index) {
		return massLog[index];
	}

	public double[] getTimeArray() {
		return timeArray;
	}

	public double[] getTimeStepArray() {
		return timeStepArray;
	}

	public double[][] getPosNEDArray(){
		return posNEDlog;
	}

	public double[][] getVelNEDArray(){
		return velNEDlog;
	}

	public double[][] getVelBODYArray(){
		return velBODYlog;
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

	public double[] getMassFuelLogArray() {
		return massFuelLog;
	}

	public double[] getMassOxLogArray() {
		return massOxLog;
	}

	public double[] getMassPropLogArray() {
		return massPropLog;
	}

	public double[] getLcgLogArray() {
		return lcgLog;
	}

	public double[] getLcgFuelLogArray() {
		return lcgFuelLog;
	}

	public double[] getLcgOxLogArray() {
		return lcgOxLog;
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

	public double[] getIjPitchLogArray() {
		return IjPitchLog;
	}

	public double[] getCdLog(){
		return CdLog;
	}

	public double[] getCNaLog(){
		return CNaLog;
	}

	public double[] getAltitudeLogArray() {
		return altitudeLog;
	}

	public double[] getDownrangeLogArray() {
		return downrangeLog;
	}

	public double[][] getVelAirNEDlogArray(){
		return velAirNEDlog;
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

	public double[] getThrustMomentumLogArray() {
		return thrustMomentumLog;
	}

	public double[] getThrustPressureLogArray() {
		return thrustPressureLog;
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

	public double[][] getMomentAeroLogArray() {
		return momentAeroLog;
	}

	public double[][] getMomentAeroDamipingLogArray() {
		return momentAeroDampingLog;
	}

	public double[][] getMomentJetDampingLogArray() {
		return momentJetDampingLog;
	}

	public double[][] getMomentGyroLogArray() {
		return momentGyroLog;
	}

	public double[][] getMomentLogArray() {
		return momentLog;
	}

	public double[] getPairLogArray() {
		return pAirLog;
	}

	/**使わなくなったArrayListをnullにする
	 * 意図的にガベージコレクションの対象にして省メモリ化を図る
	 * */
	public void dumpArrayList() {
		timeLogArrayList = null;
		timeStepLogArrayList = null;
		posNEDLogArrayList = null;
		velBODYlogArrayList = null;
		omegaBODYlogArraylist = null;
		quatLogArrayList = null;
	}
}
