import subprocess
import numpy as np
import sys
import json
import os
from flightgrapher import flightgrapher
from plotlandingscatter import plotlandingscatter
import csv
from launch_site_info import OshimaLand, OshimaSea, NoshiroLand, NoshiroSea, OtherSite

print("\n6DoF Rocket Simulator QUABLA by STEP... \n")
#--------------------更新内容-------------------------#
print("最終更新日：2022/05/17\n")
#2022/01/24 諸元ファイルに"がついていてもそれを自動で取り除けるようにした
#2022/03/14 最大法線力をresutl.txtに表示できるようにした
#2022/05/02 グラフの描画をするかどうかを聞かないで絶対に描画するようにした
#2022/05/10 Quabla_JAVAとjsonファイルにてmultiモードの基準風向を任意に変更できるようにした
#2022/05/17 保安円の出力をON OFFできるようにした
#更新：相沢(何かあればkeidxb610{@}gmail.comまで {@}を@に置き換えてください)
#---------------------------------------------------#

print("-----------\n")

# Get launch site information
launch_site_json = json.load(open('./input/launch_site.json', 'r', encoding='utf-8'))

#ロケットの諸元ファイルの絶対pathを入力させる それがPC内に存在するものかつ拡張子が.jsonならば受け取るがそれ以外なら拒否する
while(1):
    files = os.listdir("./config/")
    print('Rocket configuration files')
    print("-------------------- Configuration Files --------------------")
    for f in files:
        if f.endswith('.json'):
            print(f)
    print("-------------------- Configuration Files --------------------")
    paramaterpath = input("Enter the path of rocket paramater file (...json):\n")
    paramaterpath = 'config/' + paramaterpath
    #windowsのときにエクスプローラーからpathをコピーすると"がpathの前後につくのでそれを削除する
    # paramaterpath = paramaterpath.replace('\"','') 
    if os.path.exists(paramaterpath) == True and paramaterpath.endswith('.json') == True:
        break
    else:
        print('\nPlease enter again.\n')

#シミュレーションモードをsingleかmultiか選ばせる　それ以外の入力を受け付けない
while(1):
    simulationmode = input("\nEnter simulation mode (single or multi):\n")
    if simulationmode != 'single' and simulationmode != 'multi':
        print('\nPlease enter single or multi')
    else:
        break

print('''\

　 ／＼
　｜Q  ∧∧
　｜U ( ﾟДﾟ)
　｜A ⊂STEP|
　｜B ⊂__ ノ＠
　｜L ｜
／｜A ｜＼
￣￣￣￣￣
　　ε
　　ε
''')
print('Quabla Start...\n')


#パラメーターのjsonファイルの読み込み
json_open = open(paramaterpath, 'r', encoding="utf-8")
json_load = json.load(json_open)
resultrootpath = json_load['Solver']["Result Filepath"]
launch_site = json_load["Launch Condition"]["Site"]
safety_exist = json_load["Launch Condition"]["Safety Area Exist"]

if launch_site == '1':
    launch_site_info = OshimaLand(launch_site_json.get('oshima_land'))

elif launch_site == '2':
    launch_site_info = OshimaSea(launch_site_json.get('oshima_sea'))

elif launch_site == '3':
    launch_site_info = NoshiroLand(launch_site_json.get('noshiro_land'))

elif launch_site == '4':
    launch_site_info = NoshiroSea(launch_site_json.get('noshiro_sea'))

elif launch_site == '5' :
    launch_site_info = OtherSite()
    launch_site_info.launch_LLH[0] = float(json_load['Launch Condition']['Launch lat'])
    launch_site_info.launch_LLH[1] = float(json_load['Launch Condition']['Launch lon'])
    launch_site_info.launch_LLH[2] = float(json_load['Launch Condition']['Launch height'])
    launch_site_info.center_circle_LLH = launch_site_info.launch_LLH

# Execute Quabla.jar
subprocess.call(["java", "-jar", "Quabla.jar", paramaterpath, simulationmode, \
                str(launch_site_info.launch_LLH[0]), str(launch_site_info.launch_LLH[1]), str(launch_site_info.launch_LLH[2])])

#グラフの出力に使う結果の入ったフォルダの場所が書いてあるjsonファイルを参照する
#結果ファイルの後ろに01とかついちゃうので逐一取得する
json_open2 = open(resultrootpath + os.sep + "resultpath.json")
json_load2 = json.load(json_open2)

#グラフをプロットするために今出力した結果ファイルのpathをjsonから読み取り
resultpath = json_load2["path"]

#射角を諸元ファイルから取得
launcher_elevation = str(json_load['Launch Condition']['Launch Elevation [deg]'])

#磁気偏角を考慮しているかどうかを取得 考慮されてたら下のパラメータは0でないはずなのでそこを参照
if json_load['Launch Condition']['Input Magnetic Azimuth [deg]'] == '0.0':
    magneticdec = 'n'
else:
    magneticdec = 'y'

#最大法線力をresutl.txtに追加する
if simulationmode == 'single':  #singleモードでしか最大法線力の載っているcsvが出力されないため
    resulttxt_path = resultpath + os.sep + 'result.txt' #resutl.txtのpath
    trajectry_result_path = resultpath + os.sep + 'flightlog_trajectory.csv' #法線力の載っている計算結果csvのpath

    #最大法線力を弾道の計算結果csvから取得する
    with open (trajectry_result_path , 'r',  encoding = "utf-8") as f :
      reader = csv.reader(f)
      line = [row for row in reader]

    item_list = line[0] #一行目の項目名を格納

    #法線力が何列目かを取得
    for a in range(len(item_list)):
        if item_list[a] == 'Normal Force [N]':
          column_normalforce = a #a列目が法線力

    max_noramlforce = 0 #最大法線力を格納
    #法線力を比較していって最大値を求める
    for a in range(len(line) - 1):
        if max_noramlforce < float(line[a + 1][column_normalforce]):
            max_noramlforce = float(line[a + 1][column_normalforce])

    #result.txtの最後の行に上で求めた最大法線力を載せる
    f = open(resulttxt_path, 'a')
    f.write("Max Normal Force : " + str(round(max_noramlforce,2)) + " [N]\n")
    f.close

#グラフの描画
if simulationmode == "single":
    
    flightgrapher(resultpath, launch_site, launch_site_info.launch_LLH, launch_site_info.center_circle_LLH, launch_site_info.radius, \
                  launch_site_info.safety_area_LLH, launch_site_info.edge1_LLH, launch_site_info.edge2_LLH, safety_exist)

elif simulationmode == "multi":
    
    plotlandingscatter(resultpath, launcher_elevation, launch_site, magneticdec, \
                       launch_site_info.launch_LLH, launch_site_info.safety_area_LLH, \
                       launch_site_info.headquarters_LLH, launch_site_info.fire_LLH, \
                       launch_site_info.center_circle_LLH, launch_site_info.radius, \
                       launch_site_info.edge1_LLH, launch_site_info.edge2_LLH, safety_exist)

print("result file:" + os.path.basename(resultpath[:-1]) + "\n")