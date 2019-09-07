package quabla.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputLandingScatter {

	//TODO 計算条件の風速値と出力ファイルのラベルの風速値の対応

	/**
	 * 落下位置をファイルに出力します
	 *
	 * @param filepath 出力先
	 * @param wind_map 落下位置を保存した配列
	 * @throws IOException
	 * */
	public void output(String filepath, double[][] wind_map) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
			double angle_step = 360.0/(wind_map[0].length -1);

			StringBuilder stb_first = new StringBuilder(",,");
			for(int i=0;i<wind_map[0].length;i++) {
				stb_first.append(i*angle_step +",");
			}
			writer.write(stb_first.toString());
			writer.newLine();

			for(int wind=1;wind<=wind_map.length/2;wind++) {
				for(int xy=0;xy<2;xy++) {
					StringBuilder stb = new StringBuilder();
					if(xy==0) {
						stb.append(wind+"m/s,x,");
					}else {
						stb.append(wind+"m/s,y,");
					}

					for(int i=0;i<wind_map[0].length;i++) {
						stb.append(wind_map[2*(wind-1)+xy][i]+",");
					}

					writer.write(stb.toString());
					writer.newLine();
				}
			}

			//try-catch-resourceなのでcloseする必要がない

		}catch (IOException e) {
			throw e;
		}

	}

}
