package quabla.simulator;

public class Environment {

	private double temperture0;
	private double Re = 6378.137 * Math.pow(10, 3);//地球の半径

	/*
	 * U.S. standard atomosuphere,1976
	 * ジオポテンシャル高度11 kmまで対応
	 * **/


	public Environment(double temperture0){
		this.temperture0 = temperture0;
	}

	private double geopotential_altitude(double altitude) {
		return Re * altitude / (Re + altitude);
	}


	public double density_air(double altitude) {
		double density;//[kg/m^3] air density
		double temperture = temperture(altitude) + 273.15;//[K]
		double pressure = atomospheric_pressure(altitude);

		density = 0.0034837 * pressure / temperture;

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

		double geo_alt = geopotential_altitude(altitude);

		temperture = temperture0 - 0.0065 * geo_alt;

		return temperture;
	}

	public double soundspeed(double altitude ) {
		double Cs; //[m/s] sound speed

		Cs = 20.0468 * Math.sqrt(temperture(altitude) + 237.15);


		return Cs;
	}

	public double gravity(double altitude) {
		double gravity;
		double g0 = 9.80665;

		gravity = g0 * Math.pow(Re / (Re + altitude) , 2);

		return gravity;
	}


}
