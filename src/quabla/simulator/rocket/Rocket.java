package quabla.simulator.rocket;

import java.io.IOException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.JsonNode;

import quabla.QUABLA;
import quabla.output.OutputTxt;
import quabla.simulator.rocket.wind.AbstractWind;
import quabla.simulator.rocket.wind.Constant;
import quabla.simulator.rocket.wind.Original;
import quabla.simulator.rocket.wind.Power;

public class Rocket {

	// TODO 離陸前，燃焼後の時の重量，慣性モーメントをcsvに出力

	public final Engine engine;
	public final AeroParameter aero;
	public final Atmosphere atm;
	public final AbstractWind wind;

	public final double
	L,
	D,
	S,
	upperLug,
	lowerLug;
	public static int
	site;
	private final double
	mBef,
	mAft,
	mSt,
	mDry;
	private final double
	lcgDry,
	lcgSt,
	lcgAft;
	public final double lcgBef;
	private final double
	IjPitchBef,
	IjPitchAft,
	IjRollBef,
	IjRollAft,
	IjPitchDry,
	IjRollDry,
	IjStPitchUnit,
	IjStRollUnit;
	public final double
	CdS1,
	CdS2;
	public final boolean para2Exist, para2Timer;
	public final double alt_para2, time_para2;
	public final double dt;
	public final boolean existTipOff;
	public final double lengthLauncherRail, elevationLauncher, azimuthLauncher, magneticDec;
	public static double point[] = {0, 0, 0};

	public Rocket(JsonNode spec) {
		engine = new Engine(spec.get("Engine"));
		aero = new AeroParameter(spec.get("Aero"));
		atm = new Atmosphere(spec.get("Atmosphere").get("Temperture at 0 m [℃]").asDouble());
		JsonNode structure = spec.get("Structure");
		JsonNode parachute = spec.get("Parachute");
		JsonNode launchCond = spec.get("Launch Condition");
		if(spec.get("Wind").get("Wind File Exist").asBoolean() && QUABLA.simulationModeCheck.equals("single")) {
			wind = new Original(
					spec.get("Wind").get("Wind File").asText(),
					launchCond.get("Input Magnetic Azimuth [deg]").asDouble());
		}else if(spec.get("Wind").get("Wind Model").asText().equals("law")){
			wind = new Power(
					spec.get("Wind").get("Wind Speed [m/s]").asDouble(),
					spec.get("Wind").get("Wind Azimuth [deg]").asDouble(),
					spec.get("Wind").get("Wind Reference Altitude [m]").asDouble(),
					spec.get("Wind").get("Wind Power Law Coefficient").asDouble(),
					launchCond.get("Input Magnetic Azimuth [deg]").asDouble());
		}else {
			wind  = new Constant(
					spec.get("Wind").get("Wind Speed [m/s]").asDouble(),
					spec.get("Wind").get("Wind Azimuth [deg]").asDouble(),
					launchCond.get("Input Magnetic Azimuth [deg]").asDouble());
		}

		//--------------- Geometry ---------------
		L = structure.get("Length [m]").asDouble();
		D = structure.get("Diameter [m]").asDouble();
		S = 0.25 * Math.PI * Math.pow(D, 2);
		//-----------------------------------------

		//--------------- Mass ---------------
		mDry = structure.get("Dry Mass [kg]").asDouble();
		mSt = mDry - engine.mFuelBef;
		mBef = mDry + engine.mOxBef;
		mAft = mDry - (engine.mFuelBef - engine.mFuelAft);
		//------------------------------------

		//--------------- Center of Gravity ---------------
		lcgDry = structure.get("Dry Length-C.G. from Nosetip [m]").asDouble();
		lcgSt = (lcgDry * mDry - (L - engine.lcgFuel) * engine.mFuelBef) / mSt;
		lcgBef = (lcgDry * mDry + (L - engine.lcgOxBef) * engine.mOxBef) / (mDry + engine.mOxBef);
		lcgAft = (lcgSt * mSt + (L - engine.lcgFuel) * engine.mFuelAft) / (mSt + engine.mFuelAft);
		//-------------------------------------------------

		//--------------- Moment of Inertia ---------------
		// @ Dry
		IjPitchDry = structure.get("Dry Moment of Inertia  Pitch-Axis [kg*m^2]").asDouble();
		IjRollDry = structure.get("Dry Moment of Inertia Roll-Axis [kg*m^2]").asDouble();

		// @ Before Burn
		// Pitch, Yaw Axis
		IjStPitchUnit =
				(IjPitchDry - (engine.IjFuelPitchBef + engine.mFuelBef * Math.pow(lcgDry - (L - engine.lcgFuel), 2))) // 乾燥重心回りの構造Pitch慣性モーメント
				- mSt * Math.pow(lcgDry - lcgSt, 2); // 平行軸の定理で構造重心回りの慣性モーメントに直す
		IjPitchBef =
				(IjStPitchUnit + mSt * Math.pow(lcgBef - lcgSt, 2)) // 全機重心回り構造Pitch慣性モーメント
				+ (engine.IjFuelPitchBef + engine.mFuelBef * Math.pow(lcgBef - (L - engine.lcgFuel), 2)) // 燃焼前全機重心回り燃料Pitch慣性モーメント
				+ (engine.IjOxPitchBef + engine.mOxBef * Math.pow(lcgBef - (L - engine.lcgOxBef), 2)); // 燃焼前全機重心回り酸化剤Pitch慣性モーメント

		// Roll Axis
		IjStRollUnit = IjRollDry - engine.IjFuelRollBef;
		IjRollBef = IjRollDry + engine.IjOxRollBef;

		// @ After Burnout
		IjPitchAft =
				(IjStPitchUnit + mSt * Math.pow(lcgAft - lcgSt, 2))
				+ (engine.IjFuelPitchAft + engine.mFuelAft * Math.pow(lcgAft - (L - engine.lcgFuel), 2));
		IjRollAft = IjStRollUnit + engine.IjFuelRollAft;
		//-------------------------------------------------

		//-------------------- Parachute --------------------
		CdS1 = parachute.get("1st Parachute CdS [m2]").asDouble();
		para2Exist = parachute.get("2nd Parachute Exist").asBoolean();
		if (para2Exist) {
			CdS2 = parachute.get("2nd Parachute CdS [m2]").asDouble();
			alt_para2 = parachute.get("2nd Parachute Opening Altitude [m]").asDouble();
			time_para2 = parachute.get("2nd Timer [s]").asDouble();
			para2Timer = parachute.get("2nd Parachute Timer Mode").asBoolean();
		} else {
			CdS2 = 0.0;
			alt_para2 = 0.0;
			time_para2 = 0.0;
			para2Timer = false;
		}
		//---------------------------------------------------

		dt = spec.get("Solver").get("Time Step [sec]").asDouble();

		//------------------- Launch Config -----------------
		point[0] = launchCond.get("Launch lat").asDouble();
		point[1] = launchCond.get("Launch lon").asDouble();
		point[2] = launchCond.get("Launch height").asDouble();
		site = launchCond.get("Site").asInt();
		lengthLauncherRail = launchCond.get("Launcher Rail Length [m]").asDouble();
		elevationLauncher = launchCond.get("Launch Elevation [deg]").asDouble();
		azimuthLauncher = launchCond.get("Launch Azimuth [deg]").asDouble();
		magneticDec = launchCond.get("Input Magnetic Azimuth [deg]").asDouble();
		existTipOff = launchCond.get("Tip-Off Calculation Exist").asBoolean();
		if (existTipOff) {
			upperLug = structure.get("Upper Launch Lug [m]").asDouble();
			lowerLug = structure.get("Lower Launch Lug [m]").asDouble();
		} else { // launch clearは重心がランチャを抜けたとき
			upperLug = lcgBef;
			lowerLug = lcgBef + 0.01;
		}
		//----------------------------------------------------
	}

	/**
	 * @return 質量時間変化率。負の値で返す
	 * */
	public double mdot(double t) {
		return - engine.getMdotProp(t);
	}

	public double getMass(double t) {
		return mSt + engine.getMassFuel(t) + engine.getMassOx(t);
	}

	public double getLcg(double t) {
		if(t < engine.timeBurnout) {
			double mFuel = engine.getMassFuel(t);
			double mOx = engine.getMassOx(t);
			return (lcgSt * mSt + (L - engine.lcgFuel) * mFuel + (L - engine.getLcgOx(t))* mOx) / (mSt + mFuel + mOx);
		}else {
			return lcgAft;
		}
	}

	public double getLcgProp(double t) {
		if(t < engine.timeBurnout) {
			double massFuel = engine.getMassFuel(t);
			double massOx = engine.getMassOx(t);
			return ((L - engine.lcgFuel) * massFuel + (L - engine.getLcgOx(t)) * massOx) / (massFuel + massOx);
		}else {
			return L - engine.lcgFuel;
		}
	}

	public double getIjPitch(double t) {
		if(t < engine.timeBurnout) {
			return (IjStPitchUnit + mSt * Math.pow(getLcg(t) - lcgSt, 2))
					+ getIjPropPitch(t);
		}else {
			return IjPitchAft;
		}
	}

	public double getIjRoll(double t) {
		if(t < engine.timeBurnout) {
			return IjStRollUnit + getIjPropRoll(t);
		}else {
			return IjRollAft;
		}
	}

	private double getIjPropPitch(double t) {
		if(t < engine.timeBurnout) {
			double lcg = getLcg(t);
			return engine.getIjFuelPitch(t) + engine.getMassFuel(t) * Math.pow((L - engine.lcgFuel) - lcg, 2)
					+ engine.getIjOxPitch(t) + engine.getMassOx(t) * Math.pow((L - engine.getLcgOx(t)) - lcg, 2);
		}else {
			return engine.IjFuelPitchAft + engine.mFuelAft * Math.pow(lcgAft - (L - engine.lcgFuel), 2);
		}
	}

	private double getIjPropRoll(double t) {
		if(t < engine.timeBurnout) {
			return engine.getIjFuelRoll(t) + engine.getIjOxRoll(t);
		}else {
			return engine.IjFuelRollAft;
		}
	}

	public double getIjDotPitch(double t) {
		if(t < engine.timeBurnout) {
			if( t == 0.0) {
				return 0.0;
			}else {
				return (getIjPropPitch(t) - getIjPropPitch(t - dt)) / dt;
			}
		}else {
			return 0.0;
		}
	}
	
	public void outputSpec(String resultDir, String simulationMode) {
		OutputTxt specTxt = null;
		
		try {
			specTxt = new OutputTxt(resultDir + "rocket_param.txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		try {
			
			specTxt.outputLine("----------------------- Structure ----------------------");
			specTxt.outputLine(format("Length: ", 20) + String.format("%.3f [m]", L));
			specTxt.outputLine(format("Diameter: ", 20) + String.format("%.3f [m]", D));
			specTxt.outputLine(format("Area: ", 20) + String.format("%.3f [m^2]", S));
			if(existTipOff) {
				specTxt.outputLine(format("Upper Launch Lug: ", 20) + String.format("%.3f [m]", upperLug));
				specTxt.outputLine(format("Lower Launch Lug: ", 20) + String.format("%.3f [m]", lowerLug));
			}
			specTxt.outputLine("");
			
			specTxt.outputLine("------------------------- Mass -------------------------");
			specTxt.outputLine(format("Dry mass: ", 20) + String.format("%.4f [kg]", mDry));
			specTxt.outputLine(format("Structure mass: ", 20) + String.format("%.4f [kg]", mSt));
			specTxt.outputLine(format("Mass @ before burn: ", 20) + String.format("%.4f [kg]", mBef));
			specTxt.outputLine(format("Mass @ after burn: ", 20) + String.format("%.4f [kg]", mAft));
			specTxt.outputLine("");
			
			specTxt.outputLine("--------------- Center of Gravity (C.G.) ---------------");
			specTxt.outputLine(format("Dry Length-G.G. from Nosetip: ", 40) + String.format("%.4f [m]", lcgDry));
			specTxt.outputLine(format("Structure Length-G.G. from Nosetip: ", 40) + String.format("%.4f [m]", lcgSt));
			specTxt.outputLine(format("Length-G.G. @ before burn from Nosetip: ", 40) + String.format("%.4f [m]", lcgBef));
			specTxt.outputLine(format("Length-G.G. @ after burn from Nosetip: ", 40) + String.format("%.4f [m]", lcgAft));
			specTxt.outputLine(format("Propellant Length-G.G. @ before burn from Nosetip: ", 52) + String.format("%.4f [m]", getLcgProp(0.0)));
			specTxt.outputLine("");
			
			specTxt.outputLine("---------------- Moment of Inertia (MOI) ---------------");
			specTxt.outputLine(format("Dry MOI Roll-Axis: ", 45) + String.format("%.4f [kg*m^2]", IjRollDry));
			specTxt.outputLine(format("Dry MOI Pitch-Axis: ", 45) + String.format("%.4f [kg*m^2]", IjPitchDry));
			specTxt.outputLine(format("MOI @ before burn Roll-Axis: ", 45) + String.format("%.4f [kg*m^2]", IjRollBef));
			specTxt.outputLine(format("MOI @ before burn Pitch-Axis: ", 45) + String.format("%.4f [kg*m^2]", IjPitchBef));
			specTxt.outputLine(format("MOI @ after burn Roll-Axis:", 45) + String.format("%.4f [kg*m^2]", IjRollAft));
			specTxt.outputLine(format("MOI @ after burn Pitch-Axis: ", 45) + String.format("%.4f [kg*m^2]", IjPitchAft));
			specTxt.outputLine(format("Propellant MOI @ before burn Roll-Axis: ", 45) + String.format("%.4f [kg*m^2]", getIjPropRoll(0.0)));
			specTxt.outputLine(format("Propellant MOI @ before burn Pitch-Axis: ", 45) + String.format("%.4f [kg*m^2]", getIjPropPitch(0.0)));
			specTxt.outputLine("");
			
			specTxt.outputLine("--------------------- Aero Parameter -------------------");
			if(!aero.getLcpFileExist()) {
				specTxt.outputLine(format("Constant Length-C.P. from Nosetip: ", 40) + String.format("%.3f [m]", aero.Lcp(0.0)));
			}
			if(!aero.getCdFileExist()) {
				specTxt.outputLine(format("Constant Cd: ", 40) + String.format("%.3f [-]", aero.Cd(0.0)));
			}
			if(!aero.getCNaFileExist()) {
				specTxt.outputLine(format("Constant CNa: ", 40) + String.format("%.3f [1/rad]", aero.CNa(0.0)));
			}
			specTxt.outputLine(format("Constant Clp: ", 40) + String.format("%.3f [1/rad]", aero.Clp));
			specTxt.outputLine(format("Constant Cmq: ", 40) + String.format("%.3f [1/rad]", aero.Cmq));
			specTxt.outputLine(format("Constant Cnr: ", 40) + String.format("%.3f [1/rad]", aero.Cnr));
			specTxt.outputLine("");
			
			specTxt.outputLine("----------------------- Parachute ----------------------");
			specTxt.outputLine(format("1st Parachute CdS: ", 35) + String.format("%.3f [m^2]", CdS1));
			if(para2Exist) {
				specTxt.outputLine(format("2nd Parachute CdS: ", 35) + String.format("%.3f [m^2]", CdS2));
				if(para2Timer) {
					specTxt.outputLine(format("2nd Timer: ", 35) + String.format("%.1f [s]", time_para2));
				}else {
					specTxt.outputLine(format("2nd Parachute Opening Altitude: ", 35) + String.format("%.3f [m]", alt_para2));
				}
			}
			specTxt.outputLine("");
			
			specTxt.outputLine("-------------------- Launch Condition ------------------");
			specTxt.outputLine(format("Launcher Rail Length: ", 30) + String.format("%.1f [m]", lengthLauncherRail));
			specTxt.outputLine(format("Launch Elevation: ", 30) + String.format("%.1f [deg]", elevationLauncher));
			specTxt.outputLine(format("Launch Magnetic Azimuth: ", 30) + String.format("%.1f [deg]", azimuthLauncher));
			specTxt.outputLine(format("Launch True Azimuth: ", 30) + String.format("%.1f [deg]", azimuthLauncher - magneticDec));
			specTxt.outputLine("");
			
			specTxt.outputLine("--------------------- Wind Condition -------------------");
			switch (simulationMode) {
			
			case "single":
				if(wind instanceof Original) {
					specTxt.outputLine(format("Wind Model: ", 20) + "Original");
					specTxt.outputLine(format("Wind File: ", 20) + wind.getFilePath());
				}else if(wind instanceof Power) {
					specTxt.outputLine(format("Wind Model: ", 30) + "Power Law");
					specTxt.outputLine(format("Wind Speed: ", 30) + String.format("%.1f [m/s]", wind.getRefWindSpeed()));
					specTxt.outputLine(format("Wind Azimuth: ", 30) + String.format("%.1f [deg]", wind.getRefWindAzimuth()));
					specTxt.outputLine(format("Wind Power Law Coefficient: ", 30) + String.format("%.1f [-]", wind.getExponent()));
				}else if(wind instanceof Constant) {
					specTxt.outputLine(format("Wind Model: ", 20) + "Constant");
					specTxt.outputLine(format("Wind Speed: ", 20) + String.format("%.1f [m/s]", wind.getRefWindSpeed()));
					specTxt.outputLine(format("Wind Azimuth: ", 20) + String.format("%.1f [deg]", wind.getRefWindAzimuth()));
				}
				break;

			case "multi":
				if(wind instanceof Power) {
					specTxt.outputLine(format("Wind Model: ", 30) + "Power Law");
					specTxt.outputLine(format("Wind Power Law Coefficient: ", 30) + String.format("%.1f [-]", wind.getExponent()));
				}else if(wind instanceof Constant) {
					specTxt.outputLine(format("Wind Model: ", 20) + "Constant");
				}
				break;
			}
			specTxt.outputLine("");
			
		} catch (IOException e) {
			 throw new RuntimeException(e);
		}
		
		try {
			specTxt.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
    // 文字列の整形用の関数
	private static String format(String target, int length){
        int byteDiff = (getByteLength(target, Charset.forName("UTF-8"))-target.length())/2;
        return String.format("%-"+(length-byteDiff)+"s", target);
    }

    private static int getByteLength(String string, Charset charset) {
        return string.getBytes(charset).length;
    }

	public double getIjDotRoll(double t) {
		if(t < engine.timeBurnout) {
			if(t == 0.0) {
				return 0.0;
			}else {
				return (getIjPropRoll(t) - getIjPropRoll(t - dt)) / dt;
			}
		}else {
			return 0.0;
		}
	}

	public double[] getPoint() {
		return point;
	}

	public int getSite() {
		return site;
	}


}
