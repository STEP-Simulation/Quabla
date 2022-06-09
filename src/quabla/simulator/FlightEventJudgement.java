package quabla.simulator;

import quabla.simulator.rocket.Rocket;
import quabla.simulator.variable.VariableParachute;
import quabla.simulator.variable.VariableTrajectory;

/**
 * FlightEventJudgement check rocket flight status and judge flight event.
 * This class can judge event of tip-off, launch clear, apogee, landing, separation of 2nd parachute.
 * */
public class FlightEventJudgement {

	private Rocket rocket;

	public FlightEventJudgement(Rocket rocket) {
		this.rocket = rocket;
	}

	public boolean judgeTipOff(VariableTrajectory variable) {
		boolean judge;

		if(variable.getDistanceUpperLug() >= rocket.lengthLauncherRail) {
			judge = true;
		}else {
			judge = false;
		}

		return judge;
	}

	public boolean judgeLaunchClear(VariableTrajectory  variable) {
		boolean judge;

		if(variable.getDistanceLowerLug() >= rocket.lengthLauncherRail) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeApogee(VariableTrajectory variable) {
		boolean judge;

		if((variable.getTime() >= rocket.engine.timeBurnout) && (variable.getVelDescent() >= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeLanding(VariableTrajectory variable) {
		boolean judge;

		if((variable.getTime() >= rocket.engine.timeBurnout) && (variable.getAltitude() <= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judgeLanding(VariableParachute variable) {
		boolean judge;

		if((variable.getTime() >= rocket.engine.timeBurnout) && (variable.getAltitude() <= 0.0)) {
			judge = true;
		}else {
			judge = false;
		}
		return judge;
	}

	public boolean judge2ndPara(VariableParachute variable) {
		boolean judge;

//		if(rocket.para2Exist &&
//				(variable.getVelDescent() <= 0.0) &&
//				(variable.getAltitude() >= rocket.alt_para2)) {
//			judge = true;
//		}else {
//			judge = false;
//		}
		
		if(rocket.para2Exist) {
			if(rocket.para2Timer) {
				if((variable.getVelDescent() <= 0.0) &&
				   (variable.getTime() <= rocket.time_para2)) {
					judge = true;
				}else{
					judge = false;
				}
			}else {
				if((variable.getVelDescent() <= 0.0) &&
				   (variable.getAltitude() >= rocket.alt_para2)) {
					judge = true;
				}else {
					judge = false;
				}
			}
		}else {
			judge = false;
		}

		return judge;
	}

}
