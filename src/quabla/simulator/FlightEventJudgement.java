package quabla.simulator;

import quabla.simulator.variable.Variable;
import quabla.simulator.variable.VariableParachute;

/**
 * FlightEventJudgement check rocket flight status and judge flight event.
 * This class can judge event of tip-off, launch clear, apogee, landing, separation of 2nd parachute.
 * */
public class FlightEventJudgement {

	private RocketParameter rocket;

	public FlightEventJudgement(RocketParameter rocket) {
		this.rocket = rocket;
	}

	public boolean judgeTipOff(Variable variable) {
		boolean judge;

		if(variable.getDistanceUpperLug() >= rocket.lengthLauncherRail) {
			judge = true;
		}else {
			judge = false;
		}

		return judge;
	}

	public boolean judgeLaunchClear(Variable  variable) {
		boolean judge;

		if(variable.getDistanceLowerLug() >= rocket.lengthLauncherRail) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeApogee(Variable variable) {
		boolean judge;

		if((variable.getTime() >= rocket.timeBurnout) && (variable.getVelDescet() >= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeLanding(Variable variable) {
		boolean judge;

		if((variable.getTime() >= rocket.timeBurnout) && (variable.getAltitude() <= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeLanding(VariableParachute variable) {
		boolean judge;

		if((variable.getTime() >= rocket.timeBurnout) && (variable.getAltitude() <= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judge2ndPara(VariableParachute variable) {
		boolean judge;

		if(rocket.para2Exist &&
				(variable.getVelDescent() <= 0.0) &&
				(variable.getAltitude() >= rocket.alt_para2)) {
			judge = true;
		}else {
			judge = false;
		}

		return judge;
	}

}
