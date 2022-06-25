package quabla.simulator.rocket.wind;

import quabla.simulator.Coordinate;

/**
 * Constant provide constant wind vector.
 * Using constant wind model, both wind speed and azimuth don't vary from altitude.
 * */
public class Constant extends AbstractWind{

	private double speed, azimuthDeg;

	public Constant(double speed, double azimuth, double magneticDec) {
		this.speed = speed;
		this.azimuthDeg = azimuth;
		super.magneticDecDeg = magneticDec;
	}

	@Override
	public double[] getWindENU(double alt) {
		double azimuthRad = Coordinate.deg2rad(- azimuthDeg + 90.0 + magneticDecDeg);
		return toWindENU(speed, azimuthRad);
	}

	@Override
	public void setRefWind(double speed, double azimuth) {
		this.speed = speed;
		this.azimuthDeg = azimuth;
	}
	
	@Override
	public double getRefWindSpeed() {
		return speed;
	}
	
	@Override
	public double getRefWindAzimuth() {
		return azimuthDeg;
	}
	
	@Override
	public double getExponent() {
		return 0.0;
	}
	
	@Override
	public String getFilePath() {
		return null;
	}
}
