Quabla
==
6-DoF Rocket Simulator for STEP<br>
coding UTF-8

![Quabla_logo](./src/image/Quablaロゴ(透過済).png)

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
`C:/hoge`にある`rocket_config.json`を絶対パスで指定する場合，
コマンドライン引数は`C:/hoge/rocket_config.json`となる。
2. `QUABLA.java`を実行。

## Future Works
* 変数が発散したときの例外処理
* gui化
