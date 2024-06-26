package quabla.simulator;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import quabla.simulator.rocket.Rocket;

public class ENUtoLLH {

    //a : [m] radius at
    static double a = 6378137.0;
    //f : oblateness
    static double f = 1.0 / 298.257223563;
    //e_square : sq8are of eccentricit y
    static double e_square = 2.0*f - Math.pow(f, 2);
    static double b = a * (1.0 - f);

//    public static void main(String[] args){
//    	double a[] = {34.679730, 139.438373, 0.0};
//    	double b[] = {100, 100, 0};
//    	//int num = 2;
//    	double c[] = ENU2LLH(b);
//    	for(int i = 0; i < 3; i++) {
//    		System.out.println(c[i]);
//    	}
//    }


	public static double[] LLH2ECEF(double LLH[]){
		double lat, lon, height;
	    lat = Math.toRadians(LLH[0]); //rad
	    lon = Math.toRadians(LLH[1]); //rad
	    height = LLH[2]; //m

	    double N = a / Math.sqrt(1 - e_square*(Math.pow(Math.sin(lat),2)));

	    double x = (N + height) * Math.cos(lat) * Math.cos(lon);
	    double y = (N + height) * Math.cos(lat) * Math.sin(lon);
	    double z = (N*(1.0 - e_square) + height) * Math.sin(lat);
	    double ECEF[] = {x, y, z};

	    return ECEF;
	}


	public static double[] ECEF2LLH(double ECEF[]) {
	    double x = ECEF[0];
	    double y = ECEF[1];
	    double z = ECEF[2];

	    double p = Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));

	    double edash_square = e_square * (Math.pow(a,2) / Math.pow(b,2));
	    double theta = Math.atan2(z*a, p*b);

	    double lat = Math.atan2(z + edash_square*b*Math.pow(Math.sin(theta),3), p - e_square*a*Math.pow(Math.cos(theta),3));
	    double lon = Math.atan2(y, x);
	    double N = a / Math.sqrt(1 - e_square*(Math.pow(Math.sin(lat),2)));
	    double height = p/Math.cos(lat) - N;

	    double LLH[] = {Math.toDegrees(lat), Math.toDegrees(lon), height}; //rad
	    return LLH;
	}

	public static double[][] ECEF2NED(double launch_LLH[]) {
	    //lat, lon, height
	    double lat = launch_LLH[0]; //rad
	    double lon = launch_LLH[1]; //rad

	    double DCM_ECEF2NED[][] = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
	    DCM_ECEF2NED[0][0] = -Math.cos(lon)*Math.sin(lat);
	    DCM_ECEF2NED[0][1] = -Math.sin(lon)*Math.sin(lat);
	    DCM_ECEF2NED[0][2] = Math.cos(lat);
	    DCM_ECEF2NED[1][0] = -Math.sin(lon);
	    DCM_ECEF2NED[1][1] = Math.cos(lon);
	    DCM_ECEF2NED[1][2] = 0.0;
	    DCM_ECEF2NED[2][0] = -Math.cos(lon)*Math.cos(lat);
	    DCM_ECEF2NED[2][1] = -Math.sin(lon)*Math.cos(lat);
	    DCM_ECEF2NED[2][2] = -Math.sin(lat);
	    return DCM_ECEF2NED;
	}

	public static double[] multiplicationOfMatrices(double[] a, double[][] b){

		double[] c = new double[3];

		for(int i=0; i<3; i++){
			double ret = 0.0;
			for(int j=0; j<3; j++){
				ret += a[j] * b[j][i];
			}
			c[i] = ret;
		}
		return c;
	}

	public static double[][] matrixTranspose(double[][] matrix){
		double[][] newMatrix = new double[3][3];

		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				newMatrix[j][i] = matrix[i][j];
			}
		}

		return newMatrix;
	}

	public static double[] ENU2LLH(double[] posNED) {

		String launchSiteInfo = "input" + File.separator + "launch_site.json";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = null;
		try {
			node = mapper.readTree(new File(launchSiteInfo));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		int launchsite = Rocket.site;
		JsonNode nodeSite = null;
		double launch_LLH[] = {0,0,0};
		switch (launchsite) {
		case 1:
			nodeSite = node.get("oshima_land");
			break;
		case 2:
			nodeSite = node.get("oshima_sea");
			break;
		case 3:
			nodeSite = node.get("noshiro_land");
			break;
		case 4:
			nodeSite = node.get("noshiro_sea");
			break;
		case 5:
			launch_LLH[0] = Rocket.point[0];
			launch_LLH[1] = Rocket.point[1];
			launch_LLH[1] = Rocket.point[2];
			break;
		}
		
		if (launchsite < 5) {
			for (int i = 0; i < 3; i++) {
				launch_LLH[i] = nodeSite.get("launch_LLH").get(i).asDouble();
			}
		}

	    // double north = posNED[0];
	    // double east  = posNED[1];
	    // double down  = posNED[2];

	    // double[] posNED = {north, east, down};

	    double lat = Math.toRadians(launch_LLH[0]);
	    double lon = Math.toRadians(launch_LLH[1]);
	    double height = launch_LLH[2];
	    double LLH[] = {lat, lon, height}; //rad rad m

	    double launch_ECEF[] = LLH2ECEF(launch_LLH);
	    double DCM_NED2ECEF[][] = ECEF2NED(LLH);
	    double multiple[] = multiplicationOfMatrices(posNED, DCM_NED2ECEF);
	    double point_ECEF[] = {multiple[0] + launch_ECEF[0], multiple[1] + launch_ECEF[1], multiple[2] + launch_ECEF[2]};

	    return ECEF2LLH(point_ECEF);
	}

}

