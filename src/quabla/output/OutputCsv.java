package quabla.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

public class OutputCsv {

	private BufferedWriter writer;

	//private String[] name;
	/**変数の名前*/
	private String firstline ;
	//配列で受け取る

	/**数値の出力*/
	//private String format;


	/**
	 * ファイル出力するクラスのコンストラクタ
	 *
	 * @param 出力先のファイルパス
	 * @throws IOException 指定されたファイルが存在するが通常ファイルではなくディレクトリである場合、存在せず作成もできない場合、またはなんらかの理由で開くことができない場合
	 * */
	public OutputCsv(String filepath,String[] name) throws IOException {
		writer = new BufferedWriter(new FileWriter(filepath));
		//this.name = name;
		//set_format(name);
		make_firstline(name);
	}


	/**
	 * 一行目に書き込む文字列を書き込みます
	 * @throws IOException 入出力エラーが発生した場合
	 * */
	public void outputFirstLine() throws IOException {
		writer.write(firstline);
		writer.newLine();
	}


	//配列で受け取る
	public void outputLine(double[] result) throws IOException {


		//出力したい文字列に整形
		StringJoiner stj = new StringJoiner(",");
		String[] str = new String[result.length];
		for(int i=0; i<result.length; i++) {
			str[i] = String.valueOf(result[i]);//double値からString型へ
			stj.add(str[i]);
		}
		String linestr = stj.toString();
		//String linestr = String.format(format, result[0]);

		//文字列をファイルに出力
		writer.write(linestr);

		//改行
		writer.newLine();
	}

	/*
	//formatの生成
	public void set_format(String[] name) {
		StringJoiner stj = new StringJoiner(",");

		for(int i=0; i< name.length; i++) {
			stj.add("%f");
		}
		format = stj.toString();
	}
	*/

	//firstlineの生成
	public void make_firstline(String[] name) {
		StringJoiner stj = new StringJoiner(",");

		for(int i=0; i<name.length; i++) {
			stj.add(name[i]);
		}
		firstline = stj.toString();

	}


	/**
	 * 出力を終えます。
	 *
	 * @throws IOException
	 * */
	public void close() throws IOException {
		this.writer.close();
	}


}
