import matplotlib.pyplot as plt
import os
import sys
from PlotLandingScatter.launch_site.launch_site import LaunchSite
from PlotLandingScatter.get_landing_point import GetLandingPoint
from PlotLandingScatter.plot_graph import PlotGraph

from PlotLandingScatter.launch_site.oshima_land import OshimaLand
from PlotLandingScatter.launch_site.oshima_sea import OshimaSea
from PlotLandingScatter.launch_site.noshiro_sea import NoshiroSea
from PlotLandingScatter.launch_site.noshiro_land import NoshiroLand
from PlotLandingScatter.launch_site.launch_site_none import LaunchSiteNone

def plotlandingscatter(path, launch_elevation, number, magneticflag, launch_LLH, safety_area, headquarters_LLH, fire_LLH, safety_circle, radius, safety_line1,safety_line2, safety_exist):
    print("\nPlotLandingScatter start...")

    elevation = launch_elevation
    dir_path = path
    trajectory_name = 'trajectory' + elevation + '[deg]'
    parachute_name = 'parachute' + elevation + '[deg]'

    launchsite = {
        '1' : 'oshima_land',
        '2' : 'oshima_sea',
        '3' : 'noshiro_land',
        '4' : 'noshiro_sea',
        '5' : 'other'
    }

    launch_site_name = launchsite[number]

    # シミュレーション時に磁気偏角を考慮している場合False, 考慮していない場合True(地図を回転させる)

    magnetic_dec_exist_flag = magneticflag
    if magnetic_dec_exist_flag == 'y':
        magnetic_dec_exist = False
    elif magnetic_dec_exist_flag == 'n':
        magnetic_dec_exist = True

    name_array = [trajectory_name, parachute_name]
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

    if launch_site_name == 'oshima_land':
        launch_site = OshimaLand(elevation, magnetic_dec_exist, dir_path, glp_list, launch_LLH, safety_area, headquarters_LLH, fire_LLH, safety_exist)
    elif launch_site_name == 'oshima_sea':
        launch_site = OshimaSea(elevation, magnetic_dec_exist, dir_path, glp_list, launch_LLH, safety_circle, radius, safety_line1,safety_line2, safety_exist)
    elif launch_site_name == 'noshiro_land':
        launch_site = NoshiroLand(elevation, magnetic_dec_exist, dir_path, glp_list, launch_LLH, safety_area, safety_exist)
    elif launch_site_name == 'noshiro_sea':
        launch_site = NoshiroSea(elevation, magnetic_dec_exist, dir_path, glp_list, launch_LLH, safety_circle, radius, safety_line1,safety_line2, safety_exist)
    else:
        print('\nNot Input Launch Site!!')
        launch_site = LaunchSiteNone(elevation, magnetic_dec_exist, launch_LLH, dir_path, glp_list)

    launch_site.plot_landing_scatter()
    #launch_site.judge_inside()
    launch_site.output_kml()

    print('Done!\n')
