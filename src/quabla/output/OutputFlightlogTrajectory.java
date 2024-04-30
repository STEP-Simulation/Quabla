package quabla.output;

import java.io.IOException;

import quabla.simulator.logger.LoggerVariable;

/**
 * OutputFlightlogTrajectory makes csv file and writes value about flightlog(e.g. time, position, velocity, etc.).
 * */
public class OutputFlightlogTrajectory {

	private double[]   timeArray;
	private double[]   timeStepArray;
	private double[][] posNEDArray;
	private double[][] velNEDArray;
	private double[][] velBODYArray;
	private double[][] omegaBODYArray;
	private double[][] quatArray;
	private double[][] attitudeArray;
	private double[]   massArray;
	private double[]   massFuelArray;
	private double[]   massOxArray;
	private double[]   massPropArray;
	private double[]   lcgArray;
	private double[]   lcgFuelArray;
	private double[]   lcgOxArray;
	private double[]   lcgPropArray;
	private double[]   lcpArray;
	private double[]   IjRollArray;
	private double[]   IjPitchArray;
	private double[]   CdArray;
	private double[]   CNaArray;
	private double[]   altitudeArray;
	private double[]   downrangeArray;
	private double[][] velAirNEDArray;
	private double[][] velAirBODYArray;
	private double[]   velAirAbsArray;
	private double[]   alphaArray;
	private double[]   betaArray;
	private double[]   machArray;
	private double[]   dynamicsPressureArray;
	private double[]   fstArray;
	private double[]   dragArray;
	private double[]   normalArray;
	private double[]   sideArray;
	private double[]   thrustArray;
	private double[]   thrustMomentumArray;
	private double[]   thrustPressureArray;
	private double[][] forceBODYArray;
	private double[][] accENUArray;
	private double[][] accBODYArray;
	private double[]   accAbsArray;
	private double[][] momentAeroArray;
	private double[][] momentAeroDampingArray;
	private double[][] momentJetDampingArray;
	private double[][] momentGyroArray;
	private double[][] momentArray;
	private double[]   pAirArray;

	private int index;
	private double[] result;

	private final String[] nameList = {
			"time [sec]",
			"time_step [sec]",
			"pos_north [m]",
			"pos_east [m]",
			"pos_down [m]",
			"vel_north [m/s]",
			"vel_east [m/s]",
			"vel_down [m/s]",
			"vel_BODY_x [m/s]",
			"vel_BODY_y [m/s]",
			"vel_BODY_z [m/s]",
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
			"C_D [-]",
			"C_Na [1/rad]",
			"Altitude [km]",
			"Downrange [km]",
			"vel_air_north [m/s]",
			"vel_air_east [m/s]",
			"vel_air_down [m/s]",
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
			"Thrust Momentum [N]",
			"Thrust Pressure [N]",
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
			"Atmospheric Pressure [Pa]"
	};

	/**
	 * @param filepath 出力先のファイルパス
	 * @throws IOException
	 * */
	public OutputFlightlogTrajectory(LoggerVariable lv) {

		timeArray = lv.getTimeArray().clone();
		timeStepArray = lv.getTimeStepArray().clone();
		posNEDArray = lv.getPosNEDArray().clone();
		velNEDArray = lv.getVelNEDArray().clone();
		velBODYArray = lv.getVelBODYArray().clone();
		omegaBODYArray = lv.getOmegaBODYArray().clone();
		quatArray = lv.getQuatArray().clone();
		attitudeArray = lv.getAttitudeLogArray().clone();
		massArray = lv.getMassLogArray().clone();
		massFuelArray = lv.getMassFuelLogArray().clone();
		massOxArray = lv.getMassOxLogArray().clone();
		massPropArray = lv.getMassPropLogArray().clone();
		lcgArray = lv.getLcgLogArray().clone();
		lcgFuelArray = lv.getLcgFuelLogArray().clone();
		lcgOxArray = lv.getLcgOxLogArray().clone();
		lcgPropArray = lv.getLcgPropLogArray().clone();
		lcpArray = lv.getLcpLogArray().clone();
		IjRollArray = lv.getIjRollLogArray().clone();
		IjPitchArray = lv.getIjPitchLogArray().clone();
		CdArray = lv.getCdLog().clone();
		CNaArray = lv.getCNaLog().clone();
		altitudeArray = lv.getAltitudeLogArray().clone();
		downrangeArray = lv.getDownrangeLogArray().clone();
		velAirNEDArray = lv.getVelAirNEDlogArray().clone();
		velAirBODYArray = lv.getVelAirBODYlogArray().clone();
		velAirAbsArray = lv.getVelAirAbsLogArray().clone();
		alphaArray = lv.getAlphaLogArray().clone();
		betaArray = lv.getBetaLogArray().clone();
		machArray = lv.getMachLogArray().clone();
		dynamicsPressureArray = lv.getDynamicsPressureLogArray().clone();
		fstArray = lv.getFstLogArray().clone();
		dragArray = lv.getDragLogArray().clone();
		normalArray = lv.getNormalLogArray().clone();
		sideArray = lv.getSideLogArray().clone();
		thrustArray = lv.getThrustLogArray().clone();
		thrustMomentumArray = lv.getThrustMomentumLogArray();
		thrustPressureArray = lv.getThrustPressureLogArray();
		forceBODYArray = lv.getForceBODYlogArray().clone();
		accENUArray = lv.getAccENUlogArray().clone();
		accBODYArray = lv.getAccBODYlogArray().clone();
		accAbsArray = lv.getAccAbsLogArray().clone();
		momentAeroArray = lv.getMomentAeroLogArray().clone();
		momentAeroDampingArray = lv.getMomentAeroDamipingLogArray().clone();
		momentJetDampingArray = lv.getMomentJetDampingLogArray().clone();
		momentGyroArray = lv.getMomentGyroLogArray().clone();
		momentArray = lv.getMomentLogArray().clone();
		pAirArray = lv.getPairLogArray().clone();

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

		for(int i = 0; i < timeArray.length; i++) {

			result = new double[nameList.length];
			index = 0;

			storeResultArray(timeArray[i]);
			storeResultArray(timeStepArray[i]);
			storeResultArray(posNEDArray[i]);
			storeResultArray(velNEDArray[i]);
			storeResultArray(velBODYArray[i]);
			storeResultArray(omegaBODYArray[i]);
			storeResultArray(quatArray[i]);
			storeResultArray(attitudeArray[i]);
			storeResultArray(massArray[i]);
			storeResultArray(massFuelArray[i]);
			storeResultArray(massOxArray[i]);
			storeResultArray(massPropArray[i]);
			storeResultArray(lcgArray[i]);
			storeResultArray(lcgFuelArray[i]);
			storeResultArray(lcgOxArray[i]);
			storeResultArray(lcgPropArray[i]);
			storeResultArray(lcpArray[i]);
			storeResultArray(IjRollArray[i]);
			storeResultArray(IjPitchArray[i]);
			storeResultArray(CdArray[i]);
			storeResultArray(CNaArray[i]);
			storeResultArray(altitudeArray[i]);
			storeResultArray(downrangeArray[i]);
			storeResultArray(velAirNEDArray[i]);
			storeResultArray(velAirBODYArray[i]);
			storeResultArray(velAirAbsArray[i]);
			storeResultArray(alphaArray[i]);
			storeResultArray(betaArray[i]);
			storeResultArray(machArray[i]);
			storeResultArray(dynamicsPressureArray[i]);
			storeResultArray(fstArray[i]);
			storeResultArray(dragArray[i]);
			storeResultArray(normalArray[i]);
			storeResultArray(sideArray[i]);
			storeResultArray(thrustArray[i]);
			storeResultArray(thrustMomentumArray[i]);
			storeResultArray(thrustPressureArray[i]);
			storeResultArray(forceBODYArray[i]);
			storeResultArray(accENUArray[i]);
			storeResultArray(accBODYArray[i]);
			storeResultArray(accAbsArray[i]);
			storeResultArray(momentAeroArray[i]);
			storeResultArray(momentAeroDampingArray[i]);
			storeResultArray(momentJetDampingArray[i]);
			storeResultArray(momentGyroArray[i]);
			storeResultArray(momentArray[i]);
			storeResultArray(pAirArray[i]);


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

	private void storeResultArray(double var){

		result[index] = var;
		index ++;
	}

	private void storeResultArray(double[] var){

		System.arraycopy(var, 0, result, index, var.length);
		index += var.length;
	}
}
