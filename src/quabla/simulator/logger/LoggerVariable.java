package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.Variable;

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

	private int length;

	private ArrayList<Double> timeLogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> posENULogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> velENUlogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> omegaBODYlogArraylist = new ArrayList<>();
	private ArrayList<MathematicalVector> quatLogArrayList = new ArrayList<>();


	public void log(Variable variable) {

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

		for(int i=0; i<length; i++) {
			timeArray[i] = timeLogArrayList.get(i);
			System.arraycopy(posENULogArrayList.get(i).toDouble(), 0, posENUlog[i], 0, 3);
			System.arraycopy(velENUlogArrayList.get(i).toDouble(), 0, velENUlog[i], 0, 3);
			System.arraycopy(omegaBODYlogArraylist.get(i).toDouble(), 0, omegaBODYlog[i], 0, 3);
			System.arraycopy(quatLogArrayList.get(i).toDouble(), 0, quatLog[i], 0, 4);
		}
	}

	public int getArrayLength() {
		return length;
	}

	public double getTimeArrayList(int index) {
		return timeLogArrayList.get(index);
	}

	public MathematicalVector getPos_ENU(int index) {
		return posENULogArrayList.get(index);
	}

	public MathematicalVector getVel_ENU(int index) {
		return velENUlogArrayList.get(index);
	}

	public MathematicalVector getOmega_BODY(int index) {
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
