from abc import ABCMeta, abstractmethod
import numpy as np


class LaunchSite(metaclass=ABCMeta):

    @abstractmethod
    def plot_landing_scatter(self):
        pass

    @abstractmethod
    def judge_inside(self):
        pass

    @abstractmethod
    def output_kml(self):
        pass

def trans_matrix(theta):
    matrix = np.array([[np.cos(theta), -np.sin(theta), 0.0],
                       [np.sin(theta), np.cos(theta) , 0.0],
                       [0.0          , 0.0           , 1.0]])
    return matrix

def magnetic_declination(lat, lon):
    delta_lat  = lat - 37.0
    delta_lon = lon - 138
    return (7.0 + 57.201 / 60.0) + (18.750 / 60.0) * delta_lat - (6.761 / 60.0) * delta_lon - (0.059 / 60.0) * delta_lat**2 - (0.014 / 60.0) * delta_lat * delta_lon - (0.579 / 60.0) * delta_lon**2 #[deg]
