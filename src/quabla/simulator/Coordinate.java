package quabla.simulator;

public class Coordinate {

	/**
	 * ----------Caution!!-----------
	 * ----↓define quaternion ↓----
	 * q = q0*1 + q1*i + q2*j + q3*k
	 * ----↑define quaternion ↑----
	 * */

	public static double[][] quat2DCM_ENU2Body(double quat[]) {
		double DCM_ENU2Body[][] = new double[3][3];
		double q0,q1,q2,q3;
		q0 = quat[0];
		q1 = quat[1];
		q2 = quat[2];
		q3 = quat[3];

		DCM_ENU2Body[0][0] = q0*q0 + q1*q1 - q2*q2 - q3*q3 ;
		DCM_ENU2Body[0][1] = 2*(q1*q2 + q0*q3);
		DCM_ENU2Body[0][2] = 2*(q1*q3 - q0*q2);
		DCM_ENU2Body[1][0] = 2*(q1*q2 - q0*q3);
		DCM_ENU2Body[1][1] = q0*q0 - q1*q1 + q2*q2 - q3*q3 ;
		DCM_ENU2Body[1][2] = 2*(q2*q3 + q0*q1);
		DCM_ENU2Body[2][0] = 2*(q1*q3 + q0*q2);
		DCM_ENU2Body[2][1] = 2*(q2*q3 - q0*q1);
		DCM_ENU2Body[2][2] = q0*q0 - q1*q1 - q2*q2 + q3*q3 ;

		return DCM_ENU2Body;
	}



	public static double[] quat_nomalization(double quat[]) {
		double norm;
		double quat_nomalized[] = new double[4];

		norm = Math.sqrt(quat[0]*quat[0] + quat[1]*quat[1] + quat[2]*quat[2] + quat[3]*quat[3]) ;
		for(int i = 0; i<4; i++) {
			quat_nomalized[i] = quat[i] / norm;
		}

		return quat_nomalized;
	}



	public static double[][] Omega_tensor(double p, double q , double r){
		double tensor[][] = new double[4][4];
		double omega_Body[] = new double[3];

		omega_Body[0] = p;
		omega_Body[1] = q;
		omega_Body[2] = r;

		for(int i = 0 ; i<4 ; i++) {
			tensor[i][i] = 0.0;
		}

		for(int i = 0 ; i<3 ; i++) {
			tensor[i+1][0] = omega_Body[i];
			tensor[0][i+1] = - omega_Body[i];
		}

		tensor[1][2] = r;
		tensor[2][1] = -r;
		tensor[3][1] = q;
		tensor[1][3] = -q;
		tensor[2][3] = p;
		tensor[3][2] = -p;

		return tensor;
	}




	public static double[][] DCM_ENU2Body2DCM_Body2_ENU(double DCM_ENU2Body[][]){
		double DCM_Body2ENU[][] = new double[3][3];

		for(int i = 0 ; i<3 ; i++) {
			for(int j = 0 ; j<3 ; j++) {
				DCM_Body2ENU[i][j] = DCM_ENU2Body[j][i] ;
			}
		}

		return DCM_Body2ENU;
	}



	public static double[] euler2quat(double azimuth , double elevation , double roll) {
		double DCM[][] = new double[3][3];
		double quat[] = new double[4];
		double quat_max = 0.0;
		int count = 0;

		DCM = euler2DCM_ENU2Body(azimuth , elevation , roll);
		quat[0] = 0.5*Math.sqrt(1 + DCM[0][0] + DCM[1][1] + DCM[2][2]);
		quat[1] = 0.5*Math.sqrt(1 + DCM[0][0] - DCM[1][1] - DCM[2][2]);
		quat[2] = 0.5*Math.sqrt(1 - DCM[0][0] + DCM[1][1] - DCM[2][2]);
		quat[3] = 0.5*Math.sqrt(1 - DCM[0][0] - DCM[1][1] + DCM[2][2]);


		for(int i = 0 ; i<4 ; i++) {
			if(quat[i] > quat_max) {
				quat_max = quat[i];
				count = i;
			}
		}

		switch(count) {
		case 0:
			quat[0] = 0.5*Math.sqrt(1 + DCM[0][0] + DCM[1][1] + DCM[2][2]);
			quat[1] = (DCM[1][2] - DCM[2][1]) / (4.0*quat[0]);
			quat[2] = (DCM[2][0] - DCM[0][2]) / (4.0*quat[0]);
			quat[3] = (DCM[0][1] - DCM[1][0]) / (4.0*quat[0]);
			break;
		case 1:
			quat[1] = 0.5*Math.sqrt(1 + DCM[0][0] - DCM[1][1] - DCM[2][2]);
			quat[0] = (DCM[1][2] - DCM[2][1]) / (4.0*quat[1]);
			quat[2] = (DCM[0][1] + DCM[1][0]) / (4.0*quat[1]);
			quat[3] = (DCM[2][0] + DCM[0][2]) / (4.0*quat[1]);
			break;
		case 2:
			quat[2] = 0.5*Math.sqrt(1 - DCM[0][0] + DCM[1][1] - DCM[2][2]);
			quat[0] = (DCM[2][0] - DCM[0][2]) / (4.0*quat[2]);
			quat[1] = (DCM[0][1] + DCM[1][0]) / (4.0*quat[2]);
			quat[3] = (DCM[1][2] + DCM[2][1]) / (4.0*quat[2]);
			break;
		case 3:
			quat[3] = 0.5*Math.sqrt(1 - DCM[0][0] - DCM[1][1] + DCM[2][2]);
			quat[0] = (DCM[0][1] - DCM[1][0]) / (4.0*quat[3]);
			quat[1] = (DCM[2][0] + DCM[0][2]) / (4.0*quat[3]);
			quat[2] = (DCM[1][2] + DCM[2][1]) / (4.0*quat[3]);
			break;
		}

		return quat;
	}




	public static double[][] euler2DCM_ENU2Body(double azimuth, double elevation , double roll){
		double DCM[][] = new double[3][3];
		double DCM_ENU2Body[][] = new double[3][3];

		DCM[0][0] = Math.cos(azimuth)*Math.cos(elevation);
		DCM[0][1] = Math.sin(azimuth)*Math.cos(elevation);
		DCM[0][2] = - Math.sin(elevation);
		DCM[1][0] = Math.cos(azimuth)*Math.sin(elevation)*Math.sin(roll) - Math.sin(azimuth)*Math.cos(roll);
		DCM[1][1] = Math.sin(azimuth)*Math.sin(elevation)*Math.sin(roll) + Math.cos(azimuth)*Math.cos(roll);
		DCM[1][2] = Math.cos(elevation)*Math.sin(roll);
		DCM[2][0] = Math.cos(azimuth)*Math.sin(elevation)*Math.cos(roll) + Math.sin(azimuth)*Math.sin(roll);
		DCM[2][1] = Math.sin(azimuth)*Math.sin(elevation)*Math.cos(roll) - Math.cos(azimuth)*Math.sin(roll);
		DCM[2][2] = Math.cos(elevation)*Math.cos(roll);

		for(int i = 0 ; i<3 ; i++) {
			for(int j = 0 ; j<3 ; j++) {
				DCM_ENU2Body[i][j] = DCM[i][j];
			}
		}

		return DCM_ENU2Body;
	}



	public static double[] DCM2euler(double DCM[][]) {
		double azimuth , elevation , roll;//[rad]
		double euler[] = new double[3];//euler angle

		azimuth = Math.atan2(DCM[0][1], DCM[0][0]);
		elevation = Math.asin(-DCM[0][2]);
		roll = Math.atan2(DCM[1][2], DCM[2][2]);

		euler[0] = azimuth;
		euler[1] = elevation;
		euler[2] = roll;

		for(int i = 0 ; i<3 ; i++) {
			euler[i] = rad2deg(euler[i]);
		}

		return euler; //[deg]で返す

	}

	public static double rad2deg(double radian) {
		double degree;

		degree = radian * 180.0 / Math.PI;

		return degree;
	}

	public static double deg2rad(double degree) {
		double radian;

		radian = degree * Math.PI / 180.0;

		return radian;
	}

	public static double[] vec_trans(double Mat[][], double vec[]) {
		int length = vec.length;
		double vec_conversioned[] = new double[length];
		for(int i = 0; i<length; i++) {
			vec_conversioned[i] = 0.0;
		}

		for(int i=0; i < length; i++) {
			for(int j=0; j < length; j++) {
				vec_conversioned[i] += Mat[i][j] * vec[j];
			}
		}

		return vec_conversioned;
	}

}
