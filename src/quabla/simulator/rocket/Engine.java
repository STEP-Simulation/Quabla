package quabla.simulator.rocket;

import com.fasterxml.jackson.databind.JsonNode;

import quabla.simulator.GetCsv;
import quabla.simulator.numerical_analysis.Interpolation;

public class Engine {

	public final double
	dth, // Diameter of throat
	eps, // Expansion ratio
	Ath, // Area of throat
	Ae, // Area of outlet
	de; // diameter of throat
	private double
	dFuelInBef,
	dFuelInAft,
	dFuelOut,
	lFuel;
	private double
	lTank,
	dTank,
	distanceTank;
	public final  double
	mOxBef,
	mFuelBef,
	mFuelAft;
	private double mDotFuel;
	// エンジン関連の重心は全部エンジン後端(ノズル側)からの長さ
	public final double
	lcgFuel,
	lcgOxBef,
	lcgOxAft;
	/** 燃料重心回りの慣性モーメント */
	public final  double
	IjFuelPitchBef,
	IjFuelRollBef,
	IjFuelPitchAft,
	IjFuelRollAft;
	public final double
	IjOxPitchBef,
	IjOxRollBef;
	private Interpolation
	thrustAnaly,
	mDotPropAnaly,
	// mFuelAnaly,
	mOxAnaly,
	lgcOxAnaly;
	public final double
	timeBurnout,
	timeActuate;
	private double IspAve;

	public Engine(JsonNode engine) {

		// Specific thrust
		IspAve = engine.get("Isp [sec]").asDouble();
		timeBurnout = engine.get("Burn Time [sec]").asDouble();

		mOxBef = (engine.get("Tank Volume [cc]").asDouble() * Math.pow(10, -6)) * engine.get("Oxidizer Density [kg/m^3]").asDouble();
		mFuelBef = engine.get("Fuel Mass Before [kg]").asDouble();
		mFuelAft = engine.get("Fuel Mass After [kg]").asDouble();
		mDotFuel = (mFuelBef - mFuelAft) / timeBurnout;

		//-------------------- Thrust --------------------
		/* 1st Column : Time [s]
		 *  2nd Column : Thrust [N] **/
		double[][] thrust_data = GetCsv.get2ColumnArray(engine.get("Thrust Curve").asText());
		double[] time_array = new double[thrust_data.length];
		double[] thrust_array = new double[thrust_data.length];
		double[] mDotPropLog = new double[thrust_data.length];
		double[] mDotFuelLog = new double[thrust_data.length];
		double[] mDotOxLog = new double[thrust_data.length];
		for (int i = 0; i < thrust_data.length; i++) {
			time_array[i] = thrust_data[i][0];
			thrust_array[i] = thrust_data[i][1];
			mDotPropLog[i] = thrust_array[i] / (IspAve * 9.80665);
			if(time_array[i] < timeBurnout) {
				mDotFuelLog[i] = mDotFuel;
				// mDotPropLog[i] = thrust_array[i] / (IspAve * 9.80665);
				// mDotOxLog[i] = mDotPropLog[i] - mDotFuel;
				// mDotOxLog[i] = Math.max(0.0, mDotPropLog[i] - mDotFuel);
			}else {
				mDotFuelLog[i] = 0.0;
				// mDotPropLog[i] = 0.0;
				// mDotOxLog[i] = 0.0;
			}
			mDotOxLog[i] = Math.max(0.0, mDotPropLog[i] - mDotFuelLog[i]);
		}
		thrustAnaly = new Interpolation(time_array, thrust_array);
		mDotPropAnaly = new Interpolation(time_array, mDotPropLog);
		timeActuate = time_array[thrust_data.length - 1];
		double[] mOxLog = new double[thrust_data.length];
		mOxLog[0] = mOxBef;
		Interpolation mDotOxAnaly = new Interpolation(time_array, mDotOxLog);
		for (int i = 0; i < time_array.length - 1; i++) {
			double t = time_array[i];
			double dt = time_array[i+1] - time_array[i];
			double k1 = - mDotOxAnaly.linearInterp1column(t);
			double k2 = - mDotOxAnaly.linearInterp1column(t + 0.5 * dt);
			double k3 = - mDotOxAnaly.linearInterp1column(t + 0.5 * dt);
			double k4 = - mDotOxAnaly.linearInterp1column(t + dt);
			mOxLog[i + 1] = Math.max(0.0, mOxLog[i] + (dt / 6.0) * (k1 + 2.0 * k2 + 2.0 * k3 + k4));
		}
		mOxAnaly = new Interpolation(time_array, mOxLog);

		//--------------- Length, Diameter ---------------
		dth = engine.get("Nozzle Throat Diameter [mm]").asDouble() * Math.pow(10, -3);// [mm] => [m]
		eps = engine.get("Nozzle Expansion Ratio").asDouble();
		Ath = 0.25 * Math.PI * Math.pow(dth, 2);
		Ae = Ath * eps;
		de = Math.sqrt(Ae * 4 / Math.PI);

		// Fuel(grain)
		dFuelInBef = engine.get("Fuel Inside Diameter [mm]").asDouble() * Math.pow(10, -3);
		//燃料は密度分布が一様であると仮定して,燃焼前後の重量比を用いて算出
		dFuelInAft = Math.sqrt((1 - mFuelAft / mFuelBef) * Math.pow(dFuelOut, 2) + (mFuelAft / mFuelBef) * Math.pow(dFuelInBef, 2));

		dFuelOut = engine.get("Fuel Outside Diameter [mm]").asDouble() * Math.pow(10, -3);
		lFuel = engine.get("Fuel Length [m]").asDouble();

		// Oxidizer tank
		lTank = engine.get("Tank Length [m]").asDouble();
		dTank = engine.get("Tank Diameter [mm]").asDouble() * Math.pow(10, -3);
		distanceTank = engine.get("Length Tank-End from End [m]").asDouble();
		//------------------------------------------------

		//--------------- Center of Gravity ---------------
		//燃料は半径方向にのみ一様に減少するとし,重心が機軸方向に移動しない仮定
		lcgFuel = engine.get("Length Fuel-C.G. from End [m]").asDouble();
		lcgOxBef = distanceTank + 0.5 * lTank;
		lcgOxAft = distanceTank;
		double[] lcgOxLog = new double[thrust_data.length];
		for (int i = 0; i < lcgOxLog.length; i++) {
			lcgOxLog[i] = lcgOxAft - (mOxLog[i] / mOxBef) * (lcgOxAft - lcgOxBef);			
		}
		lgcOxAnaly = new Interpolation(time_array, lcgOxLog);
		//-------------------------------------------------

		// -------------- Moment of Inertia --------------
		IjFuelPitchBef = mFuelBef * ((Math.pow(dFuelInBef, 2) + Math.pow(dFuelOut, 2)) / 16.0 + Math.pow(lFuel, 2) / 12.0);
		IjFuelRollBef = mFuelBef * (Math.pow(dFuelInBef, 2) + Math.pow(dFuelOut, 2)) / 8.0;
		IjFuelPitchAft = mFuelAft * ((Math.pow(dFuelInAft, 2) + Math.pow(dFuelOut, 2)) / 16.0 + Math.pow(lFuel, 2) / 12.0);
		IjFuelRollAft = mFuelAft * ((Math.pow(dFuelInAft, 2) + Math.pow(dFuelOut, 2))) / 8.0;

		IjOxPitchBef = mOxBef * (Math.pow(dTank, 2) / 16.0 + Math.pow(lTank, 2) / 12.0);
		IjOxRollBef = mOxBef * Math.pow(dTank, 2) / 8.0;
		//------------------------------------------------
	}

	/**
	 * @return 推進剤質量時間変化率。正の値で返されるので注意
	 * */
	public double getMdotProp(double t) {
		if(t < timeBurnout) {
			return mDotPropAnaly.linearInterp1column(t);
		}
		else {
			return 0.0;
		}
	}

	public double getMassFuel(double t) {
		if(t < timeBurnout) {
			return mFuelBef + (mFuelAft - mFuelBef) * (t / timeBurnout);
		}
		else {
			return mFuelAft;
		}
	}

	public double getMassOx(double t) {
		if(t < timeActuate) {
			return mOxAnaly.linearInterp1column(t);
			// return mOxBef *(1 -  t / timeBurnout);
		}else {
			return 0.0;
		}
	}

	/**
	 * 燃料の内径を燃焼前後の値で線形的に変化するとして計算
	 * コンストラクタの計算みたいに，質量比から計算してもいいが，あまり結果に影響ないと思う
	 * @return 燃料の内径
	 * */
	private double getDiamFuelIn(double t) {
		if(t < timeBurnout) {
			return dFuelInBef + (dFuelInAft - dFuelInBef) * (t / timeBurnout);
		}else {
			return dFuelInAft;
		}
	}

	private double getLengthOx(double t) {
		if(t < timeActuate) {
			// return lTank * (t / timeBurnout);
			return lTank * (1.0 - t / timeActuate);
		}else {
			return 0.0;
		}
	}

	/**
	 * @return エンジン後端から酸化剤重心までの距離 [m]
	 * */
	public double getLcgOx(double t) {
		if(t < timeActuate) {
			// return distanceTank + 0.5 * getLengthOx(t);
			return lgcOxAnaly.linearInterp1column(t);
		}else {
			return lcgOxAft;
		}
	}

	/**
	 * @return 燃料重心回りの燃料のPitch慣性モーメント
	 * */
	public double getIjFuelPitch(double t) {
		if(t < timeBurnout) {
			/* 中空円筒の慣性モーメント
			 * Iyy = m * ((din^2 + dout^2) / 16 + L / 12)
			 * **/
			return getMassFuel(t) * ((Math.pow(dFuelOut, 2) + Math.pow(getDiamFuelIn(t), 2)) / 16.0 + Math.pow(lFuel, 2) / 12.0);
		}else {
			return IjFuelPitchAft;
		}
	}

	/**
	 * @return 燃料重心回りの燃料のPitch慣性モーメント
	 * */
	public double getIjFuelRoll(double t) {
		if(t < timeBurnout) {
			/*中空円筒の慣性モーメント
			 * Ixx = m * (din^2 + dout^2) / 8
			 * **/
			return getMassFuel(t) * (Math.pow(getDiamFuelIn(t), 2) + Math.pow(dFuelOut, 2)) / 8.0;
		}else {
			return IjFuelRollAft;
		}
	}

	/**
	 * スロッシングの影響を無視。
	 * 液体酸化剤を一様円柱と仮定。
	 * @return 酸化剤重心回りの酸化剤のPitch慣性モーメント
	 * */
	public double getIjOxPitch(double t) {
		if(t < timeActuate) {
			/* 中実円筒の慣性モーメント
			 * Iyy = m * (d^2 / 16 + L^2 / 12)
			 * **/
			return getMassOx(t) * (Math.pow(dTank, 2) / 16.0 + Math.pow(getLengthOx(t), 2) / 12.0);
		}else {
			return 0.0;
		}
	}

	/**
	 * スロッシングの影響を無視。
	 * 液体酸化剤を一様円柱と仮定。
	 * @return 酸化剤重心回りの酸化剤のPitch慣性モーメント
	 * */
	public double getIjOxRoll(double t) {
		if(t < timeActuate) {
			/* 中実円筒の慣性モーメント
			 * Ixx = m * d^2 / 8
			 * **/
			return getMassOx(t) * Math.pow(dTank, 2) / 8.0;
		}else {
			return 0.0;
		}
	}

	public double thrust(double t) {
		if(t < timeActuate) {
			return thrustAnaly.linearInterp1column(t);
		}
		return 0.0;
	}

}
