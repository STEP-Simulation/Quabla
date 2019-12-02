package quabla.parameter;

public class InputParam {

	//======================↓input information↓=================================


	//Length [m]================================================================
	/**Total Length*/
	public double l    = 3.00; // 1.888;// 2.406;//[m]

	/**Center of gravity @ take-off*/
	public double lcg0 = 1.887263;//1.054;//1.487;//[m]

	/**Center of Gravity @ engine-cut-off*/
	public double lcgf = 1.788345;//1.028;//1.430;//[m]

	/**Center of Gravity of Propellant*/
	public double lcgp = 2.245251;// 0.560;//1.992;  //[m]

	/**Outer Diameter*/
	public double d    = 0.120; //0.116;//0.120;  //[m]

	/**Upper Launch Lug from Nosecone*/
	public double upper_lug = 0.8;//0.79;//[m]

	/**Lower Launch Lug from Nosecone*/
	public double lower_lug = 1.8;//2.35;//[m]
	//==========================================================================


	//Weight [kg]===============================================================
	public double m0  = 17.803;// 7.243;//6.514;   //weight @lift-off
	public double mf  = 13.88359; //6.621;//5.708;   //weight @ engnine-cut-off
	//==========================================================================



	//moment of inertria [kg m^2]===============================================
	public double Ij_pitch_0  = 10.596653;//1.638;//3.47;  //[kg m^2] pitch moment of inertia  @take off
	public double Ij_pitch_f  = 9.677825; // 1.579;//3.288;  //[kg m^2]pitch moment of inertia @engine cut off
	public double Ij_roll_0  = 0.040251;//0.5 ;   //[kg m^2]roll moment of inertia @lift off
	public double Ij_roll_f  = 0.038338;//0.4 ;   //[kg m^2] roll moment of inertia @ engine cut off
	public double IjStRoll = 0.05 ; //[kg m^2] moment of inertia of structure in roll
	public double IjStructPitch = 7.0 ; //[kg m^2] moment of inertia of structure in pitch
	//==========================================================================


	//aero parameter============================================================
	public boolean Cd_file_exist = true;
	public boolean CNa_file_exist = false;
	public boolean Lcp_file_exist = false;
	public String Lcp_file;
	public String Cd_file = "C:\\Users\\zoooi\\Documents\\STEP\\機体班\\シュミレーション\\ForRocket\\ForRocket\\Cd2.csv";
	public String CNa_file;
	/**Constant Center of Pressure*/
	public double Lcp = 2.100;//1.276;///1.771 ;//[m]

	/**Constant Coefficient of Drag*/
	public double Cd  = 0.45;//0.6;    //[-]

	/**Constant Coefficient of Nomal Force*/
	public double CNa = 10.0;    //[1/rad]

	/**Coefficient of Aero Dumping Moment in Pitch and Yaw*/
	public double Cmq = -2.17;   //[1/rad]

	/**Coefficient of Aero Dumping Moment in Roll*/
	public double Clp = -0.050;   //[1/rad]
	//==========================================================================



	//Parachute=============================================
	/** Product of drag coefficient of parachute(or draug chute) and  */
	public double CdS1 = 0.209396866;     //[m2]
	public boolean para2_exist = false;
											/**
											 * true : using 2 parachutes
											 * false : using only a parachute
											 *  */
	public double CdS2      = 0.851268565;   //[m/s] velocity after 2nd para open (falling 1st and 2nd para)
	public double alt_para2 = 200.0;  //[m]altitude of 2nd para open
	//==========================================================================


	//launcher==================================================================
	public double elevation_launcher = 85.0;  //[deg] elevation of launcher (vertical = 90.0 deg)
	public double azimuth_launcher   = 290.0;  //[deg] azimuth of launcher (east = 0 deg , south = 270 deg )
	public double length_Launcher    = 5.0;       //[m]length of launcher
	public double magnetic_dec       = 8.9;    //[deg] magnetic declination
	//==========================================================================



	//engine configulation======================================================
	/** Nozzle throat diameter */
	public double dth = 11.45;   //[mm]
	/** nozzle expansion ratio
	 * nozzle area ratio */
	public double eps = 2.6951;  //[-]

	public double massFuelBef;

	public double massFuelAft;

	public double diameterFuelPort;

	public double diameterFuelOut;

	/** oxidizer mass*/
	public double massOxInit; //[kg]

	/**length of oxidizer's tank*/
	public double lengthTank ; //[m]

	/** diameter of oxidizer's tank*/
	public double diameterTank; //[m]

	/** Tank Volume */
	public double volTank = 4030 ; //[cc]

	/** Oxidizer's Density */
	public double densityOxidizer ; //[kg/m^3]

	/**length of motor case
	 * if using HyperTEK series, length of grain*/
	public double lenghtMotor; //[m]

	public double lengthFuel; //[m]
	//==========================================================================



	//wind======================================================================
	public boolean Wind_file_exsit = false;
	public String wind_file = "C:\\Users\\zoooi\\Documents\\STEP\\機体班\\シュミレーション"
			+ "\\wind_august_noshiro_nominal.csv";
	public int WindModel   = 1;  // wind model

	public double Cdv      = 6.0;   //coefficient []
									/**
									 * coefficient [-]
									 * in case of WindModel = 1
									 * */

	/**wind azimuth*/
	public double wind_azimuth =  -150.0;//[deg]
	/* 0 deg : from west to east
	 * 270 deg : from north to south
	 * **/

	public double wind_speed   = 1.0; 	 //wind speed [m/s];
	/**wind reference altitude*/
	public double Zr     = 5.0; 		 //altitude anemometer located [m]
	public double temperture0  = 15.0; //temperture at 0 m [℃]
	//==========================================================================


	//wind map configulation============================
	//落下分散を出すときのみ使用

	/**minimum wind speed*/
	public double speed_min = 1.0; //[m/s]
	public double speed_step = 1.0; //[m/s]風速の刻み幅
	public int speed_num = 7;   //何風速分計算したいか
	public int angle_num = 8;
	 							/*何風向分知りたいか
	 							 * 基本4の倍数で入力
	 							 * **/
	//=========================================

	//simulation================================================================
	public int Mode = 1;
	public boolean tip_off_exist = false;
	public int     n = 400000; //maximum number of simulation steps
	public double dt = 0.001;   // [s] メインソルバの時間ステップ

	/**log dataの時間ステップ
	 * 粗くしてもメインソルバの精度には影響が出ない
	 * メインソルバより細かくすると値がおかしくなる
	 * */
	public double dt_output = 0.01;//[s]
	public String  thrustcurve = "C:\\Users\\zoooi\\Documents\\STEP\\機体班\\シュミレーション\\thrust_M1000.csv"; //推力データのアドレス

	/**計算結果の出力先のファイルパス
	 * パスは\\で終わるように
	 * */
	public String result_filepath = "C:\\Users\\zoooi\\Documents\\STEP\\機体班\\シュミレーション\\";//出力先のファイルパス
	public String dir_name = "test";
	//==========================================================================



	//======================↑input information↑=================================


}
