package quabla.simulator;

public class ConstantVariable {

	private Atmosphere atm;
	private RocketParameter rocket;
	private AeroParameter aero;
	private Wind wind;

	public ConstantVariable(Atmosphere env, RocketParameter rocket, AeroParameter aero,Wind wind) {
		this.atm = env;
		this.rocket = rocket;
		this.aero = aero;
		this.wind = wind;
	}

	public RocketParameter getRocket() {
		return rocket;
	}

	public Atmosphere getAtmosphere() {
		return atm;
	}

	public AeroParameter getAeroParam() {
		return aero;
	}

	public Wind getWind() {
		return wind;
	}

}
