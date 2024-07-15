import shutil
import subprocess
import numpy as np
import sys
import multiprocessing
import json
import os
import matplotlib.pyplot as plt
import numpy as np
from flightgrapher import flightgrapher
from plotlandingscatter import plotlandingscatter
from PlotLandingScatter.launch_site.launch_site_info import OshimaLand, OshimaSea, NoshiroLand, NoshiroSea, OtherSite
from PlotLandingScatter.launch_site.launch_site_info import LaunchSiteInfo

def main():

    print("\n6DoF Rocket Simulator QUABLA by STEP... \n")
    print("-----------\n")


    # ロケットの諸元ファイルの絶対pathを入力させる それがPC内に存在するものかつ拡張子が.jsonならば受け取るがそれ以外なら拒否する
    print('Rocket configuration files')
    path_parameter = (input("Enter the path of rocket parameter file (...json):\n >> ")).strip()
    while(not (os.path.exists(path_parameter) and path_parameter.endswith('.json')) ):
        
        # Debug mode
        if path_parameter == 'deb':
            path_parameter = 'config' + os.sep + 'sample_rocket.json'
            break

        path_parameter = (input('\nPlease enter again. \
                                 \nEnter the path of rocket parameter file (...json): \
                                 \n >> ')).strip()

    # シミュレーションモードをsingleかmultiか選ばせる　それ以外の入力を受け付けない
    mode_simulation = input("\nEnter simulation mode (single or multi):\n >> ")
    while(mode_simulation != 'single' and mode_simulation != 'multi'):
        mode_simulation = input("\nEnter simulation mode (single or multi):\n >> ")

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
    json_open = open(path_parameter, 'r', encoding="utf-8")
    json_load = json.load(json_open)
    resultrootpath = json_load['Solver']["Result Filepath"]
    model_name = json_load['Solver']['Name']
    launch_site = json_load["Launch Condition"]["Site"]
    safety_exist = json_load["Launch Condition"]["Safety Area Exist"]
    exist_payload = json_load["Payload"]["Payload Exist"]

    # Resultフォラウダに何も指定されていない時，デフォルトで直下のフォルダを指定
    if not resultrootpath: resultrootpath = '.' 

    def __make_dir_result(result_dir):
        _dir_result = result_dir
        if os.path.exists(_dir_result):
            resultdir_org = _dir_result
            i = 1
            while os.path.exists(_dir_result):
                _dir_result = resultdir_org + '_%02d' % (i)
                i += 1
        os.mkdir(_dir_result)

        return _dir_result
        
    # Make Result directory
    if mode_simulation == 'single':
        result_dir = __make_dir_result(resultrootpath + os.sep + 'Result_single_' + model_name)

    elif mode_simulation == 'multi':
        result_dir = __make_dir_result(resultrootpath + os.sep + 'Result_multi_' + model_name)
        dir_summary = result_dir + os.sep + '_01_summary'
        os.mkdir(dir_summary)

    copy_config_files(json_load, result_dir)

    print('-------------------- INFORMATION --------------------')
    print('  Config File:     ', os.path.basename(path_parameter))
    print('  Model Name:      ', model_name)
    print('  Simulation Mode: ', mode_simulation)
    print('  Result File:      ' + os.path.basename(result_dir))
    print('-----------------------------------------------------\n')

    if launch_site == '0' :
        launch_site_info = OtherSite()
        launch_site_info.launch_LLH[0] = float(json_load['Launch Condition']['Launch lat'])
        launch_site_info.launch_LLH[1] = float(json_load['Launch Condition']['Launch lon'])
        launch_site_info.launch_LLH[2] = float(json_load['Launch Condition']['Launch height'])
        launch_site_info.center_circle_LLH = launch_site_info.launch_LLH

    else:
        launch_site_info = LaunchSiteInfo(launch_site)

    nproc = multiprocessing.cpu_count() - 1

    # Execute Quabla.jar ##########################################################################
    subprocess.run(["java", "-jar", "Quabla.jar", path_parameter, mode_simulation, result_dir, str(nproc)], \
                    check=True)
    ###############################################################################################

    resultpath = result_dir + os.sep

    #射角を諸元ファイルから取得
    launcher_elevation = str(json_load['Launch Condition']['Launch Elevation [deg]'])

    # 地図そのものを回転させて対処する機能は廃止
    magneticdec = 'y'

    #グラフの描画
    if mode_simulation == "single":
        
        flightgrapher(resultpath, launch_site_info, safety_exist, exist_payload)

    elif mode_simulation == "multi":
        
        plotlandingscatter(resultpath, launcher_elevation, launch_site_info, exist_payload, magneticdec, safety_exist)

def copy_config_files(json_config, path_result):

    plt.rcParams['font.family'] = 'Arial'
    plt.rcParams['font.size']   = 12
    plt.rcParams['figure.titlesize'] = 13
    plt.rcParams["xtick.direction"]   = "in"
    plt.rcParams["ytick.direction"]   = "in"
    plt.rcParams["xtick.top"]         = True
    plt.rcParams["ytick.right"]       = True
    plt.rcParams["xtick.major.width"] = 1.5
    plt.rcParams["ytick.major.width"] = 1.5
    plt.rcParams["axes.linewidth"] = 1.5

    dir_config = path_result + os.sep + '_00_config'
    model_name = json_config['Solver']['Name']
    os.mkdir(dir_config)

    json_copy = json_config
    json_copy['Solver']["Result Filepath"] = dir_config

    shutil.copy(json_config["Engine"]["Thrust Curve"], dir_config + os.sep + model_name + '_thrust.csv')
    time_burn = json_config["Engine"]["Burn Time [sec]"]
    json_copy["Engine"]["Thrust Curve"] = dir_config + os.sep + model_name + '_thrust.csv'

    # Thrust
    fig = plt.figure(figsize=(9, 3))
    ax = fig.add_subplot()
    ax.set_title('Thrust vs. Time')
    thrust_array = np.loadtxt(json_config["Engine"]["Thrust Curve"], delimiter=',', skiprows=1)
    ax.plot(thrust_array[:, 0], thrust_array[:, 1], color='#FF4B00')
    ax.axvline(x=time_burn, color='black', linestyle='--', linewidth=2)
    ax.set_xlim(xmin=0., xmax=thrust_array[-1, 0])
    ax.set_ylim(ymin=0.)
    ymin, ymax = ax.get_ylim()
    ax.text(x=time_burn, y=0.95*ymax, s=' Burning Time', horizontalalignment='right', verticalalignment='top', rotation=90)
    ax.set_xlabel('Time [sec]')
    ax.set_ylabel('Thrust [N]')
    ax.grid()
    fig.savefig(dir_config + os.sep + '_thrust.png', bbox_inches='tight', pad_inches=0.1)

    # Wind
    if json_copy["Wind"]["Wind File Exist"]:
        shutil.copy(json_config["Wind"]["Wind File"], dir_config + os.sep + model_name + '_wind.csv')
        json_copy["Wind"]["Wind File"] = dir_config + os.sep + model_name + '_wind.csv'

        wind_array = np.loadtxt(json_config["Wind"]["Wind File"], delimiter=',', skiprows=1)
        fig, axes = plt.subplots(nrows=1, ncols=2)
        axes[0].set_title('Wind Speed vs. Altitude')
        axes[0].plot(wind_array[:, 1], wind_array[:, 0], color='#FF4B00', marker='o')
        axes[0].set_ylim(ymin=0.)
        axes[0].set_xlabel('Wind Speed [m/s]')
        axes[0].set_ylabel('Altitude [m]')
        axes[0].grid()
        axes[1].set_title('Wind Direction vs. Altitude')
        axes[1].plot(wind_array[:, 2], wind_array[:, 0], color='#FF4B00', marker='o')
        axes[1].set_ylim(ymin=0.)
        axes[1].set_xlabel('Wind Direction [deg]')
        axes[1].set_ylabel('Altitude [m]')
        axes[1].grid()
        fig.subplots_adjust(wspace=0.4)
        fig.savefig(dir_config + os.sep + '_wind.png')

    else:
        json_copy["Wind"]["Wind File"] = ''

    # Cd
    if json_copy["Aero"]["Cd File Exist"]:
        shutil.copy(json_config["Aero"]["Cd File"], dir_config + os.sep + model_name + '_Cd.csv')
        json_copy["Aero"]["Cd File"] = dir_config + os.sep + model_name + '_Cd.csv'

        fig, ax = plt.subplots()
        ax.set_title('$C_D$ vs. Mach')
        Cd_array = np.loadtxt(json_config["Aero"]["Cd File"], delimiter=',', skiprows=1)
        ax.plot(Cd_array[:, 0], Cd_array[:, 1], color='#FF4B00', marker='o')
        ax.set_xlim(xmin=0.)
        ax.set_xlabel('Mach Number [-]')
        ax.set_ylabel('$C_D$ [-]')
        ax.grid()
        fig.savefig(dir_config + os.sep + '_Cd.png')

    else:
        json_copy["Aero"]["Cd File"] = ''

    # Lcp
    if json_copy["Aero"]["Length-C.P. File Exist"]:
        shutil.copy(json_config["Aero"]["Length-C.P. File"], dir_config + os.sep + model_name + '_Lcp.csv')
        json_copy["Aero"]["Length-C.P. File"] = dir_config + os.sep + model_name + '_Lcp.csv'

        fig, ax = plt.subplots()
        ax.set_title('$L_{C.P.}$ vs. Mach')
        lcp_array = np.loadtxt(json_config["Aero"]["Length-C.P. File"], delimiter=',', skiprows=1)
        ax.plot(lcp_array[:, 0], lcp_array[:, 1], color='#FF4B00', marker='o')
        ax.set_xlim(xmin=0.)
        ax.set_xlabel('Mach Number [-]')
        ax.set_ylabel('$L_{C.P.}$ [m]')
        ax.grid()
        fig.savefig(dir_config + os.sep + '_Lcp.png')

    else:
        json_copy["Aero"]["Length-C.P. File"] = ''

    # CNa
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
