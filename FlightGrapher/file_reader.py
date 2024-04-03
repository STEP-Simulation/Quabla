import numpy as np
import pandas as pd
import os

class FileReader:

    def __init__(self, path, name):
        # file open
        # Flightlog file
        path_flightlog = path + os.sep + name
        logdata = open(path_flightlog)
        self.__logdata_array = np.loadtxt(logdata, delimiter=',', skiprows=1)
        logdata.close()
        self.__df_logdata = pd.read_csv(path_flightlog)

        # Summary file
        self.__df_summary = pd.read_csv(path + os.sep + 'summary.csv')

    def get_logdata(self):
        return self.__logdata_array

    def get_df(self):
        return self.__df_logdata, self.__df_summary
