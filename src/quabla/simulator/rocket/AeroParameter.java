package quabla.simulator.rocket;

import com.fasterxml.jackson.databind.JsonNode;

import quabla.simulator.GetCsv;
import quabla.simulator.numerical_analysis.Interpolation;

public class AeroParameter {

	private double Lcp_const;
	private double Cd_const, CNa_const;
	Interpolation Lcp_analy,Cd_analy,CNa_analy;
	public double Clp, Cmq, Cnr;
	private final boolean Lcp_file_exist,Cd_file_exist,CNa_file_exist;


	public AeroParameter(JsonNode aero){

		Lcp_file_exist = aero.get("Length-C.P. File").asBoolean();
		Cd_file_exist = aero.get("Cd File").asBoolean();
		CNa_file_exist = aero.get("CNa File").asBoolean();

		if(Lcp_file_exist) {
			double[][] Lcp_data = GetCsv.get2ColumnArray(aero.get("Length-C.P. File").asText());
			double Mach_array[] = new double[Lcp_data.length];
			double Lcp_array[] = new double[Lcp_data.length];
			for(int i = 0; i < Lcp_data.length ; i++) {
				Mach_array[i] = Lcp_data[i][0];
				Lcp_array[i] = Lcp_data[i][1];
			}
			this.Lcp_analy = new Interpolation(Mach_array , Lcp_array);//Mach_array,Lcp_arrayってインスタンス変数にする必要ある？
		}else {
			Lcp_const = aero.get("Constant Length-C.P. from Nosetip [m]").asDouble();
		}


		if(Cd_file_exist) {
			double[][] Cd_data = GetCsv.get2ColumnArray(aero.get("Cd File").asText());
			double Mach_array[] = new double[Cd_data.length];
			double Cd_array[] = new double[Cd_data.length];
			for(int i = 0 ; i < Cd_data.length ; i++) {
				Mach_array[i] = Cd_data[i][0];
				Cd_array[i] = Cd_data[i][1];
			}
			this.Cd_analy = new Interpolation(Mach_array , Cd_array);
		}else {
			this.Cd_const = aero.get("Constant Cd").asDouble();
		}


		if(CNa_file_exist) {
			double[][] CNa_data = GetCsv.get2ColumnArray(aero.get("CNa File").asText());
			double Mach_array[] = new double[CNa_data.length];
			double CNa_array[] = new double[CNa_data.length];
			for(int i = 0; i < CNa_data.length ; i++) {
				Mach_array[i] = CNa_data[i][0];
				CNa_array[i] = CNa_data[i][1];
			}
			this.CNa_analy = new Interpolation(Mach_array , CNa_array);
		}else {
			this.CNa_const = aero.get("Constant CNa").asDouble();
		}

		this.Clp = aero.get("Roll Dumping Moment Coefficient Clp").asDouble();
		if(Clp > 0.0)
			Clp *= -1.0;

		this.Cmq = aero.get("Pitch Dumping Moment Coefficient Cmq").asDouble();
		if(Cmq > 0.0)
			Cmq *= -1.0;

		this.Cnr = Cmq;


	}


	public double Lcp(double Mach) {
		if(Lcp_file_exist) {
			return Lcp_analy.linearInterp1column(Mach);
		}else {
			return Lcp_const;
		}
	}


	public double Cd(double Mach) {
		if(Cd_file_exist) {
			return Cd_analy.linearInterp1column(Mach);
		}else {
			return Cd_const;
		}
	}


	public double CNa(double Mach) {
		if(CNa_file_exist) {
			return CNa_analy.linearInterp1column(Mach);
		}else {
			return CNa_const;
		}
	}


}
