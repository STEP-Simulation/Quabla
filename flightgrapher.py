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
    launch_site   : Kind of launch site
    launch_LLH    : absolute coordinate of launch site
    safety_circle : absolute coordinate of center of safety circle
    radius        : radius of center circle
    safety_area   : safety area for land 
    safety_line1  : edge 1 of safety line
    safety_line2  : edge 2 of safety line
    safety_exsist : True or false of safety area
'''
def flightgrapher(path, launch_site, launch_LLH, safety_circle, radius, safety_area, safety_line1, safety_line2, safety_exist):
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
        graph_trajectory = GraphPlotterTrajectory(logdata_array_trajectory, path, launch_LLH)
        graph_parachute = GraphPlotterParachute(logdata_array_parachute, path,launch_LLH)

        land_point = LandPoint(path)

        # グラフのプロット
        graph_trajectory.plot_graph(land_point)
        graph_parachute.plot_graph(graph_trajectory.get_index_coast(),land_point)

        makekml(path, safety_circle, radius, safety_area, safety_line1, safety_line2, safety_exist)
        land_point.make_land_point(launch_site)

        print('Done!\n')
