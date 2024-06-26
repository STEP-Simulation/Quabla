from FlightGrapher.file_reader import FileReader
from FlightGrapher.graph_plotter_trajectory import GraphPlotterTrajectory
import os
import sys
from FlightGrapher.graph_plotter_parachute import GraphPlotterParachute
from FlightGrapher.land_point import LandPoint
from FlightGrapher.make_kml import MakeKml

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
def flightgrapher(path, launch_site_info, safety_exist, exist_payload):

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
    make_kml = MakeKml(exist_payload)

    # グラフのプロット
    graph_trajectory.plot_graph(make_kml)
    graph_parachute.plot_graph(make_kml, True)

    if exist_payload:
        os.mkdir(path + os.sep +'_01_payload')
        df_log_payl, df_summary_payl = FileReader(path, 'flightlog_payload.csv').get_df()
        graph_paylaod = GraphPlotterParachute(df_log_payl, df_summary_payl, path, launch_site_info.launch_LLH)
        graph_paylaod.plot_graph(make_kml, False)

    make_kml.make_kml(path, launch_site_info.center_circle_LLH, launch_site_info.radius, launch_site_info.safety_area_LLH, \
                     launch_site_info.edge1_LLH, launch_site_info.edge2_LLH, safety_exist)
    make_kml.make_csv(path)
    
    land_point.set_land_point(path, exist_payload)
    land_point.make_land_point(launch_site_info, safety_exist)

    print('[Post Proc.] Done!\n')
