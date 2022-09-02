from PlotLandingScatter.launch_site.launch_site import LaunchSite


import numpy as np
import pandas as pd

import matplotlib.cm as cm

from PlotLandingScatter.get_landing_point import GetLandingPoint
from PlotLandingScatter.plot_graph import PlotGraph
from PlotLandingScatter.judge_inside.judge_inside_circle import JudgeInsideCircle
from PlotLandingScatter.launch_site.launch_site import magnetic_declination
import simplekml

class LaunchSiteNone(LaunchSite):
    def __init__(self, elevation, magnetic_dec_exist, launch_LLH, result_dir, glp_list):

        self.glp_tra = glp_list[0]
        self.glp_par = glp_list[1]
        self.wind_array = self.glp_tra.get_wind_name()
        self.safety_exist = False

        self.trajectory = PlotGraph("Trajectory " + elevation + "[deg]", result_dir, self.glp_tra)
        self.parachute = PlotGraph("Parachute " + elevation + "[deg]", result_dir, self.glp_par)

        self.magnetic_dec = 0.0
        if magnetic_dec_exist:
            self.magnetic_dec = - magnetic_declination(launch_LLH[0], launch_LLH[1])
        self.launch_LLH = launch_LLH

    def plot_landing_scatter(self):
        flight_mode = [self.trajectory, self.parachute]
        color_cm = [cm.cool, cm.Wistia]

        def plot_scatter(obj, color_cm):
            obj.ax.plot(0.0, 0.0, marker='o', color='r')
            i = 0
            for x, y in zip(obj.x_array, obj.y_array):
                obj.ax.plot(x, y, label=str(obj.wind_array[i]), color=color_cm(i / obj.row), marker='o')
                i += 1
            obj.ax.set_aspect('equal')
            obj.ax.set_xlim(-6000, 6000)
            obj.ax.set_ylim(-6000, 6000)

        for index, obj in enumerate(flight_mode):
            plot_scatter(obj, color_cm[index])
            obj.plot_circle([0.0, 0.0, 0.0], 5.0 * (10**3))
            obj.save_fig()

    def judge_inside(self):
        jic = JudgeInsideCircle(np.array([0.0, 0.0]), 5.0 * 10**3)

        judge_tra_list = []
        judge_par_list = []

        for point_tra, point_par in zip(self.glp_tra.pos_ENU_array, self.glp_par.pos_ENU_array):
            judge_tra = [jic.judge_inside(point) for point in point_tra]
            judge_par = [jic.judge_inside(point) for point in point_par]
            judge_tra_list.append(judge_tra)
            judge_par_list.append(judge_par)
        judge = [judge_tra and judge_par for judge_tra, judge_par in zip(judge_tra_list, judge_par_list)]

        angle_step = 360 / self.glp_tra.num_angle
        angle_array = np.arange(0.0, 360 + angle_step, angle_step)
        pd.DataFrame(judge, index=self.wind_array, columns=angle_array).to_csv(self.trajectory.result_dir + '/judge.csv')

    def output_kml(self):
        self.trajectory.output_kml(self.launch_LLH, self.magnetic_dec, simplekml.Color.limegreen, 0, 0, 0, 1, 0, self.safety_exist)
        self.parachute.output_kml(self.launch_LLH, self.magnetic_dec, simplekml.Color.maroon, 0, 0, 0, 1, 0, self.safety_exist)