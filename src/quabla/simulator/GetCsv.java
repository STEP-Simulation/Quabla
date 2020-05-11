package quabla.simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * GetCsv reads csv file.
 * Functions of this class must be used properly for the number of columns.
 * */
public class GetCsv {

	public static double[] get1ColumnArray(String filepath) {
		//ArrayListは最初に長さを決めない配列
		ArrayList<Double> list = new ArrayList<>();
		boolean flag = true;

		try(BufferedReader bre = new BufferedReader(new FileReader(filepath))){

			//一行の文字列を格納
			String linestr;

			//bre.readLine()で一行を読み込む
			//読み込んだ内容をlinestrに格納
			//それがnullなら、ファイルの終了
			while((linestr = bre.readLine())!=null) {
				for(int i=0;i<linestr.length();i++) {
					//先頭の文字が空文字の場合があるための処理
					//先頭文字が0から9までの数字である場合、切り取りを行わない
					if(!linestr.substring(0,1).matches("[0-9]")) {
						linestr = linestr.substring(1);
					}else {
						break;
					}
				}

				if(flag) {
					flag = false;
				}else {
				//listにdouble値として格納していく
				list.add(Double.parseDouble(linestr));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//今はlistに一時的に結果を格納してある
		//これと同じ長さの配列を用意
		double[] array_1column = new double[list.size()];
		for(int i=0;i<array_1column.length;i++) {
			array_1column[i] = list.get(i);
		}

		return array_1column;
	}


	public static double[][] get2ColumnArray(String filepath) {
		//ArrayListは最初に長さを決めない配列
		ArrayList<Double> list1 = new ArrayList<>();//1列目
		ArrayList<Double> list2 = new ArrayList<>();//2列目
		boolean flag = true;

		try(BufferedReader bre = new BufferedReader(new FileReader(filepath))){

			//一行の文字列を格納
			String linestr;
			String linestr_split[] = new String[2];//コンマで分割した後の1行の文字列

			//bre.readLine()で一行を読み込む
			//読み込んだ内容をlinestrに格納
			//それがnullなら、ファイルの終了
			while((linestr = bre.readLine())!=null) {
				for(int i=0;i<linestr.length();i++) {
					//先頭の文字が空文字の場合があるための処理
					//先頭文字が0から9までの数字である場合、切り取りを行わない
					if(!linestr.substring(0,1).matches("[0-9]")) {
						linestr = linestr.substring(1);
					}else {
						break;
					}
				}


				linestr_split = linestr.split(",");

				if(flag) {
					flag = false;
				}else {
					//listにdouble値として格納していく
					list1.add(Double.parseDouble(linestr_split[0]));
					list2.add(Double.parseDouble(linestr_split[1]));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//今はlistに一時的に結果を格納してある
		//これと同じ長さの配列を用意
		double[][] array_2column = new double[list1.size()][2];

		for(int i=0;i<array_2column.length;i++) {
			array_2column[i][0] = list1.get(i);
			array_2column[i][1] = list2.get(i);
		}

		return array_2column;
	}

	public static double[][] get3ColumnArray(String filepath) {
		ArrayList<Double> list1 = new ArrayList<>();//1列目
		ArrayList<Double> list2 = new ArrayList<>();//2列目
		ArrayList<Double> list3 = new ArrayList<>();//3列目
		boolean flag = true;

		try(BufferedReader bre = new BufferedReader(new FileReader(filepath))){

			String linestr;
			String linestr_split[] = new String[3] ;//コンマで分割した後の1行の文字列


			while((linestr = bre.readLine())!=null) {
				for(int i=0;i<linestr.length();i++) {
					if(!linestr.substring(0,1).matches("[0-9]")) {
						linestr = linestr.substring(1);
					}else {
						break;
					}
				}


				linestr_split = linestr.split(",");

				if(flag) {
					flag = false;//1行目は項目名の文字列なのでスキップ
				}else {
					//listにdouble値として格納していく
					list1.add(Double.parseDouble(linestr_split[0]));
					list2.add(Double.parseDouble(linestr_split[1]));
					list3.add(Double.parseDouble(linestr_split[2]));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//今はlistに一時的に結果を格納してある
		//これと同じ長さの配列を用意
		double[][] array_3column = new double[list1.size()][3];

		for(int i=0;i<array_3column.length;i++) {
			array_3column[i][0] = list1.get(i);
			array_3column[i][1] = list2.get(i);
			array_3column[i][2] = list3.get(i);
		}

		return array_3column;
	}

}
