package quabla.simulator.logger.event_value;

import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.LoggerVariableParachute;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableParachute;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableTrajectory;
import quabla.simulator.numerical_analysis.ArrayAnalysis;

/**
 * EventValueSingle stores and calcurate flight event values(e.g. Launch Clear, apogee, etc.).
 * */
public class EventValueSingle {

	private double velLaunchClear;
	private double timeLaunchClear, timeMaxQ, timeMaxVelAir, timeMaxMach, timeApogee, time2ndPara, timeLandingTrajectory, timeLandingParachute;
	private double altApogee, altMaxQ, altVelAirMax, altMachMax, alt2ndPara;
	private double accLaunchClear;
	private double dynamicsPressureMax;
	private double velAirMax, velAirApogee;
	private double machMax;
	private double downrangeApogee, downrangeLandingTrajectory, downrangeLandingParachute;
	private double[] posENUlandingTrajectory = new double[2];
	private double[] posENUlandingParachute = new double[2];
	private int indexLaunchClear, indexMaxQ, index2ndPara, indexMaxVelAir, indexMaxMach, indexApogee, indexLandingTrajectory, indexLandingParachute;

	LoggerVariable lvt; // LoggerVariable Trajectory
	LoggerVariableParachute lvp; // LoggerVariable Parachute

	LoggerOtherVariableTrajectory lovt;
	LoggerOtherVariableParachute lovp;

	public EventValueSingle(LoggerVariable lvt, LoggerOtherVariableTrajectory lovt) {
		this.lvt = lvt;
		this.lovt = lovt;
		// イベント発生時のインデックスを必要としないもの(最高高度など)のみ先に計算
		// イベント発生時のインデックスを外部から入力する必要があるもの(ランチクリアなど)は値入力時に計算
		calculateMachMax();
		calculateAtVelAirMax();
		calculateAtMaxQ();
		calculateAtApogee();
	}

	//-------------------- Set Function --------------------
	public void setLoggerVariableParachute(LoggerVariableParachute lvp, LoggerOtherVariableParachute lovp) {
		this.lvp = lvp;
		this.lovp = lovp;
	}

	public void setIndexLaunchClear(int indexLaunchClear) {
		this.indexLaunchClear = indexLaunchClear;
		calculateLaunchClear();
	}

	public void setIndexLandingTrajectory(int indexLandingTrajectory) {
		this.indexLandingTrajectory = indexLandingTrajectory;
		calculateLandingTrajectory();
	}

	public void setIndexLandingParachute(int indexLandingParachute) {
		this.indexLandingParachute = indexLandingParachute;
		calculateLandingParachute();
	}

	public void setIndex2ndPara(int index2ndPara) {
		this.index2ndPara = index2ndPara;
		compute2ndPara();
	}

	//-------------------- Calculate Function --------------------
	private void calculateLaunchClear() {
		timeLaunchClear = lvt.getTime(indexLaunchClear);
		accLaunchClear = lovt.getAccAbsLogArray()[indexLaunchClear];
		velLaunchClear = Math.sqrt(Math.pow(lvt.getVelENUlog(indexLaunchClear)[0], 2) + Math.pow(lvt.getVelENUlog(indexLaunchClear)[1], 2) + Math.pow(lvt.getVelENUlog(indexLaunchClear)[2], 2));
		//velLaunchClear =
	}

	private void calculateAtApogee() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getAltitudeLogArray());
		aa.calculateMaxValue();
		this.altApogee = aa.getMaxValue();
		this.indexApogee = aa.getIndexMaxValue();

		timeApogee = lvt.getTime(indexApogee);
		downrangeApogee = lovt.getDownrangeLogArray()[indexApogee];
		velAirApogee = lovt.getVelAirAbsLogArray()[indexApogee];
	}

	private void calculateAtVelAirMax() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getVelAirAbsLogArray());
		aa.calculateMaxValue();
		velAirMax = aa.getMaxValue();
		indexMaxVelAir = aa.getIndexMaxValue();

		timeMaxVelAir = lvt.getTime(indexMaxVelAir);
		altVelAirMax = lovt.getAltitudeLogArray()[indexMaxVelAir];
	}

	private void calculateAtMaxQ() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getDynamicsPressureLogArray());
		aa.calculateMaxValue();
		dynamicsPressureMax = aa.getMaxValue();
		indexMaxQ = aa.getIndexMaxValue();

		timeMaxQ = lvt.getTime(indexMaxQ);
		altMaxQ = lovt.getAltitudeLogArray()[indexMaxQ];
	}

	private void calculateMachMax() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getMachLogArray());
		aa.calculateMaxValue();
		machMax = aa.getMaxValue();
		indexMaxMach = aa.getIndexMaxValue();

		timeMaxMach = lvt.getTime(indexMaxMach);
		altMachMax = lovt.getAltitudeLogArray()[indexMaxMach];
	}

	private void compute2ndPara() {
		time2ndPara = lvp.getTime(index2ndPara);
		alt2ndPara = lovp.getAltitudeArray()[index2ndPara];
	}

	private void calculateLandingTrajectory() {
		timeLandingTrajectory = lvt.getTime(indexLandingTrajectory);
		System.arraycopy(lvt.getPosENUlog(indexLandingTrajectory), 0, posENUlandingTrajectory, 0, 2);
		downrangeLandingTrajectory = lovt.getDownrangeLogArray()[indexLandingTrajectory];
	}

	private void calculateLandingParachute() {
		timeLandingParachute = lvp.getTime(indexLandingParachute);
		System.arraycopy(lvp.getPosENUlog(indexLandingParachute), 0, posENUlandingParachute, 0, 2);
		downrangeLandingParachute = lovp.getDownrangeArray()[indexLandingParachute];
	}

	public void calculate() {
		calculateLaunchClear();
		calculateMachMax();
		calculateAtVelAirMax();
		calculateAtMaxQ();
		calculateAtApogee();
		calculateLandingTrajectory();
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
