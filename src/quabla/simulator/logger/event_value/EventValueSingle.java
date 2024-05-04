package quabla.simulator.logger.event_value;

import java.util.Arrays;

import quabla.simulator.Coordinate;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.numerical_analysis.ArrayAnalysis;
import quabla.simulator.rocket.Rocket;

/**
 * EventValueSingle stores and calculate flight event values(e.g. Launch Clear, apogee, etc.).
 * */
public class EventValueSingle {

	private double fstMin, fstMax;
	private double velLaunchClear;
	private double timeLaunchClear, timeMaxQ, timeMaxVelAir, timeMaxMach, timeNormalMax, timeSideMax, timeApogee, time1stPara, time2ndPara, timeLandingTrajectory, timeLandingParachute, timeLandingPaylaod;
	private double altApogee, altMaxQ, altVelAirMax, altMachMax, altNormalMax, altSideMax, alt1stPara, alt2ndPara;
	private double accLaunchClear;
	private double dynamicsPressureMax;
	private double velAirMax, velAirApogee;
	private double velDescent;
	private double machMax;
	private double normalMax, sideMax;
	private double downrangeApogee, downrangeLandingTrajectory, downrangeLandingParachute, downrangeLandingPayload;
	private double[] posNEDlandingTrajectory = new double[2];
	private double[] posNEDlandingParachute  = new double[2];
	private double[] posNEDlandingPayload    = new double[2];
	private int indexLaunchClear, indexMaxQ, index1stPara, index2ndPara, indexMaxVelAir, indexMaxMach, indexNormalMax, indexSideMax, indexApogee, indexLandingTrajectory, indexLandingParachute, indexLandingPayload;

	public EventValueSingle(LoggerVariable lvt, Rocket rocket) {
		// イベント発生時のインデックスを必要としないもの(最高高度など)のみ先に計算
		// イベント発生時のインデックスを外部から入力する必要があるもの(ランチクリアなど)は値入力時に計算
		calculateFstMinMax(lvt);
		calculateAtApogee(lvt);
		calculateMachMax(lvt);
		calculateAtVelAirMax(lvt);
		calculateAtMaxQ(lvt);
		calculateNormalMax(lvt);
		calculateSideMax(lvt);
		calculate1stPara(lvt, rocket.timeParaLag);
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

	public void setIndexLandingPayload(int indexLandingPayload) {
		this.indexLandingPayload = indexLandingPayload;
	}

	public void setIndex1stPara(int index1stPara) {
		this.index1stPara = index1stPara;
	}
	
	public void setIndex2ndPara(int index2ndPara) {
		this.index2ndPara = index2ndPara;
	}

	//-------------------- Calculate Function --------------------
	private void calculateLaunchClear(LoggerVariable lvt) {
		timeLaunchClear = lvt.getTime(indexLaunchClear);
		accLaunchClear = lvt.getAccAbsLogArray()[indexLaunchClear];
		double[][] dcmNED2BODY = Coordinate.getDcmNED2BODYfromQuat(lvt.getQuatLog(indexLaunchClear));
		velLaunchClear = Coordinate.transVector(dcmNED2BODY, lvt.getVelNEDlog(indexLaunchClear))[0]; // 機軸方向対地速度をランチクリア速度とする
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

	private void calculateFstMinMax(LoggerVariable lvt){
		ArrayAnalysis aa = new ArrayAnalysis(lvt.getFstLogArray());
		aa.calculateMinimumValue();
		aa.calculateMaxValue();
		fstMin = aa.getMinValue();
		fstMax = aa.getMaxValue();
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

	private void calculate1stPara(LoggerVariable lvt, double timeParaLag){
		time1stPara = timeApogee + timeParaLag;
		ArrayAnalysis aa = new ArrayAnalysis(lvt.getTimeArray());
		index1stPara = aa.serchIndex(time1stPara);
		alt1stPara = lvt.getAltitudeLogArray()[index1stPara];
	}

	// private void compute1stPara(LoggerVariableParachute lvp) {
	// 	time1stPara = lvp.getTime(index1stPara);
	// 	alt1stPara = lvp.getAltitudeArray()[index1stPara];
	// }
	
	private void compute2ndPara(LoggerVariableParachute lvp) {
		time2ndPara = lvp.getTime(index2ndPara);
		alt2ndPara = lvp.getAltitudeArray()[index2ndPara];
	}

	private void calculateLandingTrajectory(LoggerVariable lvt) {
		timeLandingTrajectory = lvt.getTime(indexLandingTrajectory);
		System.arraycopy(lvt.getPosNEDlog(indexLandingTrajectory), 0, posNEDlandingTrajectory, 0, 2);
		downrangeLandingTrajectory = lvt.getDownrangeLogArray()[indexLandingTrajectory];
	}

	private void calculateLandingParachute(LoggerVariableParachute lvp) {
		timeLandingParachute = lvp.getTime(indexLandingParachute);
		System.arraycopy(lvp.getPosNEDlog(indexLandingParachute), 0, posNEDlandingParachute, 0, 2);
		downrangeLandingParachute = lvp.getDownrangeArray()[indexLandingParachute];
		velDescent = Math.abs(lvp.getVelDescentArray()[indexLandingParachute]);
	}

	private void calculateLandingPaylaod(LoggerVariableParachute lvp) {
		timeLandingPaylaod = lvp.getTime(indexLandingPayload);
		System.arraycopy(lvp.getPosNEDlog(indexLandingPayload), 0, posNEDlandingPayload, 0, 2);
		downrangeLandingPayload = lvp.getDownrangeArray()[indexLandingPayload];
		// velDescent = Math.abs(lvp.getVelDescentArray()[indexLandingPayload]);
	}

	// index入力後に実行する
	public void calculate(LoggerVariable lvt, LoggerVariableParachute lvp, LoggerVariableParachute lvpp) {
		calculateLaunchClear(lvt);
		// compute1stPara(lvp);
		compute2ndPara(lvp);
		calculateLandingTrajectory(lvt);
		calculateLandingParachute(lvp);
		calculateLandingPaylaod(lvpp);
	}

	public void calculate(LoggerVariable lvt, LoggerVariableParachute lvp) {
		calculateLaunchClear(lvt);
		// compute1stPara(lvp);
		compute2ndPara(lvp);
		calculateLandingTrajectory(lvt);
		calculateLandingParachute(lvp);
	}

	//-------------------- Get Function --------------------
	// Fst
	public double getFstMin(){
		return fstMin;
	}

	public double getFstMax(){
		return fstMax;
	}
	
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

	// Landing Trajectory
	public double getTimeLandingTrajectory() {
		return timeLandingTrajectory;
	}

	public double[] getPosNEDlandingTrajectory() {
		return posNEDlandingTrajectory;
	}

	public double getDownrangeLandingTrajectory() {
		return downrangeLandingTrajectory;
	}

	public int getIndexLandingTorajectory() {
		return indexLandingTrajectory;
	}

	// 1st Para Open
	public double getTime1stPara() {
		return time1stPara;
	}

	public int getIndex1stPara() {
		return index1stPara;
	}

	public double getAlt1stPara() {
		return alt1stPara;
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

	public double[] getPosNEDlandingParachute() {
		return posNEDlandingParachute;
	}

	public double[] getPosNEDlandingPayload() {
		return posNEDlandingPayload;
	}

	public double getDownrangeLandingParachute() {
		return downrangeLandingParachute;
	}
	
	public double getVelDescentLandingParachute(){
		return velDescent;
	}
	
	public int getIndexLandingParachute() {
		return indexLandingParachute;
	}


}
