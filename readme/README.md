Quabla
==
6-DoF Rocket Simulator for STEP<br>
coding UTF-8

<img src="./Quabla_logo.png" width="400px">

## Libraries
Jsonファイルを読み込むために，以下のライブラリが必要。
Jacksonで読み込んでいる。
* jacson-core
* jackson-annotations
* jackson-databind

ダウンロードは[こちらのサイト](https://www.sejuku.net/blog/39599)を参照のこと。

## Reference
* 戸川隼人，石黒登美子；スピンを伴うロケットの運動を計算するプログラム，航空宇宙技術研究所資料 NAL TM-145，1968
* 嶋田有三，佐々修一；飛行力学，森北出版，2017

## Useage
1. 実行時のコマンドラインで機体の諸元を入力したjsonファイルを指定。<br>
パスの指定方法は相対パスでも絶対パスでもどちらでもよい。
例えば，あらかじめ入っている`sample_rocket.json`を相対パスで指定する場合，
コマンドライン引数は`sample_rocket.json`となる。<br>
`C:\hoge`にある`rocket_config.json`を絶対パスで指定する場合，
コマンドライン引数は`C:\hoge\rocket_config.json`となる。
2. `QUABLA.java`を実行。

## Rocket Configurations
### Caution
* jsonの文法にの取って記入すること。
例えば，
<<<<<<< HEAD
    * 負の値を入力している（特に減衰モーメント係数。プログラム内で自動で修正してくれる）
    * コロンが無い，逆にコロンが必要ない<br>
などの文法ミスに注意。

* jsonファイルをUTF-8で編集しているか必ず確認

* 長さなどの定義に注意。長さの基準が異なっている可能性がある。

### Solver
ソルバーに関する設定。
|項目|備考|
|---|---|
|Name|プロジェクト名や機体名など|
|Result Filepath|結果の出力先。|
|Simulation Mode|'single'か'multi'を指定。|
|Time Step|シミュレーションの時間刻み。|

`'single'` `'multi'`の各モードについては以下の通り。<br>
|項目|説明|
|---|---|
|'single'|単一条件での計算。位置や姿勢角の時間履歴などを見たい場合はこのモードを選択。|
|'multi'|複数条件での計算。落下分散を計算したい場合に選択。singleモードと異なり位置などの時間履歴は出力されず，落下地点や最高高度などの表のみ出力。|

### Multi Solver
複数条件のシミュレーション時の設定。
|項目|単位|備考|
|---|---|---|
|Minimum Wind Speed | m/s | 計算する風速の最小値。|
|Step Wind Speed | m/s || 風速の時間刻み。|
|Number of Wind Speed | - | 計算する風速の数。|
|Number of Wind Azimuth | -  | 計算する風向の数。基本的に4の倍数にすること。|

### Launch
ランチャなどの打上げ条件に関する設定。<br>
|項目|単位|備考|
|---|---|---|
|Date|N/A|打上げ日時。現状このパラメータは使用していない。|
|Site|N/A|射点のLLH系における位置座標。`[緯度，経度，高度]`の順で入力すること。現状，使用していない。|
|Launch Azimuth|deg|打上げ方位角。磁北から時計回りを正。|
=======
	* 負の値を入力している（特に減衰モーメント係数。プログラム内で自動で修正してくれる）
	* コロンが無い，逆にコロンが必要ない<br>
などの文法ミスに注意。

* jsonファイルをUTF-8で編集しているか必ず確認

* 長さなどの定義に注意。長さの基準が異なっている可能性がある。

### Solver
ソルバーに関する設定。
|項目|備考|
|---|---|
|Name|プロジェクト名や機体名など|
|Result Filepath|結果の出力先。|
|Simulation Mode|'single'か'multi'を指定。|
|Time Step|シミュレーションの時間刻み。|

`'single'` `'multi'`の各モードについては以下の通り。<br>
|項目|説明|
|---|---|
|'single'|単一条件での計算。位置や姿勢角の時間履歴などを見たい場合はこのモードを選択。|
|'multi'|複数条件での計算。落下分散を計算したい場合に選択。singleモードと異なり位置などの時間履歴は出力されず，落下地点や最高高度などの表のみ出力。|

### Multi Solver
複数条件のシミュレーション時の設定。
|項目|単位|備考|
|---|---|---|
|Minimum Wind Speed | m/s | 計算する風速の最小値。|
|Step Wind Speed | m/s || 風速の時間刻み。|
|Number of Wind Speed | - | 計算する風速の数。|
|Number of Wind Azimuth | -  | 計算する風向の数。基本的に4の倍数にすること。|

### Launch
ランチャなどの打上げ条件に関する設定。<br>
|項目|単位|備考|
|---|---|---|
|Date|N/A|打上げ日時。現状このパラメータは使用していない。|
|Site|N/A|射点のLLH系における位置座標。`[緯度，経度，高度]`の順で入力すること。現状，使用していない。|
|Launch Azimuth|deg|打上げ方位角。磁北から反時計回りを正。|
>>>>>>> branch 'master' of https://github.com/STEP-Simulation/Quabla
|Launch Elevation|deg|打上げ仰角。|
|Launcher Rail Length |m|ランチャ有効レール長。|
|Tip-Off Calculation Exist|N/A|チップオフを考慮するかどうか。ガントリー式の場合，`false`にする。|
|Input Magnetic Azimuth|deg|磁気偏角。磁北と真北のずれ。|

### Structure
構造に関するパラメータ。<br>
|項目|単位|備考|
|---|---|---|
|Length|m|機体全長。ノーズコーン先端から機体後端まで。ボートテイルを有する場合はボートテイル後端まで。ノズルカバーは含めない。|
|Diameter|m|機体代表直径。|
|Dry Mass|kg|乾燥時（酸化剤を除いた）の機体重量。|
|Dry Length-C.G. from Nosetip|m|乾燥時の機体重心とノーズコーン先端の距離。|
|Dry Moment of Inertia Roll-Axis|kg*m^2|乾燥時のロール軸回りの慣性モーメント。|
|Dry Moment of Inertia  Pitch-Axis|kg*m^2|乾燥時のピッチ軸回りの慣性モーメント。|
|Upper Launch Lug|m|1本目のランチラグとノーズコーン先端の距離。ランチラグが3本以上の場合，機体後端から2本目のランチラグとの距離にすること。|
|Lower Launch Lug|m|機体後端から最も近いランチラグとノーズコーン先端との距離。|

### Engine
エンジンに関するパラメータ。<br>
|項目|単位|備考|
|---|---|---|
|Thrust Curve|-|推力履歴のcsvファイルのパス。|
|Nozzle Throat Diameter|mm|ノズルスロートの直径|
|Nozzle Expansion Ratio|-|ノズル開口比。ノズルの出口面積とスロートの面積比とすること。|
|Burn Time|sec|燃焼時間。作動時間とは異なる。|
|Isp|sec|平均比推力。現状，比推力はシミュレーションに用いていない。|
|Tank Volume|cc|酸化剤タンクの容量。|
|Oxidizer Density|kg/m^3|酸化剤密度。
|Length Fuel-C.G. from End|m|機体後端から燃料（グレイン，固形燃料）重心までの距離。インジェクターベルは燃料重心に含めない。|
|Length Tank-End from End|m|機体後端から酸化剤タンク口金までの距離。|
|Fuel Mass Before|kg|燃焼前燃料重量。|
|Fuel Mass After|kg|燃焼後燃料重量|
|Fuel Outside Diameter|mm|燃料外径。|
|Fuel Inside Diamter|mm|燃焼前の燃料内径。|
|Tank Diameter|mm|タンク外径。|
|Fuel Length|m|燃料長さ。インジェクターベル含めず。|
|Tank Length|m|タンク長さ。|

### Aero
空力微係数関連のパラメータ。<br>
|項目|単位|備考|
|---|---|---|
|Cd File|N/A|抗力係数（軸力係数）カーブのファイルのパス。|
|Cd File Exist|N/A|抗力係数カーブを使用する（抗力係数のマッハ数依存性を考慮する。）かどうか|
|Constant Cd|-|一定抗力係数。|
|Length-C.P. File|N/A|圧力中心カーブのファイルのパス。|
|Length-C.P. File Exist|N/A|圧力中心カーブを使用するかどうか。|
|Constant Length-C.P. from Nosetip|m|一定圧力中心。ノーズコーン先端からの距離。|
|CNa File|N/A|法線力係数傾斜（法線力傾斜。OpenRocketでいうところの法線力係数。）カーブ。|
|CNa File Exist|N/A|法線力係数傾斜カーブを使用するかどうか。|
|Constant CNa|1/rad|一定法線力係数傾斜。|
|Roll Dumping Moment Coefficient Clp|1/rad|ロール減衰モーメント係数。本来，負の値だがJsonの文法エラーになるので，入力時は正の値で入力すること。|
|Pitch Dumping Moment Coefficient Cmq|1/rad|ピッチ・ヨー減衰モーメント係数。本来，負の値だがJsonの文法エラーになるので，入力時は正の値で入力すること。|

### Parachute
パラシュート，ドローグシュートについてのパラメータ。<br>
|項目|単位|備考|
|---|---|---|
|1st Parachute CdS|m^2|1段目のパラシュート（単段分離ならメインシュート，2段分離ならドローグシュート）の抗力係数とパラシュート面積の積。|
|2nd Parachute Exist|N/A|2段分離を行うかどうか。|
|2nd Parachute CdS|m2|2段目パラシュートのCdS|
|2nd Parachute Opening Altitude|m|2段目開傘の高度。|

### Wind
風についての設定。<br>
|項目|単位|備考|
|---|---|---|
|Wind File Exist|N/A|上空風データ（csvファイル）を用いてシミュレーションを行うかどうか。|
|Wind File|N/A|上空風データのパス。|
|Wind Model|N/A|風モデルの指定。`Wind File Exist`が`false`の場合，有効となる。'law','constant'の中から選択。モデルの指定に誤りがあるか，指定なしの場合'constant'になる。|
|Wind Power Law Coefficient|-|高度分布係数。陸打ちは4.5，海打ちは6.0とする。運営から指定がある場合，そちらに従う。|
|Wind Speed|m/s|基準高度（風速計設置高度）での基準風速。`Simulation Mode`が`single`の場合のみ有効。|
|Wind Azimuth|deg|風向。北を0 deg，時計回り正。|
|Wind Reference Altitude|m|基準高度。風向風速計の設置高度とする。|

`Wind Model`については以下の通り。
|項目|説明|
|---|---|
|'law'|べき法則で風速を計算。風向は高度分布をもたない。|
|'constant'|定常風。風向・風速が高度分布をもたない。|

## Future Works
* 変数が発散したときの例外処理
* gui化 → 現状，CUIを検討
* マルチスレッド化（現状のコードでも速度自体は十分。今後計算条件が増えるなら実装の必要あり）