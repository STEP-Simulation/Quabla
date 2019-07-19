package quabla.simulator;

import quabla.InputParam;
import quabla.simulator.numerical_analysis.Interpolation;

public class Aero_param {

	double Lcp_const;
	double Cd_const, CNa_const;
	double Lcp_data[][],Cd_data[][],CNa_data[][];
	//double
	Interpolation Lcp_analy,Cd_analy,CNa_analy;
	double Lcp_length;
	double Clp,Cmq,Cnr;
	boolean Lcp_file_exist,Cd_file_exist,CNa_file_exist;


	public Aero_param(InputParam spec){


		if(spec.Lcp_file_exist) {
			this.Lcp_data = Getcsv.get2ColumnArray(spec.Lcp_file);
			double Mach_array[] = new double[Lcp_data.length];
			double Lcp_array[] = new double[Lcp_data.length];
			for(int i = 0; i < Lcp_data.length ; i++) {
				Mach_array[i] = Lcp_data[i][0];
				Lcp_array[i] = Lcp_data[i][1];
			}
			this.Lcp_analy = new Interpolation(Mach_array , Lcp_array);//Mach_array,Lcp_arrayってインスタンス変数にする必要ある？
		}else {
			this.Lcp_const = spec.Lcp;
		}


		if(spec.Cd_file_exist) {
			this.Cd_data = Getcsv.get2ColumnArray(spec.Cd_file);
			double Mach_array[] = new double[Cd_data.length];
			double Cd_array[] = new double[Cd_data.length];
			for(int i = 0 ; i < Cd_data.length ; i++) {
				Mach_array[i] = Cd_data[i][0];
				Cd_array[i] = Cd_data[i][1];
			}
			this.Cd_analy = new Interpolation(Mach_array , Cd_array);
		}else {
			this.Cd_const = spec.Cd;
		}


		if(spec.CNa_file_exist) {
			this.CNa_data = Getcsv.get2ColumnArray(spec.CNa_file);
			double Mach_array[] = new double[CNa_data.length];
			double CNa_array[] = new double[CNa_data.length];
			for(int i = 0; i < CNa_data.length ; i++) {
				Mach_array[i] = CNa_data[i][0];
				CNa_array[i] = CNa_data[i][1];
			}
			this.CNa_analy = new Interpolation(Mach_array , CNa_array);
		}else {
			this.CNa_const = spec.CNa;
		}


		this.Lcp_file_exist = spec.Lcp_file_exist;
		this.Cd_file_exist = spec.Cd_file_exist;
		this.CNa_file_exist = spec.CNa_file_exist;


		this.Clp = spec.Clp;
		if(Clp > 0.0)
			Clp *= -1.0;


		this.Cmq = spec.Cmq;
		if(Cmq > 0.0)
			Cmq *= -1.0;


		this.Cnr = Cmq;


	}


	public double Lcp(double Mach) {
		double Lcp;

		if(Lcp_file_exist) {
			Lcp = Lcp_analy.linear_interpolation(Mach);
		}else {
			Lcp = Lcp_const;
		}

		return Lcp;
	}


	public double Cd(double Mach) {
		double Cd;

		if(Cd_file_exist) {
			Cd = Cd_analy.linear_interpolation(Mach);
		}else {
			Cd = Cd_const;
		}

		return Cd;
	}


	public double CNa(double Mach) {
		double CNa;

		if(CNa_file_exist) {
			CNa = CNa_analy.linear_interpolation(Mach);
		}else {
			CNa = CNa_const;
		}

		return CNa;
	}


}
