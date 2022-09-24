import json
import matplotlib.pyplot as plt
import numpy as np
from mpl_toolkits.mplot3d import Axes3D
from PlotLandingScatter.coordinate import ENU2LLH
from FlightGrapher.make_kml import getparachutepoint, post_kml
import os

class GraphPlotterParachute:
    def __init__(self, df, filepath, config_file, launch_LLH):
        self.filepath = filepath

        self.time_array = np.array(df['time [sec]'])
        self.pos_ENU_log = np.array([[east, north, up] 
                                     for east, north, up 
                                     in zip(df['pos_east [m]'], 
                                            df['pos_north [m]'], 
                                            df['pos_up [m]'])])
        self.vel_ENU_log = np.array([[v_east, v_north, v_up] 
                                     for v_east, v_north, v_up 
                                     in zip(df['vel_east [m/s]'], 
                                            df['vel_north [m/s]'], 
                                            df['vel_up [m/s]'])])
        self.altitude_log = np.array(df['altitude [km]'])
        self.downrange_log = np.array(df['downrange [km]'])
        self.vel_air_ENU_log = np.array([[vair_east, vair_north, vair_up] 
                                         for vair_east, vair_north, vair_up 
                                         in zip(df['vel_air_east [m/s]'], 
                                                df['vel_air_north [m/s]'], 
                                                df['vel_air_up [m/s]'])])
        self.vel_air_abs_log = np.array(df['vel_air_abs [m/s]'])

        self.flighType = 'Parachute'

        # self.point = [logdata[len(self.time_array)-1, 1], logdata[len(self.time_array)-1, 2], logdata[len(self.time_array)-1, 3]]
        self.point = [self.pos_ENU_log[-1, 0], self.pos_ENU_log[-1, 1], self.pos_ENU_log[-1, 2]]
        self.Launch_LLH = launch_LLH

        index_apogee = np.argmax(self.pos_ENU_log[:,2])
        time_apogee = self.time_array[index_apogee]
        time_para_open_lag = config_file.get('Parachute').get('Parachute Opening Lag [sec]')
        time_1st_para = time_apogee + time_para_open_lag
        self.index_1st_para = np.argmax(time_1st_para <= self.time_array[:])

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
        # ax = fig1.gca(projection = '3d')
        ax = fig1.add_subplot(projection='3d')
        ax.set_xlabel('East [m]')
        ax.set_ylabel('North [m]')
        ax.set_zlabel('Up [m]')
        ax.set_title('Trajectory')
        ax.plot(self.pos_ENU_log[:index_coast,0],self.pos_ENU_log[:index_coast,1],self.pos_ENU_log[:index_coast,2], label='Powered')
        ax.plot(self.pos_ENU_log[index_coast:self.index_1st_para,0], self.pos_ENU_log[index_coast:self.index_1st_para,1], self.pos_ENU_log[index_coast:self.index_1st_para,2], label='Coasting')
        ax.plot(self.pos_ENU_log[self.index_1st_para:,0], self.pos_ENU_log[self.index_1st_para:,1], self.pos_ENU_log[self.index_1st_para:,2], label='Parachute')
        # xmin, xmax = ax.get_xlim()
        # ymin, ymax = ax.get_ylim()
        # zmin, zmax = ax.get_zlim()
        # minbound = min((xmin, ymin, zmin))
        # maxbound = max((xmax, ymax, zmax))
        # ax.auto_scale_xyz([minbound, maxbound], [minbound, maxbound], [0., maxbound])
        # ax.set_xlim(minbound, maxbound)
        # ax.set_ylim(minbound, maxbound)
        # ax.pbaspect = [1.0, 1.0, maxbound/(maxbound+abs(minbound))]
        # plt.figaspect(maxbound/(maxbound+abs(minbound)))
        ax.scatter(origin[0], origin[1], origin[2], marker='o', label='Launch Point', color='r')
        ax.scatter(self.pos_ENU_log[-1,0],self.pos_ENU_log[-1,1],self.pos_ENU_log[-1,2],label='Landing Point', s=30, marker='*',color='y')
        ax.legend()
        # ax.set_aspect('equal')
        ax.set_zlim(bottom=0.0)
        fig1.savefig(self.filepath + os.sep + flightType + os.sep + 'Flightlog.png')

        fig2 = plt.figure('Trajectory'+ flightType)
        trajectory = fig2.add_subplot()
        trajectory.set_title('Trajectory')
        trajectory.plot(self.downrange_log ,self.altitude_log)
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

        vENU2LLH = np.vectorize(ENU2LLH, excluded=['launch_LLH'], signature="(1),(3)->(3)")
        log_LLH = vENU2LLH(self.Launch_LLH, self.pos_ENU_log)
        point_LLH = ENU2LLH(self.Launch_LLH, self.point)
        getparachutepoint(point_LLH)
        post_kml(log_LLH, self.filepath, 'soft')
        land_point.get_point_parachute(self.point)
        

