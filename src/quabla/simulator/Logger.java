package quabla.simulator;

import java.util.ArrayList;

public class Logger {

	public double[] time_array;
	public double[][] Pos_ENU_log;
	public double[][] Vel_ENU_log;
	public double[][] omega_Body_log;
	public double[][] quat_log;

	ArrayList<Double> time_log = new ArrayList<>();
	ArrayList<Double> X_ENU_log = new ArrayList<>();
	ArrayList<Double> Y_ENU_log = new ArrayList<>();
	ArrayList<Double> Z_ENU_log = new ArrayList<>();
	ArrayList<Double> Vel_x_ENU_log = new ArrayList<>();
	ArrayList<Double> Vel_y_ENU_log = new ArrayList<>();
	ArrayList<Double> Vel_z_ENU_log = new ArrayList<>();
	ArrayList<Double> p_log = new ArrayList<>();
	ArrayList<Double> q_log = new ArrayList<>();
	ArrayList<Double> r_log = new ArrayList<>();
	ArrayList<Double> quat0_log = new ArrayList<>();
	ArrayList<Double> quat1_log = new ArrayList<>();
	ArrayList<Double> quat2_log = new ArrayList<>();
	ArrayList<Double> quat3_log = new ArrayList<>();



	public Logger(int max_step) {

	}

	public void logger(Variable variable) {

		time_log.add(variable.time);
		X_ENU_log.add(variable.Pos_ENU[0]);
		Y_ENU_log.add(variable.Pos_ENU[1]);
		Z_ENU_log.add(variable.Pos_ENU[2]);
		Vel_x_ENU_log.add(variable.Vel_ENU[0]);
		Vel_y_ENU_log.add(variable.Vel_ENU[1]);
		Vel_z_ENU_log.add(variable.Vel_ENU[2]);
		p_log.add(variable.omega_Body[0]);
		q_log.add(variable.omega_Body[1]);
		r_log.add(variable.omega_Body[2]);
		quat0_log.add(variable.quat[0]);
		quat1_log.add(variable.quat[1]);
		quat2_log.add(variable.quat[2]);
		quat3_log.add(variable.quat[3]);

	}

	public void list2array() {
		int length = time_log.size();
		time_array = new double[length];
		Pos_ENU_log = new double[length][3];
		Vel_ENU_log = new double[length][3];
		omega_Body_log = new double[length][3];
		quat_log = new double[length][4];

		for(int i=0; i<length; i++) {
			time_array[i] = time_log.get(i);
			Pos_ENU_log[i][0] = X_ENU_log.get(i);
			Pos_ENU_log[i][1] = Y_ENU_log.get(i);
			Pos_ENU_log[i][2] = X_ENU_log.get(i);
			Vel_ENU_log[i][0] = Vel_x_ENU_log.get(i);
			Vel_ENU_log[i][1] = Vel_y_ENU_log.get(i);
			Vel_ENU_log[i][2] = Vel_z_ENU_log.get(i);
			omega_Body_log[i][0] = p_log.get(i);
			omega_Body_log[i][1] = q_log.get(i);
			omega_Body_log[i][2] = r_log.get(i);
			quat_log[i][0] = quat0_log.get(i);
			quat_log[i][1] = quat1_log.get(i);
			quat_log[i][2] = quat2_log.get(i);
			quat_log[i][3] = quat3_log.get(i);

		}

	}

}
