package quabla;

public class InputParam {

	//======================↓input information↓=================================


	//Length [m]================================================================
	/**Total Length*/
	public double l    = 2.406;//[m]

	/**Center of gravity @ take-off*/
	public double lcg0 = 1.487;//[m]

	/**Center of Gravity @ engine-cut-off*/
	public double lcgf = 1.430;//[m]

	/**Center of Gravity of Propellant*/
	public double lcgp = 1.992;  //[m]

	/**Outer Diameter*/
	public double d    = 0.120;  //[m]
	/**Upper Launch Lug from Nosecone*/

	public double upper_lug = 0.79;//[m]

	/**Lower Launch Lug from Nosecone*/
	public double lower_lug = 2.35;//[m]
	//==========================================================================


	//Weight [kg]===============================================================
	public double m0  = 6.514;   //weight @lift-off
	public double mf  = 5.708;   //weight @ engnine-cut-off
	//==========================================================================



	//moment of inertria [kg m^2]===============================================
	public double Ij_pitch_0  = 3.47;  //[kg m^2] pitch moment of inertia  @take off
	public double Ij_pitch_f  = 3.288;  //[kg m^2]pitch moment of inertia @engine cut off
	public double Ij_roll_0  = 0.5 ;   //[kg m^2]roll moment of inertia @lift off
	public double Ij_roll_f  =0.4 ;   //[kg m^2] roll moment of inertia @ engine cut off
	/*
	public double Ij_prop_pitch_0 = 0.1825;  //[kg m^2]pitch moment of inertia of fuel & N2O
	public double Ij_prop_roll_0 ; //[kg m^2]roll moment of inertia fo fuel & N2O
	*/
	//==========================================================================


	//aero parameter============================================================
	public boolean Cd_file_exist = false;
	public boolean CNa_file_exist = false;
	public boolean Lcp_file_exist = false;
	public String Lcp_file;
	public String Cd_file;
	public String CNa_file;
	/**Constant Center of Pressure*/
	public double Lcp = 1.771 ;//[m]
	/**Constant Coefficient of Drag*/
	public double Cd  = 0.6;    //[-]
	/**Constant Coefficient of Nomal Force*/
	public double CNa = 10.0;    //[1/rad]
	/**Coefficient of Aero Dumping Moment in Pitch and Yaw*/
	public double Cmq = -3.00;   //[1/rad]
	/**Coefficient of Aero Dumping Moment in Roll*/
	public double Clp = -0.10;   //[1/rad]
	//==========================================================================



	//Parachute=============================================
	public double CdS1 = 0.5;     //[m/s] falling velocity of 1st parachute
	public boolean para2_exist = false;
											/**
											 * true : using 2 parachutes
											 * false : using only a parachute
											 *  */
	public double CdS2   = 0.482;   //[m/s] velocity after 2nd para open (falling 1st and 2nd para)
	public double alt_para2 = 200.0;  //[m]altitude of 2nd para open
	//==========================================================================


	//launcher==================================================================
	public double elevation_launcher = 85.0;  //[deg] elevation of launcher (vertical = 90.0 deg)
	public double azimuth_launcher   = 290.0;  //[deg] azimuth of launcher (east = 0 deg , south = 270 deg )
	public double length_Launcher     = 5.0;       //[m]length of launcher
	public double magnetic_dec       = 0.0;      //[deg] magnetic declination
	//==========================================================================



	//engine configulation======================================================
	public double dth = 11.45;   //[mm] Nozzle throat diameter
	public double eps = 2.6951;  //[-] Nozzle ezpansion raitio
	//==========================================================================



	//wind======================================================================
	public boolean Wind_file_exsit = false; // true or false
	public String wind_file;
	public int WindModel   = 1;  // wind model

	public double Cdv      = 6.0;   //coefficient []
									/**
									 * coefficient [-]
									 * in case of WindModel = 1
									 * */

	public double wind_azimuth =  360.0;
									/** [deg]azimuth of wind
									 * 0 deg : from west to east
									 * 270 deg: from north to south
									 * */
	public double wind_speed   = 1.0; 	 //wind speed [m/s];
	public double Zr     = 5.0; 		 //altitude anemometer located [m]
	public double temperture0  = 15.0; //temperture at 0 m [℃]
	//==========================================================================


	//wind map configulation============================
	//落下分散を出すときのみ使用
	public double speed_min = 1.0; //[m/s]minimun wind speed
	public double speed_step = 1.0; //[m/s]風速の刻み幅
	public int speed_num = 8;   //何風速分計算したいか
	public int angle_num = 8;
	 							/*何風向分知りたいか
	 							 * 基本4の倍数で入力
	 							 * **/
	//=========================================

	//simulation================================================================
	public int Mode = 1;
	public int     n = 400000; //maximum number of simulation steps
	public double dt = 0.001;   // [s] メインソルバの時間ステップ
	public double dt_output = 0.01;//[s]logデータの時間ステップ
	public String  thrustcurve = "C:\\Users\\zoooi\\Documents\\STEP\\機体班\\シュミレーション\\thrust.csv"; //推力データのアドレス
	public String result_filepath = "C://Users/zoooi/Documents/STEP/機体班/シュミレーション/";//出力先のファイルパス
	//public String filipath ;
	//==========================================================================



	//======================↑input information↑=================================


}
