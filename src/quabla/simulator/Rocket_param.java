package quabla.simulator;

import quabla.InputParam;
import quabla.simulator.numerical_analysis.Interpolation;


/**
 * このクラスはhogehoge
 *
 * */
public class Rocket_param {

	//solverで1回だけ呼び出したい
	//時間変化するものは、インスタンスメソッドにするしかない？

	public double L , d, S,upper_lug,lower_lug;
	double Lcg_0,Lcg_f,Lcg_prop;
	public double dth , eps , Ath , Ae, de;
	double m0 , mf , mp0;
	double Ij_pitch_0, Ij_pitch_f , Ij_roll_0 , Ij_roll_f ;
	double Ij_dot_pitch, Ij_dot_roll;
	double Ij_prop_pitch_0 , Ij_prop_roll_0;
	double CdS1,CdS2;
	boolean para2_exist;
	double alt_para2;
	double dt;
	double thrust_data[][];
	Interpolation thrust_analy;
	double t_burnout;



	public Rocket_param(InputParam spec){



		//==geometory===================================================
		this.L = spec.l;
		this.d = spec.d;
		this.S = 0.25 * Math.PI * Math.pow(d , 2);
		this.upper_lug = spec.upper_lug;
		this.lower_lug = spec.lower_lug;

		this.dth = spec.dth * Math.pow(10, -3);// [mm] => [m]
		this.eps = spec.eps;
		this.Ath = 0.25 * Math.PI * Math.pow(dth, 2);
		this.Ae = Ath * eps;
		this.de = Math.sqrt(Ae * 4 / Math.PI);
		//===============================================================


		//==Center of gravity============================================
		this.Lcg_0 = spec.lcg0;
		this.Lcg_f = spec.lcgf;
		this.Lcg_prop = spec.lcgp;
		//===============================================================

		//==mass=========================================================
		this.m0 = spec.m0;
		this.mf = spec.mf;
		this.mp0 = m0 - mf;
		//===============================================================


		//==moment of inertia============================================
		this.Ij_pitch_0 = spec.Ij_pitch_0;
		this.Ij_pitch_f = spec.Ij_pitch_f;
		this.Ij_roll_0 = spec.Ij_roll_0;
		this.Ij_roll_f = spec.Ij_roll_f;
		this.Ij_prop_pitch_0 = Ij_pitch_0 - Ij_pitch_f;
		this.Ij_prop_roll_0 = Ij_roll_0 - Ij_roll_f;
		//===============================================================


		//Parachute======================================================
		this.CdS1 = spec.CdS1;
		this.para2_exist = spec.para2_exist;
		if(para2_exist) {
			this.CdS2 = spec.CdS2;
			this.alt_para2 = spec.alt_para2;
		}else {
			this.CdS2 = 0.0;
			this.alt_para2 = 0.0;
		}
		//===============================================================


		this.dt = spec.dt;


		this.thrust_data = Getcsv.get2ColumnArray(spec.thrustcurve);

		this.t_burnout = thrust_data.length * dt ;

		double time_array[] = new double[thrust_data.length];
		double thrust_array[] = new double[thrust_data.length];

		for(int i = 0; i < thrust_data.length ; i++) {
			time_array[i] = thrust_data[i][0];
			thrust_array[i] = thrust_data[i][1];
		}

		this.thrust_analy = new Interpolation(time_array , thrust_array);



	}


	public double mdot(double t) {
		double mdot;

		if(t < t_burnout) {
			mdot = (mf - m0) / t_burnout;// mdot < 0
		}else {
			mdot = 0;
		}

		return mdot;
	}

	public double[] Ij_dot(double t) {
		double Ij_dot[] = new double[3];

		if(t<t_burnout) {
			Ij_dot[0] = (Ij_roll_f - Ij_roll_0) / t_burnout;
			Ij_dot[1] = (Ij_pitch_f - Ij_pitch_0) / t_burnout;
			Ij_dot[2] = Ij_dot[1];
		}else {
			for(int i=0; i<3; i++) {
				Ij_dot[i] = 0.0;
			}
		}

		return Ij_dot;
	}


	public double mass(double t) {
		double m;

		if(t < t_burnout) {
			m = m0 +  (mf - m0) * t / t_burnout;
		}else {
			m = mf;
		}

		return m;
	}


	public double Ij_roll(double t) {
		double Ij_roll;

		if(t < t_burnout) {
			Ij_roll = Ij_roll_0 + t*(Ij_roll_f - Ij_roll_0) / t_burnout;
		}else {
			Ij_roll = Ij_roll_f;
		}

		return Ij_roll;
	}


	public double Ij_pitch(double t) {
		double Ij_pitch;

		if(t < t_burnout) {
			Ij_pitch = Ij_pitch_0 + t*(Ij_pitch_f - Ij_pitch_0) / t_burnout;
		}else {
			Ij_pitch = Ij_pitch_f;
		}

		return Ij_pitch;
	}


	public double Lcg(double t){
		double Lcg;

		if(t < t_burnout) {
			Lcg = Lcg_0 + (Lcg_f - Lcg_0) * t / t_burnout;
		}else {
			Lcg = Lcg_f;
		}

		return Lcg;
	}


	public double thrust(double t) {
		double thrust ;

		if(t < t_burnout) {
			thrust = thrust_analy.linear_interpolation(t);
		}else {
			thrust = 0.0;
		}

		return thrust;
	}

}
