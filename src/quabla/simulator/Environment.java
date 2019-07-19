package quabla.simulator;

public class Environment {

	double temperture0;

	/*
	 * U.S. standard atomosuphere,1976
	 * ジオポテンシャル高度11 kmまで対応
	 * **/


	public Environment(double temperture0){
		this.temperture0 = temperture0;
	}


	public double density_air(double altitude) {
		double density;//[kg/m^3] air density

		density = temperture0;

		return density;
	}

	public double atomospheric_pressure(double altitude) {
		double pressure;//[Pa] atomospheric pressure
		double pressure0 = 101325;//[Pa] atomospheric pressure at 0 m
		double gamma = -5.256;

		pressure = pressure0 * Math.pow(288.15 / (273.15 + temperture(altitude)), gamma);

		return pressure ;
	}

	public double temperture(double altitude) {
		double temperture ;
		double gamma = 0.0065;

		temperture = temperture0 - gamma * altitude;


		return temperture;
	}

	public double soundspeed(double altitude ) {
		double Cs; //[m/s] sound speed

		//Cs = 331.5 + 0.61*temperture(altitude);
		Cs = 20.0468 * Math.sqrt(temperture(altitude));

		return Cs;
	}

	public double gravity(double altitude) {
		double gravity;
		double g0 = 9.80665;
		double R_earth = 6378.137e3 ; //地球の半径

		gravity = g0 * Math.pow(R_earth / (R_earth + altitude) , 2);

		return gravity;
	}


}
