from PlotLandingScatter.launch_site.launch_site import LaunchSite

import numpy as np
import matplotlib.cm as cm
import pandas as pd

from PlotLandingScatter.get_landing_point import GetLandingPoint
from PlotLandingScatter.plot_graph import PlotGraph
from PlotLandingScatter.launch_site.launch_site import trans_matrix
import PlotLandingScatter.coordinate as cd
from PlotLandingScatter.judge_inside.judge_inside_poly import JudgeInsidePoly
from PlotLandingScatter.judge_inside.judge_inside_circle import JudgeInsideCircle
import simplekml
from PlotLandingScatter.launch_site.launch_site import magnetic_declination

class OshimaLand(LaunchSite):

    def __init__(self, elevation, magnetic_dec_exist, result_dir, glp_list, launch_site_info, safety_exist):
        self.img = launch_site_info.img

        self.glp_tra = glp_list[0]
        self.glp_par = glp_list[1]
        self.wind_array = self.glp_tra.get_wind_name()
        self.safety_exist = safety_exist

        self.trajectory = PlotGraph("Trajectory " + elevation + "[deg]", result_dir, glp_list[0])
        self.parachute = PlotGraph("Parachute " + elevation + "[deg]", result_dir, glp_list[1])

        # 射点
        self.launch_LLH = launch_site_info.launch_LLH
        # 落下保安域
        self.safety_LLH = launch_site_info.safety_area_LLH
        # 本部
        headquarters_ENU = cd.LLH2ENU(self.launch_LLH, launch_site_info.headquarters_LLH)
        # 点火点
        fire_ENU = cd.LLH2ENU(self.launch_LLH, launch_site_info.fire_LLH)

        self.magnetic_dec = 0.0
        x_offset = 0.0
        y_offset = 0.0
        if magnetic_dec_exist:
            self.magnetic_dec = - magnetic_declination(self.launch_LLH[0], self.launch_LLH[1])
            x_offset = -6.0
            y_offset = 2.0
        matrix = trans_matrix(np.deg2rad(self.magnetic_dec))
        self.xlim = launch_site_info.x_offset + np.array(launch_site_info.xlim)
        self.ylim = launch_site_info.y_offset + np.array(launch_site_info.ylim)

        # 磁気偏角が存在する場合,回転行列で座標変換
        self.headquarters_ENU = matrix.dot(headquarters_ENU)
        self.fire_ENU = matrix.dot(fire_ENU)
        row = self.safety_LLH.shape[0]
        self.safety_ENU = np.zeros((row,3))
        for i in range(row):
            self.safety_ENU[i,0:3] = cd.LLH2ENU(self.launch_LLH, self.safety_LLH[i, 0:3])
            self.safety_ENU[i,0:3] = matrix.dot(self.safety_ENU[i,0:3])

    def plot_landing_scatter(self):

        flight_mode = [self.trajectory, self.parachute]
        color_cm = [cm.winter, cm.Wistia]

        for index, obj in enumerate(flight_mode):
            if self.safety_exist == True:
                obj.plot_polygon(self.safety_ENU)
                obj.plot_circle(np.array([0.0, 0.0, 0.0]), 50.0)
                obj.plot_circle(self.headquarters_ENU, 50.0)
                obj.plot_circle(self.fire_ENU, 50.0)
            obj.plot_scatter(self.img, self.xlim, self.ylim, color_cm[index], self.magnetic_dec)
            obj.save_fig()

    def judge_inside(self):
        jip = JudgeInsidePoly(self.safety_ENU)
        jic_launch = JudgeInsideCircle(np.array([0.0, 0.0]), 50.0)
        jic_fire = JudgeInsideCircle(self.fire_ENU, 50.0)
        jic_head = JudgeInsideCircle(self.headquarters_ENU, 50.0)

        judge_list = []
        for point_tra, point_par in zip(self.glp_tra.pos_ENU_array, self.glp_par.pos_ENU_array):
            judge_tra_list = [all([jip.judge_inside(point), not jic_launch.judge_inside(point), not jic_fire.judge_inside(point), not jic_head.judge_inside(point)]) for point in point_tra]
            judge_par_list = [all([jip.judge_inside(point), not jic_launch.judge_inside(point), not jic_fire.judge_inside(point), not jic_head.judge_inside(point)]) for point in point_par]
            judge_list.append([judge_tra and judge_par for judge_tra, judge_par in zip(judge_tra_list, judge_par_list)])

        angle_step = 360 / (self.glp_tra.num_angle)
        angle_array = np.arange(0.0, 360 + angle_step, angle_step)
        pd.DataFrame(judge_list, index=self.wind_array, columns=angle_array).to_csv(self.trajectory.result_dir + '/judge.csv')

    def output_kml(self):
        self.trajectory.output_kml(self.launch_LLH, self.magnetic_dec, cm.winter, 0, 0, 0, 0, self.safety_LLH,self.safety_exist)
        self.parachute.output_kml(self.launch_LLH, self.magnetic_dec, cm.Wistia,  0, 0, 0, 0, self.safety_LLH, self.safety_exist)