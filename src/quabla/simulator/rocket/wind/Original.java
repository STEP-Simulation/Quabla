package quabla.simulator.rocket.wind;

import quabla.simulator.Coordinate;
import quabla.simulator.GetCsv;
import quabla.simulator.numerical_analysis.Interpolation;

/**
 * Original provedes wind vector using wind data(broadcasting or statics wind).
 * Both wind speed and azimuth vary from altitude.
 * Wind data is interpolated by linear interpolation.
 * */
public class Original extends AbstractWind{

	private final Interpolation speedAnaly, azimuthAnaly;
	private double azimuthFix;// 落下分散計算時に使用
	private String filepath;

	/**
	 * @param filepath 風データのファイルパス
	 * */
	public Original(String filepath, double magneticDec) {
		/* 1st Column : alt [m]
		 * 2nd Column : wind speed [m/s]
		 * 3rd Column : wnd azimuth [deg]
		 * **/
		this.filepath = filepath;
		double[][] windData = GetCsv.get3ColumnArray(filepath);
		double[] altArray = new double[windData.length];
		double[] speedArray = new double[windData.length];
		double[] azimuthArray = new double[windData.length];
		for(int i = 0; i < windData.length; i++) {
			altArray[i] = windData[i][0];
			speedArray[i] = windData[i][1];
			azimuthArray[i] = windData[i][2];
		}
		speedAnaly = new Interpolation(altArray, speedArray);
		azimuthAnaly = new Interpolation(altArray, azimuthArray);
		super.magneticDecDeg = magneticDec;
	}

	private double getWindSpeed(double alt) {
		if(alt <= 0.0) {
			return 0.0;
		}else {
			return speedAnaly.linearInterp1column(alt);
		}
	}

	private double getWindAzimuth(double alt) {
		if(alt <= 0.0) {
			return 0.0;
		}else {
			return azimuthAnaly.linearInterp1column(alt);
		}
	}

	@Override
	public double[] getWindNED(double alt) {
		// double azimuthRad = Coordinate.deg2rad(- getWindAzimuth(alt) + 90.0 + magneticDecDeg);
		// double azimuthRad = Coordinate.deg2rad(getWindAzimuth(alt) - magneticDecDeg);
		double azimuthRad = Coordinate.deg2rad(getMagAzimuthNED(getWindAzimuth(alt), magneticDecDeg));

		return toWindNED(getWindSpeed(alt), azimuthRad);
	}

	@Override
	public void setRefWind(double speed, double azimuth) {
		azimuthFix = azimuth;
	}

	@Override
	public double getRefWindSpeed() {
		return 0.0;
	}
	
	@Override
	public double getRefWindAzimuth() {
		return 0.0;
	}
	
	@Override
	public double getExponent() {
		return 0.0;
	}
	
	@Override
	public String getFilePath() {
		return filepath;
	}
}
