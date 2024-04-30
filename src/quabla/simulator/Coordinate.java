package quabla.simulator;

/*
 * ----------Caution!!-----------
 * ----↓define quaternion ↓----
 * q = q0*1 + q1*i + q2*j + q3*k
 * ----↑define quaternion ↑----
 * */

/**
 * Coordinate deals with coordinate transformation
 * using DCM(Direct Cosine Matrix), euler angles(321 System) and quaternion.
 * */
public class Coordinate {


	/**
	 * @param quat
	 * @return dcm_ENU2BODY
	 * */
	public static double[][] getDcmNED2BODYfromQuat(double[] quat) {
		double q0,q1,q2,q3;
		q0 = quat[0];
		q1 = quat[1];
		q2 = quat[2];
		q3 = quat[3];
		
		double[][] dcmNED2BODY = {
			{q0*q0 + q1*q1 - q2*q2 - q3*q3, 2.*(q1*q2 + q0*q3)           , 2.*(q1*q3 - q0*q2)           }, 
			{2.*(q1*q2 - q0*q3)           , q0*q0 - q1*q1 + q2*q2 - q3*q3, 2.*(q2*q3 + q0*q1)           }, 
			{2.*(q1*q3 + q0*q2)           , 2.*(q2*q3 - q0*q1)           , q0*q0 - q1*q1 - q2*q2 + q3*q3}
		};

		return dcmNED2BODY;
	}



	public static double[] nomalizeQuat(double[] quat) {
		double norm;
		double quatNomalized[] = new double[4];

		norm = Math.sqrt(
			   quat[0]*quat[0] 
			 + quat[1]*quat[1] 
			 + quat[2]*quat[2] 
			 + quat[3]*quat[3] ) ;

		for(int i = 0; i<4; i++) {
			quatNomalized[i] = quat[i] / norm;
		}

		return quatNomalized;
	}


	/**
	 * This function makes tensor used in Kinematic ODE.
	 * */
	public static double[][] getOmegaTensor(double p, double q , double r){

		double[][] tensor = {
			{.0, -p, -q, -r},
			{ p, .0,  r, -q},
			{ q, -r, .0,  p},
			{ r,  q, -p, .0}
		};

		return tensor;
	}


	/**
	 * @param dcmNED2BODY
	 * @return dcm_BODY2ENU
	 * */
	public static double[][] getDcmBODY2NEDfromDcmNED2BODY(double[][] dcmNED2BODY){
		double dcmBODY2NED[][] = new double[3][3];

		for(int i = 0 ; i<3 ; i++) {
			for(int j = 0 ; j<3 ; j++) {
				dcmBODY2NED[i][j] = dcmNED2BODY[j][i] ;
			}
		}

		return dcmBODY2NED;
	}


	/**
	 * This function is used initial quaterion from launch azimuth, elevation and roll angle.
	 * @param azimuth [deg]
	 * @param elevation [deg]
	 * @param roll [deg]
	 * @return quat
	 * */
	public static double[] getQuatFromEuler(double azimuth, double elevation, double roll) {
		double dcm[][] = new double[3][3];
		double quatMax = 0.0;
		int count = 0;

		dcm = getDcmNED2BODYfromEuler(azimuth , elevation , roll);
		double[] quat = {
			.5*Math.sqrt(1. + dcm[0][0] + dcm[1][1] + dcm[2][2]),
			.5*Math.sqrt(1. + dcm[0][0] - dcm[1][1] - dcm[2][2]),
			.5*Math.sqrt(1. - dcm[0][0] + dcm[1][1] - dcm[2][2]),
			.5*Math.sqrt(1. - dcm[0][0] - dcm[1][1] + dcm[2][2])
		};

		for(int i = 0 ; i<4 ; i++) {
			if(quat[i] > quatMax) {
				quatMax = quat[i];
				count = i;
			}
		}

		switch(count) {
		case 0:
			quat[0] = 0.5*Math.sqrt(1 + dcm[0][0] + dcm[1][1] + dcm[2][2]);
			quat[1] = (dcm[1][2] - dcm[2][1]) / (4.0*quat[0]);
			quat[2] = (dcm[2][0] - dcm[0][2]) / (4.0*quat[0]);
			quat[3] = (dcm[0][1] - dcm[1][0]) / (4.0*quat[0]);
			break;
		case 1:
			quat[1] = 0.5*Math.sqrt(1 + dcm[0][0] - dcm[1][1] - dcm[2][2]);
			quat[0] = (dcm[1][2] - dcm[2][1]) / (4.0*quat[1]);
			quat[2] = (dcm[0][1] + dcm[1][0]) / (4.0*quat[1]);
			quat[3] = (dcm[2][0] + dcm[0][2]) / (4.0*quat[1]);
			break;
		case 2:
			quat[2] = 0.5*Math.sqrt(1 - dcm[0][0] + dcm[1][1] - dcm[2][2]);
			quat[0] = (dcm[2][0] - dcm[0][2]) / (4.0*quat[2]);
			quat[1] = (dcm[0][1] + dcm[1][0]) / (4.0*quat[2]);
			quat[3] = (dcm[1][2] + dcm[2][1]) / (4.0*quat[2]);
			break;
		case 3:
			quat[3] = 0.5*Math.sqrt(1 - dcm[0][0] - dcm[1][1] + dcm[2][2]);
			quat[0] = (dcm[0][1] - dcm[1][0]) / (4.0*quat[3]);
			quat[1] = (dcm[2][0] + dcm[0][2]) / (4.0*quat[3]);
			quat[2] = (dcm[1][2] + dcm[2][1]) / (4.0*quat[3]);
			break;
		}

		return quat;
	}


	/**
	 * @param azimuth [rad]
	 * @param elevation [rad]
	 * @param roll [rad]
	 * @return dcm_ENU2BODY
	 * */
	public static double[][] getDcmNED2BODYfromEuler(double azimuth, double elevation , double roll){

		double[][] dcm = {
			{Math.cos(azimuth)*Math.cos(elevation)                                                  , Math.sin(azimuth)*Math.cos(elevation)                                                  , - Math.sin(elevation)             },
			{Math.cos(azimuth)*Math.sin(elevation)*Math.sin(roll) - Math.sin(azimuth)*Math.cos(roll), Math.sin(azimuth)*Math.sin(elevation)*Math.sin(roll) + Math.cos(azimuth)*Math.cos(roll), Math.cos(elevation)*Math.sin(roll)}, 
			{Math.cos(azimuth)*Math.sin(elevation)*Math.cos(roll) + Math.sin(azimuth)*Math.sin(roll), Math.sin(azimuth)*Math.sin(elevation)*Math.cos(roll) - Math.cos(azimuth)*Math.sin(roll), Math.cos(elevation)*Math.cos(roll)}
		};

		return dcm;
	}


	/**
	 * This function calculate euler angles from DCM.
	 * This function is used to get result euler angles.
	 * @param dcm
	 * @return euler [deg]
	 * */
	public static double[] getEulerFromDCM(double dcm[][]) {
		double azimuth , elevation , roll;//[rad]
		double euler[] = new double[3];//euler angle

		azimuth   = Math.atan2(dcm[0][1], dcm[0][0]);
		elevation = Math.asin(-dcm[0][2]);
		roll      = Math.atan2(dcm[1][2], dcm[2][2]);

		euler[0] = azimuth;
		euler[1] = elevation;
		euler[2] = roll;

		for(int i = 0 ; i<3 ; i++) {
			euler[i] = rad2deg(euler[i]);
		}

		return euler; //[deg]で返す
	}


	public static double rad2deg(double radian) {
		return radian * 180.0 / Math.PI;
	}


	public static double deg2rad(double degree) {
		return degree * Math.PI / 180.0;
	}


	public static double[] transVector(double[][] mat, double[] vec) {
		int length = vec.length;
		double[] vecTrans = new double[length];

		for(int i = 0; i < length; i++) {
			vecTrans[i] = 0.0;

			for(int j=0; j < length; j++) {
				vecTrans[i] += mat[i][j] * vec[j];
			}
		}

		return vecTrans;
	}

}
