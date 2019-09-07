package quabla.simulator;

public class FlightEventJudgement {

	private RocketParameter rocket;

	public FlightEventJudgement(RocketParameter rocket) {
		this.rocket = rocket;
	}

	public boolean judgeTipOff(Variable variable) {
		boolean judge;

		if(variable.getDistanceUpperLug() >= rocket.launcher_rail) {
			judge = true;
		}else {
			judge = false;
		}

		return judge;
	}

	public boolean judgeLaunchClear(Variable  variable) {
		boolean judge;

		if(variable.getDistanceLowerLug() >= rocket.launcher_rail) {
			judge = true;
		}else {
			judge = false;
		}

		return judge;
	}

	public boolean judgeApogee(Variable variable) {
		boolean judge;

		if((variable.getTime() >= rocket.time_Burnout) && (variable.getVelDescet() >= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeLanding(Variable variable) {
		boolean judge;

		if((variable.getTime() >= rocket.time_Burnout) && (variable.getAltitude() <= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

}
