package quabla.simulator;

public class Variable {

	//Main parameters
	public double[] Pos_ENU = new double[3];
	public double[] Vel_ENU = new double[3];
	public double[] omega_Body = new double[3];
	public double[] quat = new double[4];


	//on Launcher Parameters
	public double[] Vel_Body = new double[3];
	public double[] distance_Body = new double[3];
	public double distance_upper_lug;
	public double distance_lower_lug;

	//Parachute Parameters
	public double Vel_descent;




}
