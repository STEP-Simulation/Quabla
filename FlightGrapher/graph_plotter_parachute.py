import matplotlib.pyplot as plt
import numpy as np
from mpl_toolkits.mplot3d import Axes3D
from PlotLandingScatter.coordinate import ENU2LLH
from FlightGrapher.make_kml import getparachutepoint
import os

class GraphPlotterParachute:
    def __init__(self, logdata, filepath, launch_LLH):
        self.filepath = filepath

        self.time_array = logdata[:,0]
        self.pos_ENU_log = logdata[:, 1:4]
        self.vel_ENU_log = logdata[:, 4:7]
        self.altitude_log = logdata[:, 7]
        self.downrange_log = logdata[:, 8]
        self.vel_air_ENU_log = logdata[:,9:12]
        self.vel_air_abs_log = logdata[:,12]

        self.flighType = 'Parachute'

        self.point = [logdata[len(self.time_array)-1, 1], logdata[len(self.time_array)-1, 2], logdata[len(self.time_array)-1, 3]]
        self.Launch_LLH = launch_LLH

        self.index_apogee = np.argmax(self.pos_ENU_log[:,2])

    def plot_graph(self,index_coast,land_point):
        flightType = 'Parachute'

        plt.close('all')

        plt.figure('Position_ENU' + flightType)
        plt.title('Position ENU')
        plt.plot(self.time_array, self.pos_ENU_log[:,0], label='Pos_East')
        plt.plot(self.time_array, self.pos_ENU_log[:,1], label='Pos_North')
        plt.plot(self.time_array, self.pos_ENU_log[:,2], label='Pos_Up')
        plt.xlabel('Time [sec]')
        plt.ylabel('Position [m]')
        plt.xlim(xmin = 0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + os.sep + flightType + os.sep + 'Position_ENU.png')

        fig1 = plt.figure('Flightlog' + flightType)
        origin = np.array([0.0, 0.0, 0.0])
        ax = fig1.gca(projection = '3d')
        ax.set_xlabel('East [m]')
        ax.set_ylabel('North [m]')
        ax.set_zlabel('Up [m]')
        ax.set_title('Trajectory')
        ax.plot(self.pos_ENU_log[:index_coast,0],self.pos_ENU_log[:index_coast,1],self.pos_ENU_log[:index_coast,2], label='Powered')
        ax.plot(self.pos_ENU_log[index_coast:self.index_apogee,0], self.pos_ENU_log[index_coast:self.index_apogee,1], self.pos_ENU_log[index_coast:self.index_apogee,2], label='Coasting')
        ax.plot(self.pos_ENU_log[self.index_apogee:,0], self.pos_ENU_log[self.index_apogee:,1], self.pos_ENU_log[self.index_apogee:,2], label='Parachute')
        ax.scatter(origin[0], origin[1], origin[2], marker='o', label='Launch Point', color='r')
        ax.scatter(self.pos_ENU_log[-1,0],self.pos_ENU_log[-1,1],self.pos_ENU_log[-1,2],label='Landing Point', s=30, marker='*',color='y')
        ax.legend()
        ax.set_zlim(bottom=0.0)
        fig1.savefig(self.filepath + os.sep + flightType + os.sep + 'Flightlog.png')

        fig2 = plt.figure('Trajectory'+ flightType)
        trajectory = fig2.add_subplot()
        trajectory.set_title('Trajectory')
        trajectory.plot(self.downrange_log / 1000.0 ,self.altitude_log / 1000.0)
        trajectory.set_xlabel('Downrange [km]')
        trajectory.set_ylabel('Altitude [km]')
        trajectory.set_ylim(ymin = 0.0)
        trajectory.grid()
        trajectory.set_aspect('equal')
        plt.savefig(self.filepath + os.sep + flightType + os.sep + 'Trajectory.png')

        fig2 = plt.figure('Downrange' + flightType)
        trajectory = fig2.add_subplot()
        trajectory.set_title('Downrange')
        trajectory.plot(self.pos_ENU_log[:, 0] / 1000.0 , self.pos_ENU_log[:, 1] / 1000.0)
        trajectory.set_xlabel('East [km]')
        trajectory.set_ylabel('North [km]')
        trajectory.grid()
        trajectory.set_aspect('equal')
        plt.savefig(self.filepath + os.sep + flightType +  os.sep + 'Downrange.png')

        plt.figure('Vel_ENU' + flightType)
        plt.title('Vel_ENU')
        plt.plot(self.time_array, self.vel_ENU_log[:,0], label = 'Vel_x_ENU')
        plt.plot(self.time_array, self.vel_ENU_log[:,1], label = 'Vel_y_ENU')
        plt.plot(self.time_array, self.vel_ENU_log[:,2], label = 'Vel_z_ENU')
        plt.xlabel('Time [sec]')
        plt.ylabel('Velocity [m/s]')
        plt.xlim(xmin = 0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + os.sep + flightType + os.sep + 'Vel_ENU.png')

        point_LLH = ENU2LLH(self.Launch_LLH, self.point)
        getparachutepoint(point_LLH)
        land_point.get_point_parachute(self.point)
        

