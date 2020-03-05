package quabla.output;

import java.io.IOException;

import quabla.parameter.InputParam;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.ivent_value.IventValueSingle;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableTrajectory;
import quabla.simulator.numerical_analysis.Interpolation;

public class OutputFlightlogTrajectory {

	/**出力するもの
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
	 * 質量 m
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
	lcgAnaly,
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
			"Lcg [m]",
			"Lcg_prop [m]",
			"Lcp [m]",
			"Ij_roll [kg m2]",
			"Ij_pitch [kg m2]",
			"Altitude [km]",
			"downrange [km]",
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
			"Atomospheric Pressure [Pa]"
	};

	/**
	 * @param filepath 出力先のファイルパス
	 * @throws IOException
	 * */
	public OutputFlightlogTrajectory(InputParam spec, LoggerVariable lv, LoggerOtherVariableTrajectory lov,IventValueSingle ivs) {
		timeLandingTrajectory = ivs.getTimeLandingTrajectory();

		posENUanaly = new Interpolation(lv.getTimeArray(), lv.getPosENUArray());
		velENUanaly = new Interpolation(lv.getTimeArray(), lv.getVelENUArray());
		omegaBODYanaly = new Interpolation(lv.getTimeArray(), lv.getOmegaBODYArray());
		quatAnaly = new Interpolation(lv.getTimeArray(), lv.getQuatArray());
		attitudeAnaly = new Interpolation(lv.getTimeArray(), lov.getAttitudeLogArray());
		massAnaly = new Interpolation(lv.getTimeArray(), lov.getMassLogArray());
		lcgAnaly = new Interpolation(lv.getTimeArray(), lov.getLcgLogArray());
		lcgPropAnaly = new Interpolation(lv.getTimeArray(), lov.getLcgPropLogArray());
		lcpAnaly = new Interpolation(lv.getTimeArray(), lov.getLcpLogArray());
		IjRollAnaly = new Interpolation(lv.getTimeArray(), lov.getIjRollLogArray());
		IjPitchAnaly = new Interpolation(lv.getTimeArray(), lov.getIjPitchLogArray());
		altitudeAnaly = new Interpolation(lv.getTimeArray(), lov.getAltitudeLogArray());
		downrangeAnaly = new Interpolation(lv.getTimeArray(), lov.getDownrangeLogArray());
		velAirENUanaly = new Interpolation(lv.getTimeArray(), lov.getVelAirENUlogArray());
		velAirBODYanaly = new Interpolation(lv.getTimeArray(), lov.getVelAirBODYlogArray());
		velAirAbsAnaly = new Interpolation(lv.getTimeArray(), lov.getVelAirAbsLogArray());
		alphaAnaly = new Interpolation(lv.getTimeArray(), lov.getAlphaLogArray());
		betaAnaly = new Interpolation(lv.getTimeArray(), lov.getBetaLogArray());
		machAnaly = new Interpolation(lv.getTimeArray(), lov.getMachLogArray());
		dynamicsPressureAnaly = new Interpolation(lv.getTimeArray(), lov.getDynamicsPressureLogArray());
		fstAnaly = new Interpolation(lv.getTimeArray(), lov.getFstLogArray());
		dragAnaly = new Interpolation(lv.getTimeArray(), lov.getDragLogArray());
		normalAnaly = new Interpolation(lv.getTimeArray(), lov.getDownrangeLogArray());
		sideAnaly = new Interpolation(lv.getTimeArray(), lov.getSideLogArray());
		thrustAnaly = new Interpolation(lv.getTimeArray(), lov.getThrustLogArray());
		forceBODYanaly = new Interpolation(lv.getTimeArray(), lov.getForceBODYlogArray());
		accENUanaly = new Interpolation(lv.getTimeArray(), lov.getAccENUlogArray());
		accBODYanaly = new Interpolation(lv.getTimeArray(), lov.getAccBODYlogArray());
		accAbsAnaly = new Interpolation(lv.getTimeArray(), lov.getAccAbsLogArray());
		pAirAnaly = new Interpolation(lv.getTimeArray(), lov.getPairLogArray());
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
			result[18] = lcgAnaly.linearInterp1column(time);
			result[19] = lcgPropAnaly.linearInterp1column(time);
			result[20] = lcpAnaly.linearInterp1column(time);
			result[21] = IjRollAnaly.linearInterp1column(time);
			result[22] = IjPitchAnaly.linearInterp1column(time);
			result[23] = altitudeAnaly.linearInterp1column(time);
			result[24] = downrangeAnaly.linearInterp1column(time);
			System.arraycopy(velAirENUanaly.linearInterpPluralColumns(time), 0, result, 25, 3);
			System.arraycopy(velAirBODYanaly.linearInterpPluralColumns(time), 0, result, 28, 3);
			result[31] = velAirAbsAnaly.linearInterp1column(time);
			result[32] = alphaAnaly.linearInterp1column(time);
			result[33] = betaAnaly.linearInterp1column(time);
			result[34] = machAnaly.linearInterp1column(time);
			result[35] = dynamicsPressureAnaly.linearInterp1column(time);
			result[36] = fstAnaly.linearInterp1column(time);
			result[37] = dragAnaly.linearInterp1column(time);
			result[38] = normalAnaly.linearInterp1column(time);
			result[39] = sideAnaly.linearInterp1column(time);
			result[40] = thrustAnaly.linearInterp1column(time);
			System.arraycopy(forceBODYanaly.linearInterpPluralColumns(time), 0, result, 41, 3);
			System.arraycopy(accENUanaly.linearInterpPluralColumns(time), 0, result, 44, 3);
			System.arraycopy(accBODYanaly.linearInterpPluralColumns(time), 0, result, 47, 3);
			result[50] = accAbsAnaly.linearInterp1column(time);
			result[51] = pAirAnaly.linearInterp1column(time);

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
