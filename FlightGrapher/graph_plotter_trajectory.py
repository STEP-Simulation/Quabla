import matplotlib.pyplot as plt
from cProfile import label
from mpl_toolkits.mplot3d import Axes3D
from mpl_toolkits.mplot3d import proj3d
import numpy as np
from matplotlib.pyplot import subplot
from PlotLandingScatter.coordinate import ENU2LLH
from FlightGrapher.make_kml import gettrajectorypoint, post_kml

class GraphPlotterTrajectory:

    def __init__(self, logdata, filepath, launch_LLH):
        self.filepath = filepath

        self.time_array = logdata[:, 0]
        self.pos_ENU_log = logdata[:, 1:4]
        self.vel_ENU_log = logdata[:, 4:7]
        self.omega_Body_log = logdata[:, 7:10]
        self.quat_log = logdata[:, 10:14]
        self.attitude_log = logdata[:,14:17]
        self.mass_log = logdata[:, 17]
        self.Lcg_log = logdata[:, 18]
        self.Lcg_prop_log = logdata[:, 19]
        self.Lcp_log = logdata[:, 20]
        self.Ij_roll_log = logdata[:,21]
        self.Ij_pitch_log = logdata[:,22]
        self.altitude_log = logdata[:, 23]
        self.downrange_log = logdata[:, 24]
        self.vel_air_ENU_log = logdata[:,25:28]
        self.vel_air_BODY_log = logdata[:,28:31]
        self.Vel_air_abs_log = logdata[:, 31]
        self.alpha_log = logdata[:, 32]
        self.beta_log = logdata[:, 33]
        self.Mach_log = logdata[:, 34]
        self.dynamics_perssure_log = logdata[:, 35]
        self.Fst_log = logdata[:, 36]
        self.drag_log = logdata[:, 37]
        self.normal_log = logdata[:, 38]
        self.side_log = logdata[:, 39]
        self.thrust_log = logdata[:, 40]
        self.force_log = logdata[:, 41:44]
        self.acc_ENU_log = logdata[:, 44:47]
        self.acc_BODY_log = logdata[:, 47:50]
        self.acc_abs_log = logdata[:, 50]

        self.index_apogee = np.argmax(self.pos_ENU_log[:,2])
        self.index_coast = np.argmin(self.thrust_log[1:])

        self.point = [logdata[len(self.time_array)-1, 1], logdata[len(self.time_array)-1, 2], logdata[len(self.time_array)-1, 3]]
        self.Launch_LLH = launch_LLH

    def plot_graph(self,land_point):
        flightType = 'Trajectory'

        plt.close('all')

        plt.figure('Position ENU' + flightType)
        plt.title('Position ENU')
        plt.plot(self.time_array, self.pos_ENU_log[:, 0], label='Pos_East')
        plt.plot(self.time_array, self.pos_ENU_log[:, 1], label='Pos_North')
        plt.plot(self.time_array, self.pos_ENU_log[:, 2], label='Pos_Up')
        plt.xlabel('Time [sec]')
        plt.ylabel('Position [m]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Position_ENU.png')

        fig1 = plt.figure('Flightlog' + flightType)
        origin = np.zeros(3)
        ax = fig1.gca(projection='3d')
        ax.set_xlabel('East [m]')
        ax.set_ylabel('North [m]')
        ax.set_zlabel('Up [m]')
        ax.set_title('Trajectory')
        ax.plot(self.pos_ENU_log[:self.index_coast, 0], self.pos_ENU_log[:self.index_coast, 1], self.pos_ENU_log[:self.index_coast, 2], label='Powered')
        ax.plot(self.pos_ENU_log[self.index_coast:self.index_apogee, 0], self.pos_ENU_log[self.index_coast:self.index_apogee, 1], self.pos_ENU_log[self.index_coast:self.index_apogee, 2], label='Coasting')
        ax.plot(self.pos_ENU_log[self.index_apogee:, 0], self.pos_ENU_log[self.index_apogee:, 1], self.pos_ENU_log[self.index_apogee:, 2],label='Trajectory')
        ax.scatter(origin[0], origin[1], origin[2], label='Launch Point', color='r', marker='o')
        ax.scatter(self.pos_ENU_log[-1,0], self.pos_ENU_log[-1,1],self.pos_ENU_log[-1,2],label='Landing Point', color='y', marker='*',s=30)
        ax.grid()
        ax.legend()
        ax.set_zlim(bottom=0.0)
        fig1.savefig(self.filepath + '/' + flightType + '/Flightlog.png')

        plt.figure('Velocity ENU' + flightType)
        plt.title('Velocity ENU')
        plt.plot(self.time_array, self.vel_ENU_log[:, 0], label='Vel_x_ENU')
        plt.plot(self.time_array, self.vel_ENU_log[:, 1], label='Vel_y_ENU')
        plt.plot(self.time_array, self.vel_ENU_log[:, 2], label='Vel_z_ENU')
        plt.xlabel('Time [sec]')
        plt.ylabel('Velocity [m/s]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Velocity_ENU.png')

        plt.figure('Anguler Speed BODY' + flightType)
        plt.title('Anguler Speed BODY')
        plt.plot(self.time_array, self.omega_Body_log[:, 0], label='roll rate')
        plt.plot(self.time_array, self.omega_Body_log[:, 1], label='pitch rate')
        plt.plot(self.time_array, self.omega_Body_log[:, 2], label='yaw rate')
        plt.xlabel('Time[sec]')
        plt.ylabel('Anguler speed[rad/s]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/omega_BODY.png')

        plt.figure('Quaternion' + flightType)
        plt.title('Quaternion')
        plt.plot(self.time_array, self.quat_log[:, 0], label='quat0')
        plt.plot(self.time_array, self.quat_log[:, 1], label='quat1')
        plt.plot(self.time_array, self.quat_log[:, 2], label='quat2')
        plt.plot(self.time_array, self.quat_log[:, 3], label='quat3')
        plt.xlabel('Time[sec]')
        plt.ylabel('Quaternion')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Querternion.png')

        plt.figure('Mass' + flightType)
        plt.title('Mass')
        plt.plot(self.time_array , self.mass_log)
        plt.xlabel('Time [sec]')
        plt.ylabel('Mass [kg]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.savefig(self.filepath + '/' + flightType + '/Mass.png')

        plt.figure('Moment of Inertia' + flightType)
        plt.title('Moment of Inertia')
        plt.plot(self.time_array, self.Ij_pitch_log, label='Moment of Inertia in Pitch')
        plt.plot(self.time_array, self.Ij_roll_log, label='Moment of Inertia in Roll')
        plt.xlabel('Time [sec]')
        plt.ylabel('Moment of Inertia [kg * m^2]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.savefig(self.filepath + '/' + flightType + '/MomentOfInertia.png')

        fig2 = plt.figure('Trajectory' + flightType)
        trajectory = fig2.add_subplot()
        trajectory.set_title('Trajectory')
        trajectory.plot(self.downrange_log , self.altitude_log)
        trajectory.set_xlabel('Downrange [km]')
        trajectory.set_ylabel('Altitude [km]')
        trajectory.set_ylim(ymin=0.0)
        trajectory.grid()
        trajectory.set_aspect('equal')
        plt.savefig(self.filepath + '/' + flightType + '/Trajectory.png')

        fig2 = plt.figure('Downrange' + flightType)
        trajectory = fig2.add_subplot()
        trajectory.set_title('Downrange')
        trajectory.plot(self.pos_ENU_log[:, 0] /1000, self.pos_ENU_log[:, 1] / 1000.0)
        trajectory.set_xlabel('East [km]')
        trajectory.set_ylabel('North [km]')
        trajectory.grid()
        trajectory.set_aspect('equal')
        plt.savefig(self.filepath + '/' + flightType + '/Downrange.png')

        plt.figure('Air Speed BODY'  + flightType)
        plt.title('Vel_air')
        plt.plot(self.time_array, self.vel_air_BODY_log[:,0], label='vel_air_x')
        plt.plot(self.time_array, self.vel_air_BODY_log[:,1], label='vel_air_y')
        plt.plot(self.time_array, self.vel_air_BODY_log[:,2], label='vel_air_z')
        plt.plot(self.time_array , self.Vel_air_abs_log, label='vel_air_abs')
        plt.xlabel('Time [sec]')
        plt.ylabel('Velocity [m/s]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/AirSpeed.png')

        plt.figure('Mach Number' + flightType)
        plt.title('Mach Number')
        plt.plot(self.time_array , self.Mach_log)
        plt.xlabel('Time [sec]')
        plt.ylabel('Mach Number')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.savefig(self.filepath + '/' + flightType + '/MachNumber.png')

        plt.figure('Attitude' + flightType)
        plt.title('Attitude')
        plt.plot(self.time_array , self.attitude_log[:, 0], label='azimuth')
        plt.plot(self.time_array , self.attitude_log[:, 1], label='elevation')
        plt.plot(self.time_array , self.attitude_log[:, 2], label='roll')
        plt.plot(self.time_array , self.alpha_log, label='angle of attack')
        plt.plot(self.time_array , self.beta_log, label='angle of side-slip')
        plt.xlabel('Time [sec]')
        plt.ylabel('Angle [deg]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Attitude.png')

        plt.figure('Center of Gravity' + flightType)
        plt.title('Center of Gravity')
        plt.plot(self.time_array , self.Lcg_log, label='C.G.')
        plt.plot(self.time_array, self.Lcg_prop_log, label='Propellant C.G.')
        plt.plot(self.time_array , self.Lcp_log, label='C.P.')
        plt.xlabel('Time [sec]')
        plt.ylabel('Length [m]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/CenterOfGravity.png')

        plt.figure('Fst' + flightType)
        plt.title('Fst')
        plt.plot(self.time_array , self.Fst_log)
        plt.xlabel('Time [sec]')
        plt.ylabel('Ratio[%]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.savefig(self.filepath + '/' + flightType + '/Fst.png')

        plt.figure('Dynamics Pressure' + flightType)
        plt.title('Dynamics Pressure')
        plt.plot(self.time_array , self.dynamics_perssure_log)
        plt.xlabel('Time [sec]')
        plt.ylabel('Pressure [kPa]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.savefig(self.filepath + '/' + flightType + '/DynamicsPressure.png')

        plt.figure('Force' + flightType)
        plt.title('Force')
        plt.plot(self.time_array , self.drag_log, label='Drag')
        plt.plot(self.time_array , self.normal_log, label='Normal Force')
        plt.plot(self.time_array , self.side_log, label='Side Force')
        plt.plot(self.time_array , self.thrust_log, label='Thrust')
        plt.xlabel('Time [sec]')
        plt.ylabel('Force [N]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Force.png')

        plt.figure('Force Body' + flightType)
        plt.title('Force Body')
        plt.plot(self.time_array , self.force_log[:, 0], label='Force_Body_x')
        plt.plot(self.time_array , self.force_log[:, 1], label='Force_Body_y')
        plt.plot(self.time_array , self.force_log[:, 2], label='Force_Body_z')
        plt.xlabel('Time [sec]')
        plt.ylabel('Force [N]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Force_BODY.png')

        plt.figure('Acc_ENU' + flightType)
        plt.title('Acc_ENU')
        plt.plot(self.time_array, self.acc_ENU_log[:, 0], label='Acc_East')
        plt.plot(self.time_array, self.acc_ENU_log[:, 1], label='Acc_North')
        plt.plot(self.time_array, self.acc_ENU_log[:, 2], label='Acc_Up')
        plt.xlabel('Time [sec]')
        plt.ylabel('Acceleration [m/s^2]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Acceleration_ENU.png')

        plt.figure('Acceleration BODY' + flightType)
        plt.title('Acceleration BODY')
        plt.plot(self.time_array, self.acc_BODY_log[:, 0], label='Acc_Roll')
        plt.plot(self.time_array, self.acc_BODY_log[:, 1], label='Acc_Pitch')
        plt.plot(self.time_array, self.acc_BODY_log[:, 2], label='Acc_Yaw')
        plt.xlabel('Time [sec]')
        plt.ylabel('Acceleration [m/s^2]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Acceleration_BODY.png')

        vENU2LLH = np.vectorize(ENU2LLH, excluded=['launch_LLH'], signature="(1),(3)->(3)")
        log_LLH = vENU2LLH(self.Launch_LLH, self.pos_ENU_log)
        point_LLH = ENU2LLH(self.Launch_LLH, self.point)
        gettrajectorypoint(point_LLH)
        post_kml(log_LLH, self.filepath, 'hard')
        land_point.get_point_trajectory(self.point)


    def get_index_coast(self):
        return self.index_coast
