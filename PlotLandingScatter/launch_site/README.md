# はじめに
射点の緯度経度や保安行きが変更された際の射場マップの更新方法について解説する。
また新たに射場を追加する場合は既存のPythonプログラムファイルを参考に追加してほしい。

# 手順
1. [launch_site.json](../../input/launch_site.json)の情報を更新する。各パラメータの外湯を以下に示す。<br>
以下は射場によらず，必須のパラメータである。<br>

|Name|Description|
|--|--|
|`launch_LLH`|射点の緯度・経度・高度。|
|`safety_area_LLH`|落下保安行きの緯度・経度・高度の配列。|
|`xlim_ENU`|射場マップの$x$（東西）方向の表示範囲。相対座標（地面固定座 $\fallingdotseq$ ENU座標）で記述する。|
|`ylim_ENU`|射場マップのy（南北）方向の表示範囲。相対座標（地面固定座 $\fallingdotseq$ ENU座標）で記述する。|
|`x_offset`|地図画像を$x$方向（地面固定座標）への移動量。地図上の座標と落下分散中の座標が一致しないときに調整に使う（詳細は後述する）。|
|`y_offset`|地図画像を$y$方向（地面固定座標）への移動量。|
|`img`|落下分散の描画に用いる地図の画像の相対パス。|

陸打ち（保安域が多角形で定義）の場合は保安域の各頂点の座標の配列が必要である。

|Name|Description|
|--|--|
|`safety_area_LLH`|`[[lat1, lon1, hei1], ...]`のように配列型式で記述する。|

海打ち（保安域が円形で定義）の場合は保安円の中心，半径，境界端点座標が必要である。
|Name|Description|
|--|--|
|`center_circle_LLH`|保安円中心の座標|
|`radius`|保安円半径。単位はメートル|
|`edge1_LLH`|境界区域境界の端点1の座標|
|`edge2_LLH`|境界区域境界の端点2の座標|

2. 射場情報を格納したkmlファイルを作成する。<br>
[coordinate.py](../coordinate.py)をメイン実行する。射場の選択はプログラム内`launch_site`で行う。<br>
射場情報が格納されたkmlファイルが[kmlフォルダ](kml)に出力される。

3. kmlを[Google Earth](https://www.google.co.jp/earth/)等で読み込んでスクリーンショット，画像を黒線に沿って切り取る。

4. 画像を[imgフォルダ](img)に保存，[launch_site.json](../../input/launch_site.json)の`img`を書き換える。

5. 画像上の保安域とプロット上の線がずれる場合があるので，`xlim, ylim, x_offset, y_offset`を駆使して，調整する。

# Future Work
* 射場のクラス制度廃止（jsonへの追加だけで完結するようにしたい）
* 射場と射場番号の対応表の作成（辞書型でjsonに？）
* 打上方式ではなく，保安域形状で分類したい