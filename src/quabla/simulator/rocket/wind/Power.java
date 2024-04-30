package quabla.simulator.rocket.wind;

import quabla.simulator.Coordinate;

/**
 * Power provides wind vector calculated by power wind law model.
 * Using power wind law model, wind azimuth don't vary but speed
 * vary exponentially from altitude.
 * Exponent of power law is determined by launch pad condition(ex. sea, land etc.).
 * */
public class Power extends AbstractWind{

	private double speedRef, azimuthRef;
	private final double altRef, exponent;
	/**
	 * @param speedRef wind speed @ reference altitude [m/s]
	 * @param azimuthRef wind azimuth @ ref alt. [deg] . mesured by clockwise.
	 * @param altRef reference altitude [m]
	 * @param exponent exponent of power law
	 * */
	public Power(double speedRef, double azimuthRef, double altRef, double exponent, double magneticDec) {
		this.speedRef = speedRef;
		this.azimuthRef = azimuthRef;
		this.altRef = altRef;
		this.exponent = exponent;
		super.magneticDecDeg = magneticDec;
	}

	private double getWindSpeed(double alt) {
		if(alt <= 0.0) {
			return 0.0;
		}else {
			return speedRef * Math.pow(alt / altRef, 1 / exponent);
		}
	}

	@Override
	public double[] getWindNED(double alt) {
		// double azimuthRad = Coordinate.deg2rad(- azimuthRef + 90.0 + magneticDecDeg);
		// double azimuthRad = Coordinate.deg2rad(azimuthRef - magneticDecDeg);
		double azimuthRad = Coordinate.deg2rad(getMagAzimuthNED(azimuthRef, magneticDecDeg));
		return toWindNED(getWindSpeed(alt), azimuthRad);
	}

	@Override
	public void setRefWind(double speed, double azimuth) {
		this.speedRef = speed;
		this.azimuthRef = azimuth;
	}
	
	@Override
	public double getRefWindSpeed() {
		return speedRef;
	}
	
	@Override
	public double getRefWindAzimuth() {
		return azimuthRef;
	}
	
	@Override
	public double getExponent() {
		return exponent;
	}
	
	@Override
	public String getFilePath() {
		return null;
	}
}
