import matplotlib.pyplot as plt
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
from cProfile import label

class LandPoint:
    def __init__(self, filepath):
        self.filepath = filepath

    def get_point_parachute(self,point_ENU):
        self.point_ENU_parachute = point_ENU
        
    def get_point_trajectory(self,point_ENU):
        self.point_ENU_trajectory = point_ENU
        
    def make_land_point(self, launch_site):
        if launch_site == '1':
            x_offset = 0.0
            y_offset = 0.0
            xlim = np.array([-819 + x_offset, 795 + x_offset])
            ylim = np.array([-825 + y_offset, 705 + y_offset])
            img = 'PlotLandingScatter/oshima_land.png'
            plot_graph(self.filepath, img, self.point_ENU_trajectory, self.point_ENU_parachute, xlim, ylim)

        elif launch_site == '2':
            x_offset = 0.0
            y_offset = 0.0
            xlim = np.array([-2015 + x_offset, 4810 + x_offset])
            ylim = np.array([-4860 + y_offset, 1050 + y_offset])
            img = 'PlotLandingScatter/oshima_sea.png'
            plot_graph(self.filepath, img, self.point_ENU_trajectory, self.point_ENU_parachute, xlim, ylim)
        
        elif launch_site == '3':
            x_offset = 0.0
            y_offset = 0.0
            xlim = np.array([-495 + x_offset, 205 + x_offset])
            ylim = np.array([-512 + y_offset, 198 + y_offset])
            img = 'PlotLandingScatter/noshiro_land.png'
            plot_graph(self.filepath, img, self.point_ENU_trajectory, self.point_ENU_parachute, xlim, ylim)
        
        elif launch_site == '4':
            x_offset = 0.0
            y_offset = 0.0
            xlim = np.array([-3500 + x_offset, 500 + x_offset])
            ylim = np.array([-1500 + y_offset, 2000 + y_offset])
            img = 'PlotLandingScatter/noshiro_sea.png'
            plot_graph(self.filepath, img, self.point_ENU_trajectory, self.point_ENU_parachute, xlim, ylim)



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