package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.OtherVariableParachute;
import quabla.simulator.variable.VariableParachute;

public class LoggerVariableParachute {

	private double[] timeLog;
	private double[] timeStepLog;
	private double[][] posNEDlog;
	private double[][] velNEDlog;
	private double[] velDescentLog;
	private double[] altitudeLog;
	private double[] downrangeLog;
	private double[][] windNEDlog;
	private double[][] velAirNEDlog;
	private double[] velAirAbsLog;

	private int length;

	private ArrayList<Double> timeLogArrayList       = new ArrayList<>();
	private ArrayList<Double> timeStepLogArrayList   = new ArrayList<>();
	private ArrayList<Double> posNEDxlogArrayList    = new ArrayList<>();
	private ArrayList<Double> posNEDylogArrayList    = new ArrayList<>();
	private ArrayList<Double> posNEDzlogArrayList    = new ArrayList<>();
	private ArrayList<Double> velNEDxlogArrayList    = new ArrayList<>();
	private ArrayList<Double> velNEDylogArrayList    = new ArrayList<>();
	private ArrayList<Double> velNEDzlogArrayList    = new ArrayList<>();
	private ArrayList<Double> velDescentLogArrayList = new ArrayList<>();

	private final OtherVariableParachute ovp;

	public LoggerVariableParachute(Rocket rocket) {
		ovp = new OtherVariableParachute(rocket);
	}

	public void log(VariableParachute variable, double timeStep) {
		timeLogArrayList.add(variable.getTime());
		timeStepLogArrayList.add(timeStep);
		posNEDxlogArrayList.add(variable.getPosNED().toDouble(0));
		posNEDylogArrayList.add(variable.getPosNED().toDouble(1));
		posNEDzlogArrayList.add(variable.getPosNED().toDouble(2));
		velDescentLogArrayList.add(variable.getVelDescent());
		velNEDxlogArrayList.add(variable.getVelNED().toDouble(0));
		velNEDylogArrayList.add(variable.getVelNED().toDouble(1));
		velNEDzlogArrayList.add(variable.getVelNED().toDouble(2));
	}

	public void makeArray() {
		length = timeLogArrayList.size();

		timeLog       = new double[length];
		timeStepLog   = new double[length];
		posNEDlog     = new double[length][3];
		velNEDlog     = new double[length][3];
		velDescentLog = new double[length];

		altitudeLog  = new double[length];
		downrangeLog = new double[length];
		windNEDlog   = new double[length][3];
		velAirNEDlog = new double[length][3];
		velAirAbsLog = new double[length];

		for(int i = 0; i < length; i++) {
			timeLog[i] = timeLogArrayList.get(i);
			timeStepLog[i] = timeStepLogArrayList.get(i);
			posNEDlog[i][0] = posNEDxlogArrayList.get(i);
			posNEDlog[i][1] = posNEDylogArrayList.get(i);
			posNEDlog[i][2] = posNEDzlogArrayList.get(i);
			velNEDlog[i][0] = velNEDxlogArrayList.get(i);
			velNEDlog[i][1] = velNEDylogArrayList.get(i);
			velNEDlog[i][2] = velNEDzlogArrayList.get(i);
			velDescentLog[i] = velDescentLogArrayList.get(i);

			ovp.calculateOtherVariable(timeLog[i], posNEDlog[i], velNEDlog[i]);
			altitudeLog[i] = ovp.getAltitude();
			downrangeLog[i] = ovp.getDownrange();
			System.arraycopy(ovp.getWindENU(), 0, windNEDlog[i], 0, 3);
			System.arraycopy(ovp.getVelAirENU(), 0, velAirNEDlog[i], 0, 3);
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

	public double[] getPosNEDlog(int index) {
		return posNEDlog[index];
	}

	public double[] getVelNEDlog(int index) {
		return velNEDlog[index];
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

	public double[][] getPosNEDArray(){
		return posNEDlog;
	}

	public double[][] getVelNEDArray(){
		return velNEDlog;
	}

	public double[] getVelDescentArray() {
		return velDescentLog;
	}

	public double[][] getWindNEDArray(){
		return windNEDlog;
	}

	public double[][] getVelAirENUArray(){
		return velAirNEDlog;
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
			posNEDxlogArrayList.add(logdata.getPosNED(i).toDouble(0));
			posNEDylogArrayList.add(logdata.getPosNED(i).toDouble(1));
			posNEDzlogArrayList.add(logdata.getPosNED(i).toDouble(2));
			velDescentLogArrayList.add(logdata.getVelNED(i).toDouble(2));
			velNEDxlogArrayList.add(logdata.getVelNEDlog(i)[0]);
			velNEDylogArrayList.add(logdata.getVelNEDlog(i)[1]);
			velNEDzlogArrayList.add(logdata.getVelNEDlog(i)[2]);
		}
	}
}
