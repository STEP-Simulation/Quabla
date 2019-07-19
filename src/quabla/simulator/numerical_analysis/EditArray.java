package quabla.simulator.numerical_analysis;

public class EditArray {

	public static double[] cut_array(int index_max, double array[]){
		double new_array[] = new double[index_max];

		for(int i=0; i<index_max; i++) {
			new_array[i] = array[i];
		}

		return new_array;
	}

}
