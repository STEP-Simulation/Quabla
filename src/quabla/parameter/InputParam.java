package quabla.parameter;

public class InputParam {

	//--------------------↓input information↓--------------------


	// Length -------------------------------------------------
	/** Total Length*/
	public double l    = 1.793;//[m]

	/** Center of Grabity @ Dry*/
	public double lcgDry = 1.020; // [m]

	/** Outer Diameter*/
	public double d    = 0.102; //[m]

	/** Upper Launch Lug from Nosecone*/
	public double upper_lug = 0.8;//[m]

	/** Lower Launch Lug from Nosecone*/
	public double lower_lug = 1.793;//[m]
	//---------------------------------------------------------



	// Weight -------------------------------------------------
	/** Weight @ Dry */
	public double mDry = 4.85; // [kg]
	//---------------------------------------------------------



	// Moment of Inertia --------------------------------------
	public double IjPitchDry = 0.7053; // [kg m^2]
	public double IjRollDry = 0.0133; // [kg m^2]
	//---------------------------------------------------------


	// Aero Parameter -----------------------------------------
	public boolean Cd_file_exist = true;
	public boolean CNa_file_exist = false;
	public boolean Lcp_file_exist = false;
	public String Lcp_file = "Lcp.csv";
	public String Cd_file = "Cd.csv";
	public String CNa_file = "CNa.csv";
	/**Constant Center of Pressure*/
	public double Lcp = 1.313;//[m]

	/** Constant Coefficient of Drag*/
	public double Cd  = 0.60; //[-]

	/** Constant Coefficient of Nomal Force*/
	public double CNa = 8.579;  //[1/rad]

	/** Coefficient of Aero Dumping Moment in Pitch and Yaw*/
	public double Cmq = - 2.676;//[1/rad]

	/** Coefficient of Aero Dumping Moment in Roll*/
	public double Clp = -0.0504; //[1/rad]
	//---------------------------------------------------------



	// Parachute ----------------------------------------------
	/** Product of drag coefficient of parachute(or draug chute) and  */
	public double CdS1 = 0.7734;     //[m2]
	public boolean para2_exist = false;
											/**
											 * true : using 2 parachutes
											 * false : using only a parachute
											 *  */
	public double CdS2      = 0.851269;   //[m/s] velocity after 2nd para open (falling 1st and 2nd para)
	public double alt_para2 = 300.0;  //[m]altitude of 2nd para open
	//---------------------------------------------------------


	// Launcher -----------------------------------------------
	public double elevation_launcher = 85.0;  //[deg] elevation of launcher (vertical = 90.0 deg)
	public double azimuth_launcher   = 285.0;  //[deg] azimuth of launcher (east = 0 deg , south = 270 deg )
	public double length_Launcher    = 5.0;       //[m]length of launcher
	public double magnetic_dec       = 0.0;    //[deg] magnetic declination
	//---------------------------------------------------------



	// Engine Configulation -----------------------------------
	/** Nozzle throat diameter */
	public double dth = 11.45;   //[mm]

	/** nozzle expansion ratio
	 * nozzle area ratio */
	public double eps = 2.6951; //[-]

	//---------- 改良後エンジンパラメータ ----------

	// 燃焼時間 , エンジン作動時間ではない
	public double timeBurnout = 5.6; //[s]

	/** 平均比推力 */
	public double Isp = 220.38; // [s]

	/** 機体後端からグレイン重心までの距離(インジェクタベル含めず) */
	public double distanceFuelCG = 0.139; // [m]

	public double massFuelBef = 0.3800; //[kg]

	public double massFuelAft = 0.2805; //[kg]

	public double diameterFuelPort = 25.50 ; //[mm]

	public double diameterFuelOut = 50.0; //[mm]

	/** 機体後端からタンク後端(口金)までの距離 */
	public double distanceTank = 0.2794;// [m]

	/**length of oxidizer's tank */
	public double lengthTank = 0.541; //[m]

	/** diameter of oxidizer's tank */
	public double diameterTank = 54; //[mm]

	/** Tank Volume */
	public double volTank = 835; //[cc]

	/** Oxidizer's Density */
	public double densityOxidizer = 724 ; //[kg/m^3]

	public double lengthFuel = 0.2794; //[m]
	//---------------------------------------------------------



	// Wind ---------------------------------------------------
	public boolean Wind_file_exsit = true;
	public String wind_file = "wind.csv";

	public int WindModel   = 1;  // wind model

	public double Cdv      = 6.0;   //coefficient []
									/**
									 * coefficient [-]
									 * in case of WindModel = 1
									 * */

	/** Wind Azimuth @ Reference Altitude */
	public double wind_azimuth = 270.0;//[deg]
	/* 0 deg : from west to east
	 * 270 deg : from north to south
	 * **/

	/** Wind Speed @ reference altitude */
	public double wind_speed   = 2.0; 	 //wind speed [m/s];

	/** wind reference altitude*/
	public double Zr     = 5.0; //[m]

	/** temperture at 0 m */
	public double temperture0  = 15.0; //[℃]
	//---------------------------------------------------------


	// Wind Map Configulation ---------------------------------
	//落下分散を出すときのみ使用

	/** minimum wind speed */
	public double speed_min = 1.0; //[m/s]
	public double speed_step = 1.0; //[m/s]風速の刻み幅
	public int speed_num = 7;   //何風速分計算したいか
	public int angle_num = 8;
	 							/*何風向分知りたいか
	 							 * 基本4の倍数で入力
	 							 * **/
	//----------------------------------------------------------

	// Simulation -----------------------------------------------
	public String simulationMode = "multi";
		/* Input "single" OR "multi"
		 * single : single condition
		 * multi : multi condition (落下分散の計算)
		 * **/
	public boolean tip_off_exist = false;
	public int     n = 400000; //maximum number of simulation steps
	public double dt = 0.001;   // [s] メインソルバの時間ステップ

	/**log dataの時間ステップ
	 * 粗くしてもメインソルバの精度には影響が出ない
	 * メインソルバより細かくすると値がおかしくなる
	 * */
	public double dt_output = 0.01;//[s]
	public String  thrustcurve = "thrust.csv"; //推力データのアドレス

	/**
	 * 計算結果の出力先のファイルパス
	 * パスは\\で終わるように
	 * */
	public String result_filepath = "C:\\Users\\zoooi\\Documents\\STEP\\機体班\\シュミレーション\\テスト\\";//出力先のファイルパス
	public String dir_name = "debug";
	//------------------------------------------------------------


	//--------------------↑input information↑--------------------


}
