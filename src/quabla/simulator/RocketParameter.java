package quabla.simulator;

import quabla.parameter.InputParam;
import quabla.simulator.numerical_analysis.Interpolation;

/**
 * このクラスはhogehoge
 *
 * */
public class RocketParameter {

	public final double L, D, S, upperLug, lowerLug;
	public final double lcgBef;
	private double lcgAft;
	private double dth, eps, Ath;
	public final double Ae;
	/** Engine Nozzle Outlet Diameter [m] */
	public final double de;
	/** Mass */
	private double mBef, mAft, mSt, mOxBef, mDry;
	/** Length of Oxidizer Tank */
	private double lTank;
	/** Moment of Inertia */
	private double IjPitchBef, IjPitchAft, IjRollBef, IjRollAft;
	private double IjFuelRollBef, IjFuelRollAft, IjFuelPitchBef, IjFuelPitchAft;
	/** 燃料の '燃料の重心回り' の慣性モーメント */
	private double IjFuelPitchBefUnit;
	private double IjFuelPitchDry;
	private double IjOxPitchBef, IjOxRollBef;
	private final double IjPitchDry, IjRollDry;
	public final double CdS1, CdS2;
	public final boolean para2Exist;
	public final double alt_para2;
	/** Simulation time step */
	public final double dt;
	Interpolation thrustAnaly;
	public final double timeBurnout, timeActuate;
	final double lengthLauncherRail;
	boolean existTipOff;
	/** Structure Moment of Inertia
	 * '構造重心回り' の慣性モーメント*/
	private double IjStPitchUnit, IjStRollUnit;
	private double lMotor, dTank, lFuel;
	private double mFuelBef, mFuelAft;
	/** Diameter of Fuel */
	private double dFuelInBef,dFuelInAft ,dFuelOut;
	private double lcgDry, lcgSt,lcgFuel, lcgOxBef, lcgOxAft;

	public RocketParameter(InputParam spec) {

		//-------------------- Geometory --------------------
		L = spec.l;
		D = spec.d;
		S = 0.25 * Math.PI * Math.pow(D, 2);

		// Engine configuration
		dth = spec.dth * Math.pow(10, -3);// [mm] => [m]
		eps = spec.eps;
		Ath = 0.25 * Math.PI * Math.pow(dth, 2);
		Ae = Ath * eps;
		de = Math.sqrt(Ae * 4 / Math.PI);

		// fuel(grain)
		dFuelInBef = spec.diameterFuelPort * Math.pow(10, -3);
		dFuelOut = spec.diameterFuelOut * Math.pow(10, -3);
		lFuel = spec.lengthFuel;
		lMotor = spec.lenghtMotor;

		// oxidizer tank
		lTank = spec.lengthTank;
		dTank = spec.diameterTank * Math.pow(10, -3);
		//----------------------------------------------------

		//-------------------- Mass --------------------
		mOxBef = (spec.volTank * Math.pow(10, -6)) * spec.densityOxidizer;
		mDry = spec.mDry;
		mFuelBef = spec.massFuelBef;
		mFuelAft = spec.massFuelAft;
		//燃料は密度分布が一様であると仮定して,燃焼前後の重量比を用いて算出
		dFuelInAft = Math.sqrt((1 - mFuelAft / mFuelBef) * Math.pow(dFuelOut, 2) + (mFuelAft / mFuelBef) * Math.pow(dFuelInBef, 2));
		mSt = mDry - mFuelBef;

		mBef = mDry + mOxBef;
		mAft = mDry - (mFuelBef - mFuelAft);
		//----------------------------------------------

		//-------------------- Center of Gravity --------------------
		lcgDry = spec.lcgDry;
		//燃料は半径方向にのみ一様に減少するとし,重心が機軸方向に移動しない仮定
		lcgFuel = L - (lMotor - 0.5 * lFuel);
		lcgOxBef = L - (lMotor + 0.5 * lTank);
		lcgOxAft = L - lMotor;
		lcgSt = (lcgDry * mDry - lcgFuel * mFuelBef) / mSt;

		lcgBef = (lcgDry * mDry + lcgOxBef * mOxBef) / (mDry + mOxBef);
		lcgAft = (lcgSt * mSt + lcgFuel * mFuelAft) / (mSt + mFuelAft);
		//------------------------------------------------------------

		//-------------------- Moment of Inertia --------------------
		// Moment of Inertia @ Dry
		IjPitchDry = spec.IjPitchDry;
		IjRollDry = spec.IjRollDry;

		// Moment of Inertia @ Before Flight
		IjFuelPitchBefUnit = mFuelBef * ((Math.pow(dFuelInBef, 2) + Math.pow(dFuelOut, 2)) / 16.0 + Math.pow(lFuel, 2) / 12.0); //Fuelの 'fuel重心回り' のPitch慣性モーメント
		IjFuelPitchDry = IjFuelPitchBefUnit + mFuelBef * Math.pow(lcgDry - lcgFuel, 2); //乾燥時のFuelの '全機重心回り' のPitch慣性モーメント
		IjFuelPitchBef = IjFuelPitchBefUnit + mFuelBef * Math.pow(lcgBef - lcgFuel, 2);
		IjStPitchUnit = (IjPitchDry - IjFuelPitchDry) - mSt * Math.pow(lcgDry - lcgSt, 2);
		IjOxPitchBef = mOxBef * (Math.pow(dTank, 2) / 16.0 + Math.pow(lTank, 2) / 12.0) + mOxBef * Math.pow(lcgBef - lcgOxBef, 2);
		IjPitchBef =  (IjStPitchUnit + mSt * Math.pow(lcgBef - lcgSt, 2)) + IjFuelPitchBef + IjOxPitchBef;

		IjFuelRollBef = mFuelBef * (Math.pow(dFuelInBef, 2) + Math.pow(dFuelOut, 2)) / 8.0;
		IjStRollUnit = IjRollDry - IjFuelRollBef;
		IjOxRollBef = mOxBef * Math.pow(dTank, 2) / 8.0;
		IjRollBef = IjStRollUnit + IjFuelRollBef + IjOxRollBef;

		// Moment of Inertia @ After Flight
		IjFuelPitchAft = mFuelAft * ((Math.pow(dFuelInAft, 2) + Math.pow(dFuelOut, 2)) / 16.0 + Math.pow(lFuel, 2) / 12.0);
		IjFuelPitchAft += mFuelAft * Math.pow(lcgAft - lcgFuel, 2);
		IjPitchAft = (IjStPitchUnit + mSt * Math.pow(lcgAft - lcgSt, 2)) + IjFuelPitchAft;

		IjFuelRollAft = mFuelAft * ((Math.pow(dFuelInAft, 2) + Math.pow(dFuelOut, 2))) / 8.0;
		IjRollAft = IjStRollUnit + IjFuelRollAft;
		//------------------------------------------------------------

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

		//-------------------- Thrust --------------------
		/* 1st Column : Time [s]
		 * 2nd Column : Thrust [N] **/
		double[][] thrust_data = GetCsv.get2ColumnArray(spec.thrustcurve);
		double[] time_array = new double[thrust_data.length];
		double[] thrust_array = new double[thrust_data.length];
		for (int i = 0; i < thrust_data.length; i++) {
			time_array[i] = thrust_data[i][0];
			thrust_array[i] = thrust_data[i][1];
		}
		thrustAnaly = new Interpolation(time_array, thrust_array);
		timeActuate = time_array[thrust_data.length - 1];
		timeBurnout = spec.timeBurnout;
		//-------------------------------------------------

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

	public double mdot(double t) {
		double mdot;

		if (t < timeBurnout) {
			mdot = (mAft - mBef) / timeBurnout;// mdot < 0
		} else {
			mdot = 0;
		}

		return mdot;
	}

	public double getIjDotPitch(double t) {
		if(t < timeBurnout) {
			if(t == 0.0) {
				return 0.0;
			}else {
				return (getIjPropPitch(t) - getIjPropPitch(t - dt)) / dt;
			}
		}else {
			return 0.0;
		}
	}

	public double getIjDotRoll(double t) {
		if(t < timeBurnout) {
			if(t == 0.0) {
				return 0.0;
			}else {
				return (getIjPropRoll(t) - getIjPropRoll(t - dt)) / dt;
			}
		}else {
			return 0.0;
		}
	}

	// TODO 修正
	private double getMassFuel(double t) {
		if(t < timeBurnout) {
		return mFuelBef + (mFuelAft - mFuelBef) * (t / timeBurnout);
		}else {
			return mFuelAft;
		}
	}

	/**
	 * calculate oxidizer's mass
	 * @param t 時間 [sec]
	 * @return massOx
	 * */
	private double getMassOxidizer(double t)	{
		if(t < timeBurnout) {
			return mOxBef *(1 -  t / timeBurnout);
		}else {
			return 0.0;
		}
	}

	public double getMass(double t) {
		if (t < timeBurnout) {
			return mBef + (mAft - mBef) * t / timeBurnout;
		} else {
			return mAft;
		}
	}

	public double getMassProp(double t) {
		if(t < timeBurnout) {
			return getMassFuel(t) + getMassOxidizer(t);
		}else {
			return mFuelAft;
		}
	}

	private double getIjPropPitch(double t) {
		if(t < timeBurnout) {
			double lcg = getLcg(t);

			//----- fuel -------------------------------------
			double massFuel = getMassFuel(t);
			// 中空円筒の慣性モーメント
			// Iyy = m * ((din^2 + dout^2) / 16 + Lf / 12)
			double IjFuelPitch = massFuel * ((Math.pow(dFuelOut, 2) + Math.pow(getDiamFuelIn(t), 2)) / 16.0 + Math.pow(lFuel, 2) / 12.0); //fuelの重心回りの慣性モーメント
			IjFuelPitch += massFuel * Math.pow(lcgFuel - lcg, 2); // parallel axis thorem

			//----- oxidizer ----------------------------------
			double massOx = getMassOxidizer(t);
			/* oxidizerの慣性モーメント
			 * スロッシングの影響を無視し,液体酸化剤を一様円柱と仮定して計算 **/
			double IjOxPitch = massOx * (Math.pow(dTank, 2) / 16.0 + Math.pow(getLengthOx(t), 2) / 12.0); //oxidizer自身の重心回りの慣性モーメント
			IjOxPitch += massOx * Math.pow(getLcgOx(t) - lcg, 2); // parallel axis theorem

			return IjOxPitch + IjFuelPitch;
		}else {
			return IjFuelPitchAft;
		}
	}

	private double getIjPropRoll(double t) {
		if(t < timeBurnout) {
			double IjFuelRoll = getMassFuel(t) * (Math.pow(getDiamFuelIn(t), 2) + Math.pow(dFuelOut, 2)) / 8.0;
			double IjOxRoll = getMassOxidizer(t) * Math.pow(dTank, 2) / 8.0;
			return IjFuelRoll + IjOxRoll;
		}else {
			return IjFuelRollAft;
		}
	}

	public double getIjPitch(double t) {
		if(t < timeBurnout) {
			return (IjStPitchUnit + mSt * Math.pow(getLcg(t) - lcgSt, 2)) + getIjPropPitch(t);
		}else {
			return IjPitchAft;
		}
	}

	public double getIjRoll(double t) {
		if(t < timeBurnout) {
			return IjStRollUnit + getIjPropRoll(t);
		}else {
			return IjRollAft;
		}
	}

	//TODO 変更
	public double getLcg(double t) {
	//	double Lcg;

		if (t < timeBurnout) {
			return (lcgSt * mSt + getLcgProp(t) * getMassProp(t)) / getMass(t);
		} else {
			return lcgAft;
		}
	}

	private double getLengthOx(double t) {
		if(t < timeBurnout) {
			return lTank * (t / timeBurnout);
		}else {
			return 0.0;
		}
	}

	/**
	 * @param t time [s]
	 * @return 機体先端から酸化剤重心までの距離 [m]
	 * */
	private double getLcgOx(double t) {
		if(t < timeBurnout) {
			return L - (lMotor + 0.5 * getLengthOx(t));
		}else {
			return lcgOxAft;
		}
	}

	private double getDiamFuelIn(double t) {
		if(t < timeBurnout) {
			return dFuelInBef + (dFuelInAft - dFuelInBef) * (t / timeBurnout);
		}else {
			return dFuelInAft;
		}
	}

	public double getLcgProp(double t) {
		if(t < timeBurnout) {
			double massFuel = getMassFuel(t);
			double massOx = getMassOxidizer(t);
			return (lcgFuel * massFuel + getLcgOx(t) * massOx) / (massFuel + massOx);
		}else {
			return lcgFuel;
		}
	}

	public double thrust(double t) {
		double thrust;

		if (t < timeActuate) {
			thrust = thrustAnaly.linearInterp1column(t);
		} else {
			thrust = 0.0;
		}

		return thrust;
	}

	public double getTimeStep() {
		return dt;
	}

}
