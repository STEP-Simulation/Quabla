package quabla.simulator.rocket;

import com.fasterxml.jackson.databind.JsonNode;

import quabla.simulator.GetCsv;
import quabla.simulator.numerical_analysis.Interpolation;

public class AeroParameter {

	private double lcpConst;
	private double CdConst, CNaConst;
	Interpolation lcpAnaly,CdAnaly,CNaAnaly;
	public double Clp, Cmq, Cnr;
	private final boolean lcpFileExist,CdFileExist,CNaFileExist;


	public AeroParameter(JsonNode aero){

		lcpFileExist = aero.get("Length-C.P. File").asBoolean();
		CdFileExist = aero.get("Cd File").asBoolean();
		CNaFileExist = aero.get("CNa File").asBoolean();

		if(lcpFileExist) {
			double[][] lcpData = GetCsv.get2ColumnArray(aero.get("Length-C.P. File").asText());
			double machArray[] = new double[lcpData.length];
			double lcpArray[] = new double[lcpData.length];
			for(int i = 0; i < lcpData.length ; i++) {
				machArray[i] = lcpData[i][0];
				lcpArray[i] = lcpData[i][1];
			}
			this.lcpAnaly = new Interpolation(machArray , lcpArray);//Mach_array,Lcp_arrayってインスタンス変数にする必要ある？
		}else {
			lcpConst = aero.get("Constant Length-C.P. from Nosetip [m]").asDouble();
		}


		if(CdFileExist) {
			double[][] CdData = GetCsv.get2ColumnArray(aero.get("Cd File").asText());
			double machArray[] = new double[CdData.length];
			double CdArray[] = new double[CdData.length];
			for(int i = 0 ; i < CdData.length ; i++) {
				machArray[i] = CdData[i][0];
				CdArray[i] = CdData[i][1];
			}
			this.CdAnaly = new Interpolation(machArray , CdArray);
		}else {
			this.CdConst = aero.get("Constant Cd").asDouble();
		}


		if(CNaFileExist) {
			double[][] CNaData = GetCsv.get2ColumnArray(aero.get("CNa File").asText());
			double machArray[] = new double[CNaData.length];
			double CNaArray[] = new double[CNaData.length];
			for(int i = 0; i < CNaData.length ; i++) {
				machArray[i] = CNaData[i][0];
				CNaArray[i] = CNaData[i][1];
			}
			this.CNaAnaly = new Interpolation(machArray , CNaArray);
		}else {
			this.CNaConst = aero.get("Constant CNa").asDouble();
		}

		this.Clp = aero.get("Roll Dumping Moment Coefficient Clp").asDouble();
		if(Clp > 0.0) {
			Clp *= -1.0;
		}

		this.Cmq = aero.get("Pitch Dumping Moment Coefficient Cmq").asDouble();
		if(Cmq > 0.0) {
			Cmq *= -1.0;
		}

		this.Cnr = Cmq;


	}


	public double Lcp(double Mach) {
		if(lcpFileExist) {
			return lcpAnaly.linearInterp1column(Mach);
		}else {
			return lcpConst;
		}
	}
	
	public boolean getLcpFileExist() {
		return lcpFileExist;
	}

	public double Cd(double Mach) {
		if(CdFileExist) {
			return CdAnaly.linearInterp1column(Mach);
		}else {
			return CdConst;
		}
	}

	public boolean getCdFileExist() {
		return CdFileExist;
	}

	public double CNa(double Mach) {
		if(CNaFileExist) {
			return CNaAnaly.linearInterp1column(Mach);
		}else {
			return CNaConst;
		}
	}
	
	public boolean getCNaFileExist() {
		return CNaFileExist;
	}



}
