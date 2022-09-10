package quabla.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputTxt {

	private BufferedWriter writer;

	/**
	 * @param filepath 出力先のアドレス
	 * @throws IOException 指定されたファイルが存在するが通常ファイルではなくディレクトリである場合,存在せず作成もできない場合,または何らかの理由で開くことができない場合
	 * */
	public OutputTxt(String filepath) throws IOException {
		writer = new BufferedWriter(new FileWriter(filepath));
	}

	public void outputLine(double value) throws IOException {
		String str = String.valueOf(value);
		writer.write(str);
	}

	public void outputLine(String text) throws IOException{
		writer.write(text);
		writer.newLine();
	}

	/**改行を行う
	 * */
	public void newLine() throws IOException{
		writer.newLine();
	}

	public void close() throws IOException{
		writer.close();
	}

}
