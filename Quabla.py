import shutil
import subprocess
import numpy as np
import sys
import json
import os
import matplotlib.pyplot as plt
import numpy as np
from flightgrapher import flightgrapher
from plotlandingscatter import plotlandingscatter
import csv
from PlotLandingScatter.launch_site.launch_site_info import OshimaLand, OshimaSea, NoshiroLand, NoshiroSea, OtherSite

def main():

    print("\n6DoF Rocket Simulator QUABLA by STEP... \n")
    print("-----------\n")

    # Get launch site information
    launch_site_json = json.load(open('./input/launch_site.json', 'r', encoding='utf-8'))

    #ロケットの諸元ファイルの絶対pathを入力させる それがPC内に存在するものかつ拡張子が.jsonならば受け取るがそれ以外なら拒否する
    while(1):
        files = os.listdir("./config/")
        print('Rocket configuration files')
        # print("-------------------- Configuration Files --------------------")
        # for f in files:
        #     if f.endswith('.json'):
        #         print(f)
        # print("-------------------- Configuration Files --------------------")
        paramaterpath = input("Enter the path of rocket paramater file (...json):\n")
        # paramaterpath = 'config/' + paramaterpath
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
    model_name = json_load['Solver']['Name']
    launch_site = json_load["Launch Condition"]["Site"]
    safety_exist = json_load["Launch Condition"]["Safety Area Exist"]

    # Resultフォラウダに何も指定されていない時，デフォルトで直下のフォルダを指定
    if not resultrootpath: resultrootpath = '.' 

    # Make Result directory
    if simulationmode == 'single':
        result_dir = resultrootpath + os.sep + 'Result_single_' + model_name

    elif simulationmode == 'multi':
        result_dir = resultrootpath + os.sep + 'Result_multi_' + model_name
        
    if os.path.exists(result_dir):
        resultdir_org = result_dir
        i = 1
        while os.path.exists(result_dir):
            result_dir = resultdir_org + '_%02d' % (i)
            i += 1
    os.mkdir(result_dir)

    copy_config_files(json_load, paramaterpath, result_dir)

    print('-------------------- INFORMATION --------------------')
    print('  Config File:     ', os.path.basename(paramaterpath))
    print('  Model Name:      ', model_name)
    print('  Simulation Mode: ', simulationmode)
    print('  Result File:      ' + os.path.basename(result_dir))
    print('-----------------------------------------------------\n')

    if launch_site == '0' :
        launch_site_info = OtherSite()
        launch_site_info.launch_LLH[0] = float(json_load['Launch Condition']['Launch lat'])
        launch_site_info.launch_LLH[1] = float(json_load['Launch Condition']['Launch lon'])
        launch_site_info.launch_LLH[2] = float(json_load['Launch Condition']['Launch height'])
        launch_site_info.center_circle_LLH = launch_site_info.launch_LLH

    elif launch_site == '1':
        launch_site_info = OshimaLand(launch_site_json.get('oshima_land'))

    elif launch_site == '2':
        launch_site_info = OshimaSea(launch_site_json.get('oshima_sea'))

    elif launch_site == '3':
        launch_site_info = NoshiroLand(launch_site_json.get('noshiro_land'))

    elif launch_site == '4':
        launch_site_info = NoshiroSea(launch_site_json.get('noshiro_sea'))


    # Execute Quabla.jar
    subprocess.run(["java", "-jar", "Quabla.jar", paramaterpath, simulationmode, result_dir], \
                    check=True)

    resultpath = result_dir + os.sep

    #射角を諸元ファイルから取得
    launcher_elevation = str(json_load['Launch Condition']['Launch Elevation [deg]'])

    #磁気偏角を考慮しているかどうかを取得 考慮されてたら下のパラメータは0でないはずなのでそこを参照
    # if json_load['Launch Condition']['Input Magnetic Azimuth [deg]'] == '0.0':
    #     magneticdec = 'n'
    # else:
    #     magneticdec = 'y'
    # 地図そのものを回転させて対処する機能は廃止
    magneticdec = 'y'

    #グラフの描画
    if simulationmode == "single":
        
        flightgrapher(resultpath, launch_site_info, safety_exist)

    elif simulationmode == "multi":
        
        plotlandingscatter(resultpath, launcher_elevation, launch_site, launch_site_info, magneticdec, safety_exist)

def copy_config_files(json_config, path_config, path_result):

    dir_config = path_result + os.sep + '_00_config'
    model_name = json_config['Solver']['Name']
    os.mkdir(dir_config)

    json_copy = json_config
    json_copy['Solver']["Result Filepath"] = dir_config

    shutil.copy(json_config["Engine"]["Thrust Curve"], dir_config + os.sep + model_name + '_thrust.csv')
    json_copy["Engine"]["Thrust Curve"] = dir_config + os.sep + model_name + '_thrust.csv'

    fig, ax = plt.subplots()
    ax.set_title('Thrust vs. Time')
    thrust_array = np.loadtxt(json_config["Engine"]["Thrust Curve"], delimiter=',', skiprows=1)
    ax.plot(thrust_array[:, 0], thrust_array[:, 1], color='#FF4B00')
    ax.set_xlim(xmin=0., xmax=thrust_array[-1, 0])
    ax.set_ylim(ymin=0.)
    ax.set_xlabel('Time [sec]')
    ax.set_ylabel('Thrust [N]')
    ax.grid()
    fig.savefig(dir_config + os.sep + '_thrust.png')

    if json_copy["Wind"]["Wind File Exist"]:
        shutil.copy(json_config["Wind"]["Wind File"], dir_config + os.sep + model_name + '_wind.csv')
        json_copy["Wind"]["Wind File"] = dir_config + os.sep + model_name + '_wind.csv'

        fig, ax = plt.subplots()
        ax.set_title('Wind Speed vs. Altitude')
        wind_array = np.loadtxt(json_config["Wind"]["Wind File"], delimiter=',', skiprows=1)
        ax.plot(wind_array[:, 1], wind_array[:, 0], color='#FF4B00', marker='o')
        ax.set_ylim(ymin=0.)
        ax.set_xlabel('Wind Speed [m/s]')
        ax.set_ylabel('Altitude [m]')
        ax.grid()
        fig.savefig(dir_config + os.sep + '_wind.png')

    else:
        json_copy["Wind"]["Wind File"] = ''

    if json_copy["Aero"]["Cd File Exist"]:
        shutil.copy(json_config["Aero"]["Cd File"], dir_config + os.sep + model_name + '_Cd.csv')
        json_copy["Aero"]["Cd File"] = dir_config + os.sep + model_name + '_Cd.csv'

        fig, ax = plt.subplots()
        ax.set_title('$C_D$ vs. Mach')
        Cd_array = np.loadtxt(json_config["Aero"]["Cd File"], delimiter=',', skiprows=1)
        ax.plot(Cd_array[:, 0], Cd_array[:, 1], color='#FF4B00', marker='o')
        ax.set_xlim(xmin=0.)
        # ax.set_ylim(ymin=0.)
        ax.set_xlabel('Mach Number [-]')
        ax.set_ylabel('$C_D$ [-]')
        ax.grid()
        fig.savefig(dir_config + os.sep + '_Cd.png')

    else:
        json_copy["Aero"]["Cd File"] = ''

    if json_copy["Aero"]["Length-C.P. File Exist"]:
        shutil.copy(json_config["Aero"]["Length-C.P. File"], dir_config + os.sep + model_name + '_Lcp.csv')
        json_copy["Aero"]["Length-C.P. File"] = dir_config + os.sep + model_name + '_Lcp.csv'

        fig, ax = plt.subplots()
        ax.set_title('$L_{C.P.}$ vs. Mach')
        lcp_array = np.loadtxt(json_config["Aero"]["Length-C.P. File"], delimiter=',', skiprows=1)
        ax.plot(lcp_array[:, 0], lcp_array[:, 1], color='#FF4B00', marker='o')
        ax.set_xlim(xmin=0.)
        # ax.set_ylim(ymin=0.)
        ax.set_xlabel('Mach Number [-]')
        ax.set_ylabel('$L_{C.P.}$ [m]')
        ax.grid()
        fig.savefig(dir_config + os.sep + '_Lcp.png')

    else:
        json_copy["Aero"]["Length-C.P. File"] = ''

    if json_copy["Aero"]["CNa File Exist"]:
        shutil.copy(json_config["Aero"]["CNa File"], dir_config + os.sep + model_name + '_CNa.csv')
        json_copy["Aero"]["CNa File"] = dir_config + os.sep + model_name + '_CNa.csv'

        fig, ax = plt.subplots()
        ax.set_title('$C_{Na}$ vs. Mach')
        CNa_array = np.loadtxt(json_config["Aero"]["CNa File"], delimiter=',', skiprows=1)
        ax.plot(CNa_array[:, 0], CNa_array[:, 1], color='#FF4B00', marker='o')
        ax.set_xlim(xmin=0.)
        # ax.set_ylim(ymin=0.)
        ax.set_xlabel('Mach Number [-]')
        ax.set_ylabel('$C_{Na}$ [-]')
        ax.grid()
        fig.savefig(dir_config + os.sep + '_CNa.png')

    else:
        json_copy["Aero"]["CNa File"] = ''

    json.dump(json_copy, open(dir_config + os.sep + model_name + '_config.json', 'w', encoding="utf-8"), indent=4, ensure_ascii=False)

if __name__=='__main__':

    main()
