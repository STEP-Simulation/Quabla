from FlightGrapher.file_reader import FileReader
from FlightGrapher.graph_plotter_trajectory import GraphPlotterTrajectory
import os
import sys
from FlightGrapher.graph_plotter_parachute import GraphPlotterParachute
from FlightGrapher.land_point import LandPoint
from FlightGrapher.make_kml import makekml

'''
Args
    path          : file path of results
    config_file   : Configuration file of rocket
    launch_site   : Kind of launch site
    launch_LLH    : absolute coordinate of launch site
    safety_circle : absolute coordinate of center of safety circle
    radius        : radius of center circle
    safety_area   : safety area for land 
    safety_line1  : edge 1 of safety line
    safety_line2  : edge 2 of safety line
    img           : File path of lauch site image
    safety_exsist : True or false of safety area
'''
def flightgrapher(path, launch_site_info, safety_exist):

    print("\n[Post Proc.] Start...")

    os.mkdir(path + os.sep +'_01_trajectory')
    os.mkdir(path + os.sep +'_01_parachute')

    # 指定されたcsvファイルから飛翔履歴の取得
    df_log_traj, df_summary_traj = FileReader(path, 'flightlog_trajectory.csv').get_df()
    df_log_para, df_summary_para = FileReader(path, 'flightlog_parachute.csv').get_df()

    # インスタンスの生成
    graph_trajectory = GraphPlotterTrajectory(df_log_traj, df_summary_traj, path, launch_site_info.launch_LLH)
    graph_parachute = GraphPlotterParachute(df_log_para, df_summary_para, path, launch_site_info.launch_LLH)

    land_point = LandPoint(path, launch_site_info.img)

    # グラフのプロット
    graph_trajectory.plot_graph(land_point)
    graph_parachute.plot_graph(land_point)

    makekml(path, launch_site_info.center_circle_LLH, launch_site_info.radius, launch_site_info.safety_area_LLH, \
            launch_site_info.edge1_LLH, launch_site_info.edge2_LLH, safety_exist)
    land_point.make_land_point(launch_site_info, safety_exist)

    print('[Post Proc.] Done!\n')
