package quabla.simulator.logger.event_value;

import java.util.Arrays;

import quabla.simulator.Coordinate;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.numerical_analysis.ArrayAnalysis;

/**
 * EventValueSingle stores and calcurate flight event values(e.g. Launch Clear, apogee, etc.).
 * */
public class EventValueSingle {

	private double velLaunchClear;
	private double timeLaunchClear, timeMaxQ, timeMaxVelAir, timeMaxMach, timeNormalMax, timeSideMax, timeApogee, time2ndPara, timeLandingTrajectory, timeLandingParachute;
	private double altApogee, altMaxQ, altVelAirMax, altMachMax, altNormalMax, altSideMax, alt2ndPara;
	private double accLaunchClear;
	private double dynamicsPressureMax;
	private double velAirMax, velAirApogee;
	private double machMax;
	private double normalMax, sideMax;
	private double downrangeApogee, downrangeLandingTrajectory, downrangeLandingParachute;
	private double[] posENUlandingTrajectory = new double[2];
	private double[] posENUlandingParachute = new double[2];
	private int indexLaunchClear, indexMaxQ, index2ndPara, indexMaxVelAir, indexMaxMach, indexNormalMax, indexSideMax, indexApogee, indexLandingTrajectory, indexLandingParachute;

	public EventValueSingle(LoggerVariable lvt) {
		// イベント発生時のインデックスを必要としないもの(最高高度など)のみ先に計算
		// イベント発生時のインデックスを外部から入力する必要があるもの(ランチクリアなど)は値入力時に計算
		calculateAtApogee(lvt);
		calculateMachMax(lvt);
		calculateAtVelAirMax(lvt);
		calculateAtMaxQ(lvt);
		calculateNormalMax(lvt);
		calculateSideMax(lvt);
	}

	//-------------------- Set Function --------------------

	public void setIndexLaunchClear(int indexLaunchClear) {
		this.indexLaunchClear = indexLaunchClear;
	}

	public void setIndexLandingTrajectory(int indexLandingTrajectory) {
		this.indexLandingTrajectory = indexLandingTrajectory;
	}

	public void setIndexLandingParachute(int indexLandingParachute) {
		this.indexLandingParachute = indexLandingParachute;
	}

	public void setIndex2ndPara(int index2ndPara) {
		this.index2ndPara = index2ndPara;
	}

	//-------------------- Calculate Function --------------------
	private void calculateLaunchClear(LoggerVariable lvt) {
		timeLaunchClear = lvt.getTime(indexLaunchClear);
		accLaunchClear = lvt.getAccAbsLogArray()[indexLaunchClear];
		double[][] dcmENUtoBODY = Coordinate.getDCM_ENU2BODYfromQuat(lvt.getQuatLog(indexLaunchClear));
		velLaunchClear = Coordinate.vec_trans(dcmENUtoBODY, lvt.getVelENUlog(indexLaunchClear))[0]; // 機軸方向対地速度をランチクリア速度とする
	}

	private void calculateAtApogee(LoggerVariable lvt) {
		ArrayAnalysis aa = new ArrayAnalysis(lvt.getAltitudeLogArray());
		aa.calculateMaxValue();
		this.altApogee = aa.getMaxValue();
		this.indexApogee = aa.getIndexMaxValue();

		timeApogee = lvt.getTime(indexApogee);
		downrangeApogee = lvt.getDownrangeLogArray()[indexApogee];
		velAirApogee = lvt.getVelAirAbsLogArray()[indexApogee];
	}

	private void calculateAtVelAirMax(LoggerVariable lvt) {
//		ArrayAnalysis aa = new ArrayAnalysis(lvt.getVelAirAbsLogArray());
		ArrayAnalysis aa = new ArrayAnalysis(Arrays.copyOf(lvt.getVelAirAbsLogArray(), indexApogee));
		aa.calculateMaxValue();
		velAirMax = aa.getMaxValue();
		indexMaxVelAir = aa.getIndexMaxValue();

		timeMaxVelAir = lvt.getTime(indexMaxVelAir);
		altVelAirMax = lvt.getAltitudeLogArray()[indexMaxVelAir];
	}

	private void calculateAtMaxQ(LoggerVariable lvt) {
//		ArrayAnalysis aa = new ArrayAnalysis(lvt.getDynamicsPressureLogArray());
		ArrayAnalysis aa = new ArrayAnalysis(Arrays.copyOf(lvt.getDynamicsPressureLogArray(), indexApogee));
		aa.calculateMaxValue();
		dynamicsPressureMax = aa.getMaxValue();
		indexMaxQ = aa.getIndexMaxValue();

		timeMaxQ = lvt.getTime(indexMaxQ);
		altMaxQ = lvt.getAltitudeLogArray()[indexMaxQ];
	}

	private void calculateMachMax(LoggerVariable lvt) {
//		ArrayAnalysis aa = new ArrayAnalysis(lvt.getMachLogArray());
		ArrayAnalysis aa = new ArrayAnalysis(Arrays.copyOf(lvt.getMachLogArray(), indexApogee));
		aa.calculateMaxValue();
		machMax = aa.getMaxValue();
		indexMaxMach = aa.getIndexMaxValue();

		timeMaxMach = lvt.getTime(indexMaxMach);
		altMachMax = lvt.getAltitudeLogArray()[indexMaxMach];
	}
	
	private void calculateNormalMax(LoggerVariable lvt) {
		ArrayAnalysis aa = new ArrayAnalysis(Arrays.copyOf(lvt.getNormalLogArray(), indexApogee));
		aa.calculateMaxValue();
		normalMax = aa.getMaxValue();
		indexNormalMax = aa.getIndexMaxValue();

		timeNormalMax = lvt.getTime(indexNormalMax);
		altNormalMax = lvt.getAltitudeLogArray()[indexNormalMax];
	}
	
	private void calculateSideMax(LoggerVariable lvt) {
		ArrayAnalysis aa = new ArrayAnalysis(Arrays.copyOf(lvt.getSideLogArray(), indexApogee));
		aa.calculateMaxValue();
		sideMax = aa.getMaxValue();
		indexSideMax= aa.getIndexMaxValue();

		timeSideMax = lvt.getTime(indexSideMax);
		altSideMax = lvt.getAltitudeLogArray()[indexSideMax];
	}

	private void compute2ndPara(LoggerVariableParachute lvp) {
		time2ndPara = lvp.getTime(index2ndPara);
		alt2ndPara = lvp.getAltitudeArray()[index2ndPara];
	}

	private void calculateLandingTrajectory(LoggerVariable lvt) {
		timeLandingTrajectory = lvt.getTime(indexLandingTrajectory);
		System.arraycopy(lvt.getPosENUlog(indexLandingTrajectory), 0, posENUlandingTrajectory, 0, 2);
		downrangeLandingTrajectory = lvt.getDownrangeLogArray()[indexLandingTrajectory];
	}

	private void calculateLandingParachute(LoggerVariableParachute lvp) {
		timeLandingParachute = lvp.getTime(indexLandingParachute);
		System.arraycopy(lvp.getPosENUlog(indexLandingParachute), 0, posENUlandingParachute, 0, 2);
		downrangeLandingParachute = lvp.getDownrangeArray()[indexLandingParachute];
	}

	// index入力後に実行する
	public void calculate(LoggerVariable lvt, LoggerVariableParachute lvp) {
		calculateLaunchClear(lvt);
		compute2ndPara(lvp);
		calculateLandingTrajectory(lvt);
		calculateLandingParachute(lvp);
	}

	//-------------------- Get Function --------------------
	// Launch Clear
	public double getVelLaunchClear() {
		return velLaunchClear;
	}

	public double getTimeLaunchClear() {
		return timeLaunchClear;
	}

	public double getAccLaunchClear() {
		return accLaunchClear;
	}

	public int getIndexLaunchClear() {
		return indexLaunchClear;
	}

	// Apogee
	public double getAltApogee() {
		return altApogee;
	}

	public double getTimeApogee() {
		return timeApogee;
	}

	public int getIndexApogee() {
		return indexApogee;
	}

	public double getDownrangeApogee() {
		return downrangeApogee;
	}

	public double getVelAirApogee() {
		return velAirApogee;
	}

	// VelAirMax
	public double getVelAirMax() {
		return velAirMax;
	}

	public double getTimeMaxVelAir() {
		return timeMaxVelAir;
	}

	public double getAltitudeMaxVelAir() {
		return altVelAirMax;
	}

	public int getIndexMaxVelAir() {
		return indexMaxVelAir;
	}

	// Max-Q
	public double getDynamicsPressureMax() {
		return dynamicsPressureMax;
	}

	public double getTimeMaxQ() {
		return timeMaxQ;
	}

	public double getAltitudeMaxQ() {
		return altMaxQ;
	}

	public int getIndexMaxQ() {
		return indexMaxQ;
	}

	// Max Mach Number
	public double getMachMax() {
		return machMax;
	}

	public double getTimeMaxMach() {
		return timeMaxMach;
	}

	public double getAltitudeMaxMach() {
		return altMachMax;
	}

	public int getIndexMaxMach() {
		return indexMaxMach;
	}
	
	// Max Normal Force
	public double getNormalMax() {
		return normalMax;
	}

	public double getTimeNormalMax() {
		return timeNormalMax;
	}

	public double getAltitudeNormalMax() {
		return altNormalMax;
	}

	public int getIndexNormalMax() {
		return indexNormalMax;
	}
	
	// Max Side Force
	public double getSideMax() {
		return sideMax;
	}

	public double getTimeSideMach() {
		return timeSideMax;
	}

	public double getAltitudeSideMax() {
		return altSideMax;
	}

	public int getIndexSideMax() {
		return indexSideMax;
	}

	// Landing Trajctory
	public double getTimeLandingTrajectory() {
		return timeLandingTrajectory;
	}

	public double[] getPosENUlandingTrajectory() {
		return posENUlandingTrajectory;
	}

	public double getDownrangeLandingTrajectory() {
		return downrangeLandingTrajectory;
	}

	public int getIndexLandingTorajectory() {
		return indexLandingTrajectory;
	}

	// 2nd Para Open
	public double getTime2ndPara() {
		return time2ndPara;
	}

	public int getIndex2ndPara() {
		return index2ndPara;
	}

	public double getAlt2ndPara() {
		return alt2ndPara;
	}

	// Landing Parachute
	public double getTimeLandingParachute() {
		return timeLandingParachute;
	}

	public double[] getPosENUlandingParachute() {
		return posENUlandingParachute;
	}

	public double getDownrangeLandingParachute() {
		return downrangeLandingParachute;
	}

	public int getIndexLandingParachute() {
		return indexLandingParachute;
	}


}
