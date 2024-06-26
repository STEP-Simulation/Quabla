from PlotLandingScatter.launch_site.launch_site import LaunchSite

import numpy as np
import matplotlib.cm as cm
import pandas as pd

from PlotLandingScatter.get_landing_point import GetLandingPoint
from PlotLandingScatter.plot_graph import PlotGraph
from PlotLandingScatter.launch_site.launch_site import trans_matrix
import PlotLandingScatter.coordinate as cd
from PlotLandingScatter.judge_inside.judge_inside_circle import JudgeInsideCircle
import simplekml
from PlotLandingScatter.judge_inside.judge_inside_border import JudgeInsideBorder
from PlotLandingScatter.launch_site.launch_site import magnetic_declination

class NoshiroSea(LaunchSite):

    def __init__(self, elevation, exist_payload, magnetic_dec_exist, result_dir, glp_list, launch_site_info, safety_exist):
        # self.img = 'PlotLandingScatter/noshiro_sea.png'
        self.img = launch_site_info.img
        self.exist_payload = exist_payload

        self.glp_tra = glp_list[0]
        self.glp_par = glp_list[1]
        self.wind_array = self.glp_tra.get_wind_name()
        self.safety_exist = safety_exist

        self.trajectory = PlotGraph("Trajectory " + elevation + "[deg]", result_dir, self.glp_tra)
        self.parachute  = PlotGraph("Parachute " + elevation + "[deg]", result_dir, self.glp_par)
        if exist_payload:
            self.glp_pay = glp_list[2]
            self.payload = PlotGraph("Payload " + elevation + "[deg]", result_dir, self.glp_pay)

        # 射点
        self.launch_LLH = launch_site_info.launch_LLH
        # 保安円半径
        self.radius = launch_site_info.radius
        # 端点
        self.safety_line1 = launch_site_info.edge1_LLH
        self.safety_line2 = launch_site_info.edge2_LLH
        # 保安円中心
        self.center_circle_LLH = launch_site_info.center_circle_LLH
        # 境界線端点
        edge1_LLH = launch_site_info.edge1_LLH
        edge2_LLH = launch_site_info.edge2_LLH

        self.magnetic_dec = 0.0
        x_offset = 0.0
        y_offset = 0.0
        if magnetic_dec_exist :
            self.magnetic_dec = - magnetic_declination(self.launch_LLH[0], self.launch_LLH[1])
            x_offset = 70.0
            y_offset = 230.0
            print(self.magnetic_dec)
        point_LLH_array = [self.center_circle_LLH, edge1_LLH, edge2_LLH]
        point_ENU_array = []
        matrix = trans_matrix(np.deg2rad(self.magnetic_dec))
        for point_LLH in point_LLH_array:
            point_ENU = cd.LLH2ENU(self.launch_LLH, point_LLH)
            point_ENU_array.append(matrix.dot(point_ENU))
        self.center_circle_ENU, self.edge1_ENU, self.edge2_ENU = point_ENU_array
        self.xlim = launch_site_info.x_offset + np.array(launch_site_info.xlim)
        self.ylim = launch_site_info.y_offset + np.array(launch_site_info.ylim)

    def plot_landing_scatter(self):
        flight_mode = [self.trajectory, self.parachute]
        color_cm = [cm.cool, cm.spring]
        if self.exist_payload:
            flight_mode.append(self.payload)
            color_cm.append(cm.autumn)

        for index, obj in enumerate(flight_mode):
            obj.ax.plot(0.0, 0.0, marker='o', markersize=3, color='r')
            if self.safety_exist == True:
                obj.plot_circle(self.center_circle_ENU, self.radius)
                obj.plot_line(self.edge1_ENU, self.edge2_ENU)
            obj.grid_on()
            obj.plot_scatter(self.img, self.xlim, self.ylim, color_cm[index], self.magnetic_dec)
            obj.save_fig()

    def judge_inside(self):
        jic = JudgeInsideCircle(self.center_circle_ENU, self.radius)
        jib = JudgeInsideBorder(self.edge1_ENU, self.edge2_ENU, self.center_circle_ENU)

        judge_list = []
        for point_tra, point_par in zip(self.glp_tra.pos_ENU_array, self.glp_par.pos_ENU_array):
            judge_tra = [jic.judge_inside(point) and jib.judge_inside(point) for point in point_tra]
            judge_par = [jic.judge_inside(point) and jib.judge_inside(point) for point in point_par]
            judge_list.append([judge_traje and judge_para for judge_traje, judge_para in zip(judge_tra, judge_par)])

        angle_step = 360 / self.glp_tra.num_angle
        angle_array = np.arange(0.0, 360 + angle_step, angle_step)
        pd.DataFrame(judge_list, index=self.wind_array, columns=angle_array).to_csv(self.trajectory.result_dir + '/judge.csv')

    def output_kml(self):
        self.trajectory.output_kml(self.launch_LLH, self.magnetic_dec, cm.cool, self.radius, self.safety_line1,self.safety_line2, self.center_circle_LLH, 0, self.safety_exist)
        self.parachute.output_kml(self.launch_LLH, self.magnetic_dec, cm.spring, self.radius, self.safety_line1,self.safety_line2, self.center_circle_LLH, 0, self.safety_exist)