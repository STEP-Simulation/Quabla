package quabla.output;

import java.io.IOException;

import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.event_value.EventValueSingle;
import quabla.simulator.numerical_analysis.Interpolation;

/**
 * OutputFlightlogTrajectory makes csv file and writes value about flightlog(e.g. time, position, velocity, etc.).
 * */
public class OutputFlightlogTrajectory {

	/* 出力するもの
	 * 時間 time,
	 * 位置局所水平座標軸成分x_ENU,
	 * 位置局所水平座標軸成分y_ENU,
	 * 位置局所水平座標軸成分z_ENU,
	 * 対地速度局所水平座標軸成分Vel_x_ENU,
	 * 対地速度局所水平座標軸成分Vel_y_ENU,
	 * 対地速度局所水平座標軸成分Vel_z_ENU,
	 * 角速度機体軸成分 p
	 * 角速度機体軸成分 q
	 * 角速度機体軸成分 r
	 * クォータニオン q0
	 * クォータニオン q1
	 * クォータニオン q2
	 * クォータニオン q3
	 * 全機質量 m
	 * 燃料質量 mFuel
	 * 酸化剤質量 mOx
	 * 推進剤質量 mProp
	 * 高さalttitude
	 * 水平距離downrange
	 * 対気速度絶対値Vel_air_abs
	 * マッハ数Mach
	 * 迎角alpha
	 * 横滑り角beta
	 * 方位角azimuth
	 * 仰角elevation
	 * ロール角roll
	 * 重心位置Lcg
	 * 圧力中心位置Lcp
	 * 全長安定比Fst
	 * 動圧dynamics_pressure
	 * 抗力drag
	 * 法線力normal
	 * 横力side
	 * 推力thrust
	 * 力Force_x_Body
	 * 力Force_y_Body
	 * 力Force_z_Body
	 * 加速度局所水平座標軸成分Acc_x_ENU
	 * 加速度局所水平座標軸成分Acc_y_ENU
	 * 加速度局所水平座標軸成分Acc_z_ENU
	 * 加速度機体軸成分Acc_x_Body
	 * 加速度機体軸成分Acc_y_Body
	 * 加速度機体軸成分Acc_z_Body
	 * 加速度絶対値Acc_abs
	 * */
/*
	private String filepath;
	private InputParam spec;
	private LoggerVariable lv;
	private LoggerOtherVariableTrajectory lov;*/

	private Interpolation
	posENUanaly,
	velENUanaly,
	omegaBODYanaly,
	quatAnaly,
	attitudeAnaly,
	massAnaly,
	massFuelAnaly,
	massOxAnaly,
	massPropAnaly,
	lcgAnaly,
	lcgFuelAnaly,
	lcgOxAnaly,
	lcgPropAnaly,
	lcpAnaly,
	IjRollAnaly,
	IjPitchAnaly,
	altitudeAnaly,
	downrangeAnaly,
	velAirENUanaly,
	velAirBODYanaly,
	velAirAbsAnaly,
	alphaAnaly,
	betaAnaly,
	machAnaly,
	dynamicsPressureAnaly,
	fstAnaly,
	dragAnaly,
	normalAnaly,
	sideAnaly,
	thrustAnaly,
	forceBODYanaly,
	accENUanaly,
	accBODYanaly,
	accAbsAnaly,
	momentAeroAnaly,
	momentAeroDampingAnaly,
	momentJetDampingAnaly,
	momentGyroAnaly,
	momentAnaly,
	pAirAnaly;

	private double timeLandingTrajectory;

	private final double TIME_STEP_OUTPUT = 0.01;

	private final String[] nameList = {
			"time [sec]",
			"pos_east [m]",
			"pos_north [m]",
			"pos_up [m]",
			"vel_east [m/s]",
			"vel_north [m/s]",
			"vel_up [m/s]",
			"omega_roll [rad/s]",
			"omega_pitch [rad/s]",
			"omega_yaw [rad/s]",
			"quat0",
			"quat1",
			"quat2",
			"quat3",
			"yaw [deg]",
			"pitch [deg]",
			"roll [deg]",
			"Mass [kg]",
			"Mass_fuel [kg]",
			"Mass_ox [kg]",
			"Mass_prop [kg]",
			"Lcg [m]",
			"Lcg_fuel [m]",
			"Lcg_ox [m]",
			"Lcg_prop [m]",
			"Lcp [m]",
			"Ij_roll [kg m2]",
			"Ij_pitch [kg m2]",
			"Altitude [km]",
			"Downrange [km]",
			"vel_air_east [m/s]",
			"vel_air_north [m/s]",
			"vel_air_up [m/s]",
			"vel_air_BODY_x [m/s]",
			"vel_air_BODY_y [m/s]",
			"vel_air_BODY_z [m/s]",
			"vel_air_abs [m/s]",
			"alpha [deg]",
			"beta [deg]",
			"Mach",
			"Dynamics Pressure [kPa]",
			"Fst",
			"Drag [N]",
			"Normal Force [N]",
			"Side Force [N]",
			"Thrust [N]",
			"Force_BODY_x [N]",
			"Force_BODY_y [N]",
			"Force_BODY_z [N]",
			"Acc_east [m/s2]",
			"Acc_north [m/s2]",
			"Acc_up [m/s2]",
			"Acc_BODY_x [m/s2]",
			"Acc_BODY_y [m/s2]",
			"Acc_BODY_z [m/s2]",
			"Acc_abs [m/s2]",
			"moment_aero_x [N*m]",
			"moment_aero_y [N*m]",
			"moment_aero_z [N*m]",
			"moment_aero_damping_x [N*m]",
			"moment_aero_damping_y [N*m]",
			"moment_aero_damping_z [N*m]",
			"moment_jet_damping_x [N*m]",
			"moment_jet_damping_y [N*m]",
			"moment_jet_damping_z [N*m]",
			"moment_gyro_x [N*m]",
			"moment_gyro_y [N*m]",
			"moment_gyro_z [N*m]",
			"moment_x [N*m]",
			"moment_y [N*m]",
			"moment_z [N*m]",
			"Atomospheric Pressure [Pa]"
	};

	/**
	 * @param filepath 出力先のファイルパス
	 * @throws IOException
	 * */
	public OutputFlightlogTrajectory(LoggerVariable lv, EventValueSingle ivs) {
		timeLandingTrajectory = ivs.getTimeLandingTrajectory();

		posENUanaly = new Interpolation(lv.getTimeArray(), lv.getPosENUArray());
		velENUanaly = new Interpolation(lv.getTimeArray(), lv.getVelENUArray());
		omegaBODYanaly = new Interpolation(lv.getTimeArray(), lv.getOmegaBODYArray());
		quatAnaly = new Interpolation(lv.getTimeArray(), lv.getQuatArray());
		attitudeAnaly = new Interpolation(lv.getTimeArray(), lv.getAttitudeLogArray());
		massAnaly = new Interpolation(lv.getTimeArray(), lv.getMassLogArray());
		massFuelAnaly = new Interpolation(lv.getTimeArray(), lv.getMassFuelLogArray());
		massOxAnaly = new Interpolation(lv.getTimeArray(), lv.getMassOxLogArray());
		massPropAnaly = new Interpolation(lv.getTimeArray(), lv.getMassPropLogArray());
		lcgAnaly = new Interpolation(lv.getTimeArray(), lv.getLcgLogArray());
		lcgFuelAnaly = new Interpolation(lv.getTimeArray(), lv.getLcgFuelLogArray());
		lcgOxAnaly = new Interpolation(lv.getTimeArray(), lv.getLcgOxLogArray());
		lcgPropAnaly = new Interpolation(lv.getTimeArray(), lv.getLcgPropLogArray());
		lcpAnaly = new Interpolation(lv.getTimeArray(), lv.getLcpLogArray());
		IjRollAnaly = new Interpolation(lv.getTimeArray(), lv.getIjRollLogArray());
		IjPitchAnaly = new Interpolation(lv.getTimeArray(), lv.getIjPitchLogArray());
		altitudeAnaly = new Interpolation(lv.getTimeArray(), lv.getAltitudeLogArray());
		downrangeAnaly = new Interpolation(lv.getTimeArray(), lv.getDownrangeLogArray());
		velAirENUanaly = new Interpolation(lv.getTimeArray(), lv.getVelAirENUlogArray());
		velAirBODYanaly = new Interpolation(lv.getTimeArray(), lv.getVelAirBODYlogArray());
		velAirAbsAnaly = new Interpolation(lv.getTimeArray(), lv.getVelAirAbsLogArray());
		alphaAnaly = new Interpolation(lv.getTimeArray(), lv.getAlphaLogArray());
		betaAnaly = new Interpolation(lv.getTimeArray(), lv.getBetaLogArray());
		machAnaly = new Interpolation(lv.getTimeArray(), lv.getMachLogArray());
		dynamicsPressureAnaly = new Interpolation(lv.getTimeArray(), lv.getDynamicsPressureLogArray());
		fstAnaly = new Interpolation(lv.getTimeArray(), lv.getFstLogArray());
		dragAnaly = new Interpolation(lv.getTimeArray(), lv.getDragLogArray());
		normalAnaly = new Interpolation(lv.getTimeArray(), lv.getNormalLogArray());
		sideAnaly = new Interpolation(lv.getTimeArray(), lv.getSideLogArray());
		thrustAnaly = new Interpolation(lv.getTimeArray(), lv.getThrustLogArray());
		forceBODYanaly = new Interpolation(lv.getTimeArray(), lv.getForceBODYlogArray());
		accENUanaly = new Interpolation(lv.getTimeArray(), lv.getAccENUlogArray());
		accBODYanaly = new Interpolation(lv.getTimeArray(), lv.getAccBODYlogArray());
		accAbsAnaly = new Interpolation(lv.getTimeArray(), lv.getAccAbsLogArray());
		momentAeroAnaly = new Interpolation(lv.getTimeArray(), lv.getMomentAeroLogArray());
		momentAeroDampingAnaly = new Interpolation(lv.getTimeArray(), lv.getMomentAeroDamipingLogArray());
		momentJetDampingAnaly = new Interpolation(lv.getTimeArray(), lv.getMomentJetDampingLogArray());
		momentGyroAnaly = new Interpolation(lv.getTimeArray(), lv.getMomentGyroLogArray());
		momentAnaly = new Interpolation(lv.getTimeArray(), lv.getMomentLogArray());
		pAirAnaly = new Interpolation(lv.getTimeArray(), lv.getPairLogArray());
	}

	public void runOutputLine(String filepath) {
		OutputCsv flightlog = null;

		try {
			flightlog = new OutputCsv(filepath, nameList);
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		//1行目(変数名)の書き込み
		try {
			flightlog.outputFirstLine();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		double time;

		for(int i = 0; ; i++) {
			time = i * TIME_STEP_OUTPUT;

			double[] result = new double[nameList.length];

			result[0] = time;
			System.arraycopy(posENUanaly.linearInterpPluralColumns(time), 0, result, 1, 3);
			System.arraycopy(velENUanaly.linearInterpPluralColumns(time), 0, result, 4, 3);
			System.arraycopy(omegaBODYanaly.linearInterpPluralColumns(time), 0, result, 7, 3);
			System.arraycopy(quatAnaly.linearInterpPluralColumns(time), 0, result, 10, 4);
			System.arraycopy(attitudeAnaly.linearInterpPluralColumns(time), 0, result, 14, 3);
			result[17] = massAnaly.linearInterp1column(time);
			result[18] = massFuelAnaly.linearInterp1column(time);
			result[19] = massOxAnaly.linearInterp1column(time);
			result[20] = massPropAnaly.linearInterp1column(time);
			result[21] = lcgAnaly.linearInterp1column(time);
			result[22] = lcgFuelAnaly.linearInterp1column(time);
			result[23] = lcgOxAnaly.linearInterp1column(time);
			result[24] = lcgPropAnaly.linearInterp1column(time);
			result[25] = lcpAnaly.linearInterp1column(time);
			result[26] = IjRollAnaly.linearInterp1column(time);
			result[27] = IjPitchAnaly.linearInterp1column(time);
			result[28] = altitudeAnaly.linearInterp1column(time);
			result[29] = downrangeAnaly.linearInterp1column(time);
			System.arraycopy(velAirENUanaly.linearInterpPluralColumns(time), 0, result, 30, 3);
			System.arraycopy(velAirBODYanaly.linearInterpPluralColumns(time), 0, result, 33, 3);
			result[36] = velAirAbsAnaly.linearInterp1column(time);
			result[37] = alphaAnaly.linearInterp1column(time);
			result[38] = betaAnaly.linearInterp1column(time);
			result[39] = machAnaly.linearInterp1column(time);
			result[40] = dynamicsPressureAnaly.linearInterp1column(time);
			result[41] = fstAnaly.linearInterp1column(time);
			result[42] = dragAnaly.linearInterp1column(time);
			result[43] = normalAnaly.linearInterp1column(time);
			result[44] = sideAnaly.linearInterp1column(time);
			result[45] = thrustAnaly.linearInterp1column(time);
			System.arraycopy(forceBODYanaly.linearInterpPluralColumns(time), 0, result, 46, 3);
			System.arraycopy(accENUanaly.linearInterpPluralColumns(time), 0, result, 49, 3);
			System.arraycopy(accBODYanaly.linearInterpPluralColumns(time), 0, result, 52, 3);
			result[55] = accAbsAnaly.linearInterp1column(time);
			System.arraycopy(momentAeroAnaly.linearInterpPluralColumns(time), 0, result, 56, 3);
			System.arraycopy(momentAeroDampingAnaly.linearInterpPluralColumns(time), 0, result, 59, 3);
			System.arraycopy(momentJetDampingAnaly.linearInterpPluralColumns(time), 0, result, 62, 3);
			System.arraycopy(momentGyroAnaly.linearInterpPluralColumns(time), 0, result, 65, 3);
			System.arraycopy(momentAnaly.linearInterpPluralColumns(time), 0, result, 68, 3);
			result[71] = pAirAnaly.linearInterp1column(time);

			try {
				flightlog.outputLine(result);
			}catch(IOException e) {
				throw new RuntimeException(e);
			}
			if(time >= timeLandingTrajectory) {
				break;
			}
		}


		//ファイルのクローズ
		try {
			flightlog.close();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

}
