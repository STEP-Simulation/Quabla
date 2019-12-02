package quabla.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

/**
 * <pre>
 * OutputCsv outputlog = new null;
 * try{
 * 	outputlog = new OutputCsv(filepath, name);
 * } catch (IOException) {
 * 	throw new RuntimeException(e);
 * }
 *
 * try{
 * 	outputlog.outputFirstLine();
 * } catch (IOException e){
 * 	throw new RuntimeException(e);
 * }
 * for(int i = 0; ;i++){
 * 	double[] result = new double[3];
 * 	result = pos_ENU;
 *
 * 	try{
 * 		outputlog.outputLine(result);
 * 	} catch (IOException e) {
 * 		throw new RuntimeException(e);
 * 	}
 * }
 * try{
 * 	outputlog.close();
 * } catch(IOException e) {
 * 	throw new RuntimeException(e);
 * }
 * </pre>
 *
 * */
public class OutputCsv {

	private BufferedWriter writer;

	/**変数の名前*/
	private String firstline ;


	/**
	 * ファイル出力するクラスのコンストラクタ
	 * @param 出力先のファイルパス
	 * @param name array of names of variable
	 * @throws IOException 指定されたファイルが存在するが通常ファイルではなくディレクトリである場合、存在せず作成もできない場合、またはなんらかの理由で開くことができない場合
	 * */
	public OutputCsv(String filepath,String[] name) throws IOException {
		writer = new BufferedWriter(new FileWriter(filepath));
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


	/**
	 * @param result array of data for output
	 * @throws IOException
	 * */
	public void outputLine(double[] result) throws IOException {
		//出力したい文字列に整形
		StringJoiner stj = new StringJoiner(",");
		for(int i=0; i<result.length; i++) {
			stj.add(String.valueOf(result[i]));
		}
		String linestr = stj.toString();

		//文字列をファイルに出力
		writer.write(linestr);

		//改行
		writer.newLine();
	}

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
		writer.close();
	}


}
