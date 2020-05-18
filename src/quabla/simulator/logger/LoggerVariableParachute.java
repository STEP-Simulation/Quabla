package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.VariableParachute;

public class LoggerVariableParachute {

	private double[] timeLog;
	private double[][] posENUlog;
	private double[][] velENUlog;
	private double[] velDescentLog;

	private int length;

	private ArrayList<Double> timeLogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> posENUlogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> velENUlogArrayList = new ArrayList<>();
	private ArrayList<Double> velDescentLogArrayList = new ArrayList<>();

	public void log(VariableParachute variable) {
		timeLogArrayList.add(variable.getTime());
		posENUlogArrayList.add(variable.getPosENU());
		velDescentLogArrayList.add(variable.getVelDescent());
		velENUlogArrayList.add(variable.getVelENU());
	}

	public void makeArray() {
		length = timeLogArrayList.size();

		timeLog = new double[length];
		posENUlog = new double[length][3];
		velENUlog = new double[length][3];
		velDescentLog = new double[length];

		for(int i = 0; i < length; i++) {
			timeLog[i] = timeLogArrayList.get(i);
			System.arraycopy(posENUlogArrayList.get(i).toDouble(), 0, posENUlog[i], 0, 3);
			System.arraycopy(velENUlogArrayList.get(i).toDouble(), 0, velENUlog[i], 0, 3);
			velDescentLog[i] = velDescentLogArrayList.get(i);
		}
	}

	public int getArrayLength() {
		return length;
	}

	public double getTime(int index) {
		return timeLog[index];
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

	public double[][] getPosENUArray(){
		return posENUlog;
	}

	public double[][] getVelENUArray(){
		return velENUlog;
	}

	public double[] getVelDescentArray() {
		return velDescentLog;
	}

	public void copy(int indexLimit, LoggerVariable logdata) {
		for(int i = 0; i <= indexLimit; i++) {
			timeLogArrayList.add(logdata.getTimeArrayList(i));
			posENUlogArrayList.add(logdata.getPosENU(i));
			velDescentLogArrayList.add(logdata.getVelENU(i).toDouble(2));
			velENUlogArrayList.add(logdata.getVelENU(i));
		}
	}
}
