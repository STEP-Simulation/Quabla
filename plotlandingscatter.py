import os
from PlotLandingScatter.launch_site.launch_site import LaunchSite
from PlotLandingScatter.get_landing_point import GetLandingPoint

def plotlandingscatter(path, launch_elevation, launch_site_info, exist_payload, magneticflag, safety_exist):
    print("\n[Post Proc.] Start...")

    elevation = launch_elevation
    dir_path = path
    trajectory_name = 'trajectory' + elevation + '[deg]'
    parachute_name = 'parachute' + elevation + '[deg]'
    payload_name = 'payload' + elevation + '[deg]'

    # シミュレーション時に磁気偏角を考慮している場合False, 考慮していない場合True(地図を回転させる)
    magnetic_dec_exist_flag = magneticflag
    if magnetic_dec_exist_flag == 'y':
        magnetic_dec_exist = False
    elif magnetic_dec_exist_flag == 'n':
        magnetic_dec_exist = True

    name_array = [trajectory_name, parachute_name]
    if exist_payload:
        name_array.append(payload_name)

    glp_list = []
    # 落下地点の取得
    for name in name_array:
        filepath = dir_path +  name + '.csv'
        glp = GetLandingPoint(filepath)
        glp_list.append(glp)

    if os.path.exists(dir_path + trajectory_name + '.csv'):
        pass
    else :
        print('\nNot Found File')

    launch_site = LaunchSite(elevation, exist_payload, dir_path, glp_list, launch_site_info, safety_exist)

    launch_site.plot_landing_scatter()
    launch_site.judge_inside()
    launch_site.output_kml()

    print('[Post Proc.] Done!\n')
