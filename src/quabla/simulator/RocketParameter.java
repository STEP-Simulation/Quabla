package quabla.simulator;

import quabla.parameter.InputParam;
import quabla.simulator.numerical_analysis.Interpolation;

/**
 * このクラスはhogehoge
 *
 * */
public class RocketParameter {

	public double l, d, S, upperLug, lowerLug;
	public double lcgBef, lcgAft, lcgPropBef;
	public double dth, eps, Ath, Ae;
	/** Engine nozzle diameter [m] */
	public double de;
	/** Mass */
	double m0, mAft, mProp0, massOxBef;
	private double lengthTank;
	/** Moment of Inertia */
	double IjPitchBef, IjPitchAft, IjRollBef, IjRollAft;
	double IjDotPitch, IjDotRoll;
	double IjPropPitchBef, IjPropRollBef;
	private double IjFuelRollBef, IjFuelPitchBef;
	public double CdS1, CdS2;
	public boolean para2Exist;
	public double alt_para2;
	/** Simulation time step */
	public double dt;
	Interpolation thrustAnaly;
	public double timeBurnout;
	double lengthLauncherRail;
	boolean existTipOff;
	private double IjStPitch, IjStRoll;
	private double lMotor, dTank, lFuel;
	private double massFuelBef, massFuelAft;
	private double diffMassFuel;
	private double dFuelInBef,dFuelInAft ,dFuelOut;
	private double lcgFuel;

	public RocketParameter(InputParam spec) {

		//// Geometory //////////////////////////////////////////////////
		l = spec.l;
		d = spec.d;
		S = 0.25 * Math.PI * Math.pow(d, 2);
		existTipOff = spec.tip_off_exist;
		if (existTipOff) {
			upperLug = spec.upper_lug;
			lowerLug = spec.lower_lug;
		} else { // launch clearは重心がランチャを抜けたとき
			upperLug = spec.lcg0;
			lowerLug = spec.lcg0 + 0.01;
		}

		// Engine configuration
		dth = spec.dth * Math.pow(10, -3);// [mm] => [m]
		eps = spec.eps; //
		Ath = 0.25 * Math.PI * Math.pow(dth, 2);
		Ae = Ath * eps;
		de = Math.sqrt(Ae * 4 / Math.PI);

		// fuel(grain)
		dFuelInBef = spec.diameterFuelPort;
		dFuelInAft = spec.diameterFuelPort;
		dFuelOut = spec.diameterFuelOut;
		lFuel = spec.lengthFuel;

		// oxidizer tank
		lengthTank = spec.lengthTank;
		dTank = spec.diameterTank;
		/////////////////////////////////////////////////////////////////

		///// Center of Gravity /////////////////////////////////////////
		lcgBef = spec.lcg0;
		lcgAft = spec.lcgf;
		lcgPropBef = spec.lcgp;
		lcgFuel = l - (lMotor - 0.5 * lFuel);
		//TODO l_cgProp の時間変化
		lMotor = spec.lenghtMotor;
		/////////////////////////////////////////////////////////////////

		//// Mass ///////////////////////////////////////////////////////
		m0 = spec.m0;
		//massOxBef = spec.massOxInit;
		massOxBef = (spec.volTank * Math.pow(10, -6)) * spec.densityOxidizer;
		mAft = spec.mf;
		mProp0 = m0 - mAft;
		massFuelBef = spec.massFuelBef;
		massFuelAft = spec.massFuelAft;
		diffMassFuel = spec.massFuelBef - massFuelAft;
		/////////////////////////////////////////////////////////////////

		///// Moment of Inertia /////////////////////////////////////////
		IjPitchBef = spec.Ij_pitch_0;
		IjPitchAft = spec.Ij_pitch_f;
		IjRollBef = spec.Ij_roll_0;
		IjRollAft = spec.Ij_roll_f;
		IjPropPitchBef = IjPitchBef - IjPitchAft;
		IjPropRollBef = IjRollBef - IjRollAft;

		// Structure's Momtnt of Inertia
		IjStRoll = spec.IjStRoll;
		IjStPitch = spec.IjStructPitch;
		/////////////////////////////////////////////////////////////////

		//// Parachute //////////////////////////////////////////////////
		CdS1 = spec.CdS1;
		para2Exist = spec.para2_exist;
		if (para2Exist) {
			CdS2 = spec.CdS2;
			alt_para2 = spec.alt_para2;
		} else {
			CdS2 = 0.0;
			alt_para2 = 0.0;
		}
		/////////////////////////////////////////////////////////////////

		dt = spec.dt;

		//// Thrust /////////////////////////////////////////////////////
		double[][] thrust_data = GetCsv.get2ColumnArray(spec.thrustcurve);
		double[] time_array = new double[thrust_data.length];
		double[] thrust_array = new double[thrust_data.length];
		for (int i = 0; i < thrust_data.length; i++) {
			time_array[i] = thrust_data[i][0];
			thrust_array[i] = thrust_data[i][1];
		}
		thrustAnaly = new Interpolation(time_array, thrust_array);
		timeBurnout = time_array[thrust_data.length - 1];
		/////////////////////////////////////////////////////////////////

		lengthLauncherRail = spec.length_Launcher;
	}

	public double mdot(double t) {
		double mdot;

		if (t < timeBurnout) {
			mdot = (mAft - m0) / timeBurnout;// mdot < 0
		} else {
			mdot = 0;
		}

		return mdot;
	}

	public double[] Ij_dot(double t) {
		double[] Ij_dot = new double[3];

		if (t < timeBurnout) {
			Ij_dot[0] = (IjRollAft - IjRollBef) / timeBurnout;
			Ij_dot[1] = (IjPitchAft - IjPitchBef) / timeBurnout;
			Ij_dot[2] = Ij_dot[1];
		} else {
			for (int i = 0; i < 3; i++) {
				Ij_dot[i] = 0.0;
			}
		}

		return Ij_dot;
	}

	// TODO 修正
	private double getMassFuel(double t) {
		return diffMassFuel * t / timeBurnout;
	}

	/**
	 * calculate oxidizer's mass
	 * @param t 時間 [sec]
	 * @return massOx
	 * */
	private double getMassOxidizer(double t)	{
		if(t < timeBurnout) {
			return massOxBef * t / timeBurnout;
		}else {
			return 0.0;
		}
	}

	public double getMass(double t) {
		if (t < timeBurnout) {
			return m0 + (mAft - m0) * t / timeBurnout;
		} else {
			return mAft;
		}
	}

	//TODO 燃焼前後での条件分岐
	private double getIjPropPitch(double t) {
		double IjPropPitch;
		double lcg = getLcg(t);

		// fuel
		/* 燃料(グレイン)の内径 **/
		double dFuelIn = dFuelInBef + (dFuelInAft - dFuelInBef) * t / timeBurnout;
		double massFuel = getMassFuel(t);

		// 中空円筒の慣性モーメント
		// Iyy = m * ((din^2 + dout^2) / 16 + Lf / 12)
		double IjFuelPitch = massFuel * ((Math.pow(dFuelOut, 2) + Math.pow(dFuelIn, 2)) / 16.0 + lFuel / 12.0); //fuelの重心回りの慣性モーメント
		IjFuelPitch += massFuel * Math.pow(lcgFuel - lcg, 2); // parallel axis thorem

		// oxidizer
		double massOx = getMassOxidizer(t);
		double lengthOx = lengthTank * t / timeBurnout;
		/*oxidizerの慣性モーメント
		 * スロッシングの影響を無視し,液体酸化剤を一様円柱と仮定して計算 **/
		double IjOxPitch = massOx * (Math.pow(dTank, 2) / 16.0 + Math.pow(lengthOx, 2) / 12.0); //oxidizer自身の重心回りの慣性モーメント
		IjOxPitch += massOx * Math.pow(getLcgOx(t) - lcg, 2); // parallel axis theorem

		IjPropPitch = IjOxPitch + IjFuelPitch;

		return IjPropPitch;
	}

	public double Ij_roll(double t) {
		double Ij_roll;

		if (t < timeBurnout) {
			Ij_roll = IjRollBef + t * (IjRollAft - IjRollBef) / timeBurnout;
		} else {
			Ij_roll = IjRollAft;
		}

		return Ij_roll;
	}

	public double Ij_pitch(double t) {
		double Ij_pitch;

		if (t < timeBurnout) {
			Ij_pitch = IjPitchBef + t * (IjPitchAft - IjPitchBef) / timeBurnout;
		} else {
			Ij_pitch = IjPitchAft;
		}

		return Ij_pitch;
	}

	public double getLcg(double t) {
		double Lcg;

		if (t < timeBurnout) {
			Lcg = lcgBef + (lcgAft - lcgBef) * t / timeBurnout;
		} else {
			Lcg = lcgAft;
		}

		return Lcg;
	}

	//実装中
	public double getLcgProp(double t) {
		if(t < timeBurnout) {
			return getMassFuel(t) * lcgFuel + getMassOxidizer(t) * getLcgOx(t);
		}else {
			return 0.0;
		}
	}

	/**
	 * claculate oxidizer's cnter of gravity
	 * @param t [sec]
	 * @return lcgOx 機体先端からoxidizer重心までの距離
	 * */
	public double getLcgOx(double t) {
		double lengthOx = lengthTank * (t / timeBurnout); //oxidizerを円柱としたときの円柱の長さ
		return l - (lMotor + lengthOx);
	}

	public double thrust(double t) {
		double thrust;

		if (t < timeBurnout) {
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
