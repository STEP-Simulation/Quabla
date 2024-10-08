import os
import matplotlib.pyplot as plt
from cProfile import label
from mpl_toolkits.mplot3d import Axes3D
from mpl_toolkits.mplot3d import proj3d
import numpy as np
from matplotlib.pyplot import subplot
from PlotLandingScatter.coordinate import ENU2LLH
from PIL import Image
from FlightGrapher.sub_tool import get_extent_values, update_limits, set_limits
from FlightGrapher.make_kml import post_kml

class GraphPlotterTrajectory:
    
    def __init__(self, df_log, df_summary, filepath, launch_LLH):
       
       self.filepath = filepath

       self.time_array = np.array(df_log['time [sec]'])
       self.time_step_array = np.array(df_log['time_step [sec]'])
       self.pos_NED_log = np.array([[north, east, down] 
                                    for north, east, down 
                                    in zip(df_log['pos_north [m]'], 
                                           df_log['pos_east [m]'], 
                                           df_log['pos_down [m]'])])
       self.vel_NED_log = np.array([[v_east, v_north, v_up] 
                                     for v_east, v_north, v_up 
                                     in zip(df_log['vel_north [m/s]'], 
                                            df_log['vel_east [m/s]'], 
                                            df_log['vel_down [m/s]'])])
       self.vel_BODY_log = np.array([[v_roll, v_pitch, v_yaw] 
                                     for v_roll, v_pitch, v_yaw 
                                     in zip(df_log['vel_BODY_x [m/s]'], 
                                            df_log['vel_BODY_y [m/s]'], 
                                            df_log['vel_BODY_z [m/s]'])])
       self.omega_Body_log = np.array([[p, q, r]
                                        for p, q, r
                                        in zip(df_log['omega_roll [rad/s]'], 
                                               df_log['omega_pitch [rad/s]'], 
                                               df_log['omega_yaw [rad/s]'])])
       self.quat_log = np.array([[q0, q1, q2, q3]
                                  for q0, q1, q2, q3
                                  in zip(df_log['quat0'],
                                         df_log['quat1'],
                                         df_log['quat2'],
                                         df_log['quat3'])])
       self.attitude_log = np.array([[yaw, pitch, roll]
                                      for yaw, pitch, roll
                                      in zip(df_log['yaw [deg]'],
                                             df_log['pitch [deg]'],
                                             df_log['roll [deg]'])])
       self.mass_log = np.array(df_log['Mass [kg]'])
       self.mass_fuel_log = np.array(df_log['Mass_fuel [kg]'])
       self.mass_ox_log = np.array(df_log['Mass_ox [kg]'])
       self.mass_prop_log = np.array(df_log['Mass_prop [kg]'])
       self.Lcg_log = np.array(df_log['Lcg [m]'])
       self.Lcg_fuel_log = np.array(df_log['Lcg_fuel [m]'])
       self.Lcg_ox_log = np.array(df_log['Lcg_ox [m]'])
       self.Lcg_prop_log = np.array(df_log['Lcg_prop [m]'])
       self.Lcp_log = np.array(df_log['Lcp [m]'])
       self.Ij_roll_log = np.array(df_log['Ij_roll [kg m2]'])
       self.Ij_pitch_log = np.array(df_log['Ij_pitch [kg m2]'])
       self.Cd_log = np.array(df_log['C_D [-]'])
       self.CNa_log = np.array(df_log['C_Na [1/rad]'])
       self.altitude_log = np.array(df_log['Altitude [km]'])
       self.downrange_log = np.array(df_log['Downrange [km]'])
       self.vel_air_ENU_log = np.array([[vair_east, vair_north, vair_up] 
                                         for vair_east, vair_north, vair_up 
                                         in zip(df_log['vel_air_north [m/s]'], 
                                                df_log['vel_air_east [m/s]'], 
                                                df_log['vel_air_down [m/s]'])])
       self.vel_air_BODY_log = np.array([[vair_x, vair_y, vair_z] 
                                         for vair_x, vair_y, vair_z 
                                         in zip(df_log['vel_air_BODY_x [m/s]'], 
                                                df_log['vel_air_BODY_y [m/s]'], 
                                                df_log['vel_air_BODY_z [m/s]'])])
       self.Vel_air_abs_log = np.array(df_log['vel_air_abs [m/s]'])
       self.alpha_log = np.array(df_log['alpha [deg]'])
       self.beta_log = np.array(df_log['beta [deg]'])
       self.Mach_log = np.array(df_log['Mach'])
       self.dynamics_pressure_log = np.array(df_log['Dynamics Pressure [kPa]'])
       self.Fst_log = np.array(df_log['Fst'])
       self.drag_log = np.array(df_log['Drag [N]'])
       self.normal_log = np.array(df_log['Normal Force [N]'])
       self.side_log = np.array(df_log['Side Force [N]'])
       self.thrust_log = np.array(df_log['Thrust [N]'])
       self.thrust_momentum_log = np.array(df_log['Thrust Momentum [N]'])
       self.thrust_pressure_log = np.array(df_log['Thrust Pressure [N]'])
       self.force_log = np.array([[Fx, Fy, Fz] 
                                  for Fx, Fy, Fz 
                                  in zip(df_log['Force_BODY_x [N]'], 
                                         df_log['Force_BODY_y [N]'], 
                                         df_log['Force_BODY_z [N]'])])
       self.acc_NED_log = np.array([[acc_north, acc_east, acc_down] 
                                    for acc_north, acc_east, acc_down
                                    in zip(df_log['Acc_east [m/s2]'], 
                                           df_log['Acc_north [m/s2]'], 
                                           df_log['Acc_up [m/s2]'])])
       self.acc_BODY_log = np.array([[acc_x, acc_y, acc_z] 
                                     for acc_x, acc_y, acc_z
                                     in zip(df_log['Acc_BODY_x [m/s2]'], 
                                            df_log['Acc_BODY_y [m/s2]'], 
                                            df_log['Acc_BODY_z [m/s2]'])])
       self.moment_aero_log = np.array([[moment_aero_x, moment_aero_y, moment_aero_z]
                                         for moment_aero_x, moment_aero_y, moment_aero_z
                                         in zip(df_log['moment_aero_x [N*m]'],
                                                df_log['moment_aero_y [N*m]'],
                                                df_log['moment_aero_z [N*m]'])])
       self.moment_aero_damping_log = np.array([[moment_aero_damping_x, moment_aero_damping_y, moment_aero_damping_z]
                                                 for moment_aero_damping_x, moment_aero_damping_y, moment_aero_damping_z
                                                 in zip(df_log['moment_aero_damping_x [N*m]'],
                                                        df_log['moment_aero_damping_y [N*m]'],
                                                        df_log['moment_aero_damping_z [N*m]'])])
       self.moment_jet_damping_log = np.array([[moment_jet_damping_x, moment_jet_damping_y, moment_jet_damping_z]
                                                for moment_jet_damping_x, moment_jet_damping_y, moment_jet_damping_z
                                                in zip(df_log['moment_jet_damping_x [N*m]'],
                                                       df_log['moment_jet_damping_y [N*m]'],
                                                       df_log['moment_jet_damping_z [N*m]'])])
       self.moment_gyro_log = np.array([[moment_gyro_x, moment_gyro_y, moment_gyro_z]
                                                for moment_gyro_x, moment_gyro_y, moment_gyro_z
                                                in zip(df_log['moment_gyro_x [N*m]'],
                                                       df_log['moment_gyro_y [N*m]'],
                                                       df_log['moment_gyro_z [N*m]'])])
       self.moment_log = np.array([[moment_x, moment_y, moment_z]
                                                for moment_x, moment_y, moment_z
                                                in zip(df_log['moment_x [N*m]'],
                                                       df_log['moment_y [N*m]'],
                                                       df_log['moment_z [N*m]'])])
       self.acc_abs_log = np.array(df_log['Acc_abs [m/s2]'])

       time_launch_clear = df_summary['Time Launch Clear [sec]'].to_numpy()
       time_actuate = df_summary['Time Engine Actuate [sec]'].to_numpy()
       self.time_apogee = df_summary['Time Apogee [sec]'].to_numpy()
       self.time_end = self.time_array[-1]
       self.index_launch_clear = np.argmax(time_launch_clear < self.time_array)
       self.index_coast = np.argmax(time_actuate < self.time_array)
       self.index_apogee = np.argmax(self.time_apogee < self.time_array)

       self.point = [self.pos_NED_log[-1, 0], self.pos_NED_log[-1, 1], self.pos_NED_log[-1, 2]]
       self.Launch_LLH = launch_LLH

    def plot_graph(self, make_kml):
       flightType = '_01_trajectory'

       img_logo = Image.open('Quabla_logo.png')
       aspect_logo = img_logo.height / img_logo.width
       
       plt.rcParams['font.family'] = 'Arial'
       plt.rcParams['font.size']   = 12
       plt.rcParams['figure.titlesize'] = 13
       plt.rcParams["xtick.direction"]   = "in"
       plt.rcParams["ytick.direction"]   = "in"
       plt.rcParams["xtick.top"]         = True
       plt.rcParams["ytick.right"]       = True
       plt.rcParams["xtick.major.width"] = 1.5
       plt.rcParams["ytick.major.width"] = 1.5
       plt.rcParams["axes.linewidth"] = 1.5

       plt.close('all')

       fig, ax = plt.subplots()
       ax.set_title('Time Step')
       ax.plot(self.time_array[:self.index_coast], self.time_step_array[:self.index_coast], color='#FF4B00', linestyle='-')
       ax.plot(self.time_array[self.index_coast:], self.time_step_array[self.index_coast:], color='#FF4B00', linestyle='--')
       ax.text(x=self.time_array[self.index_coast], y=self.time_step_array[self.index_coast], s='Engine cut-off\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       ax.plot(self.time_array[self.index_coast], self.time_step_array[self.index_coast], marker='x', color='black')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Time Step [sec]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ax.set_ylim(ymin=0.)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'TimeStep.png')

       fig, ax = plt.subplots()
       ax.set_title('Position NED')
       ax.plot(self.time_array[:self.index_coast], self.pos_NED_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='North')
       ax.plot(self.time_array[:self.index_coast], self.pos_NED_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='East')
       ax.plot(self.time_array[:self.index_coast], self.pos_NED_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Down')
       ax.plot(self.time_array[self.index_coast:], self.pos_NED_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.pos_NED_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.pos_NED_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Position [m]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'PositionNED.png')

       fig1 = plt.figure('Flightlog' + flightType)
       origin = np.zeros(3)
       ax = fig1.add_subplot(projection='3d')
       ax.set_xlabel('East [m]')
       ax.set_ylabel('North [m]')
       ax.set_zlabel('Up [m]')
       ax.set_title('Trajectory')
       ax.plot(self.pos_NED_log[:self.index_coast, 1], self.pos_NED_log[:self.index_coast, 0], - self.pos_NED_log[:self.index_coast, 2], color='#FF4B00', label='Powered')
       ax.plot(self.pos_NED_log[self.index_coast:self.index_apogee, 1], self.pos_NED_log[self.index_coast:self.index_apogee, 0], - self.pos_NED_log[self.index_coast:self.index_apogee, 2], color='#005AFF', label='Coasting')
       ax.plot(self.pos_NED_log[self.index_apogee:, 1], self.pos_NED_log[self.index_apogee:, 0], - self.pos_NED_log[self.index_apogee:, 2], color='#03AF7A', label='Trajectory')
       ax.scatter(origin[0], origin[1], origin[2], label='Launch Point', color='r', marker='o')
       ax.scatter(self.pos_NED_log[-1, 1], self.pos_NED_log[-1, 0], - self.pos_NED_log[-1, 2],label='Landing Point', color='y', marker='*',s=30)
       ax.grid()
       ax.legend()
       ax.set_zlim(bottom=0.0)
       set_limits(ax)
       fig1.savefig(self.filepath + os.sep + flightType + os.sep + 'Flightlog.png')

       fig, ax = plt.subplots()
       ax.set_title('Velocity NED')
       ax.plot(self.time_array[:self.index_coast], self.vel_NED_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='North')
       ax.plot(self.time_array[:self.index_coast], self.vel_NED_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='East')
       ax.plot(self.time_array[:self.index_coast], self.vel_NED_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Down')
       ax.plot(self.time_array[self.index_coast:], self.vel_NED_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.vel_NED_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.vel_NED_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Velocity [m/s]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'VelocityNED.png')

       fig, ax = plt.subplots()
       ax.set_title('Velocity BODY')
       ax.plot(self.time_array[:self.index_coast], self.vel_BODY_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='Roll')
       ax.plot(self.time_array[:self.index_coast], self.vel_BODY_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Pitch')
       ax.plot(self.time_array[:self.index_coast], self.vel_BODY_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Yaw')
       ax.plot(self.time_array[self.index_coast:], self.vel_BODY_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.vel_BODY_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.vel_BODY_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Velocity [m/s]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'VelocityBODY.png')

       fig, ax = plt.subplots()
       ax.set_title('Angular Speed BODY')
       ax.plot(self.time_array[:self.index_coast], self.omega_Body_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='Roll')
       ax.plot(self.time_array[:self.index_coast], self.omega_Body_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Pitch')
       ax.plot(self.time_array[:self.index_coast], self.omega_Body_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Yaw')
       ax.plot(self.time_array[self.index_coast:], self.omega_Body_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.omega_Body_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.omega_Body_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time[sec]')
       ax.set_ylabel('Angular speed[rad/s]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'omegaBODY.png')

       fig, ax = plt.subplots()
       ax.set_title('Quaternion')
       ax.plot(self.time_array[:self.index_coast], self.quat_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='quat0')
       ax.plot(self.time_array[:self.index_coast], self.quat_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='quat1')
       ax.plot(self.time_array[:self.index_coast], self.quat_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='quat2')
       ax.plot(self.time_array[:self.index_coast], self.quat_log[:self.index_coast, 3], color='#4DC4FF', linestyle='-', label='quat3')
       ax.plot(self.time_array[self.index_coast:], self.quat_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.quat_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.quat_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.quat_log[self.index_coast:, 3], color='#4DC4FF', linestyle='--')
       ax.set_xlabel('Time[sec]')
       ax.set_ylabel('Quaternion')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'Quaternion.png')

       fig, ax = plt.subplots()
       ax.set_title('Mass All')
       ax.plot(self.time_array[:self.index_coast], self.mass_log[:self.index_coast]     , color='#FF4B00', linestyle='-', label='All')
       ax.plot(self.time_array[self.index_coast:self.index_apogee], self.mass_log[self.index_coast:self.index_apogee]     , color='#FF4B00', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Mass [kg]')
       ax.set_xlim(xmin=0.0, xmax=self.time_apogee)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MassAll.png')

       fig, ax = plt.subplots()
       ax.set_title('Mass Propellant')
       ax.plot(self.time_array[:self.index_coast], self.mass_prop_log[:self.index_coast], color='#FF4B00', linestyle='-', label='Propellant')
       ax.plot(self.time_array[:self.index_coast], self.mass_fuel_log[:self.index_coast], color='#005AFF', linestyle='-', label='Fuel')
       ax.plot(self.time_array[:self.index_coast], self.mass_ox_log[:self.index_coast]  , color='#03AF7A', linestyle='-', label='Oxidizer')
       ax.plot(self.time_array[self.index_coast:self.index_apogee], self.mass_prop_log[self.index_coast:self.index_apogee], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:self.index_apogee], self.mass_fuel_log[self.index_coast:self.index_apogee], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:self.index_apogee], self.mass_ox_log[self.index_coast:self.index_apogee]  , color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Mass [kg]')
       ax.set_xlim(xmin=0.0, xmax=self.time_apogee)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MassPropellant.png')

       fig, ax = plt.subplots()
       ax.set_title('Moment of Inertia')
       ax.plot(self.time_array[:self.index_coast], self.Ij_pitch_log[:self.index_coast], color='#FF4B00', linestyle='-', label='Pitch')
       ax.plot(self.time_array[:self.index_coast], self.Ij_roll_log[:self.index_coast] , color='#005AFF', linestyle='-', label='Roll')
       ax.plot(self.time_array[self.index_coast:], self.Ij_pitch_log[self.index_coast:], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.Ij_roll_log[self.index_coast:] , color='#005AFF', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Moment of Inertia [kg * m^2]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MomentOfInertia.png')

       fig, ax1 = plt.subplots()
       ax1.set_title('Aero Coefficient')
       ax2 = ax1.twinx()
       ax1.plot(self.time_array[:self.index_coast], self.Cd_log[:self.index_coast], color='#FF4B00', linestyle='-', label='$C_D$')
       ax1.plot(self.time_array[self.index_coast:], self.Cd_log[self.index_coast:], color='#FF4B00', linestyle='--')
       ax1.plot(self.time_array[-1], self.Cd_log[-1], label='$C_{Na}$', color='#005AFF')
       ax2.plot(self.time_array[:self.index_coast], self.CNa_log[:self.index_coast], color='#005AFF', linestyle='-', label='$C_{Na}$')
       ax2.plot(self.time_array[self.index_coast:], self.CNa_log[self.index_coast:], color='#005AFF', linestyle='--')
       ax1.set_xlabel('Time [sec]')
       ax1.set_ylabel('Drag Coefficient [-]')
       ax2.set_ylabel('Slope of Normal Force Coefficient [1/rad]')
       ax1.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax1.get_ylim()
       ax1.set_ylim(ymin=ymin, ymax=ymax)
       ax1.imshow(img_logo, extent=(get_extent_values(fig, ax1, aspect_logo)), alpha=0.5)
       ax1.set_aspect('auto')
       ax1.grid()
       ax1.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'AeroCoefficient.png')

       fig2, trajectory = plt.subplots()
       trajectory.set_title('Trajectory')
       trajectory.plot(self.downrange_log[:self.index_coast], self.altitude_log[:self.index_coast], color='#FF4B00', linestyle='-')
       trajectory.plot(self.downrange_log[self.index_coast:], self.altitude_log[self.index_coast:], color='#FF4B00', linestyle='--')
       trajectory.text(x=self.downrange_log[self.index_coast], y=self.altitude_log[self.index_coast], s='Engine cut-off\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       trajectory.plot(self.downrange_log[self.index_coast], self.altitude_log[self.index_coast], marker='x', color='black')
       trajectory.set_xlabel('Downrange [km]')
       trajectory.set_ylabel('Altitude [km]')
       trajectory.set_ylim(ymin=0.0)
       xmin, xmax, ymin, ymax = update_limits(trajectory.get_xlim(), trajectory.get_ylim(), fig2.get_figheight() / fig2.get_figwidth())
       trajectory.set_xlim(xmin=xmin, xmax=xmax)
       trajectory.set_ylim(ymin=ymin, ymax=ymax)
       trajectory.imshow(img_logo, extent=(get_extent_values(fig2, trajectory, aspect_logo)), alpha=0.5)
       trajectory.grid()
       trajectory.set_aspect('equal')
       fig2.savefig(self.filepath + os.sep + flightType + os.sep + 'Trajectory.png')

       plt.close('all')

       fig, ax = plt.subplots()
       ax.set_title('Downrange')
       ax.plot(self.pos_NED_log[:self.index_coast, 1] / 1000, self.pos_NED_log[:self.index_coast, 0] / 1000.0, color='#FF4B00', linestyle='-')
       ax.plot(self.pos_NED_log[self.index_coast:, 1] / 1000, self.pos_NED_log[self.index_coast:, 0] / 1000.0, color='#FF4B00', linestyle='--')
       ax.text(x=self.pos_NED_log[self.index_coast, 1] / 1000, y=self.pos_NED_log[self.index_coast, 0] / 1000, s='Engine cut-off\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       ax.text(x=self.pos_NED_log[-1, 1] / 1000              , y=self.pos_NED_log[-1, 0] / 1000              , s='Landing Point\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       ax.text(x=self.pos_NED_log[0 , 1] / 1000              , y=self.pos_NED_log[0 , 0] / 1000              , s='Launch Point\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       ax.plot(self.pos_NED_log[self.index_coast, 1] / 1000, self.pos_NED_log[self.index_coast, 0] / 1000, marker='x', color='black')
       ax.plot(self.pos_NED_log[-1, 1] / 1000              , self.pos_NED_log[-1, 0] / 1000              , marker='x', color='black')
       ax.plot(self.pos_NED_log[0 , 1] / 1000              , self.pos_NED_log[0 , 0] / 1000              , marker='x', color='black')
       xmin, xmax, ymin, ymax = update_limits(ax.get_xlim(), ax.get_ylim(), fig.get_figheight() / fig.get_figwidth())
       ax.set_xlim(xmin=xmin, xmax=xmax)
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_xlabel('East [km]')
       ax.set_ylabel('North [km]')
       ax.grid()
       ax.set_aspect('equal')
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'Downrange.png')

       fig, ax = plt.subplots()
       ax.set_title('Air Speed')
       ax.plot(self.time_array[:self.index_coast], self.vel_air_BODY_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='Roll')
       ax.plot(self.time_array[:self.index_coast], self.vel_air_BODY_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Pitch')
       ax.plot(self.time_array[:self.index_coast], self.vel_air_BODY_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Yaw')
       ax.plot(self.time_array[self.index_coast:], self.vel_air_BODY_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.vel_air_BODY_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.vel_air_BODY_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Velocity [m/s]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'AirSpeed.png')

       fig, ax = plt.subplots()
       ax.set_title('Mach Number')
       ax.plot(self.time_array[:self.index_coast] , self.Mach_log[:self.index_coast], color='#FF4B00', linestyle='-')
       ax.plot(self.time_array[self.index_coast:] , self.Mach_log[self.index_coast:], color='#FF4B00', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Mach Number')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MachNumber.png')

       fig, ax = plt.subplots()
       ax.set_title('Attitude')
       ax.plot(self.time_array[:self.index_coast] , self.attitude_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='Azimuth')
       ax.plot(self.time_array[:self.index_coast] , self.attitude_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Elevation')
       ax.plot(self.time_array[:self.index_coast] , self.attitude_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Roll')
       ax.plot(self.time_array[self.index_coast:] , self.attitude_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:] , self.attitude_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:] , self.attitude_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Angle [deg]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ax.set_ylim(ymin=-180., ymax=180.)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'Attitude.png')
       
       fig, ax = plt.subplots()
       ax.set_title('AoA AoS')
       ax.plot(self.time_array[:self.index_coast] , self.alpha_log[:self.index_coast], color='#FF4B00', linestyle='-', label='Angle of Attack')
       ax.plot(self.time_array[:self.index_coast] , self.beta_log[:self.index_coast] , color='#005AFF', linestyle='-', label='Angle of Side-slip')
       ax.plot(self.time_array[self.index_coast:], self.alpha_log[self.index_coast:], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.beta_log[self.index_coast:] , color='#005AFF', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Angle [deg]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'AoA_AoS.png')

       fig, ax = plt.subplots()
       ax.set_title('Center of Gravity')
       ax.plot(self.time_array[:self.index_coast], self.Lcg_log[:self.index_coast]     , color='#FF4B00', linestyle='-', label='C.G.')
       ax.plot(self.time_array[:self.index_coast], self.Lcg_fuel_log[:self.index_coast], color='#005AFF', linestyle='-', label='Fuel C.G.')
       ax.plot(self.time_array[:self.index_coast], self.Lcg_ox_log[:self.index_coast]  , color='#03AF7A', linestyle='-', label='Oxidizer C.G.')
       ax.plot(self.time_array[:self.index_coast], self.Lcg_prop_log[:self.index_coast], color='#4DC4FF', linestyle='-', label='Propellant C.G.')
       ax.plot(self.time_array[:self.index_coast], self.Lcp_log[:self.index_coast]     , color='#F6AA00', linestyle='-', label='C.P.')
       ax.plot(self.time_array[self.index_coast:], self.Lcg_log[self.index_coast:]     , color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.Lcg_fuel_log[self.index_coast:], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.Lcg_ox_log[self.index_coast:]  , color='#03AF7A', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.Lcg_prop_log[self.index_coast:], color='#4DC4FF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.Lcp_log[self.index_coast:]     , color='#F6AA00', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Length [m]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'CenterOfGravity.png')

       fig, ax = plt.subplots()
       ax.set_title('Fst')
       ax.plot(self.time_array[:self.index_coast] , self.Fst_log[:self.index_coast], color='#FF4B00', linestyle='-')
       ax.plot(self.time_array[self.index_coast:] , self.Fst_log[self.index_coast:], color='#FF4B00', linestyle='--')
       ax.axhline(y=10., color='black', linestyle=':')
       ax.axhline(y=20., color='black', linestyle=':')
       ax.text(x=self.time_array[self.index_coast], y=self.Fst_log[self.index_coast], s='Engine cut-off\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       ax.plot(self.time_array[self.index_coast], self.Fst_log[self.index_coast], marker='x', color='black')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Ratio[%]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ax.set_ylim(ymin=5., ymax=25.)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'Fst.png')

       fig, ax = plt.subplots()
       ax.set_title('Dynamics Pressure')
       ax.plot(self.time_array[:self.index_coast] , self.dynamics_pressure_log[:self.index_coast], color='#FF4B00', linestyle='-')
       ax.plot(self.time_array[self.index_coast:] , self.dynamics_pressure_log[self.index_coast:], color='#FF4B00', linestyle='--')
       ax.text(x=self.time_array[self.index_coast], y=self.dynamics_pressure_log[self.index_coast], s='Engine cut-off\n', fontsize='medium', horizontalalignment='center', verticalalignment='center')
       ax.plot(self.time_array[self.index_coast], self.dynamics_pressure_log[self.index_coast], marker='x', color='black')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Pressure [kPa]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ax.set_ylim(ymin=0.0)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'DynamicsPressure.png')

       fig, ax = plt.subplots()
       ax.set_title('Force Aero')
       ax.plot(self.time_array[:self.index_coast], self.drag_log[:self.index_coast]  , color='#FF4B00', linestyle='-', label='Drag')
       ax.plot(self.time_array[:self.index_coast], self.normal_log[:self.index_coast], color='#005AFF', linestyle='-', label='Normal Force')
       ax.plot(self.time_array[:self.index_coast], self.side_log[:self.index_coast]  , color='#03AF7A', linestyle='-', label='Side Force')
       ax.plot(self.time_array[self.index_coast:], self.drag_log[self.index_coast:]  , color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.normal_log[self.index_coast:], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.side_log[self.index_coast:]  , color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Force [N]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'ForceAero.png')
       
       fig, ax = plt.subplots()
       ax.set_title('Force Thrust')
       ax.plot(self.time_array[:self.index_apogee] , self.thrust_log[:self.index_apogee], color='#FF4B00', linestyle='-', label='Thrust All')
       ax.plot(self.time_array[:self.index_apogee] , self.thrust_momentum_log[:self.index_apogee], color='#005AFF', linestyle='-', label='Momentum Thrust')
       ax.plot(self.time_array[:self.index_apogee] , self.thrust_pressure_log[:self.index_apogee], color='#03AF7A', linestyle='-', label='Pressure Thrust')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Force [N]')
       ax.set_xlim(xmin=0.0, xmax=self.time_apogee)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.legend()
       ax.grid()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'ForceThrust.png')

       fig, ax = plt.subplots()
       ax.set_title('Force Body')
       ax.plot(self.time_array[:self.index_coast] , self.force_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='Roll')
       ax.plot(self.time_array[:self.index_coast] , self.force_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Pitch')
       ax.plot(self.time_array[:self.index_coast] , self.force_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Yaw')
       ax.plot(self.time_array[self.index_coast:] , self.force_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:] , self.force_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:] , self.force_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Force [N]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'ForceBODY.png')

       plt.close('all')

       fig, ax = plt.subplots()
       ax.set_title('Acceleration BODY')
       ax.plot(self.time_array[:self.index_coast], self.acc_BODY_log[:self.index_coast, 0], color='#FF4B00', linestyle='-', label='Roll')
       ax.plot(self.time_array[:self.index_coast], self.acc_BODY_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Pitch')
       ax.plot(self.time_array[:self.index_coast], self.acc_BODY_log[:self.index_coast, 2], color='#03AF7A', linestyle='-', label='Yaw')
       ax.plot(self.time_array[self.index_coast:], self.acc_BODY_log[self.index_coast:, 0], color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.acc_BODY_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.acc_BODY_log[self.index_coast:, 2], color='#03AF7A', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Acceleration [m/s^2]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'AccelerationBODY.png')

       plt.close('all')

       fig, ax = plt.subplots()
       ax.set_title('Moment Body X-Axis')
       ax.plot(self.time_array[:self.index_coast], self.moment_aero_log[:self.index_coast, 0]        , color='#FF4B00', linestyle='-', label='Aero')
       ax.plot(self.time_array[:self.index_coast], self.moment_aero_damping_log[:self.index_coast, 0], color='#005AFF', linestyle='-', label='Aero Damping')
       ax.plot(self.time_array[:self.index_coast], self.moment_jet_damping_log[:self.index_coast, 0] , color='#03AF7A', linestyle='-', label='Jet Damping')
       ax.plot(self.time_array[:self.index_coast], self.moment_gyro_log[:self.index_coast, 0]        , color='#4DC4FF', linestyle='-', label='Gyro')
       ax.plot(self.time_array[self.index_coast:], self.moment_aero_log[self.index_coast:, 0]        , color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_aero_damping_log[self.index_coast:, 0], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_jet_damping_log[self.index_coast:, 0] , color='#03AF7A', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_gyro_log[self.index_coast:, 0]        , color='#4DC4FF', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Moment [N*m]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MomentBODY_X-Axis.png')

       fig, ax = plt.subplots()
       ax.set_title('Moment Body Y-Axis')
       ax.plot(self.time_array[:self.index_coast], self.moment_aero_log[:self.index_coast, 1]        , color='#FF4B00', linestyle='-', label='Aero')
       ax.plot(self.time_array[:self.index_coast], self.moment_aero_damping_log[:self.index_coast, 1], color='#005AFF', linestyle='-', label='Aero Damping')
       ax.plot(self.time_array[:self.index_coast], self.moment_jet_damping_log[:self.index_coast, 1] , color='#03AF7A', linestyle='-', label='Jet Damping')
       ax.plot(self.time_array[:self.index_coast], self.moment_gyro_log[:self.index_coast, 1]        , color='#4DC4FF', linestyle='-', label='Gyro')
       ax.plot(self.time_array[self.index_coast:], self.moment_aero_log[self.index_coast:, 1]        , color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_aero_damping_log[self.index_coast:, 1], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_jet_damping_log[self.index_coast:, 1] , color='#03AF7A', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_gyro_log[self.index_coast:, 1]        , color='#4DC4FF', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Moment [N*m]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MomentBODY_Y-Axis.png')

       fig, ax = plt.subplots()
       ax.set_title('Moment Body Z-Axis')
       ax.plot(self.time_array[:self.index_coast], self.moment_aero_log[:self.index_coast, 2]        , color='#FF4B00', linestyle='-', label='Aero')
       ax.plot(self.time_array[:self.index_coast], self.moment_aero_damping_log[:self.index_coast, 2], color='#005AFF', linestyle='-', label='Aero Damping')
       ax.plot(self.time_array[:self.index_coast], self.moment_jet_damping_log[:self.index_coast , 2], color='#03AF7A', linestyle='-', label='Jet Damping')
       ax.plot(self.time_array[:self.index_coast], self.moment_gyro_log[:self.index_coast, 2]        , color='#4DC4FF', linestyle='-', label='Gyro')
       ax.plot(self.time_array[self.index_coast:], self.moment_aero_log[self.index_coast:, 2]        , color='#FF4B00', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_aero_damping_log[self.index_coast:, 2], color='#005AFF', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_jet_damping_log[self.index_coast: , 2], color='#03AF7A', linestyle='--')
       ax.plot(self.time_array[self.index_coast:], self.moment_gyro_log[self.index_coast:, 2]        , color='#4DC4FF', linestyle='--')
       ax.set_xlabel('Time [sec]')
       ax.set_ylabel('Moment [N*m]')
       ax.set_xlim(xmin=0.0, xmax=self.time_end)
       ymin, ymax = ax.get_ylim()
       ax.set_ylim(ymin=ymin, ymax=ymax)
       ax.imshow(img_logo, extent=(get_extent_values(fig, ax, aspect_logo)), alpha=0.5)
       ax.set_aspect('auto')
       ax.grid()
       ax.legend()
       fig.savefig(self.filepath + os.sep + flightType + os.sep + 'MomentBODY_Z-Axis.png')

       vENU2LLH = np.vectorize(ENU2LLH, excluded=['launch_LLH'], signature="(1),(3)->(3)")
       log_LLH = vENU2LLH(self.Launch_LLH, self.pos_NED_log)
       point_LLH = ENU2LLH(self.Launch_LLH, self.point)
       make_kml.get_trajectory_point(point_LLH)
       post_kml(log_LLH, self.filepath, '_02_hard')
