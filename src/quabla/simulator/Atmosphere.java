package quabla.simulator;

public class Atmosphere {

	private double temperture0;
	private double Re = 6378.137 * Math.pow(10, 3);//地球の半径

	/*
	 * U.S. standard atomosuphere,1976
	 * ジオポテンシャル高度11 kmまで対応
	 * **/


	public Atmosphere(double temperture0){
		this.temperture0 = temperture0;
	}

	private double getGeopotentialAltitude(double altitude) {
		return Re * altitude / (Re + altitude);
	}


	public double getAirDensity(double altitude) {
		double density;//[kg/m^3] air density
		double temperture = getTemperture(altitude) + 273.15;//[K]
		double pressure = getAtomosphericPressure(altitude);

		density = 0.0034837 * pressure / temperture;

		return density;
	}

	public double getAtomosphericPressure(double altitude) {
		double pressure;//[Pa] atomospheric pressure
		double pressure0 = 101325;//[Pa] atomospheric pressure at 0 m
		double gamma = -5.256;

		pressure = pressure0 * Math.pow(288.15 / (273.15 + getTemperture(altitude)), gamma);

		return pressure ;
	}

	public double getTemperture(double altitude) {
		double temperture ;

		double geo_alt = getGeopotentialAltitude(altitude);

		temperture = temperture0 - 0.0065 * geo_alt;

		return temperture;
	}

	public double getSoundSpeed(double altitude ) {
		double Cs; //[m/s] sound speed

		Cs = 20.0468 * Math.sqrt(getTemperture(altitude) + 237.15);


		return Cs;
	}

	public double getGravity(double altitude) {
		double gravity;
		double g0 = 9.80665;

		gravity = g0 * Math.pow(Re / (Re + altitude) , 2);

		return gravity;
	}


}
