package quabla.simulator.rocket.wind;

public abstract class AbstractWind {

	protected double magneticDecDeg;

	abstract public double[] getWindNED(double alt);
	abstract public void setRefWind(double speed, double azimuth);
	abstract public double getRefWindSpeed();
	abstract public double getRefWindAzimuth();
	abstract public String getFilePath();
	abstract public double getExponent();

	/**
	 * @param speed 風速 [m/s]
	 * @param azimuth 風向。東から反時計回り正 [rad]
	 * @return windENU 風向からのENU座標系での風速ベクトル
	 * */
	protected static double[] toWindNED(double speed, double azimuth) {
		double[] windNED = {
				- speed * Math.cos(azimuth),
				- speed * Math.sin(azimuth),
				0.0
		};
		return windNED;
	}

	/**
	 * @param azimuthTrue [deg] 真北から測った風向の方位角
	 * @param magDec [deg] 磁気偏角
	 * @return [deg]
	 * */ 
	protected static double getMagAzimuthNED(double azimuthTrue, double magDec){
		return azimuthTrue - magDec;
	}
	
	/**
	 * @param azimuthTrue [deg] 真北から測った風向の方位角
	 * @param magDec [deg] 磁気偏角
	 * @return [deg]
	 * */ 
	protected static double getMagAzimuthENU(double azimuthTrue, double magDec){
		return - azimuthTrue + magDec + 90.;
	}

}
