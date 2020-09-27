package quabla.simulator.rocket.wind;

public abstract class AbstractWind {

	protected double magneticDecDeg;

	abstract public double[] getWindENU(double alt);
	abstract public void setRefWind(double speed, double azimuth);

	/**
	 * @param speed 風速 [m/s]
	 * @param azimuth 風向。東から反時計回り正 [rad]
	 * @return windENU 風向からのENU座標系での風速ベクトル
	 * */
	protected static double[] toWindENU(double speed, double azimuth) {
		double[] windENU = {
				- speed * Math.cos(azimuth),
				- speed * Math.sin(azimuth),
				0.0
		};
		return windENU;
	}

}
