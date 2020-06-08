package quabla.simulator.rocket;

import quabla.parameter.InputParam;

public class Rocket {

	// TODO 離陸前，燃焼後の時の重量，慣性モーメントをcsvに出力

	public final Engine engine;
	public final AeroParameter aero;
	public final Atmosphere atm;
	public final Wind wind;

	public final double
	L,
	D,
	S,
	upperLug,
	lowerLug;
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
	public final boolean para2Exist;
	public final double alt_para2;
	public final double dt;
	public final boolean existTipOff;
	public final double lengthLauncherRail;

	public Rocket(InputParam spec) {
		engine = new Engine(spec);
		aero = new AeroParameter(spec);
		atm = new Atmosphere(spec.temperture0);
		wind = new Wind(spec);

		//--------------- Geometory ---------------
		L = spec.l;
		D = spec.d;
		S = 0.25 * Math.PI * Math.pow(D, 2);
		//-----------------------------------------

		//--------------- Mass ---------------
		mDry = spec.mDry;
		mSt = mDry - engine.mFuelBef;
		mBef = mDry + engine.mOxBef;
		mAft = mDry - (engine.mFuelBef - engine.mFuelAft);
		//------------------------------------

		//--------------- Center of Gravity ---------------
		lcgDry = spec.lcgDry;
		lcgSt = (lcgDry * mDry - (L - engine.lcgFuel) * engine.mFuelBef) / mSt;
		lcgBef = (lcgDry * mDry + (L - engine.lcgOxBef) * engine.mOxBef) / (mDry + engine.mOxBef);
		lcgAft = (lcgSt * mSt + (L - engine.lcgFuel) * engine.mFuelAft) / (mSt + engine.mFuelAft);
		//-------------------------------------------------

		//--------------- Moment of Inertia ---------------
		// @ Dry
		IjPitchDry = spec.IjPitchDry;
		IjRollDry = spec.IjRollDry;

		// @ Before Burn
		// Pitch, Yaw Axsis
		IjStPitchUnit =
				(IjPitchDry - (engine.IjFuelPitchBef + engine.mFuelBef * Math.pow(lcgDry - (L - engine.lcgFuel), 2))) // 乾燥重心回りの構造Pitch慣性モーメント
				- mSt * Math.pow(lcgDry - lcgSt, 2); // 平行軸の定理で構造重心回りの慣性モーメントに直す
		IjPitchBef =
				(IjStPitchUnit + mSt * Math.pow(lcgBef - lcgSt, 2)) // 全機重心回り構造Pitch慣性モーメント
				+ (engine.IjFuelPitchBef + engine.mFuelBef * Math.pow(lcgBef - (L - engine.lcgFuel), 2)) // 燃焼前全機重心回り燃料Pitch慣性モーメント
				+ (engine.IjOxPitchBef + engine.mOxBef * Math.pow(lcgBef - (L - engine.lcgOxBef), 2)); // 燃焼前全機重心回り酸化剤Pitch慣性モーメント

		// Roll Axsis
		IjStRollUnit = IjRollDry - engine.IjFuelRollBef;
		IjRollBef = IjRollDry + engine.IjOxRollBef;

		// @ After Burnout
		IjPitchAft =
				(IjStPitchUnit + mSt * Math.pow(lcgAft - lcgSt, 2))
				+ (engine.IjFuelPitchAft + engine.mFuelAft * Math.pow(lcgAft - (L - engine.lcgFuel), 2));
		IjRollAft = IjStRollUnit + engine.IjFuelRollAft;
		//-------------------------------------------------

		//-------------------- Parachute --------------------
		CdS1 = spec.CdS1;
		para2Exist = spec.para2_exist;
		if (para2Exist) {
			CdS2 = spec.CdS2;
			alt_para2 = spec.alt_para2;
		} else {
			CdS2 = 0.0;
			alt_para2 = 0.0;
		}
		//---------------------------------------------------

		dt = spec.dt;

		//------------------- Launch Config -----------------
		lengthLauncherRail = spec.length_Launcher;
		existTipOff = spec.tip_off_exist;
		if (existTipOff) {
			upperLug = spec.upper_lug;
			lowerLug = spec.lower_lug;
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

}
