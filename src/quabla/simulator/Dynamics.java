package quabla.simulator;

/**
 * 位置、姿勢、時間、諸元を受け取って、微小変化量dxを返す
 * */

public class Dynamics {

	public static double[] trajectory(double x[], double t, Rocket_param rocket, Environment env,Aero_param aero, Wind wind) {
		double dx[] = new double[13];
		double h = rocket.dt;

		double Pos_ENU[] = {x[0] , x[1] , x[2]};
		double Vel_ENU[] = {x[3] , x[4] , x[5]};
		double omega_Body[] = {x[6] , x[7] , x[8]};
		double quat[] = {x[9] , x[10] , x[11] , x[12]};

		double m = rocket.mass(t);
		double m_dot = rocket.mdot(t);
		double altitude = Pos_ENU[2];
		double p = omega_Body[0];
		double q = omega_Body[1];
		double r = omega_Body[2];
		quat = Coodinate.quat_nomalization(quat);

		//Translation coodinate
		double DCM_ENU2Body[][] = Coodinate.quat2DCM_ENU2Body(quat);
		double DCM_Body2ENU[][] = Coodinate.DCM_ENU2Body2DCM_Body2_ENU(DCM_ENU2Body);

		//alpha(angle of attck), beta(side-slip angle)
		double wind_ENU[] = Wind.wind_ENU(wind.wind_speed(altitude), wind.wind_direction(altitude));
		double Vel_air_ENU[] = new double[3];

		for(int i = 0; i<3; i++) {
			Vel_air_ENU[i] = Vel_ENU[i] - wind_ENU[i];
		}
		double Vel_air_Body[] = Coodinate.vec_trans(DCM_ENU2Body, Vel_air_ENU);
		double Vel_air_abs = Math.sqrt(Vel_air_Body[0]*Vel_air_Body[0] + Vel_air_Body[1]*Vel_air_Body[1] + Vel_air_Body[2]*Vel_air_Body[2]);

		double u = Vel_air_Body[0];
		double v = Vel_air_Body[1];
		double w = Vel_air_Body[2];

		double alpha , beta; //angle of atack , angle of side-slip
		if(Vel_air_abs <= 0.0) {
			alpha = 0.0;
			beta = 0.0;
		}else {
			alpha = Math.asin(w / Vel_air_abs);
			beta = Math.asin(v / Vel_air_abs);
		}

		double g[] = {0.0 , 0.0 , -env.gravity(altitude)};
		double P0 = env.atomospheric_pressure(0);
		double P = env.atomospheric_pressure(altitude);
		double rho = env.density_air(altitude);
		double Cs = env.soundspeed(altitude);
		double Mach = Vel_air_abs / Cs;
		double dynamics_pressure = 0.5 * rho * Math.pow(Vel_air_abs, 2);

		double thrust[] = {rocket.thrust(t), 0.0, 0.0};
		if(thrust[0] <= 0.0) {
			thrust[0] = 0.0;
			thrust[1] = 0.0;
			thrust[2] = 0.0;
		}else {
			double pressure_thrust = (P0 - P)*rocket.Ae;
			thrust[0] += pressure_thrust;
			thrust[1] = 0.0;
			thrust[2] = 0.0;
		}

		//Aero Force
		double drag = dynamics_pressure * aero.Cd(Mach) * rocket.S;
		double nomal = dynamics_pressure * aero.CNa(Mach) * rocket.S * alpha;
		double side = dynamics_pressure * aero.CNa(Mach) * rocket.S * beta;
		double F_aero[] = {- drag , - side , - nomal};

		//Newton Equation
		double Force[] = new double[3];
		for(int i=0; i<3; i++) {
			Force[i] = thrust[i] + F_aero[i];
		}
		double Acc_ENU[] = Coodinate.vec_trans(DCM_Body2ENU, Force);
		for(int i=0; i<3; i++) {
			Acc_ENU[i] =Acc_ENU[i]/m +  g[i];
		}

		//center of gravity
		double Lcg = rocket.Lcg(t);
		double Lcg_p = rocket.Lcg_prop;
		double Lcp = aero.Lcp(Mach);

		//Inretia Moment
		double Ij_roll = rocket.Ij_roll(t);
		double Ij_pitch = rocket.Ij_pitch(t);
		double Ij[] = {Ij_roll, Ij_pitch, Ij_pitch};

		double Ij_dot[] = rocket.Ij_dot(t);

		double moment_aero[] = {0.0, F_aero[2]*(Lcp - Lcg), -F_aero[1]*(Lcp - Lcg)};
		//Aero Dumping Moment
		double moment_aero_dump[] = new double[3];
		moment_aero_dump[0] = dynamics_pressure * aero.Clp * rocket.S *(0.5*Math.pow(rocket.d, 2)/Vel_air_abs);
		moment_aero_dump[1] = dynamics_pressure * aero.Cmq * rocket.S *(0.5*Math.pow(rocket.L, 2)/Vel_air_abs);
		moment_aero_dump[0] = dynamics_pressure * aero.Cnr * rocket.S *(0.5*Math.pow(rocket.L, 2)/Vel_air_abs);

		//Jet Dumping Moment
		double moment_jet_dump[] = new double[3];
		moment_jet_dump[0] = (-Ij_dot[0] + m_dot * 0.5 * (0.25*Math.pow(rocket.de, 2)));
		moment_jet_dump[1] = (-Ij_dot[1] + m_dot * (Math.pow(Lcg-Lcg_p, 2) - Math.pow(rocket.L-Lcg_p, 2)));
		moment_jet_dump[2] = (-Ij_dot[2] + m_dot * (Math.pow(Lcg-Lcg_p, 2) - Math.pow(rocket.L-Lcg_p, 2)));

		double moment_dumping[] = new double[3];
		for(int i=0; i<3; i++){
			moment_dumping[i] = moment_aero_dump[i] + moment_jet_dump[i];
		}


		double k1[] = Acc_anguler(t,p,q,r,Ij,moment_aero,moment_dumping);
		double k2[] = Acc_anguler(t+0.5*h,p+0.5*h*k1[0],q+0.5*h*k1[1],r+0.5*h*k1[2],Ij,moment_aero,moment_dumping);
		double k3[] = Acc_anguler(t+0.5*h,p+0.5*h*k2[0],q+0.5*h*k2[1],r+0.5*h*k2[2],Ij,moment_aero,moment_dumping);
		double k4[] = Acc_anguler(t+h,p+h*k3[0],q+h*k3[1],r+h*k3[2],Ij,moment_aero,moment_dumping);

		double omegadot[] = new double[3];
		for(int i=0; i<3; i++) {
			omegadot[i] = (k1[i] + 2.0*k2[i] + 2.0*k3[i] + k4[i]) / 6.0;
		}
		double tensor[][] = Coodinate.Omega_tensor(p, q, r);
		double quatdot[] = Coodinate.vec_trans(tensor, quat);
		for(int i=0; i<4;i++)
			quatdot[i] *= 0.5;

		for(int i=0; i<3; i++) {
			dx[i] = Vel_ENU[i];//Pos_ENU
			dx[3+i] = Acc_ENU[i];//Vel_ENU
			dx[6+i] = omegadot[i];//omega_Body
		}
		for(int i=0; i<4; i++)
			dx[9+i] = quatdot[i];//quat

		return dx;
	}

	public static double[] on_luncher(double x[], double t, Rocket_param rocket, Environment env , Aero_param aero, Wind wind , double quat0[]) {
		double dx[] = new double[12];
		double Pos_ENU[] = {x[0] , x[1] , x[2]};
		double Vel_ENU[] = {x[6] , x[7] , x[8]};
		double Vel_Body[] = {x[9] , x[10] , x[11]};
		double altitude = Pos_ENU[2];

		double m = rocket.mass(t);



		double Vel_air_abs;

		//Tronsition coodinate
		double DCM_ENU2Body[][] = Coodinate.quat2DCM_ENU2Body(quat0);
		double DCM_Body2ENU[][] = Coodinate.DCM_ENU2Body2DCM_Body2_ENU(DCM_ENU2Body);

		double elevation = Coodinate.deg2rad(Coodinate.DCM2euler(DCM_ENU2Body)[2]);
		double Z0 = (rocket.L-rocket.Lcg_0)*Math.sin(elevation);

		double wind_ENU[] = Wind.wind_ENU(wind.wind_speed(altitude), wind.wind_direction(altitude));
		double Vel_air_ENU[] = new double[3];
		for(int i = 0; i<3; i++) {
			Vel_air_ENU[i] = Vel_ENU[i] - wind_ENU[i];
		}

		double Vel_air_Body[] = Coodinate.vec_trans(DCM_ENU2Body, Vel_air_ENU);

		Vel_air_abs = Math.sqrt(Vel_air_Body[0]*Vel_air_Body[0] + Vel_air_Body[1]*Vel_air_Body[1] + Vel_air_Body[2]*Vel_air_Body[2]);

		double g[] = {0.0 , 0.0 , -env.gravity(altitude)};
		double P0 = env.atomospheric_pressure(0);
		double P = env.atomospheric_pressure(altitude);
		double rho = env.density_air(altitude);
		double Cs = env.soundspeed(altitude);
		double Mach = Vel_air_abs / Cs;
		double dynamic_pressure = 0.5 * rho * Math.pow(Vel_air_abs, 2);


		//thrust
		double thrust[] = {rocket.thrust(t) , 0.0 , 0.0};
		if(thrust[0] <= 0.0) {
			for(int i = 0; i<3; i++) {
				thrust[i] = 0.0;
			}
		}else {
			double pressure_thrust = (P0 - P) * rocket.Ae;
			thrust[0] += pressure_thrust;
			thrust[1] = 0.0;
			thrust[2] = 0.0;
		}

		//Aero force
		double drag = dynamic_pressure * rocket.S * aero.Cd(Mach);
		double F_aero[] = { -drag , 0.0 , 0.0};

		//Newton equation
		double Force[] =  new double[3];
		for(int i = 0; i<3 ; i++) {
			Force[i] = thrust[i] + F_aero[i];
		}

		double Acc_Body[] = {Force[0] / m + Math.abs(g[2])*Math.sin(elevation) , 0.0 , 0.0};
		double Acc_ENU[] = Coodinate.vec_trans(DCM_Body2ENU, Acc_Body);

		//推力が自重に負けているとき(居座り)
		if(Acc_ENU[2] <= 0.0 && t<rocket.t_burnout && Pos_ENU[2]<=Z0) {
			for(int i=0; i<3; i++) {
				Acc_ENU[i] = 0.0;
			}
		}


		for(int i = 0 ; i<3; i++) {
			dx[i] = Vel_ENU[i];     //Pos_ENU
			dx[3+i] = Vel_Body[i];  //distance_Body
			dx[6+i] = Acc_ENU[i];   //Vel_ENU
			dx[9+i] = Acc_Body[i];  //Vel_Body
		}

		return dx;
	}

	/*
	public static double[] tip_off() {

	}
	*/

	public static double[] parachute(double x[], double t, Rocket_param rocket, Environment env, Wind wind) {
		double dx[] = new double[4];
		double m = rocket.mass(t);
		double Pos_ENU[] = {x[0] , x[1] , x[2]};
		double altitude = Pos_ENU[2];
		double Vel_descent = x[3];

		double wind_ENU[] = Wind.wind_ENU(wind.wind_speed(altitude), wind.wind_direction(altitude));
		double Vel_ENU[] = {wind_ENU[0], wind_ENU[1], Vel_descent};

		double g = env.gravity(altitude);
		double rho = env.density_air(altitude);

		double CdS;
		if(rocket.para2_exist && altitude <= rocket.alt_para2) {
			CdS = rocket.CdS1 + rocket.CdS2;
		}else {
			CdS = rocket.CdS1;
		}

		double drag = 0.5 * rho * CdS * Math.pow(Vel_ENU[2], 2);
		double Acc = drag / m - g;
		for(int i=0; i<3; i++) {
			dx[i] = Vel_ENU[i];
		}
		dx[3] = Acc;

		return dx;

	}

	public static double[] Acc_anguler(double t, double p,double q,double r,double Ij[],double moment_aero[], double moment_dumping[]) {
		double omegadot[] = new double[3];

		omegadot[0] = ((Ij[1]-Ij[2])*q*r + moment_aero[0] + moment_dumping[0]*p) / Ij[0];
		omegadot[1] = ((Ij[2]-Ij[0])*p*r + moment_aero[1] + moment_dumping[1]*q) / Ij[1];
		omegadot[2] = ((Ij[0]-Ij[1])*p*q + moment_aero[2] + moment_dumping[2]*r) / Ij[2];
		return omegadot;
	}

}
