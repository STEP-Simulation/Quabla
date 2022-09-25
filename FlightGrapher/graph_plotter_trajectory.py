import matplotlib.pyplot as plt
from cProfile import label
from mpl_toolkits.mplot3d import Axes3D
from mpl_toolkits.mplot3d import proj3d
import numpy as np
from matplotlib.pyplot import subplot
from PlotLandingScatter.coordinate import ENU2LLH
from FlightGrapher.make_kml import gettrajectorypoint, post_kml

class GraphPlotterTrajectory:

    def __init__(self, df, filepath, launch_LLH):
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
        self.omega_Body_log = np.array([[p, q, r]
                                        for p, q, r
                                        in zip(df['omega_roll [rad/s]'], 
                                               df['omega_pitch [rad/s]'], 
                                               df['omega_yaw [rad/s]'])])
        self.quat_log = np.array([[q0, q1, q2, q3]
                                  for q0, q1, q2, q3
                                  in zip(df['quat0'],
                                         df['quat1'],
                                         df['quat2'],
                                         df['quat3'])])
        self.attitude_log = np.array([[yaw, pitch, roll]
                                      for yaw, pitch, roll
                                      in zip(df['yaw [deg]'],
                                             df['pitch [deg]'],
                                             df['roll [deg]'])])
        self.mass_log = np.array(df['Mass [kg]'])
        self.mass_fuel_log = np.array(df['Mass_fuel [kg]'])
        self.mass_ox_log = np.array(df['Mass_ox [kg]'])
        self.mass_prop_log = np.array(df['Mass_prop [kg]'])
        self.Lcg_log = np.array(df['Lcg [m]'])
        self.Lcg_fuel_log = np.array(df['Lcg_fuel [m]'])
        self.Lcg_ox_log = np.array(df['Lcg_ox [m]'])
        self.Lcg_prop_log = np.array(df['Lcg_prop [m]'])
        self.Lcp_log = np.array(df['Lcp [m]'])
        self.Ij_roll_log = np.array(df['Ij_roll [kg m2]'])
        self.Ij_pitch_log = np.array(df['Ij_pitch [kg m2]'])
        self.altitude_log = np.array(df['Altitude [km]'])
        self.downrange_log = np.array(df['Downrange [km]'])
        self.vel_air_ENU_log = np.array([[vair_east, vair_north, vair_up] 
                                         for vair_east, vair_north, vair_up 
                                         in zip(df['vel_air_east [m/s]'], 
                                                df['vel_air_north [m/s]'], 
                                                df['vel_air_up [m/s]'])])
        self.vel_air_BODY_log = np.array([[vair_x, vair_y, vair_z] 
                                         for vair_x, vair_y, vair_z 
                                         in zip(df['vel_air_BODY_x [m/s]'], 
                                                df['vel_air_BODY_y [m/s]'], 
                                                df['vel_air_BODY_z [m/s]'])])
        self.Vel_air_abs_log = np.array(df['vel_air_abs [m/s]'])
        self.alpha_log = np.array(df['alpha [deg]'])
        self.beta_log = np.array(df['beta [deg]'])
        self.Mach_log = np.array(df['Mach'])
        self.dynamics_perssure_log = np.array(df['Dynamics Pressure [kPa]'])
        self.Fst_log = np.array(df['Fst'])
        self.drag_log = np.array(df['Drag [N]'])
        self.normal_log = np.array(df['Normal Force [N]'])
        self.side_log = np.array(df['Side Force [N]'])
        self.thrust_log = np.array(df['Thrust [N]'])
        self.force_log = np.array([[Fx, Fy, Fz] 
                                   for Fx, Fy, Fz 
                                   in zip(df['Force_BODY_x [N]'], 
                                          df['Force_BODY_y [N]'], 
                                          df['Force_BODY_z [N]'])])
        self.acc_ENU_log = np.array([[acc_east, acc_north, acc_up] 
                                     for acc_east, acc_north, acc_up
                                     in zip(df['Acc_east [m/s2]'], 
                                            df['Acc_north [m/s2]'], 
                                            df['Acc_up [m/s2]'])])
        self.acc_BODY_log = np.array([[acc_x, acc_y, acc_z] 
                                     for acc_x, acc_y, acc_z
                                     in zip(df['Acc_BODY_x [m/s2]'], 
                                            df['Acc_BODY_y [m/s2]'], 
                                            df['Acc_BODY_z [m/s2]'])])
        self.moment_aero_log = np.array([[moment_aero_x, moment_aero_y, moment_aero_z]
                                         for moment_aero_x, moment_aero_y, moment_aero_z
                                         in zip(df['moment_aero_x [N*m]'],
                                                df['moment_aero_y [N*m]'],
                                                df['moment_aero_z [N*m]'])])
        self.moment_aero_damping_log = np.array([[moment_aero_damping_x, moment_aero_damping_y, moment_aero_damping_z]
                                                 for moment_aero_damping_x, moment_aero_damping_y, moment_aero_damping_z
                                                 in zip(df['moment_aero_damping_x [N*m]'],
                                                        df['moment_aero_damping_y [N*m]'],
                                                        df['moment_aero_damping_z [N*m]'])])
        self.moment_jet_damping_log = np.array([[moment_jet_damping_x, moment_jet_damping_y, moment_jet_damping_z]
                                                for moment_jet_damping_x, moment_jet_damping_y, moment_jet_damping_z
                                                in zip(df['moment_jet_damping_x [N*m]'],
                                                       df['moment_jet_damping_y [N*m]'],
                                                       df['moment_jet_damping_z [N*m]'])])
        self.moment_gyro_log = np.array([[moment_gyro_x, moment_gyro_y, moment_gyro_z]
                                                for moment_gyro_x, moment_gyro_y, moment_gyro_z
                                                in zip(df['moment_gyro_x [N*m]'],
                                                       df['moment_gyro_y [N*m]'],
                                                       df['moment_gyro_z [N*m]'])])
        self.moment_log = np.array([[moment_x, moment_y, moment_z]
                                                for moment_x, moment_y, moment_z
                                                in zip(df['moment_x [N*m]'],
                                                       df['moment_y [N*m]'],
                                                       df['moment_z [N*m]'])])
        self.acc_abs_log = np.array(df['Acc_abs [m/s2]'])

        self.index_apogee = np.argmax(self.pos_ENU_log[:,2])
        self.index_coast = np.argmin(self.thrust_log[1:])

        self.point = [self.pos_ENU_log[-1, 0], self.pos_ENU_log[-1, 1], self.pos_ENU_log[-1, 2]]
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
        # ax = fig1.gca(projection='3d')
        ax = fig1.add_subplot(projection='3d')
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
        plt.plot(self.time_array , self.mass_log, label='Mass')
        plt.plot(self.time_array , self.mass_fuel_log, label='Fuel Mass')
        plt.plot(self.time_array , self.mass_ox_log, label='Oxidizer Mass')
        plt.plot(self.time_array , self.mass_prop_log, label='Propellant Mass')
        plt.xlabel('Time [sec]')
        plt.ylabel('Mass [kg]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Mass.png')

        plt.figure('Moment of Inertia' + flightType)
        plt.title('Moment of Inertia')
        plt.plot(self.time_array, self.Ij_pitch_log, label='Moment of Inertia in Pitch')
        plt.plot(self.time_array, self.Ij_roll_log, label='Moment of Inertia in Roll')
        plt.xlabel('Time [sec]')
        plt.ylabel('Moment of Inertia [kg * m^2]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
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
        plt.plot(self.time_array , self.Lcg_fuel_log, label='Fuel C.G.')
        plt.plot(self.time_array , self.Lcg_ox_log, label='Oxidizer C.G.')
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

        plt.close('all')

        plt.figure('Moment Body X-Axis' + flightType)
        plt.title('Moment Body X-Axis')
        plt.plot(self.time_array, self.moment_aero_log[:, 0], label='Aero')
        plt.plot(self.time_array, self.moment_aero_damping_log[:, 0], label='Aero Damping')
        plt.plot(self.time_array, self.moment_jet_damping_log[:, 0], label='Jet Damping')
        plt.plot(self.time_array, self.moment_gyro_log[:, 0], label='Gyro')
        plt.xlabel('Time [sec]')
        plt.ylabel('Moment [N*m]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Moment_Body_X-Axis.png')

        plt.figure('Moment Body Y-Axis' + flightType)
        plt.title('Moment Body Y-Axis')
        plt.plot(self.time_array, self.moment_aero_log[:, 1], label='Aero')
        plt.plot(self.time_array, self.moment_aero_damping_log[:, 1], label='Aero Damping')
        plt.plot(self.time_array, self.moment_jet_damping_log[:, 1], label='Jet Damping')
        plt.plot(self.time_array, self.moment_gyro_log[:, 1], label='Gyro')
        plt.xlabel('Time [sec]')
        plt.ylabel('Moment [N*m]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Moment_Body_Y-Axis.png')

        plt.figure('Moment Body Z-Axis' + flightType)
        plt.title('Moment Body Z-Axis')
        plt.plot(self.time_array, self.moment_aero_log[:, 2], label='Aero')
        plt.plot(self.time_array, self.moment_aero_damping_log[:, 2], label='Aero Damping')
        plt.plot(self.time_array, self.moment_jet_damping_log[:, 2], label='Jet Damping')
        plt.plot(self.time_array, self.moment_gyro_log[:, 2], label='Gyro')
        plt.xlabel('Time [sec]')
        plt.ylabel('Moment [N*m]')
        plt.xlim(xmin=0.0)
        plt.grid()
        plt.legend()
        plt.savefig(self.filepath + '/' + flightType + '/Moment_Body_Z-Axis.png')

        vENU2LLH = np.vectorize(ENU2LLH, excluded=['launch_LLH'], signature="(1),(3)->(3)")
        log_LLH = vENU2LLH(self.Launch_LLH, self.pos_ENU_log)
        point_LLH = ENU2LLH(self.Launch_LLH, self.point)
        gettrajectorypoint(point_LLH)
        post_kml(log_LLH, self.filepath, 'hard')
        land_point.get_point_trajectory(self.point)


    def get_index_coast(self):
        return self.index_coast
