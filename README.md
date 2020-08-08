Quabla
==
6-DoF Rocket Simulator for STEP<br>
coding UTF-8

<img src="./src/image/Quablaロゴ(透過済).png" width="400px">

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
	* 負の値を入力している（特に減衰モーメント係数。プログラム内で自動で修正してくれる）
	* コロンが無い，逆にコロンが必要ない<br>
などの文法ミスに注意。

* jsonファイルをUTF-8で編集しているか必ず確認

* 長さなどの定義に注意。長さの基準が異なっている可能性がある。

### Solver
### Multi Solver
### Structure
### Engine
### Parachute
### Wind

## Problem
* Mac OSで使用する場合，文字コードのせいか階層を区切る`\\`が文字化けしてしまい，
ディレクトリの指定で失敗する。<br>
`QUABLA.java`中の`\\`を`/`に変更するとうまくいく。

## Future Works
* 変数が発散したときの例外処理
* gui化 → 現状，CUIを検討
* マルチメソッド化（現状のコードでも速度自体は十分。今後計算条件が増えるなら実装の必要あり）