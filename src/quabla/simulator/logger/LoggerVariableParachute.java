package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.OtherVariableParachute;
import quabla.simulator.variable.VariableParachute;

public class LoggerVariableParachute {

	private double[] timeLog;
	private double[] timeStepLog;
	private double[][] posENUlog;
	private double[][] velENUlog;
	private double[] velDescentLog;
	private double[] altitudeLog;
	private double[] downrangeLog;
	private double[][] windENUlog;
	private double[][] velAirENUlog;
	private double[] velAirAbsLog;


	private int length;

	private ArrayList<Double> timeLogArrayList = new ArrayList<>();
	private ArrayList<Double> timeStepLogArrayList = new ArrayList<>();
	private ArrayList<Double> posENUxlogArrayList = new ArrayList<>();
	private ArrayList<Double> posENUylogArrayList = new ArrayList<>();
	private ArrayList<Double> posENUzlogArrayList = new ArrayList<>();
	private ArrayList<Double> velENUxlogArrayList = new ArrayList<>();
	private ArrayList<Double> velENUylogArrayList = new ArrayList<>();
	private ArrayList<Double> velENUzlogArrayList = new ArrayList<>();
	private ArrayList<Double> velDescentLogArrayList = new ArrayList<>();

	private final OtherVariableParachute ovp;

	public LoggerVariableParachute(Rocket rocket) {
		ovp = new OtherVariableParachute(rocket);
	}

	public void log(VariableParachute variable, double timeStep) {
		timeLogArrayList.add(variable.getTime());
		timeStepLogArrayList.add(timeStep);
		posENUxlogArrayList.add(variable.getPosENU().toDouble(0));
		posENUylogArrayList.add(variable.getPosENU().toDouble(1));
		posENUzlogArrayList.add(variable.getPosENU().toDouble(2));
		velDescentLogArrayList.add(variable.getVelDescent());
		velENUxlogArrayList.add(variable.getVelENU().toDouble(0));
		velENUylogArrayList.add(variable.getVelENU().toDouble(1));
		velENUzlogArrayList.add(variable.getVelENU().toDouble(2));
	}

	public void makeArray() {
		length = timeLogArrayList.size();

		timeLog = new double[length];
		timeStepLog = new double[length];
		posENUlog = new double[length][3];
		velENUlog = new double[length][3];
		velDescentLog = new double[length];

		altitudeLog = new double[length];
		downrangeLog = new double[length];
		windENUlog = new double[length][3];
		velAirENUlog = new double[length][3];
		velAirAbsLog = new double[length];

		for(int i = 0; i < length; i++) {
			timeLog[i] = timeLogArrayList.get(i);
			timeStepLog[i] = timeStepLogArrayList.get(i);
//			System.arraycopy(posENUlogArrayList.get(i).toDouble(), 0, posENUlog[i], 0, 3);
//			System.arraycopy(velENUlogArrayList.get(i).toDouble(), 0, velENUlog[i], 0, 3);
			posENUlog[i][0] = posENUxlogArrayList.get(i);
			posENUlog[i][1] = posENUylogArrayList.get(i);
			posENUlog[i][2] = posENUzlogArrayList.get(i);
			velENUlog[i][0] = velENUxlogArrayList.get(i);
			velENUlog[i][1] = velENUylogArrayList.get(i);
			velENUlog[i][2] = velENUzlogArrayList.get(i);
			velDescentLog[i] = velDescentLogArrayList.get(i);

			ovp.calculateOtherVariable(timeLog[i], posENUlog[i], velENUlog[i]);
			altitudeLog[i] = ovp.getAltitude();
			downrangeLog[i] = ovp.getDownrange();
			System.arraycopy(ovp.getWindENU(), 0, windENUlog[i], 0, 3);
			System.arraycopy(ovp.getVelAirENU(), 0, velAirENUlog[i], 0, 3);
			velAirAbsLog[i] = ovp.getVelAirAbs();
		}
	}

	public int getArrayLength() {
		return length;
	}

	public double getTime(int index) {
		return timeLog[index];
	}

	public double getTimeStep(int index) {
		return timeStepLog[index];
	}

	public double[] getPosENUlog(int index) {
		return posENUlog[index];
	}

	public double[] getVelENUlog(int index) {
		return velENUlog[index];
	}

	public double getVelDescentLog(int index) {
		return velDescentLog[index];
	}

	public double[] getTimeArray() {
		return timeLog;
	}

	public double[] getTimeStepArray() {
		return timeStepLog;
	}

	public double[][] getPosENUArray(){
		return posENUlog;
	}

	public double[][] getVelENUArray(){
		return velENUlog;
	}

	public double[] getVelDescentArray() {
		return velDescentLog;
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

	public void copy(int indexLimit, LoggerVariable logdata) {
		for(int i = 0; i <= indexLimit; i++) {
			timeLogArrayList.add(logdata.getTimeArrayList(i));
			timeStepLogArrayList.add(logdata.getTimeStepArrayList(i));
//			posENUlogArrayList.add(logdata.getPosENU(i));
			posENUxlogArrayList.add(logdata.getPosENU(i).toDouble(0));
			posENUylogArrayList.add(logdata.getPosENU(i).toDouble(1));
			posENUzlogArrayList.add(logdata.getPosENU(i).toDouble(2));
			velDescentLogArrayList.add(logdata.getVelENU(i).toDouble(2));
//			velENUlogArrayList.add(logdata.getVelENU(i));
			velENUxlogArrayList.add(logdata.getVelENU(i).toDouble(0));
			velENUylogArrayList.add(logdata.getVelENU(i).toDouble(1));
			velENUzlogArrayList.add(logdata.getVelENU(i).toDouble(2));
		}
	}
}
