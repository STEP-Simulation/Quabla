import matplotlib.pyplot as plt
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
from cProfile import label

class LandPoint:
    def __init__(self, filepath, img):
        self.filepath = filepath
        self.img = img

    def get_point_parachute(self,point_ENU):
        self.point_ENU_parachute = point_ENU
        
    def get_point_trajectory(self,point_ENU):
        self.point_ENU_trajectory = point_ENU
        
    def make_land_point(self, site_name, xlim, ylim, safety_exsist):
        x_offset = 0.
        y_offset = 0.
        self.xlim = x_offset + np.array(xlim)
        self.ylim = y_offset + np.array(ylim)

        title = 'landing_point'
        fig = plt.figure(title, figsize=(8, 8))
        ax = fig.add_subplot()
        ax.set_title(title)
        ax.scatter(0.0, 0.0, color='r', marker='o', label='Launch point')
        ax.scatter(self.point_ENU_trajectory[0], self.point_ENU_trajectory[1], label='Trajectory', color='b', marker='o')
        ax.scatter(self.point_ENU_parachute[0], self.point_ENU_parachute[1], label='Parachute', color='orange', marker='o')
        if safety_exsist:
            if site_name == 'oshima_land' or site_name == 'noshiro_land':
                pass
            elif site_name == 'oshima_sea' or site_name == 'noshiro_sea':
                pass
        ax.set_aspect('equal')
        ax.set_xlim(self.xlim[0], self.xlim[1])
        ax.set_ylim(self.ylim[0], self.ylim[1])
        ax.imshow(Image.open(self.img), extent=(self.xlim[0], self.xlim[1], self.ylim[0], self.ylim[1]), aspect='equal')
        ax.grid(ls='--', alpha=0.6)
        ax.legend()
        
        fig.savefig(self.filepath + '/' + title + '.jpg')



def plot_graph(filepath, img, pos_trajectory, pos_parachute, xlim, ylim):
    # img = 'PlotLandingScatter/noshiro_land.png'
    title = 'landing_point'

    fig = plt.figure(title, figsize=(8, 8))
    ax = fig.add_subplot()
    ax.set_title(title)

    ax.scatter(0.0, 0.0, color='r', marker='o', label='Launch point')
    # ax.plot(pos_trajectory[0], pos_trajectory[1], label='Trajectory', color='b', marker='o')
    # ax.plot(pos_parachute[0], pos_parachute[1], label='Parachute', color='orange', marker='o')
    ax.scatter(pos_trajectory[0], pos_trajectory[1], label='Trajectory', color='b', marker='o')
    ax.scatter(pos_parachute[0], pos_parachute[1], label='Parachute', color='orange', marker='o')
    ax.set_aspect('equal')
    ax.set_xlim(xlim[0], xlim[1])
    ax.set_ylim(ylim[0], ylim[1])
    ax.imshow(Image.open(img), extent=(xlim[0], xlim[1], ylim[0], ylim[1]), aspect='equal')
    ax.grid(ls='--', alpha=0.6)
    ax.legend()

    fig.savefig(filepath + '/' + title + '.jpg')