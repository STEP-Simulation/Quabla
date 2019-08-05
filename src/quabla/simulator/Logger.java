package quabla.simulator;

public class Logger {

	public double[] time;
	public double[][] Pos_ENU;
	public double[][] Vel_ENU;
	public double[][] omega_Body;
	public double[][] quat;

	public Logger(int max_step) {
		time = new double[max_step];
		Pos_ENU = new double[max_step][3];
		Vel_ENU = new double[max_step][3];
		omega_Body = new double[max_step][3];
		quat = new double[max_step][4];

	}

	public void logger(int index, Variable variable) {
		time[index] = variable.time;//これいるか？

		for(int i=0; i<3; i++) {
			Pos_ENU[index][i] = variable.Pos_ENU[i];
			Vel_ENU[index][i] = variable.Vel_ENU[i];
			omega_Body[index][i] = variable.omega_Body[i];
		}

		for(int i=0; i<4; i++) {
			quat[index][i] = variable.quat[i];
		}

	}

}
