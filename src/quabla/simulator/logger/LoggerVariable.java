package quabla.simulator.logger;

import java.util.ArrayList;

import quabla.simulator.numerical_analysis.vectorOperation.MathematicalVector;
import quabla.simulator.variable.Variable;

/**
 * 飛翔中のvariableの値を記録するためのクラス
 *
 * */
public class LoggerVariable {

	private double[] time_array;
	private double[][] pos_ENU_log;
	private double[][] vel_ENU_log;
	private double[][] omega_BODY_log;
	private double[][] quat_log;

	private int length;

	private ArrayList<Double> timeLogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> pos_ENULogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> velENUlogArrayList = new ArrayList<>();
	private ArrayList<MathematicalVector> omegaBODYlogArraylist = new ArrayList<>();
	private ArrayList<MathematicalVector> quatLogArrayList = new ArrayList<>();


	public void log(Variable variable) {

		timeLogArrayList.add(variable.getTime());
		pos_ENULogArrayList.add(variable.getPos_ENU());
		velENUlogArrayList.add(variable.getVel_ENU());
		omegaBODYlogArraylist.add(variable.getOmega_Body());
		quatLogArrayList.add(variable.getQuat());
	}
	/**
	 * ArrayListをdouble型の配列へ変換する
	 * */
	public void makeArray() {
		length = timeLogArrayList.size();

		time_array = new double[length];
		pos_ENU_log = new double[length][3];
		vel_ENU_log = new double[length][3];
		omega_BODY_log = new double[length][3];
		quat_log = new double[length][4];

		for(int i=0; i<length; i++) {
			time_array[i] = timeLogArrayList.get(i);
			System.arraycopy(pos_ENULogArrayList.get(i).toDouble(), 0, pos_ENU_log[i], 0, 3);
			System.arraycopy(velENUlogArrayList.get(i).toDouble(), 0, vel_ENU_log[i], 0, 3);
			System.arraycopy(omegaBODYlogArraylist.get(i).toDouble(), 0, omega_BODY_log[i], 0, 3);
			System.arraycopy(quatLogArrayList.get(i).toDouble(), 0, quat_log[i], 0, 4);
		}
	}

	public int getArrayLength() {
		return length;
	}

	public double getTimeArrayList(int index) {
		return timeLogArrayList.get(index);
	}

	public MathematicalVector getPos_ENU(int index) {
		return pos_ENULogArrayList.get(index);
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
		return time_array[index];
	}

	public double[] getPosENUlog(int index) {
		return pos_ENU_log[index];
	}

	public double[] getVelENUlog(int index) {
		return vel_ENU_log[index];
	}

	public double[] getOmegaBODYlog(int index) {
		return omega_BODY_log[index];
	}

	public double[] getQuatLog(int index) {
		return quat_log[index];
	}

	public double[] getTimeArray() {
		return time_array;
	}

	public double[][] getPosENUArray(){
		return pos_ENU_log;
	}

	public double[][] getVelENUArray(){
		return vel_ENU_log;
	}

	public double[][] getOmegaBODYArray(){
		return omega_BODY_log;
	}

	public double[][] getQuatArray(){
		return quat_log;
	}

	/**使わなくなったArrayListをnullにする
	 * 意図的にガベージコレクションの対象にして省メモリ化を図る
	 * */
	public void dumpArrayList() {
		timeLogArrayList = null;
		pos_ENULogArrayList = null;
		velENUlogArrayList = null;
		omegaBODYlogArraylist = null;
		quatLogArrayList = null;
	}
}
