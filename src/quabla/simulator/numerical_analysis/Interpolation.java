package quabla.simulator.numerical_analysis;

public class Interpolation {
	double x_array[];
	double y_array[];
	private int length;

	public Interpolation(double x_array[], double y_array[]){
		this.x_array = x_array;
		this.y_array = y_array;
		length = x_array.length;
	}

	public double linear_interpolation(double x) {
		double y;
		int count = 0;



		//xが取得データの範囲外の場合,最大値及び最小値をyの値とする
		if( x < x_array[0] ) {
			y = y_array[0] ;
		}else if(x > x_array[length-1] ) {
			y = y_array[length-1];
		}else {

			//xが何群目に所属しているかを調べる
			for(int i=0; i<length; i++) {
				if(x >= x_array[i]) {
					count = i;
				}else {
					break;
				}
			}

			y = y_array[count] + (y_array[count+1] - y_array[count])*(x - x_array[count])/(x_array[count+1]-x_array[count]);
		}

		return y;
	}

}
