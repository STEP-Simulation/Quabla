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
def flightgrapher(path, config_file, launch_site_info, safety_exist):
    print("\nFlightGrapher start...")

    filepath_trajectory = path + os.sep +'flightlog_trajectory.csv'
    filepath_parachute = path + os.sep + 'flightlog_parachute.csv'

    # make directory
    # ゴミコード
    if os.path.exists(path + '/Trajectory'):
        pass
    else :
        os.mkdir(path + '/Trajectory')

    if os.path.exists(path + '/Parachute'):
        pass
    else:
        os.mkdir(path + '/Parachute')

        # 指定されたcsvファイルから飛翔履歴の取得
        logdata_array_trajectory = FileReader(filepath_trajectory).get_logdata()
        logdata_array_parachute = FileReader(filepath_parachute).get_logdata()

        # インスタンスの生成
        graph_trajectory = GraphPlotterTrajectory(logdata_array_trajectory, path, launch_site_info.launch_LLH)
        graph_parachute = GraphPlotterParachute(logdata_array_parachute, path, config_file, launch_site_info.launch_LLH)

        land_point = LandPoint(path, launch_site_info.img)

        # グラフのプロット
        graph_trajectory.plot_graph(land_point)
        graph_parachute.plot_graph(graph_trajectory.get_index_coast(),land_point)

        makekml(path, launch_site_info.center_circle_LLH, launch_site_info.radius, launch_site_info.safety_area_LLH, \
                launch_site_info.edge1_LLH, launch_site_info.edge2_LLH, safety_exist)
        land_point.make_land_point(launch_site_info, safety_exist)

        print('Done!\n')
