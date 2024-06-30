import os
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import PlotLandingScatter.coordinate as cd
import matplotlib.patches as patches
import pandas as pd

class LandPoint:
    def __init__(self, filepath, img):
        self.filepath = filepath
        self.img = img

    def set_point_parachute(self, pos_NED):
        self.pos_NED_para = pos_NED
        
    def set_point_trajectory(self, pos_NED):
        self.pos_NED_traj = pos_NED

    def set_land_point(self, path, exist_paylaod):

        self.exist_payload = exist_paylaod

        df = pd.read_csv(path + os.sep + 'land_point_NED.csv')
        self.pos_NED_traj = np.array(df['Trajectory'])
        self.pos_NED_para = np.array(df['Parachute'])
        if exist_paylaod:
            self.pos_NED_payl = np.array(df['Payload'])
        
    def make_land_point(self, launch_site_info, safety_exsist):
        self.xlim = launch_site_info.x_offset + np.array(launch_site_info.xlim)
        self.ylim = launch_site_info.y_offset + np.array(launch_site_info.ylim)

        title = '_03_landing_point'
        fig = plt.figure(title, figsize=(8, 8))
        ax = fig.add_subplot()
        ax.set_title(title)
        ax.scatter(0.0, 0.0, color='r', marker='o', label='Launch point')
        ax.scatter(self.pos_NED_traj[1], self.pos_NED_traj[0], label='Trajectory', color='b', marker='o')
        ax.scatter(self.pos_NED_para[1], self.pos_NED_para[0], label='Parachute', color='orange', marker='o')
        if self.exist_payload:
            ax.scatter(self.pos_NED_payl[1], self.pos_NED_payl[0], label='Payload', color='teal', marker='o')
        if safety_exsist:
            # if launch_site_info.site_name == 'oshima_land' or launch_site_info.site_name == 'noshiro_land':
            if launch_site_info.type_safety == 'polygon':
                safety_ENU = [cd.LLH2ENU(launch_site_info.launch_LLH, LLH).tolist() for LLH in launch_site_info.safety_area_LLH]
                if not safety_ENU[-1] == safety_ENU[0]:
                    safety_ENU.append(safety_ENU[0])
                apex = np.array(safety_ENU)
                ax.plot(apex[:, 0], apex[:, 1], color='y', ls='dashed')
            elif launch_site_info.type_safety == 'circle':
                center_circle_ENU = cd.LLH2ENU(launch_site_info.launch_LLH, launch_site_info.center_circle_LLH)
                ax.scatter(center_circle_ENU[0], center_circle_ENU[1], color='red', marker='o')
                ax.add_patch(patches.Circle(xy=(center_circle_ENU[0], center_circle_ENU[1]), radius=launch_site_info.radius, ls='dashed', ec='y', fc='None'))
                edge_LLH = np.array([launch_site_info.edge1_LLH, launch_site_info.edge2_LLH])
                edge_ENU = np.array([cd.LLH2ENU(launch_site_info.launch_LLH, LLH) for LLH in edge_LLH])
                ax.plot(edge_ENU[:, 0], edge_ENU[:, 1], color='y', ls='dashed')
        ax.set_aspect('equal')
        ax.set_xlim(self.xlim[0], self.xlim[1])
        ax.set_ylim(self.ylim[0], self.ylim[1])
        ax.imshow(Image.open(self.img), extent=(self.xlim[0], self.xlim[1], self.ylim[0], self.ylim[1]), aspect='equal')
        ax.grid(ls='--', alpha=0.6)
        ax.legend()
        
        fig.savefig(self.filepath + os.sep + title + '.jpg')

        pos_LLH_traj = cd.ENU2LLH(launch_site_info.launch_LLH, self.pos_NED_traj)
        pos_LLH_para = cd.ENU2LLH(launch_site_info.launch_LLH, self.pos_NED_para)



# def plot_graph(filepath, img, pos_trajectory, pos_parachute, xlim, ylim):
#     # img = 'PlotLandingScatter/noshiro_land.png'
#     title = 'landing_point'

#     fig = plt.figure(title, figsize=(8, 8))
#     ax = fig.add_subplot()
#     ax.set_title(title)

#     ax.scatter(0.0, 0.0, color='r', marker='o', label='Launch point')
#     # ax.plot(pos_trajectory[0], pos_trajectory[1], label='Trajectory', color='b', marker='o')
#     # ax.plot(pos_parachute[0], pos_parachute[1], label='Parachute', color='orange', marker='o')
#     ax.scatter(pos_trajectory[0], pos_trajectory[1], label='Trajectory', color='b', marker='o')
#     ax.scatter(pos_parachute[0], pos_parachute[1], label='Parachute', color='orange', marker='o')
#     ax.set_aspect('equal')
#     ax.set_xlim(xlim[0], xlim[1])
#     ax.set_ylim(ylim[0], ylim[1])
#     ax.imshow(Image.open(img), extent=(xlim[0], xlim[1], ylim[0], ylim[1]), aspect='equal')
#     ax.grid(ls='--', alpha=0.6)
#     ax.legend()

#     fig.savefig(filepath + '/' + title + '.jpg')