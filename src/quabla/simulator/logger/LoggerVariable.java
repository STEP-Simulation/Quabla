package quabla.simulator;

import java.util.ArrayList;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;

public class Logger {

	public double[] time_array;
	public double[][] pos_ENU_log;
	public double[][] vel_ENU_log;
	public double[][] omega_BODY_log;
	public double[][] quat_log;

	//private ArrayList<Variable> variableLog = new ArrayList<>();
	private ArrayList<Double> timeLog = new ArrayList<>();
	private ArrayList<MathematicalVector> pos_ENULog = new ArrayList<>();
	private ArrayList<MathematicalVector> vel_ENULog = new ArrayList<>();
	private ArrayList<MathematicalVector> omega_BODYLog = new ArrayList<>();
	private ArrayList<MathematicalVector> quatLog = new ArrayList<>();


	public void logVariable(Variable variable) {

		//variableLog.add(index, variable);
		timeLog.add(variable.getTime());
		pos_ENULog.add(variable.getPos_ENU());
		vel_ENULog.add(variable.getVel_ENU());
		omega_BODYLog.add(variable.getOmega_Body());
		quatLog.add(variable.getQuat());
	}

	public void setArray() {
		int length = timeLog.size();

		time_array = new double[length];
		pos_ENU_log = new double[length][3];
		vel_ENU_log = new double[length][3];
		omega_BODY_log = new double[length][3];
		quat_log = new double[length][4];

		for(int i=0; i<length; i++) {
			time_array[i] = timeLog.get(i);
			System.arraycopy(pos_ENULog.get(i).getValue(), 0, pos_ENU_log[i], 0, 3);
			System.arraycopy(vel_ENULog.get(i).getValue(), 0, vel_ENU_log[i], 0, 3);
			System.arraycopy(omega_BODYLog.get(i).getValue(), 0, omega_BODY_log[i], 0, 3);
			System.arraycopy(quatLog.get(i).getValue(), 0, quat_log[i], 0, 4);
		}
	}

	public double getTime(int index) {
		return timeLog.get(index);
	}

	public MathematicalVector getPos_ENU(int index) {
		return pos_ENULog.get(index);
	}

	public MathematicalVector getVel_ENU(int index) {
		return vel_ENULog.get(index);
	}

	public MathematicalVector getOmega_BODY(int index) {
		return omega_BODYLog.get(index);
	}

	public MathematicalVector getQuat(int index) {
		return quatLog.get(index);
	}

	public void copyLog(int index_limit,Logger logdata) {

		for(int i=0; i <= index_limit; i++) {
			timeLog.add(logdata.getTime(i));
			pos_ENULog.add(logdata.getPos_ENU(i));
			vel_ENULog.add(logdata.getVel_ENU(i));
			omega_BODYLog.add(logdata.getOmega_BODY(i));
			quatLog.add(logdata.getQuat(i));
		}
	}

}
