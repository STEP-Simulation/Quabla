package quabla.simulator.logger.ivent_value;

import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableParachute;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableTrajectory;
import quabla.simulator.numerical_analysis.ArrayAnalysis;

public class IventValueSingle {

	private double velLaunchClear;
	private double timeLaunchClear, timeMaxQ, timeMaxVelAir, timeMaxMach, timeApogee, timeLandingTrajectory, timeLandingParachute;
	private double altApogee, altMaxQ, altVelAirMax, altMachMax;
	private double accLaunchClear;
	private double dynamicsPressureMax;
	private double velAirMax, velAirApogee;
	private double machMax;
	private double downrangeApogee, downrangeLandingTrajectory, downrangeLandingParachute;
	private double[] posENUlandingTrajectory = new double[2];
	private double[] posENUlandingParachute = new double[2];
	private int indexLaunchClear, indexMaxQ, indexMaxVelAir, indexMaxMach, indexApogee, indexLandingTrajectory, indexLandingParachute;

	LoggerVariable lvt; // LoggerVariable Trajectory
	LoggerVariable lvp; // LoggerVariable Parachute

	LoggerOtherVariableTrajectory lovt;
	LoggerOtherVariableParachute lovp;

	public IventValueSingle(LoggerVariable lvt, LoggerOtherVariableTrajectory lovt) {
		this.lvt = lvt;
		this.lovt = lovt;
	}

	public void setLoggerVariableParachute(LoggerVariable lvp,	LoggerOtherVariableParachute lovp) {
		this.lvp = lvp;
		this.lovp = lovp;
	}

	public void setIndexLaunchClear(int indexLaunchClear) {
		this.indexLaunchClear = indexLaunchClear;
	}

	public void setIndexLandingTrajectory(int indexLandingTrajectory) {
		this.indexLandingTrajectory = indexLandingTrajectory;
	}

	public void setIndexLandingParachute(int indexLandingParachute) {
		this.indexLandingParachute = indexLandingParachute;
	}

/*
	public void setMaxAltitude(double altApogee) {
		this.altApogee = altApogee;
	}
*/
	/*public void setAltitudeMaxQ(double altMaxQ) {
		this.altMaxQ = altMaxQ;
	}*/

	/*public void setVelLaunchClear(double velLaunchClear) {
		this.velLaunchClear = velLaunchClear;
	}*/

	/*public void setTimeLaunchClear(double timeLaunchClear) {
		this.timeLaunchClear = timeLaunchClear;
	}*/

	/*public void setTimeMaxQ(double timeMaxQ) {
		this.timeMaxQ = timeMaxQ;
	}*/
/*
	public void setTimeMaxVelAir(double timeMaxVelAir) {
		this.timeMaxVelAir = timeMaxVelAir;
	}*/

	/*public void setTimeMaxMach(double timeMaxMach) {
		this.timeMaxMach = timeMaxMach;
	}*/
/*
	public void setTimeApogee(double timeApogee) {
		this.timeApogee = timeApogee;
	}
*/
	/*
	public void setTimeLandingTrajectory(double timeLandingTrajectory) {
		this.timeLandingTrajectory = timeLandingTrajectory;
	}

	public void setTimeLandingParachute(double timeLandingParachute) {
		this.timeLandingParachute = timeLandingParachute;
	}
*/
/*	public void setAccLaunchClear(double accLaunchClear) {
		this.accLaunchClear = accLaunchClear;
	}*/

/*	public void setDynamicsPressureMax(double dynamicsPressureMax) {
		this.dynamicsPressureMax = dynamicsPressureMax;
	}*/
/*
	public void setVelAirMax(double velAirMax) {
		this.velAirMax = velAirMax;
	}*/
/*
	public void setVelAirApogee(double velAirApogee) {
		this.velAirApogee = velAirApogee;
	}
*/
	/*public void setMachMax(double machMax) {
		this.machMax = machMax;
	}*/
/*
	public void setDownrangeApogee(double downrangeApogee) {
		this.downrangeApogee = downrangeApogee;
	}
*/
	/*
	public void setDownrangeLandingTrajectory(double downrangeLandingTrajectory) {
		this.downrangeLandingTrajectory = downrangeLandingTrajectory;
	}

	public void setDownrangeLandingParachute(double downrangeLandingParachute) {
		this.downrangeLandingParachute = downrangeLandingParachute;
	}*/

	public void calculateLaunchClear() {
		timeLaunchClear = lvt.getTime(indexLaunchClear);
		accLaunchClear = lovt.getAccAbsLog(indexLaunchClear);
		velLaunchClear = Math.sqrt(Math.pow(lvt.getVelENUlog(indexLaunchClear)[0], 2) + Math.pow(lvt.getVelENUlog(indexLaunchClear)[1], 2) + Math.pow(lvt.getVelENUlog(indexLaunchClear)[2], 2));
	}

	public void calculateAtApogee() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getAltitudeLogArray());
		aa.calculateMaxValue();
		this.altApogee = aa.getMaxValue();
		this.indexApogee = aa.getIndexMaxValue();

		timeApogee = lvt.getTime(indexApogee);
		downrangeApogee = lovt.getDownrangeLog(indexApogee);
		velAirApogee = lovt.getVelAirAbsLog(indexApogee);
	}

	public void calculateAtVelAirMax() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getVelAirAbsLogArray());
		aa.calculateMaxValue();
		velAirMax = aa.getMaxValue();
		indexMaxVelAir = aa.getIndexMaxValue();

		timeMaxVelAir = lvt.getTime(indexMaxVelAir);
		altVelAirMax = lovt.getAltitudeLog(indexMaxVelAir);
	}

	public void calculateAtMaxQ() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getDynamicsPressureLogArray());
		aa.calculateMaxValue();
		dynamicsPressureMax = aa.getMaxValue();
		indexMaxQ = aa.getIndexMaxValue();

		timeMaxQ = lvt.getTime(indexMaxQ);
		altMaxQ = lovt.getAltitudeLog(indexMaxQ);
	}

	public void calculateMachMax() {
		ArrayAnalysis aa = new ArrayAnalysis(lovt.getMachLogArray());
		aa.calculateMaxValue();
		machMax = aa.getMaxValue();
		indexMaxMach = aa.getIndexMaxValue();

		timeMaxMach = lvt.getTime(indexMaxMach);
		altMachMax = lovt.getAltitudeLog(indexMaxMach);
	}

	public void calculateLandingTrajectory() {
		timeLandingTrajectory = lvt.getTime(indexLandingTrajectory);
		System.arraycopy(lvt.getPosENUlog(indexLandingTrajectory), 0, posENUlandingTrajectory, 0, 2);
		downrangeLandingTrajectory = lovt.getDownrangeLog(indexLandingTrajectory);
	}

	public void calculateLandingParachute() {
		timeLandingParachute = lvp.getTime(indexLandingParachute);
		System.arraycopy(lvp.getPosENUlog(indexLandingParachute), 0, posENUlandingParachute, 0, 2);
		downrangeLandingParachute = lovp.getDownrangeLog(indexLandingParachute);
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