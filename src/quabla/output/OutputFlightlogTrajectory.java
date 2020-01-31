package quabla.output;

import java.io.IOException;

import quabla.parameter.InputParam;
import quabla.simulator.logger.LoggerVariable;
import quabla.simulator.logger.logger_other_variable.LoggerOtherVariableTrajectory;

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

	private String filepath;
	private InputParam spec;
	private LoggerVariable lv;
	private LoggerOtherVariableTrajectory lov;

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
			"Mass [kg]",
			"Lcg [m]",
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
			"Acc_abs [m/s2]"
	};

	/**
	 * @param filepath 出力先のファイルパス
	 * @throws IOException
	 * */
	public OutputFlightlogTrajectory(String filepath, InputParam spec, LoggerVariable lv, LoggerOtherVariableTrajectory lov) {
		this.filepath = filepath;
		this.spec = spec;
		this.lv = lv;
		this.lov = lov;
	}

	public void runOutputLine() {
		OutputCsv flightlog = null;
		try {
			flightlog = new OutputCsv(spec.result_filepath + filepath + ".csv", nameList);
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		//1行目(変数名)の書き込み
		try {
			flightlog.outputFirstLine();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}

		int length = lv.getArrayLength();
		for(int i = 0; i < length; i++) {
			double[] result = new double[nameList.length];

			result[0] = lv.getTime(i);
			System.arraycopy(lv.getPosENUlog(i), 0, result, 1, 3);
			System.arraycopy(lv.getVelENUlog(i), 0, result, 4, 3);
			System.arraycopy(lv.getOmegaBODYlog(i), 0, result, 7, 3);
			System.arraycopy(lv.getQuatLog(i), 0, result, 10, 4);
			result[14] = lov.getMassLog(i);
			result[15] = lov.getLcgLog(i);
			result[16] = lov.getLcpLog(i);
			result[17] = lov.getIjRollLog(i);
			result[18] = lov.getIjPitchLog(i);
			result[19] = lov.getAltitudeLog(i);
			result[20] = lov.getDownrangeLog(i);
			System.arraycopy(lov.getVelAirENUlog(i), 0, result, 21, 3);
			System.arraycopy(lov.getVelAirBODYlog(i), 0, result, 24, 3);
			result[27] = lov.getVelAirAbsLog(i);
			result[28] = lov.getAlphaLog(i);
			result[29] = lov.getBetaLog(i);
			result[30] = lov.getMachLog(i);
			result[31] = lov.getDynamicsPressureLog(i);
			result[32] = lov.getFstLog(i);
			result[33] = lov.getDragLog(i);
			result[34] = lov.getNormalLog(i);
			result[35] = lov.getSideLog(i);
			result[36] = lov.getThrustLog(i);
			System.arraycopy(lov.getForceBODYlog(i), 0, result, 37, 3);
			System.arraycopy(lov.getAccENUlog(i), 0, result, 40, 3);
			System.arraycopy(lov.getAccBODYlog(i), 0, result, 43, 3);
			result[46] = lov.getAccAbsLog(i);

			try {
				flightlog.outputLine(result);
			}catch(IOException e) {
				throw new RuntimeException(e);
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
